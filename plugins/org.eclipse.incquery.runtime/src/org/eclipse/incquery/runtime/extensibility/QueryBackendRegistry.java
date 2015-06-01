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
package org.eclipse.incquery.runtime.extensibility;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.incquery.runtime.matchers.backend.IQueryBackend;
import org.eclipse.incquery.runtime.matchers.backend.IQueryBackendFactory;
import org.eclipse.incquery.runtime.rete.matcher.ReteBackendFactory;
import org.eclipse.incquery.runtime.rete.matcher.ReteEngine;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * A registry component for storing all known query backend factories.
 * 
 * @author Zoltan Ujhelyi
 * @since 0.9
 *
 */
public class QueryBackendRegistry {

    private static final QueryBackendRegistry INSTANCE = new QueryBackendRegistry(); 
    
    private Map<Class<? extends IQueryBackend>, IQueryBackendFactory> queryBackendFactories = Maps.newHashMap();
    /**
     * Default backend implementation.
     */
    private Class<? extends IQueryBackend> defaultBackendClass = ReteEngine.class;
    /**
     * Default caching backend implementation (in case the regular deafult is non-caching).
     */
    private Class<? extends IQueryBackend> defaultCachingBackendClass = ReteEngine.class;
    

	private QueryBackendRegistry () {
        queryBackendFactories.put(ReteEngine.class, new ReteBackendFactory());        
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
    public Class<? extends IQueryBackend> getDefaultBackendClass() {
        return defaultBackendClass;
    }
    /**
     * @return the default caching backend, if a caching backend is explicitly requested
     */
    public Class<? extends IQueryBackend> getDefaultCachingBackendClass() {
    	return defaultCachingBackendClass;
    }
 
    /**
     * Registers a factory instance for a backend class.
     * @throws IllegalStateException if a factory is already registered for the selected backend
     */
    public void registerQueryBackendFactory(Class<? extends IQueryBackend> backend, IQueryBackendFactory factory) {
        Preconditions.checkArgument(!queryBackendFactories.containsKey(backend), "Backend %s already registered", backend.getName());
        queryBackendFactories.put(backend, factory);
    }
    
    /**
     * Registers a factory instance for a backend class if no class is already registered
     * 
     * @throws IllegalStateException
     *             if a factory is already registered for the selected backend
     * @since 1.0
     */
    public void registerQueryBackendFactoryUnchecked(Class<? extends IQueryBackend> backend,
            IQueryBackendFactory factory) {
        if (!queryBackendFactories.containsKey(backend)) {
            queryBackendFactories.put(backend, factory);
        }
    }

    /**
     * Returns a factory for the selected backend class. If no factories are available for the selected backend, an
     * exception is thrown.
     * 
     * @param backend
     * @throws IllegalStateException
     *             if no corresponding backend is registered into the factory
     */
    public IQueryBackendFactory getFactory(Class<? extends IQueryBackend> backend) {
        Preconditions.checkArgument(queryBackendFactories.containsKey(backend), "Unknown backend %s", backend.getName());
        return queryBackendFactories.get(backend);
    }
    
    /**
     * Returns all known factory instances in an iterable object
     */
    public Iterable<Entry<Class<? extends IQueryBackend>,IQueryBackendFactory>> getAllKnownFactories() {
        return queryBackendFactories.entrySet();
    }
    
}
