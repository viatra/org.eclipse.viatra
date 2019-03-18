/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary.reproduction;

import java.util.List;

import org.eclipse.viatra.dse.evolutionary.interfaces.IReproductionStrategy;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public class SimpleReproductionStrategy implements IReproductionStrategy {

    private ReproductionStrategyType type;

    public enum ReproductionStrategyType {
        CURRENT_POPULATION, SURVIVED_POPULATION, FIRST_FRONT
    }

    public SimpleReproductionStrategy(ReproductionStrategyType type) {
        this.type = type;
    }

    @Override
    public List<TrajectoryFitness> getParentPopulation(List<TrajectoryFitness> currentPopulation,
            List<? extends List<TrajectoryFitness>> frontsOfCurrentPopulation,
            List<TrajectoryFitness> survivedPopulation) {
        if (type.equals(ReproductionStrategyType.CURRENT_POPULATION)) {
            return currentPopulation;
        } else if (type.equals(ReproductionStrategyType.FIRST_FRONT)) {
            return frontsOfCurrentPopulation.get(0);
        } else {
            return survivedPopulation;
        }
    }

}
