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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.dse.api.PatternWithCardinality;
import org.eclipse.viatra.dse.api.Solution;
import org.eclipse.viatra.dse.api.strategy.Strategy;
import org.eclipse.viatra.dse.api.strategy.interfaces.ICheckConstraints;
import org.eclipse.viatra.dse.api.strategy.interfaces.ICheckGoalState;
import org.eclipse.viatra.dse.api.strategy.interfaces.ISolutionFound;
import org.eclipse.viatra.dse.api.strategy.interfaces.ITraversedStateFound;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.IState.TraversalStateType;

/**
 * 
 * Implements four of the strategy components interfaces:
 * <ul>
 * <li>Does nothing, when a traversed state found.</li>
 * <li>Checks all global constraints, but does nothing when the global constraints are not satisfied.</li>
 * <li>Does nothing when a solution is found. In effect, if the {@link Strategy} does not stop for some other reason,
 * the exploration process will continue indefinitely.</li>
 * <li>Checks all specified goal patterns, and marks the state as a goal state if all are satisfied. If there is no goal
 * pattern it won't mark the state as a goal state.</li>
 * </ul>
 * 
 * @see ITraversedStateFound
 * @see ISolutionFound
 * @see ICheckConstraints
 * @see ICheckGoalState
 * 
 * @author Andras Szabolcs Nagy
 * 
 */
public class SimpleStrategyComponent implements ITraversedStateFound, ISolutionFound, ICheckConstraints,
        ICheckGoalState {

    public SimpleStrategyComponent() {
    }

    @Override
    public ExecutationType solutionFound(ThreadContext context, Solution solution) {
        return ExecutationType.CONTINUE;
    }

    @Override
    public void traversedStateFound(ThreadContext context, TraversalStateType traversalState) {
        return;
    }

    @Override
    public boolean checkConstraints(ThreadContext context) {
        for (PatternWithCardinality constraint : context.getGlobalContext().getConstraints()) {
            if (!constraint.isPatternSatisfied(context.getIncqueryEngine())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Map<String, Double> isGoalState(ThreadContext context) {
        Set<PatternWithCardinality> goalPatterns = context.getGlobalContext().getGoalPatterns();

        if (goalPatterns.isEmpty()) {
            return null;
        }

        for (PatternWithCardinality goalPattern : goalPatterns) {
            if (!goalPattern.isPatternSatisfied(context.getIncqueryEngine())) {
                return null;
            }
        }

        return new HashMap<String, Double>();
    }

}
