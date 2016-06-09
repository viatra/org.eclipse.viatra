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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Solution {

    private List<SolutionTrajectory> trajectories;
    private final Object stateId;

    public Solution(Object stateId, SolutionTrajectory trajectory) {
        this.stateId = stateId;
        trajectories = new ArrayList<>();
        trajectories.add(trajectory);
    }

    public void addTrajectory(SolutionTrajectory trajectory) {
        trajectories.add(trajectory);
    }

    public SolutionTrajectory getArbitraryTrajectory() {
        return trajectories.get(0);
    }

    public SolutionTrajectory getShortestTrajectory() {
        if (trajectories.size() == 1) {
            return trajectories.get(0);
        }
        SolutionTrajectory shortestTrajecotry = trajectories.get(0);
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
