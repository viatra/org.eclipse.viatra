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

package org.eclipse.incquery.runtime.internal.apiimpl;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.api.IMatchUpdateListener;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQueryGroup;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryEngineLifecycleListener;
import org.eclipse.incquery.runtime.api.IncQueryEngineManager;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.api.IncQueryModelUpdateListener;
import org.eclipse.incquery.runtime.api.impl.BaseMatcher;
import org.eclipse.incquery.runtime.api.scope.IBaseIndex;
import org.eclipse.incquery.runtime.api.scope.IEngineContext;
import org.eclipse.incquery.runtime.api.scope.IEngineContext.IQueryBackendInitializer;
import org.eclipse.incquery.runtime.api.scope.IIndexingErrorListener;
import org.eclipse.incquery.runtime.api.scope.IncQueryScope;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.QueryBackendRegistry;
import org.eclipse.incquery.runtime.extensibility.QuerySpecificationRegistry;
import org.eclipse.incquery.runtime.internal.engine.LifecycleProvider;
import org.eclipse.incquery.runtime.internal.engine.ModelUpdateProvider;
import org.eclipse.incquery.runtime.matchers.backend.IQueryBackend;
import org.eclipse.incquery.runtime.matchers.backend.IQueryBackendFactory;
import org.eclipse.incquery.runtime.matchers.backend.IQueryBackendHintProvider;
import org.eclipse.incquery.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.incquery.runtime.matchers.backend.IUpdateable;
import org.eclipse.incquery.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.incquery.runtime.matchers.context.IQueryCacheContext;
import org.eclipse.incquery.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.incquery.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQueries;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery.PQueryStatus;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * An IncQuery engine back-end (implementation)
 * 
 * @author Bergmann Gábor
 * 
 */
public class IncQueryEngineImpl extends AdvancedIncQueryEngine implements IQueryBackendHintProvider, IQueryCacheContext {
	
    /**
     * The engine manager responsible for this engine. Null if this engine is unmanaged.
     */
    private final IncQueryEngineManager manager;
    /**
     * The model to which the engine is attached.
     */
    private final IncQueryScope scope;
//    private final Notifier emfRoot;
    
    /**
     * The context of the engine, provided by the scope.
     */
    private IEngineContext engineContext;
    
    /**
     * Query evaluation options registered for each query. Must have non-null fields if stored here.
     */
    private final Map<PQuery, QueryEvaluationHint> hints
    		= Maps.newHashMap();    
    
    /**
     * Initialized matchers for each query
     */
    private final Map<IQuerySpecification<? extends IncQueryMatcher<?>>, IncQueryMatcher<?>> matchers
    		= Maps.newHashMap();
    
	/**
     * The RETE and other pattern matcher implementations of the IncQuery engine.
     */
    private volatile Map<Class<? extends IQueryBackend>, IQueryBackend> queryBackends 
 = Maps.newHashMap();
    
    private final LifecycleProvider lifecycleProvider;
    private final ModelUpdateProvider modelUpdateProvider;
    private Logger logger;
	private boolean disposed = false;
    
    
    /**
     * @param manager
     *            null if unmanaged
     * @param scope
     * @throws IncQueryException
     *             if the emf root is invalid
     */
    public IncQueryEngineImpl(IncQueryEngineManager manager, IncQueryScope scope) throws IncQueryException {
        super();
        this.manager = manager;
        this.scope = scope;
        this.lifecycleProvider = new LifecycleProvider(this, getLogger());
        this.modelUpdateProvider = new ModelUpdateProvider(this, getLogger());
        this.engineContext = scope.createEngineContext(this, taintListener, getLogger());
    }

    @Override
	public Notifier getEMFRoot() {
        return ((EMFScope)scope).getScopeRoot();
    }
    
    @Override
	public Set<? extends IncQueryMatcher<? extends IPatternMatch>> getCurrentMatchers(){
        return ImmutableSet.copyOf(matchers.values());
    }
    
    @Override
	public <Matcher extends IncQueryMatcher<? extends IPatternMatch>> Matcher getMatcher(IQuerySpecification<Matcher> querySpecification) throws IncQueryException {
        return querySpecification.getMatcher(this);
    }
    
    @Override
    public <Matcher extends IncQueryMatcher<? extends IPatternMatch>> Matcher getMatcher(
    		IQuerySpecification<Matcher> querySpecification,
    		QueryEvaluationHint optionalEvaluationHints) 
    	throws IncQueryException 
    {
    	overrideKnownHints(querySpecification.getInternalQueryRepresentation(), optionalEvaluationHints);
        return getMatcher(querySpecification);
    }

