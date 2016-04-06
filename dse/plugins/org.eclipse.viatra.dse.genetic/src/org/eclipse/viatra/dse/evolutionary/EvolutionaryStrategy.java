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
import org.eclipse.viatra.dse.base.GlobalContext;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.ITransition;
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
import org.eclipse.viatra.dse.solutionstore.SolutionStore;

public class EvolutionaryStrategy implements IStrategy {

    // configs
    protected int populationSize;
    protected int childPopulationSize;
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
    private GlobalContext gc;
    private DesignSpaceManager dsm;
    private SolutionStore solutionStore;
    private AtomicBoolean isInterrupted = new AtomicBoolean(false);
    private Random random = new Random();
    private Set<TrajectoryFitness> childPopulation;

    @Override
    public void initStrategy(ThreadContext context) {
        this.context = context;
        gc = context.getGlobalContext();
        dsm = context.getDesignSpaceManager();
        solutionStore = gc.getSolutionStore2();
        childPopulation = new HashSet<>(childPopulationSize);
        
        evaluationStrategy.init(context);
        for (IEvolutionaryStrategyAdapter adapter : adapters) {
            adapter.init(context);
        }
        
        // TODO no design space
        // context.setDesignSpace(new DummyDesignSpace())
        // context.setTrajectorySaver(new FullTrajectorySaver())
        // TODO no instance model
    }

    @Override
    public void explore() {

        // initial population selection
        initialPopulationSelector.setPopulationSize(populationSize);
        initialPopulationSelector.initStrategy(context);
        initialPopulationSelector.explore();
        List<TrajectoryFitness> currentPopulation = initialPopulationSelector.getInitialPopulation();

        // TODO start instance workers
        
        while (!isInterrupted.get()) {
            
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
            
            if (stop) {
                for (TrajectoryFitness trajectoryFitness : survivedPopulation) {
                    if (trajectoryFitness.rank == 1) {
                        while(dsm.undoLastTransformation());
                        for (ITransition transition : trajectoryFitness.trajectory) {
                            dsm.fireActivation(transition);
                        }
                        context.calculateFitness();
                        solutionStore.newSolution(context);
                    }
                }
                // TODO stop worker threads
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
            while (childPopulationSize > childPopulation.size() - survivedPopulation.size()) {

                if (childPopulation.size() == childPopulationSize - 1 || random.nextDouble() < mutationChance) {
                    int index = random.nextInt(mutations.size());
                    IMutation mutation = mutations.get(index);
                    TrajectoryFitness parent = parentSelectionStrategy.getNextParent();
                    TrajectoryFitness child = mutation.mutate(parent, context);
                    childPopulation.add(child);
                } else {
                    int index = random.nextInt(crossovers.size());
                    ICrossover crossover = crossovers.get(index);
                    TrajectoryFitness parent1 = parentSelectionStrategy.getNextParent();
                    TrajectoryFitness parent2 = parentSelectionStrategy.getNextParent();
                    TrajectoryFitness[] children = crossover.mutate(parent1, parent2, context);
                    childPopulation.add(children[0]);
                    childPopulation.add(children[1]);
                }
            }
            
            currentPopulation = new ArrayList<TrajectoryFitness>(childPopulation);
        }
        
    }

    @Override
    public void interruptStrategy() {
        initialPopulationSelector.interruptStrategy();
        isInterrupted.set(true);
    }

}
