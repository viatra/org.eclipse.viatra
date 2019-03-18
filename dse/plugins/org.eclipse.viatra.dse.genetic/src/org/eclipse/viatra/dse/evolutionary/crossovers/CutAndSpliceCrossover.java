/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
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
public class CutAndSpliceCrossover implements ICrossover {

    private Random random = new Random();
    private Object[] parent1ts;
    private Object[] parent2ts;
    private int p1Size;
    private int p2Size;
    private int index1;
    private int index2;

    @Override
    public boolean mutate(TrajectoryFitness parent1, TrajectoryFitness parent2, ThreadContext context) {

        parent1ts = parent1.trajectory;
        parent2ts = parent2.trajectory;
        p1Size = parent1ts.length;
        p2Size = parent2ts.length;

        if (p1Size < 2 || p2Size < 2) {
            return false;
        }

        index1 = random.nextInt(p1Size - 1) + 1;
        index2 = random.nextInt(p2Size - 1) + 1;

        context.executeTrajectoryWithMinimalBacktrackWithoutStateCoding(parent1ts, index1);
        context.executeTrajectoryByTryingWithoutStateCoding(parent2ts, index2, p2Size);

        return true;
    }

    @Override
    public boolean mutateAlternate(TrajectoryFitness parent1, TrajectoryFitness parent2, ThreadContext context) {
        context.executeTrajectoryWithMinimalBacktrackWithoutStateCoding(parent2ts, index2);
        context.executeTrajectoryByTryingWithoutStateCoding(parent1ts, index1, p1Size);
        return true;
    }

    @Override
    public ICrossover createNew() {
        return new CutAndSpliceCrossover();
    }

}
