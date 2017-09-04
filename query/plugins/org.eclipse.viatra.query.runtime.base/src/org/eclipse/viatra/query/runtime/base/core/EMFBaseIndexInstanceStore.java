/*******************************************************************************
 * Copyright (c) 2010-2016, "Gabor Bergmann", Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   "Gabor Bergmann" - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.base.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.query.runtime.base.api.IStructuralFeatureInstanceProcessor;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.matchers.util.IMultiset;

/**
 * Stores the indexed contents of an EMF model 
 * 	(includes instance model information).
 * 
 * @author Gabor Bergmann
 * @noextend This class is not intended to be subclassed by clients.
 */
public class EMFBaseIndexInstanceStore extends AbstractBaseIndexStore {
    
    public EMFBaseIndexInstanceStore(NavigationHelperImpl navigationHelper, Logger logger) {
        super(navigationHelper, logger);
    }

    /**
     * since last run of after-update callbacks
     */
    boolean isDirty = false;
    
    /**
     * feature (EAttribute or EReference or equivalent String key) -> FeatureData
     * @since 1.7
     */
    private Map<Object, FeatureData> featureDataMap = CollectionsFactory.createMap();
    
    /**
     * value -> featureKey(s);
     * constructed on-demand, null if unused (hopefully most of the time)
     */
    private Map<Object, IMultiset<Object>> valueToFeatureMap = null; 

    
    /**
     * key (String id or EClass instance) -> instance(s)
     */
    private final Map<Object, Set<EObject>> instanceMap = CollectionsFactory.createMap();

    /**
     * key (String id or EDataType instance) -> multiset of value(s)
     */
    private final Map<Object, IMultiset<Object>> dataTypeMap = CollectionsFactory.createMap();


    
    /**
     * Bundles all instance store data specific to a given binary feature.
     * 
     * <p> TODO: specialize for to-one features and unique to-many features
     * <p> TODO: on-demand construction of valueToHolderMap
     * 
     * @author Gabor Bergmann
     * @since 1.7
     */
    class FeatureData {
        /** value -> holder(s) */
        private Map<Object, IMultiset<EObject>> valueToHolderMap = CollectionsFactory.createMap(); 
        /**
         * holder -> value(s);
         * constructed on-demand, null if unused
         */
        private Map<EObject, IMultiset<Object>> holderToValueMap; 
               
        /**
         * feature (EAttribute or EReference) or its string key (in dynamic EMF mode)
         */
        private Object featureKey;
        
        /**
         * @return feature (EAttribute or EReference) or its string key (in dynamic EMF mode)
         */
       public Object getFeatureKey() {
            return featureKey;
        }
        
        @Override
        public String toString() {
            return this.getClass().getSimpleName() + ":" + featureKey;
        }

        /**
         * @return true if this was the first time the value was added to this feature of this holder (false is only
         *         expected for non-unique features)
         */
        boolean insertFeatureTuple(boolean unique, final Object value, final EObject holder) {
            // TODO we currently assume V2H map exists
           boolean changed = addToValueToHolderMap(value, holder);
            if (holderToValueMap != null) {
                addToHolderToValueMap(value, holder);
            }
            
            if (unique && !changed) { 
                String msg = String.format(
                        "Error: trying to add duplicate value %s to the unique feature %s of host object %s. This indicates some errors in underlying model representation.", 
                        value, featureKey, holder);
                logNotificationHandlingError(msg);
            }
            return changed;
        }
        /**
         * @return true if this was the last duplicate of the value added to this feature of this holder (false is only
         *         expected for non-unique features)
         */
        boolean removeFeatureTuple(boolean unique, final Object value, final EObject holder) {
            try {
                // TODO we currently assume V2H map exists
                boolean changed = removeFromValueToHolderMap(value, holder);
                if (holderToValueMap != null) {
                    removeFromHolderToValueMap(value, holder);
                }
                
                if (unique && !changed) { 
                    String msg = String.format(
                            "Error: trying to remove duplicate value %s from the unique feature %s of host object %s. This indicates some errors in underlying model representation.", 
                            value, featureKey, holder);
                    logNotificationHandlingError(msg);
                }
                return changed;
            } catch (IllegalStateException ex) {
                String msg = String.format(
                        "Error: trying to remove non-existing value %s from the feature %s of host object %s. This indicates some errors in underlying model representation.", 
                        value, featureKey, holder);
                logNotificationHandlingError(msg);
                return false;
            }
        }

