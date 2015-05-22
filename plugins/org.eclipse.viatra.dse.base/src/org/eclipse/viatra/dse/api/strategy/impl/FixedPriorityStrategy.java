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
package org.eclipse.viatra.dse.api.strategy.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.api.DSETransformationRule;
import org.eclipse.viatra.dse.api.strategy.interfaces.LocalSearchStrategyBase;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.IGetCertainTransitions.FilterOptions;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.objectives.Fitness;

import com.google.common.collect.Lists;

/**
 * This strategy component determines the next step of the traversal based on the priority of the rules. <br/>
 * It can be configured to
 * <ul>
 * <li>select the rule with the highest or the lowest priority,</li>
 * <li>traverse all of the transitions in a state or just the best ones.</li>
 * </ul>
 * 
 * @author Andras Szabolcs Nagy
 * 
 */
public class FixedPriorityStrategy implements LocalSearchStrategyBase {

    private boolean tryBestTransitionsOnly = true;
    private Map<Object, Integer> bestPriorityInState = new HashMap<Object, Integer>();
    private int depthLimit = 0;
    private Random rnd = new Random();
    private DesignSpaceManager dsm;
    private boolean isInterrupted = false;

    protected Map<DSETransformationRule<?, ?>, Integer> priorities = new HashMap<DSETransformationRule<?, ?>, Integer>();
    private FilterOptions filterOptions;

    private Logger logger = Logger.getLogger(LocalSearchStrategyBase.class);
    private ThreadContext context;

    /**
     * Creates a fixed priority strategy instance, with default configuration: it tries only the rule activations with
     * the highest priority from a state, without a depth limit.
     */
    public FixedPriorityStrategy() {
        filterOptions = new FilterOptions().nothingIfCut().nothingIfGoal().untraversedOnly();
    }

    /**
     * If called the strategy will traverse all of the transitions in a state instead of just the best ones.
     * 
     * @return The actual instance to enable a builder pattern like usage.
     */
    public FixedPriorityStrategy withFullSearch() {
        this.tryBestTransitionsOnly = false;
        return this;
    }

    /**
     * Adds a depth limit to the strategy.
     * 
     * @param depthLimit
     *            The depth limit.
     * @return The actual instance to enable a builder pattern like usage.
     */
    public FixedPriorityStrategy withDepthLimit(int depthLimit) {
        this.depthLimit = depthLimit;
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

    @Override
    public void init(ThreadContext context) {
        this.context = context;
        dsm = context.getDesignSpaceManager();
    }

    @Override
    public ITransition getNextTransition(boolean lastWasSuccesful) {

        if (isInterrupted) {
            return null;
        }

        do {
            // Backtrack if there is no more unfired transition from here
            Collection<? extends ITransition> transitions = dsm.getTransitionsFromCurrentState(filterOptions);
            while ((depthLimit > 0 && dsm.getTrajectoryFromRoot().size() >= depthLimit)
                    || (transitions == null || transitions.isEmpty())) {
                if (!dsm.undoLastTransformation()) {
                    logger.debug("No more available transitions, the reachable design space has been fully explored.");
                    return null;
                }
                transitions = dsm.getTransitionsFromCurrentState(filterOptions);
            }

            if (tryBestTransitionsOnly) {
                Integer bestPriority = bestPriorityInState.get(dsm.getCurrentState().getId());
                if (bestPriority == null) {
                    bestPriority = getBestPriority(dsm.getTransitionsFromCurrentState());
                    bestPriorityInState.put(dsm.getCurrentState().getId(), bestPriority);
                }
                logger.debug("Best priority in state: " + bestPriority);
                List<ITransition> bestTrasitions = Lists.newArrayList();
                for (ITransition iTransition : transitions) {
                    if (priorities.get(iTransition.getTransitionMetaData().rule) == bestPriority) {
                        bestTrasitions.add(iTransition);
                    }
                }
                // Random selection between transitions on the same level
                if (!bestTrasitions.isEmpty()) {
                    int index = rnd.nextInt(bestTrasitions.size());
                    ITransition iTransition = bestTrasitions.get(index);
                    return iTransition;
                }
            } else {
                ITransition bestTransition = getBestTransition(transitions);
                if (bestTransition != null) {
                    return bestTransition;
                }
            }

        } while (!isInterrupted && dsm.undoLastTransformation());

        return null;
    }

    @Override
    public void newStateIsProcessed(boolean isAlreadyTraversed, Fitness fitness, boolean constraintsNotSatisfied) {
        if (isAlreadyTraversed || constraintsNotSatisfied || (fitness.isSatisifiesHardObjectives())) {
            context.getDesignSpaceManager().undoLastTransformation();
        }
    }

    @Override
    public void interrupted() {
        isInterrupted = true;
    }

    private ITransition getBestTransition(Collection<? extends ITransition> transitions) {
        ITransition bestTransition = null;
        Integer bestPriority = Integer.MIN_VALUE;
        for (ITransition iTransition : transitions) {
            Integer priority = priorities.get(iTransition.getTransitionMetaData().rule);
            if (priority > bestPriority) {
                bestPriority = priority;
                bestTransition = iTransition;
            }
        }
        return bestTransition;
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

    public boolean isTryBestTransitionsOnly() {
        return tryBestTransitionsOnly;
    }

    public void setTryBestTransitionsOnly(boolean tryBestTransitionsOnly) {
        this.tryBestTransitionsOnly = tryBestTransitionsOnly;
    }

    public Map<Object, Integer> getBestPriorityInState() {
        return bestPriorityInState;
    }

    public void setBestPriorityInState(Map<Object, Integer> bestPriorityInState) {
        this.bestPriorityInState = bestPriorityInState;
    }

    public int getDepthLimit() {
        return depthLimit;
    }

    public void setDepthLimit(int depthLimit) {
        this.depthLimit = depthLimit;
    }

}
