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
 * This hard objective collects a list of IncQuery patterns and checks if any of them has a match on a solution
 * (trajectoy). It is unsatisfied if any of them has a match returning 0 or it is satisfied if none of them has a match
 * returning 1.
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class ModelQueriesHardObjective extends BaseObjective {

    protected static final String DEFAULT_NAME = "ModelQueriesHardObjective";
    protected List<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> constraints;
    protected List<IncQueryMatcher<? extends IPatternMatch>> matchers = new ArrayList<IncQueryMatcher<? extends IPatternMatch>>();
    protected ModelQueryType type = ModelQueryType.ALL_MUST_HAVE_MATCH;

    public ModelQueriesHardObjective(String name,
            List<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> constraints) {
        super(name);
        Preconditions.checkNotNull(constraints, "The list of constraints cannot be null.");

        this.constraints = constraints;
    }

    public ModelQueriesHardObjective(
            List<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> constraints) {
        this(DEFAULT_NAME, constraints);
    }

    public ModelQueriesHardObjective(String name) {
        this(name, new ArrayList<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>>());
    }

    public ModelQueriesHardObjective() {
        this(DEFAULT_NAME, new ArrayList<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>>());
    }

    /**
     * Adds a new IncQuery pattern.
     * 
     * @param constraint
     *            An IncQuery pattern.
     * @return The actual instance to enable builder pattern like usage.
     */
    public ModelQueriesHardObjective withConstraint(
            IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> constraint) {
        constraints.add(constraint);
        return this;
    }

    @Override
    public Double getFitness(ThreadContext context) {
        for (IncQueryMatcher<? extends IPatternMatch> matcher : matchers) {
            if ((type.equals(ModelQueryType.ALL_MUST_HAVE_MATCH) && matcher.countMatches() == 0)
                    || (type.equals(ModelQueryType.NO_MATCH) && matcher.countMatches() > 0)) {
                return 0d;
            }
        }
        return 1d;
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
            throw new DSEException("Couldn't get the incquery matcher, see inner exception", e);
        }
    }

    @Override
    public IObjective createNew() {
        return new ModelQueriesHardObjective(name, constraints)
            .withType(type)
            .withComparator(comparator)
            .withLevel(level);
    }

    @Override
    public boolean isHardObjective() {
        return true;
    }

    @Override
    public boolean satisifiesHardObjective(Double fitness) {
        return fitness.doubleValue() > 0.5d;
    }

    public ModelQueryType getType() {
        return type;
    }

    public void setType(ModelQueryType type) {
        this.type = type;
    }

    public ModelQueriesHardObjective withType(ModelQueryType type) {
        setType(type);
        return this;
    }
}
