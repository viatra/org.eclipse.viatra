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
package org.eclipse.viatra.dse.api.strategy.interfaces;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.GlobalContext;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.IState;
import org.eclipse.viatra.dse.designspace.api.IState.TraversalStateType;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.monitor.PerformanceMonitorManager;
import org.eclipse.viatra.dse.objectives.Fitness;
import org.eclipse.viatra.dse.objectives.IGlobalConstraint;
import org.eclipse.viatra.dse.solutionstore.ISolutionStore;
import org.eclipse.viatra.dse.solutionstore.ISolutionStore.StopExecutionType;

/**
 * This abstract {@link IStrategy} implementation defines an exploration loop especially for local search based
 * exploration strategies, where a concrete strategy shall derive from this class and define the abstract methods
 * (template method design pattern):
 * <ul>
 * <li>{@link LocalSearchStrategyBase#init(ThreadContext)}</li>
 * <li>{@link LocalSearchStrategyBase#getNextTransition(boolean)}</li>
 * <li>{@link LocalSearchStrategyBase#newStateIsProcessed(boolean, Fitness, boolean)}</li>
 * <li>{@link LocalSearchStrategyBase#interrupted()}</li>
 * </ul>
 * 
 * @author Andras Szabolcs Nagy
 * 
 */
public abstract class LocalSearchStrategyBase implements IStrategy {

    private static final String STATE_EVALUATION = "stateEvaluation";
    private static final String FIRE_ACTIVATION_TIMER = "fireActivationTimer";
    private static final String GET_NEXT_TRANSITION_ID_TIMER = "getNextTransitionIdTimer";
    private static final String WALKER_CYCLE = "walkerCycle";

    private ThreadContext context;
    private GlobalContext globalContext;
    private DesignSpaceManager designSpaceManager;
    private ISolutionStore solutionStore;

    private final AtomicBoolean interrupted = new AtomicBoolean(false);

    private final Logger logger = Logger.getLogger(this.getClass());

    /**
     * Initializes the strategy, called once before the first
     * {@link LocalSearchStrategyBase#getNextTransition(ThreadContext)} is called for every new thread.
     * 
     * @param context
     *            The {@link ThreadContext} which contains necessary informations. Should be assigned to a field.
     */
    public abstract void init(ThreadContext context);

    /**
     * Returns the next {@link ITransition} to fire, the next step in the design space. It can be a quite complex method
     * or a simple depth first search.
     * 
     * @param lastWasSuccessful
     *            False if the last returned transition was already fired by someone and therefore should choose an
     *            other transition, otherwise true.
     * @return An {@link ITransition} which is <b>not traversed</b> yet. Null if there is no more to fire.
     */
    public abstract ITransition getNextTransition(boolean lastWasSuccessful);

    /**
     * Called after the chosen transition is fired and the new state has been processed.
     * 
     * @param isAlreadyTraversed
     *            True if the new state is already traversed in the past.
     * @param fitness
     *            A map containing the values of the objectives.
     * @param areConstraintsSatisfied
     *            True if the new state doesn't satisfies the global constraints.
     */
    public abstract void newStateIsProcessed(boolean isAlreadyTraversed, Fitness fitness,
            boolean constraintsNotSatisfied);

    /**
     * Called if the exploration process is interrupted for example by timeout. Exit by returning null in the
     * {@link LocalSearchStrategyBase#getNextTransition(ThreadContext, boolean)} method witch is called right after this
     * one.
     */
    public abstract void interrupted();

    @Override
    public void initStrategy(ThreadContext context) {
        this.context = context;
        globalContext = context.getGlobalContext();
        designSpaceManager = context.getDesignSpaceManager();
        solutionStore = globalContext.getSolutionStore();

        init(context);
    }

    @Override
    public void explore() {
        boolean continueExecution = true;

        mainloop: do {

            PerformanceMonitorManager.startTimer(STATE_EVALUATION);

            IState currentState = designSpaceManager.getCurrentState();
            boolean isAlreadyTraversed = designSpaceManager.isNewModelStateAlreadyTraversed();
            boolean areConstraintsSatisfied = true;

            Fitness fitness = context.calculateFitness();

            if (isAlreadyTraversed) {
                TraversalStateType traversalState = currentState.getTraversalState();

                // Create new trajectory for solution
                if (fitness.isSatisifiesHardObjectives() && !solutionStore.isStrategyDependent()) {
                    StopExecutionType verdict = solutionStore.newSolution(context);
                    switch (verdict) {
                    case STOP_ALL:
                        continueExecution = false;
                        globalContext.stopAllThreads();
                        break;
                    case STOP_THREAD:
                        continueExecution = false;
                        break;
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

                        if (!solutionStore.isStrategyDependent()) {
                            StopExecutionType verdict = solutionStore.newSolution(context);
                            switch (verdict) {
                            case STOP_ALL:
                                continueExecution = false;
                                globalContext.stopAllThreads();
                                break;
                            case STOP_THREAD:
                                continueExecution = false;
                                break;
                            default:
                                break;
                            }
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

            newStateIsProcessed(isAlreadyTraversed, fitness, !areConstraintsSatisfied);
            PerformanceMonitorManager.endTimer(STATE_EVALUATION);

            // do the exploration until {@link StrategyBase#solutionFound}
            // returns
            // stop, or interrupted from outside by Strategy#stopRunning

            PerformanceMonitorManager.endTimer(WALKER_CYCLE);

            if (interrupted.get()) {
                interrupted();
            }

            PerformanceMonitorManager.startTimer(WALKER_CYCLE);

            ITransition transition = null;

            do {
                // Get next activation to fire. Eventually calls the
                // getNextTransition methods.
                PerformanceMonitorManager.startTimer(GET_NEXT_TRANSITION_ID_TIMER);
                transition = getNextTransition(transition == null);
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
    }

    @Override
    public void interruptStrategy() {
        interrupted.set(true);
    }

    private boolean checkGlobalConstraints() {
        for (IGlobalConstraint globalConstraint : globalContext.getGlobalConstraints()) {
            if (!globalConstraint.checkGlobalConstraint(context)) {
                return false;
            }
        }
        return true;
    }
}
