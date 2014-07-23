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
package org.eclipse.viatra.dse.solutionstore;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.eclipse.viatra.dse.api.Solution;
import org.eclipse.viatra.dse.api.SolutionTrajectory;
import org.eclipse.viatra.dse.api.strategy.interfaces.ICheckGoalState;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;

public class UsefulnessSolutionStore implements ISolutionStore {

    private String key;

    private double solutionThreshold = 0.001;

    private int numberOfStoredTrajectories;
    private final boolean differentTrajectoryIsDifferentSolution;

    private final HashMap<SolutionTrajectory, Double> usefulnesses = new HashMap<SolutionTrajectory, Double>();
    private final HashMap<SolutionTrajectory, Object> stateIds = new HashMap<SolutionTrajectory, Object>();
    private final TreeSet<SolutionTrajectory> solutionsTrajectories = new TreeSet<SolutionTrajectory>(
            new Comparator<SolutionTrajectory>() {
                @Override
                public int compare(SolutionTrajectory o1, SolutionTrajectory o2) {
                    Double d1 = usefulnesses.get(o1);
                    Double d2 = usefulnesses.get(o2);
                    return d1.compareTo(d2);
                }
            });

    /**
     * Creates a new {@link UsefulnessSolutionStore} instance.
     * 
     * @param key
     *            The key in the measurements Map returned by {@link ICheckGoalState}.
     * @param differentTrajectoryIsDifferentSolution
     *            If set to true different trajectories will be handled as different solutions, although they can lead
     *            to the same state. False if only the end state matters.
     */
    public UsefulnessSolutionStore(String key, boolean differentTrajectoryIsDifferentSolution) {
        this.key = key;
        this.differentTrajectoryIsDifferentSolution = differentTrajectoryIsDifferentSolution;
    }

    /**
     * Creates a new {@link UsefulnessSolutionStore} instance.
     * 
     * @param key
     *            The key in the measurements Map
     */
    public UsefulnessSolutionStore(String key) {
        this(key, false);
    }

    @Override
    public synchronized Solution newSolution(ThreadContext context, Map<String, Double> measurements) {

        Double usefulness = measurements.get(key);

        if (solutionThreshold > 0 && solutionThreshold > usefulness) {
            return null;
        }

        if (numberOfStoredTrajectories == 0 || numberOfStoredTrajectories > solutionsTrajectories.size()) {
            saveTrajectory(context.getDesignSpaceManager(), usefulness);
        } else if (solutionsTrajectories.first().getUsefulness() > usefulness) {
            return null;
        } else {
            SolutionTrajectory worst = solutionsTrajectories.pollFirst();
            usefulnesses.remove(worst);
            stateIds.remove(worst);
            saveTrajectory(context.getDesignSpaceManager(), usefulness);
        }
        return null;
    }

    @Override
    public synchronized Collection<Solution> getSolutions() {
        Map<Object, Solution> solutions = new HashMap<Object, Solution>();

        for (SolutionTrajectory traj : solutionsTrajectories) {
            Object id = stateIds.get(traj);
            Solution solution = solutions.get(id);
            if (solution == null) {
                solution = new Solution(id, traj);
                solutions.put(id, solution);
            } else {
                solution.addTrajectory(traj);
            }
        }

        return solutions.values();
    }

    private void saveTrajectory(DesignSpaceManager dsm, Double usefulness) {
        SolutionTrajectory trajectory = dsm.createSolutionTrajectroy();
        usefulnesses.put(trajectory, usefulness);
        solutionsTrajectories.add(trajectory);
        stateIds.put(trajectory, dsm.getCurrentState().getId());
    }

}
