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

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

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
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQueries;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery.PQueryStatus;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.registry.IDefaultRegistryView;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistry;
import org.eclipse.viatra.query.runtime.registry.QuerySpecificationRegistry;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * A VIATRA Query engine back-end (implementation)
 * 
 * @author Bergmann Gábor
 */
public final class ViatraQueryEngineImpl extends AdvancedViatraQueryEngine implements IQueryBackendHintProvider, IQueryCacheContext, IQueryResultProviderAccess{
    
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
    private final Multimap<IQuerySpecification<? extends ViatraQueryMatcher<?>>, ViatraQueryMatcher<?>> matchers
    		= ArrayListMultimap.create();
    
	/**
     * The RETE and other pattern matcher implementations of the VIATRA Query Engine.
     */
    private volatile Map<IQueryBackendFactory, IQueryBackend> queryBackends = Maps.newHashMap();
    
    /**
     * The current engine default hints
     */
    private final ViatraQueryEngineOptions engineOptions;
    
    private final LifecycleProvider lifecycleProvider;
    private final ModelUpdateProvider modelUpdateProvider;
    private Logger logger;
	private boolean disposed = false;
	
    /**
     * @param manager
     *            null if unmanaged
     * @param scope
     * @param engineDefaultHint
     * @throws ViatraQueryException
     *             if the emf root is invalid
     * @since 1.4
     */
    public ViatraQueryEngineImpl(ViatraQueryEngineManager manager, QueryScope scope, ViatraQueryEngineOptions engineOptions) throws ViatraQueryException {
        super();
        this.manager = manager;
        this.scope = scope;
        this.lifecycleProvider = new LifecycleProvider(this, getLogger());
        this.modelUpdateProvider = new ModelUpdateProvider(this, getLogger());
        this.engineContext = scope.createEngineContext(this, taintListener, getLogger());
        
        if (engineOptions != null){
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
     * @throws ViatraQueryException
     *             if the emf root is invalid
     */
    public ViatraQueryEngineImpl(ViatraQueryEngineManager manager, QueryScope scope) throws ViatraQueryException {
        this(manager, scope, ViatraQueryEngineOptions.DEFAULT);
    }
    
    @Override
	public Set<? extends ViatraQueryMatcher<? extends IPatternMatch>> getCurrentMatchers(){
        return ImmutableSet.copyOf(matchers.values());
    }
    
    @Override
	public <Matcher extends ViatraQueryMatcher<? extends IPatternMatch>> Matcher getMatcher(IQuerySpecification<Matcher> querySpecification) throws ViatraQueryException {
        return getMatcher(querySpecification, getQueryEvaluationHint(querySpecification.getInternalQueryRepresentation()));
    }
    
    @Override
    public <Matcher extends ViatraQueryMatcher<? extends IPatternMatch>> Matcher getMatcher(
    		IQuerySpecification<Matcher> querySpecification,
    		QueryEvaluationHint optionalEvaluationHints) 
    	throws ViatraQueryException 
    {
        Matcher matcher = getExistingMatcher(querySpecification, optionalEvaluationHints);
        if (matcher != null){
            return matcher;
        }
        matcher = querySpecification.instantiate();
        if (matcher == null){
            // Backward compatibility, generated code before 1.4 does not provide method to create uninitialized matchers
            // In this case, we lose hint information.
            return querySpecification.getMatcher(this);
        }
        
        BaseMatcher<?> baseMatcher = (BaseMatcher<?>) matcher;
        IMatcherCapability capability = getRequestedCapability(querySpecification, optionalEvaluationHints);
        try {
            ((QueryResultWrapper)baseMatcher).setBackend(this, getResultProvider(querySpecification, optionalEvaluationHints), capability);
        } catch (QueryProcessingException e) {
           throw new ViatraQueryException(e);
        }
        internalRegisterMatcher(querySpecification, baseMatcher);
        return matcher;
    }

	@Override
	public <Matcher extends ViatraQueryMatcher<? extends IPatternMatch>> Matcher getExistingMatcher(IQuerySpecification<Matcher> querySpecification) {
		return getExistingMatcher(querySpecification, null);
	}
	
	@SuppressWarnings("unchecked")
	public <Matcher extends ViatraQueryMatcher<? extends IPatternMatch>> Matcher getExistingMatcher(IQuerySpecification<Matcher> querySpecification, QueryEvaluationHint optionalOverrideHints) {
	    IMatcherCapability requestedCapability = getRequestedCapability(querySpecification, optionalOverrideHints);
	    for(ViatraQueryMatcher<?> matcher : matchers.get(querySpecification)){
	        BaseMatcher<?> baseMatcher = (BaseMatcher<?>)matcher;
	        if (baseMatcher.getCapabilities().canBeSubstitute(requestedCapability)) return (Matcher) matcher;
	    }
	    return null;
	}
    
    @Override
    public ViatraQueryMatcher<? extends IPatternMatch> getMatcher(String patternFQN) throws ViatraQueryException {
        IQuerySpecificationRegistry registry = QuerySpecificationRegistry.getInstance();
        IDefaultRegistryView view = registry.getDefaultView();
        IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> querySpecification = view.getEntry(patternFQN).get();
        if (querySpecification != null) {
            return getMatcher(querySpecification);
        } else {
            throw new ViatraQueryException(
                    String.format(
                            "No matcher could be constructed for the pattern with FQN %s; if the generated matcher class is not available, please access for the first time using getMatcher(IQuerySpecification)",
                            patternFQN), "No matcher could be constructed for given pattern FQN.");
        }
    }

    @Override
	public IBaseIndex getBaseIndex() throws ViatraQueryException {
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

    /**
     * Report when a pattern matcher has been completely initialized, so that it can be registered into the engine.
     * @param querySpecification the {@link IQuerySpecification} that corresponds to the matcher
     * @param matcher the {@link ViatraQueryMatcher} that has finished its initialization process
     * 
     * TODO make it package-only visible when implementation class is moved to impl package
     * @deprecated generated code after 1.4 will not need this method
     */
	@Deprecated
    public void reportMatcherInitialized(IQuerySpecification<?> querySpecification, ViatraQueryMatcher<?> matcher) {
        internalRegisterMatcher(querySpecification, matcher);
    }
    
    private void internalRegisterMatcher(IQuerySpecification<?> querySpecification, ViatraQueryMatcher<?> matcher) {
        matchers.put(querySpecification, matcher);
        lifecycleProvider.matcherInstantiated(matcher);
    }

    /**
     * Provides access to the selected query backend component of the VIATRA Query Engine.
     */
    @Override
	public IQueryBackend getQueryBackend(IQueryBackendFactory iQueryBackendFactory) throws ViatraQueryException {
    	IQueryBackend iQueryBackend = queryBackends.get(iQueryBackendFactory);
        if (iQueryBackend == null) {
        	// do this first, to make sure the runtime context exists
        	final IQueryRuntimeContext queryRuntimeContext = engineContext.getQueryRuntimeContext();
        	
        	// maybe the backend has been created in the meantime when the indexer was initialized and queried for derived features
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
        
        try{
	        engineContext.dispose();
        } catch (IllegalStateException ex) {
        	getLogger().warn(
        			"The base index could not be disposed along with the InQuery engine, as there are still active listeners on it.");
        }
    }

    @Override
    public void wipe() {
        if (manager != null) {
        	throw new UnsupportedOperationException(
        			String.format("Cannot wipe() managed VIATRA Query Engine. Attempted for scope %s.", scope));
        }
        // TODO generalize for each query backend
        if (queryBackends != null) {
        	for (IQueryBackend backend : queryBackends.values()) {
				backend.dispose();
			}
            queryBackends.clear();
        }
        matchers.clear();
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
            //Errors does not mean tainting        
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
		//IQueryResultProvider resultProvider = reteEngine.accessMatcher(matcher.getSpecification());
		return matcher.backend;
	}

    @Override
	public <Match extends IPatternMatch> void addMatchUpdateListener(final ViatraQueryMatcher<Match> matcher,
            final IMatchUpdateListener<? super Match> listener, boolean fireNow) {
    	
        checkArgument(listener != null, "Cannot add null listener!");
        checkArgument(matcher.getEngine() == this, "Cannot register listener for matcher of different engine!");
        checkArgument(!disposed, "Cannot register listener on matcher of disposed engine!");
        
        final BaseMatcher<Match> bm = (BaseMatcher<Match>)matcher;
        
        final IUpdateable updateDispatcher = new IUpdateable() {
			@Override
			public void update(Tuple updateElement, boolean isInsertion) {
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
		            logger.warn(String.format(
		                            "The incremental pattern matcher encountered an error during %s a callback on %s of match %s of pattern %s. Error message: %s. (Developer note: %s in %s callback)",
		                            match == null ? "preparing" : "invoking",
		                            isInsertion ? "insertion" : "removal", 
		                            match == null ? updateElement.toString() : match.prettyPrint(),
		                            matcher.getPatternName(), 
		                            e.getMessage(), e.getClass().getSimpleName(), listener), e);
		        }
	            
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
        
        final BaseMatcher<Match> bm = (BaseMatcher<Match>)matcher;
        
        try {
            IQueryResultProvider resultProvider = getUnderlyingResultProvider(bm);
            resultProvider.removeUpdateListener(listener);
        } catch (Exception e) {
            logger.error("Error while removing listener " + listener + " from the matcher of " + matcher.getPatternName(), e);
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
     * @param query the pattern for which the result provider should be delivered
     * 
     * @throws QueryProcessingException
     * @throws ViatraQueryException
     */
	public IQueryResultProvider getResultProvider(IQuerySpecification<?> query) 
		throws QueryProcessingException, ViatraQueryException 
	{
		Preconditions.checkState(!disposed, QUERY_ON_DISPOSED_ENGINE_MESSAGE);
        
		return getResultProviderInternal(query, getQueryEvaluationHint(query.getInternalQueryRepresentation()));
	}
	
	/**
     * Returns an internal interface towards the query backend to feed the matcher with results. 
     * 
     * @param query the pattern for which the result provider should be delivered
     * 
     * @throws QueryProcessingException
     * @throws ViatraQueryException
     */
    public IQueryResultProvider getResultProvider(IQuerySpecification<?> query, QueryEvaluationHint hint) 
        throws QueryProcessingException, ViatraQueryException 
    {
        Preconditions.checkState(!disposed, QUERY_ON_DISPOSED_ENGINE_MESSAGE);
        
        return getResultProviderInternal(query, getQueryEvaluationHint(query, hint));
    }

    /**
     * This method returns the result provider exactly as described by the passed hint. 
     * Query and hint cannot be null!
     * Use {@link #getQueryEvaluationHint(IQuerySpecification, QueryEvaluationHint)} before passing a hint to this method
     * to make sure engine and query specific hints are correctly applied.
     */
    private IQueryResultProvider getResultProviderInternal(IQuerySpecification<?> query, QueryEvaluationHint hint) throws ViatraQueryException, QueryProcessingException{
        return getResultProviderInternal(query.getInternalQueryRepresentation(), hint);
    }
    
    /**
     * This method returns the result provider exactly as described by the passed hint. 
     * Query and hint cannot be null!
     * Use {@link #getQueryEvaluationHint(IQuerySpecification, QueryEvaluationHint)} before passing a hint to this method
     * to make sure engine and query specific hints are correctly applied.
     */
    private IQueryResultProvider getResultProviderInternal(PQuery query, QueryEvaluationHint hint) throws ViatraQueryException, QueryProcessingException{
        Preconditions.checkArgument(query != null, "Query cannot be null!");
        Preconditions.checkArgument(hint != null, "Hint cannot be null!");
        final IQueryBackend backend = getQueryBackend(hint.getQueryBackendFactory());
        return backend.getResultProvider(query, hint);
    }
	
	/**
	 * Returns the query backend (influenced by the hint system), even if it is a non-caching backend.
	 */
	private IQueryBackend getQueryBackend(PQuery query) throws ViatraQueryException {
		return getQueryBackend(getQueryEvaluationHint(query).getQueryBackendFactory());
	}
	/**
	 * Returns a caching query backend (influenced by the hint system).
	 */
	private IQueryBackend getCachingQueryBackend(PQuery query) throws ViatraQueryException {
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
		} catch (ViatraQueryException iqe)  {
			getLogger().error("Error while accessing query evaluator backend", iqe);
			return false;
		}
	}
	
	@Override
	public IQueryResultProvider getCachingResultProvider(PQuery query) throws QueryProcessingException {
		try {
			return getCachingQueryBackend(query).getResultProvider(query);
		} catch (ViatraQueryException iqe)  {
			getLogger().error("Error while accessing query evaluator backend", iqe);
			throw new QueryProcessingException(
					"Error while attempting to consult caching query evaluator backend for evaluating query {1}", 
					new String[] {query.getFullyQualifiedName()}, 
					"Error while accessing query evaluator backends", 
					query, iqe);
		}
	}
		
	private QueryEvaluationHint getEngineDefaultHint(){
	    return engineOptions.getEngineDefaultHints();
	}
	
	@Override
	public QueryEvaluationHint getQueryEvaluationHint(PQuery query) {
	    return getEngineDefaultHint().overrideBy(query.getEvaluationHints());
	}
	
	private QueryEvaluationHint getQueryEvaluationHint(IQuerySpecification<?> querySpecification, QueryEvaluationHint optionalOverrideHints) {
	    return getQueryEvaluationHint(querySpecification.getInternalQueryRepresentation()).overrideBy(optionalOverrideHints);
	}
	
	private IMatcherCapability getRequestedCapability(IQuerySpecification<?> querySpecification, QueryEvaluationHint optionalOverrideHints){
	    return getQueryEvaluationHint(querySpecification, optionalOverrideHints).calculateRequiredCapability(querySpecification.getInternalQueryRepresentation());
	}
	
	@Override
	public void prepareGroup(IQueryGroup queryGroup,
			final QueryEvaluationHint optionalEvaluationHints) throws ViatraQueryException 
	{
        try {
    		Preconditions.checkState(!disposed, QUERY_ON_DISPOSED_ENGINE_MESSAGE);
    		
    		final Set<IQuerySpecification<?>> specifications = new HashSet<IQuerySpecification<?>>(queryGroup.getSpecifications());
            final Collection<PQuery> patterns = Collections2.transform(specifications, new Function<IQuerySpecification<?>, PQuery>() {
            	@Override
            	public PQuery apply(IQuerySpecification<?> input) {
            		return input.getInternalQueryRepresentation();
            	}
            });
            for (PQuery pQuery : patterns) {
                pQuery.ensureInitialized();
            }
            Collection<String> erroneousPatterns = Collections2.transform(
                    Collections2.filter(patterns, PQueries.queryStatusPredicate(PQueryStatus.ERROR)),
                    PQueries.queryNameFunction());
            Preconditions.checkState(erroneousPatterns.isEmpty(), "Erroneous query(s) found: %s", Joiner.on(", ")
                    .join(erroneousPatterns));

            // TODO maybe do some smarter preparation per backend?
            try {
                engineContext.getBaseIndex().coalesceTraversals(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        for (IQuerySpecification<?> query : specifications) {
                            QueryEvaluationHint queryEvaluationHint = getQueryEvaluationHint(query, optionalEvaluationHints);
                            getResultProviderInternal(query, queryEvaluationHint);
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
        return ((QueryResultWrapper)matcher).backend;
    }
    
    @Override
    public IQueryResultProvider getResultProvider(PQuery query, QueryEvaluationHint overrideHints) throws QueryProcessingException {
        try {
            return getResultProviderInternal(query, getQueryEvaluationHint(query).overrideBy(overrideHints));
        } catch (ViatraQueryException e) {
            getLogger().error("Error while accessing query evaluator backend", e);
            throw new QueryProcessingException(
                    "Error while attempting to consult query evaluator backend for evaluating query {1}", 
                    new String[] {query.getFullyQualifiedName()}, 
                    "Error while accessing query evaluator backends", 
                    query, e);
        }
    }
    
    // /**
    // * EXPERIMENTAL: Creates a VIATRA Query Engine that executes post-commit, or retrieves an already existing one.
    // * @param emfRoot the EMF root where this engine should operate
    // * @param reteThreads experimental feature; 0 is recommended
    // * @return a new or previously existing engine
    // * @throws ViatraQueryException
    // */
    // public ReteEngine<String> getReteEngine(final TransactionalEditingDomain editingDomain, int reteThreads) throws
    // ViatraQueryException {
    // final ResourceSet resourceSet = editingDomain.getResourceSet();
    // WeakReference<ReteEngine<String>> weakReference = engines.get(resourceSet);
    // ReteEngine<String> engine = weakReference != null ? weakReference.get() : null;
    // if (engine == null) {
    // IPatternMatcherRuntimeContext<String> context = new
    // EMFPatternMatcherRuntimeContext.ForTransactionalEditingDomain<String>(editingDomain);
    // engine = buildReteEngine(context, reteThreads);
    // if (engine != null) engines.put(resourceSet, new WeakReference<ReteEngine<String>>(engine));
    // }
    // return engine;
    // }

    
}