	@Override
	@SuppressWarnings("unchecked")
	public <Matcher extends IncQueryMatcher<? extends IPatternMatch>> Matcher getExistingMatcher(IQuerySpecification<Matcher> querySpecification) {
		return (Matcher) matchers.get(querySpecification);
	}
    
    @Override
    public IncQueryMatcher<? extends IPatternMatch> getMatcher(String patternFQN) throws IncQueryException {
        IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> querySpecification = QuerySpecificationRegistry
                .getQuerySpecification(patternFQN);
        if (querySpecification != null) {
            return getMatcher(querySpecification);
        } else {
            throw new IncQueryException(
                    String.format(
                            "No matcher could be constructed for the pattern with FQN %s; if the generated matcher class is not available, please access for the first time using getMatcher(IQuerySpecification)",
                            patternFQN), "No matcher could be constructed for given pattern FQN.");
        }
    }

    @Override
	public IBaseIndex getBaseIndex() throws IncQueryException {
        return engineContext.getBaseIndex();
    }

	public final Logger getLogger() {
        if (logger == null) {
            final int hash = System.identityHashCode(this);
            logger = Logger.getLogger(IncQueryLoggingUtil.getLogger(IncQueryEngine.class).getName() + "." + hash);
            if (logger == null)
                throw new AssertionError(
                        "Configuration error: unable to create IncQuery runtime logger for engine " + hash);
        }
        return logger;
    }
    
    ///////////////// internal stuff //////////////

    /**
     * Report when a pattern matcher has been completely initialized, so that it can be registered into the engine.
     * @param querySpecification the {@link IQuerySpecification} that corresponds to the matcher
     * @param matcher the {@link IncQueryMatcher} that has finished its initialization process
     * 
     * TODO make it package-only visible when implementation class is moved to impl package
     */
    public void reportMatcherInitialized(IQuerySpecification<?> querySpecification, IncQueryMatcher<?> matcher) {
        if(matchers.containsKey(querySpecification)) {
            // TODO simply dropping the matcher can cause problems
            logger.debug("Query " + 
                    querySpecification.getFullyQualifiedName() + 
                    " already initialized in IncQueryEngine!");
        } else {
            matchers.put(querySpecification, matcher);
            lifecycleProvider.matcherInstantiated(matcher);
        }
    }

    /**
     * Provides access to the selected query backend component of the IncQuery engine.
     */
    @Override
	public IQueryBackend getQueryBackend(Class<? extends IQueryBackend> backendClass) throws IncQueryException {
        initBackends();
        final IQueryBackend iQueryBackend = queryBackends.get(backendClass);
        if (iQueryBackend == null)
        	throw new IncQueryException("Query backend class not registered: " + backendClass.getName(), "Unknown query backend.");
		return iQueryBackend;
    }

	private void initBackends() throws IncQueryException {
		synchronized (this) {
            final Iterable<Entry<Class<? extends IQueryBackend>, IQueryBackendFactory>> factories = Iterables.filter(
                    QueryBackendRegistry.getInstance().getAllKnownFactories(),
                    new Predicate<Entry<Class<? extends IQueryBackend>, IQueryBackendFactory>>() {

                        @Override
                        public boolean apply(Entry<Class<? extends IQueryBackend>, IQueryBackendFactory> input) {
                            return !queryBackends.containsKey(input.getKey());
                        }
                    });
            if (factories.iterator().hasNext()) {
		    	boolean initialized = false;
		    	try {
		    		engineContext.initializeBackends(new IQueryBackendInitializer() {
		    			@Override
		    			public void initializeWith(IQueryRuntimeContext runtimeContext) {
		    				queryBackends = Maps.newHashMap();
                            for (Entry<Class<? extends IQueryBackend>, IQueryBackendFactory> factoryEntry : factories) {
		    					IQueryBackend backend = factoryEntry.getValue().create(logger, runtimeContext, IncQueryEngineImpl.this, IncQueryEngineImpl.this);
		    					queryBackends.put(factoryEntry.getKey(), backend);
		    				}
		    			}
		    		});
		    		initialized = true;
		    	} finally {
		    		if (!initialized) 
		    			queryBackends = null;
		    	}
		    }
		}
	}

    ///////////////// advanced stuff /////////////
    
