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

import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.objectives.IObjective;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;

import com.google.common.base.Preconditions;

/**
 * This objective serves as soft and as hard objective at the same time by defining two lists of VIATRA Query
 * specifications.
 * 
 * As a soft objective, it collects a list of VIATRA Query specifications, which have predefined weights. Then the
 * fitness value of an arbitrary solution is calculated in the following way:
 * <p>
 * <code>fitness = sum( pattern[i].countMatches() * weight[i] )</code>
 * <p>
 * As a hard objective it collects a separate list of VIATRA Query specifications. If every one of them has a match the
 * hard constraint is considered to be fulfilled.
 * 
 * @author Andras Szabolcs Nagy
 * @see IObjective
 *
 */
public class ConstraintsObjective extends BaseObjective {

    public static final String DEFAULT_NAME = "ConstraintsObjective";

    protected List<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> softConstraints;
    protected List<Double> weights;
    protected List<String> softNames;

    protected List<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> hardConstraints;
    protected List<String> hardNames;

    protected List<ViatraQueryMatcher<? extends IPatternMatch>> softMatchers;
    protected List<ViatraQueryMatcher<? extends IPatternMatch>> hardMatchers;
    protected List<Integer> softMatches;
    protected List<Integer> hardMatches;

    public ConstraintsObjective(String name,
            List<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> softConstraints,
            List<Double> weights,
            List<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> hardConstraints) {
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

        for (IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> hardConstraint : hardConstraints) {
            hardNames.add(hardConstraint.getFullyQualifiedName());
        }
        for (IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> softConstraint : softConstraints) {
            softNames.add(softConstraint.getFullyQualifiedName());
        }
    }

    public ConstraintsObjective(String name,
            List<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> softConstraints,
            List<Double> weights) {
        this(name, softConstraints, weights,
                new ArrayList<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>>());
    }

    public ConstraintsObjective(
            List<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> softConstraints,
            List<Double> weights) {
        this(DEFAULT_NAME, softConstraints, weights,
                new ArrayList<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>>());
    }

    public ConstraintsObjective(String name,
            List<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> hardConstraints) {
        this(name, hardConstraints, new ArrayList<Double>(),
                new ArrayList<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>>());
    }

    public ConstraintsObjective(
            List<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> hardConstraints) {
        this(DEFAULT_NAME, hardConstraints, new ArrayList<Double>(),
                new ArrayList<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>>());
    }

    public ConstraintsObjective(String name) {
        this(name, new ArrayList<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>>(),
                new ArrayList<Double>(),
                new ArrayList<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>>());
    }

    public ConstraintsObjective() {
        this(DEFAULT_NAME, new ArrayList<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>>(),
                new ArrayList<Double>(),
                new ArrayList<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>>());
    }

    /**
     * Adds a new soft constraint.
     * 
     * @param name
     *            A name for the soft constraint.
     * @param softConstraint
     *            A VIATRA Query pattern specification.
     * @param weight
     *            The weight of the pattern.
     * @return The actual instance to enable builder pattern like usage.
     */
    public ConstraintsObjective withSoftConstraint(String name,
            IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> softConstraint, double weight) {
        softConstraints.add(softConstraint);
        weights.add(new Double(weight));
        softNames.add(name);
        return this;
    }

    /**
     * Adds a new soft constraint with the name of the query specification's fully qualified name.
     * 
     * @param softConstraint
     *            A VIATRA Query pattern specification.
     * @param weight
     *            The weight of the pattern.
     * @return The actual instance to enable builder pattern like usage.
     */
    public ConstraintsObjective withSoftConstraint(
            IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> softConstraint, double weight) {
        return withSoftConstraint(softConstraint.getFullyQualifiedName(), softConstraint, weight);
    }

    /**
     * Adds a new hard constraint.
     * 
     * @param name
     *            A name for the hard constraint.
     * @param softConstraint
     *            A VIATRA Query pattern specification.
     * @return The actual instance to enable builder pattern like usage.
     */
    public ConstraintsObjective withHardConstraint(String name,
            IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> hardConstraint) {
        hardConstraints.add(hardConstraint);
        hardNames.add(name);
        return this;
    }

    /**
     * Adds a new hard constraint with the name of the query specification's fully qualified name.
     * 
     * @param softConstraint
     *            A VIATRA Query pattern specification.
     * @return The actual instance to enable builder pattern like usage.
     */
    public ConstraintsObjective withHardConstraint(
            IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> hardConstraint) {
        return withHardConstraint(hardConstraint.getFullyQualifiedName(), hardConstraint);
    }

    @Override
    public Double getFitness(ThreadContext context) {

        if (softConstraints.isEmpty() && satisifiesHardObjective(null)) {
            return new Double(1d);
        }

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
        softMatchers = new ArrayList<ViatraQueryMatcher<? extends IPatternMatch>>(softConstraints.size());
        hardMatches = new ArrayList<Integer>(hardConstraints.size());
        hardMatchers = new ArrayList<ViatraQueryMatcher<? extends IPatternMatch>>(hardConstraints.size());
        for (IQuerySpecification<?> qs : softConstraints) {
            softMatches.add(0);
        }
        for (IQuerySpecification<?> qs : hardConstraints) {
            hardMatches.add(0);
        }

        try {
            ViatraQueryEngine queryEngine = context.getQueryEngine();

            for (IQuerySpecification<?> qs : softConstraints) {
                softMatchers.add(qs.getMatcher(queryEngine));
            }

            for (IQuerySpecification<?> qs : hardConstraints) {
                hardMatchers.add(qs.getMatcher(queryEngine));
            }

        } catch (ViatraQueryException e) {
            throw new DSEException("Couldn't initialize the VIATRA Query matcher, see inner exception", e);
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

    public List<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> getSoftConstraints() {
        return softConstraints;
    }

    public List<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> getHardConstraints() {
        return hardConstraints;
    }

    public List<String> getSoftNames() {
        return softNames;
    }

    public List<String> getHardNames() {
        return hardNames;
    }

    public List<Integer> getSoftMatches() {
        return softMatches;
    }

    public List<Integer> getHardMatches() {
        return hardMatches;
    }

}
