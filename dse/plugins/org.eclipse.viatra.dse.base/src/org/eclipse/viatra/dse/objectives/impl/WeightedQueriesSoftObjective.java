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
import java.util.List;

import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.objectives.IObjective;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.IncQueryEngine;
import org.eclipse.viatra.query.runtime.api.IncQueryMatcher;
import org.eclipse.viatra.query.runtime.exception.IncQueryException;

import com.google.common.base.Preconditions;

/**
 * This soft objective collects a list of IncQuery patterns and weights. Then the fitness value of a solution is
 * calculated in the following way:
 * <p>
 * <code>fitness = sum( pattern[i].countMatches() * weight[i] )</code>
 * 
 * @author Andras Szabolcs Nagy
 * @see IObjective
 *
 */
public class WeightedQueriesSoftObjective extends BaseObjective {

    public static final String DEFAULT_NAME = "WeightedQueriesSoftObjective";
    protected List<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> constraints;
    protected List<Double> weights;
    protected List<IncQueryMatcher<? extends IPatternMatch>> matchers = new ArrayList<IncQueryMatcher<? extends IPatternMatch>>();

    public WeightedQueriesSoftObjective(String name,
            List<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> constraints,
            List<Double> weights) {
        super(name);
        Preconditions.checkNotNull(constraints, "The list of constraints cannot be null.");
        Preconditions.checkArgument(constraints.size() == weights.size(),
                "The size of the two list must be equivalent.");

        this.constraints = constraints;
        this.weights = weights;
    }

    public WeightedQueriesSoftObjective(
            List<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> constraints,
            List<Double> weights) {
        this(DEFAULT_NAME, constraints, weights);
    }

    public WeightedQueriesSoftObjective(String name) {
        this(name, new ArrayList<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>>(),
                new ArrayList<Double>());
    }

    public WeightedQueriesSoftObjective() {
        this(DEFAULT_NAME, new ArrayList<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>>(),
                new ArrayList<Double>());
    }

    /**
     * Adds a new IncQuery pattern.
     * 
     * @param constraint
     *            An IncQuery pattern.
     * @param weight
     *            The weight of the pattern.
     * @return The actual instance to enable builder pattern like usage.
     */
    public WeightedQueriesSoftObjective withConstraint(
            IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> constraint, double weight) {
        constraints.add(constraint);
        weights.add(new Double(weight));
        return this;
    }

    @Override
    public Double getFitness(ThreadContext context) {

        double result = 0;

        for (int i = 0; i < constraints.size(); i++) {
            result += matchers.get(i).countMatches() * weights.get(i);
        }

        return new Double(result);
    }

    @Override
    public void init(ThreadContext context) {
        try {
            IncQueryEngine incQueryEngine = context.getIncqueryEngine();

            for (IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> querySpecification : constraints) {
                IncQueryMatcher<? extends IPatternMatch> matcher = querySpecification.getMatcher(incQueryEngine);
                matchers.add(matcher);
            }

        } catch (IncQueryException e) {
            throw new DSEException("Couldn't initialize the incquery matcher, see inner exception", e);
        }
    }

    @Override
    public IObjective createNew() {
        return new WeightedQueriesSoftObjective(name, constraints, weights)
            .withComparator(comparator)
            .withLevel(level);
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
