/*******************************************************************************
 * Copyright (c) 2010-2013, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.api;

import org.apache.log4j.Appender;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.internal.apiimpl.IncQueryEngineImpl;
import org.eclipse.incquery.runtime.rete.matcher.ReteEngine;


/**
 * Advanced interface to an EMF-IncQuery incremental evaluation engine.
 * 
 * <p>You can create a new, private, unmanaged {@link AdvancedIncQueryEngine} instance using {@link #createUnmanagedEngine(Notifier)}. 
 * Additionally, you can access the advanced interface on any {@link IncQueryEngine} by {@link AdvancedIncQueryEngine#from(IncQueryEngine)}.
 * 
 * <p>While the default interface {@link IncQueryEngine}, is suitable for most users, this advanced interface 
 *   provides more control over the engine. The most important added functionality is the following: <ul>
 * <li> You can have tighter control over the lifecycle of the engine, if you create a private, unmanaged engine instance. 
 *   For instance, a (non-managed) engine can be disposed in order to detach from the EMF model and stop listening on update notifications. 
 *   The indexes built previously in the engine can then be garbage collected, even if the model itself is retained.  
 *   Total lifecycle control is only available for private, unmanaged engines (created using {@link #createUnmanagedEngine(Notifier)}); 
 *   a managed engine (obtained via {@link IncQueryEngine#on(Notifier)}) is shared among clients and can not be disposed or wiped.
 * <li> You can add and remove listeners to receive notification when the model or the match sets change.
 * <li> You can add and remove listeners to receive notification on engine lifecycle events, such as creation of new matchers. 
 *   For instance, if you explicitly share a private, unmanaged engine between multiple sites, you should register a callback using 
 *   {@link #addLifecycleListener(IncQueryEngineLifecycleListener)} to learn when another client has called the destructive methods 
 *   {@link #dispose()} or {@link #wipe()}.
 * </ul>
 * 
 * @author Bergmann Gabor
 *
 */
public abstract class AdvancedIncQueryEngine extends IncQueryEngine {
	
    /**
     * Creates a new unmanaged EMF-IncQuery engine at an EMF model root (recommended: Resource or ResourceSet). Repeated
     * invocations will return different instances, so other clients are unable to independently access and influence
     * the returned engine. Note that unmanaged engines do not benefit from some performance improvements that stem from
     * sharing incrementally maintained indices and caches between multiple clients using the same managed engine instance.
     * 
     * <p> 
     * Client is responsible for the lifecycle of the returned engine, hence the usage of the advanced interface 
     * {@link AdvancedIncQueryEngine}.
     * 
     * <p>
     * The scope of pattern matching will be the given EMF model root and below (see FAQ for more precise definition).
     * The match set of any patterns will be incrementally refreshed upon updates from this scope.
     * 
     * @param emfScopeRoot
     *            the root of the EMF containment hierarchy where this engine should operate. Recommended: Resource or
     *            ResourceSet.
     * @return the advanced interface to a newly created unmanaged engine
     * @throws IncQueryException
     * 
     * @see {@link #createUnmanagedEngine(Notifier, boolean, boolean)} for performance tuning and dynamic EMF options.
     */
	public static AdvancedIncQueryEngine createUnmanagedEngine(Notifier emfScopeRoot) throws IncQueryException {
        return createUnmanagedEngine(emfScopeRoot, IncQueryEngine.WILDCARD_MODE_DEFAULT);
	}
	
    /**
     * Creates a new unmanaged EMF-IncQuery engine at an EMF model root (recommended: Resource or ResourceSet). Repeated
     * invocations will return different instances, so other clients are unable to independently access and influence
     * the returned engine. Note that unmanaged engines do not benefit from some performance improvements that stem from
     * sharing incrementally maintained indices and caches between multiple clients using the same managed engine instance.
     * 
     * <p> 
     * Client is responsible for the lifecycle of the returned engine, hence the usage of the advanced interface 
     * {@link AdvancedIncQueryEngine}.
     * 
     * <p>
     * The scope of pattern matching will be the given EMF model root and below (see FAQ for more precise definition).
     * The match set of any patterns will be incrementally refreshed upon updates from this scope.
     * 
     * @param emfScopeRoot
     *            the root of the EMF containment hierarchy where this engine should operate. Recommended: Resource or
     *            ResourceSet.
     * @param wildcardMode
     *            specifies whether the base index should be built in wildcard mode. See {@link NavigationHelper} for the
     *            explanation of wildcard mode. Defaults to false.				
     * @return the advanced interface to a newly created unmanaged engine
     * @throws IncQueryException
     */
	public static AdvancedIncQueryEngine createUnmanagedEngine(Notifier emfScopeRoot, boolean wildcardMode) throws IncQueryException {
        return createUnmanagedEngine(emfScopeRoot, wildcardMode, IncQueryEngine.DYNAMIC_EMF_MODE_DEFAULT);
	}	
	
