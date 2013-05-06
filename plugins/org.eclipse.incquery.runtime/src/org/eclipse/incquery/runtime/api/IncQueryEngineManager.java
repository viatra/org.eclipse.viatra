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

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.internal.BaseIndexListener;
import org.eclipse.incquery.runtime.internal.apiimpl.IncQueryEngineImpl;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;

import com.google.common.collect.Sets;

/**
 * Global registry of active EMF-IncQuery engines.
 * 
 * <p>
 * Manages an {@link IncQueryEngine} for each EMF model, that is created on demand. Managed engines are shared between
 * clients querying the same EMF model.
 * 
 * <p>
 * It is also possible to create private, unmanaged engines that are not shared between clients.
 * 
 * <p>
 * Only weak references are retained on the managed engines. So if there are no other references to the matchers or the
 * engine, they can eventually be GC'ed, and they won't block the EMF model from being GC'ed either.
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
    Map<Notifier, WeakReference<IncQueryEngineImpl>> engines;

    IncQueryEngineManager() {
        super();
        engines = new WeakHashMap<Notifier, WeakReference<IncQueryEngineImpl>>();
        initializationListeners = new HashSet<IncQueryEngineInitializationListener>();
    }

    /**
     * Creates a managed EMF-IncQuery engine at an EMF model root (recommended: Resource or ResourceSet) or retrieves an
     * already existing one. Repeated invocations for a single model root will return the same engine. Consequently, the
     * engine will be reused between different clients querying the same model, providing performance benefits.
     * 
     * <p>
     * The scope of pattern matching will be the given EMF model root and below (see FAQ for more precise definition).
     * The match set of any patterns will be incrementally refreshed upon updates from this scope.
     * 
     * @param emfRoot
     *            the root of the EMF containment hierarchy where this engine should operate. Recommended: Resource or
     *            ResourceSet.
     * @return a new or previously existing engine
     * @throws IncQueryException
     */
    public IncQueryEngine getIncQueryEngine(Notifier emfRoot) throws IncQueryException {
    	IncQueryEngineImpl engine = getEngineInternal(emfRoot);
        if (engine == null) {
            engine = new IncQueryEngineImpl(this, emfRoot, false);
            engines.put(emfRoot, new WeakReference<IncQueryEngineImpl>(engine));
            notifyInitializationListeners(engine);
        }
        return engine;
    }

    /**
     * Retrieves an already existing managed EMF-IncQuery engine.
     * 
     * @param emfRoot
     *            the root of the EMF containment hierarchy where this engine operates.
     * @return a previously existing engine, or null if no engine is active for the given EMF model root
     */
    public IncQueryEngine getIncQueryEngineIfExists(Notifier emfRoot) {
        return getEngineInternal(emfRoot);
    }

    /**
     * Collects all {@link IncQueryEngine} instances that still exist.
     * 
     * @return set of engines if there is any, otherwise EMTPY_SET
     */
    public Set<IncQueryEngine> getExistingIncQueryEngines(){
        Set<IncQueryEngine> existingEngines = null;
        for (WeakReference<IncQueryEngineImpl> engineRef : engines.values()) {
        	IncQueryEngineImpl engine = engineRef == null ? null : engineRef.get();
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
    
    /**
     * Creates a new advanced and unmanaged EMF-IncQuery engine at an EMF model root (recommended: Resource or ResourceSet). Repeated
     * invocations will return different instances, so other clients are unable to independently access and influence
     * the returned engine. Note that unmanaged engines do not benefit from some performance improvements that stem from
     * sharing incrementally maintained indices and caches.
     * 
     * <p> 
     * Client is responsible for the lifecycle of the returned engine, hence the usage of the advanced interface 
     * {@link AdvancedIncQueryEngine}.
     * 
     * <p>
     * The scope of pattern matching will be the given EMF model root and below (see FAQ for more precise definition).
     * The match set of any patterns will be incrementally refreshed upon updates from this scope.
     * 
     * @param emfRoot
     *            the root of the EMF containment hierarchy where this engine should operate. Recommended: Resource or
     *            ResourceSet.
     * @return a new existing engine
     * @throws IncQueryException
     */
    public AdvancedIncQueryEngine createAdvancedIncQueryEngine(Notifier emfRoot) throws IncQueryException {
        return new IncQueryEngineImpl(null, emfRoot, true);
    }

    /**
     * Disconnects the managed engine that was previously attached at the given EMF model root. Matcher objects will
     * continue to return stale results. Subsequent invocations of {@link #getIncQueryEngine(Notifier)} with the same
     * EMF root will return a new managed engine.
     * 
     * <p>
     * The engine will not impose on the model its update overhead anymore. If no references are retained to the
     * matchers or the engine, GC'ing the engine and its caches is presumably made easier, although (due to weak
     * references) a dispose() call is not strictly necessary.
     * <p>
     * If the engine is managed (see {@link IncQueryEngine#isManaged()}), there may be other clients using it. Such engines will not be disposed.
     * 
     * @return true is an engine was found and disconnected, false if no engine was found for the given root.
     */
    public boolean disposeEngine(Notifier emfRoot) {
    	IncQueryEngineImpl engine = getEngineInternal(emfRoot);
        if (engine == null || engine.isManaged())
            return false;
        else {
            engine.dispose();
            return true;
        }
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
        	IncQueryLoggingUtil.getDefaultLogger().fatal(
                    "EMF-IncQuery Engine Manager encountered an error in delivering notifications"
                            + " about engine initialization. ", ex);
        }
    }
    
    /**
     * @param emfRoot
     */
    void killInternal(Notifier emfRoot) {
        engines.remove(emfRoot);
    }

    /**
     * @param emfRoot
     * @return
     */
    private IncQueryEngineImpl getEngineInternal(Notifier emfRoot) {
        final WeakReference<IncQueryEngineImpl> engineRef = engines.get(emfRoot);
        IncQueryEngineImpl engine = engineRef == null ? null : engineRef.get();
        return engine;
    }

}
