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
package org.eclipse.viatra.query.runtime.api;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.viatra.query.runtime.api.scope.QueryScope;
import org.eclipse.viatra.query.runtime.base.api.BaseIndexOptions;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.internal.apiimpl.ViatraQueryEngineImpl;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackend;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendFactory;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;

/**
 * Advanced interface to an VIATRA incremental evaluation engine.
 * 
 * <p>
 * You can create a new, private, unmanaged {@link AdvancedViatraQueryEngine} instance using
 * {@link #createUnmanagedEngine(QueryScope)}. Additionally, you can access the advanced interface on any
 * {@link ViatraQueryEngine} by {@link AdvancedViatraQueryEngine#from(ViatraQueryEngine)}.
 * 
 * <p>
 * While the default interface {@link ViatraQueryEngine}, is suitable for most users, this advanced interface provides more
 * control over the engine. The most important added functionality is the following:
 * <ul>
 * <li>You can have tighter control over the lifecycle of the engine, if you create a private, unmanaged engine
 * instance. For instance, a (non-managed) engine can be disposed in order to detach from the EMF model and stop
 * listening on update notifications. The indexes built previously in the engine can then be garbage collected, even if
 * the model itself is retained. Total lifecycle control is only available for private, unmanaged engines (created using
 * {@link #createUnmanagedEngine(QueryScope)}); a managed engine (obtained via {@link ViatraQueryEngine#on(QueryScope)}) is
 * shared among clients and can not be disposed or wiped.
 * <li>You can add and remove listeners to receive notification when the model or the match sets change.
 * <li>You can add and remove listeners to receive notification on engine lifecycle events, such as creation of new
 * matchers. For instance, if you explicitly share a private, unmanaged engine between multiple sites, you should
 * register a callback using {@link #addLifecycleListener(ViatraQueryEngineLifecycleListener)} to learn when another client
 * has called the destructive methods {@link #dispose()} or {@link #wipe()}.
 * </ul>
 * 
 * @author Bergmann Gabor
 * 
 */
public abstract class AdvancedViatraQueryEngine extends ViatraQueryEngine {

    /**
     * Creates a new unmanaged VIATRA Query engine at an EMF model root (recommended: Resource or ResourceSet). Repeated
     * invocations will return different instances, so other clients are unable to independently access and influence
     * the returned engine. Note that unmanaged engines do not benefit from some performance improvements that stem from
     * sharing incrementally maintained indices and caches between multiple clients using the same managed engine
     * instance.
     * 
     * <p>
     * Client is responsible for the lifecycle of the returned engine, hence the usage of the advanced interface
     * {@link AdvancedViatraQueryEngine}.
     * 
     * <p>
     * The scope of pattern matching will be the given EMF model root and below (see FAQ for more precise definition).
     * The match set of any patterns will be incrementally refreshed upon updates from this scope.
     * 
     * @param emfScopeRoot
     *            the root of the EMF containment hierarchy where this engine should operate. Recommended: Resource or
     *            ResourceSet.
     * @return the advanced interface to a newly created unmanaged engine
     * @throws ViatraQueryException
     * 
     * @see #createUnmanagedEngine(Notifier, BaseIndexOptions) for performance tuning and dynamic EMF options.
     * @deprecated use {@link #createUnmanagedEngine(QueryScope)} instead to evaluate queries on both EMF and non-EMF scopes.
     */
    @Deprecated
    public static AdvancedViatraQueryEngine createUnmanagedEngine(Notifier emfScopeRoot) throws ViatraQueryException {
        return createUnmanagedEngine(emfScopeRoot, new BaseIndexOptions());
    }

