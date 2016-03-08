/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary;

import java.util.Collection;
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
    public List<? extends List<TrajectoryFitness>> evaluatePopulation(Collection<TrajectoryFitness> currentPopulation) {
        return helper.getFronts();
    }

}
