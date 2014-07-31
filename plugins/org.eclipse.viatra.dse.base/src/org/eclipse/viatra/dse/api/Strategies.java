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
import org.eclipse.viatra.dse.api.strategy.impl.ConfigurableSoultionFound;
import org.eclipse.viatra.dse.api.strategy.impl.DepthFirstNextTransition;
import org.eclipse.viatra.dse.api.strategy.impl.FixedPriorityNextTransition;
import org.eclipse.viatra.dse.api.strategy.impl.ParallelBFSNextTransition;

/**
 * Helper class for instantiating Strategies. To implement a new strategy use the {@link Strategy} class.
 * 
 * @author Andras Szabolcs Nagy
 * 
 */
public final class Strategies {

    private Strategies() {
    }

    public static StrategyBase createDFSStrategy() {
        return createDFSStrategy(0, 0);
    }

    public static StrategyBase createDFSStrategy(int numOfSolutionsToFind) {
        return createDFSStrategy(numOfSolutionsToFind, 0);
    }

    public static StrategyBase createDFSStrategy(int numOfSolutionsToFind, int depthLimit) {
        StrategyBase strategyBase = new StrategyBase(new DepthFirstNextTransition(depthLimit));
        strategyBase.setSolutionFoundHandler(new ConfigurableSoultionFound(numOfSolutionsToFind));
        return strategyBase;
    }

    public static StrategyBase createFixedPriorityStrategy() {
        return createFixedPriorityStrategy(0, 0, true);
    }

    public static StrategyBase createFixedPriorityStrategy(int numOfSolutionsToFind) {
        return createFixedPriorityStrategy(numOfSolutionsToFind, 0, true);
    }

    public static StrategyBase createFixedPriorityStrategy(int numOfSolutionsToFind, int depthLimit) {
        return createFixedPriorityStrategy(numOfSolutionsToFind, depthLimit, true);
    }

    public static StrategyBase createFixedPriorityStrategy(int numOfSolutionsToFind, int depthLimit,
            boolean tryHigherPriorityFirst) {
        StrategyBase strategyBase = new StrategyBase(new FixedPriorityNextTransition(tryHigherPriorityFirst, true,
                depthLimit));
        strategyBase.setSolutionFoundHandler(new ConfigurableSoultionFound(numOfSolutionsToFind));
        return strategyBase;
    }

    public static StrategyBase createBFSStrategy() {
        return createBFSStrategy(0, 0);
    }

    public static StrategyBase createBFSStrategy(int numOfSolutionsToFind) {
        return createBFSStrategy(numOfSolutionsToFind, 0);
    }

    public static StrategyBase createBFSStrategy(int numOfSolutionsToFind, int depthLimit) {
        StrategyBase strategyBase = new StrategyBase(new ParallelBFSNextTransition(depthLimit));
        strategyBase.setSolutionFoundHandler(new ConfigurableSoultionFound(numOfSolutionsToFind));
        return strategyBase;
    }
}