        private boolean addToHolderToValueMap(Object value, EObject holder) {
            IMultiset<Object> values = holderToValueMap.get(holder);
            if (values == null) {
                values = CollectionsFactory.createMultiset();
                holderToValueMap.put(holder, values);
            }
            boolean changed = values.addOne(value);
            return changed;
        }
        private boolean addToValueToHolderMap(final Object value, final EObject holder) {
            IMultiset<EObject> holders = valueToHolderMap.get(value);
            if (holders == null) {
                holders = CollectionsFactory.createMultiset();
                valueToHolderMap.put(value, holders);
            }
            boolean changed = holders.addOne(holder);
            return changed;
        }
        
        private boolean removeFromHolderToValueMap(Object value, EObject holder) throws IllegalStateException {
            IMultiset<Object> values = holderToValueMap.get(holder);
            boolean changed = values.removeOne(value);
            if (changed) {
                if (values.isEmpty())
                    holderToValueMap.remove(holder);
            }
            return changed;
        }
        private boolean removeFromValueToHolderMap(final Object value, final EObject holder) throws IllegalStateException {
            IMultiset<EObject> holders = valueToHolderMap.get(value);
            boolean changed = holders.removeOne(holder);
            if (changed) {
                if (holders.isEmpty())
                    valueToHolderMap.remove(value);
            }
            return changed;
        }

        private Map<EObject, IMultiset<Object>> getHolderToValueMap() {
            if (holderToValueMap == null) {
                holderToValueMap = CollectionsFactory.createMap();
                
                // TODO we currently assume V2H map exists
                for (Entry<Object, IMultiset<EObject>> entry : valueToHolderMap.entrySet()) {
                    Object value = entry.getKey();
                    IMultiset<EObject> holders = entry.getValue();
                    for (EObject holder : holders.keySet()) {
                        int count = holders.getCount(holder);
                        
                        IMultiset<Object> valuesOfHolder = holderToValueMap.get(holder);
                        if (valuesOfHolder == null) {
                            valuesOfHolder = CollectionsFactory.createMultiset();
                            holderToValueMap.put(holder, valuesOfHolder);
                        }
                        valuesOfHolder.addPositive(value, count);
                    }
                }
            }
            return holderToValueMap;
        }        
        private Map<Object, IMultiset<EObject>> getValueToHolderMap() {
            // TODO we currently assume V2H map exists
            return valueToHolderMap;
        }
        
        public void forEach(IStructuralFeatureInstanceProcessor processor) {
            // TODO we currently assume V2H map exists
            if (valueToHolderMap != null) {
                for (Entry<Object, IMultiset<EObject>> entry : valueToHolderMap.entrySet()) {
                    Object value = entry.getKey();
                    for (EObject eObject : entry.getValue().keySet()) {
                        processor.process(eObject, value);
                    }
                }
            } else throw new UnsupportedOperationException("TODO implement");
        }
        
        public Set<EObject> getAllDistinctHolders() {
            return getHolderToValueMap().keySet();
        }

        public Set<Object> getAllDistinctValues() {
            return getValueToHolderMap().keySet();
        }

        public Set<EObject> getDistinctHoldersOfValue(Object value) {
            IMultiset<EObject> holdersMultiset = getValueToHolderMap().get(value);
            if (holdersMultiset == null) 
                return Collections.emptySet();
            else return holdersMultiset.keySet();
        }


