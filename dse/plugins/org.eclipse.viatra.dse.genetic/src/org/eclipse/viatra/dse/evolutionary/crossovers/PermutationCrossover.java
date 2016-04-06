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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.designspace.api.TrajectoryInfo;
import org.eclipse.viatra.dse.evolutionary.interfaces.ICrossover;
import org.eclipse.viatra.dse.genetic.core.GeneticHelper;
import org.eclipse.viatra.dse.genetic.core.InstanceData;
import org.eclipse.viatra.dse.objectives.Fitness;
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

    @Override
    public TrajectoryFitness[] mutate(TrajectoryFitness parent1, TrajectoryFitness parent2, ThreadContext context) {

        TrajectoryFitness[] children = new TrajectoryFitness[2];
        DesignSpaceManager dsm = context.getDesignSpaceManager();
        TrajectoryInfo trajectoryInfo = dsm.getTrajectoryInfo();

        ITransition[] parent1t = parent1.trajectory;
        ITransition[] parent2t = parent2.trajectory;
        int p1Size = parent1t.length;
        int p2Size = parent2t.length;

        if (p1Size < 2 || p2Size < 2) {
            throw new DSEException("Cannot crossover with empty or one long parent trajectories.");
        }

        int minSize = Math.min(p1Size, p2Size);
        int index = random.nextInt(minSize);

        for (int i = 0; i < index; i++) {
            dsm.fireActivation(parent1t[i]);
        }
        addPermutation(dsm, trajectoryInfo, parent2t);

        Fitness fitness = context.calculateFitness();
        children[0] = new TrajectoryFitness(dsm.getTrajectoryInfo(), fitness);

        dsm.undoUntilRoot();

        for (int i = 0; i < index; i++) {
            dsm.fireActivation(parent2t[i]);
        }
        addPermutation(dsm, trajectoryInfo, parent1t);

        fitness = context.calculateFitness();
        children[1] = new TrajectoryFitness(dsm.getTrajectoryInfo(), fitness);

        dsm.undoUntilRoot();

        return children;
    }

    private void addPermutation(DesignSpaceManager dsm, TrajectoryInfo trajectoryInfo, ITransition[] parent2t) {
        outerLoop: for (ITransition transitionToAdd : parent2t) {

            Object transitionToAddId = transitionToAdd.getId();

            for (ITransition childTransition : trajectoryInfo.getFullTransitionTrajectory()) {
                Object id = childTransition.getId();
                if (transitionToAddId.equals(id)) {
                    continue outerLoop;
                }
            }

            GeneticHelper.tryFireRightTransition(dsm, transitionToAdd);
        }
    }

}