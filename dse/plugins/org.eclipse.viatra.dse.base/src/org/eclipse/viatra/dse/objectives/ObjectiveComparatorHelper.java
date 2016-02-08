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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class is responsible to compare and sort fitness values. {@link TrajectoryFitness} instances can be added to an
 * instance of this class, that it can sort them.
 * 
 * @author András Szabolcs Nagy
 */
public class ObjectiveComparatorHelper {

    private IObjective[][] leveledObjectives;
    private List<TrajectoryFitness> trajectoryFitnesses = new ArrayList<TrajectoryFitness>();
    private Random random = new Random();

    public ObjectiveComparatorHelper(IObjective[][] leveledObjectives) {
        this.leveledObjectives = leveledObjectives;
    }

    /**
     * Compares two fitnesses based on hierarchical dominance. Returns -1 if the second parameter {@code o2} is a better
     * solution ({@code o2} dominates {@code o1}), 1 if the first parameter {@code o1} is better ({@code o1} dominates
     * {@code o2}) and returns 0 if they are non-dominating each other.
     * 
     * @param o1
     * @param o2
     * @return
     */
    public int compare(Fitness o1, Fitness o2) {

        levelsLoop: for (int i = 0; i < leveledObjectives.length; i++) {

            boolean o1HasBetterFitness = false;
            boolean o2HasBetterFitness = false;

            for (IObjective objective : leveledObjectives[i]) {
                String objectiveName = objective.getName();
                int sgn = objective.getComparator().compare(o1.get(objectiveName), o2.get(objectiveName));

                if (sgn < 0) {
                    o2HasBetterFitness = true;
                }
                if (sgn > 0) {
                    o1HasBetterFitness = true;
                }
                if (o1HasBetterFitness && o2HasBetterFitness) {
                    continue levelsLoop;
                }
            }
            if (o2HasBetterFitness && !o1HasBetterFitness) {
                return -1;
            } else if (!o2HasBetterFitness && o1HasBetterFitness) {
                return 1;
            }
        }

        return 0;

    }

    /**
     * Adds a {@link TrajectoryFitness} to an inner list to compare later.
     * @param trajectoryFitness
     */
    public void addTrajectoryFitness(TrajectoryFitness trajectoryFitness) {
        trajectoryFitnesses.add(trajectoryFitness);
    }

    /**
     * Clears the inner {@link TrajectoryFitness} list.
     */
    public void clearTrajectoryFitnesses() {
        trajectoryFitnesses.clear();
    }

    /**
     * Returns the inner {@link TrajectoryFitness} list.
     * @return
     */
    public List<TrajectoryFitness> getTrajectoryFitnesses() {
        return trajectoryFitnesses;
    }

    /**
     * Returns a random {@link TrajectoryFitness} from the pareto front.
     * @return
     */
    public TrajectoryFitness getRandomBest() {
        List<TrajectoryFitness> paretoFront = getParetoFront();
        int randomIndex = random.nextInt(paretoFront.size());
        return paretoFront.get(randomIndex);
    }

    /**
     * Returns the pareto front of the previously added {@link TrajectoryFitness}.
     * @return
     */
    public List<TrajectoryFitness> getParetoFront() {
        return getFronts().get(0);
    }

    /**
     * Returns the previously added {@link TrajectoryFitness} instances in fronts.
     * @return
     */
    public List<ArrayList<TrajectoryFitness>> getFronts() {
        List<ArrayList<TrajectoryFitness>> fronts = new ArrayList<ArrayList<TrajectoryFitness>>();

        Map<TrajectoryFitness, ArrayList<TrajectoryFitness>> dominatedInstances = new HashMap<TrajectoryFitness, ArrayList<TrajectoryFitness>>();
        Map<TrajectoryFitness, Integer> dominatingInstances = new HashMap<TrajectoryFitness, Integer>();

        // calculate dominations
        for (TrajectoryFitness TrajectoryFitnessP : trajectoryFitnesses) {
            dominatedInstances.put(TrajectoryFitnessP, new ArrayList<TrajectoryFitness>());
            dominatingInstances.put(TrajectoryFitnessP, 0);

            for (TrajectoryFitness TrajectoryFitnessQ : trajectoryFitnesses) {
                int dominates = compare(TrajectoryFitnessP.fitness, TrajectoryFitnessQ.fitness);
                if (dominates > 0) {
                    dominatedInstances.get(TrajectoryFitnessP).add(TrajectoryFitnessQ);
                } else if (dominates < 0) {
                    dominatingInstances.put(TrajectoryFitnessP, dominatingInstances.get(TrajectoryFitnessP) + 1);
                }
            }

            if (dominatingInstances.get(TrajectoryFitnessP) == 0) {
                // p belongs to the first front
                TrajectoryFitnessP.rank = 1;
                if (fronts.isEmpty()) {
                    ArrayList<TrajectoryFitness> firstDominationFront = new ArrayList<TrajectoryFitness>();
                    firstDominationFront.add(TrajectoryFitnessP);
                    fronts.add(firstDominationFront);
                } else {
                    List<TrajectoryFitness> firstDominationFront = fronts.get(0);
                    firstDominationFront.add(TrajectoryFitnessP);
                }
            }
        }

        // create fronts
        int i = 1;
        while (fronts.size() == i) {
            ArrayList<TrajectoryFitness> nextDominationFront = new ArrayList<TrajectoryFitness>();
            for (TrajectoryFitness TrajectoryFitnessP : fronts.get(i - 1)) {
                for (TrajectoryFitness TrajectoryFitnessQ : dominatedInstances.get(TrajectoryFitnessP)) {
                    dominatingInstances.put(TrajectoryFitnessQ, dominatingInstances.get(TrajectoryFitnessQ) - 1);
                    if (dominatingInstances.get(TrajectoryFitnessQ) == 0) {
                        TrajectoryFitnessQ.rank = i + 1;
                        nextDominationFront.add(TrajectoryFitnessQ);
                    }
                }
            }
            i++;
            if (!nextDominationFront.isEmpty()) {
                fronts.add(nextDominationFront);
            }
        }

        return fronts;
    }

}
