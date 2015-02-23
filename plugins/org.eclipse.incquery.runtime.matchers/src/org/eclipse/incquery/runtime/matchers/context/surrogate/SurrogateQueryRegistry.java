/*******************************************************************************
 * Copyright (c) 2010-2015, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.matchers.context.surrogate;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * @author Abel Hegedus
 *
 */
public class SurrogateQueryRegistry {
    
    private Map<Object,String> registeredSurrogateQueryFQNMap = new WeakHashMap<Object,String>();
    private Map<Object,String> dynamicSurrogateQueryFQNMap = new WeakHashMap<Object,String>();
    
    /**
     * Hidden constructor
     */
    private SurrogateQueryRegistry() {
    }

    private static final SurrogateQueryRegistry INSTANCE = new SurrogateQueryRegistry();
    
    public static SurrogateQueryRegistry instance() {
        return INSTANCE;
    }
    
    /**
     * 
     * @param feature
     * @param surrogateQueryFQN
     * @return the previous surrogate query FQN associated with feature, or null if there was no such query FQN registered
     * @throws IllegalArgumentException if feature or surrogateQueryFQN is null 
     */
    public String registerSurrogateQueryForFeature(Object feature, String surrogateQueryFQN) {
        Preconditions.checkArgument(feature != null, "Feature must not be null!");
        Preconditions.checkArgument(surrogateQueryFQN != null, "Surrogate query FQN must not be null!");
        return registeredSurrogateQueryFQNMap.put(feature, surrogateQueryFQN);
    }
    
    public String addDynamicSurrogateQueryForFeature(Object feature, String surrogateQueryFQN) {
        Preconditions.checkArgument(feature != null, "Feature must not be null!");
        Preconditions.checkArgument(surrogateQueryFQN != null, "Surrogate query FQN must not be null!");
        return dynamicSurrogateQueryFQNMap.put(feature, surrogateQueryFQN);
    }

    public String removeDynamicSurrogateQueryForFeature(Object feature) {
        Preconditions.checkArgument(feature != null, "Feature must not be null!");
        return dynamicSurrogateQueryFQNMap.remove(feature);
    }
    
    /**
     * 
     * @param feature that may have surrogate query defined, null not allowed
     * @return true if the feature has a surrogate query defined
     * @throws IllegalArgumentException if feature is null
     */
    public boolean hasSurrogateQueryFQN(Object feature) {
        Preconditions.checkArgument(feature != null, "Feature must not be null!");
        boolean surrogateExists = dynamicSurrogateQueryFQNMap.containsKey(feature);
        if(!surrogateExists){
            surrogateExists = registeredSurrogateQueryFQNMap.containsKey(feature);
        }
        return surrogateExists;
    }
    
    /**
     * 
     * @param feature for which the surrogate query FQN should be returned
     * @return the surrogate query FQN defined for the feature
     * @throws IllegalArgumentException if feature is null
     * @throws NoSuchElementException if the feature has no surrogate query defined, use {@link #hasSurrogateQueryFQN} to check
     */
    public String getSurrogateQueryFQN(Object feature) {
        Preconditions.checkArgument(feature != null, "Feature must not be null!");
        String surrogateFQN = dynamicSurrogateQueryFQNMap.get(feature);
        if(surrogateFQN == null) {
            surrogateFQN = registeredSurrogateQueryFQNMap.get(feature);
        }
        if(surrogateFQN != null) {
            return surrogateFQN;
        } else {
            throw new NoSuchElementException(String.format("Feature %s has no surrogate query defined! Use #hasSurrogateQueryFQN to check existence.", feature));
        }
    }

    /**
     * @return the map of features with registered surrogate query FQNs
     */
    public Map<Object, String> getRegisteredSurrogateQueryFQNMap() {
        return registeredSurrogateQueryFQNMap;
    }
    
    /**
     * @return the map of features with dynamically added surrogate query FQNs
     */
    public Map<Object, String> getDynamicSurrogateQueryFQNMap() {
        return dynamicSurrogateQueryFQNMap;
    }
    
    /**
     * Returns a copy of the map that contains all features with surrogate queries.
     * Dynamically added surrogates override registered surrogates and each feature will only appear once in the map. 
     * 
     * @return a new copy of the map that contains all features with surrogate queries.
     */
    public Map<Object, String> getAllSurrogateQueryFQNMap() {
        HashMap<Object, String> allSurrogateQueries = Maps.newHashMap(registeredSurrogateQueryFQNMap);
        allSurrogateQueries.putAll(dynamicSurrogateQueryFQNMap);
        return allSurrogateQueries;
    }
}
