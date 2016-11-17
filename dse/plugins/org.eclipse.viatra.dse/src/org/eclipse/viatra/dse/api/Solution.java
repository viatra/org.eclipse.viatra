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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Solution {

    private Set<SolutionTrajectory> trajectories;
    private final Object stateId;

    public Solution(Object stateId, SolutionTrajectory trajectory) {
        this.stateId = stateId;
        trajectories = new HashSet<>();
        trajectories.add(trajectory);
    }

    public void addTrajectory(SolutionTrajectory trajectory) {
        trajectories.add(trajectory);
    }

    public SolutionTrajectory getArbitraryTrajectory() {
        return trajectories.iterator().next();
    }

    public SolutionTrajectory getShortestTrajectory() {
        Iterator<SolutionTrajectory> iterator = trajectories.iterator();
        SolutionTrajectory shortestTrajecotry = iterator.next();
        int minSize = shortestTrajecotry.getTrajectoryLength();

        while (iterator.hasNext()) {
            SolutionTrajectory traj = iterator.next();
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
