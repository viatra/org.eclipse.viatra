/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.api.strategy.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.DSETransformationRule;
import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategy;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.IState;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.objectives.Fitness;
import org.eclipse.viatra.dse.solutionstore.SolutionStore;

import com.google.common.collect.Lists;

public class FixedPriorityStrategy implements IStrategy {

    private int maxDepth = Integer.MAX_VALUE;
    private AtomicBoolean isInterrupted = new AtomicBoolean(false);
    private DesignSpaceManager dsm;
    private ThreadContext context;
    private SolutionStore solutionStore;

    private Logger logger = Logger.getLogger(getClass());
    private Map<DSETransformationRule<?, ?>, Integer> priorities = new HashMap<DSETransformationRule<?, ?>, Integer>();

    private Random random = new Random();
    private Map<Object, List<ITransition>> bestPriorityInState = new HashMap<>();

    /**
     * Adds a depth limit to the strategy.
     * 
     * @param depthLimit
     *            The depth limit.
     * @return The actual instance to enable a builder pattern like usage.
     */
    public FixedPriorityStrategy withDepthLimit(int maxDepth) {
        if (maxDepth <= 0) {
            this.maxDepth = Integer.MAX_VALUE;
        } else {
            this.maxDepth = maxDepth;
        }
        return this;
    }

    /**
     * Assigns a priority to a rule. Unassigned rule will have a priority of 0.
     * 
     * @param rule
     *            The transformation rule.
     * @param priority
     *            The priority of the rule.
     * @return The actual instance to enable a builder pattern like usage.
     */
    public FixedPriorityStrategy withRulePriority(DSETransformationRule<?, ?> rule, int priority) {
        priorities.put(rule, priority);
        return this;
    }

    public Map<DSETransformationRule<?, ?>, Integer> getPriorities() {
        return priorities;
    }

    @Override
    public void initStrategy(ThreadContext context) {
        this.context = context;
        dsm = context.getDesignSpaceManager();
        solutionStore = context.getGlobalContext().getSolutionStore();

        logger.info("Initied");
    }

    @Override
    public void explore() {

        mainloop: do {

            boolean globalConstraintsAreSatisfied = context.checkGlobalConstraints();
            if (!globalConstraintsAreSatisfied) {
                boolean isSuccessfulUndo = dsm.undoLastTransformation();
                if (!isSuccessfulUndo) {
                    logger.info("Global contraint is not satisifed and cannot backtrack.");
                    break;
                } else {
                    logger.debug("Global contraint is not satisifed, backtrack.");
                    continue;
                }
            }

            Fitness fitness = context.calculateFitness();
            if (fitness.isSatisifiesHardObjectives()) {
                solutionStore.newSolution(context);
                boolean isSuccessfulUndo = dsm.undoLastTransformation();
                if (!isSuccessfulUndo) {
                    logger.info("Found a solution but cannot backtrack.");
                    break;
                } else {
                    logger.debug("Found a solution, backtrack.");
                    continue;
                }
            }

            if (dsm.getTrajectoryInfo().getDepthFromCrawlerRoot() >= maxDepth) {
                boolean isSuccessfulUndo = dsm.undoLastTransformation();
                if (!isSuccessfulUndo) {
                    logger.info("Reached max depth but cannot bactrack.");
                    break;
                } else {
                    logger.debug("Reached max depth, bactrack.");
                    continue;
                }
            }

            if (isInterrupted.get()) {
                logger.info("Interrupted, stop exploration.");
                break;
            }

            List<ITransition> transitions;

            do {

                transitions = bestPriorityInState.get(dsm.getCurrentState().getId());

                if (transitions == null) {
                    Integer bestPriority = getBestPriority(dsm.getTransitionsFromCurrentState());
                    transitions = Lists.newArrayList();
                    for (ITransition iTransition : dsm.getTransitionsFromCurrentState()) {
                        if (priorities.get(iTransition.getTransitionMetaData().rule).equals(bestPriority)) {
                            transitions.add(iTransition);
                        }
                    }
                    bestPriorityInState.put(dsm.getCurrentState().getId(), transitions);
                }

                if (transitions.isEmpty()) {
                    boolean isSuccessfulUndo = dsm.undoLastTransformation();
                    if (!isSuccessfulUndo) {
                        logger.info("No more transitions from current state and cannot backtrack.");
                        break mainloop;
                    } else {
                        logger.debug("No more transitions from current state, backtrack.");
                        continue;
                    }
                }
            } while (transitions.isEmpty());

            int index = random.nextInt(transitions.size());
            ITransition transition = transitions.remove(index);

            IState prevState = dsm.getCurrentState();
            dsm.fireActivation(transition);

            if (logger.isDebugEnabled()) {
                logger.debug("Transition " + transition.getId() + " fired from: " + prevState.getId() + " and reached: "
                        + dsm.getCurrentState().getId());
            }

            boolean loopInTrajectory = dsm.isCurentStateInTrajectory();
            if (loopInTrajectory) {
                boolean isSuccessfulUndo = dsm.undoLastTransformation();
                if (!isSuccessfulUndo) {
                    throw new DSEException(
                            "The new state is present in the trajectoy but cannot bactkrack. Should never happen!");
                } else {
                    logger.info("The new state is already visited in the trajectory, backtrack.");
                }
            }

        } while (true);

        logger.info("Terminated.");
    }

    @Override
    public void interruptStrategy() {
        isInterrupted.set(true);
    }

    private Integer getBestPriority(Collection<? extends ITransition> transitions) {
        Integer bestPriority = Integer.MIN_VALUE;
        for (ITransition iTransition : transitions) {
            Integer priority = priorities.get(iTransition.getTransitionMetaData().rule);
            if (priority > bestPriority) {
                bestPriority = priority;
            }
        }
        return bestPriority;
    }
}
