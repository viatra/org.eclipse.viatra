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
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.objectives.IObjective;

import com.google.common.base.Preconditions;

/**
 * This hard objective collects a list of IncQuery pattern and checks if all of them has a match on a solution
 * (trajectoy). It is unsatisfied if any of them has no match returning 0 or it is satisfied if all of them has a match
 * returning 1.
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class AllMustHaveMatchHardObjective extends NoMatchHardObjective {

    public AllMustHaveMatchHardObjective(String name, List<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> constraints) {
        super(name, constraints);
    }

    public AllMustHaveMatchHardObjective(List<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> constraints) {
        this(HARD_OBJECTIVE, constraints);
    }

    public AllMustHaveMatchHardObjective(String name) {
        this(name, new ArrayList<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>>());
    }

    public AllMustHaveMatchHardObjective() {
        this(HARD_OBJECTIVE, new ArrayList<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>>());
    }

    @Override
    public Double getFitness(ThreadContext context) {

        for (IncQueryMatcher<? extends IPatternMatch> matcher : matchers) {
            if (matcher.countMatches() == 0) {
                return 0d;
            }
        }
        return 1d;
    }

    @Override
    public IObjective createNew() {
        
        AllMustHaveMatchHardObjective hardObjectiveCopy = new AllMustHaveMatchHardObjective(name, constraints);
        hardObjectiveCopy.setComparator(comparator);
        
        return hardObjectiveCopy;
    }

}
