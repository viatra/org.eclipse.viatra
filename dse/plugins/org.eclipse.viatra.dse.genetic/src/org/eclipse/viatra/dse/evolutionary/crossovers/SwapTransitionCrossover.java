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

import java.util.Arrays;
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

        dsm.executeTrajectoryWithoutStateCoding(parent1t, index1);
        dsm.tryFireActivation(parent2t[index2]);
        Object[] trajectoryEnd1 = Arrays.copyOfRange(parent1t, index1 + 1, p1Size);
        context.executeTrajectoryByTryingWithoutStateCoding(trajectoryEnd1);

        return true;
    }

    @Override
    public boolean mutateAlternate(TrajectoryFitness parent1, TrajectoryFitness parent2, ThreadContext context) {
        dsm.executeTrajectoryWithoutStateCoding(parent2t, index2);
        dsm.tryFireActivation(parent1t[index1]);
        Object[] trajectoryEnd2 = Arrays.copyOfRange(parent2t, index2 + 1, p2Size);
        context.executeTrajectoryByTryingWithoutStateCoding(trajectoryEnd2);
        return true;
    }

    @Override
    public ICrossover createNew() {
        return new SwapTransitionCrossover();
    }

}