/*******************************************************************************
 * Copyright (c) 2010-2015, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.genetic.api;

import java.util.Map;

import org.eclipse.viatra.dse.api.DSETransformationRule;
import org.eclipse.viatra.dse.api.SolutionTrajectory;
import org.eclipse.viatra.dse.genetic.core.GeneticSharedObject;
import org.eclipse.viatra.dse.genetic.core.InstanceData;
import org.eclipse.viatra.dse.genetic.core.MainGeneticStrategy;
import org.eclipse.viatra.dse.genetic.debug.GeneticDebugger;
import org.eclipse.viatra.dse.genetic.interfaces.ICrossoverTrajectories;
import org.eclipse.viatra.dse.genetic.interfaces.IInitialPopulationSelector;
import org.eclipse.viatra.dse.genetic.interfaces.IMutateTrajectory;
import org.eclipse.viatra.dse.genetic.interfaces.ISelectNextPopulation;
import org.eclipse.viatra.dse.genetic.selectors.NonDominatedAndCrowdingDistanceSelector;

public class GeneticStrategyBuilder {

    private final MainGeneticStrategy strategy;
    private final GeneticSharedObject sharedObject;

    public GeneticStrategyBuilder() {
        sharedObject = new GeneticSharedObject();
        strategy = new MainGeneticStrategy(sharedObject);
        sharedObject.mainStrategy = strategy;
        sharedObject.geneticStrategyBuilder = this;
        
        setSelector(new NonDominatedAndCrowdingDistanceSelector());
    }

    public void setRulePriority(DSETransformationRule<?, ?> rule, int priority) {
        sharedObject.priorities.put(rule, priority);
    }

    public void setInitialPopulationSelector(IInitialPopulationSelector selector) {
        sharedObject.initialPopulationSelector = selector;
    }

    public void setSizeOfPopulation(int sizeOfPopulation) {
        sharedObject.sizeOfPopulation = sizeOfPopulation;
    }

    public void setStopCondition(StopCondition stopCondition, int stopConditionNumber) {
        sharedObject.stopCondition = stopCondition;
        sharedObject.stopConditionNumber = stopConditionNumber;
    }

    public void setMutationChanceAtCrossover(float baseChance) {
        setMutationChanceAtCrossover(baseChance, 0.0f);
    }

    public void setMutationChanceAtCrossover(float baseChance, float multiplierForAdaptivity) {
        sharedObject.chanceOfMutationInsteadOfCrossover = baseChance;
        sharedObject.mutationChanceMultiplier = multiplierForAdaptivity;
    }

    public void addMutatitor(IMutateTrajectory mutatior) {
        addMutatitor(mutatior, 1);
    }

    public void addMutatitor(IMutateTrajectory mutatior, int weight) {
        sharedObject.mutationApplications.put(mutatior, 0);
        for (int i = 0; i < weight; ++i) {
            sharedObject.mutatiors.add(mutatior);
        }
    }

    public void addCrossover(ICrossoverTrajectories crossover) {
        addCrossover(crossover, 1);
    }

    public void addCrossover(ICrossoverTrajectories crossover, int weight) {
        sharedObject.crossoverApplications.put(crossover, 0);
        for (int i = 0; i < weight; ++i) {
            sharedObject.crossovers.add(crossover);
        }
    }

    public void setSelector(ISelectNextPopulation selector) {
        sharedObject.selector = selector;
    }

    public GeneticSharedObject getSharedObject() {
        return sharedObject;
    }

    public MainGeneticStrategy getStrategy() {
        return strategy;
    }
    
    public Map<InstanceData, SolutionTrajectory> getSolutions() {
        return sharedObject.bestSolutions;
    }

    public void setDebugger(GeneticDebugger geneticDebugger) {
        strategy.setGeneticDebugger(geneticDebugger);
    }
}
