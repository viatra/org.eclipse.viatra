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
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.objectives.IGlobalConstraint;
import org.eclipse.viatra.dse.objectives.IObjective;

import com.google.common.base.Preconditions;

/**
 * This global constraint collects a list of IncQuery pattern and checks if any of them has a match on a solution
 * (trajectoy). It is unsatisfied if any of them has no match.
 * 
 * This hard objective collects a list of IncQuery pattern and checks if none of them has a match on a solution
 * (trajectoy). It is unsatisfied if any of them has a match.
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class NoMatchGlobalConstraint implements IGlobalConstraint {

    protected static final String GLOBAL_CONSTRAINT = "GlobalConstraint";

    protected String name;

    protected List<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> constraints;

    protected List<IncQueryMatcher<? extends IPatternMatch>> matchers = new ArrayList<IncQueryMatcher<? extends IPatternMatch>>();

    public NoMatchGlobalConstraint(String name,
            List<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> constraints) {
        Preconditions.checkNotNull(name, "Name of the global constraint cannot be null.");
        Preconditions.checkNotNull(constraints, "The list of constraints cannot be null.");

        this.name = name;
        this.constraints = constraints;
    }

    public NoMatchGlobalConstraint(
            List<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> constraints) {
        this(GLOBAL_CONSTRAINT, constraints);
    }

    public NoMatchGlobalConstraint(String name) {
        this(name, new ArrayList<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>>());
    }

    public NoMatchGlobalConstraint() {
        this(GLOBAL_CONSTRAINT, new ArrayList<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>>());
    }

    /**
     * Adds a new IncQuery pattern.
     * @param constraint An IncQuery pattern.
     * @return The actual instance to enable builder pattern like usage.
     */
    public NoMatchGlobalConstraint addConstraint(
            IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> constraint) {
        constraints.add(constraint);
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean checkGlobalConstraint(ThreadContext context) {
        for (IncQueryMatcher<? extends IPatternMatch> matcher : matchers) {
            if (matcher.countMatches() > 0) {
                return false;
            }
        }
        return true;
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
    public IGlobalConstraint createNew() {
        return new NoMatchGlobalConstraint(name, constraints);
    }

}
