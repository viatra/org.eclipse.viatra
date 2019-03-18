/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary.survival;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.evolutionary.interfaces.ISurvivalStrategy;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;

public class FirstNSolutionsSurvivalStrategy implements ISurvivalStrategy {

    private int numberOfSelectedInstances = -1;

    public FirstNSolutionsSurvivalStrategy() {

    }

    public FirstNSolutionsSurvivalStrategy(int numberOfSelectedInstances) {
        this.numberOfSelectedInstances = numberOfSelectedInstances;
    }

    public void setNumberOfSelectedInstances(int numberOfSelectedInstances) {
        this.numberOfSelectedInstances = numberOfSelectedInstances;
    }

    @Override
    public void init(ThreadContext context) {
        Preconditions.checkArgument(numberOfSelectedInstances > 0, "Number of selected instances is not correctly set.");
    }

    @Override
    public List<TrajectoryFitness> selectSurvivedPopulation(
            List<? extends List<TrajectoryFitness>> frontsOfCurrentPopulation) {

        List<TrajectoryFitness> survivedPopulation = new ArrayList<>();

        // Creating result from fronts
        for (List<TrajectoryFitness> front : frontsOfCurrentPopulation) {
            int newSize = survivedPopulation.size() + front.size();
            if (newSize <= numberOfSelectedInstances) {
                survivedPopulation.addAll(front);
                if (newSize == numberOfSelectedInstances) {
                    break;
                }
            }
            // Selection by crowding distance
            else {
                TrajectoryFitness[] sortedFront = new TrajectoryFitness[front.size()];
                Arrays.sort(front.toArray(sortedFront), Comparator.comparingDouble(f -> f.crowdingDistance));
                int size = survivedPopulation.size();
                for (int i = 0; i < numberOfSelectedInstances - size; ++i) {
                    survivedPopulation.add(sortedFront[i]);
                }
                break;
            }
        }

        return survivedPopulation;
    }

}
