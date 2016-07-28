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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

/**
 * Provides VIATRA Query with additional hints on how a query should be evaluated. The same hint can be provided to multiple queries. 
 * 
 * <p>
 * Here be dragons: for advanced users only.
 * 
 * @author Bergmann Gabor
 *
 */
public class QueryEvaluationHint {
	
	IQueryBackendFactory queryBackendFactory; 
	Map<String, Object> backendHints;
	
	/**
	 * Specifies the suggested query backend, and additional backend-specific options. Both parameters are optional.
	 * 
     * @param queryBackendFactory overrides the query evaluator algorithm; passing null retains the default algorithm associated with the query
     * @param backendHints each entry in the map overrides backend-specific options regarding query evaluation (null-valued map entries permitted to erase hints); passing null means default options associated with the query
	 */
	public QueryEvaluationHint(
			IQueryBackendFactory queryBackendFactory, 
			Map<String, Object> backendHints) {
		super();
		this.queryBackendFactory = queryBackendFactory;
		this.backendHints = backendHints;
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
	 */
	public Map<String, Object> getBackendHints() {
		return backendHints;
	}

	/**
	 * Override values in this hint and return a consolidated instance.
	 * 
	 * @param overridingHint
	 * @return
	 * @since 1.4
	 */
	public QueryEvaluationHint overrideBy(QueryEvaluationHint overridingHint){
	    if (overridingHint == null)
            return this;
        
        IQueryBackendFactory queryBackendFactory = 
                this.getQueryBackendFactory();
        if (overridingHint.getQueryBackendFactory() != null)
            queryBackendFactory = overridingHint.getQueryBackendFactory();
        
        Map<String, Object> backendHints = 
                new HashMap<String, Object>(this.getBackendHints());
        if (overridingHint.getBackendHints() != null)
            backendHints.putAll(overridingHint.getBackendHints());
        
        return new QueryEvaluationHint(queryBackendFactory, backendHints);
	}
	
	/**
	 * Extract the requested capabilities
     * @since 1.4
     */
	public IMatcherCapability calculateRequiredCapability(PQuery query){
	    return queryBackendFactory.calculateRequiredCapability(query, this);
	}
}
