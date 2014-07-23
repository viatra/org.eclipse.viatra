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

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Solution {

    private ConcurrentLinkedQueue<SolutionTrajectory> trajectories;
    private final Object stateId;

    public Solution(Object stateId, SolutionTrajectory trajectory) {
        this.stateId = stateId;
        trajectories = new ConcurrentLinkedQueue<SolutionTrajectory>();
        trajectories.add(trajectory);
    }

    public void addTrajectory(SolutionTrajectory trajectory) {
        trajectories.add(trajectory);
    }

    public SolutionTrajectory getArbitraryTrajectory() {
        return trajectories.peek();
    }

    public SolutionTrajectory getShortestTrajectory() {
        if (trajectories.size() == 1) {
            return trajectories.peek();
        }
        SolutionTrajectory shortestTrajecotry = trajectories.peek();
        int minSize = shortestTrajecotry.getTrajectoryLength();
        for (SolutionTrajectory traj : trajectories) {
            int size = traj.getTrajectoryLength();
            if (size < minSize) {
                shortestTrajecotry = traj;
                minSize = size;
            }
        }
        return shortestTrajecotry;
    }

    public Collection<SolutionTrajectory> getTrajectories() {
        return trajectories;
    }

    public Object getStateCode() {
        return stateId;
    }

}
