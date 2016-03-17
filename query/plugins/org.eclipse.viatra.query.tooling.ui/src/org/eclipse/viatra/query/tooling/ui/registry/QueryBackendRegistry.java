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

import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchBackendFactory;
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
    private final IQueryBackendFactory defaultBackendFactory = new ReteBackendFactory();
    private final IQueryBackendFactory defaultCachingBackendFactory= defaultBackendFactory;
    private final IQueryBackendFactory localSearchBackendFactory = LocalSearchBackendFactory.INSTANCE;
    
    private Collection<IQueryBackendFactory> queryBackendFactories = Lists.newArrayList(defaultBackendFactory, localSearchBackendFactory);

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
        return defaultBackendFactory;
    }
    /**
     * @return the default caching backend, if a caching backend is explicitly requested
     */
    public IQueryBackendFactory getDefaultCachingBackendClass() {
    	return defaultCachingBackendFactory;
    }
    
    /**
     * Returns all known factory instances in an iterable object
     */
    public Iterable<IQueryBackendFactory> getAllKnownFactories() {
        return queryBackendFactories;
    }
    
}
