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
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.incquery.runtime.base.api.BaseIndexOptions;
import org.eclipse.incquery.runtime.base.api.DataTypeListener;
import org.eclipse.incquery.runtime.base.api.FeatureListener;
import org.eclipse.incquery.runtime.base.api.InstanceListener;
import org.eclipse.incquery.runtime.base.api.LightweightEObjectObserver;
import org.eclipse.incquery.runtime.base.api.filters.IBaseIndexObjectFilter;
import org.eclipse.incquery.runtime.base.api.filters.IBaseIndexResourceFilter;
import org.eclipse.incquery.runtime.base.comprehension.EMFModelComprehension;
import org.eclipse.incquery.runtime.base.comprehension.EMFVisitor;
import org.eclipse.incquery.runtime.base.exception.IncQueryBaseException;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

public class NavigationHelperContentAdapter extends EContentAdapter {

    private static final EClass EOBJECT_CLASS = EcorePackage.eINSTANCE.getEObject();

    private final NavigationHelperImpl navigationHelper;

    /**
     *  since last run of after-update callbacks
     */
    private boolean isDirty = false;

    /**
     *  value -> feature (EAttribute or EReference) -> holder(s)
     */
    private final Table<Object, Object, Set<EObject>> valueToFeatureToHolderMap;

    /** feature ((String id or EStructuralFeature) -> holder(s)
     *  constructed on-demand
     */
    private Map<Object, Multiset<EObject>> featureToHolderMap;

    /** holder -> feature (String id or EStructuralFeature) -> value(s)
     *  constructed on-demand
     */
    private Table<EObject, Object, Set<Object>> holderToFeatureToValueMap;

    /**
     *  key (String id or EClass instance) -> instance(s)
     */
    private final Map<Object, Set<EObject>> instanceMap;

    /**
     *  key (String id or EDataType instance) -> multiset of value(s)
     */
    private final Map<Object, Map<Object, Integer>> dataTypeMap;

    /**
     *  Field variable because it is needed for collision detection. Used for all EClasses whose instances were encountered at least once.
     */
    private final Set<EClassifier> knownClassifiers = new HashSet<EClassifier>();
    /**
     *  Field variable because it is needed for collision detection. Used for all EStructuralFeatures whose instances were encountered at least once.
     */
    private final Set<EStructuralFeature> knownFeatures = new HashSet<EStructuralFeature>();

    /**
     *  (EClass or String ID) -> all subtypes in knownClasses
     */
    private final Map<Object, Set<Object>> subTypeMap = new HashMap<Object, Set<Object>>();
    /**
     *  (EClass or String ID) -> all supertypes in knownClasses
     */
    private final Map<Object, Set<Object>> superTypeMap = new HashMap<Object, Set<Object>>();

    /**
     *  EPacakge NsURI -> EPackage instances; this is instance-level to detect collisions
     */
    private final Multimap<String, EPackage> uniqueIDToPackage = HashMultimap.create();

    /**
     *  static maps between metamodel elements and their unique IDs
     */
    private final Map<EClassifier,String> uniqueIDFromClassifier = new HashMap<EClassifier, String>();
    private final Map<ETypedElement,String> uniqueIDFromTypedElement = new HashMap<ETypedElement, String>();
    private final Multimap<String,EClassifier> uniqueIDToClassifier = HashMultimap.create(100, 1);
    private final Multimap<String,ETypedElement> uniqueIDToTypedElement = HashMultimap.create(100, 1);
    private Object eObjectClassKey = null;


    // move optimization to avoid removing and re-adding entire subtrees
    protected EObject ignoreInsertionAndDeletion;
    // Set<EObject> ignoreRootInsertion = new HashSet<EObject>();
    // Set<EObject> ignoreRootDeletion = new HashSet<EObject>();

    private final EMFModelComprehension comprehension;
    private final boolean isDynamicModel;

    private IBaseIndexObjectFilter objectFilterConfiguration;
    private IBaseIndexResourceFilter resourceFilterConfiguration;

