/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.internal.apiimpl;

import static org.eclipse.viatra.query.runtime.matchers.util.Preconditions.checkArgument;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IMatchUpdateListener;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQueryGroup;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineLifecycleListener;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineManager;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.api.ViatraQueryModelUpdateListener;
import org.eclipse.viatra.query.runtime.api.impl.BaseMatcher;
import org.eclipse.viatra.query.runtime.api.scope.IBaseIndex;
import org.eclipse.viatra.query.runtime.api.scope.IEngineContext;
import org.eclipse.viatra.query.runtime.api.scope.IIndexingErrorListener;
import org.eclipse.viatra.query.runtime.api.scope.QueryScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.internal.engine.LifecycleProvider;
import org.eclipse.viatra.query.runtime.internal.engine.ModelUpdateProvider;
import org.eclipse.viatra.query.runtime.matchers.ViatraQueryRuntimeException;
import org.eclipse.viatra.query.runtime.matchers.backend.IMatcherCapability;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackend;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendFactory;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendHintProvider;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.viatra.query.runtime.matchers.backend.IUpdateable;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryBackendContext;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryCacheContext;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryResultProviderAccess;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.runtime.matchers.psystem.analysis.QueryAnalyzer;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQueries;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery.PQueryStatus;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.query.runtime.registry.IDefaultRegistryView;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistry;
import org.eclipse.viatra.query.runtime.registry.QuerySpecificationRegistry;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * A VIATRA Query engine back-end (implementation)
 * 
 * @author Bergmann Gábor
 */
