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

        DesignSpaceManager dsm = context.getDesignSpaceManager();

        parent1ts = parent1.trajectory;
        parent2ts = parent2.trajectory;
        p1Size = parent1ts.length;
        p2Size = parent2ts.length;

        if (p1Size < 2 || p2Size < 2) {
            return false;
        }

        index1 = random.nextInt(p1Size - 1) + 1;
        index2 = random.nextInt(p2Size - 1) + 1;

        dsm.executeTrajectoryWithoutStateCoding(parent1ts, index1);
        Object[] trajectoryEnd1 = Arrays.copyOfRange(parent2ts, index2, p2Size);
        context.executeTrajectoryByTryingWithoutStateCoding(trajectoryEnd1);

        return true;
    }

    @Override
    public boolean mutateAlternate(TrajectoryFitness parent1, TrajectoryFitness parent2, ThreadContext context) {
        // TODO Auto-generated method stub
        context.getDesignSpaceManager().executeTrajectory(parent2ts, index2);
        Object[] trajectoryEnd2 = Arrays.copyOfRange(parent1ts, index1, p1Size);
        context.executeTrajectoryByTryingWithoutStateCoding(trajectoryEnd2);
        return true;
    }

    @Override
    public ICrossover createNew() {
        return new CutAndSpliceCrossover();
    }

}
