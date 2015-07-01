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
package org.eclipse.viatra.dse.genetic.selectors;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.genetic.core.InstanceData;
import org.eclipse.viatra.dse.genetic.interfaces.ISelectNextPopulation;
import org.eclipse.viatra.dse.objectives.Fitness;
import org.eclipse.viatra.dse.objectives.IObjective;
import org.eclipse.viatra.dse.objectives.ObjectiveComparatorHelper;

public class NonDominatedAndCrowdingDistanceSelector implements ISelectNextPopulation {

    private Logger logger = Logger.getLogger(getClass());

    @Override
    public List<InstanceData> selectNextPopulation(Collection<InstanceData> currentPopulation,
            List<IObjective> objectives, int numberOfSelectedInstances, boolean finalSelection, ObjectiveComparatorHelper helper, 
            boolean calcCrowdingDistanceForEachFront) {

        List<InstanceData> newPopulation = new LinkedList<InstanceData>();

        LinkedList<LinkedList<InstanceData>> fronts = nonDominatedSort(currentPopulation, objectives, helper, true);

        if (logger.getLevel() != null && logger.getLevel().equals(Level.DEBUG)) {
            StringBuilder sb = new StringBuilder();
            sb.append("First front:\n");
            for (InstanceData instance : fronts.getFirst()) {
                sb.append("\t---\n");
                instance.prettyPrint(sb);
            }
            logger.debug(sb.toString());
        }

        if (finalSelection) {
            return fronts.getFirst();
        } else {
            // Creating result from fronts
            for (LinkedList<InstanceData> front : fronts) {
                int newSize = newPopulation.size() + front.size();
                if (newSize <= numberOfSelectedInstances) {
                    newPopulation.addAll(front);
                    if (newSize == numberOfSelectedInstances) {
                        break;
                    }
                }
                // Selection by crowding distance
                else {
                    if (!calcCrowdingDistanceForEachFront) {
                        crowdingDistanceAssignment(front, objectives);
                    }
                    InstanceData[] sortedFront = sortByCrowdingDistance(front);
                    int size = newPopulation.size();
                    for (int i = 0; i < numberOfSelectedInstances - size; ++i) {
                        newPopulation.add(sortedFront[i]);
                    }
                    break;
                }
            }
        }

        return newPopulation;
    }

    @Override
    public boolean filtersDuplicates() {
        return false;
    }

    /**
     * Makes a fast non-domination sort of the specified InstanceDatas. The method returns the different domination
     * fronts in ascending order by their rank and sets their rank value.
     * 
     * @param population
     *            InstanceDatas to sort
     * @return domination fronts in ascending order by their rank
     */
    public static LinkedList<LinkedList<InstanceData>> nonDominatedSort(Collection<InstanceData> population,
            List<IObjective> objectives, ObjectiveComparatorHelper helper,
            boolean calcCrowdingDistanceForEachFront) {

        LinkedList<LinkedList<InstanceData>> dominationFronts = new LinkedList<LinkedList<InstanceData>>();

        HashMap<InstanceData, LinkedList<InstanceData>> dominatedInstances = new HashMap<InstanceData, LinkedList<InstanceData>>();
        HashMap<InstanceData, Integer> dominatingInstances = new HashMap<InstanceData, Integer>();

        // calculate dominations
        // TODO make this parallel or use dynamic programming
        for (InstanceData InstanceDataP : population) {
            dominatedInstances.put(InstanceDataP, new LinkedList<InstanceData>());
            dominatingInstances.put(InstanceDataP, 0);

            for (InstanceData InstanceDataQ : population) {
                int dominates = dominates(InstanceDataP, InstanceDataQ, helper);
                if (dominates > 0) {
                    dominatedInstances.get(InstanceDataP).add(InstanceDataQ);
                } else if (dominates < 0) {
                    dominatingInstances.put(InstanceDataP, dominatingInstances.get(InstanceDataP) + 1);
                }
            }

            if (dominatingInstances.get(InstanceDataP) == 0) {
                // p belongs to the first front
                InstanceDataP.rank = 1;
                if (dominationFronts.isEmpty()) {
                    LinkedList<InstanceData> firstDominationFront = new LinkedList<InstanceData>();
                    firstDominationFront.add(InstanceDataP);
                    dominationFronts.add(firstDominationFront);
                } else {
                    LinkedList<InstanceData> firstDominationFront = dominationFronts.getFirst();
                    firstDominationFront.add(InstanceDataP);
                }
            }
        }

        // create fronts
        int i = 1;
        while (dominationFronts.size() == i) {
            LinkedList<InstanceData> nextDominationFront = new LinkedList<InstanceData>();
            for (InstanceData InstanceDataP : dominationFronts.get(i - 1)) {
                for (InstanceData InstanceDataQ : dominatedInstances.get(InstanceDataP)) {
                    dominatingInstances.put(InstanceDataQ, dominatingInstances.get(InstanceDataQ) - 1);
                    if (dominatingInstances.get(InstanceDataQ) == 0) {
                        InstanceDataQ.rank = i + 1;
                        nextDominationFront.add(InstanceDataQ);
                    }
                }
            }
            i++;
            if (!nextDominationFront.isEmpty()) {
                crowdingDistanceAssignment(nextDominationFront, objectives);
                dominationFronts.add(nextDominationFront);
            }
        }

        return dominationFronts;
    }

