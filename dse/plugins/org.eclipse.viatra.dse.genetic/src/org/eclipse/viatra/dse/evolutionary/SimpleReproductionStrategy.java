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

import org.eclipse.viatra.dse.evolutionary.interfaces.IReproductionStrategy;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public class SimpleReproductionStrategy implements IReproductionStrategy {

    private ReproductionStrategyType type;

    public enum ReproductionStrategyType {
        CURRENT_POPULATION, SURVIVED_POPULATION
    }

    public SimpleReproductionStrategy(ReproductionStrategyType type) {
        this.type = type;
    }

    @Override
    public Collection<TrajectoryFitness> getParentPopulation(Collection<TrajectoryFitness> currentPopulation,
            Collection<TrajectoryFitness> survivedPopulation) {
        if (type.equals(ReproductionStrategyType.CURRENT_POPULATION)) {
            return currentPopulation;
        } else {
            return survivedPopulation;
        }
    }

}