public final class ViatraQueryEngineImpl extends AdvancedViatraQueryEngine
        implements IQueryBackendHintProvider, IQueryCacheContext, IQueryResultProviderAccess {

    /**
     * 
     */
    private static final String ERROR_ACCESSING_BACKEND = "Error while accessing query evaluator backend";
    /**
     * 
     */
    private static final String QUERY_ON_DISPOSED_ENGINE_MESSAGE = "Cannot evaluate query on disposed engine!";
    /**
     * The engine manager responsible for this engine. Null if this engine is unmanaged.
     */
    private final ViatraQueryEngineManager manager;
    /**
     * The model to which the engine is attached.
     */
    private final QueryScope scope;

    /**
     * The context of the engine, provided by the scope.
     */
    private IEngineContext engineContext;

    /**
     * Initialized matchers for each query
     */
    private final Multimap<IQuerySpecification<? extends ViatraQueryMatcher<?>>, ViatraQueryMatcher<?>> matchers = ArrayListMultimap
            .create();

    /**
     * The RETE and other pattern matcher implementations of the VIATRA Query Engine.
     */
    private volatile Map<IQueryBackendFactory, IQueryBackend> queryBackends = new HashMap<>();

    /**
     * The current engine default hints
     */
    private final ViatraQueryEngineOptions engineOptions;

    /**
     * Common query analysis provided to backends
     */
    private QueryAnalyzer queryAnalyzer;

    /**
     * true if message delivery is currently delayed, false otherwise
     */
    private boolean delayMessageDelivery = false;

    private final LifecycleProvider lifecycleProvider;
    private final ModelUpdateProvider modelUpdateProvider;
    private Logger logger;
    private boolean disposed = false;

    /**
     * @param manager
     *            null if unmanaged
     * @param scope
     * @param engineDefaultHint
     * @since 1.4
     */
    public ViatraQueryEngineImpl(ViatraQueryEngineManager manager, QueryScope scope,
            ViatraQueryEngineOptions engineOptions) {
        super();
        this.manager = manager;
        this.scope = scope;
        this.lifecycleProvider = new LifecycleProvider(this, getLogger());
        this.modelUpdateProvider = new ModelUpdateProvider(this, getLogger());
        this.engineContext = scope.createEngineContext(this, taintListener, getLogger());

        if (engineOptions != null) {
            this.engineOptions = engineOptions;
        } else {
            this.engineOptions = ViatraQueryEngineOptions.DEFAULT;
        }

    }

    /**
     * @param manager
     *            null if unmanaged
     * @param scope
     * @param engineDefaultHint
     */
    public ViatraQueryEngineImpl(ViatraQueryEngineManager manager, QueryScope scope) {
        this(manager, scope, ViatraQueryEngineOptions.DEFAULT);
    }

    @Override
    public boolean isUpdatePropagationDelayed() {
        return this.delayMessageDelivery;
    }

    @Override
    public <V> V delayUpdatePropagation(Callable<V> callable) throws InvocationTargetException {
        boolean wasAlreadyDelayed = this.delayMessageDelivery;
        V result = null;

        if (!wasAlreadyDelayed) {
            this.delayMessageDelivery = true;
        }

        try {
            result = callable.call();
        } catch (Exception e) {
            throw new InvocationTargetException(e);
        }

        if (!wasAlreadyDelayed) {
            this.delayMessageDelivery = false;

            // now deliver the messages
            for (IQueryBackend backend : this.queryBackends.values()) {
                backend.flushUpdates();
            }
        }
        return result;
    }

    @Override
    public Set<? extends ViatraQueryMatcher<? extends IPatternMatch>> getCurrentMatchers() {
        return new HashSet<>(matchers.values());
    }

    @Override
    public <Matcher extends ViatraQueryMatcher<? extends IPatternMatch>> Matcher getMatcher(
            IQuerySpecification<Matcher> querySpecification) {
        return getMatcher(querySpecification, null);
    }

    @Override
    public <Matcher extends ViatraQueryMatcher<? extends IPatternMatch>> Matcher getMatcher(
            IQuerySpecification<Matcher> querySpecification, QueryEvaluationHint optionalEvaluationHints) {
        Matcher matcher = getExistingMatcher(querySpecification, optionalEvaluationHints);
        if (matcher != null) {
            return matcher;
        }
        matcher = querySpecification.instantiate();
        if (matcher == null) {
            // Backward compatibility, generated code before 1.4 does not provide method to create uninitialized
            // matchers
            // In this case, we lose hint information.
            return querySpecification.getMatcher(this);
        }

        BaseMatcher<?> baseMatcher = (BaseMatcher<?>) matcher;
        IMatcherCapability capability = getRequestedCapability(querySpecification, optionalEvaluationHints);
        ((QueryResultWrapper) baseMatcher).setBackend(this,
                getResultProvider(querySpecification, optionalEvaluationHints), capability);
        internalRegisterMatcher(querySpecification, baseMatcher);
        return matcher;
    }

    @Override
    public <Matcher extends ViatraQueryMatcher<? extends IPatternMatch>> Matcher getExistingMatcher(
            IQuerySpecification<Matcher> querySpecification) {
        return getExistingMatcher(querySpecification, null);
    }

    @SuppressWarnings("unchecked")
    public <Matcher extends ViatraQueryMatcher<? extends IPatternMatch>> Matcher getExistingMatcher(
            IQuerySpecification<Matcher> querySpecification, QueryEvaluationHint optionalOverrideHints) {
        IMatcherCapability requestedCapability = getRequestedCapability(querySpecification, optionalOverrideHints);
        for (ViatraQueryMatcher<?> matcher : matchers.get(querySpecification)) {
            BaseMatcher<?> baseMatcher = (BaseMatcher<?>) matcher;
            if (baseMatcher.getCapabilities().canBeSubstitute(requestedCapability))
                return (Matcher) matcher;
        }
        return null;
    }

    @Override
    public ViatraQueryMatcher<? extends IPatternMatch> getMatcher(String patternFQN) {
        IQuerySpecificationRegistry registry = QuerySpecificationRegistry.getInstance();
        IDefaultRegistryView view = registry.getDefaultView();
        IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> querySpecification = view
                .getEntry(patternFQN).get();
        if (querySpecification != null) {
            return getMatcher(querySpecification);
        } else {
            throw new ViatraQueryException(String.format(
                    "No matcher could be constructed for the pattern with FQN %s; if the generated matcher class is not available, please access for the first time using getMatcher(IQuerySpecification)",
                    patternFQN), "No matcher could be constructed for given pattern FQN.");
        }
    }

    @Override
    public IBaseIndex getBaseIndex() {
        return engineContext.getBaseIndex();
    }

    public final Logger getLogger() {
        if (logger == null) {
            final int hash = System.identityHashCode(this);
            logger = Logger.getLogger(ViatraQueryLoggingUtil.getLogger(ViatraQueryEngine.class).getName() + "." + hash);
            if (logger == null)
                throw new AssertionError(
                        "Configuration error: unable to create VIATRA Query runtime logger for engine " + hash);
        }
        return logger;
    }

    ///////////////// internal stuff //////////////
    private void internalRegisterMatcher(IQuerySpecification<?> querySpecification, ViatraQueryMatcher<?> matcher) {
        matchers.put(querySpecification, matcher);
        lifecycleProvider.matcherInstantiated(matcher);
    }

    /**
     * Provides access to the selected query backend component of the VIATRA Query Engine.
     */
    @Override
    public IQueryBackend getQueryBackend(IQueryBackendFactory iQueryBackendFactory) {
        IQueryBackend iQueryBackend = queryBackends.get(iQueryBackendFactory);
        if (iQueryBackend == null) {
            // do this first, to make sure the runtime context exists
            final IQueryRuntimeContext queryRuntimeContext = engineContext.getQueryRuntimeContext();

            // maybe the backend has been created in the meantime when the indexer was initialized and queried for
            // derived features
            // no need to instantiate a new backend in that case
            iQueryBackend = queryBackends.get(iQueryBackendFactory);
            if (iQueryBackend == null) {

                // need to instantiate the backend
                iQueryBackend = iQueryBackendFactory.create(new IQueryBackendContext() {

                    @Override
                    public IQueryRuntimeContext getRuntimeContext() {
                        return queryRuntimeContext;
                    }

                    @Override
                    public IQueryCacheContext getQueryCacheContext() {
                        return ViatraQueryEngineImpl.this;
                    }

                    @Override
                    public Logger getLogger() {
                        return logger;
                    }

                    @Override
                    public IQueryBackendHintProvider getHintProvider() {
                        return ViatraQueryEngineImpl.this;
                    }

                    @Override
                    public IQueryResultProviderAccess getResultProviderAccess() {
                        return ViatraQueryEngineImpl.this;
                    }

                    @Override
                    public QueryAnalyzer getQueryAnalyzer() {
                        if (queryAnalyzer == null)
                            queryAnalyzer = new QueryAnalyzer(queryRuntimeContext.getMetaContext());
                        return queryAnalyzer;
                    }

                    @Override
                    public boolean areUpdatesDelayed() {
                        return ViatraQueryEngineImpl.this.delayMessageDelivery;
                    }

                });
                queryBackends.put(iQueryBackendFactory, iQueryBackend);
            }
        }
        return iQueryBackend;
    }

    ///////////////// advanced stuff /////////////

    @Override
    public void dispose() {
        if (manager != null) {
            throw new UnsupportedOperationException(
                    String.format("Cannot dispose() managed VIATRA Query Engine. Attempted for scope %s.", scope));
        }
        wipe();

        this.disposed = true;

        // called before base index disposal to allow removal of base listeners
        lifecycleProvider.engineDisposed();

        try {
            engineContext.dispose();
        } catch (IllegalStateException ex) {
            getLogger().warn(
                    "The base index could not be disposed along with the VIATRA Query engine, as there are still active listeners on it.");
        }
    }

    @Override
    public void wipe() {
        if (manager != null) {
            throw new UnsupportedOperationException(
                    String.format("Cannot wipe() managed VIATRA Query Engine. Attempted for scope %s.", scope));
        }
        if (queryBackends != null) {
            for (IQueryBackend backend : queryBackends.values()) {
                backend.dispose();
            }
            queryBackends.clear();
        }
        matchers.clear();
        queryAnalyzer = null;
        lifecycleProvider.engineWiped();
    }

    /**
     * Indicates whether the engine is in a tainted, inconsistent state.
     */
    private boolean tainted = false;
    private IIndexingErrorListener taintListener = new SelfTaintListener(this);

    private static class SelfTaintListener implements IIndexingErrorListener {
        WeakReference<ViatraQueryEngineImpl> queryEngineRef;

        public SelfTaintListener(ViatraQueryEngineImpl queryEngine) {
            this.queryEngineRef = new WeakReference<ViatraQueryEngineImpl>(queryEngine);
        }

        public void engineBecameTainted(String description, Throwable t) {
            final ViatraQueryEngineImpl queryEngine = queryEngineRef.get();
            if (queryEngine != null) {
                queryEngine.tainted = true;
                queryEngine.lifecycleProvider.engineBecameTainted(description, t);
            }
        }

        private boolean noTaintDetectedYet = true;

        protected void notifyTainted(String description, Throwable t) {
            if (noTaintDetectedYet) {
                noTaintDetectedYet = false;
                engineBecameTainted(description, t);
            }
        }

        @Override
        public void error(String description, Throwable t) {
            // Errors does not mean tainting
        }

        @Override
        public void fatal(String description, Throwable t) {
            notifyTainted(description, t);
        }
    }

    @Override
    public boolean isTainted() {
        return tainted;
    }

    @Override
    public boolean isManaged() {
        return manager != null;
        // return isAdvanced; ???
    }

    private <Match extends IPatternMatch> IQueryResultProvider getUnderlyingResultProvider(
            final BaseMatcher<Match> matcher) {
        // IQueryResultProvider resultProvider = reteEngine.accessMatcher(matcher.getSpecification());
        return matcher.backend;
    }

    @Override
    public <Match extends IPatternMatch> void addMatchUpdateListener(final ViatraQueryMatcher<Match> matcher,
            final IMatchUpdateListener<? super Match> listener, boolean fireNow) {

        checkArgument(listener != null, "Cannot add null listener!");
        checkArgument(matcher.getEngine() == this, "Cannot register listener for matcher of different engine!");
        checkArgument(!disposed, "Cannot register listener on matcher of disposed engine!");

        final BaseMatcher<Match> bm = (BaseMatcher<Match>) matcher;

        final IUpdateable updateDispatcher = (updateElement, isInsertion) -> {
            Match match = null;
            try {
                match = bm.newMatch(updateElement.getElements());
                if (isInsertion)
                    listener.notifyAppearance(match);
                else
                    listener.notifyDisappearance(match);
            } catch (Throwable e) { // NOPMD
                if (e instanceof Error)
                    throw (Error) e;
                logger.warn(
                        String.format(
                                "The incremental pattern matcher encountered an error during %s a callback on %s of match %s of pattern %s. Error message: %s. (Developer note: %s in %s callback)",
                                match == null ? "preparing" : "invoking", isInsertion ? "insertion" : "removal",
                                match == null ? updateElement.toString() : match.prettyPrint(),
                                matcher.getPatternName(), e.getMessage(), e.getClass().getSimpleName(), listener),
                        e);
            }

        };

        IQueryResultProvider resultProvider = getUnderlyingResultProvider(bm);
        resultProvider.addUpdateListener(updateDispatcher, listener, fireNow);
    }

    @Override
    public <Match extends IPatternMatch> void removeMatchUpdateListener(ViatraQueryMatcher<Match> matcher,
            IMatchUpdateListener<? super Match> listener) {
        checkArgument(listener != null, "Cannot remove null listener!");
        checkArgument(matcher.getEngine() == this, "Cannot remove listener from matcher of different engine!");
        checkArgument(!disposed, "Cannot remove listener from matcher of disposed engine!");

        final BaseMatcher<Match> bm = (BaseMatcher<Match>) matcher;

        try {
            IQueryResultProvider resultProvider = getUnderlyingResultProvider(bm);
            resultProvider.removeUpdateListener(listener);
        } catch (Exception e) {
            logger.error(
                    "Error while removing listener " + listener + " from the matcher of " + matcher.getPatternName(),
                    e);
        }
    }

    @Override
    public void addModelUpdateListener(ViatraQueryModelUpdateListener listener) {
        modelUpdateProvider.addListener(listener);
    }

    @Override
    public void removeModelUpdateListener(ViatraQueryModelUpdateListener listener) {
        modelUpdateProvider.removeListener(listener);
    }

    @Override
    public void addLifecycleListener(ViatraQueryEngineLifecycleListener listener) {
        lifecycleProvider.addListener(listener);
    }

    @Override
    public void removeLifecycleListener(ViatraQueryEngineLifecycleListener listener) {
        lifecycleProvider.removeListener(listener);
    }

    /**
     * Returns an internal interface towards the query backend to feed the matcher with results.
     * 
     * @param query
     *            the pattern for which the result provider should be delivered
     * 
     * @throws ViatraQueryRuntimeException
     */
    public IQueryResultProvider getResultProvider(IQuerySpecification<?> query) {
        Preconditions.checkState(!disposed, QUERY_ON_DISPOSED_ENGINE_MESSAGE);

        return getResultProviderInternal(query, null);
    }

    /**
     * Returns an internal interface towards the query backend to feed the matcher with results.
     * 
     * @param query
     *            the pattern for which the result provider should be delivered
     * 
     * @throws ViatraQueryRuntimeException
     */
    public IQueryResultProvider getResultProvider(IQuerySpecification<?> query, QueryEvaluationHint hint) {
        Preconditions.checkState(!disposed, QUERY_ON_DISPOSED_ENGINE_MESSAGE);

        return getResultProviderInternal(query, hint);
    }

    /**
     * This method returns the result provider exactly as described by the passed hint. Query cannot be null! Use
     * {@link #getQueryEvaluationHint(IQuerySpecification, QueryEvaluationHint)} before passing a hint to this method to
     * make sure engine and query specific hints are correctly applied.
     * 
     * @throws ViatraQueryRuntimeException
     */
    private IQueryResultProvider getResultProviderInternal(IQuerySpecification<?> query, QueryEvaluationHint hint) {
        return getResultProviderInternal(query.getInternalQueryRepresentation(), hint);
    }

    /**
     * This method returns the result provider exactly as described by the passed hint. Query cannot be null! Use
     * {@link #getQueryEvaluationHint(IQuerySpecification, QueryEvaluationHint)} before passing a hint to this method to
     * make sure engine and query specific hints are correctly applied.
     * 
     * @throws ViatraQueryRuntimeException
     */
    private IQueryResultProvider getResultProviderInternal(PQuery query, QueryEvaluationHint hint) {
        Preconditions.checkArgument(query != null, "Query cannot be null!");
        final IQueryBackend backend = getQueryBackend(getQueryEvaluationHint(query, hint).getQueryBackendFactory());
        return backend.getResultProvider(query, hint);
    }

    /**
     * Returns the query backend (influenced by the hint system), even if it is a non-caching backend.
     * 
     * @throws ViatraQueryRuntimeException
     */
    private IQueryBackend getQueryBackend(PQuery query) {
        return getQueryBackend(getQueryEvaluationHint(query).getQueryBackendFactory());
    }

    /**
     * Returns a caching query backend (influenced by the hint system).
     * 
     * @throws ViatraQueryRuntimeException
     */
    private IQueryBackend getCachingQueryBackend(PQuery query) {
        IQueryBackend regularBackend = getQueryBackend(query);
        if (regularBackend.isCaching())
            return regularBackend;
        else
            return getQueryBackend(engineOptions.getDefaultCachingBackendFactory());
    }

    @Override
    public boolean isResultCached(PQuery query) {
        try {
            return null != getCachingQueryBackend(query).peekExistingResultProvider(query);
        } catch (ViatraQueryException iqe) {
            getLogger().error(ERROR_ACCESSING_BACKEND, iqe);
            return false;
        }
    }

    @Override
    public IQueryResultProvider getCachingResultProvider(PQuery query) {
        try {
            return getCachingQueryBackend(query).getResultProvider(query);
        } catch (ViatraQueryException iqe) {
            getLogger().error(ERROR_ACCESSING_BACKEND, iqe);
            throw iqe;
        }
    }

    private QueryEvaluationHint getEngineDefaultHint() {
        return engineOptions.getEngineDefaultHints();
    }

    @Override
    public QueryEvaluationHint getQueryEvaluationHint(PQuery query) {
        return getEngineDefaultHint().overrideBy(query.getEvaluationHints());
    }

    private QueryEvaluationHint getQueryEvaluationHint(IQuerySpecification<?> querySpecification,
            QueryEvaluationHint optionalOverrideHints) {
        return getQueryEvaluationHint(querySpecification.getInternalQueryRepresentation())
                .overrideBy(optionalOverrideHints);
    }

    private QueryEvaluationHint getQueryEvaluationHint(PQuery query, QueryEvaluationHint optionalOverrideHints) {
        return getQueryEvaluationHint(query).overrideBy(optionalOverrideHints);
    }

    private IMatcherCapability getRequestedCapability(IQuerySpecification<?> querySpecification,
            QueryEvaluationHint optionalOverrideHints) {
        return getQueryEvaluationHint(querySpecification, optionalOverrideHints)
                .calculateRequiredCapability(querySpecification.getInternalQueryRepresentation());
    }

    @Override
    public void prepareGroup(IQueryGroup queryGroup, final QueryEvaluationHint optionalEvaluationHints) {
        try {
            Preconditions.checkState(!disposed, QUERY_ON_DISPOSED_ENGINE_MESSAGE);

            final Set<IQuerySpecification<?>> specifications = new HashSet<IQuerySpecification<?>>(
                    queryGroup.getSpecifications());
            final Collection<PQuery> patterns = specifications.stream().map(
                    IQuerySpecification::getInternalQueryRepresentation).collect(Collectors.toList());
            patterns.forEach(PQuery::ensureInitialized);
            
            Collection<String> erroneousPatterns = patterns.stream().
                    filter(PQueries.queryStatusPredicate(PQueryStatus.ERROR)).
                    map(PQuery::getFullyQualifiedName).
                    collect(Collectors.toList());
            Preconditions.checkState(erroneousPatterns.isEmpty(), "Erroneous query(s) found: %s",
                    erroneousPatterns.stream().collect(Collectors.joining(", ")));

            // TODO maybe do some smarter preparation per backend?
            try {
                engineContext.getBaseIndex().coalesceTraversals(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        for (IQuerySpecification<?> query : specifications) {
                            getResultProviderInternal(query, optionalEvaluationHints);
                        }
                        return null;
                    }
                });
            } catch (InvocationTargetException ex) {
                final Throwable cause = ex.getCause();
                if (cause instanceof QueryProcessingException)
                    throw (QueryProcessingException) cause;
                if (cause instanceof ViatraQueryException)
                    throw (ViatraQueryException) cause;
                if (cause instanceof RuntimeException)
                    throw (RuntimeException) cause;
                assert (false);
            }
        } catch (QueryProcessingException e) {
            throw new ViatraQueryException(e);
        }
    }

    @Override
    public QueryScope getScope() {
        return scope;
    }

    @Override
    public ViatraQueryEngineOptions getEngineOptions() {
        return engineOptions;
    }

    @Override
    public IQueryResultProvider getResultProviderOfMatcher(ViatraQueryMatcher<? extends IPatternMatch> matcher) {
        return ((QueryResultWrapper) matcher).backend;
    }

    @Override
    public IQueryResultProvider getResultProvider(PQuery query, QueryEvaluationHint overrideHints) {
        try {
            return getResultProviderInternal(query, overrideHints);
        } catch (ViatraQueryException e) {
            getLogger().error(ERROR_ACCESSING_BACKEND, e);
            throw e;
        }
    }

    @Override
    public boolean isDisposed() {
        return disposed;
    }
    
}
