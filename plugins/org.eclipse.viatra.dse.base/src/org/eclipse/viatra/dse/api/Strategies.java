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
package org.eclipse.viatra.dse.api;

import org.eclipse.viatra.dse.api.strategy.Strategy;
import org.eclipse.viatra.dse.api.strategy.StrategyBase;
import org.eclipse.viatra.dse.api.strategy.StrategyBuildingBlocksManager;
import org.eclipse.viatra.dse.api.strategy.impl.SimpleStrategyComponent;

/**
 * Helper class for instantiating Strategies. To implement a new strategy use the {@link Strategy} class.
 * 
 * @author Andras Szabolcs Nagy
 * 
 */
public final class Strategies {

    private Strategies() {
    }

    private static final String BACKTRACK_WHEN_TRAVERSED_STATE_FOUND = "BacktrackWhenTraversedStateFound";
    private static final String CHECK_ALL_GOAL_PATTERN = "CheckAllGoalPattern";
    private static final String DEPTH_FIRST_NEXT_TRANSITION = "DepthFirstNextTransition";
    private static final String FIXED_PRIORITY_NEXT_TRANSITION = "FixedPriorityNextTransition";
    private static final String STOP_AT_FIRST_SOLUTION_FOUND = "StopAtFirstSolutionFound";
    private static final String FIND_ALL_SOLUTIONS = "FindAllSolutions";

    public static StrategyBase createSimpleDepthFirstStrategy() {
        return new StrategyBase(new SimpleStrategyComponent(),
                StrategyBuildingBlocksManager.INSTANCE.createCheckGoalStateByName(CHECK_ALL_GOAL_PATTERN),
                StrategyBuildingBlocksManager.INSTANCE.createNextTransitionByName(DEPTH_FIRST_NEXT_TRANSITION),
                StrategyBuildingBlocksManager.INSTANCE.createSolutionFoundByName(STOP_AT_FIRST_SOLUTION_FOUND),
                StrategyBuildingBlocksManager.INSTANCE
                        .createTraversedStateFoundByName(BACKTRACK_WHEN_TRAVERSED_STATE_FOUND));
    }

    public static StrategyBase createSimpleDepthFirstAllSolutionsStrategy() {
        return new StrategyBase(new SimpleStrategyComponent(),
                StrategyBuildingBlocksManager.INSTANCE.createCheckGoalStateByName(CHECK_ALL_GOAL_PATTERN),
                StrategyBuildingBlocksManager.INSTANCE.createNextTransitionByName(DEPTH_FIRST_NEXT_TRANSITION),
                StrategyBuildingBlocksManager.INSTANCE.createSolutionFoundByName(FIND_ALL_SOLUTIONS),
                StrategyBuildingBlocksManager.INSTANCE
                        .createTraversedStateFoundByName(BACKTRACK_WHEN_TRAVERSED_STATE_FOUND));
    }

    public static StrategyBase createFixedPriorityStrategy() {
        return new StrategyBase(new SimpleStrategyComponent(),
                StrategyBuildingBlocksManager.INSTANCE.createCheckGoalStateByName(CHECK_ALL_GOAL_PATTERN),
                StrategyBuildingBlocksManager.INSTANCE.createNextTransitionByName(FIXED_PRIORITY_NEXT_TRANSITION),
                StrategyBuildingBlocksManager.INSTANCE.createSolutionFoundByName(STOP_AT_FIRST_SOLUTION_FOUND),
                StrategyBuildingBlocksManager.INSTANCE
                        .createTraversedStateFoundByName(BACKTRACK_WHEN_TRAVERSED_STATE_FOUND));
    }

}
