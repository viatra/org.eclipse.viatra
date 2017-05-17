/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary.crossovers;

import java.util.Random;

import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.evolutionary.interfaces.ICrossover;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

/**
 * Makes two child trajectories from two parent trajectories. <br/>
 * <br/>
 * A single crossover point on both parents' trajectories is selected randomly. All transitions beyond that point in
 * either trajectory is swapped between the two parent trajectories. The resulting trajectories are the children. <br/>
 * 
 */
public class OnePointCrossover implements ICrossover {

    private Random random = new Random();
    private int index;
    private Object[] parent1ts;
    private Object[] parent2ts;
    private int p1Size;
    private int p2Size;

    @Override
    public boolean mutate(TrajectoryFitness parent1, TrajectoryFitness parent2, ThreadContext context) {

        parent1ts = parent1.trajectory;
        parent2ts = parent2.trajectory;
        p1Size = parent1ts.length;
        p2Size = parent2ts.length;

        if (p1Size < 2 || p2Size < 2) {
            return false;
        }

        int minSize = Math.min(p1Size, p2Size);
        index = random.nextInt(minSize - 1) + 1;

        context.executeTrajectoryWithMinimalBacktrackWithoutStateCoding(parent1ts, index);
        context.executeTrajectoryByTryingWithoutStateCoding(parent2ts, index, p2Size);

        return true;
    }

    @Override
    public boolean mutateAlternate(TrajectoryFitness parent1, TrajectoryFitness parent2, ThreadContext context) {
        context.executeTrajectoryWithMinimalBacktrackWithoutStateCoding(parent2ts, index);
        context.executeTrajectoryByTryingWithoutStateCoding(parent1ts, index, p1Size);
        return true;
    }

    @Override
    public ICrossover createNew() {
        return new OnePointCrossover();
    }

}
