/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.objectives.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.objectives.IObjective;

import com.google.common.base.Preconditions;

/**
 * This soft objective collects a list of IncQuery patterns and weights. Then the fitness value of a solution is
 * calculated in the following way:
 * <p>
 * <code>fitness = sum( pattern[i].countMatches() * weight[i] )</code>
 * <p>
 * Hard constraints can also be registered with this type of objective. If every IncQuery pattern has a match the hard
 * constraint is considered to be fulfilled.
 * 
 * @author Andras Szabolcs Nagy
 * @see IObjective
 *
 */
public class ConstraintsObjective extends BaseObjective {

    public static final String DEFAULT_NAME = "WeightedQueriesObjective";

    protected List<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> softConstraints;
    protected List<Double> weights;
    protected List<String> softNames;

    protected List<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> hardConstraints;
    protected List<String> hardNames;

    protected List<IncQueryMatcher<? extends IPatternMatch>> softMatchers = new ArrayList<IncQueryMatcher<? extends IPatternMatch>>();
    protected List<IncQueryMatcher<? extends IPatternMatch>> hardMatchers = new ArrayList<IncQueryMatcher<? extends IPatternMatch>>();
    protected List<Integer> softMatches;
    protected List<Integer> hardMatches;

    public ConstraintsObjective(String name,
            List<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> softConstraints,
            List<Double> weights,
            List<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> hardConstraints) {
        super(name);
        Preconditions.checkNotNull(softConstraints, "The list of soft constraints cannot be null.");
        Preconditions.checkNotNull(hardConstraints, "The list of hard constraints cannot be null.");
        Preconditions.checkNotNull(weights, "The list of weights cannot be null.");
        Preconditions.checkArgument(softConstraints.size() == weights.size(),
                "The size of the two list must be equivalent.");

        this.softConstraints = softConstraints;
        this.weights = weights;
        this.hardConstraints = hardConstraints;

        this.softNames = new ArrayList<String>(softConstraints.size());
        this.hardNames = new ArrayList<String>(hardConstraints.size());

        for (IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> hardConstraint : hardConstraints) {
            hardNames.add(hardConstraint.getFullyQualifiedName());
        }
        for (IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> softConstraint : softConstraints) {
            softNames.add(softConstraint.getFullyQualifiedName());
        }
    }

    public ConstraintsObjective(String name,
            List<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> softConstraints,
            List<Double> weights) {
        this(name, softConstraints, weights,
                new ArrayList<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>>());
    }

    public ConstraintsObjective(
            List<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> softConstraints,
            List<Double> weights) {
        this(DEFAULT_NAME, softConstraints, weights,
                new ArrayList<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>>());
    }

    public ConstraintsObjective(String name,
            List<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> hardConstraints) {
        this(name, hardConstraints, new ArrayList<Double>(),
                new ArrayList<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>>());
    }

    public ConstraintsObjective(
            List<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> hardConstraints) {
        this(DEFAULT_NAME, hardConstraints, new ArrayList<Double>(),
                new ArrayList<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>>());
    }

    public ConstraintsObjective(String name) {
        this(name, new ArrayList<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>>(),
                new ArrayList<Double>(),
                new ArrayList<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>>());
    }

    public ConstraintsObjective() {
        this(DEFAULT_NAME, new ArrayList<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>>(),
                new ArrayList<Double>(),
                new ArrayList<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>>());
    }

    /**
     * Adds a new soft constraint.
     * 
     * @param name
     *            A name for the soft constraint.
     * @param softConstraint
     *            An IncQuery pattern specification.
     * @param weight
     *            The weight of the pattern.
     * @return The actual instance to enable builder pattern like usage.
     */
    public ConstraintsObjective withSoftConstraint(String name,
            IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> softConstraint, double weight) {
        softConstraints.add(softConstraint);
        weights.add(new Double(weight));
        softNames.add(name);
        return this;
    }

    /**
     * Adds a new soft constraint with the name of the query specification's fully qualified name.
     * 
     * @param softConstraint
     *            An IncQuery pattern specification.
     * @param weight
     *            The weight of the pattern.
     * @return The actual instance to enable builder pattern like usage.
     */
    public ConstraintsObjective withSoftConstraint(
            IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> softConstraint, double weight) {
        return withSoftConstraint(softConstraint.getFullyQualifiedName(), softConstraint, weight);
    }

    /**
     * Adds a new hard constraint.
     * 
     * @param name
     *            A name for the hard constraint.
     * @param softConstraint
     *            An IncQuery pattern specification.
     * @return The actual instance to enable builder pattern like usage.
     */
    public ConstraintsObjective withHardConstraint(String name,
            IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> hardConstraint) {
        hardConstraints.add(hardConstraint);
        hardNames.add(name);
        return this;
    }

    /**
     * Adds a new hard constraint with the name of the query specification's fully qualified name.
     * 
     * @param softConstraint
     *            An IncQuery pattern specification.
     * @return The actual instance to enable builder pattern like usage.
     */
    public ConstraintsObjective withHardConstraint(
            IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> hardConstraint) {
        return withHardConstraint(hardConstraint.getFullyQualifiedName(), hardConstraint);
    }

    @Override
    public Double getFitness(ThreadContext context) {

        double result = 0;

        for (int i = 0; i < softConstraints.size(); i++) {
            int countMatches = softMatchers.get(i).countMatches();
            result += countMatches * weights.get(i);
            softMatches.set(i, Integer.valueOf(countMatches));
        }

        return new Double(result);
    }

    @Override
    public void init(ThreadContext context) {

        softMatches = new ArrayList<Integer>(softConstraints.size());
        softMatchers = new ArrayList<IncQueryMatcher<? extends IPatternMatch>>(softConstraints.size());
        hardMatches = new ArrayList<Integer>(hardConstraints.size());
        hardMatchers = new ArrayList<IncQueryMatcher<? extends IPatternMatch>>(hardConstraints.size());
        for (IQuerySpecification<?> qs : softConstraints) {
            softMatches.add(0);
        }
        for (IQuerySpecification<?> qs : hardConstraints) {
            hardMatches.add(0);
        }

        try {
            IncQueryEngine incQueryEngine = context.getIncqueryEngine();

            for (IQuerySpecification<?> qs : softConstraints) {
                softMatchers.add(qs.getMatcher(incQueryEngine));
            }

            for (IQuerySpecification<?> qs : hardConstraints) {
                hardMatchers.add(qs.getMatcher(incQueryEngine));
            }

        } catch (IncQueryException e) {
            throw new DSEException("Couldn't initialize the incquery matcher, see inner exception", e);
        }
    }

    @Override
    public IObjective createNew() {
        ConstraintsObjective result = new ConstraintsObjective(name, softConstraints, weights, hardConstraints);
        result.softNames = softNames;
        result.hardNames = hardNames;
        return result.withComparator(comparator).withLevel(level);
    }

    @Override
    public boolean isHardObjective() {
        return !hardConstraints.isEmpty();
    }

    @Override
    public boolean satisifiesHardObjective(Double fitness) {

        boolean result = true;

        for (int i = 0; i < hardConstraints.size(); i++) {
            int countMatches = hardMatchers.get(i).countMatches();
            hardMatches.set(i, Integer.valueOf(countMatches));
            if (countMatches <= 0) {
                result = false;
            }
        }
        return result;
    }
}
