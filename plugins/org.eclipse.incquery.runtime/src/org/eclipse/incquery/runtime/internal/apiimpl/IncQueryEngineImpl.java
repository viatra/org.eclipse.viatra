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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.api.IMatchUpdateListener;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryEngineLifecycleListener;
import org.eclipse.incquery.runtime.api.IncQueryEngineManager;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.api.IncQueryModelUpdateListener;
import org.eclipse.incquery.runtime.api.impl.BaseMatcher;
import org.eclipse.incquery.runtime.api.scope.IBaseIndex;
import org.eclipse.incquery.runtime.api.scope.IEngineContext;
import org.eclipse.incquery.runtime.api.scope.IncQueryScope;
import org.eclipse.incquery.runtime.base.api.IIndexingErrorListener;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.QuerySpecificationRegistry;
import org.eclipse.incquery.runtime.internal.engine.LifecycleProvider;
import org.eclipse.incquery.runtime.internal.engine.ModelUpdateProvider;
import org.eclipse.incquery.runtime.matchers.backend.IQueryBackend;
import org.eclipse.incquery.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.incquery.runtime.matchers.backend.IUpdateable;
import org.eclipse.incquery.runtime.matchers.planning.QueryPlannerException;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.construction.plancompiler.ReteRecipeCompiler;
import org.eclipse.incquery.runtime.rete.matcher.IPatternMatcherRuntimeContext;
import org.eclipse.incquery.runtime.rete.matcher.ReteEngine;
import org.eclipse.incquery.runtime.rete.util.Options;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * An EMF-IncQuery engine back-end (implementation)
 * 
 * @author Bergmann Gábor
 * 
 */
public class IncQueryEngineImpl extends AdvancedIncQueryEngine {
	
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
     * Initialized matchers for each query
     */
    private final Map<IQuerySpecification<? extends IncQueryMatcher<?>>, IncQueryMatcher<?>> matchers
    		= Maps.newHashMap();
    
//    /**
//     * The base index keeping track of basic EMF contents of the model.
//     */
//    private NavigationHelper baseIndex;
//    /**
//     * Whether to initialize the base index in wildcard mode.
//     * Whether to initialize the base index in dynamic EMF mode.
//     */
//	private final BaseIndexOptions options;
	/**
     * The RETE pattern matcher component of the IncQuery engine.
     */
    private IQueryBackend reteEngine = null;

    private final LifecycleProvider lifecycleProvider;
    private final ModelUpdateProvider modelUpdateProvider;
    private Logger logger;
	private boolean disposed = false;
    
    /**
     * EXPERIMENTAL
     */
    private final int reteThreads = 0;
    
    /**
     * @param manager
     *            null if unmanaged
     * @param emfRoot
     * @throws IncQueryException
     *             if the emf root is invalid
     */
    public IncQueryEngineImpl(IncQueryEngineManager manager, IncQueryScope scope) throws IncQueryException {
        super();
        this.manager = manager;
        this.scope = scope;
        this.lifecycleProvider = new LifecycleProvider(this, getLogger());
        this.modelUpdateProvider = new ModelUpdateProvider(this, getLogger());
        this.engineContext = scope.createEngineContext(this, getLogger());
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
     * Provides access to the internal RETE pattern matcher component of the IncQuery engine.
     * 
     * @noreference A typical user would not need to call this method.
     * TODO make it package visible only
     */
    @Override
	public IQueryBackend getReteEngine() throws IncQueryException {
        if (reteEngine == null) {
        	engineContext.withoutBaseIndexInitializationDo(new Runnable() {
				@Override
				public void run() {
		            synchronized (this) {
		                reteEngine = buildReteEngineInternal(engineContext.getRuntimeContext());
		            }
				}
			});
        }
        return reteEngine;

    }

    

    private IQueryBackend buildReteEngineInternal(IPatternMatcherRuntimeContext context) {
        ReteEngine engine;
        engine = new ReteEngine(context, reteThreads);
        ReteRecipeCompiler compiler = new ReteRecipeCompiler(Options.builderMethod.layoutStrategy(), context);
        //EPMBuilder builder = new EPMBuilder(buildable, context);
        engine.setCompiler(compiler);
        return engine;
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
        if (reteEngine != null) {
            reteEngine.dispose();
            reteEngine = null;
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
			final BaseMatcher<Match> matcher) throws QueryPlannerException {
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
        
        try {
            IQueryResultProvider resultProvider = getUnderlyingResultProvider(bm);
            resultProvider.addUpdateListener(updateDispatcher, listener, fireNow);
        } catch (QueryPlannerException e) {
            logger.error("Error while adding listener " + listener + " to the matcher of " + matcher.getPatternName(), e);
        }
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
     * @throws QueryPlannerException
     * @throws IncQueryException
     */
	public IQueryResultProvider getResultProvider(PQuery query)
			throws QueryPlannerException, IncQueryException 
	{
        checkArgument(!disposed, "Cannot evaluate query on disposed engine!");
        
		// TODO: there could be different ways for selecting a backend for this query
		final IQueryBackend backend = getReteEngine();
		
		return backend.getResultProvider(query);
	}

	/**
	 * Prepares backends for providing results for the given queries.
	 * @param patterns a set of patterns to prepare at once
	 * @throws IncQueryException 
	 * @throws QueryPlannerException 
	 */
	public void prepareBackendsCoalesced(final Set<PQuery> patterns) throws QueryPlannerException, IncQueryException {
		// TODO maybe do some smarter preparation per backend?
        try {
			engineContext.getBaseIndex().coalesceTraversals(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					for (PQuery pQuery : patterns) {
						getResultProvider(pQuery);
					}
					return null;
				}
			});
        } catch (InvocationTargetException ex) {
            final Throwable cause = ex.getCause();
            if (cause instanceof QueryPlannerException)
                throw (QueryPlannerException) cause;
            if (cause instanceof IncQueryException)
                throw (IncQueryException) cause;
            if (cause instanceof RuntimeException)
                throw (RuntimeException) cause;
            assert (false);
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
