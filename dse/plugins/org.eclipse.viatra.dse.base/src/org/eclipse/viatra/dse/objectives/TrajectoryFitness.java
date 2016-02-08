/*******************************************************************************
 * Copyright (c) 2010-2015, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.objectives;

import java.util.Arrays;
import java.util.List;

import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.designspace.api.TrajectoryInfo;

/**
 * This class represents a trajectory and its fitness.
 * @author Andras Szabolcs Nagy
 *
 */
public class TrajectoryFitness {

    public ITransition[] trajectory;
    public Fitness fitness;

    public int rank;
    public double crowdingDistance;

    private int hash;

    /**
     * Creates a {@link TrajectoryFitness} with the full trajectory.
     * @param trajectory The trajectory.
     * @param fitness The fitness.
     */
    public TrajectoryFitness(ITransition[] trajectory, Fitness fitness) {
        this.fitness = fitness;
        this.trajectory = trajectory;
    }

    /**
     * Creates a {@link TrajectoryFitness} with the full trajectory.
     * @param trajectoryInfo The trajectory.
     * @param fitness The fitness.
     */
    public TrajectoryFitness(TrajectoryInfo trajectoryInfo, Fitness fitness) {
        this.fitness = fitness;
        List<ITransition> fullTraj = trajectoryInfo.getFullTransitionTrajectory();
        trajectory = fullTraj.toArray(new ITransition[fullTraj.size()]);
    }

    /**
     * Creates a {@link TrajectoryFitness} with the given {@link ITransition}
     * @param transition The transition.
     * @param fitness The fitness.
     */
    public TrajectoryFitness(ITransition transition, Fitness fitness) {
        this.fitness = fitness;
        trajectory = new ITransition[] {transition};
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TrajectoryFitness) {
            return Arrays.equals(trajectory, ((TrajectoryFitness) obj).trajectory);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (hash == 0 && trajectory.length > 0) {
            hash = Arrays.hashCode(trajectory);
        }
        return hash;
    }

    @Override
    public String toString() {
        return Arrays.toString(trajectory) + fitness.toString();
    }
}
