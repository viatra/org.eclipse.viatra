/*******************************************************************************
 * Copyright (c) 2010-2012, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.base.core;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.NotifyingList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.incquery.runtime.base.api.DataTypeListener;
import org.eclipse.incquery.runtime.base.api.FeatureListener;
import org.eclipse.incquery.runtime.base.api.IEStructuralFeatureProcessor;
import org.eclipse.incquery.runtime.base.api.IncQueryBaseIndexChangeListener;
import org.eclipse.incquery.runtime.base.api.InstanceListener;
import org.eclipse.incquery.runtime.base.api.LightweightEObjectObserver;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.base.comprehension.EMFModelComprehension;
import org.eclipse.incquery.runtime.base.exception.IncQueryBaseException;

import com.google.common.collect.Multiset;

public class NavigationHelperImpl implements NavigationHelper {

    protected boolean inWildcardMode;
    protected Set<EClass> directlyObservedClasses;
    protected Set<EClass> allObservedClasses = null; // including subclasses
    protected Set<EDataType> observedDataTypes;
    protected Set<EStructuralFeature> observedFeatures;

    protected Notifier notifier;
    protected Set<Notifier> modelRoots;
    private boolean expansionAllowed;
    // protected NavigationHelperVisitor visitor;
    protected NavigationHelperContentAdapter contentAdapter;

    private final Logger logger;

    /**
     * These global listeners will be called after updates.
     */
    //private final Set<Runnable> afterUpdateCallbacks;
    private final Set<IncQueryBaseIndexChangeListener> baseIndexChangeListeners;

    private final Map<InstanceListener, Collection<EClass>> instanceListeners;
    private final Map<FeatureListener, Collection<EStructuralFeature>> featureListeners;
    private final Map<DataTypeListener, Collection<EDataType>> dataTypeListeners;
    private final Map<LightweightEObjectObserver, Collection<EObject>> lightweightObservers;

    /**
     * Feature registration and model traversal is delayed while true
     */
    protected boolean delayTraversals = false;
    /**
     * Classes to be registered once the coalescing period is over
     */
    protected Set<EClass> delayedClasses;
    /**
     * EStructuralFeatures to be registered once the coalescing period is over
     */
    protected Set<EStructuralFeature> delayedFeatures;
    /**
     * EDataTypes to be registered once the coalescing period is over
     */
    protected Set<EDataType> delayedDataTypes;

    <T> Set<T> setMinus(Set<T> a, Set<T> b) {
        Set<T> result = new HashSet<T>(a);
        result.removeAll(b);
        return result;
    }

    @SuppressWarnings("unchecked")
    <T extends EObject> Set<T> resolveAll(Set<T> a) {
    	if (a==null) a = Collections.emptySet();
        Set<T> result = new HashSet<T>();
        for (T t : a) {
            if (t.eIsProxy()) {
                result.add((T) EcoreUtil.resolve(t, (ResourceSet) null));
            } else {
                result.add(t);
            }
        }
        return result;
    }

    @Override
    public boolean isInWildcardMode() {
        return inWildcardMode;
    }
    @Override
    public boolean isInDynamicEMFMode() {
    	return contentAdapter.isDynamicModel();
    }
    
    public NavigationHelperImpl(Notifier emfRoot, boolean wildcardMode, boolean dynamicModel, Logger logger) throws IncQueryBaseException {
        this.logger = logger;
        assert (logger != null);

        this.instanceListeners = new HashMap<InstanceListener, Collection<EClass>>();
        this.featureListeners = new HashMap<FeatureListener, Collection<EStructuralFeature>>();
        this.dataTypeListeners = new HashMap<DataTypeListener, Collection<EDataType>>();
        this.lightweightObservers = new HashMap<LightweightEObjectObserver, Collection<EObject>>();
        this.directlyObservedClasses = new HashSet<EClass>();
        this.observedFeatures = new HashSet<EStructuralFeature>();
        this.observedDataTypes = new HashSet<EDataType>();
        this.contentAdapter = new NavigationHelperContentAdapter(this, dynamicModel);
        // this.visitor = new NavigationHelperVisitor(this);
        //this.afterUpdateCallbacks = new HashSet<Runnable>();
        this.baseIndexChangeListeners = new HashSet<IncQueryBaseIndexChangeListener>();

        this.notifier = emfRoot;
        this.modelRoots = new HashSet<Notifier>();
        this.expansionAllowed = false;
        this.inWildcardMode = wildcardMode;
        
        // if (this.navigationHelperType == NavigationHelperType.ALL) {
        // visitor.visitModel(notifier, observedFeatures, observedClasses, observedDataTypes);
        // }
        if (emfRoot != null) {
            addRootInternal(emfRoot);
        }
    }

    public NavigationHelperContentAdapter getContentAdapter() {
        return contentAdapter;
    }

    public Set<EStructuralFeature> getObservedFeatures() {
        return observedFeatures;
    }

    // public NavigationHelperVisitor getVisitor() {
    // return visitor;
    // }

    @Override
    public void dispose() {
    	ensureNoListenersForDispose();
        for (Notifier root : modelRoots) {
            contentAdapter.removeAdapter(root);
        }
    }

    @Override
    public Collection<Object> getDataTypeInstances(EDataType type) {
        Map<Object, Integer> valMap = contentAdapter.getDataTypeMap(type);
        if (valMap != null) {
            return Collections.unmodifiableSet(valMap.keySet());
        } else {
        	contentAdapter.maintainMetamodel(type);           	 	
            return Collections.emptySet();
        }
    }

    @Override
    public Collection<Setting> findByAttributeValue(Object value) {
        Set<Setting> retSet = new HashSet<Setting>();
        Map<Object, Set<EObject>> valMap = contentAdapter.getValueToFeatureToHolderMap().row(value);

        for (Entry<Object, Set<EObject>> entry : valMap.entrySet()) {
            for (EObject holder : entry.getValue()) {
                if (contentAdapter.isDynamicModel()) {
                    retSet.add(new NavigationHelperSetting(contentAdapter.getKnownFeature((String) entry.getKey()), holder, value));
                }
                else {
                    retSet.add(new NavigationHelperSetting((EStructuralFeature) entry.getKey(), holder, value));
                }
            }
        }

        return retSet;
    }

    @Override
    public Collection<Setting> findByAttributeValue(Object value, Collection<EAttribute> attributes) {
        Set<Setting> retSet = new HashSet<Setting>();
        Map<Object, Set<EObject>> valMap = contentAdapter.getValueToFeatureToHolderMap().row(value);

        for (EAttribute attr : attributes) {
            Object feature = (contentAdapter.isDynamicModel() ? NavigationHelperContentAdapter.getUniqueIdentifier(attr) : attr);
            if (valMap.get(feature) != null) {
                for (EObject holder : valMap.get(feature)) {
                    retSet.add(new NavigationHelperSetting(attr, holder, value));
                }
            } else {
            	contentAdapter.maintainMetamodel(attr);
            }
        }

        return retSet;
    }

    @Override
    public Collection<EObject> findByAttributeValue(Object value, EAttribute attribute) {
        Map<Object, Set<EObject>> valMap = contentAdapter.getValueToFeatureToHolderMap().row(value);
        Object feature = (contentAdapter.isDynamicModel() ? NavigationHelperContentAdapter.getUniqueIdentifier(attribute) : attribute);
        if (valMap.get(feature) == null) {
        	contentAdapter.maintainMetamodel(attribute);
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(valMap.get(feature));
        }
    }
    
    @Override
    public void processAllFeatureInstances(EStructuralFeature feature, IEStructuralFeatureProcessor processor) {
    	contentAdapter.maintainMetamodel(feature);
       final Map<Object, Set<EObject>> instanceMap = contentAdapter.getValueToFeatureToHolderMap().column(feature);
        for (Entry<Object, Set<EObject>> entry : instanceMap.entrySet()) {
            for (EObject src : entry.getValue()) {
                processor.process(feature, src, entry.getKey());
            }
        }
    }

    // @Override
    // public Collection<Setting> findAllAttributeValuesByType(Class<?> clazz) {
    // Set<Setting> retSet = new HashSet<Setting>();
    //
    // for (Object value : contentAdapter.featureMap.keySet()) {
    // if (value.getClass().equals(clazz)) {
    // for (EStructuralFeature attr : contentAdapter.featureMap.get(value).keySet()) {
    // for (EObject holder : contentAdapter.featureMap.get(value).get(attr)) {
    // retSet.add(new NavigationHelperSetting(attr, holder, value));
    // }
    // }
    // }
    // }
    //
    // return retSet;
    // }

    @Override
    public Collection<Setting> getInverseReferences(EObject target) {
        Set<Setting> retSet = new HashSet<Setting>();
        Map<Object, Set<EObject>> valMap = contentAdapter.getValueToFeatureToHolderMap().row(target);

        for (Entry<Object, Set<EObject>> entry : valMap.entrySet()) {
            for (EObject source : entry.getValue()) {
                if (contentAdapter.isDynamicModel()) {
                    retSet.add(new NavigationHelperSetting(contentAdapter.getKnownFeature((String) entry.getKey()), source, target));
                }
                else {
                    retSet.add(new NavigationHelperSetting((EStructuralFeature) entry.getKey(), source, target));
                }
            }
        }

        return retSet;
    }

    @Override
    public Collection<Setting> getInverseReferences(EObject target, Collection<EReference> references) {
        Set<Setting> retSet = new HashSet<Setting>();
        Map<Object, Set<EObject>> valMap = contentAdapter.getValueToFeatureToHolderMap().row(target);

        for (EReference ref : references) {
            Object feature = (contentAdapter.isDynamicModel() ? NavigationHelperContentAdapter.getUniqueIdentifier(ref) : ref);
            if (valMap.get(feature) != null) {
                for (EObject source : valMap.get(feature)) {
                    retSet.add(new NavigationHelperSetting(ref, source, target));
                }
            } else {
            	contentAdapter.maintainMetamodel(ref);           	
            }
        }

        return retSet;
    }

    @Override
    public Collection<EObject> getInverseReferences(EObject target, EReference reference) {
        Object feature = (contentAdapter.isDynamicModel() ? NavigationHelperContentAdapter.getUniqueIdentifier(reference) : reference);
        Map<Object, Set<EObject>> valMap = contentAdapter.getValueToFeatureToHolderMap().row(target);
        if (valMap.get(feature) == null) {
        	contentAdapter.maintainMetamodel(reference);           	        	
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(valMap.get(feature));
        }
    }

	@Override
	@SuppressWarnings("unchecked")
    public Set<EObject> getReferenceValues(EObject source, EReference reference) {
    	Set<Object> targets = getFeatureTargets(source, reference);
        return (Set<EObject>)(Set<?>) targets; // this is known to be safe, as EReferences can only point to EObjects
    }

    @Override
    public Set<Object> getFeatureTargets(EObject source, EStructuralFeature _feature) {
        Object feature = (contentAdapter.isDynamicModel() ? NavigationHelperContentAdapter.getUniqueIdentifier(_feature) : _feature);
        final Set<Object> valSet = contentAdapter.getHolderToFeatureToValueMap().get(source, feature);
        if (valSet == null) {
        	contentAdapter.maintainMetamodel(_feature);           	
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(valSet);
        }
    }
    
    @Override
    public Map<EObject, Set<Object>> getFeatureInstances(EStructuralFeature _feature) {
        Object feature = (contentAdapter.isDynamicModel() ? NavigationHelperContentAdapter.getUniqueIdentifier(_feature) : _feature);
    	final Map<EObject, Set<Object>> valMap = contentAdapter.getHolderToFeatureToValueMap().column(feature);
        if (valMap == null) {
        	contentAdapter.maintainMetamodel(_feature);           	
            return Collections.emptyMap();
        } else {
            return Collections.unmodifiableMap(valMap);
        }
    }

    @Override
    public Collection<EObject> getDirectInstances(EClass type) {
        Set<EObject> valSet = contentAdapter.getInstanceSet(type);
        if (valSet == null) {
        	contentAdapter.maintainMetamodel(type);           	 	
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(valSet);
        }
    }

    @Override
    public Collection<EObject> getAllInstances(EClass type) {
        Set<EObject> retSet = new HashSet<EObject>();

        Set<EClass> valSet = NavigationHelperContentAdapter.getSubTypeMap().get(type);
        if (valSet != null) {
            for (EClass c : valSet) {
                final Set<EObject> instances = contentAdapter.getInstanceSet(c);
                if (instances != null) {
                    retSet.addAll(instances);
                }
            }
        } else {
        	contentAdapter.maintainMetamodel(type);           	 	
        }
        final Set<EObject> instances = contentAdapter.getInstanceSet(type);
        if (instances != null) {
            retSet.addAll(instances);
        }

        return retSet;
    }

    @Override
    public Collection<EObject> findByFeatureValue(Object value, EStructuralFeature _feature) {
        Object feature = (contentAdapter.isDynamicModel() ? NavigationHelperContentAdapter.getUniqueIdentifier(_feature) : _feature);
        Set<EObject> retSet = new HashSet<EObject>();
        Map<Object, Set<EObject>> valMap = contentAdapter.getValueToFeatureToHolderMap().row(value);
        if (valMap.get(feature) != null) {
            retSet.addAll(valMap.get(feature));
        } else {
        	contentAdapter.maintainMetamodel(_feature);           	
        }
        return retSet;
    }

    @Override
    public Set<EObject> getHoldersOfFeature(EStructuralFeature _feature) {
        Object feature = (contentAdapter.isDynamicModel() ? NavigationHelperContentAdapter.getUniqueIdentifier(_feature) : _feature);
        Multiset<EObject> holders = contentAdapter.getFeatureToHolderMap().get(feature);
        if (holders == null) {
        	contentAdapter.maintainMetamodel(_feature);           	
           return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(holders.elementSet());
        }
    }

    @Override
    public void addInstanceListener(Collection<EClass> classes, InstanceListener listener) {
        contentAdapter.maintainMetamodel(classes);
        Collection<EClass> registered = this.instanceListeners.get(listener);
        if (registered == null) {
            registered = new HashSet<EClass>();
            this.instanceListeners.put(listener, registered);
        }
        registered.addAll(classes);
    }

    @Override
    public void removeInstanceListener(Collection<EClass> classes, InstanceListener listener) {
        Collection<EClass> restriction = this.instanceListeners.get(listener);
        if (restriction != null) {
            restriction.removeAll(classes);
            if (restriction.size() == 0) {
                this.instanceListeners.remove(listener);
            }
        }
    }

    @Override
    public void addFeatureListener(Collection<EStructuralFeature> features, FeatureListener listener) {
        for (EStructuralFeature feature : features) {
            contentAdapter.maintainMetamodel(feature);
        }
        Collection<EStructuralFeature> registered = this.featureListeners.get(listener);
        if (registered == null) {
            registered = new HashSet<EStructuralFeature>();
            this.featureListeners.put(listener, registered);
        }
        registered.addAll(features);
    }

    @Override
    public void removeFeatureListener(Collection<EStructuralFeature> features, FeatureListener listener) {
        Collection<EStructuralFeature> restriction = this.featureListeners.get(listener);
        if (restriction != null) {
            restriction.removeAll(features);
            if (restriction.size() == 0) {
                this.featureListeners.remove(listener);
            }
        }
    }

    @Override
    public void addDataTypeListener(Collection<EDataType> types, DataTypeListener listener) {
        contentAdapter.maintainMetamodel(types);
        Collection<EDataType> registered = this.dataTypeListeners.get(listener);
        if (registered == null) {
            registered = new HashSet<EDataType>();
            this.dataTypeListeners.put(listener, registered);
        }
        registered.addAll(types);
    }

    @Override
    public void removeDataTypeListener(Collection<EDataType> types, DataTypeListener listener) {
        Collection<EDataType> restriction = this.dataTypeListeners.get(listener);
        if (restriction != null) {
            restriction.removeAll(types);
            if (restriction.size() == 0) {
                this.dataTypeListeners.remove(listener);
            }
        }
    }

    public Map<InstanceListener, Collection<EClass>> getInstanceListeners() {
        return instanceListeners;
    }

    public Map<FeatureListener, Collection<EStructuralFeature>> getFeatureListeners() {
        return featureListeners;
    }

    public Map<DataTypeListener, Collection<EDataType>> getDataTypeListeners() {
        return dataTypeListeners;
    }

    /**
     * @return the observedDataTypes
     */
    public Set<EDataType> getObservedDataTypes() {
        return observedDataTypes;
    }
    
    @Override
    public void addLightweightEObjectObserver(LightweightEObjectObserver observer, EObject observedObject){
        Collection<EObject> observedObjects = lightweightObservers.get(observer);
        if(observedObjects == null) {
            observedObjects = new HashSet<EObject>();
            observedObjects.add(observedObject);
        }
        lightweightObservers.put(observer, observedObjects);
    }
    
    @Override
    public void removeLightweightEObjectObserver(LightweightEObjectObserver observer, EObject observedObject) {
        Collection<EObject> observedObjects = lightweightObservers.get(observer);
        if(observedObjects != null) {
            observedObjects.remove(observedObject);
            if(observedObjects.isEmpty()) {
                lightweightObservers.remove(observer);
            }
        }
    }
    
    /**
     * @return the lightweightObservers
     */
    public Map<LightweightEObjectObserver, Collection<EObject>> getLightweightObservers() {
        return lightweightObservers;
    }

    /**
     * These runnables will be called after updates by the manipulationListener at its own discretion. Can be used e.g.
     * to check delta monitors.
     * 
     * @deprecated use {@link #addBaseIndexChangeListener(IncQueryBaseIndexChangeListener)} instead! 
     */
