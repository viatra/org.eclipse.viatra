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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra.dse.api.TransformationRule;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.objectives.Comparators;
import org.eclipse.viatra.dse.objectives.IObjective;

import com.google.common.base.Preconditions;

/**
 * This soft objective calculates a fitness value based on the length of the trajectory. Costs to the rules can be assigned.
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class TrajectoryCostSoftObjective implements IObjective {

    private static final String DEFAULT_NAME = "TrajectoryCostObjective";

    protected String name;

    protected List<TransformationRule<? extends IPatternMatch>> rules;
    protected List<Double> costs;

    protected Comparator<Double> comparator = Comparators.BIGGER_IS_BETTER;

    public TrajectoryCostSoftObjective(String name) {
        Preconditions.checkNotNull(name, "Name of the objective cannot be null.");
        this.name = name;
    }

    public TrajectoryCostSoftObjective() {
        this(DEFAULT_NAME);
    }

    public void setComparator(Comparator<Double> comparator) {
        this.comparator = comparator;
    }

    /**
     * Sets the cost of a rule.
     * 
     * @param rule
     * @param cost
     * @return The actual instance to enable builder pattern like usage.
     */
    public TrajectoryCostSoftObjective addCost(TransformationRule<? extends IPatternMatch> rule, double cost) {
        Preconditions.checkNotNull(rule);
        Preconditions.checkArgument(!rules.contains(rule));
        if (rules == null) {
            rules = new ArrayList<TransformationRule<? extends IPatternMatch>>();
        }
        if (costs == null) {
            costs = new ArrayList<Double>();
        }
        rules.add(rule);
        costs.add(cost);
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Comparator<Double> getComparator() {
        return comparator;
    }

    @Override
    public Double getFitness(ThreadContext context) {

        List<ITransition> trajectory = context.getDesignSpaceManager().getTrajectoryInfo().getFullTransitionTrajectory();
        
        double result = 0;

        for (ITransition transition : trajectory) {
            TransformationRule<? extends IPatternMatch> rule = transition.getTransitionMetaData().rule;
            int index = rules.indexOf(rule);
            if (index > -1) {
                result += costs.get(index);
            }
            else {
                result += 1;
            }
        }
        
        return result;
    }

    @Override
    public void init(ThreadContext context) {
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
