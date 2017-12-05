/*******************************************************************************
 * Copyright (c) 2010-2014, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.backend;

import org.eclipse.viatra.query.runtime.matchers.ViatraQueryRuntimeException;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

/**
 * Internal interface for a VIATRA query specification. Each query is associated with a pattern. Methods instantiate a matcher
 * of the pattern with various parameters.
 *
 * @author Bergmann Gábor
 * @since 0.9
 */
public interface IQueryBackend {
	
	/**
	 * @return true iff this backend is incremental, i.e. it caches the results of queries for quick retrieval, 
	 * and can provide update notifications on result set changes.
	 */
	public boolean isCaching();
	
    /**
     * Returns a result provider for a given query. Repeated calls may return the same instance.
     * @throws ViatraQueryRuntimeException
     */
	public IQueryResultProvider getResultProvider(PQuery query);
	
	/**
     * Returns a result provider for a given query. Repeated calls may return the same instance.
     * @param optional hints that may override engine and query defaults (as provided by {@link IQueryBackendHintProvider}). Can be null.
     * @throws ViatraQueryRuntimeException 
	 * @since 1.4
     */
    public IQueryResultProvider getResultProvider(PQuery query, QueryEvaluationHint hints);
	
    /**
     * Returns an existing result provider for a given query, if it was previously constructed, returns null otherwise.
     * Will not construct and initialize new result providers.
     */
	public IQueryResultProvider peekExistingResultProvider(PQuery query);

	/**
	 * Propagates all pending updates in this query backend. The implementation of this method is optional, and it 
	 * can be ignored entirely if the backend does not delay updates.   
	 * @since 1.6
	 */
	public void flushUpdates();
	
	/**
	 * Disposes the query backend.
	 */
	public abstract void dispose();

}
