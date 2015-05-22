/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.genetic.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.strategy.interfaces.LocalSearchStrategyBase;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.GlobalContext;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.IState;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.genetic.api.StopCondition;
import org.eclipse.viatra.dse.genetic.debug.GeneticDebugger;
import org.eclipse.viatra.dse.genetic.interfaces.ICrossoverTrajectories;
import org.eclipse.viatra.dse.genetic.interfaces.IMutateTrajectory;
import org.eclipse.viatra.dse.genetic.interfaces.IStoreChild;
import org.eclipse.viatra.dse.multithreading.DSEThreadPool;
import org.eclipse.viatra.dse.objectives.Fitness;
import org.eclipse.viatra.dse.objectives.IObjective;
import org.eclipse.viatra.dse.util.EMFHelper;

import com.google.common.base.Preconditions;

public class MainGeneticStrategy extends LocalSearchStrategyBase implements IStoreChild {

    enum GeneticStrategyState {
        FIRST_POPULATION,
        CREATING_NEW_POPULATION,
        STOPPING
    }

    private GeneticSharedObject sharedObject;
    private List<InstanceData> parentPopulation = new ArrayList<InstanceData>();

    private GeneticStrategyState state = GeneticStrategyState.FIRST_POPULATION;
    private GlobalContext gc;
    private DesignSpaceManager dsm;

    private Random random = new Random();

    private Logger logger = Logger.getLogger(MainGeneticStrategy.class);
    private GeneticDebugger geneticDebugger;

    private double actualBestSoftConstraint = Double.MAX_VALUE;
    private int noBetterSolutionForXIterations = 0;
    private ThreadContext context;
    private List<IObjective> objectives;
    private GeneticConstraintObjective genObjective;

    public MainGeneticStrategy(GeneticSharedObject sharedObject) {
        Preconditions.checkNotNull(sharedObject);
        this.sharedObject = sharedObject;
    }

    @Override
    public void init(ThreadContext context) {

        this.context = context;
        gc = context.getGlobalContext();
        dsm = context.getDesignSpaceManager();
        
        gc.setSharedObject(sharedObject);

        if (!gc.getSolutionStore().isStrategyDependent()) {
            throw new DSEException("The genetic strategy needs a strategy dependent solution store.");
        }
        
        IObjective[][] leveledObjectives = context.getLeveledObjectives();
        IObjective objective = leveledObjectives[0][0];
        if (!(objective instanceof GeneticConstraintObjective) || leveledObjectives[0].length != 1) {
            throw new DSEException("The only objective on the first level should be the GeneticSoftConstraintHardObjective.");
        }
        
        DSEThreadPool pool = gc.getThreadPool();
        if (pool.getMaximumPoolSize() < 2) {
            throw new DSEException("The genetic strategy needs at least two threads.");
        }

        sharedObject.initialModel = EMFHelper.clone(context.getModelRoot());

        sharedObject.instancesToBeChecked = new ArrayBlockingQueue<InstanceData>(sharedObject.sizeOfPopulation, false);

        sharedObject.initialPopulationSelector.setChildStore(this);
        sharedObject.initialPopulationSelector.setPopulationSize(sharedObject.sizeOfPopulation);
        sharedObject.initialPopulationSelector.init(context);

        objectives = context.getGlobalContext().getObjectives();
        
        genObjective = (GeneticConstraintObjective) context.getLeveledObjectives()[0][0];
        
        logger.debug("MainGeneticStratgey is inited");
    }

