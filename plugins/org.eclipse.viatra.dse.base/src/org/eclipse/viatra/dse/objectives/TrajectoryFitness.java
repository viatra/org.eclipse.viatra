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

    private int hashCode;

    public TrajectoryFitness(TrajectoryInfo trajectoryInfo, Fitness fitness) {
        this.fitness = fitness;
        List<ITransition> fullTraj = trajectoryInfo.getFullTransitionTrajectory();
        trajectory = fullTraj.toArray(new ITransition[fullTraj.size()]);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Object[]) {
            return Arrays.equals(trajectory, (Object[]) obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0 && trajectory.length > 0) {
            hashCode = Arrays.hashCode(trajectory);
        }
        return hashCode;
    }

    @Override
    public String toString() {
        return trajectory.toString() + fitness.toString();
    }
}
