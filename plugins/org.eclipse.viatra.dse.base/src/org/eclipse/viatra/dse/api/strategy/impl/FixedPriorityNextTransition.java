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

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra.dse.api.TransformationRule;
import org.eclipse.viatra.dse.api.strategy.interfaces.INextTransition;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.guidance.RuleInfo;

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
public class FixedPriorityNextTransition implements INextTransition {

    private boolean tryHigherPriorityFirst = true;
    private boolean tryBestTransitionsOnly = true;
    private Map<Object, Double> bestPriorityInState;
    private int depthLimit = 0;
    private Random rnd = new Random();
    private DesignSpaceManager dsm;
    private boolean isInterrupted = false;

    public FixedPriorityNextTransition() {
        // tryBestTransitionsOnly = true by default
        bestPriorityInState = new HashMap<Object, Double>();
    }

    /**
     * @param tryHigherPriorityFirst
     *            If set to true (default) it will return the transition with the highest priority, else it returns the
     *            lowest.
     * @param tryBestTransitionsOnly
     *            If set to true (default) it will try only the best rules based on priority, else it will try the less
     *            ones true.
     */
    public FixedPriorityNextTransition(boolean tryHigherPriorityFirst, boolean tryBestTransitionsOnly, int depthLimit) {
        this.tryHigherPriorityFirst = tryHigherPriorityFirst;
        this.tryBestTransitionsOnly = tryBestTransitionsOnly;
        this.depthLimit = depthLimit;
        if (tryBestTransitionsOnly) {
            bestPriorityInState = new HashMap<Object, Double>();
        }
    }

    @Override
    public void init(ThreadContext context) {
        dsm = context.getDesignSpaceManager();
    }

    @Override
    public ITransition getNextTransition(final ThreadContext context, boolean lastWasSuccesful) {

        if (isInterrupted) {
            return null;
        }

        Map<TransformationRule<? extends IPatternMatch>, RuleInfo> ruleInfos = context.getGuidance().getRuleInfos();

        // Backtrack if there is no more unfired transition from here
        List<? extends ITransition> transitions = dsm.getUntraversedTransitionsFromCurrentState();
        while ((depthLimit > 0 && dsm.getTrajectoryFromRoot().size() >= depthLimit) || (transitions == null || transitions.isEmpty())) {
            if (!dsm.undoLastTransformation()) {
                return null;
            }
            transitions = dsm.getUntraversedTransitionsFromCurrentState();
        }

        do {

            if (tryBestTransitionsOnly) {
                Double bestPriority = bestPriorityInState.get(dsm.getCurrentState().getId());
                if (bestPriority == null) {
                    bestPriority = getBestPriority(dsm.getTransitionsFromCurrentState(), ruleInfos);
                    bestPriorityInState.put(dsm.getCurrentState().getId(), bestPriority);
                }
                List<ITransition> bestTrasitions = Lists.newArrayList();
                for (ITransition iTransition : dsm.getUntraversedTransitionsFromCurrentState()) {
                    if (ruleInfos.get(iTransition.getTransitionMetaData().rule).getPriority() == bestPriority) {
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
                ITransition bestTransition = getBestTransition(dsm.getUntraversedTransitionsFromCurrentState(),
                        ruleInfos);
                if (bestTransition != null) {
                    return bestTransition;
                }
            }


        } while (!dsm.undoLastTransformation());

        return null;
    }

    @Override
    public void newStateIsProcessed(ThreadContext context, boolean isAlreadyTraversed, Map<String, Double> objectives,
            boolean constraintsNotSatisfied) {
        if (isAlreadyTraversed || constraintsNotSatisfied || (objectives!=null && objectives.isEmpty())) {
            context.getDesignSpaceManager().undoLastTransformation();
        }
    }

    @Override
    public void interrupted(ThreadContext context) {
        isInterrupted = true;
    }

    private ITransition getBestTransition(Collection<? extends ITransition> transitions,
            Map<TransformationRule<? extends IPatternMatch>, RuleInfo> ruleInfos) {
        double bestPriority;
        ITransition bestTransition = null;
        if (tryHigherPriorityFirst) {
            bestPriority = Double.MIN_VALUE;
        } else {
            bestPriority = Double.MAX_VALUE;
        }
        for (ITransition iTransition : transitions) {
            double priority = ruleInfos.get(iTransition.getTransitionMetaData().rule).getPriority();
            if (tryHigherPriorityFirst ? (priority > bestPriority) : (priority < bestPriority)) {
                bestPriority = priority;
                bestTransition = iTransition;
            }
        }
        return bestTransition;
    }

    private double getBestPriority(Collection<? extends ITransition> transitions,
            Map<TransformationRule<? extends IPatternMatch>, RuleInfo> ruleInfos) {
        double bestPriority;
        if (tryHigherPriorityFirst) {
            bestPriority = Double.MIN_VALUE;
        } else {
            bestPriority = Double.MAX_VALUE;
        }
        for (ITransition iTransition : transitions) {
            double priority = ruleInfos.get(iTransition.getTransitionMetaData().rule).getPriority();
            if (tryHigherPriorityFirst ? (priority > bestPriority) : (priority < bestPriority)) {
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

    public Map<Object, Double> getBestPriorityInState() {
        return bestPriorityInState;
    }

    public void setBestPriorityInState(Map<Object, Double> bestPriorityInState) {
        this.bestPriorityInState = bestPriorityInState;
    }

    public boolean isTryHigherPriorityFirst() {
        return tryHigherPriorityFirst;
    }

    public void setTryHigherPriorityFirst(boolean tryHigherPriorityFirst) {
        this.tryHigherPriorityFirst = tryHigherPriorityFirst;
    }

    public int getDepthLimit() {
        return depthLimit;
    }

    public void setDepthLimit(int depthLimit) {
        this.depthLimit = depthLimit;
    }

}
