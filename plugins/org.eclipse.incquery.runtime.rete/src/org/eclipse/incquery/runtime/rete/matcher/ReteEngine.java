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

package org.eclipse.incquery.runtime.rete.matcher;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.incquery.runtime.matchers.backend.IQueryBackend;
import org.eclipse.incquery.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.incquery.runtime.matchers.context.IPatternMatcherRuntimeContext;
import org.eclipse.incquery.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.incquery.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.incquery.runtime.matchers.tuple.TupleMask;
import org.eclipse.incquery.runtime.matchers.util.CollectionsFactory;
import org.eclipse.incquery.runtime.rete.boundary.Disconnectable;
import org.eclipse.incquery.runtime.rete.boundary.ReteBoundary;
import org.eclipse.incquery.runtime.rete.construction.RetePatternBuildException;
import org.eclipse.incquery.runtime.rete.construction.plancompiler.ReteRecipeCompiler;
import org.eclipse.incquery.runtime.rete.index.Indexer;
import org.eclipse.incquery.runtime.rete.network.Network;
import org.eclipse.incquery.runtime.rete.network.NodeProvisioner;
import org.eclipse.incquery.runtime.rete.traceability.RecipeTraceInfo;

/**
 * @author Gabor Bergmann
 *
 */
public class ReteEngine implements IQueryBackend {

    protected Network reteNet;
    protected final int reteThreads;
    protected ReteBoundary boundary;

    protected IPatternMatcherRuntimeContext context;
    protected IQueryRuntimeContext runtimeContext;

    protected Collection<Disconnectable> disconnectables;
//    protected IPredicateTraceListener traceListener;
    // protected MachineListener machineListener;

    protected Map<PQuery, RetePatternMatcher> matchers;
    // protected Map<GTPattern, Map<Map<Integer, Scope>, RetePatternMatcher>> matchersScoped; // (pattern, scopemap) ->
    // matcher

    protected ReteRecipeCompiler compiler;

    protected final boolean parallelExecutionEnabled; // TRUE if model manipulation can go on

    private boolean disposedOrUninitialized = true;

    // while RETE does its job.

    // protected BlockingQueue<Throwable> caughtExceptions;

    /**
     * @param context
     *            the context of the pattern matcher, conveying all information from the outside world.
     * @param reteThreads
     *            the number of threads to operate the RETE network with; 0 means single-threaded operation, 1 starts an
     *            asynchronous thread to operate the RETE net, >1 uses multiple RETE containers.
     */
    public ReteEngine(IPatternMatcherRuntimeContext context, IQueryRuntimeContext runtimeContext, int reteThreads) {
        super();
        this.context = context;
		this.runtimeContext = runtimeContext;
        this.reteThreads = reteThreads;
        this.parallelExecutionEnabled = reteThreads > 0;
        // this.framework = new WeakReference<IFramework>(context.getFramework());

        initEngine();

        this.compiler = null;
    }

    /**
     * initializes engine components
     */
    synchronized private void initEngine() {
    	this.disposedOrUninitialized = false;
        this.disconnectables = new LinkedList<Disconnectable>();
        // this.caughtExceptions = new LinkedBlockingQueue<Throwable>();

        this.reteNet = new Network(reteThreads, this);
        this.boundary = new ReteBoundary(this); // prerequisite: network

        this.matchers = //new HashMap<PatternDescription, RetePatternMatcher>();
                CollectionsFactory.getMap();
        /* this.matchersScoped = new HashMap<PatternDescription, Map<Map<Integer,Scope>,RetePatternMatcher>>(); */

        // prerequisite: network, framework, boundary, disconnectables
        //context.subscribeBackendForUpdates(this.boundary);
        // prerequisite: boundary, disconnectables
//        this.traceListener = context.subscribePatternMatcherForTraceInfluences(this);

    }

    /**
     * deconstructs engine components
     */
    synchronized private void deconstructEngine() {
    	ensureInitialized();
        reteNet.kill();

        //context.unSubscribeBackendFromUpdates(this.boundary);
        for (Disconnectable disc : disconnectables) {
            disc.disconnect();
        }

        this.matchers = null;
        this.disconnectables = null;

        this.reteNet = null;
        this.boundary = null;

        // this.machineListener = new MachineListener(this); // prerequisite:
        // framework, disconnectables
//        this.traceListener = null;

    	this.disposedOrUninitialized = true;
    }

    /**
     * Deconstructs the engine to get rid of it finally
     */
    public void killEngine() {
        deconstructEngine();
        // this.framework = null;
        this.compiler = null;
        this.context = null;
    }

    /**
     * Resets the engine to an after-initialization phase
     *
     */
    public void reset() {
        deconstructEngine();

        initEngine();

        compiler.reset();
    }

