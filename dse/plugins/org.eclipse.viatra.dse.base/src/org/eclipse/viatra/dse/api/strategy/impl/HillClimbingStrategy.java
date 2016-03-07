/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.api.strategy.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategy;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.objectives.Fitness;
import org.eclipse.viatra.dse.objectives.ObjectiveComparatorHelper;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;
import org.eclipse.viatra.dse.solutionstore.SolutionStore;

public class HillClimbingStrategy implements IStrategy {

    private AtomicBoolean isInterrupted = new AtomicBoolean(false);
    private DesignSpaceManager dsm;
    private ThreadContext context;
    private SolutionStore solutionStore;

    private Logger logger = Logger.getLogger(getClass());

    private Random random = new Random();
    private double percentOfOpenedStates;
    private ObjectiveComparatorHelper objectiveComparatorHelper;

    public HillClimbingStrategy() {
        this(2);
    }

    public HillClimbingStrategy(double percentOfOpenedStates) {
        this.percentOfOpenedStates = percentOfOpenedStates;
    }

    @Override
    public void initStrategy(ThreadContext context) {
        this.context = context;
        dsm = context.getDesignSpaceManager();
        solutionStore = context.getGlobalContext().getSolutionStore2();
        objectiveComparatorHelper = context.getObjectiveComparatorHelper();
        logger.info("Initied");
    }

    @Override
    public void explore() {

        boolean globalConstraintsAreSatisfied = context.checkGlobalConstraints();
        if (!globalConstraintsAreSatisfied) {
            boolean isSuccessfulUndo = dsm.undoLastTransformation();
            if (!isSuccessfulUndo) {
                logger.info("Global contraint is not satisifed and cannot backtrack.");
                return;
            }
        }

        Fitness previousFitness = context.calculateFitness();

        do {

            Collection<? extends ITransition> transitionsFromCurrentState = dsm.getTransitionsFromCurrentState();
            ArrayList<ITransition> transitionsToTry = new ArrayList<>(transitionsFromCurrentState.size());
            for (ITransition transition : transitionsFromCurrentState) {
                transitionsToTry.add(transition);
            }
            double numberOfTransitionsToTry = transitionsToTry.size() * percentOfOpenedStates;

            for (; numberOfTransitionsToTry > 0 && transitionsToTry.size() > 0; numberOfTransitionsToTry--) {
                int index = random.nextInt(transitionsToTry.size());
                ITransition transition = transitionsToTry.remove(index);

                dsm.fireActivation(transition);

                if (!context.checkGlobalConstraints()) {
                    dsm.undoLastTransformation();
                    logger.debug("Global contraint is not satisifed, backtrack.");
                    continue;
                }
                if (dsm.isCurentStateInTrajectory()) {
                    dsm.undoLastTransformation();
                    logger.debug("Current state is in trajectory, backtrack.");
                    continue;
                }

                Fitness fitness = context.calculateFitness();
                objectiveComparatorHelper.addTrajectoryFitness(
                        new TrajectoryFitness(dsm.getTrajectoryInfo().getLastTransition(), fitness));
                dsm.undoLastTransformation();
            }

            TrajectoryFitness randomBestFitness = objectiveComparatorHelper.getRandomBest();
            objectiveComparatorHelper.clearTrajectoryFitnesses();

            int compare = objectiveComparatorHelper.compare(previousFitness, randomBestFitness.fitness);

            if (compare >= 0) {
                context.calculateFitness();
                solutionStore.newSolution(context);
                logger.debug("Found solution: " + dsm.getTrajectoryInfo().toString());
                break;
            } else {
                previousFitness = randomBestFitness.fitness;
                ITransition transition = randomBestFitness.trajectory[randomBestFitness.trajectory.length - 1];
                dsm.fireActivation(transition);
            }

            if (isInterrupted.get()) {
                logger.info("Interrupted, stop exploration.");
                break;
            }

        } while (true);

        logger.info("Terminated.");
    }

    @Override
    public void interruptStrategy() {
        isInterrupted.set(true);
    }

}
