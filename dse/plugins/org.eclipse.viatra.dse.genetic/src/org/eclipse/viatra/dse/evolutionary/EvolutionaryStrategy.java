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
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.api.DesignSpaceExplorer;
import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategy;
import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategyFactory;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.TrajectoryInfo;
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
import org.eclipse.viatra.dse.objectives.Fitness;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

import com.google.common.util.concurrent.AtomicDouble;

public class EvolutionaryStrategy implements IStrategy {

    public static class EvolutionaryStrategySharedObject {
        // configs
        public int initialPopulationSize = -1;
        public int childPopulationSize = -1;
        public IInitialPopulationSelector initialPopulationSelector;
        public IEvaluationStrategy evaluationStrategy;
        public ISurvivalStrategy survivalStrategy;
        public IReproductionStrategy reproductionStrategy;
        public IParentSelectionStrategy parentSelectionStrategy;
        public IStopCondition stopCondition;
        public IMutationRate mutationRate;
        public List<ICrossover> crossovers = new ArrayList<>();
        public List<IMutation> mutations = new ArrayList<>();

        // multi thread
        public CyclicBarrier barrierBeforeChildGeneration;
        public CyclicBarrier barrierAfterChildGeneration;
        public AtomicDouble mutationChance = new AtomicDouble(1);
        public Set<TrajectoryFitness> childPopulation;
        public AtomicReference<List<TrajectoryFitness>> parentPopulation = new AtomicReference<List<TrajectoryFitness>>();
    }

    protected EvolutionaryStrategySharedObject so;
    protected List<ICrossover> crossovers = new ArrayList<>();
    protected List<IMutation> mutations = new ArrayList<>();

    protected List<IEvolutionaryStrategyAdapter> adapters = new ArrayList<>();

    // local variables
    protected ThreadContext context;
    protected DesignSpaceManager dsm;
    protected AtomicBoolean isInterrupted = new AtomicBoolean(false);
    protected Random random = new Random();

    protected IParentSelectionStrategy localParentSelector;
    protected boolean isFirstThread = false;

    protected Logger logger = Logger.getLogger(IStrategy.class);

    public EvolutionaryStrategy() {
        so = new EvolutionaryStrategySharedObject();
    }

    @Override
    public void initStrategy(ThreadContext context) {
        this.context = context;
        dsm = context.getDesignSpaceManager();

        if (context.getSharedObject() == null) {

            isFirstThread = true;

            mutations = so.mutations;
            crossovers = so.crossovers;

            so.childPopulation = new HashSet<>(so.childPopulationSize);

            if (logger.isDebugEnabled()) {
                adapters.add(new EvolutionaryStrategyDebugAdapter());
            }

            for (IEvolutionaryStrategyAdapter adapter : adapters) {
                adapter.init(context);
            }

            context.setSharedObject(so);
            so.evaluationStrategy.init(context);
            localParentSelector = so.parentSelectionStrategy;

            so.stopCondition.init(context);

            logger.info("Evolutionary exploration strategy is inited.");
        } else {
            so = (EvolutionaryStrategySharedObject) context.getSharedObject();
            localParentSelector = so.parentSelectionStrategy.createNew();
            for (IMutation mutation : so.mutations) {
                mutations.add(mutation.createNew());
            }
            for (ICrossover crossover : so.crossovers) {
                crossovers.add(crossover.createNew());
            }
        }

    }

    @Override
    public void explore() {
        try {
            if (isFirstThread) {
                mainThread();
            } else {
                while (true) {
                    so.barrierBeforeChildGeneration.await();
                    if (isInterrupted.get()) {
                        return;
                    }
                    generateChildren();
                    so.barrierAfterChildGeneration.await();
                }
            }
        } catch (InterruptedException | BrokenBarrierException e) {
            Logger.getLogger(DesignSpaceExplorer.class).error(e.toString());
        }
    }