    /**
     * Accesses the patternmatcher for a given pattern, constructs one if a matcher is not available yet.
     *
     * @pre: builder is set.
     * @param query
     *            the pattern to be matched.
     * @return a patternmatcher object that can match occurences of the given pattern.
     * @throws RetePatternBuildException
     *             if construction fails.
     */
    public synchronized RetePatternMatcher accessMatcher(final PQuery query)
            throws QueryProcessingException {
    	ensureInitialized();
    	RetePatternMatcher matcher;
        // String namespace = gtPattern.getNamespace().getName();
        // String name = gtPattern.getName();
        // String fqn = namespace + "." + name;
        matcher = matchers.get(query);
        if (matcher == null) {
            constructionWrapper(new Callable<Void>() {
        		@Override
        		public Void call() throws QueryProcessingException {
        			RecipeTraceInfo prodNode;
        			prodNode = boundary.accessProductionTrace(query);

        			RetePatternMatcher retePatternMatcher = new RetePatternMatcher(ReteEngine.this,
        					prodNode);
        			retePatternMatcher.setTag(query);
        			matchers.put(query, retePatternMatcher);
        			return null;
        		}
        	});
            matcher = matchers.get(query);
        }

        return matcher;
    }

    
    /**
     * Constructs RETE pattern matchers for a collection of patterns, if they are not available yet. Model traversal
     * during the whole construction period is coalesced (which may have an effect on performance, depending on the
     * matcher context).
     *
     * @pre: builder is set.
     * @param specifications
     *            the patterns to be matched.
     * @throws RetePatternBuildException
     *             if construction fails.
     */
    public synchronized void buildMatchersCoalesced(final Collection<PQuery> specifications)
            throws QueryProcessingException {
    	ensureInitialized();
    	constructionWrapper(new Callable<Void>() {
    		@Override
    		public Void call() throws QueryProcessingException {
    			for (PQuery specification : specifications) {
    			    boundary.accessProductionNode(specification);
    			}
    			return null;
    		}
    	});
    }

	private void constructionWrapper(final Callable<Void> payload)
			throws RetePatternBuildException {
		context.modelReadLock();
		    try {
		        if (parallelExecutionEnabled)
		            reteNet.getStructuralChangeLock().lock();
		        try {
		            try {
						context.coalesceTraversals(payload);
		            } catch (InvocationTargetException ex) {
		                final Throwable cause = ex.getCause();
		                if (cause instanceof RetePatternBuildException)
		                    throw (RetePatternBuildException) cause;
		                if (cause instanceof RuntimeException)
		                    throw (RuntimeException) cause;
		                assert (false);
		            }
		        } finally {
		           if (parallelExecutionEnabled)
		                reteNet.getStructuralChangeLock().unlock();
		           reteNet.waitForReteTermination();
		        }
		    } finally {
		        context.modelReadUnLock();
		    }
	}

    // /**
    // * Accesses the patternmatcher for a given pattern with additional scoping, constructs one if
    // * a matcher is not available yet.
    // *
    // * @param gtPattern
    // * the pattern to be matched.
    // * @param additionalScopeMap
    // * additional, optional scopes for the symbolic parameters
    // * maps the position of the symbolic parameter to its additional scope (if any)
    // * @pre: scope.parent is non-root, i.e. this is a nontrivial constraint
    // * use the static method RetePatternMatcher.buildAdditionalScopeMap() to create from PatternCallSignature
    // * @return a patternmatcher object that can match occurences of the given
    // * pattern.
    // * @throws PatternMatcherCompileTimeException
    // * if construction fails.
    // */
    // public synchronized RetePatternMatcher accessMatcherScoped(PatternDescription gtPattern, Map<Integer, Scope>
    // additionalScopeMap)
    // throws PatternMatcherCompileTimeException {
    // if (additionalScopeMap.isEmpty()) return accessMatcher(gtPattern);
    //
    // RetePatternMatcher matcher;
    //
    // Map<Map<Integer, Scope>, RetePatternMatcher> scopes = matchersScoped.get(gtPattern);
    // if (scopes == null) {
    // scopes = new HashMap<Map<Integer, Scope>, RetePatternMatcher>();
    // matchersScoped.put(gtPattern, scopes);
    // }
    //
    // matcher = scopes.get(additionalScopeMap);
    // if (matcher == null) {
    // context.modelReadLock();
    // try {
    // reteNet.getStructuralChangeLock().lock();
    // try {
    // Address<? extends Production> prodNode;
    // prodNode = boundary.accessProductionScoped(gtPattern, additionalScopeMap);
    //
    // matcher = new RetePatternMatcher(this, prodNode);
    // scopes.put(additionalScopeMap, matcher);
    // } finally {
    // reteNet.getStructuralChangeLock().unlock();
    // }
    // } finally {
    // context.modelReadUnLock();
    // }
    // // reteNet.flushUpdates();
    // }
    //
    // return matcher;
    // }