    /**
     * Creates a new unmanaged EMF-IncQuery engine at an EMF model root (recommended: Resource or ResourceSet). Repeated
     * invocations will return different instances, so other clients are unable to independently access and influence
     * the returned engine. Note that unmanaged engines do not benefit from some performance improvements that stem from
     * sharing incrementally maintained indices and caches between multiple clients using the same managed engine instance.
     * 
     * <p> 
     * Client is responsible for the lifecycle of the returned engine, hence the usage of the advanced interface 
     * {@link AdvancedIncQueryEngine}.
     * 
     * <p>
     * The scope of pattern matching will be the given EMF model root and below (see FAQ for more precise definition).
     * The match set of any patterns will be incrementally refreshed upon updates from this scope.
     * 
     * @param emfScopeRoot
     *            the root of the EMF containment hierarchy where this engine should operate. Recommended: Resource or
     *            ResourceSet.
     * @param wildcardMode
     *            specifies whether the base index should be built in wildcard mode. See {@link NavigationHelper} for the
     *            explanation of wildcard mode. Defaults to false.				
     * @param dynamicEMFMode
     *            specifies whether the base index should be built in dynamic EMF mode. See {@link NavigationHelper} for the
     *            explanation of dynamic EMF mode. Defaults to false.				
     * @return the advanced interface to a newly created unmanaged engine
     * @throws IncQueryException
     */
	public static AdvancedIncQueryEngine createUnmanagedEngine(Notifier emfScopeRoot, boolean wildcardMode, boolean dynamicEMFMode) throws IncQueryException {
        return new IncQueryEngineImpl(null, emfScopeRoot, wildcardMode, dynamicEMFMode);
	}	
	
	/**
	 * Provides access to a given existing engine through the advanced interface.
	 * 
	 * <p> Caveat: if the referenced engine is managed (i.e. created via {@link IncQueryEngine#on(Notifier)}), 
	 * the advanced methods {@link #dispose()} and {@link #wipe()} will not be allowed.
	 * 
	 * @param engine the engine to access using the advanced interface
	 * @return a reference to the same engine conforming to the advanced interface
	 */
	public static AdvancedIncQueryEngine from(IncQueryEngine engine) {
		return (AdvancedIncQueryEngine) engine;
	}

    /**
     * Add an engine lifecycle listener to this engine instance.
     * @param listener the {@link IncQueryEngineLifecycleListener} that should listen to lifecycle events from this engine
     */
	public abstract void addLifecycleListener(IncQueryEngineLifecycleListener listener);
    /**
     * Remove an existing lifecycle listener from this engine instance.
     * @param listener the {@link IncQueryEngineLifecycleListener} that should not listen to lifecycle events from this engine anymore
     */
	public abstract void removeLifecycleListener(IncQueryEngineLifecycleListener listener);

    /**
     * Add an model update event listener to this engine instance (that fires its callbacks according to its notification level).
     * @param listener the {@link IncQueryModelUpdateListener} that should listen to model update events from this engine.
     */
	public abstract void addModelUpdateListener(IncQueryModelUpdateListener listener);
    /**
     * Remove an existing model update event listener to this engine instance.
     * @param listener the {@link IncQueryModelUpdateListener} that should not listen to model update events from this engine anymore
     */
	public abstract void removeModelUpdateListener(IncQueryModelUpdateListener listener);
	
