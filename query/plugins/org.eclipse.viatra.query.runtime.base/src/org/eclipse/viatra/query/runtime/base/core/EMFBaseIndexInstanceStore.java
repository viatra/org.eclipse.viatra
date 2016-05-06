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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

/**
 * Stores the indexed contents of an EMF model 
 * 	(includes instance model information).
 * 
 * @author Gabor Bergmann
 *
 */
public class EMFBaseIndexInstanceStore {
    
    private NavigationHelperImpl navigationHelper;
    public EMFBaseIndexInstanceStore(NavigationHelperImpl navigationHelper) {
        super();
        this.navigationHelper = navigationHelper;
    }

    /**
     * since last run of after-update callbacks
     */
    boolean isDirty = false;
    
    /**
     * value -> feature (EAttribute or EReference) -> holder(s)
     * <p>
     * holder(s) are stored as
     * <ul>
     * <li>{@link Set} if feature is unique,
     * <li>{@link Multiset} if the feature is non-unique (a single holder is contained multiple times, once for each
     * time it lists the specific value in its feature vale list)
     * <ul>
     * <p>
     * Duplicates of non-unique features are stored in this map only; other index structures consider unique values
     * only.
     */
    private final Table<Object, Object, Collection<EObject>> valueToFeatureToHolderMap = HashBasedTable.create();

    /**
     * feature ((String id or EStructuralFeature) -> holder(s) constructed on-demand
     */
    private Map<Object, Multiset<EObject>> featureToHolderMap;

    /**
     * holder -> feature (String id or EStructuralFeature) -> value(s) constructed on-demand
     */
    private Table<EObject, Object, Set<Object>> holderToFeatureToValueMap;

    /**
     * key (String id or EClass instance) -> instance(s)
     */
    private final Map<Object, Set<EObject>> instanceMap = new HashMap<Object, Set<EObject>>();

    /**
     * key (String id or EDataType instance) -> multiset of value(s)
     */
    private final Map<Object, Map<Object, Integer>> dataTypeMap = new HashMap<Object, Map<Object, Integer>>();



    /**
     * @return the valueToFeatureToHolderMap
     */
    protected Table<Object, Object, Collection<EObject>> getValueToFeatureToHolderMap() {
        return valueToFeatureToHolderMap;
    }

    
    /**
     * This method uses the original {@link EStructuralFeature} instance or the String id.
     * 
     * @return true if this was the first time the value was added to this feature of this holder (false is only
     *         possible for non-unique features)
     */
    private boolean addToFeatureMap(final Object featureKey, boolean unique, final Object value, final EObject holder) {
        Collection<EObject> setVal = valueToFeatureToHolderMap.get(value, featureKey);

        if (setVal == null) {
            setVal = unique ? new HashSet<EObject>() : HashMultiset.<EObject> create();
            valueToFeatureToHolderMap.put(value, featureKey, setVal);

        }
        boolean changed = unique || !setVal.contains(holder);
        setVal.add(holder);
        return changed;
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
    private boolean removeFromFeatureMap(final Object featureKey, boolean unique, final Object value,
            final EObject holder) {
        final Collection<EObject> setHolder = valueToFeatureToHolderMap.get(value, featureKey);
        if (setHolder != null) {
            setHolder.remove(holder);

            if (setHolder.isEmpty()) {
                valueToFeatureToHolderMap.remove(value, featureKey);
            }
            return unique || (!setHolder.contains(holder));
        }
        return false;
    }

    /**
     * This method uses either the original {@link EStructuralFeature} instance or the String id.
     */
    private void removeFromDirectFeatureMap(final EObject holder, final Object feature, final Object value) {
        final Set<Object> setVal = holderToFeatureToValueMap.get(holder, feature);
        if (setVal != null) {
            setVal.remove(value);

            if (setVal.isEmpty()) {
                holderToFeatureToValueMap.remove(holder, feature);
            }
        }
    }

    public void insertFeatureTuple(final Object featureKey, boolean unique, final Object value, final EObject holder) {
        boolean changed = addToFeatureMap(featureKey, unique, value, holder);
        if (changed) { // if not duplicated
            if (featureToHolderMap != null) {
                addToReversedFeatureMap(featureKey, holder);
            }
            if (holderToFeatureToValueMap != null) {
                addToDirectFeatureMap(holder, featureKey, value);
            }

            isDirty = true;
            navigationHelper.notifyFeatureListeners(holder, featureKey, value, true);
        }
    }

    public void removeFeatureTuple(final Object featureKey, boolean unique, final Object value, final EObject holder) {
        boolean changed = removeFromFeatureMap(featureKey, unique, value, holder);
        if (changed) { // if not duplicated
            if (featureToHolderMap != null) {
                removeFromReversedFeatureMap(featureKey, holder);
            }
            if (holderToFeatureToValueMap != null) {
                removeFromDirectFeatureMap(holder, featureKey, value);
            }

            isDirty = true;
            navigationHelper.notifyFeatureListeners(holder, featureKey, value, false);
        }
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
        navigationHelper.notifyInstanceListeners(keyClass, value, true);
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
        navigationHelper.notifyInstanceListeners(keyClass, value, false);
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
        Map<Object, Integer> valMap = dataTypeMap.get(keyType);
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
        navigationHelper.notifyDataTypeListeners(keyType, value, true, firstOccurrence);
    }

    public void removeFromDataTypeMap(final Object keyType, final Object value) {
        final Map<Object, Integer> valMap = dataTypeMap.get(keyType);
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
            navigationHelper.notifyDataTypeListeners(keyType, value, false, lastOccurrence);
        }
        // else: inconsistent deletion? log error?
    }

