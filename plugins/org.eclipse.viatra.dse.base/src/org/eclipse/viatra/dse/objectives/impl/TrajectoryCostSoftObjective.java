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
package org.eclipse.viatra.dse.objectives.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.viatra.dse.api.DSETransformationRule;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.objectives.ActivationFitnessProcessor;
import org.eclipse.viatra.dse.objectives.Comparators;
import org.eclipse.viatra.dse.objectives.IObjective;

import com.google.common.base.Preconditions;

/**
 * This soft objective calculates a fitness value based on the length of the trajectory. Costs to the rules can be
 * assigned.
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class TrajectoryCostSoftObjective extends BaseObjective {

    public static final String DEFAULT_NAME = "TrajectoryCostObjective";
    protected Map<DSETransformationRule<?, ?>, Double> fixCosts;
    protected Map<DSETransformationRule<?, ?>, ActivationFitnessProcessor> activationCostProcessors;
    protected double trajectoryLengthWeight = 0.0;
    protected boolean withTrajectoryLengthWeight;

    public TrajectoryCostSoftObjective(String name) {
        super(name);
        comparator = Comparators.LOWER_IS_BETTER;
    }

    public TrajectoryCostSoftObjective() {
        this(DEFAULT_NAME);
    }

    /**
     * Sets the cost of a rule.
     * 
     * @param rule
     * @param cost
     * @return The actual instance to enable builder pattern like usage.
     */
    public TrajectoryCostSoftObjective withRuleCost(DSETransformationRule<?, ?> rule, double cost) {
        Preconditions.checkNotNull(rule);
        if (fixCosts == null) {
            fixCosts = new HashMap<DSETransformationRule<?, ?>, Double>();
        }
        Preconditions.checkArgument(!fixCosts.containsKey(rule));
        fixCosts.put(rule, cost);
        return this;
    }

    /**
     * Sets an activation processor for a rule.
     * 
     * @param rule
     * @param activationCostProcessor
     * @return The actual instance to enable builder pattern like usage.
     */
    public TrajectoryCostSoftObjective withActivationCost(DSETransformationRule<?, ?> rule,
            ActivationFitnessProcessor activationCostProcessor) {
        Preconditions.checkNotNull(rule);
        Preconditions.checkNotNull(activationCostProcessor);
        if (activationCostProcessors == null) {
            activationCostProcessors = new HashMap<DSETransformationRule<?, ?>, ActivationFitnessProcessor>();
        }
        Preconditions.checkArgument(!activationCostProcessors.containsKey(rule));
        activationCostProcessors.put(rule, activationCostProcessor);
        return this;
    }

    /**
     * The length of the trajectory multiplied with given parameter will be added to the fitness value.
     * 
     * @param trajectoryLengthWeight
     *            The weight of a transformation rule application.
     * @return The actual instance to enable builder pattern like usage.
     */
    public TrajectoryCostSoftObjective withTrajectoryLengthWeight(double trajectoryLengthWeight) {
        this.trajectoryLengthWeight = trajectoryLengthWeight;
        this.withTrajectoryLengthWeight = true;
        return this;
    }

    @Override
    public Double getFitness(ThreadContext context) {

        List<ITransition> trajectory = context.getDesignSpaceManager().getTrajectoryInfo()
                .getFullTransitionTrajectory();

        double result = 0;

        for (ITransition transition : trajectory) {
            DSETransformationRule<?, ?> rule = transition.getTransitionMetaData().rule;

            Double cost = fixCosts.get(rule);
            if (cost != null) {
                result += cost;
            }

            Map<String, Double> costs = transition.getTransitionMetaData().costs;
            if (costs != null) {
                cost = costs.get(name);
                if (cost != null) {
                    result += cost;
                }
            }
        }

        if (withTrajectoryLengthWeight) {
            result += trajectory.size() * trajectoryLengthWeight;
        }

        return result;
    }

    @Override
    public void init(ThreadContext context) {
        DesignSpaceManager dsm = context.getDesignSpaceManager();
        for (DSETransformationRule<?, ?> rule : activationCostProcessors.keySet()) {
            dsm.registerActivationCostProcessor(name, rule, activationCostProcessors.get(rule));
        }
    }

    @Override
    public IObjective createNew() {
        return this;
    }

    @Override
    public boolean isHardObjective() {
        return false;
    }

    @Override
    public boolean satisifiesHardObjective(Double fitness) {
        return true;
    }

}
