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

import org.eclipse.viatra.dse.evolutionary.interfaces.ICrossover;
import org.eclipse.viatra.dse.evolutionary.interfaces.IEvaluationStrategy;
import org.eclipse.viatra.dse.evolutionary.interfaces.IEvolutionaryStrategyAdapter;
import org.eclipse.viatra.dse.evolutionary.interfaces.IInitialPopulationSelector;
import org.eclipse.viatra.dse.evolutionary.interfaces.IMutation;
import org.eclipse.viatra.dse.evolutionary.interfaces.IMutationRate;
import org.eclipse.viatra.dse.evolutionary.interfaces.IParentSelectionStrategy;
import org.eclipse.viatra.dse.evolutionary.interfaces.IReproductionStrategy;
import org.eclipse.viatra.dse.evolutionary.interfaces.IStopCondition;
import org.eclipse.viatra.dse.evolutionary.interfaces.ISurvivalStrategy;

public class EvolutionaryStrategyBuilder {

    private EvolutionaryStrategy strategy = new EvolutionaryStrategy();

    public EvolutionaryStrategy build() {
        return strategy;
    }

    public void setPopulationSize(int populationSize) {
        strategy.populationSize = populationSize;
    }

    public void setChildPopulationSize(int childPopulationSize) {
        strategy.childPopulationSize = childPopulationSize;
    }

    public void setInitialPopulationSelector(IInitialPopulationSelector initialPopulationSelector) {
        strategy.initialPopulationSelector = initialPopulationSelector;
    }

    public void setEvaluationStrategy(IEvaluationStrategy evaluationStrategy) {
        strategy.evaluationStrategy = evaluationStrategy;
    }

    public void setSurvivalStrategy(ISurvivalStrategy survivalStrategy) {
        strategy.survivalStrategy = survivalStrategy;
    }

    public void setReproductionStrategy(IReproductionStrategy reproductionStrategy) {
        strategy.reproductionStrategy = reproductionStrategy;
    }

    public void setParentSelectionStrategy(IParentSelectionStrategy parentSelectionStrategy) {
        strategy.parentSelectionStrategy = parentSelectionStrategy;
    }

    public void setStopCondition(IStopCondition stopCondition) {
        strategy.stopCondition = stopCondition;
    }

    public void setMutationRate(IMutationRate mutationRate) {
        strategy.mutationRate = mutationRate;
    }

    public void addCrossover(ICrossover crossover) {
        strategy.crossovers.add(crossover);
    }

    public void addCrossover(ICrossover crossover, int weight) {
        for (; weight > 0; weight--) {
            strategy.crossovers.add(crossover);
        }
    }

    public void addMutation(IMutation mutation) {
        strategy.mutations.add(mutation);
    }

    public void addMutation(IMutation mutation, int weight) {
        for (; weight > 0; weight--) {
            strategy.mutations.add(mutation);
        }
    }

    public void addStrategyAdapter(IEvolutionaryStrategyAdapter adapter) {
        strategy.adapters.add(adapter);
    }
}