    @Override
    public ITransition getNextTransition(boolean lastWasSuccesful) {

        do {
            if (state == GeneticStrategyState.FIRST_POPULATION) {

                ITransition transition = sharedObject.initialPopulationSelector.getNextTransition(lastWasSuccesful);
                if (transition != null) {
                    return transition;
                } else {
                    logger.debug("Initial population is selected, starting workers.");
                    startWorkerThreads(context);
                    state = GeneticStrategyState.CREATING_NEW_POPULATION;
                }

            }
            if (state == GeneticStrategyState.CREATING_NEW_POPULATION) {

                logger.debug(sharedObject.actNumberOfPopulation + ". population, selecting");

                boolean isLastPopulation = false;
                switch (sharedObject.stopCondition) {
                case CANT_FIND_BETTER:
                    // Intended, check after selection
                    break;
                case GOOD_ENOUGH_SOLUTION:
                    for (InstanceData instanceData : parentPopulation) {
                        if (instanceData.sumOfConstraintViolationMeauserement < sharedObject.stopConditionNumber) {
                            isLastPopulation = true;
                            break;
                        }
                    }
                    break;
                case ITERATIONS:
                    isLastPopulation = sharedObject.actNumberOfPopulation >= sharedObject.stopConditionNumber;
                    break;
                default:
                    break;
                }

                parentPopulation = sharedObject.selector.selectNextPopulation(parentPopulation,
                        objectives, sharedObject.sizeOfPopulation,
                        isLastPopulation && !geneticDebugger.isDebug(),
                        context.getObjectiveComparatorHelper());

                geneticDebugger.debug(parentPopulation);

                for (InstanceData instanceData : parentPopulation) {
                    instanceData.survive++;
                }

                if (sharedObject.stopCondition.equals(StopCondition.CANT_FIND_BETTER)) {
                    for (InstanceData instanceData : parentPopulation) {
                        if (instanceData.rank == 1) {
                            if (!(instanceData.sumOfConstraintViolationMeauserement < actualBestSoftConstraint)) {
                                noBetterSolutionForXIterations++;
                                if (noBetterSolutionForXIterations >= sharedObject.stopConditionNumber) {
                                    isLastPopulation = true;
                                }
                            } else {
                                noBetterSolutionForXIterations = 0;
                                actualBestSoftConstraint = instanceData.sumOfConstraintViolationMeauserement;
                            }
                            break;
                        }
                    }
                }

                if (isLastPopulation) {
                    sharedObject.addInstanceToBestSolutions.set(true);
                    for (InstanceData instanceData : parentPopulation) {
                        if (instanceData.rank == 1) {
                            sharedObject.instancesToBeChecked.offer(instanceData);
                        }
                    }
                    state = GeneticStrategyState.STOPPING;
                    continue;
                }

                // Create children by crossover, everybody selected as a parent at least once
                ArrayList<InstanceData> tempChildren = new ArrayList<InstanceData>();
                ArrayList<InstanceData> alreadyTriedChildren = new ArrayList<InstanceData>();
                Iterator<InstanceData> mainIterator = parentPopulation.iterator();

                int paretoFrontSize = 0;
                for (InstanceData instanceData : parentPopulation) {
                    if (instanceData.rank == 1) {
                        paretoFrontSize++;
                    }
                }

                float mutationChance = sharedObject.mutationChanceMultiplier * paretoFrontSize
                        / parentPopulation.size() + sharedObject.chanceOfMutationInsteadOfCrossover;
                while (mainIterator.hasNext()) {
                    InstanceData parent1 = mainIterator.next();

                    // Mutation (crossover with one parent)
                    if (random.nextFloat() < mutationChance) {
                        List<IMutateTrajectory> mutatiors = sharedObject.mutatiors;
                        int rnd = random.nextInt(mutatiors.size());
                        IMutateTrajectory mutator = mutatiors.get(rnd);
                        sharedObject.mutationUsed(mutator);
                        InstanceData child = mutator.mutate(parent1, context);
                        if (child.trajectory.isEmpty()) {
                            throw new DSEException("Mutation operator (at crossover) "
                                    + mutatiors.get(rnd).getClass().getSimpleName() + " returned an empty trajectory.");
                        }
                        logger.debug("Mutation: parent: " + parent1 + " child: " + child);
                        tempChildren.add(child);
                    }
                    // Crossover (with two parents)
                    else {
                        // Choose the second parent
                        int p1Index = random.nextInt(parentPopulation.size());
                        InstanceData parent2 = parentPopulation.get(p1Index);
                        if (parent1.equals(parent2)) {
                            if (p1Index + 1 < parentPopulation.size()) {
                                parent2 = parentPopulation.get(p1Index + 1);
                            } else {
                                parent2 = parentPopulation.get(0);
                            }
                        }

                        // Crossover
                        List<ICrossoverTrajectories> crossovers = sharedObject.crossovers;
                        int rnd = random.nextInt(crossovers.size());
                        ICrossoverTrajectories crossover = crossovers.get(rnd);
                        sharedObject.crossoverUsed(crossover);
                        Collection<InstanceData> result = crossover.crossover(Arrays.asList(parent1, parent1), context);
                        logger.debug("Crossover parent1: " + parent1 + " parent2: " + parent2);
                        for (InstanceData child : result) {
                            if (child.trajectory.isEmpty()) {
                                throw new DSEException("Crossover operation " + crossover.getClass().getSimpleName()
                                        + " returned an empty trajectory.");
                            }
                            logger.debug("  Child: " + child);
                            tempChildren.add(child);
                        }
                    }

                    // Check the created children and make it feasible by the worker threads
                    for (Iterator<InstanceData> iterator = tempChildren.iterator(); iterator.hasNext();) {
                        InstanceData child = iterator.next();
                        boolean isDuplicationInParent = isDuplication(child, parentPopulation);
                        boolean isDuplicationInAlreadyTrieds = isDuplication(child, alreadyTriedChildren);

                        if (child.trajectory.size() > 1 && !isDuplicationInParent && !isDuplicationInAlreadyTrieds) {
                            boolean queueIsNotFull = sharedObject.instancesToBeChecked.offer(child);
                            if (queueIsNotFull) {
                                alreadyTriedChildren.add(child);
                                logger.debug("Child to try: " + child.toString());
                            }
                        }
                        if (isDuplicationInParent || isDuplicationInAlreadyTrieds) {
                            sharedObject.numOfDuplications++;
                        }

                        iterator.remove();
                    }

                    // wait for workers and
                    if (alreadyTriedChildren.size() >= sharedObject.sizeOfPopulation) {
                        while (sharedObject.childPopulation.size() + sharedObject.unfeasibleInstances.get() < alreadyTriedChildren
                                .size()) {
                            try {
                                Thread.sleep(1);
                                if (gc.isExceptionHappendInOtherThread()) {
                                    return null;
                                }
                            } catch (InterruptedException e) {
                            }
                        }

                        // Check for duplications in an other way
                        Iterator<InstanceData> duplicationIterator = sharedObject.childPopulation.iterator();
                        boolean isDuplication = true;
                        duplicationLoop: while (duplicationIterator.hasNext()) {
                            InstanceData id1 = duplicationIterator.next();
                            Iterator<InstanceData> it = sharedObject.childPopulation.iterator();
                            boolean checkedParents = false;
                            while (it.hasNext()) {
                                InstanceData id2 = it.next();
                                if (!id1.equals(id2)) {
                                    IState id1resultState = id1.trajectory.get(id1.trajectory.size() - 1)
                                            .getResultsIn();
                                    IState id2ResultState = id2.trajectory.get(id2.trajectory.size() - 1)
                                            .getResultsIn();
                                    if (id1resultState.equals(id2ResultState)
                                            && id1.sumOfConstraintViolationMeauserement == id2.sumOfConstraintViolationMeauserement) {
                                        for (IObjective objective : objectives) {
                                            Double d1 = id1.getFitnessValue(objective.getName());
                                            Double d2 = id2.getFitnessValue(objective.getName());
                                            if (Math.abs(d1 - d2) >= 0.000001) {
                                                isDuplication = false;
                                            }
                                        }
                                        if (isDuplication) {
                                            duplicationIterator.remove();
                                            sharedObject.unfeasibleInstances.incrementAndGet();
                                            continue duplicationLoop;
                                        } else {
                                            isDuplication = true;
                                        }
                                    }
                                }
                                if (!it.hasNext() && !checkedParents) {
                                    it = parentPopulation.iterator();
                                    checkedParents = true;
                                }
                            }
                        }

                        if (sharedObject.childPopulation.size() >= sharedObject.sizeOfPopulation) {
                            // break from creating children
                            break;
                        }
                    }

                    if (!mainIterator.hasNext()) {
                        mainIterator = parentPopulation.iterator();
                    }
                }

                parentPopulation.addAll(sharedObject.childPopulation);
                sharedObject.childPopulation.clear();
                sharedObject.unfeasibleInstances.set(0);

                ++sharedObject.actNumberOfPopulation;

            }
            if (state == GeneticStrategyState.STOPPING) {
                logger.debug("Stopping");
                sharedObject.newPopulationIsNeeded.set(false);
            }

            // Interrupted
            if (!gc.getState().equals(GlobalContext.ExplorationProcessState.RUNNING)) {
                logger.debug("Interrupted");
                parentPopulation = sharedObject.selector.selectNextPopulation(parentPopulation,
                        objectives, sharedObject.sizeOfPopulation, !geneticDebugger.isDebug(), context.getObjectiveComparatorHelper());
                geneticDebugger.debug(parentPopulation);
                sharedObject.addInstanceToBestSolutions.set(true);
                for (InstanceData instanceData : parentPopulation) {
                    if (instanceData.rank == 1) {
                        logger.debug("solution to process " + instanceData.toString());
                        sharedObject.instancesToBeChecked.offer(instanceData);
                    }
                }
                sharedObject.newPopulationIsNeeded.set(false);
            }

        } while (sharedObject.newPopulationIsNeeded.get());

        logger.debug("Stopped");

        return null;
    }

