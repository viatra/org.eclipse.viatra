/*******************************************************************************
 * Copyright (c) 2010-2016, Grill Balázs, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.base.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * @author Grill Balázs
 *
 */
public class EMFBaseIndexStatisticsStore {

    /**
     * A common map is used to store instance/value statistics. The key can be an {@link EClassifier}, 
     * {@link EStructuralFeature} or a String ID. 
     */
    private final Map<Object, Integer> stats = new HashMap<Object, Integer>();
    
    /**
     * Table feature, element -> count
     */
    private final Table<Object, Object, Integer> featureStats = HashBasedTable.create();
    
    public void addFeature(Object element, Object feature){
        addInstance(feature);
        Integer v = featureStats.get(feature, element);
        featureStats.put(feature, element, v == null ? 1 : v+1);
    }
    
    public void removeFeature(Object element, Object feature){
        removeInstance(feature);
        Integer v = featureStats.get(feature, element);
        Preconditions.checkArgument(v != null && v > 0, "No instances of %s -> %s is registered before calling removeFeature method.", element, feature);
        if (v.intValue() == 1){
            featureStats.remove(feature, element);
        }else{
            featureStats.put(feature, element, v-1);
        }
    }
    
    public int countFeatures(Object element, Object feature){
        Integer v = featureStats.get(feature, element);
        return v == null ? 0 : v.intValue();
    }
    
    public void addInstance(Object key){
        Integer v = stats.get(key);
        stats.put(key, v == null ? 1 : v+1);
    }
    
    public void removeInstance(Object key){
        Integer v = stats.get(key);
        Preconditions.checkArgument(v != null && v > 0, "No instances of %s is registered before calling removeInstance method.", key);
        if (v.intValue() == 1){
            stats.remove(key);
        }else{
            stats.put(key, v-1);
        }
    }
    
    public int countInstances(Object key){
        Integer v = stats.get(key);
        return v == null ? 0 : v.intValue();
    }
    
    public void removeType(Object key){
        featureStats.row(key).clear();
        stats.remove(key);
    }

    public int countFeatures(Object feature) {
        return countInstances(feature);
    }
    
}