    /**
     * Creates a new unmanaged VIATRA Query engine at an EMF model root (recommended: Resource or ResourceSet). Repeated
     * invocations will return different instances, so other clients are unable to independently access and influence
     * the returned engine. Note that unmanaged engines do not benefit from some performance improvements that stem from
     * sharing incrementally maintained indices and caches between multiple clients using the same managed engine
     * instance.
     * 
     * <p>
     * Client is responsible for the lifecycle of the returned engine, hence the usage of the advanced interface
     * {@link AdvancedViatraQueryEngine}.
     * 
     * <p>
     * The scope of pattern matching will be the given EMF model root and below (see FAQ for more precise definition).
     * The match set of any patterns will be incrementally refreshed upon updates from this scope.
     * 
     * @param emfScopeRoot
     *            the root of the EMF containment hierarchy where this engine should operate. Recommended: Resource or
     *            ResourceSet.
     * @param wildcardMode
     *            specifies whether the base index should be built in wildcard mode. See {@link BaseIndexOptions} for
     *            the explanation of wildcard mode. Defaults to false.
     * @return the advanced interface to a newly created unmanaged engine
     * @throws ViatraQueryException
     * @deprecated use {@link #createUnmanagedEngine(QueryScope)} instead to evaluate queries on both EMF and non-EMF scopes.
     */
    @Deprecated
    public static AdvancedViatraQueryEngine createUnmanagedEngine(Notifier emfScopeRoot, boolean wildcardMode)
            throws ViatraQueryException {
        return createUnmanagedEngine(emfScopeRoot, new BaseIndexOptions().withWildcardMode(wildcardMode));
    }

    /**
     * Creates a new unmanaged VIATRA Query engine at an EMF model root (recommended: Resource or ResourceSet). Repeated
     * invocations will return different instances, so other clients are unable to independently access and influence
     * the returned engine. Note that unmanaged engines do not benefit from some performance improvements that stem from
     * sharing incrementally maintained indices and caches between multiple clients using the same managed engine
     * instance.
     * 
     * <p>
     * Client is responsible for the lifecycle of the returned engine, hence the usage of the advanced interface
     * {@link AdvancedViatraQueryEngine}.
     * 
     * <p>
     * The scope of pattern matching will be the given EMF model root and below (see FAQ for more precise definition).
     * The match set of any patterns will be incrementally refreshed upon updates from this scope.
     * 
     * @param emfScopeRoot
     *            the root of the EMF containment hierarchy where this engine should operate. Recommended: Resource or
     *            ResourceSet.
     * @param wildcardMode
     *            specifies whether the base index should be built in wildcard mode. See {@link BaseIndexOptions} for
     *            the explanation of wildcard mode. Defaults to false.
     * @param dynamicEMFMode
     *            specifies whether the base index should be built in dynamic EMF mode. See {@link BaseIndexOptions} for
     *            the explanation of dynamic EMF mode. Defaults to false.
     * @return the advanced interface to a newly created unmanaged engine
     * @throws ViatraQueryException
     * @deprecated use {@link #createUnmanagedEngine(QueryScope)} instead to evaluate queries on both EMF and non-EMF scopes.
     */
    @Deprecated
    public static AdvancedViatraQueryEngine createUnmanagedEngine(Notifier emfScopeRoot, boolean wildcardMode,
            boolean dynamicEMFMode) throws ViatraQueryException {
        return createUnmanagedEngine(emfScopeRoot, new BaseIndexOptions(dynamicEMFMode, wildcardMode));
    }

    /**
     * Creates a new unmanaged VIATRA Query engine at an EMF model root (recommended: Resource or ResourceSet). Repeated
     * invocations will return different instances, so other clients are unable to independently access and influence
     * the returned engine. Note that unmanaged engines do not benefit from some performance improvements that stem from
     * sharing incrementally maintained indices and caches between multiple clients using the same managed engine
     * instance.
     * 
     * <p>
     * Client is responsible for the lifecycle of the returned engine, hence the usage of the advanced interface
     * {@link AdvancedViatraQueryEngine}.
     * 
     * <p>
     * The scope of pattern matching will be the given EMF model root and below (see FAQ for more precise definition).
     * The match set of any patterns will be incrementally refreshed upon updates from this scope.
     * 
     * @param emfScopeRoot
     *            the root of the EMF containment hierarchy where this engine should operate. Recommended: Resource or
     *            ResourceSet.
     * @param options
     *            specifies how the base index is built, including wildcard mode (defaults to false) and dynamic EMF
     *            mode (defaults to false). See {@link BaseIndexOptions} for the explanation of wildcard mode and
     *            dynamic EMF mode.
     * @return the advanced interface to a newly created unmanaged engine
     * @deprecated use {@link #createUnmanagedEngine(QueryScope)} instead to evaluate queries on both EMF and non-EMF scopes.
     */
    @Deprecated
    public static AdvancedViatraQueryEngine createUnmanagedEngine(Notifier emfScopeRoot, BaseIndexOptions options)
            throws ViatraQueryException {
        return new ViatraQueryEngineImpl(null, new EMFScope(emfScopeRoot, options));
    }
    
