/*******************************************************************************
 * Copyright (c) 2010-2015, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.registry;

import java.util.Collection;

import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchEMFBackendFactory;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendFactory;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory;

import com.google.common.collect.Lists;

/**
 * A registry component for storing all known query backend factories. Can be used for backend selection in tooling.
 * 
 * @author Zoltan Ujhelyi
 * @since 1.2
 *
 */
public class QueryBackendRegistry {

    private static final QueryBackendRegistry INSTANCE = new QueryBackendRegistry(); 
    
    /**
     * Default backend implementation.
     */
    private static final IQueryBackendFactory DEFAULT_BACKEND = ReteBackendFactory.INSTANCE;
    private static final IQueryBackendFactory DEFAULT_CACHING_BACKEND = DEFAULT_BACKEND;
    private static final IQueryBackendFactory LOCAL_SEARCH_BACKEND = LocalSearchEMFBackendFactory.INSTANCE;
    
    private static Collection<IQueryBackendFactory> queryBackendFactories = Lists.newArrayList(DEFAULT_BACKEND, LOCAL_SEARCH_BACKEND);

    /**
     * Default caching backend implementation (in case the regular default is non-caching).
     */
    private QueryBackendRegistry () {
    }

    /**
     * Returns the singleton instanceof of the backend registry
     */
    public static QueryBackendRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * @return the default backend
     */
    public IQueryBackendFactory getDefaultBackend() {
        return DEFAULT_BACKEND;
    }
    /**
     * @return the default caching backend, if a caching backend is explicitly requested
     */
    public IQueryBackendFactory getDefaultCachingBackendClass() {
        return DEFAULT_CACHING_BACKEND;
    }
    
    /**
     * Returns all known factory instances in an iterable object
     */
    public Iterable<IQueryBackendFactory> getAllKnownFactories() {
        return queryBackendFactories;
    }
    
    /**
     * 
     * @param backend
     * @return a user-readable name for the given {@link IQueryBackend} implementation.
     * @since 2.0
     */
    public String getQueryBackendName(IQueryBackendFactory backend){
        return backend.getBackendClass().getSimpleName();
    }
}