    @Override
    public void dispose() {
        if (manager != null) {
        	throw new UnsupportedOperationException(
        			String.format("Cannot dispose() managed IncQuery engine. Attempted for scope %s.", scope));
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
        			String.format("Cannot wipe() managed IncQuery engine. Attempted for scope %s.", scope));
        }
        // TODO generalize for each query backend
        if (queryBackends != null) {
        	for (IQueryBackend backend : queryBackends.values()) {
				backend.dispose();
			}
            queryBackends = null;
        }
        hints.clear();
        matchers.clear();
        lifecycleProvider.engineWiped();
    }

    
    
    /**
     * Indicates whether the engine is in a tainted, inconsistent state.
     */
    private boolean tainted = false;
    private IIndexingErrorListener taintListener = new SelfTaintListener(this);

    private static class SelfTaintListener implements IIndexingErrorListener {
        WeakReference<IncQueryEngineImpl> iqEngRef;

        public SelfTaintListener(IncQueryEngineImpl iqEngine) {
            this.iqEngRef = new WeakReference<IncQueryEngineImpl>(iqEngine);
        }

        public void engineBecameTainted(String description, Throwable t) {
            final IncQueryEngineImpl iqEngine = iqEngRef.get();
            if (iqEngine != null) {
                iqEngine.tainted = true;
                iqEngine.lifecycleProvider.engineBecameTainted(description, t);
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
	public <Match extends IPatternMatch> void addMatchUpdateListener(final IncQueryMatcher<Match> matcher,
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
	public <Match extends IPatternMatch> void removeMatchUpdateListener(IncQueryMatcher<Match> matcher,
            IMatchUpdateListener<? super Match> listener) {
        checkArgument(listener != null, "Cannot remove null listener!");
        checkArgument(matcher.getEngine() == this, "Cannot remove listener from matcher of different engine!");
        checkArgument(!disposed, "Cannot remove listener from matcher of disposed engine!");
        //((BaseMatcher<Match>)matcher).removeCallbackOnMatchUpdate(listener);
        
        final BaseMatcher<Match> bm = (BaseMatcher<Match>)matcher;
        
        try {
            IQueryResultProvider resultProvider = getUnderlyingResultProvider(bm);
            resultProvider.removeUpdateListener(listener);
        } catch (Exception e) {
            logger.error("Error while removing listener " + listener + " from the matcher of " + matcher.getPatternName(), e);
        }
    }
    
    @Override
	public void addModelUpdateListener(IncQueryModelUpdateListener listener) {
        modelUpdateProvider.addListener(listener);
    }
    
    @Override
	public void removeModelUpdateListener(IncQueryModelUpdateListener listener) {
        modelUpdateProvider.removeListener(listener);
    }
    
    @Override
	public void addLifecycleListener(IncQueryEngineLifecycleListener listener) {
        lifecycleProvider.addListener(listener);
    }
    
    @Override
	public void removeLifecycleListener(IncQueryEngineLifecycleListener listener) {
        lifecycleProvider.removeListener(listener);
    }
	
    /**
     * Returns an internal interface towards the query backend to feed the matcher with results. 
     * 
     * @param query the pattern for which the result provider should be delivered
     * 
     * @throws QueryProcessingException
     * @throws IncQueryException
     */
	public IQueryResultProvider getResultProvider(IQuerySpecification<?> query) 
		throws QueryProcessingException, IncQueryException 
	{
		Preconditions.checkState(!disposed, "Cannot evaluate query on disposed engine!");
        
		return getResultProviderInternal(query);
	}

	private IQueryResultProvider getResultProviderInternal(IQuerySpecification<?> query) 
		throws QueryProcessingException, IncQueryException 
	{
		final IQueryBackend backend = getQueryBackend(query.getInternalQueryRepresentation());
		return backend.getResultProvider(query.getInternalQueryRepresentation());
	}
	
	/**
	 * Returns the query backend (influenced by the hint system), even if it is a non-caching backend.
	 */
	private IQueryBackend getQueryBackend(PQuery query) throws IncQueryException {
		return getQueryBackend(getCurrentHint(query).getQueryBackendClass());
	}
	/**
	 * Returns a caching query backend (influenced by the hint system).
	 */
	private IQueryBackend getCachingQueryBackend(PQuery query) throws IncQueryException {
		IQueryBackend regularBackend = getQueryBackend(query);
		if (regularBackend.isCaching()) 
			return regularBackend; 
		else
			return getQueryBackend(QueryBackendRegistry.getInstance().getDefaultCachingBackendClass());
	}
	
	@Override
	public boolean isResultCached(PQuery query) {
		try {
			return null != getCachingQueryBackend(query).peekExistingResultProvider(query);
		} catch (IncQueryException iqe)  {
			getLogger().error("Error while accessing query evaluator backend", iqe);
			return false;
		}
	}
	
	@Override
	public IQueryResultProvider getCachingResultProvider(PQuery query) throws QueryProcessingException {
		try {
			return getCachingQueryBackend(query).getResultProvider(query);
		} catch (IncQueryException iqe)  {
			getLogger().error("Error while accessing query evaluator backend", iqe);
			throw new QueryProcessingException(
					"Error while attempting to consult caching query evaluator backend for evaluating query {1}", 
					new String[] {query.getFullyQualifiedName()}, 
					"Error while accessing query evaluator backends", 
					query, iqe);
		}
	}
	
	
	@Override
	public Map<String, Object> getHints(PQuery query) {
		return getCurrentHint(query).getBackendHints();
	}

	/**
	 * @return hint with non-null fields
	 */
	private QueryEvaluationHint getCurrentHint(PQuery query) {
		QueryEvaluationHint hint = hints.get(query);
		if (hint == null) {
			// global default
			hint = new QueryEvaluationHint(QueryBackendRegistry.getInstance().getDefaultBackendClass(), new HashMap<String, Object>());
			hints.put(query, hint);
			
			// overrides provided in query specification
			QueryEvaluationHint providedHint = query.getEvaluationHints();
			if (providedHint != null) 
				hint = overrideKnownHints(query, providedHint);
		}
		return hint;
	}
			
	/**
	 * @pre current hint exists with non-null fields
	 */
	private QueryEvaluationHint overrideKnownHints(
			PQuery query, 
			QueryEvaluationHint overridingHint) 
	{
		final QueryEvaluationHint currentHint = getCurrentHint(query);
		if (overridingHint == null)
			return currentHint;
		
		Class<? extends IQueryBackend> queryBackendClass = 
				currentHint.getQueryBackendClass();
		if (overridingHint.getQueryBackendClass() != null)
			queryBackendClass = overridingHint.getQueryBackendClass();
					
		Map<String, Object> backendHints = 
				new HashMap<String, Object>(currentHint.getBackendHints());
		if (overridingHint.getBackendHints() != null)
			backendHints.putAll(overridingHint.getBackendHints());
		
		QueryEvaluationHint consolidatendHint = 
				new QueryEvaluationHint(queryBackendClass, backendHints);
		hints.put(query, consolidatendHint);
		return consolidatendHint;
      
	}
	
	@Override
	public void prepareGroup(IQueryGroup queryGroup,
			final QueryEvaluationHint optionalEvaluationHints) throws IncQueryException 
	{
        try {
    		Preconditions.checkState(!disposed, "Cannot evaluate query on disposed engine!");
    		
    		final Set<IQuerySpecification<?>> specifications = new HashSet<IQuerySpecification<?>>(queryGroup.getSpecifications());
            final Collection<PQuery> patterns = Collections2.transform(specifications, new Function<IQuerySpecification, PQuery>() {
            	@Override
            	public PQuery apply(IQuerySpecification input) {
            		return input.getInternalQueryRepresentation();
            	}
            });
            Collection<String> uninitializedPatterns = Collections2.transform(
                    Collections2.filter(patterns, PQueries.queryStatusPredicate(PQueryStatus.UNINITIALIZED)),
                    PQueries.queryNameFunction());
            Preconditions.checkState(uninitializedPatterns.isEmpty(), "Uninitialized query(s) found: %s", Joiner.on(", ")
                    .join(uninitializedPatterns));
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
   							overrideKnownHints(query.getInternalQueryRepresentation(), optionalEvaluationHints);
    					}
    					for (IQuerySpecification<?> query : specifications) {
    						getResultProviderInternal(query);
    					}
    					return null;
    				}
    			});
            } catch (InvocationTargetException ex) {
                final Throwable cause = ex.getCause();
                if (cause instanceof QueryProcessingException)
                    throw (QueryProcessingException) cause;
                if (cause instanceof IncQueryException)
                    throw (IncQueryException) cause;
                if (cause instanceof RuntimeException)
                    throw (RuntimeException) cause;
                assert (false);
            }
        } catch (QueryProcessingException e) {
            throw new IncQueryException(e);
        }
	}
	

	@Override
	public IncQueryScope getScope() {
		return scope;
	}

    // /**
    // * EXPERIMENTAL: Creates an IncQuery engine that executes post-commit, or retrieves an already existing one.
    // * @param emfRoot the EMF root where this engine should operate
    // * @param reteThreads experimental feature; 0 is recommended
    // * @return a new or previously existing engine
    // * @throws IncQueryRuntimeException
    // */
    // public ReteEngine<String> getReteEngine(final TransactionalEditingDomain editingDomain, int reteThreads) throws
    // IncQueryRuntimeException {
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
