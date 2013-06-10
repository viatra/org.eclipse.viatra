/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Gabor Bergmann, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.base.core;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.incquery.runtime.base.api.DataTypeListener;
import org.eclipse.incquery.runtime.base.api.FeatureListener;
import org.eclipse.incquery.runtime.base.api.InstanceListener;
import org.eclipse.incquery.runtime.base.api.LightweightEObjectObserver;
import org.eclipse.incquery.runtime.base.comprehension.EMFModelComprehension;
import org.eclipse.incquery.runtime.base.comprehension.EMFVisitor;
import org.eclipse.incquery.runtime.base.exception.IncQueryBaseException;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

public class NavigationHelperContentAdapter extends EContentAdapter {

    public static final EClass EOBJECT_CLASS = EcorePackage.eINSTANCE.getEObject();

    private final NavigationHelperImpl navigationHelper;

    // since last run of after-update callbacks
    private boolean isDirty = false;

    // value -> feature (EAttribute or EReference) -> holder(s)
    private Table<Object, Object, Set<EObject>> valueToFeatureToHolderMap;

    // feature (EAttribute or EReference) -> holder(s)
    // constructed on-demand
    private Map<Object, Multiset<EObject>> featureToHolderMap;

    // holder -> feature (EAttribute or EReference) -> value(s)
    // constructed on-demand
    private Table<EObject, Object, Set<Object>> holderToFeatureToValueMap;

    // key (String id or EClass instance) -> instance(s)
    private Map<Object, Set<EObject>> instanceMap;

    // key (String id or EDataType instance) -> multiset of value(s)
    private Map<Object, Map<Object, Integer>> dataTypeMap;

    // source -> feature (EReference) -> proxy target -> delayed visitors
    private Table<EObject, EReference, ListMultimap<EObject, EMFVisitor>> unresolvableProxyFeaturesMap;

    // proxy source -> delayed visitors
    private ListMultimap<EObject, EMFVisitor> unresolvableProxyObjectsMap;

    // Field variable becuase it is needed for collision detection. Used for all EClasses whose instances were encountered at least once.
    private Set<EClass> knownClasses;

    // static for EClass -> all subtypes in knownClasses (shared between the IQ engines)
    private static Map<EClass, Set<EClass>> subTypeMap = new HashMap<EClass, Set<EClass>>();

    // EPacakge NsURI -> EPacakge instances
    private Multimap<String, EPackage> ePackageMap;
    
    // static maps between metamodel elements and their unique IDs
    private static Map<EClassifier,String> uniqueIDFromClassifier = new HashMap<EClassifier, String>();
    private static Map<ETypedElement,String> uniqueIDFromTypedElement = new HashMap<ETypedElement, String>();
    private static Multimap<String,EClassifier> uniqueIDToClassifier = HashMultimap.create(100, 1);
    private static Multimap<String,ETypedElement> uniqueIDToTypedElement = HashMultimap.create(100, 1);

    // move optimization to avoid removing and re-adding entire subtrees
    protected EObject ignoreInsertionAndDeletion;
    // Set<EObject> ignoreRootInsertion = new HashSet<EObject>();
    // Set<EObject> ignoreRootDeletion = new HashSet<EObject>();

    // Maps the generated feature id to the actual EStructuralFeature instance
    // This field is used only in case of dynamic EMF models
    private Multimap<String, EStructuralFeature> knownFeatures;

    private boolean isDynamicModel;

    public NavigationHelperContentAdapter(NavigationHelperImpl navigationHelper, boolean isDynamicModel) {
        this.navigationHelper = navigationHelper;
        this.isDynamicModel = isDynamicModel;
        this.unresolvableProxyFeaturesMap = HashBasedTable.create();
        this.unresolvableProxyObjectsMap = ArrayListMultimap.create();
        this.knownFeatures = ArrayListMultimap.create();
        this.valueToFeatureToHolderMap = HashBasedTable.create();
        this.instanceMap = new HashMap<Object, Set<EObject>>();
        this.dataTypeMap = new HashMap<Object, Map<Object, Integer>>();
        this.ePackageMap = HashMultimap.create();
        this.knownClasses = new HashSet<EClass>();
    }

