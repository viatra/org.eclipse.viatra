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
import org.eclipse.viatra.dse.api.strategy.interfaces.ISolutionFoundHandler;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;

/**
 * It stores the best n solutions found so far based on a single objective. It will never stop the exploration.
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class SingleObjectiveSolutionStore implements ISolutionStore {

    private String key;
    private int solutionsToStore;

    private final HashMap<SolutionTrajectory, Object> stateIds = new HashMap<SolutionTrajectory, Object>();
    private final TreeSet<SolutionTrajectory> solutionsTrajectories;

    /**
     * It stores only the best solution (with highest fitness) found so far.
     * 
     * @param key
     *            The key in the measurements Map
     */
    public SingleObjectiveSolutionStore(String key) {
        this(key, 1, null);
    }

    /**
     * 
     * It stores the given number of best solutions (with highest fitness) found so far.
     * 
     * @param key
     *            The key in the measurements Map
     * @param solutionsToStore
     *            The number of solutions to store.
     */
    public SingleObjectiveSolutionStore(String key, int solutionsToStore) {
        this(key, solutionsToStore, null);
    }

    /**
     * @param key
     *            The key in the measurements Map
     * @param solutionsToStore
     *            The number of solutions to store.
     * @param comparator
     *            A comparator to compare the objective values. It can be used to store a solutions with the least
     *            fitness or with the least difference from a certain number instead of the utmost solutions.
     */
    public SingleObjectiveSolutionStore(final String key, int solutionsToStore, final Comparator<Double> comparator) {
        this.key = key;
        this.solutionsToStore = solutionsToStore;
        if (comparator != null) {
            solutionsTrajectories = new TreeSet<SolutionTrajectory>(new Comparator<SolutionTrajectory>() {
                @Override
                public int compare(SolutionTrajectory o1, SolutionTrajectory o2) {
                    Double d1 = o1.getObjectives().get(key);
                    Double d2 = o2.getObjectives().get(key);
                    return comparator.compare(d1, d2);
                }
            });
        } else {
            solutionsTrajectories = new TreeSet<SolutionTrajectory>(new Comparator<SolutionTrajectory>() {
                @Override
                public int compare(SolutionTrajectory o1, SolutionTrajectory o2) {
                    Double d1 = o1.getObjectives().get(key);
                    Double d2 = o2.getObjectives().get(key);
                    return d1.compareTo(d2);
                }
            });
        }

    }

    @Override
    public synchronized StopExecutionType newSolution(ThreadContext context, Map<String, Double> objectives) {

        Double fitness = objectives.get(key);

        if (solutionsToStore <= 0 || solutionsToStore > solutionsTrajectories.size()) {
            saveTrajectory(context.getDesignSpaceManager(), fitness);
        } else if (solutionsTrajectories.first().getObjectives().get(key) > fitness) {
            return StopExecutionType.CONTINUE;
        } else {
            SolutionTrajectory worst = solutionsTrajectories.pollFirst();
            stateIds.remove(worst);
            saveTrajectory(context.getDesignSpaceManager(), fitness);
        }

        return StopExecutionType.CONTINUE;
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
        solutionsTrajectories.add(trajectory);
        stateIds.put(trajectory, dsm.getCurrentState().getId());
    }

    @Override
    public void registerSolutionFoundHandler(ISolutionFoundHandler handler) {
        throw new UnsupportedOperationException();
    }

}
