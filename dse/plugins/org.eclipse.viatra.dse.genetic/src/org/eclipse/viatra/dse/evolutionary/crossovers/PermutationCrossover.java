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
import org.eclipse.viatra.dse.designspace.api.TrajectoryInfo;
import org.eclipse.viatra.dse.evolutionary.GeneticHelper;
import org.eclipse.viatra.dse.evolutionary.interfaces.ICrossover;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

/**
 * Makes two child trajectories from two parent trajectories. <br/>
 * <br/>
 * A single crossover point on both parents' trajectories is selected randomly. All transitions beyond that point in
 * either trajectory is swapped between the two parent trajectories. The resulting trajectories are the children. <br/>
 * 
 */
public class PermutationCrossover implements ICrossover {

    private Random random = new Random();
    private Object[] parent1t;
    private Object[] parent2t;
    private int p1Size;
    private int p2Size;
    private int index;

    @Override
    public boolean mutate(TrajectoryFitness parent1, TrajectoryFitness parent2, ThreadContext context) {

        DesignSpaceManager dsm = context.getDesignSpaceManager();
        TrajectoryInfo trajectoryInfo = dsm.getTrajectoryInfo();

        parent1t = parent1.trajectory;
        parent2t = parent2.trajectory;
        p1Size = parent1t.length;
        p2Size = parent2t.length;

        if (p1Size < 2 || p2Size < 2) {
            return false;
        }

        int minSize = Math.min(p1Size, p2Size);
        index = random.nextInt(minSize);

        dsm.executeTrajectoryWithMinimalBacktrackWithoutStateCoding(parent1t, index);
        addPermutation(dsm, trajectoryInfo, parent2t);

        return true;
    }

    private void addPermutation(DesignSpaceManager dsm, TrajectoryInfo trajectoryInfo, Object[] parent) {
        outerLoop: for (Object transitionToAddId : parent) {

            for (Object childTransition : trajectoryInfo.getTrajectory()) {
                Object id = childTransition;
                if (transitionToAddId.equals(id)) {
                    continue outerLoop;
                }
            }

            GeneticHelper.tryFireRightTransition(dsm, transitionToAddId);
        }
    }

    @Override
    public boolean mutateAlternate(TrajectoryFitness parent1, TrajectoryFitness parent2, ThreadContext context) {
        DesignSpaceManager dsm = context.getDesignSpaceManager();
        dsm.executeTrajectoryWithMinimalBacktrackWithoutStateCoding(parent2t, index);
        addPermutation(dsm, dsm.getTrajectoryInfo(), parent1t);
        return true;
    }

    @Override
    public ICrossover createNew() {
        return new PermutationCrossover();
    }

}