    protected void mainThread() throws InterruptedException, BrokenBarrierException {

        // initial population selection
        so.initialPopulationSelector.setPopulationSize(so.initialPopulationSize);
        so.initialPopulationSelector.initStrategy(context);
        so.initialPopulationSelector.explore();
        List<TrajectoryFitness> currentPopulation = so.initialPopulationSelector.getInitialPopulation();

        logger.info("Initial population has been generated.");

        dsm.setDesignSpace(null);

        if (isInterrupted.get()) {
            savePopulationsAsSolutions(currentPopulation);
            return;
        }

        int threads = context.getGlobalContext().getThreadPool().getMaximumPoolSize();
        so.barrierBeforeChildGeneration = new CyclicBarrier(threads);
        so.barrierAfterChildGeneration = new CyclicBarrier(threads);
        startThreads();

        while (true) {

            List<? extends List<TrajectoryFitness>> frontsOfCurrentPopulation = so.evaluationStrategy.evaluatePopulation(currentPopulation);

            List<TrajectoryFitness> survivedPopulation = so.survivalStrategy.selectSurvivedPopulation(frontsOfCurrentPopulation);

            for (TrajectoryFitness trajectoryFitness : survivedPopulation) {
                trajectoryFitness.survive++;
            }

            boolean stop = so.stopCondition.checkStopCondition(survivedPopulation);

            if (!adapters.isEmpty()) {
                for (IEvolutionaryStrategyAdapter adapter : adapters) {
                    adapter.iterationCompleted(currentPopulation, frontsOfCurrentPopulation, survivedPopulation, stop);
                }
            }

            if (stop || isInterrupted.get()) {
                savePopulationsAsSolutions(survivedPopulation);
                context.getGlobalContext().stopAllThreads();
                so.barrierBeforeChildGeneration.await();
                return;
            }

            // get potential parents
            so.parentPopulation.set(so.reproductionStrategy.getParentPopulation(currentPopulation, frontsOfCurrentPopulation, survivedPopulation));

            so.mutationChance.set(so.mutationRate.getMutationChance(currentPopulation, survivedPopulation, so.parentPopulation.get()));

            so.childPopulation.clear();
            for (TrajectoryFitness trajectoryFitness : survivedPopulation) {
                so.childPopulation.add(trajectoryFitness);
            }

            // child generation
            so.barrierBeforeChildGeneration.await();
            generateChildren();
            so.barrierAfterChildGeneration.await();

            currentPopulation = new ArrayList<TrajectoryFitness>(so.childPopulation);
        }

    }

    protected void startThreads() {
        context.startAllThreads(new IStrategyFactory() {
            @Override
            public IStrategy createStrategy() {
                return new EvolutionaryStrategy();
            }
        });
    }

    protected void generateChildren() {

        localParentSelector.init(so.parentPopulation.get());

        while (so.childPopulationSize * 2 > so.childPopulation.size()) {

            // TODO no sync between child generation => may generate children unnecessarily => performance issues

            if (random.nextDouble() < so.mutationChance.get()) {
                int index = random.nextInt(mutations.size());
                IMutation mutation = mutations.get(index);
                TrajectoryFitness parent = localParentSelector.getNextParent();
                boolean succesful = mutation.mutate(parent, context);
                if (succesful) {
                    Fitness calculateFitness = context.calculateFitness();
                    TrajectoryInfo trajectoryInfo = context.getTrajectoryInfo();
                    TrajectoryFitness child = new TrajectoryWithStateFitness(trajectoryInfo, calculateFitness);
                    context.backtrackUntilRoot();
                    // TODO fitness calc after duplication check
                    boolean shouldBreak = addToChildren(child);
                    if (shouldBreak) {
                        break;
                    }
                }
            } else {
                int index = random.nextInt(crossovers.size());
                ICrossover crossover = crossovers.get(index);
                TrajectoryFitness parent1 = localParentSelector.getNextParent();
                TrajectoryFitness parent2 = localParentSelector.getNextParent();
                boolean successful = crossover.mutate(parent1, parent2, context);
                if (successful) {
                    Fitness calculateFitness = context.calculateFitness();
                    TrajectoryInfo trajectoryInfo = context.getTrajectoryInfo();
                    TrajectoryFitness child = new TrajectoryWithStateFitness(trajectoryInfo, calculateFitness);
                    context.backtrackUntilRoot();
                    // TODO fitness calc after duplication check
                    boolean shouldBreak = addToChildren(child);
                    if (shouldBreak) {
                        break;
                    }
                    crossover.mutateAlternate(parent2, parent1, context);
                    calculateFitness = context.calculateFitness();
                    trajectoryInfo = context.getTrajectoryInfo();
                    child = new TrajectoryWithStateFitness(trajectoryInfo, calculateFitness);
                    context.backtrackUntilRoot();
                    // TODO fitness calc after duplication check
                    shouldBreak = addToChildren(child);
                    if (shouldBreak) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * 
     * @param child
     * @return True, if children population is full.
     */
    protected boolean addToChildren(TrajectoryFitness child) {
        synchronized (so.barrierBeforeChildGeneration) {
            if (so.childPopulationSize * 2 > so.childPopulation.size()) {
                so.childPopulation.add(child);
                return false;
            } else {
                return true;
            }
        }
    }

    protected void savePopulationsAsSolutions(List<TrajectoryFitness> survivedPopulation) {
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
        so.initialPopulationSelector.interruptStrategy();
        isInterrupted.set(true);
    }

}