    private boolean isDuplication(InstanceData instanceToCheck, List<InstanceData> existentInstances) {
        for (int i = existentInstances.size() - 1; i >= 0; --i) {
            InstanceData existent = existentInstances.get(i);
            boolean sameTrajectory = GeneticHelper.isSameTrajectory(existent.trajectory, instanceToCheck.trajectory);
            if (sameTrajectory) {
                return true;
            }
        }
        return false;
    }

    private void startWorkerThreads(ThreadContext context) {
        while (gc.tryStartNewThread(context, sharedObject.initialModel, true, 
                new InstanceGeneticStrategy()) != null) {
        }
    }

    @Override
    public void newStateIsProcessed(boolean isAlreadyTraversed, Fitness fitness, boolean constraintsNotSatisfied) {
        if (state == GeneticStrategyState.FIRST_POPULATION) {
            sharedObject.initialPopulationSelector.newStateIsProcessed(isAlreadyTraversed, fitness, constraintsNotSatisfied);
        }
    }

    @Override
    public void interrupted() {
    }

    @Override
    public void addChild(ThreadContext context) {
        ArrayList<ITransition> trajectory = new ArrayList<ITransition>(dsm.getTrajectoryInfo()
                .getTransitionTrajectory());
        InstanceData instance = new InstanceData(trajectory);
        instance.objectives = context.calculateFitness();
        for (int i = 0; i<genObjective.getNames().size(); i++) {
            instance.violations.put(genObjective.getNames().get(i), genObjective.getMatches().get(i));
        }
        parentPopulation.add(instance);
    }

    public void setGeneticDebugger(GeneticDebugger geneticDebugger) {
        this.geneticDebugger = geneticDebugger;
    }

    public GeneticDebugger getGeneticDebugger() {
        return geneticDebugger;
    }

}