        public Set<Object> getDistinctValuesOfHolder(EObject holder) {
            IMultiset<Object> valuesMultiset = getHolderToValueMap().get(holder);
            if (valuesMultiset == null) 
                return Collections.emptySet();
            else return valuesMultiset.keySet();
        }

        public boolean isInstance(EObject source, Object target) {
            // TODO we currently assume V2H map exists
           if (valueToHolderMap != null) {
                return valueToHolderMap.get(target).containsNonZero(source);
           } else throw new UnsupportedOperationException("TODO implement");
        }

        
    }
    
    
    FeatureData getFeatureData(Object featureKey) {
        FeatureData data = featureDataMap.get(featureKey);
        if (data == null) {
            data = createFeatureData(featureKey);
            featureDataMap.put(featureKey, data);
        }
        return data;
    }

    /**
     * TODO: specialize for to-one features and unique to-many features
     */
    private FeatureData createFeatureData(Object featureKey) {
        FeatureData data = new FeatureData();
        data.featureKey = featureKey;
        return data;
    }




    protected void insertFeatureTuple(final Object featureKey, boolean unique, final Object value, final EObject holder) {
        boolean changed = getFeatureData(featureKey).insertFeatureTuple(unique, value, holder);
        if (changed) { // if not duplicated
            
            if (valueToFeatureMap != null) {
                insertIntoValueToFeatureMap(featureKey, value);
            }
            
            isDirty = true;
            navigationHelper.notifyFeatureListeners(holder, featureKey, value, true);
        }
    }

    protected void removeFeatureTuple(final Object featureKey, boolean unique, final Object value, final EObject holder) {
        boolean changed = getFeatureData(featureKey).removeFeatureTuple(unique, value, holder);
        if (changed) { // if not duplicated
            
            if (valueToFeatureMap != null) {
                removeFromValueToFeatureMap(featureKey, value);
            }
            
            isDirty = true;
            navigationHelper.notifyFeatureListeners(holder, featureKey, value, false);
        }
    }

    
    public Set<Object> getFeatureKeysPointingTo(Object target) {
        return getValueToFeatureMap().get(target).keySet();
    }
    
    private Map<Object, IMultiset<Object>> getValueToFeatureMap() {
        if (valueToFeatureMap == null) { // must be inverted from feature data
            valueToFeatureMap = CollectionsFactory.createMap();
            for (FeatureData featureData : featureDataMap.values()) {
                final Object featureKey = featureData.getFeatureKey();
                featureData.forEach(new IStructuralFeatureInstanceProcessor() {
                    @Override
                    public void process(EObject source, Object target) {
                        insertIntoValueToFeatureMap(featureKey, target);
                    }
                });
            }
        }
        return valueToFeatureMap;
    }
    
    private void insertIntoValueToFeatureMap(final Object featureKey, Object target) {
        IMultiset<Object> featureKeys = valueToFeatureMap.get(target);
        if (featureKeys == null) {
            featureKeys = CollectionsFactory.createMultiset();
            valueToFeatureMap.put(target, featureKeys);
        }
        featureKeys.addOne(featureKey);
    }
    private void removeFromValueToFeatureMap(final Object featureKey, final Object value) {
        IMultiset<Object> featureKeys = valueToFeatureMap.get(value);
        if (featureKeys == null) 
            throw new IllegalStateException();
        featureKeys.removeOne(featureKey);
        if (featureKeys.isEmpty()) 
            valueToFeatureMap.remove(value);
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
            set = CollectionsFactory.createSet();
            instanceMap.put(keyClass, set);
        }
        
        if (!set.add(value)) {
            String msg = String.format("Notification received to index %s as a %s, but it already exists in the index. This indicates some errors in underlying model representation.", value, keyClass);
            logNotificationHandlingError(msg);
        }

