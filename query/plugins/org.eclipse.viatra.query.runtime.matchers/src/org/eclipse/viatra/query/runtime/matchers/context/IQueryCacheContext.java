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
package org.eclipse.viatra.query.runtime.matchers.context;

import org.eclipse.viatra.query.runtime.matchers.ViatraQueryRuntimeException;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackend;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

/**
 * Provides information on already cached queries to query evaluator backends at runtime.
 * 
 * @author Bergmann Gabor
 *
 */
public interface IQueryCacheContext {
    
    /**
     * Checks if there already is a caching result provider for the given query.
     * <p> Returns false if called while the caching result provider of the given query is being constructed in the first place. 
     */
    public boolean isResultCached(PQuery query);
    
    /**
     * Returns a caching result provider for the given query; it must be constructed if it does not exist yet.
     * <p> <b>Caution:</b> behavior undefined if called while the caching result provider of the given query is being constructed. 
     *   Beware of infinite loops. 
     * <p> <b>Postcondition:</b> {@link IQueryBackend#isCaching()} returns true for the {@link #getQueryBackend()} of the returned provider
     * 
     * @throws ViatraQueryRuntimeException
     */
    public IQueryResultProvider getCachingResultProvider(PQuery query);
}