    /**
     * Returns an indexer that groups the contents of this Production node by their projections to a given mask.
     * Designed to be called by a RetePatternMatcher.
     *
     * @param production
     *            the production node to be indexed.
     * @param mask
     *            the mask that defines the projection.
     * @return the Indexer.
     */
    synchronized Indexer accessProjection(RecipeTraceInfo production, TupleMask mask) {
    	ensureInitialized();
        NodeProvisioner nodeProvisioner = reteNet.getHeadContainer().getProvisioner();
        Indexer result = nodeProvisioner.peekProjectionIndexer(production, mask);
        if (result == null) {
            context.modelReadLock();
            try {
                if (parallelExecutionEnabled)
                    reteNet.getStructuralChangeLock().lock();
                try {
                    result = nodeProvisioner.accessProjectionIndexerOnetime(production, mask);
                } finally {
                    if (parallelExecutionEnabled)
                        reteNet.getStructuralChangeLock().unlock();
                }
            } finally {
                context.modelReadUnLock();
            }
        }

        return result;
    }

    // /**
    // * Retrieves the patternmatcher for a given pattern fqn, returns null if
    // the matching network hasn't been constructed yet.
    // *
    // * @param fqn the fully qualified name of the pattern to be matched.
    // * @return the previously constructed patternmatcher object that can match
    // occurences of the given pattern, or null if it doesn't exist.
    // */
    // public RetePatternMatcher getMatcher(String fqn)
    // {
    // RetePatternMatcher matcher = matchersByFqn.get(fqn);
    // if (matcher == null)
    // {
    // Production prodNode = boundary.getProduction(fqn);
    //
    // matcher = new RetePatternMatcher(this, prodNode);
    // matchersByFqn.put(fqn, matcher);
    // }
    //
    // return matcher;
    // }

    /**
     * Waits until the pattern matcher is in a steady state and output can be retrieved.
     */
    public void settle() {
    	ensureInitialized();
        reteNet.waitForReteTermination();
    }

    /**
     * Waits until the pattern matcher is in a steady state and output can be retrieved. When steady state is reached, a
     * retrieval action is executed before the steady state ceases.
     *
     * @param action
     *            the action to be run when reaching the steady-state.
     */
    public void settle(Runnable action) {
    	ensureInitialized();
        reteNet.waitForReteTermination(action);
    }

    // /**
    // * @return the framework
    // */
    // public IFramework getFramework() {
    // return framework.get();
    // }

    /**
     * @return the reteNet
     */
    public Network getReteNet() {
       ensureInitialized();
       return reteNet;
    }

    /**
     * @return the boundary
     */
    public ReteBoundary getBoundary() {
    	ensureInitialized();
        return boundary;
    }

    // /**
    // * @return the pattern matcher builder
    // */
    // public IRetePatternBuilder getBuilder() {
    // return builder;
    // }

    /**
     * @param builder
     *            the pattern matcher builder to set
     */
    public void setCompiler(ReteRecipeCompiler builder) {
    	ensureInitialized();
        this.compiler = builder;
    }

//    /**
//     * @return the manipulationListener
//     */
//    public IManipulationListener getManipulationListener() {
//    	ensureInitialized();
//       return manipulationListener;
//    }

//    /**
//     * @return the traceListener
//     */
//    public IPredicateTraceListener geTraceListener() {
//    	ensureInitialized();
//        return traceListener;
//    }

    /**
     * @param disc
     *            the new Disconnectable adapter.
     */
    public void addDisconnectable(Disconnectable disc) {
    	ensureInitialized();
        disconnectables.add(disc);
    }

    /**
     * @return the parallelExecutionEnabled
     */
    public boolean isParallelExecutionEnabled() {
        return parallelExecutionEnabled;
    }

    /**
     * @return the context
     */
    public IPatternMatcherRuntimeContext getContext() {
    	ensureInitialized();
        return context;
    }

    public IQueryRuntimeContext getRuntimeContext() {
    	ensureInitialized();
		return runtimeContext;
	}

	public ReteRecipeCompiler getCompiler() {
    	ensureInitialized();
       return compiler;
    }

    // /**
    // * For internal use only: logs exceptions occurring during term evaluation inside the RETE net.
    // * @param e
    // */
    // public void logEvaluatorException(Throwable e) {
    // try {
    // caughtExceptions.put(e);
    // } catch (InterruptedException e1) {
    // logEvaluatorException(e);
    // }
    // }
    // /**
    // * Polls the exceptions caught and logged during term evaluation by this RETE engine.
    // * Recommended usage: iterate polling until null is returned.
    // *
    // * @return the next caught exception, or null if there are no more.
    // */
    // public Throwable getNextLoggedEvaluatorException() {
    // return caughtExceptions.poll();
    // }

    void ensureInitialized() {
    	if (disposedOrUninitialized)
    		throw new IllegalStateException("Trying to use a Rete engine that has been disposed or has not yet been initialized.");

    }
    
    @Override
    public IQueryResultProvider getResultProvider(PQuery query) throws QueryProcessingException {
    	return accessMatcher(query);
    }

    @Override
    public IQueryResultProvider peekExistingResultProvider(PQuery query) {
    	ensureInitialized();
    	return matchers.get(query);
    }

	@Override
	public void dispose() {
		killEngine();
	}
	
	@Override
	public boolean isCaching() {
		return true;
	}

}