        isDirty = true;
        navigationHelper.notifyInstanceListeners(keyClass, value, true);
    }

    public void removeFromInstanceSet(final Object keyClass, final EObject value) {
        final Set<EObject> set = instanceMap.get(keyClass);
        if (set != null) {
            if(!set.remove(value)) {
                String msg = String.format("Notification received to remove %s as a %s, but it is missing from the index. This indicates some errors in underlying model representation.", value, keyClass);
                logNotificationHandlingError(msg);
            }

            if (set.isEmpty()) {
                instanceMap.remove(keyClass);
            }
        }

        isDirty = true;
        navigationHelper.notifyInstanceListeners(keyClass, value, false);
    }

    // END ********* InstanceSet *********

    // START ********* DataTypeMap *********
    public Set<Object> getDistinctDataTypeInstances(final Object keyType) {
        IMultiset<Object> values = dataTypeMap.get(keyType);
        return values == null ? Collections.emptySet() : values.keySet();
    }

    public void removeDataTypeMap(final Object keyType) {
        dataTypeMap.remove(keyType);
    }

    public void insertIntoDataTypeMap(final Object keyType, final Object value) {
        IMultiset<Object> valMap = dataTypeMap.get(keyType);
        if (valMap == null) {
            valMap = CollectionsFactory.createMultiset();
            dataTypeMap.put(keyType, valMap);
        }
        final boolean firstOccurrence = valMap.addOne(value);
        
        isDirty = true;
        navigationHelper.notifyDataTypeListeners(keyType, value, true, firstOccurrence);
    }

    public void removeFromDataTypeMap(final Object keyType, final Object value) {
        final IMultiset<Object> valMap = dataTypeMap.get(keyType);
        if (valMap != null) {
            final boolean lastOccurrence = valMap.removeOne(value);
            if (lastOccurrence) {
                if (valMap.isEmpty()) {
                    dataTypeMap.remove(keyType);
                }
            }

            isDirty = true;
            navigationHelper.notifyDataTypeListeners(keyType, value, false, lastOccurrence);
        }
    }

    // END ********* DataTypeMap *********
    
    protected Set<EObject> getHoldersOfFeature(Object featureKey) {
        FeatureData featureData = getFeatureData(featureKey);
        return featureData.getAllDistinctHolders();
    }

    /**
     * Returns all EClasses that currently have direct instances cached by the index.
     * <p>
     * Supertypes will not be returned, unless they have direct instances in the model as well. If not in
     * <em>wildcard mode</em>, only registered EClasses and their subtypes will be returned.
     * <p>
     * Note for advanced users: if a type is represented by multiple EClass objects, one of them is chosen as
     * representative and returned.
     */
    public Set<EClass> getAllCurrentClasses() {
        final Set<EClass> result = CollectionsFactory.createSet();
        final Set<Object> classifierKeys = instanceMap.keySet();
        for (final Object classifierKey : classifierKeys) {
            final EClassifier knownClassifier = navigationHelper.metaStore.getKnownClassifierForKey(classifierKey);
            if (knownClassifier instanceof EClass) {
                result.add((EClass) knownClassifier);
            }
        }
        return result;
    }

    Set<Object> getOldValuesForHolderAndFeature(EObject source, Object featureKey) {
        // while this is slower than using the holderToFeatureToValueMap, we do not want to construct that to avoid
        // memory overhead
        Map<Object, IMultiset<EObject>> oldValuesToHolders = getFeatureData(featureKey).valueToHolderMap;
        Set<Object> oldValues = new HashSet<Object>();
        for (Entry<Object, IMultiset<EObject>> entry : oldValuesToHolders.entrySet()) {
            if (entry.getValue().containsNonZero(source)) {
                oldValues.add(entry.getKey());
            }
        }
        return oldValues;
    }

    protected void forgetFeature(Object featureKey) {
        FeatureData removed = featureDataMap.remove(featureKey);
        if (valueToFeatureMap != null) {
            for (Object value : removed.getAllDistinctValues()) {
                removeFromValueToFeatureMap(featureKey, value);
            }
        }
    }

    
}