    public NavigationHelperContentAdapter(final NavigationHelperImpl navigationHelper) {
        this.navigationHelper = navigationHelper;
        final BaseIndexOptions options = this.navigationHelper.getBaseIndexOptions();
        objectFilterConfiguration = options.getObjectFilterConfiguration();
        resourceFilterConfiguration = options.getResourceFilterConfiguration();
        this.comprehension = navigationHelper.getComprehension();
        this.isDynamicModel = navigationHelper.getBaseIndexOptions().isDynamicEMFMode();
        this.valueToFeatureToHolderMap = HashBasedTable.create();
        this.instanceMap = new HashMap<Object, Set<EObject>>();
        this.dataTypeMap = new HashMap<Object, Map<Object, Integer>>();
    }

    // key representative of the EObject class


	/**
	 * @return the eObjectClassKey
	 */
	public Object getEObjectClassKey() {
    	if (eObjectClassKey == null) {
    		eObjectClassKey = toKey(EOBJECT_CLASS);
    	}
		return eObjectClassKey;
	}

	protected Object toKey(final EClassifier classifier) {
    	if (isDynamicModel) {
            return toKeyDynamicInternal(classifier);
    	} else {
		    maintainMetamodel(classifier);
    		return classifier;
    	}
    }
	private String toKeyDynamicInternal(final EClassifier classifier) {
		String id = uniqueIDFromClassifier.get(classifier);
		if (id == null) {
		    Preconditions.checkArgument(!classifier.eIsProxy(), String.format("Classifier %s is an unresolved proxy", classifier));
		    id = classifier.getEPackage().getNsURI() + "##" + classifier.getName();
		    uniqueIDFromClassifier.put(classifier, id);
		    uniqueIDToClassifier.put(id, classifier);
		    // metamodel maintenance will call back toKey(), but now the ID maps are already filled
		    maintainMetamodel(classifier);
		}
		return id;
	}
    protected Object toKey(final EStructuralFeature feature) {
    	if (isDynamicModel) {
        	String id = uniqueIDFromTypedElement.get(feature);
        	if (id == null) {
        		Preconditions.checkArgument(!feature.eIsProxy(), String.format("Element %s is an unresolved proxy", feature));
        		id = toKeyDynamicInternal((EClassifier) feature.eContainer()) + "##" + feature.getEType().getName() + "##" + feature.getName();
        		uniqueIDFromTypedElement.put(feature, id);
        		uniqueIDToTypedElement.put(id, feature);
    		    // metamodel maintenance will call back toKey(), but now the ID maps are already filled
    		    maintainMetamodel(feature);
        	}
            return id;
    	} else {
		    maintainMetamodel(feature);
    		return feature;
    	}
    }


    @Override
    public void notifyChanged(final Notification notification) {
        try {
            super.notifyChanged(notification);

            final Object oFeature = notification.getFeature();
            final Object oNotifier = notification.getNotifier();
            if (oNotifier instanceof EObject && oFeature instanceof EStructuralFeature) {
                final EObject notifier = (EObject) oNotifier;
                final EStructuralFeature feature = (EStructuralFeature) oFeature;
                
                final boolean notifyLightweightObservers = handleNotification(notification, notifier, feature);
                
                if(notifyLightweightObservers) {
                    notifyLightweightObservers(notifier, feature, notification);
                }
            }
        } catch (final Exception ex) {
            processingFatal(ex, "handle the following update notification: " + notification);
        }

        notifyBaseIndexChangeListeners();

    }