    // END ********* DataTypeMap *********

    
    /**
     * Decodes the collection of holders (potentially non-unique) to a unique set
     */
    protected static Set<EObject> holderCollectionToUniqueSet(Collection<EObject> holders) {
        if (holders instanceof Set<?>) {
            return (Set<EObject>) holders;
        } else if (holders instanceof Multiset<?>) {
            Multiset<EObject> multiSet = (Multiset<EObject>) holders;
            return multiSet.elementSet();
        } else
            throw new IllegalStateException("Neither Set nor Multiset: " + holders);
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
     * Calling this method will construct the map for all holders and features, consuming significant memory!
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
    
    private void initReversedFeatureMap() {
        for (final Cell<Object, Object, Collection<EObject>> entry : valueToFeatureToHolderMap.cellSet()) {
            final Object feature = entry.getColumnKey();
            for (final EObject holder : holderCollectionToUniqueSet(entry.getValue())) {
                addToReversedFeatureMap(feature, holder);
            }
        }
    }

    private void initDirectFeatureMap() {
        for (final Cell<Object, Object, Collection<EObject>> entry : valueToFeatureToHolderMap.cellSet()) {
            final Object value = entry.getRowKey();
            final Object feature = entry.getColumnKey();
            for (final EObject holder : holderCollectionToUniqueSet(entry.getValue())) {
                addToDirectFeatureMap(holder, feature, value);
            }
        }
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
        final Set<EClass> result = Sets.newHashSet();
        final Set<Object> classifierKeys = instanceMap.keySet();
        for (final Object classifierKey : classifierKeys) {
            final EClassifier knownClassifier = navigationHelper.metaStore.getKnownClassifierForKey(classifierKey);
            if (knownClassifier instanceof EClass) {
                result.add((EClass) knownClassifier);
            }
        }
        return result;
    }

    Set<Object> getOldValuesForHolderAndFeature(EObject source, EStructuralFeature feature) {
        // while this is slower than using the holderToFeatureToValueMap, we do not want to construct that to avoid
        // memory overhead
        Map<Object, Collection<EObject>> oldValuesToHolders = valueToFeatureToHolderMap.column(feature);
        Set<Object> oldValues = new HashSet<Object>();
        for (Entry<Object, Collection<EObject>> entry : oldValuesToHolders.entrySet()) {
            if (entry.getValue().contains(source)) {
                oldValues.add(entry.getKey());
            }
        }
        return oldValues;
    }
    
}
