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
package org.eclipse.viatra.dse.api.strategy;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.Solution;
import org.eclipse.viatra.dse.api.strategy.interfaces.INextTransition;
import org.eclipse.viatra.dse.api.strategy.interfaces.ISolutionFound;
import org.eclipse.viatra.dse.api.strategy.interfaces.ISolutionFound.ExecutationType;
import org.eclipse.viatra.dse.api.strategy.interfaces.IExplorerThread;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.GlobalContext;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.IState;
import org.eclipse.viatra.dse.designspace.api.IState.TraversalStateType;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.guidance.Guidance;
import org.eclipse.viatra.dse.guidance.ICriteria.EvaluationResult;
import org.eclipse.viatra.dse.monitor.PerformanceMonitorManager;

/**
 * 
 * @author Andras Szabolcs Nagy
 * 
 */
public class ExplorerThread implements IExplorerThread {

    private static final String STATE_EVALUATION = "stateEvaluation";
    private static final String FIRE_ACTIVATION_TIMER = "fireActivationTimer";
    private static final String GET_NEXT_TRANSITION_ID_TIMER = "getNextTransitionIdTimer";
    private static final String WALKER_CYCLE = "walkerCycle";

    private final ThreadContext threadContext;
    private GlobalContext globalContext;
    private final Strategy strategyBase;
    private final AtomicBoolean interrupted = new AtomicBoolean(false);

    private final Logger logger = Logger.getLogger(this.getClass());

    public ExplorerThread(final ThreadContext context) {
        this.threadContext = context;
        this.strategyBase = context.getStrategy();
    }

    /**
     * Makes the strategy (the thread) end it's last step, then exit.
     */
    @Override
    public void stopRunning() {
        interrupted.set(true);
    }

    /**
     * Starts the design space exploration. Returns only when {@link ISolutionFound#solutionFound(Strategy, Solution)}
     * method returns STOP or the {@link INextTransition#getNextTransition(ThreadContext)} method returns null.
     * 
     * If this main algorithm is not good for you, you can derive from this class and override this method. TODO:
     * strategy factory
     */
    @Override
    public void run() {
        try {

            // init is called here, not in the constructor, because of
            // performance
            // (initialization happens in the new thread)
            threadContext.init();

            globalContext = threadContext.getGlobalContext();

            strategyBase.initINextTransition(threadContext);

            boolean continueExecution = true;

            DesignSpaceManager designSpaceManager = threadContext.getDesignSpaceManager();
            Guidance guidance = threadContext.getGuidance();

            logger.debug("Strategy started with state: " + designSpaceManager.getCurrentState().getId());

            // do the exploration until {@link StrategyBase#solutionFound}
            // returns
            // stop, or interrupted from outside by Strategy#stopRunning
            mainloop: while (continueExecution) {
                PerformanceMonitorManager.endTimer(WALKER_CYCLE);

                if (interrupted.get()) {
                    strategyBase.interrupted(threadContext);
                }

                Map<String, Double> objectives = null;

                PerformanceMonitorManager.startTimer(WALKER_CYCLE);

                ITransition transition = null;

                do {
                    // Get next activation to fire. Eventually calls the
                    // getNextTransition methods.
                    PerformanceMonitorManager.startTimer(GET_NEXT_TRANSITION_ID_TIMER);
                    transition = strategyBase.getNextTransition(threadContext, transition == null);
                    PerformanceMonitorManager.endTimer(GET_NEXT_TRANSITION_ID_TIMER);
                    // If there are no more transitions to fire, then return and
                    // stop the exploration.
                    if (transition == null) {
                        break mainloop;
                    }
                    // if we cannot lock that particular Transition id, we try
                    // to get a new activation one
                } while (!transition.tryToLock());

                // fire activation
                PerformanceMonitorManager.startTimer(FIRE_ACTIVATION_TIMER);
                designSpaceManager.fireActivation(transition);
                PerformanceMonitorManager.endTimer(FIRE_ACTIVATION_TIMER);

                IState newState = designSpaceManager.getCurrentState();

                logger.debug("Transition fired: " + transition.getId() + " State: " + newState.getId());

                PerformanceMonitorManager.startTimer(STATE_EVALUATION);

                boolean isAlreadyTraversed = designSpaceManager.isNewModelStateAlreadyTraversed();
                boolean areConstraintsSatisfied = true;
                objectives = strategyBase.isGoalState(threadContext);
                if (isAlreadyTraversed) {
                    TraversalStateType traversalState = newState.getTraversalState();

                    // Create new trajectory for solution
                    if (traversalState == TraversalStateType.GOAL) {
                        // TODO check goal state again, because of the hash
                        // collision if(iCheckGoalState.isGoalState(context)){}
                        globalContext.getSolutionStore().newSolution(threadContext, null);
                    } else if (traversalState == TraversalStateType.CUT) {
                        areConstraintsSatisfied = false;
                    }

                    logger.debug("State is already traversed.");

                } else {
                    // if the global constraints are satisfied
                    areConstraintsSatisfied = strategyBase.checkConstraints(threadContext);
                    if (areConstraintsSatisfied) {

                        // if it is a goal state
                        if (objectives != null) {

                            logger.debug("Goal state.");

                            Solution solution = globalContext.getSolutionStore().newSolution(threadContext, objectives);

                            if (solution != null) {
                                // TODO this behavior could be unwanted
                                newState.setTraversalState(TraversalStateType.GOAL);

                                // Call SolutionFoundResolver
                                ExecutationType verdict = strategyBase.solutionFound(threadContext, solution);

                                switch (verdict) {
                                case STOP_ALL:
                                    continueExecution = false;
                                    globalContext.stopAllThreads();
                                    break;
                                case STOP_THREAD:
                                    continueExecution = false;
                                default:
                                    break;
                                }
                            }

                        }
                        // if not goal state, check the cut-off criterias
                        else {
                            if (guidance != null && guidance.evaluateCutOffCriterias() == EvaluationResult.CUT_OFF) {
                                newState.setTraversalState(TraversalStateType.CUT);
                            }

                        }
                    }
                    // if the global constraints are not satisfied
                    else {
                        newState.setTraversalState(TraversalStateType.CUT);
                        logger.debug("Constraints are not satisfied.");
                    }
                    newState.setProcessed(); // TODO there is one in addState
                }

                strategyBase.newStateIsProcessed(threadContext, isAlreadyTraversed, objectives,
                        !areConstraintsSatisfied);
                PerformanceMonitorManager.endTimer(STATE_EVALUATION);
            }

            logger.debug("Strategy stopped on Thread " + Thread.currentThread());
            globalContext.strategyFinished(this);
            return;
        } catch (Exception e) {
            logger.error("Thread stopped unexpectedly!", e);
            globalContext.getExceptionHappendInOtherThread().set(true);
            globalContext.strategyFinished(this);
            throw new DSEException(e);
        }
    }

    @Override
    public void dispose() {
        threadContext.getRuleEngine().dispose();
        try {
            threadContext.getIncqueryEngine().getBaseIndex().dispose();
        } catch (IncQueryException e) {
            throw new DSEException(e);
        }
    }

    @Override
    public ThreadContext getThreadContext() {
        return threadContext;
    }
}
