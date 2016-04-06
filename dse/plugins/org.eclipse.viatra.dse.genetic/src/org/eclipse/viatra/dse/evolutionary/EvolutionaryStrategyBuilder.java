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

import org.eclipse.viatra.dse.evolutionary.crossovers.CutAndSpliceCrossover;
import org.eclipse.viatra.dse.evolutionary.crossovers.SwapTransitionCrossover;
import org.eclipse.viatra.dse.evolutionary.evaluation.FrontsAndCrowdingDistanceEvaluationStrategy;
import org.eclipse.viatra.dse.evolutionary.initialselectors.BfsInitialSelector;
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
import org.eclipse.viatra.dse.evolutionary.mutationrate.SimpleMutationRate;
import org.eclipse.viatra.dse.evolutionary.mutations.AddRandomTransitionMutation;
import org.eclipse.viatra.dse.evolutionary.mutations.ModifyRandomTransitionMutation;
import org.eclipse.viatra.dse.evolutionary.parentselectors.CrowdedTournementParentSelector;
import org.eclipse.viatra.dse.evolutionary.reproduction.SimpleReproductionStrategy;
import org.eclipse.viatra.dse.evolutionary.reproduction.SimpleReproductionStrategy.ReproductionStrategyType;
import org.eclipse.viatra.dse.evolutionary.stopconditions.ConstantParetoFrontStopCondition;
import org.eclipse.viatra.dse.evolutionary.survival.FirstNSolutionsSurvivalStrategy;

import com.google.common.base.Preconditions;

public class EvolutionaryStrategyBuilder {

    private EvolutionaryStrategy strategy = new EvolutionaryStrategy();

    private EvolutionaryStrategyBuilder() {
    }

    public static EvolutionaryStrategyBuilder createBuilder() {
        return new EvolutionaryStrategyBuilder();
    }

    public static EvolutionaryStrategyBuilder createNsgaBuilder(int populationSize) {
        EvolutionaryStrategyBuilder builder = new EvolutionaryStrategyBuilder();
        builder.setInitialPopulationSize(populationSize * 2);
        builder.setChildPopulationSize(populationSize * 2);
        builder.setEvaluationStrategy(new FrontsAndCrowdingDistanceEvaluationStrategy());
        builder.setParentSelectionStrategy(new CrowdedTournementParentSelector());
        builder.setSurvivalStrategy(new FirstNSolutionsSurvivalStrategy(populationSize));
        builder.setReproductionStrategy(new SimpleReproductionStrategy(ReproductionStrategyType.SURVIVED_POPULATION));
        return builder;
    }

    public static EvolutionaryStrategyBuilder createNsgaBuilderFull(int populationSize) {
        EvolutionaryStrategyBuilder builder = createNsgaBuilder(populationSize);
        builder.setInitialPopulationSelector(new BfsInitialSelector(0.18f, 2));
        builder.setStopCondition(new ConstantParetoFrontStopCondition(20));
        builder.setMutationRate(new SimpleMutationRate(0.8));
        builder.addMutation(new AddRandomTransitionMutation(), 3);
        builder.addMutation(new ModifyRandomTransitionMutation());
        builder.addCrossover(new CutAndSpliceCrossover());
        builder.addCrossover(new SwapTransitionCrossover());
        return builder;
    }

    public static EvolutionaryStrategy createNsgaStrategy(int populationSize) {
        return createNsgaBuilderFull(populationSize).build();
    }

    public EvolutionaryStrategy build() {
        Preconditions.checkNotNull(strategy.initialPopulationSelector, "Initial population selector is not set!");
        Preconditions.checkNotNull(strategy.evaluationStrategy, "Evaluation strategy is not set!");
        Preconditions.checkNotNull(strategy.survivalStrategy, "Survival strategy is not set!");
        Preconditions.checkNotNull(strategy.reproductionStrategy, "Reproductions strategy is not set!");
        Preconditions.checkNotNull(strategy.parentSelectionStrategy, "Parent selection strategy is not set!");
        Preconditions.checkNotNull(strategy.stopCondition, "Stop condition is not set!");
        Preconditions.checkNotNull(strategy.mutationRate, "Mutation rate is not set!");
        Preconditions.checkArgument(!strategy.mutations.isEmpty() && !strategy.crossovers.isEmpty(),
                "No mutation nor crossover operations added!");
        Preconditions.checkArgument(strategy.initialPopulationSize > 0,
                "Initial population size is not set correctly!");
        Preconditions.checkArgument(strategy.childPopulationSize > 0, "Child population size is not set correctly!");

        return strategy;
    }

    public void setInitialPopulationSize(int populationSize) {
        strategy.initialPopulationSize = populationSize;
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
