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
import org.eclipse.viatra.dse.api.strategy.interfaces.ICheckGoalState;
import org.eclipse.viatra.dse.base.ThreadContext;

/**
 * Checks all specified goal patterns, and marks the state as a goal state if all are satisfied. If there is no goal
 * pattern it won't mark the state as a goal state.
 * 
 * @see ICheckGoalState
 * 
 * @author Andras Szabolcs Nagy
 * 
 */
public class CheckAllGoals implements ICheckGoalState {

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