    /**
     * Returns a String id representation for the given {@link EClassifier} instance. 
     * 
     * @param classifier the classifier instance
     * @return A unique string id generated from the classifier's name and the NsURI of its {@link EPackage}.
     */
    protected static String getUniqueIdentifier(EClassifier classifier) {
        String id = uniqueIDFromClassifier.get(classifier);
        if (id == null) {
            Preconditions.checkArgument(!classifier.eIsProxy(), String.format("Classifier %s is an unresolved proxy", classifier));
            id = classifier.getEPackage().getNsURI() + "##" + classifier.getName();            
            uniqueIDFromClassifier.put(classifier, id);
            uniqueIDToClassifier.put(id, classifier);
        }
        return id;
    }

    /**
     * Returns a String id representation for the given {@link ETypedElement} instance. 
     * 
     * @param typedElement the typed element instance
     * @return A unique string id generated from the typedelement's name and it's classifier type.
     */
    protected static String getUniqueIdentifier(ETypedElement typedElement) {
    	String id = uniqueIDFromTypedElement.get(typedElement);
    	if (id == null) {
    		Preconditions.checkArgument(!typedElement.eIsProxy(), String.format("Element %s is an unresolved proxy", typedElement));
    		id = getUniqueIdentifier((EClassifier) typedElement.eContainer()) + "##" + typedElement.getEType().getName() + "##" + typedElement.getName();
    		uniqueIDFromTypedElement.put(typedElement, id);
    		uniqueIDToTypedElement.put(id, typedElement);
    	}
        return id;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void notifyChanged(Notification notification) {
        try {
            // baseHandleNotification(notification);
            super.notifyChanged(notification);

            Object oFeature = notification.getFeature();
            final Object oNotifier = notification.getNotifier();
            if (oNotifier instanceof EObject && oFeature instanceof EStructuralFeature) {
                final EObject notifier = (EObject) oNotifier;
                final EStructuralFeature feature = (EStructuralFeature) oFeature;
                final Object oldValue = notification.getOldValue();
                final Object newValue = notification.getNewValue();
                final int eventType = notification.getEventType();
                switch (eventType) {
                case Notification.ADD:
                    featureUpdate(true, notifier, feature, newValue);
                    notifyLightweightObservers(notifier, feature, notification);
                    break;
                case Notification.ADD_MANY:
                    for (Object newElement : (Collection<?>) newValue) {
                        featureUpdate(true, notifier, feature, newElement);
                    }
                    notifyLightweightObservers(notifier, feature, notification);
                    break;
                case Notification.CREATE:
                    break;
                case Notification.MOVE:
                    break; // currently no support for ordering
                case Notification.REMOVE:
                    featureUpdate(false, notifier, feature, oldValue);
                    notifyLightweightObservers(notifier, feature, notification);
                    break;
                case Notification.REMOVE_MANY:
                    for (Object oldElement : (Collection<?>) oldValue) {
                        featureUpdate(false, notifier, feature, oldElement);
                    }
                    notifyLightweightObservers(notifier, feature, notification);
                    break;
                case Notification.REMOVING_ADAPTER:
                    break;
                case Notification.RESOLVE:
                    featureResolve(notifier, feature, oldValue, newValue);
                    break;
                case Notification.UNSET:
                case Notification.SET:
                    featureUpdate(false, notifier, feature, oldValue);
                    notifyLightweightObservers(notifier, feature, notification);
                    featureUpdate(true, notifier, feature, newValue);
                    notifyLightweightObservers(notifier, feature, notification);
                    break;
                }
            }
        } catch (Exception ex) {
            processingFatal(ex, "handle the following update notification: " + notification);
        }

        notifyBaseIndexChangeListeners();

    }

    protected void notifyBaseIndexChangeListeners() {
        navigationHelper.notifyBaseIndexChangeListeners(isDirty);
        if (isDirty) {
            isDirty = false;
        }
    }

    private void featureResolve(EObject source, EStructuralFeature feature, Object oldValue, Object newValue) {
        EReference reference = (EReference) feature;
        EObject proxy = (EObject) oldValue;
        EObject resolved = (EObject) newValue;

        final List<EMFVisitor> objectVisitors = popVisitorsSuspendedOnObject(proxy);
        for (EMFVisitor visitor : objectVisitors) {
            EMFModelComprehension.traverseObject(visitor, resolved);
        }

        final List<EMFVisitor> featureVisitors = popVisitorsSuspendedOnFeature(source, reference, proxy);
        for (EMFVisitor visitor : featureVisitors) {
            EMFModelComprehension.traverseFeature(visitor, source, reference, resolved);
        }
    }

    private void featureUpdate(boolean isInsertion, EObject notifier, EStructuralFeature feature, Object value) {
        // this is a safe visitation, no reads will happen, thus no danger of notifications or matcher construction
        EMFModelComprehension.traverseFeature(visitor(isInsertion), notifier, feature, value);
    }

    @Override
    protected void addAdapter(final Notifier notifier) {
        if (notifier == ignoreInsertionAndDeletion)
            return;
        try {
            this.navigationHelper.coalesceTraversals(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    if (notifier instanceof EObject) {
                        EMFModelComprehension.traverseObject(visitor(true), (EObject) notifier);
                    }
                    NavigationHelperContentAdapter.super.addAdapter(notifier);
                    return null;
                }
            });
        } catch (InvocationTargetException ex) {
            processingFatal(ex.getCause(), "add the object: " + notifier);
        } catch (Exception ex) {
            processingFatal(ex, "add the object: " + notifier);
        }
    }

    @Override
    protected void removeAdapter(final Notifier notifier) {
        if (notifier == ignoreInsertionAndDeletion)
            return;
        try {
            this.navigationHelper.coalesceTraversals(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    if (notifier instanceof EObject) {
                        EMFModelComprehension.traverseObject(visitor(false), (EObject) notifier);
                    }
                    NavigationHelperContentAdapter.super.removeAdapter(notifier);
                    return null;
                }
            });
        } catch (InvocationTargetException ex) {
            processingFatal(ex.getCause(), "remove the object: " + notifier);
        } catch (Exception ex) {
            processingFatal(ex, "remove the object: " + notifier);
        }
    }

    protected void processingFatal(Throwable ex, String task) {
        navigationHelper.getLogger().fatal(logTaskFormat(task), ex);
    }
    protected void processingError(Throwable ex, String task) {
        navigationHelper.getLogger().error(logTaskFormat(task), ex);
    }

	private String logTaskFormat(String task) {
		return "EMF-IncQuery encountered an error in processing the EMF model. " 
				+ "This happened while trying to " + task;
	}

    protected EMFVisitor visitor(final boolean isInsertion) {
        return new NavigationHelperVisitor.ChangeVisitor(navigationHelper, isInsertion);
    }

    /**
     * This method uses the original {@link EStructuralFeature} instance. 
     */
    private void addToFeatureMap(EStructuralFeature feature, Object value, EObject holder) {
        Set<EObject> setVal = (isDynamicModel ? valueToFeatureToHolderMap.get(value, getUniqueIdentifier(feature))
                : valueToFeatureToHolderMap.get(value, feature));

        if (setVal == null) {
            setVal = new HashSet<EObject>();
            if (isDynamicModel) {
                valueToFeatureToHolderMap.put(value, getUniqueIdentifier(feature), setVal);
            } else {
                valueToFeatureToHolderMap.put(value, feature, setVal);
            }
        }
        setVal.add(holder);
    }

    /**
     * This method uses either the original {@link EStructuralFeature} instance or the String id. 
     */
    private void addToReversedFeatureMap(Object feature, EObject holder) {
        Multiset<EObject> setVal = featureToHolderMap.get(feature);

        if (setVal == null) {
            setVal = HashMultiset.create();
            featureToHolderMap.put(feature, setVal);
        }
        setVal.add(holder);
    }

    /**
     * This method uses either the original {@link EStructuralFeature} instance or the String id. 
     */
    private void addToDirectFeatureMap(EObject holder, Object feature, Object value) {
        Set<Object> setVal = holderToFeatureToValueMap.get(holder, feature);

        if (setVal == null) {
            setVal = new HashSet<Object>();
            holderToFeatureToValueMap.put(holder, feature, setVal);
        }
        setVal.add(value);
    }

    /**
     * This method uses either the original {@link EStructuralFeature} instance or the String id. 
     */
    private void removeFromReversedFeatureMap(Object feature, EObject holder) {
        final Multiset<EObject> setVal = featureToHolderMap.get(feature);
        if (setVal != null) {
            setVal.remove(holder);

            if (setVal.isEmpty()) {
                featureToHolderMap.remove(feature);
            }
        }
    }

    /**
     * This method uses the original {@link EStructuralFeature} instance. 
     */
    private void removeFromFeatureMap(EStructuralFeature feature, Object value, EObject holder) {
        final Set<EObject> setHolder = (isDynamicModel ? valueToFeatureToHolderMap.get(value,
                getUniqueIdentifier(feature)) : valueToFeatureToHolderMap.get(value, feature));
        if (setHolder != null) {
            setHolder.remove(holder);

            if (setHolder.isEmpty()) {
                if (isDynamicModel) {
                    valueToFeatureToHolderMap.remove(value, getUniqueIdentifier(feature));
                } else {
                    valueToFeatureToHolderMap.remove(value, feature);
                }
            }
        }
    }

    /**
     * This method uses either the original {@link EStructuralFeature} instance or the String id. 
     */
    private void removeFromDirectFeatureMap(EObject holder, Object feature, Object value) {
        final Set<Object> setVal = holderToFeatureToValueMap.get(holder, feature);
        if (setVal != null) {
            setVal.remove(value);

            if (setVal.isEmpty()) {
                valueToFeatureToHolderMap.remove(holder, feature);
            }
        }
    }

    public void insertFeatureTuple(EStructuralFeature feature, Object value, EObject holder) {
        addToFeatureMap(feature, value, holder);

        Object dynamicFeature = (isDynamicModel ? getUniqueIdentifier(feature) : feature);
        if (featureToHolderMap != null) {
            addToReversedFeatureMap(dynamicFeature, holder);
        }
        if (holderToFeatureToValueMap != null) {
            addToDirectFeatureMap(holder, dynamicFeature, value);
        }

        if (isDynamicModel) {
            knownFeatures.put(getUniqueIdentifier(feature), feature);
        }

        isDirty = true;
        notifyFeatureListeners(holder, feature, value, true);
    }

    public void removeFeatureTuple(EStructuralFeature feature, Object value, EObject holder) {
        removeFromFeatureMap(feature, value, holder);

        Object dynamicFeature = (isDynamicModel ? getUniqueIdentifier(feature) : feature);
        if (featureToHolderMap != null) {
            removeFromReversedFeatureMap(dynamicFeature, holder);
        }
        if (holderToFeatureToValueMap != null) {
            removeFromDirectFeatureMap(holder, dynamicFeature, value);
        }

        if (isDynamicModel) {
            knownFeatures.remove(getUniqueIdentifier(feature), feature);
        }

        isDirty = true;
        notifyFeatureListeners(holder, feature, value, false);
    }

    // START ********* InstanceSet *********
    public Set<EObject> getInstanceSet(EClass keyClass) {
        if (isDynamicModel) {
            return instanceMap.get(getUniqueIdentifier(keyClass));
        } else {
            return instanceMap.get(keyClass);
        }
    }

    public void removeInstanceSet(EClass keyClass) {
        if (isDynamicModel) {
            instanceMap.remove(getUniqueIdentifier(keyClass));
        } else {
            instanceMap.remove(keyClass);
        }
    }

    public void insertIntoInstanceSet(EClass keyClass, EObject value) {
        Set<EObject> set = (isDynamicModel ? instanceMap.get(getUniqueIdentifier(keyClass)) : instanceMap.get(keyClass));
        if (set == null) {
            set = new HashSet<EObject>();
            if (isDynamicModel) {
                instanceMap.put(getUniqueIdentifier(keyClass), set);
            } else {
                instanceMap.put(keyClass, set);
            }
        }
        set.add(value);

        isDirty = true;
        notifyInstanceListeners(keyClass, value, true);
    }

    public void removeFromInstanceSet(EClass keyClass, EObject value) {
        Set<EObject> set = (isDynamicModel ? instanceMap.get(getUniqueIdentifier(keyClass)) : instanceMap.get(keyClass));
        if (set != null) {
            set.remove(value);

            if (set.isEmpty()) {
                if (isDynamicModel) {
                    instanceMap.remove(getUniqueIdentifier(keyClass));
                } else {
                    instanceMap.remove(keyClass);
                }
            }
        }

        isDirty = true;
        notifyInstanceListeners(keyClass, value, false);
    }

    // END ********* InstanceSet *********

    // START ********* DataTypeMap *********
    public Map<Object, Integer> getDataTypeMap(EDataType keyType) {
        if (isDynamicModel) {
            return dataTypeMap.get(getUniqueIdentifier(keyType));
        } else {
            return dataTypeMap.get(keyType);
        }
    }

    public void removeDataTypeMap(EDataType keyType) {
        if (isDynamicModel) {
            dataTypeMap.remove(getUniqueIdentifier(keyType));
        } else {
            dataTypeMap.remove(keyType);
        }
    }

    public void insertIntoDataTypeMap(EDataType keyType, Object value) {
        Map<Object, Integer> valMap = (isDynamicModel ? dataTypeMap.get(getUniqueIdentifier(keyType)) : dataTypeMap
                .get(keyType));
        if (valMap == null) {
            valMap = new HashMap<Object, Integer>();
            if (isDynamicModel) {
                dataTypeMap.put(getUniqueIdentifier(keyType), valMap);
            } else {
                dataTypeMap.put(keyType, valMap);
            }
        }
        if (valMap.get(value) == null) {
            valMap.put(value, Integer.valueOf(1));
        } else {
            Integer count = valMap.get(value);
            valMap.put(value, ++count);
        }

        isDirty = true;
        notifyDataTypeListeners(keyType, value, true);
    }

    public void removeFromDataTypeMap(EDataType keyType, Object value) {
        Map<Object, Integer> valMap = (isDynamicModel ? dataTypeMap.get(getUniqueIdentifier(keyType)) : dataTypeMap
                .get(keyType));
        if (valMap != null) {
            if (valMap.get(value) != null) {
                Integer count = valMap.get(value);
                if (--count == 0) {
                    valMap.remove(value);
                } else {
                    valMap.put(value, count);
                }
            }
            if (valMap.size() == 0) {
                if (isDynamicModel) {
                    dataTypeMap.remove(getUniqueIdentifier(keyType));
                } else {
                    dataTypeMap.remove(keyType);
                }
            }
        }

        isDirty = true;
        notifyDataTypeListeners(keyType, value, false);
    }

    // END ********* DataTypeMap *********

    /**
     * This method can be used to efficiently (internal cache) determine subtype relationships.
     * 
     * @param sub subtype
     * @param sup supertype
     * @return true if sup is the supertype of sub, false otherwise
     */
    private boolean isSubTypeOf(EClass sub, EClass sup) {
        Set<EClass> set = subTypeMap.get(sup);
        if (set != null) {
            return set.contains(sub);
        } else {
            return false;
        }
    }
    
    /**
     * Checks the {@link EPackage}s of the given {@link EClassifier}s for NsURI collision 
     * by calling the <code>checkEPackage(EClassifier classifier)</code> for all of the 
     * elements in the passed {@link Collection}.
     * 
     * @param classifiers the collection of classifiers
     */
    protected <T extends EClassifier> void checkEPackage(Collection<T> classifiers) {
        for (T classifier : classifiers) {
            checkEPackage(classifier);
        }
    }
    
    /**
     * Checks the {@link EStructuralFeature}'s source and target {@link EPackage} for NsURI collision.
     * An error message will be logged if a model element from an other {@link EPackage} 
     * instance with the same NsURI has been already processed. The error message will be logged 
     * only for the first time for a given {@link EPackage} instance.
     * 
     * @param classifier the classifier instance
     */
    protected void checkEPackage(EStructuralFeature feature) {
    	checkEPackage(feature.getEContainingClass());
    	checkEPackage(feature.getEType());
    }
   
    /**
     * Checks the {@link EClassifier}'s {@link EPackage} for NsURI collision.
     * An error message will be logged if a model element from an other {@link EPackage} 
     * instance with the same NsURI has been already processed. The error message will be logged 
     * only for the first time for a given {@link EPackage} instance.
     * 
     * @param classifier the classifier instance
     */
    protected void checkEPackage(EClassifier classifier) {
        Collection<EPackage> otherPackages = ePackageMap.get(classifier.getEPackage().getNsURI());
        if (!otherPackages.contains(classifier.getEPackage())) {
            ePackageMap.put(classifier.getEPackage().getNsURI(), classifier.getEPackage());
            //collision detection between EPackages (disabled in dynamic model mode)
            if (!isDynamicModel && otherPackages.size() == 2) { // only report the issue if the new EPackage instance is the second for the same URI            
                processingError(new IncQueryBaseException("NsURI ("+classifier.getEPackage().getNsURI()+ ") collision detected between different instances of EPackages"), 
                        "process new metamodel elements.");
            }
        }
    }

    /**
     * put subtype information into cache
     */
    protected void maintainTypeHierarchy(EClass clazz) {
        if (!knownClasses.contains(clazz)) {
            checkEPackage(clazz);
            knownClasses.add(clazz);

            for (EClass superType : clazz.getEAllSuperTypes()) {
                maintainTypeHierarhyInternal(clazz, superType);
            }
            maintainTypeHierarhyInternal(clazz, EOBJECT_CLASS);
        }
    }

    private void maintainTypeHierarhyInternal(EClass clazz, EClass superType) {
        if (navigationHelper.directlyObservedClasses.contains(superType)) {
            navigationHelper.getAllObservedClasses().add(clazz);
        }

        Set<EClass> set = subTypeMap.get(superType);
        if (set == null) {
            set = new HashSet<EClass>();
            subTypeMap.put(superType, set);
        }
        set.add(clazz);
    }

    private void notifyDataTypeListeners(EDataType type, Object value, boolean isInsertion) {
        for (Entry<DataTypeListener, Collection<EDataType>> entry : navigationHelper.getDataTypeListeners().entrySet()) {
            if (entry.getValue().contains(type)) {
                if (isInsertion) {
                    entry.getKey().dataTypeInstanceInserted(type, value);
                } else {
                    entry.getKey().dataTypeInstanceDeleted(type, value);
                }
            }
        }
    }

    private void notifyFeatureListeners(EObject host, EStructuralFeature feature, Object value, boolean isInsertion) {
        for (Entry<FeatureListener, Collection<EStructuralFeature>> entry : navigationHelper.getFeatureListeners()
                .entrySet()) {
            if (entry.getValue().contains(feature)) {
                if (isInsertion) {
                    entry.getKey().featureInserted(host, feature, value);
                } else {
                    entry.getKey().featureDeleted(host, feature, value);
                }
            }
        }
    }

    private void notifyInstanceListeners(EClass clazz, EObject instance, boolean isInsertion) {
        for (Entry<InstanceListener, Collection<EClass>> entry : navigationHelper.getInstanceListeners().entrySet()) {
            for (EClass sup : entry.getValue()) {
                if (isSubTypeOf(clazz, sup) || clazz.equals(sup)) {
                    if (isInsertion) {
                        entry.getKey().instanceInserted(sup, instance);
                    } else {
                        entry.getKey().instanceDeleted(sup, instance);
                    }
                }
            }
        }
    }
    
    private void notifyLightweightObservers(EObject host, EStructuralFeature feature, Notification notification) {
        for (Entry<LightweightEObjectObserver, Collection<EObject>> entry : navigationHelper.getLightweightObservers().entrySet()) {
            if(entry.getValue().contains(host)) {
                entry.getKey().notifyFeatureChanged(host, feature, notification);
            }
        }
    }

    private void initReversedFeatureMap() {
        for (Cell<Object, Object, Set<EObject>> valueToFeatureHolderMap : valueToFeatureToHolderMap.cellSet()) {
            final Object feature = valueToFeatureHolderMap.getColumnKey();
            for (EObject holder : valueToFeatureHolderMap.getValue()) {
                addToReversedFeatureMap(feature, holder);
            }
        }
    }

    private void initDirectFeatureMap() {
        for (Cell<Object, Object, Set<EObject>> valueToFeatureHolderMap : valueToFeatureToHolderMap
                .cellSet()) {
            final Object value = valueToFeatureHolderMap.getRowKey();
            final Object feature = valueToFeatureHolderMap.getColumnKey();
            for (EObject holder : valueToFeatureHolderMap.getValue()) {
                addToDirectFeatureMap(holder, feature, value);
            }
        }
    }

    // WORKAROUND for EContentAdapter bug
    // where proxy resolution during containment traversal would add a new
    // Resource to the ResourceSet (and thus the adapter)
    // that will be set as target twice:
    // - once when resolved (which happens while iterating through the
    // resources),
    // - and once when said iteration of resources reaches the end of the
    // resource list in the ResourceSet
    // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=385039
    @Override
    protected void setTarget(ResourceSet target) {
        basicSetTarget(target);
        List<Resource> resources = target.getResources();
        for (int i = 0; i < resources.size(); ++i) {
            Notifier notifier = resources.get(i);
            if (!notifier.eAdapters().contains(this)) {
                addAdapter(notifier);
            }
        }
    }

    // WORKAROUND (TMP) for eContents vs. derived features bug
    @Override
    protected void setTarget(EObject target) {
        basicSetTarget(target);
        spreadToChildren(target, true);
    }

    @Override
    protected void unsetTarget(EObject target) {
        basicUnsetTarget(target);
        spreadToChildren(target, false);
    }

    protected void spreadToChildren(EObject target, boolean add) {
        final EList<EReference> features = target.eClass().getEAllReferences();
        for (EReference feature : features) {
            if (!feature.isContainment()) {
                continue;
            }
            if (!EMFModelComprehension.representable(feature)) {
                continue;
            }
            if (feature.isMany()) {
                Collection<?> values = (Collection<?>) target.eGet(feature);
                for (Object value : values) {
                    final Notifier notifier = (Notifier) value;
                    if (add) {
                        addAdapter(notifier);
                    } else {
                        removeAdapter(notifier);
                    }
                }
            } else {
                Object value = target.eGet(feature);
                if (value != null) {
                    final Notifier notifier = (Notifier) value;
                    if (add) {
                        addAdapter(notifier);
                    } else {
                        removeAdapter(notifier);
                    }
                }
            }
        }
    }

    public void suspendVisitorOnUnresolvableFeature(EMFVisitor visitor, EObject source, EReference reference,
            EObject target, boolean isInsertion) {
        ListMultimap<EObject, EMFVisitor> targetToVisitor = unresolvableProxyFeaturesMap.get(source, reference);
        if (targetToVisitor == null) {
            targetToVisitor = ArrayListMultimap.create();
            unresolvableProxyFeaturesMap.put(source, reference, targetToVisitor);
        }
        if (isInsertion) {
            targetToVisitor.put(target, visitor);
        } else {
            targetToVisitor.remove(target, visitor);
        }
        if (targetToVisitor.isEmpty()) {
            unresolvableProxyFeaturesMap.remove(source, reference);
        }
    }

    public void suspendVisitorOnUnresolvableObject(EMFVisitor visitor, EObject source, boolean isInsertion) {
        if (isInsertion) {
            unresolvableProxyObjectsMap.put(source, visitor);
        } else {
            unresolvableProxyObjectsMap.remove(source, visitor);
        }
    }

    private List<EMFVisitor> popVisitorsSuspendedOnFeature(EObject source, EReference reference, EObject target) {
        ListMultimap<EObject, EMFVisitor> targetToVisitor = unresolvableProxyFeaturesMap.get(source, reference);
        if (targetToVisitor == null) {
            return Collections.emptyList();
        }
        final List<EMFVisitor> result = targetToVisitor.removeAll(target);
        if (targetToVisitor.isEmpty()) {
            unresolvableProxyFeaturesMap.remove(source, reference);
        }
        return result;
    }

    private List<EMFVisitor> popVisitorsSuspendedOnObject(EObject source) {
        return unresolvableProxyObjectsMap.removeAll(source);
    }

    /**
     * @return the valueToFeatureToHolderMap
     */
    protected Table<Object, Object, Set<EObject>> getValueToFeatureToHolderMap() {
        return valueToFeatureToHolderMap;
    }

    /**
     * @return the featureToHolderMap
     */
    protected Map<Object, Multiset<EObject>> getFeatureToHolderMap() {
        if (featureToHolderMap == null) {
            featureToHolderMap = new HashMap<Object, Multiset<EObject>>();
            initReversedFeatureMap();
        }
        return featureToHolderMap;
    }

    /**
     * @return the holderToFeatureToValeMap
     */
    protected Table<EObject, Object, Set<Object>> getHolderToFeatureToValueMap() {
        if (holderToFeatureToValueMap == null) {
            holderToFeatureToValueMap = HashBasedTable.create();
            initDirectFeatureMap();
        }
        return holderToFeatureToValueMap;
    }

    /**
     * @return the unresolvableProxyFeaturesMap
     */
    protected Table<EObject, EReference, ListMultimap<EObject, EMFVisitor>> getUnresolvableProxyFeaturesMap() {
        return unresolvableProxyFeaturesMap;
    }

    /**
     * @return the unresolvableProxyObjectsMap
     */
    protected ListMultimap<EObject, EMFVisitor> getUnresolvableProxyObjectsMap() {
        return unresolvableProxyObjectsMap;
    }

    /**
     * @return the subTypeMap
     */
    protected static Map<EClass, Set<EClass>> getSubTypeMap() {
        return subTypeMap;
    }

    /**
     * Returns the corresponding {@link EStructuralFeature} instance for the id.
     * 
     * @param featureId
     *            the id of the feature
     * @return the {@link EStructuralFeature} instance
     */
    public EStructuralFeature getKnownFeature(String featureId) {
        Collection<EStructuralFeature> features = knownFeatures.get(featureId);
        if (features.size() == 0) {
            return null;
        } else {
            return features.iterator().next();
        }
    }

    /**
     * Returns all EClasses that currently have direct instances cached by the index. 
     * <p>Supertypes will not be returned, unless they have direct instances in the model as well. If not in <em>wildcard mode</em>, only registered EClasses and their subtypes will be returned.  
     * <p>Note for advanced users: if a type is represented by multiple EClass objects, one of them is chosen as representative and returned. 
     */
    public Set<EClass> getAllCurrentClasses() {
        Set<EClass> result = Sets.newHashSet();
        Set<Object> classifiers = instanceMap.keySet();
        for (Object classifierElement : classifiers) {
            if (isDynamicModel) {
                Collection<EClassifier> classifiersOfThisID = uniqueIDToClassifier.get((String) classifierElement);
                if (!classifiersOfThisID.isEmpty())
                    result.add((EClass) classifiersOfThisID.iterator().next());
            }
            else {
                result.add((EClass) classifierElement);
            }
        
        }
        return result;
    }

    public boolean isDynamicModel() {
        return isDynamicModel;
    }

}