    /**
     * Compares the two instances by the number of constraint violations and the objectives.
     * 
     * @param i1
     * @param i2
     * @param objectives
     *            The objective comparator helper.
     * @return -1 if i1 is dominated by i2 </br> +1 if i1 dominates i2 </br> 0 if they aren't dominated by each other
     */
    public static int dominates(InstanceData i1, InstanceData i2, ObjectiveComparatorHelper helper) {
        return helper.compare((Fitness) i1.objectives, (Fitness) i2.objectives);
    }

    /**
     * Executes the crowding distance assignment for the specified InstanceDatas (for a Front).
     * 
     * @param front
     *            InstanceDatas //all the specified InstanceDatas must belong to the same front
     */
    public static void crowdingDistanceAssignment(List<InstanceData> front,
            List<IObjective> objectives) {

        for (InstanceData InstanceData : front) {
            // initialize crowding distance
            InstanceData.crowdingDistance = 0;
        }

        for (final IObjective objective : objectives) {

            final String m = objective.getName();
            
            InstanceData[] sortedFront = front.toArray(new InstanceData[0]);

            // sort using m-th objective value
            Arrays.sort(sortedFront, new Comparator<InstanceData>() {
                @Override
                public int compare(InstanceData o1, InstanceData o2) {
                    objective.getComparator().compare(o1.getFitnessValue(m), o2.getFitnessValue(m));
                    return 0;
                }
            });

            // so that boundary points are always selected
            sortedFront[0].crowdingDistance = Double.POSITIVE_INFINITY;
            sortedFront[sortedFront.length - 1].crowdingDistance = Double.POSITIVE_INFINITY;

            // If minimal and maximal fitness value for this objective are
            // equal, then do not change crowding distance
            if (sortedFront[0].getFitnessValue(m) != sortedFront[sortedFront.length - 1].getFitnessValue(m)) {
                for (int i = 1; i < sortedFront.length - 1; i++) {
                    double newCrowdingDistance = sortedFront[i].crowdingDistance;
                    newCrowdingDistance += (sortedFront[i + 1].getFitnessValue(m) - sortedFront[i - 1]
                            .getFitnessValue(m))
                            / (sortedFront[sortedFront.length - 1].getFitnessValue(m) - sortedFront[0]
                                    .getFitnessValue(m));

                    sortedFront[i].crowdingDistance = newCrowdingDistance;
                }
            }
        }
    }

    /**
     * Returns the specified InstanceDatas sorted in ascending order by the crowding distance value.
     * 
     * @param InstanceDatas
     *            InstanceDatas to sort
     * @return InstanceDatas sorted in ascending order by the crowding distance value
     */
    public static InstanceData[] sortByCrowdingDistance(List<InstanceData> InstanceDatas) {

        InstanceData[] result = new InstanceData[InstanceDatas.size()];
        Arrays.sort(InstanceDatas.toArray(result), new Comparator<InstanceData>() {

            @Override
            public int compare(InstanceData o1, InstanceData o2) {
                return Double.compare(o1.crowdingDistance, o2.crowdingDistance);
            }
        });

        return result;
    }

}
