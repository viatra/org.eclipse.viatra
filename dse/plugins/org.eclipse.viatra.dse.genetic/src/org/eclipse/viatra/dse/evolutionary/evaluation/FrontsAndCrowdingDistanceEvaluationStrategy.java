/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary.evaluation;

import java.util.List;

import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.evolutionary.interfaces.IEvaluationStrategy;
import org.eclipse.viatra.dse.objectives.ObjectiveComparatorHelper;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public class FrontsAndCrowdingDistanceEvaluationStrategy implements IEvaluationStrategy {

    private ObjectiveComparatorHelper helper;

    @Override
    public void init(ThreadContext context) {
        helper = context.getObjectiveComparatorHelper();
        helper.setComputeCrowdingDistance(true);
    }

    @Override
    public List<? extends List<TrajectoryFitness>> evaluatePopulation(List<TrajectoryFitness> currentPopulation) {
        
        for (TrajectoryFitness trajectoryFitness : currentPopulation) {
            helper.addTrajectoryFitness(trajectoryFitness);
        }
        List<? extends List<TrajectoryFitness>> fronts = helper.getFronts();
        helper.clearTrajectoryFitnesses();
        return fronts;
    }

}
