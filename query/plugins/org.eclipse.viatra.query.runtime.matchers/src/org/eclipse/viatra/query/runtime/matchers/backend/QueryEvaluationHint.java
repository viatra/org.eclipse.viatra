/*******************************************************************************
 * Copyright (c) 2010-2015, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.backend;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

/**
 * Provides VIATRA Query with additional hints on how a query should be evaluated. The same hint can be provided to multiple queries. 
 * 
 * <p> This class is immutable. Overriding options will create a new instance.
 * 
 * <p>
 * Here be dragons: for advanced users only.
 * 
 * @author Bergmann Gabor
 *
 */
public class QueryEvaluationHint {
    
    final IQueryBackendFactory queryBackendFactory; 
    @SuppressWarnings("rawtypes")
    final Map<QueryHintOption, Object> backendHintSettings;
    
    /**
     * Specifies the suggested query backend, and value settings for additional backend-specific options. Both parameters are optional (can be null).
     * 
     * @param backendHintSettings each entry in the map overrides backend-specific options regarding query evaluation (null-valued map entries permitted to erase hints); passing null means default options associated with the query
     * @param queryBackendFactory overrides the query evaluator algorithm; passing null retains the default algorithm associated with the query
     * @since 1.5
     */
    @SuppressWarnings("rawtypes") 
    public QueryEvaluationHint(
            Map<QueryHintOption, Object> backendHintSettings, 
            IQueryBackendFactory queryBackendFactory) {
        super();
        this.queryBackendFactory = queryBackendFactory;
        this.backendHintSettings = backendHintSettings == null ? 
                Collections.<QueryHintOption, Object>emptyMap() : 
                    ImmutableMap.copyOf(backendHintSettings);
    }
    
    /**
     * @deprecated As of 1.5, use {@link #QueryEvaluationHint(Map, IQueryBackendFactory)} instead. 
     * String-keyed hint setting map will be ignored.
     */
    public QueryEvaluationHint(
            IQueryBackendFactory queryBackendFactory, 
            Map<String, Object> backendHints) {
        this(null, queryBackendFactory);
    }
    
    /**
     * A suggestion for choosing the query evaluator algorithm. 
     * 
     * <p> Can be null.
     */
    public IQueryBackendFactory getQueryBackendFactory() {
        return queryBackendFactory;
    }

    /**
     * Each entry in the map overrides backend-specific options regarding query evaluation. 
     * 
     * <p>The map can be null if empty. Null-valued map entries are also permitted to erase hints. 
     * 
     * @deprecated As of 1.5, string keys are no longer available. Use {@link QueryHintOption} instead
     */
    public Map<String, Object> getBackendHints() {
        return Collections.emptyMap();
    }
    
    /**
     * Each entry in the immutable map overrides backend-specific options regarding query evaluation. 
     * 
     * <p>The map is non-null, even if empty. 
     * Null-valued map entries are also permitted to erase hints via {@link #overrideBy(QueryEvaluationHint)}. 
     * 
     * @since 1.5
     */
    @SuppressWarnings("rawtypes")
    public Map<QueryHintOption, Object> getBackendHintSettings() {
        return backendHintSettings;
    }


    /**
     * Override values in this hint and return a consolidated instance.
     * 
     * @since 1.4
     */
    public QueryEvaluationHint overrideBy(QueryEvaluationHint overridingHint){
        if (overridingHint == null)
            return this;
        
        IQueryBackendFactory factory = this.getQueryBackendFactory();
        if (overridingHint.getQueryBackendFactory() != null)
            factory = overridingHint.getQueryBackendFactory();
        
        @SuppressWarnings("rawtypes")
        Map<QueryHintOption, Object> hints = new HashMap<>(this.getBackendHintSettings());
        if (overridingHint.getBackendHintSettings() != null)
            hints.putAll(overridingHint.getBackendHintSettings());
        
        return new QueryEvaluationHint(hints, factory);
    }
    
    /**
     * Returns whether the given hint option is overridden.
     * @since 1.5
     */
    public boolean isOptionOverridden(QueryHintOption<?> option) {
        return getBackendHintSettings().containsKey(option);
    }
    
    /**
     * Returns the value of the given hint option from the given hint collection, or null if not defined.
     * @since 1.5
     */
    @SuppressWarnings("unchecked")
    public <HintValue> HintValue getValueOrNull(QueryHintOption<HintValue> option) {
        return (HintValue) getBackendHintSettings().get(option);
    }
    
    /**
     * Returns the value of the given hint option from the given hint collection, or the default value if not defined.
     * Intended to be called by backends to find out the definitive value that should be considered.
     * @since 1.5
     */
    public <HintValue> HintValue getValueOrDefault(QueryHintOption<HintValue> option) {
        return option.getValueOrDefault(this);
    }
   
    
    
    /**
     * Extract the requested capabilities
     * @since 1.4
     */
    public IMatcherCapability calculateRequiredCapability(PQuery query){
        return queryBackendFactory.calculateRequiredCapability(query, this);
    }


    
    @Override
    public int hashCode() {
        return Objects.hash(backendHintSettings, queryBackendFactory);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        QueryEvaluationHint other = (QueryEvaluationHint) obj;
        if (!Objects.equals(backendHintSettings, other.backendHintSettings))
                return false;
        if (!Objects.equals(queryBackendFactory, other.queryBackendFactory))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        if (getQueryBackendFactory() != null)
            sb.append("backend: ").append(getQueryBackendFactory().getBackendClass().getSimpleName());
        if (! backendHintSettings.isEmpty()) {
            sb.append("hints: ");
            if(backendHintSettings instanceof AbstractMap){
                sb.append(backendHintSettings.toString());
            } else {
                // we have to iterate on the contents
                String joinedHintMap = Joiner.on(", ").withKeyValueSeparator("=").join(backendHintSettings);
                sb.append('{').append(joinedHintMap).append('}');
            }            
        }
        
        final String result = sb.toString();
        return result.isEmpty() ? "defaults" : result;
    }
}