    @SuppressWarnings("deprecation")
    private boolean handleNotification(final Notification notification, final EObject notifier,
            final EStructuralFeature feature) {
        final Object oldValue = notification.getOldValue();
        final Object newValue = notification.getNewValue();
        final int eventType = notification.getEventType();
        boolean notifyLightweightObservers = false;
        switch (eventType) {
        case Notification.ADD:
            featureUpdate(true, notifier, feature, newValue);
            notifyLightweightObservers = true;
            break;
        case Notification.ADD_MANY:
            for (final Object newElement : (Collection<?>) newValue) {
                featureUpdate(true, notifier, feature, newElement);
            }
            notifyLightweightObservers = true;
            break;
        case Notification.CREATE:
            break;
        case Notification.MOVE:
            break; // currently no support for ordering
        case Notification.REMOVE:
            featureUpdate(false, notifier, feature, oldValue);
            notifyLightweightObservers = true;
            break;
        case Notification.REMOVE_MANY:
            for (final Object oldElement : (Collection<?>) oldValue) {
                featureUpdate(false, notifier, feature, oldElement);
            }
            notifyLightweightObservers = true;
            break;
        case Notification.REMOVING_ADAPTER:
            break;
        case Notification.RESOLVE:
        	if (navigationHelper.isFeatureResolveIgnored(feature)) break; // otherwise same as SET
        case Notification.UNSET:
        case Notification.SET:
            featureUpdate(false, notifier, feature, oldValue);
            featureUpdate(true, notifier, feature, newValue);
            notifyLightweightObservers = true;
            break;
        default:
            break;
        }
        return notifyLightweightObservers;
    }

    protected void notifyBaseIndexChangeListeners() {
        navigationHelper.notifyBaseIndexChangeListeners(isDirty);
        if (isDirty) {
            isDirty = false;
        }
    }

    private void featureUpdate(final boolean isInsertion, final EObject notifier, final EStructuralFeature feature, final Object value) {
        // this is a safe visitation, no reads will happen, thus no danger of notifications or matcher construction
        comprehension.traverseFeature(visitor(isInsertion), notifier, feature, value);
    }