    /**
     * Creates a new unmanaged VIATRA Query engine to evaluate queries over a given scope specified by an {@link QueryScope}.
     * 
     * <p> Repeated invocations will return different instances, so other clients are unable to independently access 
     * and influence the returned engine. Note that unmanaged engines do not benefit from some performance improvements 
     * that stem from sharing incrementally maintained indices and caches between multiple clients using the same managed 
     * engine instance.
     * 
     * <p>
     * Client is responsible for the lifecycle of the returned engine, hence the usage of the advanced interface
     * {@link AdvancedViatraQueryEngine}.
     * 
     * <p>
     * The match set of any patterns will be incrementally refreshed upon updates from this scope.
     * 
     * @param scope 
     * 		the scope of query evaluation; the definition of the set of model elements that this engine is operates on. 
     * 		Provide e.g. a {@link EMFScope} for evaluating queries on an EMF model.
     * @return the advanced interface to a newly created unmanaged engine
     * @since 0.9
     */
    public static AdvancedViatraQueryEngine createUnmanagedEngine(QueryScope scope)
            throws ViatraQueryException {
        return new ViatraQueryEngineImpl(null, scope);
    }

    /**
     * Provides access to a given existing engine through the advanced interface.
     * 
     * <p>
     * Caveat: if the referenced engine is managed (i.e. created via {@link ViatraQueryEngine#on(QueryScope)}), the advanced
     * methods {@link #dispose()} and {@link #wipe()} will not be allowed.
     * 
     * @param engine
     *            the engine to access using the advanced interface
     * @return a reference to the same engine conforming to the advanced interface
     */
    public static AdvancedViatraQueryEngine from(ViatraQueryEngine engine) {
        return (AdvancedViatraQueryEngine) engine;
    }

    /**
     * Add an engine lifecycle listener to this engine instance.
     * 
     * @param listener
     *            the {@link ViatraQueryEngineLifecycleListener} that should listen to lifecycle events from this engine
     */
    public abstract void addLifecycleListener(ViatraQueryEngineLifecycleListener listener);

    /**
     * Remove an existing lifecycle listener from this engine instance.
     * 
     * @param listener
     *            the {@link ViatraQueryEngineLifecycleListener} that should not listen to lifecycle events from this
     *            engine anymore
     */
    public abstract void removeLifecycleListener(ViatraQueryEngineLifecycleListener listener);

    /**
     * Add an model update event listener to this engine instance (that fires its callbacks according to its
     * notification level).
     * 
     * @param listener
     *            the {@link ViatraQueryModelUpdateListener} that should listen to model update events from this engine.
     */
    public abstract void addModelUpdateListener(ViatraQueryModelUpdateListener listener);

    /**
     * Remove an existing model update event listener to this engine instance.
     * 
     * @param listener
     *            the {@link ViatraQueryModelUpdateListener} that should not listen to model update events from this engine
     *            anymore
     */
    public abstract void removeModelUpdateListener(ViatraQueryModelUpdateListener listener);

    /**
     * Registers low-level callbacks for match appearance and disappearance on this pattern matcher.
     * 
     * <p>
     * <b>Caution: </b> This is a low-level callback that is invoked when the pattern matcher is not necessarily in a
     * consistent state yet. Importantly, no model modification permitted during the callback. Most users should use the
     * databinding support ({@link org.eclipse.viatra.addon.databinding.runtime.api.ViatraObservables ViatraObservables}) or the event-driven API
     * ({@link org.eclipse.viatra.transformation.evm.api.EventDrivenVM EventDrivenVM}) instead.
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
     *            also {@link ViatraQueryMatcher#forEachMatch(IMatchProcessor)}.
     * @param listener
     *            the listener that will be notified of each new match that appears or disappears, starting from now.
     * @param matcher
     *            the {@link ViatraQueryMatcher} for which this listener should be active
     */
    public abstract <Match extends IPatternMatch> void addMatchUpdateListener(ViatraQueryMatcher<Match> matcher,
            IMatchUpdateListener<? super Match> listener, boolean fireNow);

