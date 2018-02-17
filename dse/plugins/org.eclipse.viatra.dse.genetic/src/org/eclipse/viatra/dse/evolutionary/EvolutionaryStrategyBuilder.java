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

import java.util.Objects;

import org.eclipse.viatra.dse.evolutionary.EvolutionaryStrategy.EvolutionaryStrategySharedObject;
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
import org.eclipse.viatra.dse.evolutionary.parentselectors.RandomParentSelector;
import org.eclipse.viatra.dse.evolutionary.reproduction.SimpleReproductionStrategy;
import org.eclipse.viatra.dse.evolutionary.reproduction.SimpleReproductionStrategy.ReproductionStrategyType;
import org.eclipse.viatra.dse.evolutionary.stopconditions.ConstantParetoFrontStopCondition;
import org.eclipse.viatra.dse.evolutionary.survival.FirstNSolutionsSurvivalStrategy;
import org.eclipse.viatra.dse.evolutionary.survival.ParetoSurvivalStrategy;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;

public class EvolutionaryStrategyBuilder {

    private EvolutionaryStrategy strategy = new EvolutionaryStrategy();

    private EvolutionaryStrategyBuilder() {
    }

    public static EvolutionaryStrategyBuilder createBuilder() {
        return new EvolutionaryStrategyBuilder();
    }

    public static EvolutionaryStrategyBuilder createNsga2Builder(int populationSize) {
        EvolutionaryStrategyBuilder builder = new EvolutionaryStrategyBuilder();
        builder.setInitialPopulationSize(populationSize);
        builder.setChildPopulationSize(populationSize);
        builder.setEvaluationStrategy(new FrontsAndCrowdingDistanceEvaluationStrategy());
        builder.setParentSelectionStrategy(new CrowdedTournementParentSelector());
        builder.setSurvivalStrategy(new FirstNSolutionsSurvivalStrategy(populationSize));
        builder.setReproductionStrategy(new SimpleReproductionStrategy(ReproductionStrategyType.SURVIVED_POPULATION));
        return builder;
    }

    public static EvolutionaryStrategyBuilder createNsga2BuilderFull(int populationSize) {
        EvolutionaryStrategyBuilder builder = createNsga2Builder(populationSize);
        builder.setInitialPopulationSelector(new BfsInitialSelector(0.18f, 2));
        builder.setStopCondition(new ConstantParetoFrontStopCondition(20));
        builder.setMutationRate(new SimpleMutationRate(0.8));
        builder.addMutation(new AddRandomTransitionMutation(), 3);
        builder.addMutation(new ModifyRandomTransitionMutation());
        builder.addCrossover(new CutAndSpliceCrossover());
        builder.addCrossover(new SwapTransitionCrossover());
        return builder;
    }

    public static EvolutionaryStrategy createNsga2Strategy(int populationSize) {
        return createNsga2BuilderFull(populationSize).build();
    }

    public static EvolutionaryStrategyBuilder createPesaBuilder(int populationSize) {
        EvolutionaryStrategyBuilder builder = new EvolutionaryStrategyBuilder();
        builder.setInitialPopulationSize(populationSize);
        builder.setChildPopulationSize(populationSize);
        builder.setEvaluationStrategy(new FrontsAndCrowdingDistanceEvaluationStrategy());
        builder.setParentSelectionStrategy(new RandomParentSelector());
        builder.setSurvivalStrategy(new ParetoSurvivalStrategy(populationSize));
        builder.setReproductionStrategy(new SimpleReproductionStrategy(ReproductionStrategyType.SURVIVED_POPULATION));
        return builder;
    }

    public static EvolutionaryStrategyBuilder createPesaBuilderFull(int populationSize) {
        EvolutionaryStrategyBuilder builder = createNsga2Builder(populationSize);
        builder.setInitialPopulationSelector(new BfsInitialSelector(0.18f, 2));
        builder.setStopCondition(new ConstantParetoFrontStopCondition(20));
        builder.setMutationRate(new SimpleMutationRate(1.001));
        builder.addMutation(new AddRandomTransitionMutation(), 3);
        builder.addMutation(new ModifyRandomTransitionMutation());
        return builder;
    }

    public static EvolutionaryStrategy createPesaStrategy(int populationSize) {
        return createPesaBuilderFull(populationSize).build();
    }

    public EvolutionaryStrategy build() {
        Objects.requireNonNull(strategy.so.initialPopulationSelector, "Initial population selector is not set!");
        Objects.requireNonNull(strategy.so.evaluationStrategy, "Evaluation strategy is not set!");
        Objects.requireNonNull(strategy.so.survivalStrategy, "Survival strategy is not set!");
        Objects.requireNonNull(strategy.so.reproductionStrategy, "Reproductions strategy is not set!");
        Objects.requireNonNull(strategy.so.parentSelectionStrategy, "Parent selection strategy is not set!");
        Objects.requireNonNull(strategy.so.stopCondition, "Stop condition is not set!");
        Objects.requireNonNull(strategy.so.mutationRate, "Mutation rate is not set!");
        Preconditions.checkArgument(!(strategy.so.mutations.isEmpty() && strategy.so.crossovers.isEmpty()),
                "No mutation nor crossover operations added!");
        Preconditions.checkArgument(strategy.so.initialPopulationSize > 0,
                "Initial population size is not set correctly!");
        Preconditions.checkArgument(strategy.so.childPopulationSize > 0, "Child population size is not set correctly!");

        return strategy;
    }

    public void setInitialPopulationSize(int populationSize) {
        strategy.so.initialPopulationSize = populationSize;
    }

    public void setChildPopulationSize(int childPopulationSize) {
        strategy.so.childPopulationSize = childPopulationSize;
    }

    public void setInitialPopulationSelector(IInitialPopulationSelector initialPopulationSelector) {
        strategy.so.initialPopulationSelector = initialPopulationSelector;
    }

    public void setEvaluationStrategy(IEvaluationStrategy evaluationStrategy) {
        strategy.so.evaluationStrategy = evaluationStrategy;
    }

    public void setSurvivalStrategy(ISurvivalStrategy survivalStrategy) {
        strategy.so.survivalStrategy = survivalStrategy;
    }

    public void setReproductionStrategy(IReproductionStrategy reproductionStrategy) {
        strategy.so.reproductionStrategy = reproductionStrategy;
    }

    public void setParentSelectionStrategy(IParentSelectionStrategy parentSelectionStrategy) {
        strategy.so.parentSelectionStrategy = parentSelectionStrategy;
    }

    public void setStopCondition(IStopCondition stopCondition) {
        strategy.so.stopCondition = stopCondition;
    }

    public void setMutationRate(IMutationRate mutationRate) {
        strategy.so.mutationRate = mutationRate;
    }

    public void addCrossover(ICrossover crossover) {
        strategy.so.crossovers.add(crossover);
    }

    public void addCrossover(ICrossover crossover, int weight) {
        for (; weight > 0; weight--) {
            strategy.so.crossovers.add(crossover);
        }
    }

    public void addMutation(IMutation mutation) {
        strategy.so.mutations.add(mutation);
    }

    public void addMutation(IMutation mutation, int weight) {
        for (; weight > 0; weight--) {
            strategy.so.mutations.add(mutation);
        }
    }

    public void addStrategyAdapter(IEvolutionaryStrategyAdapter adapter) {
        strategy.adapters.add(adapter);
    }
    
    public EvolutionaryStrategySharedObject getConfigurationObject() {
        return strategy.so;
    }
}