    @Override
    protected void addAdapter(final Notifier notifier) {
        if (notifier == ignoreInsertionAndDeletion) {
            return;
        }
        try {
            this.navigationHelper.coalesceTraversals(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    if (notifier instanceof EObject) {
                        comprehension.traverseObject(visitor(true), (EObject) notifier);
                    }
                    // the notification listener is really added AFTER traversing the object, 
                    // so that if a proxy is resolved due to the traversal, we do not get about it   
                    NavigationHelperContentAdapter.super.addAdapter(notifier);
                    return null;
                }
            });
        } catch (final InvocationTargetException ex) {
            processingFatal(ex.getCause(), "add the object: " + notifier);
        } catch (final Exception ex) {
            processingFatal(ex, "add the object: " + notifier);
        }
    }

    @Override
    protected void removeAdapter(final Notifier notifier) {
        if (notifier == ignoreInsertionAndDeletion) {
            return;
        }
        try {
            this.navigationHelper.coalesceTraversals(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    if (notifier instanceof EObject) {
                        comprehension.traverseObject(visitor(false), (EObject) notifier);
                    }
                    NavigationHelperContentAdapter.super.removeAdapter(notifier);
                    return null;
                }
            });
        } catch (final InvocationTargetException ex) {
            processingFatal(ex.getCause(), "remove the object: " + notifier);
        } catch (final Exception ex) {
            processingFatal(ex, "remove the object: " + notifier);
        }
    }

    protected void processingFatal(final Throwable ex, final String task) {
        navigationHelper.notifyFatalListener(logTaskFormat(task), ex);
    }
    protected void processingError(final Throwable ex, final String task) {
        navigationHelper.notifyErrorListener(logTaskFormat(task), ex);
    }

	private String logTaskFormat(final String task) {
		return "EMF-IncQuery encountered an error in processing the EMF model. "
				+ "This happened while trying to " + task;
	}

    protected EMFVisitor visitor(final boolean isInsertion) {
        return new NavigationHelperVisitor.ChangeVisitor(navigationHelper, isInsertion);
    }

    /**
     * This method uses the original {@link EStructuralFeature} instance or the String id.
     */
    private void addToFeatureMap(final Object featureKey, final Object value, final EObject holder) {
        Set<EObject> setVal = valueToFeatureToHolderMap.get(value, featureKey);

        if (setVal == null) {
            setVal = new HashSet<EObject>();
            valueToFeatureToHolderMap.put(value, featureKey, setVal);

        }
        setVal.add(holder);
    }

    /**
     * This method uses either the original {@link EStructuralFeature} instance or the String id.
     */
    private void addToReversedFeatureMap(final Object feature, final EObject holder) {
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
    private void addToDirectFeatureMap(final EObject holder, final Object feature, final Object value) {
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
    private void removeFromReversedFeatureMap(final Object feature, final EObject holder) {
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
    private void removeFromFeatureMap(final Object featureKey, final Object value, final EObject holder) {
        final Set<EObject> setHolder =
        		valueToFeatureToHolderMap.get(value, featureKey);
        if (setHolder != null) {
            setHolder.remove(holder);

            if (setHolder.isEmpty()) {
                valueToFeatureToHolderMap.remove(value, featureKey);
            }
        }
    }

    /**
     * This method uses either the original {@link EStructuralFeature} instance or the String id.
     */
    private void removeFromDirectFeatureMap(final EObject holder, final Object feature, final Object value) {
        final Set<Object> setVal = holderToFeatureToValueMap.get(holder, feature);
        if (setVal != null) {
            setVal.remove(value);

            if (setVal.isEmpty()) {
                valueToFeatureToHolderMap.remove(holder, feature);
            }
        }
    }

    public void insertFeatureTuple(final Object featureKey, final Object value, final EObject holder) {
        addToFeatureMap(featureKey, value, holder);
        if (featureToHolderMap != null) {
            addToReversedFeatureMap(featureKey, holder);
        }
        if (holderToFeatureToValueMap != null) {
            addToDirectFeatureMap(holder, featureKey, value);
        }

        isDirty = true;
        notifyFeatureListeners(holder, featureKey, value, true);
    }

    public void removeFeatureTuple(final Object featureKey, final Object value, final EObject holder) {
        removeFromFeatureMap(featureKey, value, holder);
        if (featureToHolderMap != null) {
            removeFromReversedFeatureMap(featureKey, holder);
        }
        if (holderToFeatureToValueMap != null) {
            removeFromDirectFeatureMap(holder, featureKey, value);
        }

        isDirty = true;
        notifyFeatureListeners(holder, featureKey, value, false);
    }

    // START ********* InstanceSet *********
    public Set<EObject> getInstanceSet(final Object keyClass) {
        return instanceMap.get(keyClass);
    }

    public void removeInstanceSet(final Object keyClass) {
        instanceMap.remove(keyClass);
    }

    public void insertIntoInstanceSet(final Object keyClass, final EObject value) {
        Set<EObject> set = instanceMap.get(keyClass);
        if (set == null) {
            set = new HashSet<EObject>();
            instanceMap.put(keyClass, set);
        }
        set.add(value);

        isDirty = true;
        notifyInstanceListeners(keyClass, value, true);
    }

    public void removeFromInstanceSet(final Object keyClass, final EObject value) {
        final Set<EObject> set = instanceMap.get(keyClass);
        if (set != null) {
            set.remove(value);

            if (set.isEmpty()) {
                instanceMap.remove(keyClass);
            }
        }

        isDirty = true;
        notifyInstanceListeners(keyClass, value, false);
    }

    // END ********* InstanceSet *********

    // START ********* DataTypeMap *********
    public Map<Object, Integer> getDataTypeMap(final Object keyType) {
        return dataTypeMap.get(keyType);
    }

    public void removeDataTypeMap(final Object keyType) {
       dataTypeMap.remove(keyType);
    }

    public void insertIntoDataTypeMap(final Object keyType, final Object value) {
        Map<Object, Integer> valMap =
        		dataTypeMap.get(keyType);
        if (valMap == null) {
            valMap = new HashMap<Object, Integer>();
           dataTypeMap.put(keyType, valMap);
        }
        final boolean firstOccurrence = (valMap.get(value) == null);
		if (firstOccurrence) {
            valMap.put(value, 1);
        } else {
            Integer count = valMap.get(value);
            valMap.put(value, ++count);
        }

        isDirty = true;
        notifyDataTypeListeners(keyType, value, true, firstOccurrence);
    }

    public void removeFromDataTypeMap(final Object keyType, final Object value) {
        final Map<Object, Integer> valMap =
        		dataTypeMap.get(keyType);
        if (valMap != null && valMap.get(value) != null) {
            Integer count = valMap.get(value);
            final boolean lastOccurrence = (--count == 0);
			if (lastOccurrence) {
                valMap.remove(value);
                if (valMap.size() == 0) {
                	dataTypeMap.remove(keyType);
                }
            } else {
                valMap.put(value, count);
            }

	        isDirty = true;
	        notifyDataTypeListeners(keyType, value, false, lastOccurrence);
        }
        // else: inconsistent deletion? log error?
    }

    // END ********* DataTypeMap *********

    /**
     * Checks the {@link EStructuralFeature}'s source and target {@link EPackage} for NsURI collision.
     * An error message will be logged if a model element from an other {@link EPackage}
     * instance with the same NsURI has been already processed. The error message will be logged
     * only for the first time for a given {@link EPackage} instance.
     *
     * @param classifier the classifier instance
     */
    protected void maintainMetamodel(final EStructuralFeature feature) {
        if (!knownFeatures.contains(feature)) {
        	knownFeatures.add(feature);
        	maintainMetamodel(feature.getEContainingClass());
        	maintainMetamodel(feature.getEType());
        }
    }


    /**
     * put subtype information into cache
     */
    protected void maintainMetamodel(final EClassifier classifier) {
        if (!knownClassifiers.contains(classifier)) {
            checkEPackage(classifier);
            knownClassifiers.add(classifier);

            if (classifier instanceof EClass) {
            	final EClass clazz = (EClass) classifier;
            	final Object clazzKey = toKey(clazz);
	            for (final EClass superType : clazz.getEAllSuperTypes()) {
	                maintainTypeHierarhyInternal(clazzKey, toKey(superType));
	            }
	            maintainTypeHierarhyInternal(clazzKey, getEObjectClassKey());
            }
        }
    }

    /**
     * Checks the {@link EClassifier}'s {@link EPackage} for NsURI collision.
     * An error message will be logged if a model element from an other {@link EPackage}
     * instance with the same NsURI has been already processed. The error message will be logged
     * only for the first time for a given {@link EPackage} instance.
     *
     * @param classifier the classifier instance
     */
    private void checkEPackage(final EClassifier classifier) {
        final EPackage ePackage = classifier.getEPackage();
		final String nsURI = ePackage.getNsURI();
		final Collection<EPackage> packagesOfURI = uniqueIDToPackage.get(nsURI);
        if (!packagesOfURI.contains(ePackage)) {
            uniqueIDToPackage.put(nsURI, ePackage);
            //collision detection between EPackages (disabled in dynamic model mode)
            if (!isDynamicModel && packagesOfURI.size() == 2) { // only report the issue if the new EPackage instance is the second for the same URI
                processingError(new IncQueryBaseException("NsURI ("+nsURI+ ") collision detected between different instances of EPackages. If this is normal, try using dynamic EMF mode."),
                        "process new metamodel elements.");
            }
        }
    }

    /**
     * Maintains subtype hierarchy
     * @param subClassKey EClass or String id of subclass
     * @param superClassKey EClass or String id of superclass
     */
    private void maintainTypeHierarhyInternal(final Object subClassKey, final Object superClassKey) {
    	// update observed class and instance listener tables according to new subtype information
        if (navigationHelper.directlyObservedClasses.contains(superClassKey)) {
            navigationHelper.getAllObservedClassesInternal().add(subClassKey);
        }
		final Table<Object, InstanceListener, Set<EClass>> instanceListeners = navigationHelper.peekInstanceListeners();
		if (instanceListeners != null) { // table already constructed
			for (final Entry<InstanceListener, Set<EClass>> entry : instanceListeners.row(superClassKey).entrySet()) {
				final InstanceListener listener = entry.getKey();
				for (final EClass subscriptionType : entry.getValue()) {
					navigationHelper.addInstanceListenerInternal(listener, subscriptionType, subClassKey);
				}
			}
		}

    	// update subtype maps
        Set<Object> subTypes = subTypeMap.get(superClassKey);
        if (subTypes == null) {
            subTypes = new HashSet<Object>();
            subTypeMap.put(superClassKey, subTypes);
        }
        subTypes.add(subClassKey);
        Set<Object> superTypes = superTypeMap.get(subClassKey);
        if (superTypes == null) {
        	superTypes = new HashSet<Object>();
            superTypeMap.put(subClassKey, superTypes);
        }
        superTypes.add(superClassKey);
    }

    private void notifyDataTypeListeners(final Object typeKey, final Object value, final boolean isInsertion, final boolean firstOrLastOccurrence) {
    	for (final Entry<DataTypeListener, Set<EDataType>> entry :
    			navigationHelper.getDataTypeListeners().row(typeKey).entrySet())
    	{
			final DataTypeListener listener = entry.getKey();
    		for (final EDataType subscriptionType : entry.getValue()) {
                if (isInsertion) {
                    listener.dataTypeInstanceInserted(subscriptionType, value, firstOrLastOccurrence);
                } else {
                	listener.dataTypeInstanceDeleted(subscriptionType, value, firstOrLastOccurrence);
                }
			}
		}
    }

    private void notifyFeatureListeners(final EObject host, final Object featureKey, final Object value, final boolean isInsertion) {
    	for (final Entry<FeatureListener, Set<EStructuralFeature>> entry :
    			navigationHelper.getFeatureListeners().row(featureKey).entrySet())
    	{
    		final FeatureListener listener = entry.getKey();
    		for (final EStructuralFeature subscriptionType : entry.getValue()) {
                if (isInsertion) {
                    listener.featureInserted(host, subscriptionType, value);
                } else {
                    listener.featureDeleted(host, subscriptionType, value);
                }
			}
		}
    }

    private void notifyInstanceListeners(final Object clazzKey, final EObject instance, final boolean isInsertion) {
	    for (final Entry<InstanceListener, Set<EClass>> entry :
				navigationHelper.getInstanceListeners().row(clazzKey).entrySet())
		{
	    	final InstanceListener listener = entry.getKey();
			for (final EClass subscriptionType : entry.getValue()) {
	            if (isInsertion) {
	                listener.instanceInserted(subscriptionType, instance);
	            } else {
	            	listener.instanceDeleted(subscriptionType, instance);
	            }
			}
		}
    }

    private void notifyLightweightObservers(final EObject host, final EStructuralFeature feature, final Notification notification) {
        for (final Entry<LightweightEObjectObserver, Collection<EObject>> entry : navigationHelper.getLightweightObservers().entrySet()) {
            if(entry.getValue().contains(host)) {
                entry.getKey().notifyFeatureChanged(host, feature, notification);
            }
        }
    }

    private void initReversedFeatureMap() {
        for (final Cell<Object, Object, Set<EObject>> valueToFeatureHolderMap : valueToFeatureToHolderMap.cellSet()) {
            final Object feature = valueToFeatureHolderMap.getColumnKey();
            for (final EObject holder : valueToFeatureHolderMap.getValue()) {
                addToReversedFeatureMap(feature, holder);
            }
        }
    }

    private void initDirectFeatureMap() {
        for (final Cell<Object, Object, Set<EObject>> valueToFeatureHolderMap : valueToFeatureToHolderMap
                .cellSet()) {
            final Object value = valueToFeatureHolderMap.getRowKey();
            final Object feature = valueToFeatureHolderMap.getColumnKey();
            for (final EObject holder : valueToFeatureHolderMap.getValue()) {
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
    protected void setTarget(final ResourceSet target) {
        basicSetTarget(target);
        final List<Resource> resources = target.getResources();
        for (int i = 0; i < resources.size(); ++i) {
            final Notifier notifier = resources.get(i);
            if (!notifier.eAdapters().contains(this)) {
                addAdapter(notifier);
            }
        }
    }

    // WORKAROUND (TMP) for eContents vs. derived features bug
    @Override
    protected void setTarget(final EObject target) {
        basicSetTarget(target);
        spreadToChildren(target, true);
    }

    @Override
    protected void unsetTarget(final EObject target) {
        basicUnsetTarget(target);
        spreadToChildren(target, false);
    }

    protected void spreadToChildren(final EObject target, final boolean add) {
        final EList<EReference> features = target.eClass().getEAllReferences();
        for (final EReference feature : features) {
            if (!feature.isContainment()) {
                continue;
            }
            if (!comprehension.representable(feature)) {
                continue;
            }
            if (feature.isMany()) {
                final Collection<?> values = (Collection<?>) target.eGet(feature);
                for (final Object value : values) {
                    final Notifier notifier = (Notifier) value;
                    if (add) {
                        addAdapter(notifier);
                    } else {
                        removeAdapter(notifier);
                    }
                }
            } else {
                final Object value = target.eGet(feature);
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
    protected Map<Object, Multiset<EObject>> peekFeatureToHolderMap() {
        return featureToHolderMap;
    }

    /**
     * Calling this method will construct the map for all holders and features,
     * consuming significant memory!
     * 
     * @return the holderToFeatureToValeMap
     */
    protected Table<EObject, Object, Set<Object>> getHolderToFeatureToValueMap() {
        if (holderToFeatureToValueMap == null) {
            holderToFeatureToValueMap = HashBasedTable.create();
            initDirectFeatureMap();
        }
        return holderToFeatureToValueMap;
    }
    protected Table<EObject, Object, Set<Object>> peekHolderToFeatureToValueMap() {
        return holderToFeatureToValueMap;
    }

    /**
     * @return the subTypeMap
     */
    protected Map<Object, Set<Object>> getSubTypeMap() {
        return subTypeMap;
    }
    protected Map<Object, Set<Object>> getSuperTypeMap() {
        return superTypeMap;
    }

    /**
     * Returns the corresponding {@link EStructuralFeature} instance for the id.
     *
     * @param featureId
     *            the id of the feature
     * @return the {@link EStructuralFeature} instance
     */
    public EStructuralFeature getKnownFeature(final String featureId) {
        final Collection<ETypedElement> features = uniqueIDToTypedElement.get(featureId);
        if (features != null && !features.isEmpty()) {
            final ETypedElement next = features.iterator().next();
            if (next instanceof EStructuralFeature) {
                return (EStructuralFeature) next;
            }
        }
        return null;

    }
    
    public EStructuralFeature getKnownFeatureForKey(Object featureKey) {
        EStructuralFeature feature;
        if (isDynamicModel()) {
            feature = getKnownFeature((String) featureKey);
        } else {
            feature = (EStructuralFeature) featureKey;
        }
        return feature;
    }

    /**
     * Returns the corresponding {@link EClassifier} instance for the id.
     */
    public EClassifier getKnownClassifier(final String key) {
		final Collection<EClassifier> classifiersOfThisID = uniqueIDToClassifier.get(key);
        if (classifiersOfThisID!=null && !classifiersOfThisID.isEmpty()) {
        	return classifiersOfThisID.iterator().next();
        } else {
            return null;
        }
    }

    public EClassifier getKnownClassifierForKey(Object classifierKey) {
        EClassifier cls;
        if (isDynamicModel()) {
            cls = getKnownClassifier((String) classifierKey);
        } else {
            cls = (EClassifier) classifierKey;
        }
        return cls;
    }
    
    /**
     * Returns all EClasses that currently have direct instances cached by the index.
     * <p>Supertypes will not be returned, unless they have direct instances in the model as well. If not in <em>wildcard mode</em>, only registered EClasses and their subtypes will be returned.
     * <p>Note for advanced users: if a type is represented by multiple EClass objects, one of them is chosen as representative and returned.
     */
    public Set<EClass> getAllCurrentClasses() {
        final Set<EClass> result = Sets.newHashSet();
        final Set<Object> classifierKeys = instanceMap.keySet();
        for (final Object classifierKey : classifierKeys) {
            if (isDynamicModel) {
                final EClassifier knownClassifier = getKnownClassifier((String) classifierKey);
                if (knownClassifier instanceof EClass) {
                    result.add((EClass) knownClassifier);
                }
            }
            else {
                result.add((EClass) classifierKey);
            }

        }
        return result;
    }

    public boolean isDynamicModel() {
        return isDynamicModel;
    }

    
    protected void resampleFeatureValueForHolder(EObject source, EStructuralFeature feature, EMFVisitor insertionVisitor, EMFVisitor removalVisitor) {
        // traverse features and update value
        Object newValue = source.eGet(feature);
        Set<Object> oldValues = getOldValuesForHolderAndFeature(source, feature);
        if(feature.isMany()) {
            resampleManyFeatureValueForHolder(source, feature, newValue, oldValues, insertionVisitor, removalVisitor);
        } else {
            resampleSingleFeatureValueForHolder(source, feature, newValue, oldValues, insertionVisitor, removalVisitor);
        }
        
    }

    private Set<Object> getOldValuesForHolderAndFeature(EObject source, EStructuralFeature feature) {
        // while this is slower than using the holderToFeatureToValueMap, we do not want to construct that to avoid
        // memory overhead
        Map<Object, Set<EObject>> oldValuesToHolders = valueToFeatureToHolderMap.column(feature);
        Set<Object> oldValues = new HashSet<Object>();
        for (Entry<Object, Set<EObject>> entry : oldValuesToHolders.entrySet()) {
            if(entry.getValue().contains(source)) {
                oldValues.add(entry.getKey());
            }
        }
        return oldValues;
    }

    private void resampleManyFeatureValueForHolder(EObject source, EStructuralFeature feature, Object newValue, Set<Object> oldValues, EMFVisitor insertionVisitor, EMFVisitor removalVisitor) {
        InternalEObject internalEObject = (InternalEObject) source;
        Collection<?> newValues = (Collection<?>) newValue;
        // add those that are in new but not in old
        Set<Object> newValueSet = new HashSet<Object>(newValues);
        newValueSet.removeAll(oldValues);
        // remove those that are in old but not in new
        oldValues.removeAll(newValues);
        if(!oldValues.isEmpty()) {
            for (Object ov : oldValues) {
                comprehension.traverseFeature(removalVisitor, source, feature, ov);
            }
            ENotificationImpl removeNotification = new ENotificationImpl(internalEObject, Notification.REMOVE_MANY, feature, oldValues, null);
            notifyLightweightObservers(source, feature, removeNotification);
        }
        if(!newValueSet.isEmpty()) {
            for (Object nv : newValueSet) {
                comprehension.traverseFeature(insertionVisitor, source, feature, nv);
            }
            ENotificationImpl addNotification = new ENotificationImpl(internalEObject, Notification.ADD_MANY, feature, null, newValueSet);
            notifyLightweightObservers(source, feature, addNotification);
        }
    }

    private void resampleSingleFeatureValueForHolder(EObject source, EStructuralFeature feature, Object newValue, Set<Object> oldValues, EMFVisitor insertionVisitor, EMFVisitor removalVisitor) {
        InternalEObject internalEObject = (InternalEObject) source;
        Object oldValue = Iterables.getFirst(oldValues, null);
        if(!Objects.equal(oldValue,newValue)) {
            // value changed
            comprehension.traverseFeature(removalVisitor, source, feature, oldValue);
            comprehension.traverseFeature(insertionVisitor, source, feature, newValue);
            ENotificationImpl notification = new ENotificationImpl(internalEObject, Notification.SET, feature, oldValue, newValue);
            notifyLightweightObservers(source, feature, notification);
        }
    }

    @Override
    public void setTarget(Notifier target) {
        if (objectFilterConfiguration != null && objectFilterConfiguration.isFiltered(target)) {
            return;
        }
        super.setTarget(target);
    }

    @Override
    public void unsetTarget(Notifier target) {
        if (objectFilterConfiguration != null && objectFilterConfiguration.isFiltered(target)) {
            return;
        }
        super.unsetTarget(target);
    }
    
    @Override
    public void setTarget(Resource target) {
        if (resourceFilterConfiguration != null && resourceFilterConfiguration.isResourceFiltered(target)) {
            return;
        }
        super.setTarget(target);
    }
    
    @Override
    public void unsetTarget(Resource target) {
        if (resourceFilterConfiguration != null && resourceFilterConfiguration.isResourceFiltered(target)) {
            return;
        }
        super.unsetTarget(target);
    }


    
}
