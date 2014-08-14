/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.api;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.incquery.runtime.api.scope.IncQueryScope;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.internal.BaseIndexListener;
import org.eclipse.incquery.runtime.internal.apiimpl.IncQueryEngineImpl;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;

import com.google.common.collect.Sets;

/**
 * Global registry of active IncQuery engines.
 * 
 * <p>
 * Manages an {@link IncQueryEngine} for each model (more precisely scope), that is created on demand. Managed engines are shared between
 * clients querying the same model.
 * 
 * <p>
 * It is also possible to create private, unmanaged engines that are not shared between clients.
 * 
 * <p>
 * Only weak references are retained on the managed engines. So if there are no other references to the matchers or the
 * engine, they can eventually be GC'ed, and they won't block the model from being GC'ed either.
 * 
 * 
 * @author Bergmann Gabor
 * 
 */
public class IncQueryEngineManager {
    private static IncQueryEngineManager instance = new IncQueryEngineManager();
    

    /**
     * @return the singleton instance
     */
    public static IncQueryEngineManager getInstance() {
        return instance;
    }

    /**
     * Only a weak reference is kept on engine, so that it can be GC'ed if the model becomes unreachable.
     * 
     * <p>
     * it will not be GC'ed before because of {@link BaseIndexListener#iqEngine}
     */
    Map<IncQueryScope, WeakReference<IncQueryEngineImpl>> engines;

    IncQueryEngineManager() {
        super();
        engines = new WeakHashMap<IncQueryScope, WeakReference<IncQueryEngineImpl>>();
        initializationListeners = new HashSet<IncQueryEngineInitializationListener>();
    }

    /**
     * Creates a managed IncQuery engine at a given scope (e.g. an EMF Resource or ResourceSet, as in {@link EMFScope}) 
     * or retrieves an already existing one. Repeated invocations for a single model root will return the same engine. 
     * Consequently, the engine will be reused between different clients querying the same model, providing performance benefits.
     * 
     * <p>
     * The match set of any patterns will be incrementally refreshed upon updates from this scope.
     * 
     * @param scope 
     * 		the scope of query evaluation; the definition of the set of model elements that this engine is operates on. 
     * 		Provide e.g. a {@link EMFScope} for evaluating queries on an EMF model.
     * @return a new or previously existing engine
     * @throws IncQueryException
     */
    public IncQueryEngine getIncQueryEngine(IncQueryScope scope) throws IncQueryException {
    	IncQueryEngineImpl engine = getEngineInternal(scope);
        if (engine == null) {
            engine = new IncQueryEngineImpl(this, scope);
            engines.put(scope, new WeakReference<IncQueryEngineImpl>(engine));
            notifyInitializationListeners(engine);
        }
        return engine;
    }

    /**
     * Retrieves an already existing managed IncQuery engine.
     * 
     * @param scope 
     * 		the scope of query evaluation; the definition of the set of model elements that this engine is operates on. 
     * 		Provide e.g. a {@link EMFScope} for evaluating queries on an EMF model.
     * @return a previously existing engine, or null if no engine is active for the given EMF model root
     */
    public IncQueryEngine getIncQueryEngineIfExists(IncQueryScope scope) {
        return getEngineInternal(scope);
    }

    /**
     * Collects all {@link IncQueryEngine} instances that still exist.
     * 
     * @return set of engines if there is any, otherwise EMTPY_SET
     */
    public Set<IncQueryEngine> getExistingIncQueryEngines(){
        Set<IncQueryEngine> existingEngines = null;
        for (WeakReference<IncQueryEngineImpl> engineRef : engines.values()) {
        	AdvancedIncQueryEngine engine = engineRef == null ? null : engineRef.get();
            if(existingEngines == null) {
                existingEngines = Sets.newHashSet();
            }
            existingEngines.add(engine);
        }
        if(existingEngines == null) {
            existingEngines = Collections.emptySet();
        }
        return existingEngines;
    }
        
    private final Set<IncQueryEngineInitializationListener> initializationListeners;
    
    /**
     * Registers a listener for new engine initialization.
     * 
     * <p/> For removal, use {@link #removeIncQueryEngineInitializationListener}
     * 
     * @param listener the listener to register
     */
    public void addIncQueryEngineInitializationListener(IncQueryEngineInitializationListener listener) {
        checkArgument(listener != null, "Cannot add null listener!");
        initializationListeners.add(listener);
    }

    /**
     * Removes a registered listener added with {@link #addIncQueryEngineInitializationListener}
     * 
     * @param listener
     */
    public void removeIncQueryEngineInitializationListener(IncQueryEngineInitializationListener listener) {
        checkArgument(listener != null, "Cannot remove null listener!");
        initializationListeners.remove(listener);
    }

    /**
     * Notifies listeners that a new engine has been initialized.
     * 
     * @param engine the initialized engine
     */
    protected void notifyInitializationListeners(AdvancedIncQueryEngine engine) {
        try {
            if (!initializationListeners.isEmpty()) {
                for (IncQueryEngineInitializationListener listener : Sets.newHashSet(initializationListeners)) {
                    listener.engineInitialized(engine);
                }
            }
        } catch (Exception ex) {
        	IncQueryLoggingUtil.getLogger(getClass()).fatal(
                    "IncQuery Engine Manager encountered an error in delivering notifications"
                            + " about engine initialization. ", ex);
        }
    }

    /**
     * @param emfRoot
     * @return
     */
    private IncQueryEngineImpl getEngineInternal(IncQueryScope scope) {
        final WeakReference<IncQueryEngineImpl> engineRef = engines.get(scope);
        IncQueryEngineImpl engine = engineRef == null ? null : engineRef.get();
        return engine;
    }

}
