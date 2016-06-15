/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategy;
import org.eclipse.viatra.dse.designspace.api.IDesignSpace;
import org.eclipse.viatra.dse.multithreading.DSEThreadPool;
import org.eclipse.viatra.dse.objectives.IGlobalConstraint;
import org.eclipse.viatra.dse.objectives.IObjective;
import org.eclipse.viatra.dse.objectives.LeveledObjectivesHelper;
import org.eclipse.viatra.dse.solutionstore.SolutionStore;
import org.eclipse.viatra.dse.statecode.IStateCoderFactory;
import org.eclipse.viatra.dse.util.EMFHelper;
import org.eclipse.viatra.dse.visualizer.IDesignSpaceVisualizer;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.resolver.ConflictResolver;
import org.eclipse.viatra.transformation.evm.specific.ConflictResolvers;
import org.eclipse.viatra.transformation.runtime.emf.rules.batch.BatchTransformationRule;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * Creates new contexts for strategies. It is needed because of the multithreading.
 * 
 * @author Andras Szabolcs Nagy
 * 
 */
public class GlobalContext {

    // **** fields and methods for multi threading *****//
    // *************************************************//

    public enum ExplorationProcessState {
        NOT_STARTED,
        RUNNING,
        STOPPING,
        COMPLETED
    }

    private ConcurrentLinkedQueue<Throwable> exceptions = new ConcurrentLinkedQueue<Throwable>();

    private volatile ExplorationProcessState state = ExplorationProcessState.NOT_STARTED;
    private final Set<ExplorerThread> runningThreads = new HashSet<ExplorerThread>();
    private DSEThreadPool threadPool = new DSEThreadPool();
    private int numberOfStartedThreads = 0;
    private IDesignSpace designSpace;

    private AtomicBoolean firstThreadContextInited = new AtomicBoolean(false);
    private AtomicBoolean firstThreadContextIniting = new AtomicBoolean(false);

    private Map<RuleSpecification<?>, BatchTransformationRule<?,?>> specificationRuleMap;

    /**
     * The DesignSpaceExplorer's thread.
     */
    private final Thread mainThread;

    public GlobalContext() {
        mainThread = Thread.currentThread();
    }

    private Logger logger = Logger.getLogger(this.getClass());

    private boolean isAlreadyInited;

    /**
     * Starts a new thread to explore the design space.
     * 
     * @param originalThreadContext The context of the thread which starts the new thread.
     * @param model The model to start from.
     * @param cloneModel It should be true in most cases.
     * @param strategy The strategy, the thread will use.
     * @return The {@link ExplorerThread}
     */
    private synchronized ExplorerThread tryStartNewThread(ThreadContext originalThreadContext, Notifier model,
            IStrategy strategy) {
        
        if(!isAlreadyInited) {
            isAlreadyInited = true;
            init();
        }

        if (state != ExplorationProcessState.COMPLETED && state != ExplorationProcessState.STOPPING
                && threadPool.canStartNewThread()) {

            ThreadContext newThreadContext = new ThreadContext(this, strategy, model);

            // TODO : clone undo list? slave strategy can't go further back...
            ExplorerThread explorerThread = new ExplorerThread(newThreadContext);
            newThreadContext.setExplorerThread(explorerThread);

            boolean isSuccessful = threadPool.tryStartNewStrategy(explorerThread);

            if (isSuccessful) {
                runningThreads.add(explorerThread);

                state = ExplorationProcessState.RUNNING;
                ++numberOfStartedThreads;

                if (logger.isDebugEnabled()) {
                    logger.debug("New worker started, active workers: " + runningThreads.size());
                }

                return explorerThread;
            }
        }
        return null;
    }

    public synchronized ExplorerThread tryStartNewThread(ThreadContext originalThreadContext, IStrategy strategy) {
        return tryStartNewThread(originalThreadContext, EMFHelper.clone(originalThreadContext.getModel()), strategy);
    }

    public synchronized ExplorerThread tryStartNewThreadWithoutModelClone(ThreadContext originalThreadContext,
            IStrategy strategy) {
        return tryStartNewThread(originalThreadContext, originalThreadContext.getModel(), strategy);
    }

    public synchronized ExplorerThread startFirstThread(IStrategy strategy, Notifier model) {
        Preconditions.checkState(!isAlreadyInited, "First thread is already started.");
        return tryStartNewThread(null, EMFHelper.clone(model), strategy);
    }

    public synchronized ExplorerThread startFirstThreadWithoutModelClone(IStrategy strategy, Notifier model) {
        Preconditions.checkState(!isAlreadyInited, "First thread is already started.");
        return tryStartNewThread(null, model, strategy);
    }

    /**
     * Starts a new thread to explore the design space.
     * 
     * @param strategyBase
     *            The {@link Strategy}.
     * @param tedToClone
     *            The model to clone. Hint: context.getTed()
     * @return The newly created {@link ExplorerThread}. Null if the number of the current strategies reached their
     *         maximum.
     */

    public synchronized void strategyFinished(ExplorerThread strategy) {
        runningThreads.remove(strategy);

        if (logger.isDebugEnabled()) {
            logger.debug("Worker finished, active workers: " + runningThreads.size());
        }

        // is the first part necessary?
        if (runningThreads.size() == 0) {
            state = ExplorationProcessState.COMPLETED;
            threadPool.shutdown();

            // if the main thread (which started the exploration)
            // is waiting for the solution, than wake it up
            synchronized (mainThread) {
                mainThread.notify();
            }

        }
    }

