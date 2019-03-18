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

import org.eclipse.viatra.dse.base.DesignSpaceManager;
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
public class SwapTransitionCrossover implements ICrossover {

    private Random random = new Random();
    private Object[] parent1t;
    private Object[] parent2t;
    private int p1Size;
    private int p2Size;
    private int index1;
    private int index2;
    private DesignSpaceManager dsm;

    @Override
    public boolean mutate(TrajectoryFitness parent1, TrajectoryFitness parent2, ThreadContext context) {

        dsm = context.getDesignSpaceManager();

        parent1t = parent1.trajectory;
        parent2t = parent2.trajectory;
        p1Size = parent1t.length;
        p2Size = parent2t.length;

        if (p1Size < 2 || p2Size < 2) {
            return false;
        }

        index1 = random.nextInt(p1Size);
        index2 = random.nextInt(p2Size);

        dsm.executeTrajectoryWithMinimalBacktrackWithoutStateCoding(parent1t, index1);
        dsm.tryFireActivation(parent2t[index2]);
        context.executeTrajectoryByTryingWithoutStateCoding(parent1t, index1 + 1, p1Size);

        return true;
    }

    @Override
    public boolean mutateAlternate(TrajectoryFitness parent1, TrajectoryFitness parent2, ThreadContext context) {
        dsm.executeTrajectoryWithMinimalBacktrackWithoutStateCoding(parent2t, index2);
        dsm.tryFireActivation(parent1t[index1]);
        context.executeTrajectoryByTryingWithoutStateCoding(parent2t, index2 + 1, p2Size);
        return true;
    }

    @Override
    public ICrossover createNew() {
        return new SwapTransitionCrossover();
    }

}