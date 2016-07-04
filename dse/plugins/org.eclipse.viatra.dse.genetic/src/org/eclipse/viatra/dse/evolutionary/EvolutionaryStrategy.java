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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategy;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
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
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public class EvolutionaryStrategy implements IStrategy {

    // configs
    protected int initialPopulationSize = -1;
    protected int childPopulationSize = -1;
    protected IInitialPopulationSelector initialPopulationSelector;
    protected IEvaluationStrategy evaluationStrategy;
    protected ISurvivalStrategy survivalStrategy;
    protected IReproductionStrategy reproductionStrategy;
    protected IParentSelectionStrategy parentSelectionStrategy;
    protected IStopCondition stopCondition;
    protected IMutationRate mutationRate;
    protected List<ICrossover> crossovers = new ArrayList<>();
    protected List<IMutation> mutations = new ArrayList<>();

    protected List<IEvolutionaryStrategyAdapter> adapters = new ArrayList<>();
    
    // local variables
    private ThreadContext context;
    private DesignSpaceManager dsm;
    private AtomicBoolean isInterrupted = new AtomicBoolean(false);
    private Random random = new Random();
    private Set<TrajectoryFitness> childPopulation;

    @Override
    public void initStrategy(ThreadContext context) {
        this.context = context;
        dsm = context.getDesignSpaceManager();
        childPopulation = new HashSet<>(childPopulationSize);
        
        evaluationStrategy.init(context);
        for (IEvolutionaryStrategyAdapter adapter : adapters) {
            adapter.init(context);
        }
    }

    @Override
    public void explore() {

        // initial population selection
        initialPopulationSelector.setPopulationSize(initialPopulationSize);
        initialPopulationSelector.initStrategy(context);
        initialPopulationSelector.explore();
        List<TrajectoryFitness> currentPopulation = initialPopulationSelector.getInitialPopulation();

        dsm.setDesignSpace(null);
        
        if (isInterrupted.get()) {
            savePopulationsAsSolutions(currentPopulation);
            return;
        }
        
        while (true) {
            
            List<? extends List<TrajectoryFitness>> frontsOfCurrentPopulation = evaluationStrategy.evaluatePopulation(currentPopulation);
            
            List<TrajectoryFitness> survivedPopulation = survivalStrategy.selectSurvivedPopulation(frontsOfCurrentPopulation);
            
            for (TrajectoryFitness trajectoryFitness : survivedPopulation) {
                trajectoryFitness.survive++;
            }
            
            boolean stop = stopCondition.checkStopCondition(survivedPopulation);

            if (!adapters.isEmpty()) {
                for (IEvolutionaryStrategyAdapter adapter : adapters) {
                    adapter.iterationCompleted(currentPopulation, frontsOfCurrentPopulation, survivedPopulation, stop);
                }
            }
            
            if (stop || isInterrupted.get()) {
                savePopulationsAsSolutions(survivedPopulation);
                return;
            }

            // get potential parents
            List<TrajectoryFitness> parentPopulation = reproductionStrategy.getParentPopulation(currentPopulation, frontsOfCurrentPopulation, survivedPopulation);
            
            // child generation and duplication check
            parentSelectionStrategy.init(parentPopulation);
            
            double mutationChance = mutationRate.getMutationChance(currentPopulation, survivedPopulation, parentPopulation);
            
            childPopulation.clear();
            for (TrajectoryFitness trajectoryFitness : survivedPopulation) {
                childPopulation.add(trajectoryFitness);
            }
            // implicit duplication check - same if very same trajectory 
            while (childPopulationSize > childPopulation.size()) {

                if (childPopulation.size() == childPopulationSize - 1 || random.nextDouble() < mutationChance) {
                    int index = random.nextInt(mutations.size());
                    IMutation mutation = mutations.get(index);
                    TrajectoryFitness parent = parentSelectionStrategy.getNextParent();
                    TrajectoryFitness child = mutation.mutate(parent, context);
                    if (child != null) {
                        childPopulation.add(child);
                    }
                } else {
                    int index = random.nextInt(crossovers.size());
                    ICrossover crossover = crossovers.get(index);
                    TrajectoryFitness parent1 = parentSelectionStrategy.getNextParent();
                    TrajectoryFitness parent2 = parentSelectionStrategy.getNextParent();
                    TrajectoryFitness[] children = crossover.mutate(parent1, parent2, context);
                    if (children != null) {
                        childPopulation.add(children[0]);
                        childPopulation.add(children[1]);
                    }
                }
            }
            
            currentPopulation = new ArrayList<TrajectoryFitness>(childPopulation);
        }
        
    }

    private void savePopulationsAsSolutions(List<TrajectoryFitness> survivedPopulation) {
        for (TrajectoryFitness trajectoryFitness : survivedPopulation) {
            if (trajectoryFitness.rank == 1) {
                context.backtrackUntilRoot();
                for (Object transition : trajectoryFitness.trajectory) {
                    context.executeAcitvationId(transition);
                }
                context.calculateFitness();
                context.newSolution();
            }
        }
    }

    @Override
    public void interruptStrategy() {
        initialPopulationSelector.interruptStrategy();
        isInterrupted.set(true);
    }

}