//    @Override
//    public Set<Runnable> getAfterUpdateCallbacks() {
//        return afterUpdateCallbacks;
//    }

    /**
     * This will run after updates.
     */
    protected void notifyBaseIndexChangeListeners(boolean baseIndexChanged) {
        if (!baseIndexChangeListeners.isEmpty()) {
            for (IncQueryBaseIndexChangeListener listener : new ArrayList<IncQueryBaseIndexChangeListener>(baseIndexChangeListeners)) {
                try {
                    if(!listener.onlyOnIndexChange() || baseIndexChanged) {
                        listener.notifyChanged(baseIndexChanged);
                    }
                } catch (Exception ex) {
                    logger.fatal("EMF-IncQuery Base encountered an error in delivering notifications about changes. ",
                            ex);
                }
            }
        }
    }

    @Override
    public void addBaseIndexChangeListener(IncQueryBaseIndexChangeListener listener) {
        checkArgument(listener != null, "Cannot add null listener!");
        baseIndexChangeListeners.add(listener);
    }

    @Override
    public void removeBaseIndexChangeListener(IncQueryBaseIndexChangeListener listener) {
        checkArgument(listener != null, "Cannot remove null listener!");
        baseIndexChangeListeners.remove(listener);
    }

    protected void considerForExpansion(EObject obj) {
        if (expansionAllowed) {
            Resource eResource = obj.eResource();
            if (eResource != null && eResource.getResourceSet() == null) {
                expandToAdditionalRoot(eResource);
            }
        }
    }

    protected void expandToAdditionalRoot(Notifier root) {
        if (modelRoots.add(root)) {
            if (root instanceof ResourceSet) {
                expansionAllowed = true;
            }
            contentAdapter.addAdapter(root);
            contentAdapter.notifyBaseIndexChangeListeners();
        }
    }

    /**
     * @return the expansionAllowed
     */
    public boolean isExpansionAllowed() {
        return expansionAllowed;
    }

    /**
     * @return the directlyObservedClasses
     */
    public Set<EClass> getDirectlyObservedClasses() {
        return directlyObservedClasses;
    }

    public boolean isObserved(EClass clazz) {
        return inWildcardMode || getAllObservedClasses().contains(clazz);
    }

    /**
     * not just the directly observed classes, but also their known subtypes
     */
    public Set<EClass> getAllObservedClasses() {
        if (allObservedClasses == null) {
            allObservedClasses = new HashSet<EClass>();
            for (EClass eClass : directlyObservedClasses) {
                allObservedClasses.add(eClass);
                final Set<EClass> subTypes = NavigationHelperContentAdapter.getSubTypeMap().get(eClass);
                if (subTypes != null) {
                    allObservedClasses.addAll(subTypes);
                }
            }
        }
        return allObservedClasses;
    }
    
    @Override
    public void registerObservedTypes(Set<EClass> classes, Set<EDataType> dataTypes, Set<EStructuralFeature> features) {
        ensureNotInWildcardMode();
        if (classes !=null || features != null || dataTypes!=null) {
			final Set<EStructuralFeature> resolvedFeatures = resolveAll(features);
			final Set<EClass> resolvedClasses = resolveAll(classes);
			final Set<EDataType> resolvedDatatypes = resolveAll(dataTypes);
			
			contentAdapter.maintainMetamodel(resolvedClasses);
			contentAdapter.maintainMetamodel(resolvedDatatypes);
			for (EStructuralFeature feature : resolvedFeatures) {
	        	contentAdapter.maintainMetamodel(feature.getEType());           	 	
	        	contentAdapter.maintainMetamodel(feature.getEContainingClass());           	 	
			}
			
			try {
			     coalesceTraversals(new Callable<Void>() {
			         @Override
			         public Void call() throws Exception {
			         	delayedFeatures.addAll(resolvedFeatures);
			         	delayedDataTypes.addAll(resolvedDatatypes);
			         	delayedClasses.addAll(resolvedClasses);
			         	return null;
			         }
			     });
			 } catch (InvocationTargetException ex) {
			     processingError(ex.getCause(), "register en masse the observed EClasses " + resolvedClasses
			    		 + " and EDatatypes " + resolvedDatatypes
			    		 + " and EStructuralFeatures " + resolvedFeatures);
			 } catch (Exception ex) {
			     processingError(ex, "register en masse the observed EClasses " + resolvedClasses
			    		 + " and EDatatypes " + resolvedDatatypes
			    		 + " and EStructuralFeatures " + resolvedFeatures);
			 }
	     }
	 }

    @Override
    public void unregisterObservedTypes(Set<EClass> classes,
    		Set<EDataType> dataTypes, Set<EStructuralFeature> features) {
    	unregisterEClasses(classes);
    	unregisterEDataTypes(dataTypes);
    	unregisterEStructuralFeatures(features);
    }
    
    @Override
    public void registerEStructuralFeatures(Set<EStructuralFeature> features) {
        ensureNotInWildcardMode();
        if (features != null) {
            final Set<EStructuralFeature> resolved = resolveAll(features);
            
            for (EStructuralFeature feature : resolved) {
                contentAdapter.maintainMetamodel(feature);
            }
            
            try {
                coalesceTraversals(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                    	delayedFeatures.addAll(resolved);
                    	return null;
                    }
                });
            } catch (InvocationTargetException ex) {
                processingError(ex.getCause(), "register the observed EStructuralFeatures: " + resolved);
            } catch (Exception ex) {
                processingError(ex, "register the observed EStructuralFeatures: " + resolved);
            }
        }
    }

    @Override
    public void unregisterEStructuralFeatures(Set<EStructuralFeature> features) {
        ensureNotInWildcardMode();
        if (features != null) {
            features = resolveAll(features);
            ensureNoListeners(features, featureListeners);									
            observedFeatures.removeAll(features);
            delayedFeatures.removeAll(features);
            for (EStructuralFeature f : features) {
                contentAdapter.getValueToFeatureToHolderMap().column(NavigationHelperContentAdapter.getUniqueIdentifier(f)).clear();
            }
        }
    }

    @Override
    public void registerEClasses(Set<EClass> classes) {
        ensureNotInWildcardMode();
        if (classes != null) {
            final Set<EClass> resolvedClasses = resolveAll(classes);
            contentAdapter.maintainMetamodel(resolvedClasses);
            
            try {
                coalesceTraversals(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                    	delayedClasses.addAll(resolvedClasses);
                    	return null;
                    }
                });
            } catch (InvocationTargetException ex) {
                processingError(ex.getCause(), "register the observed EClasses: " + resolvedClasses);
            } catch (Exception ex) {
                processingError(ex, "register the observed EClasses: " + resolvedClasses);
            }
        }
    }

    /**
     * @param classes
     */
    protected void startObservingClasses(Set<EClass> classes) {
        directlyObservedClasses.addAll(classes);
        getAllObservedClasses().addAll(classes);
        for (EClass eClass : classes) {
            final Set<EClass> subTypes = NavigationHelperContentAdapter.getSubTypeMap().get(eClass);
            if (subTypes != null) {
                allObservedClasses.addAll(subTypes);
            }
        }
    }

    @Override
    public void unregisterEClasses(Set<EClass> classes) {
        ensureNotInWildcardMode();
        if (classes != null) {
            classes = resolveAll(classes);
            ensureNoListeners(classes, instanceListeners);									
            directlyObservedClasses.removeAll(classes);
            allObservedClasses = null;
            delayedClasses.removeAll(classes);
            for (EClass c : classes) {
                contentAdapter.removeInstanceSet(c);
            }
        }
    }



    @Override
    public void registerEDataTypes(Set<EDataType> dataTypes) {
        ensureNotInWildcardMode();
        if (dataTypes != null) {
            final Set<EDataType> resolved = resolveAll(dataTypes);
            contentAdapter.maintainMetamodel(resolved);
            
            try {
                coalesceTraversals(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                    	delayedDataTypes.addAll(resolved);
                    	return null;
                    }
                });
            } catch (InvocationTargetException ex) {
                processingError(ex.getCause(), "register the observed EDataTypes: " + resolved);
            } catch (Exception ex) {
                processingError(ex, "register the observed EDataTypes: " + resolved);
            }
        }
    }

    @Override
    public void unregisterEDataTypes(Set<EDataType> dataTypes) {
        ensureNotInWildcardMode();
        if (dataTypes != null) {
            dataTypes = resolveAll(dataTypes);
            ensureNoListeners(dataTypes, dataTypeListeners);									
            observedDataTypes.removeAll(dataTypes);
            delayedDataTypes.removeAll(dataTypes);
            for (EDataType dataType : dataTypes) {
                contentAdapter.removeDataTypeMap(dataType);
            }
        }
    }

    @Override
    public <V> V coalesceTraversals(Callable<V> callable) throws InvocationTargetException {
        V finalResult = null;
        
        if (delayTraversals) { // reentrant case, no special action needed
            try {
            	finalResult = callable.call();
            } catch (Exception e) {
                throw new InvocationTargetException(e);
            }
            return finalResult;
        }
        
	    boolean firstRun = true;
        while (callable != null) {   // repeat if post-processing needed  	
	        delayedClasses = new HashSet<EClass>();
	        delayedFeatures = new HashSet<EStructuralFeature>();
	        delayedDataTypes = new HashSet<EDataType>();

        	try {
        		try {
        			delayTraversals = true;
        			V result = callable.call();
        			if (firstRun) {
        				firstRun = false;
        				finalResult = result; 
        			}
        		} finally {
        			delayTraversals = false;
        			callable = null;
        			
        			delayedFeatures = setMinus(delayedFeatures, observedFeatures);
        			delayedClasses = setMinus(delayedClasses, directlyObservedClasses);
        			delayedDataTypes = setMinus(delayedDataTypes, observedDataTypes);
        			
        			boolean classesWarrantTraversal = !setMinus(delayedClasses, getAllObservedClasses()).isEmpty();
        			
        			if (!delayedClasses.isEmpty() || !delayedFeatures.isEmpty() || !delayedDataTypes.isEmpty()) {
        				final HashSet<EClass> oldClasses = new HashSet<EClass>(directlyObservedClasses);
        				startObservingClasses(delayedClasses);
        				observedFeatures.addAll(delayedFeatures);
        				observedDataTypes.addAll(delayedDataTypes);
        				
        				// make copies so that original accumulators can be cleaned for the next cycle
        				// or for the rare case that a coalesced  traversal is invoked during visitation, 
        				// e.g. by a derived feature implementation
        				final HashSet<EClass> toGatherClasses = new HashSet<EClass>(delayedClasses);
        				final HashSet<EStructuralFeature> toGatherFeatures = new HashSet<EStructuralFeature>(
        						delayedFeatures);
        				final HashSet<EDataType> toGatherDataTypes = new HashSet<EDataType>(delayedDataTypes);
        				
        				if (classesWarrantTraversal || !toGatherFeatures.isEmpty() || !toGatherDataTypes.isEmpty()) {
        					// repeat the cycle with this visit
        					final NavigationHelperVisitor visitor = new NavigationHelperVisitor.TraversingVisitor(this,
        							toGatherFeatures, toGatherClasses, oldClasses, toGatherDataTypes);
        					
        					callable = new Callable<V>() {
        						@Override
        						public V call() throws Exception {
        							traverse(visitor);
        							return null;
        						}
							};
        					
        				}
        			}
        		}
	        } catch (Exception e) {
	            getLogger()
	                    .fatal("EMF-IncQuery Base encountered an error while traversing the EMF model to gather new information. ",
	                            e);
	            throw new InvocationTargetException(e);
	        }
        }
        return finalResult;
    }

    private void traverse(final NavigationHelperVisitor visitor) {
        for (Notifier root : modelRoots) {
            EMFModelComprehension.traverseModel(visitor, root);
        }
        contentAdapter.notifyBaseIndexChangeListeners();
    }

    /**
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void addRoot(Notifier emfRoot) throws IncQueryBaseException {
        addRootInternal(emfRoot);
    }

    
    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.base.api.NavigationHelper#cheapMoveTo(org.eclipse.emf.ecore.EObject, org.eclipse.emf.common.util.EList)
     */
    @Override
    public <T extends EObject> void cheapMoveTo(T element, EList<T> targetContainmentReferenceList) {
    	if (element.eAdapters().contains(contentAdapter) && 
    			targetContainmentReferenceList instanceof NotifyingList<?>) {
    		final Object listNotifier = ((NotifyingList<?>)targetContainmentReferenceList).getNotifier();
    		if (listNotifier instanceof Notifier && ((Notifier) listNotifier).eAdapters().contains(contentAdapter)) {
	     		contentAdapter.ignoreInsertionAndDeletion = element;
		    	try {
		    		targetContainmentReferenceList.add(element);
		    	} finally {
		        	contentAdapter.ignoreInsertionAndDeletion = null;
		    	}
    		} else {
    			targetContainmentReferenceList.add(element);
    		}
    	} else {
    		targetContainmentReferenceList.add(element);
    	}
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.base.api.NavigationHelper#cheapMoveTo(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EReference)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void cheapMoveTo(EObject element, EObject parent, EReference containmentFeature) {
    	contentAdapter.maintainMetamodel(containmentFeature);           	
    	if (containmentFeature.isMany())
    		cheapMoveTo(element, (EList)parent.eGet(containmentFeature));
    	else if (element.eAdapters().contains(contentAdapter) &&
    			parent.eAdapters().contains(contentAdapter)) 
    	{
     		contentAdapter.ignoreInsertionAndDeletion = element;
	    	try {
	    		parent.eSet(containmentFeature, element);
	    	} finally {
	        	contentAdapter.ignoreInsertionAndDeletion = null;
	    	}
		} else {
			parent.eSet(containmentFeature, element);
		}
    }
    
    /**
     * @param emfRoot
     * @throws IncQueryBaseException
     */
    private void addRootInternal(Notifier emfRoot) throws IncQueryBaseException {
        if (!((emfRoot instanceof EObject) || (emfRoot instanceof Resource) || (emfRoot instanceof ResourceSet))) {
            throw new IncQueryBaseException(IncQueryBaseException.INVALID_EMFROOT);
        }
        expandToAdditionalRoot(emfRoot);
    }
    
    @Override
    public Set<EClass> getAllCurrentClasses() {
    	return contentAdapter.getAllCurrentClasses();
    }
    
    protected void processingError(Throwable ex, String task) {
        contentAdapter.processingFatal(ex, task);
    }

    private void ensureNotInWildcardMode() {
    	if (inWildcardMode) {
    		throw new IllegalStateException("Cannot register/unregister observed classes in wildcard mode");
    	}
    }
    private <Type> void ensureNoListeners(Set<Type> observedTypes, final Map<?, Collection<Type>> listenerRegistry) {
    	for (Collection<Type> listenerTypes : listenerRegistry.values()) 
    		if (!Collections.disjoint(observedTypes, listenerTypes))
    			throw new IllegalStateException("Cannot unregister observed types for which there are active listeners");
    }
    private void ensureNoListenersForDispose() {
    	if (!(baseIndexChangeListeners.isEmpty() && featureListeners.isEmpty() && dataTypeListeners.isEmpty() && instanceListeners.isEmpty()))
    		throw new IllegalStateException("Cannot dispose while there are active listeners");
    }


}