    public synchronized boolean isDone() {
        return state == ExplorationProcessState.COMPLETED && runningThreads.size() == 0;
    }

    public boolean canStartNewThread() {
        return (state == ExplorationProcessState.NOT_STARTED || state == ExplorationProcessState.RUNNING)
                && threadPool.canStartNewThread();
    }

    public synchronized void stopAllThreads() {
        if (state == ExplorationProcessState.RUNNING) {
            state = ExplorationProcessState.STOPPING;
            logger.debug("Stopping all threads.");
            for (ExplorerThread strategy : runningThreads) {
                strategy.stopRunning();
            }
        }
    }

    public void registerException(Throwable e) {
        exceptions.add(e);
    }

    // ******* fields and methods for exploration *******//
    // **************************************************//

    private List<IObjective> objectives = new ArrayList<IObjective>();
    private IObjective[][] leveledObjectives;
    private List<IGlobalConstraint> globalConstraints = new ArrayList<IGlobalConstraint>();
    private Set<BatchTransformationRule<?, ?>> transformations = new HashSet<BatchTransformationRule<?, ?>>();
    private IStateCoderFactory stateCoderFactory;
    private SolutionStore solutionStore = new SolutionStore(1);
    private Object SharedObject;
    private List<IDesignSpaceVisualizer> visualizers;

    private ConflictResolver conflictResolver = ConflictResolvers.createArbitraryResolver();

    private void init() {
        leveledObjectives = new LeveledObjectivesHelper(objectives).initLeveledObjectives();
        
        specificationRuleMap = new HashMap<>();
        for (BatchTransformationRule<?,?> rule : transformations) {
            specificationRuleMap.put(rule.getRuleSpecification(), rule);
        }
    }

    public List<IDesignSpaceVisualizer> getVisualizers() {
        return ImmutableList.copyOf(visualizers);
    }

    public void registerDesignSpaceVisualizer(IDesignSpaceVisualizer visualizer) {
        if (visualizer == null) {
            return;
        }
        if (visualizers == null) {
            visualizers = new ArrayList<IDesignSpaceVisualizer>();
        }
        visualizers.add(visualizer);
    }

    public void deregisterDesignSpaceVisualizer(IDesignSpaceVisualizer visualizer) {
        if (visualizer == null) {
            return;
        }
        if (visualizers != null) {
            visualizers.remove(visualizer);
        }
    }

    public boolean isDesignSpaceVisualizerRegistered(IDesignSpaceVisualizer visualizer) {
        if (visualizers != null) {
            return visualizers.contains(visualizer);
        }
        return false;
    }

    public void initVisualizersForThread(ThreadContext threadContext) {
        if (visualizers != null && !visualizers.isEmpty()) {
            for (IDesignSpaceVisualizer visualizer : visualizers) {
                visualizer.init(threadContext);
                threadContext.getDesignSpaceManager().registerExploreEventHandler(visualizer);
            }
        }
    }

    public boolean isExceptionHappendInOtherThread() {
        return !exceptions.isEmpty();
    }

    public Collection<Throwable> getExceptions() {
        return exceptions;
    }

    public IStateCoderFactory getStateCoderFactory() {
        return stateCoderFactory;
    }

    public void setStateCoderFactory(IStateCoderFactory stateCoderFactory) {
        this.stateCoderFactory = stateCoderFactory;
    }

    public Set<BatchTransformationRule<?, ?>> getTransformations() {
        return transformations;
    }

    public void setTransformations(Set<BatchTransformationRule<?, ?>> transformations) {
        this.transformations = transformations;
    }

    public DSEThreadPool getThreadPool() {
        return threadPool;
    }

    public IDesignSpace getDesignSpace() {
        return designSpace;
    }

    public void setDesignSpace(IDesignSpace designSpace) {
        this.designSpace = designSpace;
    }

    public int getNumberOfStartedThreads() {
        return numberOfStartedThreads;
    }

    public Object getSharedObject() {
        return SharedObject;
    }

    public void setSharedObject(Object sharedObject) {
        SharedObject = sharedObject;
    }

    public ExplorationProcessState getState() {
        return state;
    }

    public List<IObjective> getObjectives() {
        return objectives;
    }

    public void setObjectives(List<IObjective> objectives) {
        this.objectives = objectives;
    }

    public List<IGlobalConstraint> getGlobalConstraints() {
        return globalConstraints;
    }

    public void setGlobalConstraints(List<IGlobalConstraint> globalConstraints) {
        this.globalConstraints = globalConstraints;
    }

    AtomicBoolean getFirstThreadContextInited() {
        return firstThreadContextInited;
    }

    AtomicBoolean getFirstThreadContextIniting() {
        return firstThreadContextIniting;
    }

    public IObjective[][] getLeveledObjectives() {
        return leveledObjectives;
    }

    public void setSolutionStore(SolutionStore solutionStore) {
        this.solutionStore = solutionStore;
    }

    public SolutionStore getSolutionStore() {
        return solutionStore;
    }

    public Map<RuleSpecification<?>, BatchTransformationRule<?, ?>> getSpecificationRuleMap() {
        return specificationRuleMap;
    }

    public void setConflictResolver(ConflictResolver conflictResolver) {
        this.conflictResolver = conflictResolver;
    }

    public ConflictResolver getConflictResolver() {
        return conflictResolver;
    }
}
