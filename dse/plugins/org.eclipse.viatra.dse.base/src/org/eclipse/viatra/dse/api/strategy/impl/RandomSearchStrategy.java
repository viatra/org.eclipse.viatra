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

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategy;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ExplorerThread;
import org.eclipse.viatra.dse.base.GlobalContext;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.designspace.api.TrajectoryInfo;
import org.eclipse.viatra.dse.objectives.Fitness;
import org.eclipse.viatra.dse.solutionstore.SolutionStore;

public class RandomSearchStrategy implements IStrategy {

    private static class SharedData {
        public final AtomicInteger triesLeft;
        public final int minDepth;
        public final int maxDepth;

        public SharedData(int minDepth, int maxDepth, int numberOfTries) {
            this.minDepth = minDepth;
            this.maxDepth = maxDepth;
            this.triesLeft = new AtomicInteger(numberOfTries);
        }
    }

    private DesignSpaceManager dsm;
    private GlobalContext gc;
    private int maxDepth = -1;
    private Random rnd = new Random();
    private SharedData shared;
    private TrajectoryInfo trajectoryInfo;
    int nth;
    private ThreadContext context;
    private AtomicBoolean isInterrupted = new AtomicBoolean(false);
    private SolutionStore solutionStore;
    private Logger logger = Logger.getLogger(getClass());

    public RandomSearchStrategy(int minDepth, int maxDepth, int numberOfTries) {
        shared = new SharedData(minDepth, maxDepth, numberOfTries);
    }

    private RandomSearchStrategy() {
    }

    @Override
    public void initStrategy(ThreadContext context) {
        this.context = context;
        dsm = context.getDesignSpaceManager();
        trajectoryInfo = dsm.getTrajectoryInfo();
        gc = context.getGlobalContext();
        solutionStore = gc.getSolutionStore2();

        Object sharedObject = gc.getSharedObject();
        if (sharedObject == null) {
            gc.setSharedObject(shared);
            while (tryStartNewThread(context) != null) {
            }
        } else {
            shared = (SharedData) sharedObject;
        }

        maxDepth = rnd.nextInt(shared.maxDepth - shared.minDepth) + shared.minDepth;

        logger.info("Initied");
    }

    @Override
    public void explore() {

        do {

            boolean globalConstraintsAreSatisfied = context.checkGlobalConstraints();
            if (!globalConstraintsAreSatisfied) {
                boolean isSuccessfulUndo = dsm.undoLastTransformation();
                if (!isSuccessfulUndo) {
                    logger.info("Global contraint is not satisifed and cannot backtrack.");
                    break;
                } else {
                    logger.debug("Global contraint is not satisifed, backtrack.");
                    continue;
                }
            }

            Fitness fitness = context.calculateFitness();
            if (fitness.isSatisifiesHardObjectives()) {
                solutionStore.newSolution(context);
                boolean isSuccessfulUndo = dsm.undoLastTransformation();
                if (!isSuccessfulUndo) {
                    logger.info("Found a solution but cannot backtrack.");
                    break;
                } else {
                    logger.debug("Found a solution, backtrack.");
                    continue;
                }
            }

            if (trajectoryInfo.getDepthFromRoot() < maxDepth) {

                Collection<? extends ITransition> transitions = dsm.getTransitionsFromCurrentState();
                int index = rnd.nextInt(transitions.size());
                ITransition transition = getByIndex(transitions, index);
                dsm.fireActivation(transition);

            } else {

                nth = shared.triesLeft.getAndDecrement();
                logger.debug(nth + " tries left");
                if (nth > 0) {

                    while (dsm.undoLastTransformation()) {
                    }
                    maxDepth = rnd.nextInt(shared.maxDepth - shared.minDepth) + shared.minDepth;

                } else {
                    break;
                }
            }

            boolean loopInTrajectory = dsm.isCurentStateInTrajectory();
            if (loopInTrajectory) {
                boolean isSuccessfulUndo = dsm.undoLastTransformation();
                if (!isSuccessfulUndo) {
                    throw new DSEException(
                            "The new state is present in the trajectoy but cannot bactkrack. Should never happen!");
                } else {
                    logger.info("The new state is already visited in the trajectory, backtrack.");
                }
            }

        } while (isInterrupted.get());

        logger.info("Terminated.");
    }

    @Override
    public void interruptStrategy() {
        isInterrupted.set(true);
    }

    private ExplorerThread tryStartNewThread(ThreadContext context) {
        return gc.tryStartNewThread(context, context.getModel(), true, new RandomSearchStrategy());
    }

    private static ITransition getByIndex(Collection<? extends ITransition> availableTransitions, int index) {
        int i = 0;
        Iterator<? extends ITransition> iterator = availableTransitions.iterator();
        while (iterator.hasNext()) {
            ITransition transition = iterator.next();
            if (i == index) {
                return transition;
            } else {
                ++i;
            }
        }
        throw new IndexOutOfBoundsException("size: " + availableTransitions.size() + ", index: " + index);
    }
}