	/**
     * Registers low-level callbacks for match appearance and disappearance on this pattern matcher.
     * 
     * <p>
     * <b>Caution: </b>
     * This is a low-level callback that is invoked when the pattern matcher is not necessarily in a consistent state
     * yet. Importantly, no model modification permitted during the callback. 
     * Most users should use the databinding support (org.eclipse.incquery.databinding.runtime.api.IncQueryObservables) 
     * or the event-driven API (org.eclipse.incquery.runtime.evm.api.EventDrivenVM) instead. 
     * 
     * <p>
     * Performance note: expected to be much more efficient than polling at {@link #addCallbackAfterUpdates(Runnable)},
     * but prone to "signal hazards", e.g. spurious match appearances that will disappear immediately afterwards.
     * 
     * <p>
     * The callback can be unregistered via {@link #removeCallbackOnMatchUpdate(IMatchUpdateListener)}.
     * 
     * @param fireNow
     *            if true, appearCallback will be immediately invoked on all current matches as a one-time effect. See
     *            also {@link IncQueryMatcher#forEachMatch(IMatchProcessor)}.
     * @param listener
     *            the listener that will be notified of each new match that appears or disappears, starting from now.
     * @param matcher 
     *            the {@link IncQueryMatcher} for which this listener should be active
     */
	public abstract <Match extends IPatternMatch> void addMatchUpdateListener(IncQueryMatcher<Match> matcher, IMatchUpdateListener<? super Match> listener,
			boolean fireNow);
    /**
     * Remove an existing match update event listener to this engine instance.
     * @param matcher the {@link IncQueryMatcher} for which this listener should not be active anymore
     * @param listener the {@link IMatchUpdateListener} that should not receive the callbacks anymore
     */
	public abstract <Match extends IPatternMatch> void removeMatchUpdateListener(IncQueryMatcher<Match> matcher, IMatchUpdateListener<? super Match> listener);

    /**
     * Indicates whether the engine is managed, i.e. the default engine assigned to the given scope root 
     * 	by {@link IncQueryEngine#on(Notifier)}.
     * 
     * <p>
     * If the engine is managed, there may be other clients using it, as all calls to {@link IncQueryEngine#on(Notifier)} 
     *   return the same managed engine instance for a given scope root. 
     * Therefore the destructive methods {@link #wipe()} and {@link #dispose()} are not allowed. 
     * 
     * <p>On the other hand, if the engine is unmanaged (i.e. a private instance created using {@link #createUnmanagedEngine(Notifier)}), 
     * then {@link #wipe()} and {@link #dispose()} can be called.
     * If you explicitly share a private, unmanaged engine between multiple sites, register a callback using 
     * {@link #addLifecycleListener(IncQueryEngineLifecycleListener)} to learn when another client has called these destructive methods.
     * 
     * @return true if the engine is managed, and therefore potentially shared with other clients querying the same EMF
     *         model
     */
	public abstract boolean isManaged();

    /**
     * Indicates whether the engine is in a tainted, inconsistent state due to some internal errors. If true, results
     * are no longer reliable; engine should be disposed.
     * 
     * <p>
     * The engine is defined to be in a tainted state if any of its internal processes has logged a
     * <strong>fatal</strong> error to the engine's logger. The cause of the error can therefore be determined by
     * checking the contents of the log. This is possible e.g. through a custom {@link Appender} that was attached to
     * the engine's logger.
     * 
     * @return the tainted state
     */
	public abstract boolean isTainted();

    /**
     * Discards any pattern matcher caches and forgets known patterns. The base index built directly on the underlying
     * EMF model, however, is kept in memory to allow reuse when new pattern matchers are built. Use this method if you
     * have e.g. new versions of the same patterns, to be matched on the same model.
     * 
     * <p>
     * Matcher objects will continue to return stale results. If no references are retained to the matchers, they can
     * eventually be GC'ed.
     * <p>
     * Disallowed if the engine is managed (see {@link #isManaged()}), as there may be other clients using it.
     * <p>
     * If you explicitly share a private, unmanaged engine between multiple sites, register a callback using 
     * {@link #addLifecycleListener(IncQueryEngineLifecycleListener)} to learn when another client has called this destructive method.
     * 
     * @throws UnsupportedOperationException if engine is managed
     */
	public abstract void wipe();

    /**
     * Completely disconnects and dismantles the engine. Cannot be reversed.
     * <p>
     * Matcher objects will continue to return stale results. If no references are retained to the matchers or the
     * engine, they can eventually be GC'ed, and they won't block the EMF model from being GC'ed anymore.
     * <p>
     * The base indexer (see {@link #getBaseIndex()}) built on the model will be disposed alongside the engine, unless the user has 
     *  manually added listeners on the base index that were not removed yet.
     * <p>
     * Disallowed if the engine is managed (see {@link #isManaged()}), as there may be other clients using it.
     * <p>
     * If you explicitly share a private, unmanaged engine between multiple sites, register a callback using 
     * {@link #addLifecycleListener(IncQueryEngineLifecycleListener)} to learn when another client has called this destructive method.
     * 
     * @throws UnsupportedOperationException if engine is managed
     */
	public abstract void dispose();

	/**
	 * Access the internal Rete pattern matching network (for advanced debugging purposes only).
	 * @noreference for internal use only
	 */
	public abstract ReteEngine getReteEngine() throws IncQueryException;
	
}
