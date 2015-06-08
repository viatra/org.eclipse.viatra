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

import org.eclipse.incquery.runtime.matchers.context.IInputKey;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * @author Abel Hegedus
 *
 */
public class SurrogateQueryRegistry {
    
    private Map<IInputKey, PQuery> registeredSurrogateQueryMap = Maps.newHashMap();
    private Map<IInputKey, PQuery> dynamicSurrogateQueryMap = Maps.newHashMap();
    
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
     * @param surrogateQuery
     * @return the previous surrogate query FQN associated with feature, or null if there was no such query FQN registered
     * @throws IllegalArgumentException if feature or surrogateQueryFQN is null 
     */
    public PQuery registerSurrogateQueryForFeature(IInputKey feature, PQuery surrogateQuery) {
        Preconditions.checkArgument(feature != null, "Feature must not be null!");
        Preconditions.checkArgument(surrogateQuery != null, "Surrogate query must not be null!");
        return registeredSurrogateQueryMap.put(feature, surrogateQuery);
    }
    
    public PQuery addDynamicSurrogateQueryForFeature(IInputKey feature, PQuery surrogateQuery) {
        Preconditions.checkArgument(feature != null, "Feature must not be null!");
        Preconditions.checkArgument(surrogateQuery != null, "Surrogate query FQN must not be null!");
        return dynamicSurrogateQueryMap.put(feature, surrogateQuery);
    }

    public PQuery removeDynamicSurrogateQueryForFeature(IInputKey feature) {
        Preconditions.checkArgument(feature != null, "Feature must not be null!");
        return dynamicSurrogateQueryMap.remove(feature);
    }
    
    /**
     * 
     * @param feature that may have surrogate query defined, null not allowed
     * @return true if the feature has a surrogate query defined
     * @throws IllegalArgumentException if feature is null
     */
    public boolean hasSurrogateQueryFQN(IInputKey feature) {
        Preconditions.checkArgument(feature != null, "Feature must not be null!");
        boolean surrogateExists = dynamicSurrogateQueryMap.containsKey(feature);
        if(!surrogateExists){
            surrogateExists = registeredSurrogateQueryMap.containsKey(feature);
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
    public PQuery getSurrogateQuery(IInputKey feature) {
        Preconditions.checkArgument(feature != null, "Feature must not be null!");
        PQuery surrogate = dynamicSurrogateQueryMap.get(feature);
        if(surrogate == null) {
            surrogate = registeredSurrogateQueryMap.get(feature);
        }
        if(surrogate != null) {
            return surrogate;
        } else {
            throw new NoSuchElementException(String.format("Feature %s has no surrogate query defined! Use #hasSurrogateQueryFQN to check existence.", feature));
        }
    }

    /**
     * @return the map of features with registered surrogate query FQNs
     */
    public Map<IInputKey, PQuery> getRegisteredSurrogateQueryFQNMap() {
        return registeredSurrogateQueryMap;
    }
    
    /**
     * @return the map of features with dynamically added surrogate query FQNs
     */
    public Map<IInputKey, PQuery> getDynamicSurrogateQueryFQNMap() {
        return dynamicSurrogateQueryMap;
    }
    
    /**
     * Returns a copy of the map that contains all features with surrogate queries.
     * Dynamically added surrogates override registered surrogates and each feature will only appear once in the map. 
     * 
     * @return a new copy of the map that contains all features with surrogate queries.
     */
    public Map<IInputKey, PQuery> getAllSurrogateQueryFQNMap() {
        HashMap<IInputKey, PQuery> allSurrogateQueries = Maps.newHashMap(registeredSurrogateQueryMap);
        allSurrogateQueries.putAll(dynamicSurrogateQueryMap);
        return allSurrogateQueries;
    }
}
