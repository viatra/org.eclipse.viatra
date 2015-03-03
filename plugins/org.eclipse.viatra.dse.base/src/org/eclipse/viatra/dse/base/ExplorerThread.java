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

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.Solution;
import org.eclipse.viatra.dse.api.strategy.interfaces.IExplorerThread;
import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategy;
import org.eclipse.viatra.dse.api.strategy.interfaces.ISolutionFoundHandler;
import org.eclipse.viatra.dse.designspace.api.IState;
import org.eclipse.viatra.dse.designspace.api.IState.TraversalStateType;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.guidance.Guidance;
import org.eclipse.viatra.dse.guidance.ICriteria.EvaluationResult;
import org.eclipse.viatra.dse.monitor.PerformanceMonitorManager;
import org.eclipse.viatra.dse.objectives.IGlobalConstraint;
import org.eclipse.viatra.dse.objectives.IObjective;
import org.eclipse.viatra.dse.objectives.Fitness;
import org.eclipse.viatra.dse.solutionstore.ISolutionStore.StopExecutionType;

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
    private final AtomicBoolean interrupted = new AtomicBoolean(false);

    private final Logger logger = Logger.getLogger(this.getClass());
    private Fitness fitness;

    public ExplorerThread(final ThreadContext context) {
        this.threadContext = context;
    }

    /**
     * Makes the strategy (the thread) end it's last step, then exit.
     */
    @Override
    public void stopRunning() {
        interrupted.set(true);
    }

    /**
     * Starts the design space exploration. Returns only when
     * {@link ISolutionFoundHandler#solutionFound(Strategy, Solution)} method returns STOP or the
     * {@link IStrategy#getNextTransition(ThreadContext)} method returns null.
     * 
     * If this main algorithm is not good for you, you can derive from this class and override this method.
     */
    @Override
    public void run() {
        try {

            // init is called here, not in the constructor, because of
            // performance
            // (initialization happens in the new thread)
            threadContext.init();

            globalContext = threadContext.getGlobalContext();

            IStrategy strategy = threadContext.getStrategy();
            strategy.init(threadContext);

            boolean continueExecution = true;

            DesignSpaceManager designSpaceManager = threadContext.getDesignSpaceManager();
            Guidance guidance = threadContext.getGuidance();

            logger.debug("Strategy started with state: " + designSpaceManager.getCurrentState().getId());


            mainloop: do {

                PerformanceMonitorManager.startTimer(STATE_EVALUATION);

                IState currentState = designSpaceManager.getCurrentState();
                boolean isAlreadyTraversed = designSpaceManager.isNewModelStateAlreadyTraversed();
                boolean areConstraintsSatisfied = true;

                calculateFitness();

                if (isAlreadyTraversed) {
                    TraversalStateType traversalState = currentState.getTraversalState();

                    // Create new trajectory for solution
                    if (fitness.isSatisifiesHardObjectives() && !globalContext.getSolutionStore().isStrategyDependent()) {
                        StopExecutionType verdict = globalContext.getSolutionStore().newSolution(threadContext);
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
                    } else if (traversalState == TraversalStateType.CUT) {
                        areConstraintsSatisfied = false;
                    }

                    logger.debug("State is already traversed.");

                } else {
                    // if the global constraints are satisfied
                    areConstraintsSatisfied = checkGlobalConstraints();
                    if (areConstraintsSatisfied) {

                        // if it is a goal state
                        if (fitness.isSatisifiesHardObjectives()) {

                            logger.debug("State satisfies all the hard objectives.");

                            currentState.setTraversalState(TraversalStateType.GOAL);

                            if (!globalContext.getSolutionStore().isStrategyDependent()) {
                                StopExecutionType verdict = globalContext.getSolutionStore().newSolution(threadContext);
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
                                currentState.setTraversalState(TraversalStateType.CUT);
                            }

                        }
                    }
                    // if the global constraints are not satisfied
                    else {
                        currentState.setTraversalState(TraversalStateType.CUT);
                        logger.debug("Global constraints are not satisfied.");
                    }
                    currentState.setProcessed(); // TODO there is one in addState
                }

                strategy.newStateIsProcessed(threadContext, isAlreadyTraversed, fitness, !areConstraintsSatisfied);
                PerformanceMonitorManager.endTimer(STATE_EVALUATION);

                // do the exploration until {@link StrategyBase#solutionFound}
                // returns
                // stop, or interrupted from outside by Strategy#stopRunning

                PerformanceMonitorManager.endTimer(WALKER_CYCLE);

                if (interrupted.get()) {
                    strategy.interrupted(threadContext);
                }

                PerformanceMonitorManager.startTimer(WALKER_CYCLE);

                ITransition transition = null;

                do {
                    // Get next activation to fire. Eventually calls the
                    // getNextTransition methods.
                    PerformanceMonitorManager.startTimer(GET_NEXT_TRANSITION_ID_TIMER);
                    transition = strategy.getNextTransition(threadContext, transition == null);
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

                logger.debug("Transition fired: " + transition.getId() + " State: " + currentState.getId());

            } while (continueExecution);

            logger.debug("Strategy stopped on Thread " + Thread.currentThread());
            globalContext.strategyFinished(this);
            return;
        } catch (Throwable e) {
            logger.error("Thread stopped unexpectedly!", e);
            globalContext.registerException(e);
            throw new DSEException(e);
        } finally {
            globalContext.strategyFinished(this);
            dispose();
        }
    }

    @Override
    public void dispose() {
        threadContext.getRuleEngine().dispose();
        // threadContext.getIncqueryEngine().dispose();
    }

    @Override
    public ThreadContext getThreadContext() {
        return threadContext;
    }

    private Fitness calculateFitness() {
        Fitness result = new Fitness();

        boolean satisifiesHardObjectives = true;

        for (IObjective objective : globalContext.getObjectives()) {
            Double fitness = objective.getFitness(threadContext);
            result.put(objective.getName(), fitness);
            if (objective.isHardObjective() && !objective.satisifiesHardObjective(fitness)) {
                satisifiesHardObjectives = false;
            }
        }

        result.setSatisifiesHardObjectives(satisifiesHardObjectives);

        threadContext.setFitness(result);
        fitness = result;

        return result;
    }

    private boolean checkGlobalConstraints() {
        for (IGlobalConstraint globalConstraint : globalContext.getGlobalConstraints()) {
            if (!globalConstraint.checkGlobalConstraint(threadContext)) {
                return false;
            }
        }
        return true;
    }
}