    /**
     * Remove an existing match update event listener to this engine instance.
     * 
     * @param matcher
     *            the {@link ViatraQueryMatcher} for which this listener should not be active anymore
     * @param listener
     *            the {@link IMatchUpdateListener} that should not receive the callbacks anymore
     */
    public abstract <Match extends IPatternMatch> void removeMatchUpdateListener(ViatraQueryMatcher<Match> matcher,
            IMatchUpdateListener<? super Match> listener);

    
	/**
	 * Access a pattern matcher based on a {@link IQuerySpecification}, overriding some of the default query evaluation hints. 
	 * Multiple calls will return the same matcher.
	 * <p> Hints are only effective the first time a matcher is created.
	 * @param querySpecification a {@link IQuerySpecification} that describes an VIATRA query
	 * @return a pattern matcher corresponding to the specification
     * @param optionalEvaluationHints additional / overriding options on query evaluation; passing null means default options associated with the query
	 * @throws ViatraQueryException if the matcher could not be initialized
	 * @since 0.9
	 */
    public abstract <Matcher extends ViatraQueryMatcher<? extends IPatternMatch>> Matcher getMatcher(
    		IQuerySpecification<Matcher> querySpecification, 
    		QueryEvaluationHint optionalEvaluationHints)
    	throws ViatraQueryException;

    /**
     * Initializes matchers for a group of patterns as one step (optionally overriding some of the default query evaluation hints). 
     * If some of the pattern matchers are already
     * constructed in the engine, no task is performed for them.
     * 
     * <p>
     * This preparation step has the advantage that it prepares pattern matchers for an arbitrary number of patterns in a
     * single-pass traversal of the model. 
     * This is typically more efficient than traversing the model each time an individual pattern matcher is initialized on demand. 
     * The performance benefit only manifests itself if the engine is not in wildcard mode.
     * 
	 * @param queryGroup a {@link IQueryGroup} identifying a set of VIATRA queries
     * @param optionalEvaluationHints additional / overriding options on query evaluation; passing null means default options associated with each query
     * @throws ViatraQueryException
     *             if there was an error in preparing the engine
     * @since 0.9
     */
    public abstract void prepareGroup(
    		IQueryGroup queryGroup, 
    		QueryEvaluationHint optionalEvaluationHints)
    	throws ViatraQueryException;
 
    /**
     * Indicates whether the engine is managed, i.e. the default engine assigned to the given scope root by
     * {@link ViatraQueryEngine#on(QueryScope)}.
     * 
     * <p>
     * If the engine is managed, there may be other clients using it, as all calls to
     * {@link ViatraQueryEngine#on(QueryScope)} return the same managed engine instance for a given scope root. Therefore the
     * destructive methods {@link #wipe()} and {@link #dispose()} are not allowed.
     * 
     * <p>
     * On the other hand, if the engine is unmanaged (i.e. a private instance created using
     * {@link #createUnmanagedEngine(QueryScope)}), then {@link #wipe()} and {@link #dispose()} can be called. If you
     * explicitly share a private, unmanaged engine between multiple sites, register a callback using
     * {@link #addLifecycleListener(ViatraQueryEngineLifecycleListener)} to learn when another client has called these
     * destructive methods.
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
     * The engine is in a tainted state if any of its internal processes report back a fatal error. The
     * {@link ViatraQueryEngineLifecycleListener} interface provides a callback method for entering the tainted state.
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
     * {@link #addLifecycleListener(ViatraQueryEngineLifecycleListener)} to learn when another client has called this
     * destructive method.
     * 
     * @throws UnsupportedOperationException
     *             if engine is managed
     */
    public abstract void wipe();

    /**
     * Completely disconnects and dismantles the engine. Cannot be reversed.
     * <p>
     * Matcher objects will continue to return stale results. If no references are retained to the matchers or the
     * engine, they can eventually be GC'ed, and they won't block the EMF model from being GC'ed anymore.
     * <p>
     * The base indexer (see {@link #getBaseIndex()}) built on the model will be disposed alongside the engine, unless
     * the user has manually added listeners on the base index that were not removed yet.
     * <p>
     * Disallowed if the engine is managed (see {@link #isManaged()}), as there may be other clients using it.
     * <p>
     * If you explicitly share a private, unmanaged engine between multiple sites, register a callback using
     * {@link #addLifecycleListener(ViatraQueryEngineLifecycleListener)} to learn when another client has called this
     * destructive method.
     * 
     * @throws UnsupportedOperationException
     *             if engine is managed
     */
    public abstract void dispose();
 
    /**
     * Provides access to the selected query backend component of the VIATRA Query engine.
     * @noreference for internal use only
     */
	public abstract IQueryBackend getQueryBackend(IQueryBackendFactory iQueryBackendFactory)
			throws ViatraQueryException;


	
}
