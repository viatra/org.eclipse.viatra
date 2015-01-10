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
package org.eclipse.incquery.runtime.matchers.backend;

import java.util.Map;

/**
 * Provides IncQuery with additional hints on how a query should be evaluated. The same hint can be provided to multiple queries. 
 * 
 * <p>
 * Here be dragons: for advanced users only.
 * 
 * 
 * @author Bergmann Gabor
 *
 */
public class QueryEvaluationHint {
	
	Class<? extends IQueryBackend> queryBackendClass; 
	Map<String, Object> backendHints;
	
	/**
	 * Specifies the suggested query backend, and additional backend-specific options. Both parameters are optional.
	 * 
     * @param queryBackendClass overrides the query evaluator algorithm (whose factory must be registered); passing null retains the default algorithm associated with the query
     * @param backendHints each entry in the map overrides backend-specific options regarding query evaluation (null-valued map entries permitted to erase hints); passing null means default options associated with the query
	 */
	public QueryEvaluationHint(
			Class<? extends IQueryBackend> queryBackendClass,
			Map<String, Object> backendHints) {
		super();
		this.queryBackendClass = queryBackendClass;
		this.backendHints = backendHints;
	}

	/**
	 * A suggestion for the query evaluator algorithm (whose factory must be registered). 
	 * 
	 * <p> Can be null.
	 */
	public Class<? extends IQueryBackend> getQueryBackendClass() {
		return queryBackendClass;
	}
	
	/**
	 * Each entry in the map overrides backend-specific options regarding query evaluation. 
	 * 
	 * <p>Can be null. Null-valued map entries permitted to erase hints. 
	 */
	public Map<String, Object> getBackendHints() {
		return backendHints;
	}
	
	
	


}
