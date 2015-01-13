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
package org.eclipse.viatra.dse.api.strategy.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategy;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.GlobalContext;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.IState;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.designspace.api.TrajectoryInfo;
import org.eclipse.viatra.dse.objectives.ObjectiveValuesMap;

public class ParallelBFSStrategy implements IStrategy {

    private class TrajectoryWrapper {
        public final LinkedList<ITransition> transitionTrajectory;

        @SuppressWarnings("unchecked")
        public TrajectoryWrapper(TrajectoryInfo trajectory) {
            transitionTrajectory = (LinkedList<ITransition>) trajectory.getTransitionTrajectory().clone();
        }

    }

    private class SharedData {
        public volatile ConcurrentLinkedQueue<TrajectoryWrapper> pullQueue = new ConcurrentLinkedQueue<TrajectoryWrapper>();
        public volatile ConcurrentLinkedQueue<TrajectoryWrapper> pushQueue = new ConcurrentLinkedQueue<TrajectoryWrapper>();
        public volatile int maxDepth = Integer.MAX_VALUE;
        public volatile int actLevel = 0;

        public AtomicInteger numOfThreadsAtBarrier = new AtomicInteger();
        public volatile int maxNumberOfThreads = 0;
        public volatile boolean barrier1 = true;
        public volatile boolean barrier2 = true;

        public volatile boolean isAllExplored = false;
    }

    private SharedData sharedData;

    private int initMaxDepth = Integer.MAX_VALUE;

    private Logger logger = Logger.getLogger(this.getClass());

    private boolean isInterrupted = false;;

    public ParallelBFSStrategy() {
    }

    public ParallelBFSStrategy(int maxDepth) {
        this.initMaxDepth = maxDepth;
    }

    @Override
    public void init(ThreadContext context) {
        GlobalContext gc = context.getGlobalContext();
        if (gc.getSharedObject() == null) {
            sharedData = new SharedData();
            sharedData.maxDepth = initMaxDepth;
            sharedData.maxNumberOfThreads = context.getGlobalContext().getThreadPool().getMaximumPoolSize();
            gc.setSharedObject(sharedData);

            while (context.getGlobalContext().tryStartNewThread(context) != null) {
            }
        } else {
            sharedData = (SharedData) gc.getSharedObject();
        }

    }

    @Override
    public ITransition getNextTransition(ThreadContext context, boolean lastWasSuccesful) {

        DesignSpaceManager dsm = context.getDesignSpaceManager();
        TrajectoryInfo trajectory = dsm.getTrajectoryInfo();

        if ((sharedData.maxDepth > 0 && sharedData.actLevel > sharedData.maxDepth) || isInterrupted) {
            return null;
        }

        if (trajectory.canStepBack()) {
            // push to queue only, if not already traversed, not goal and not cut
            if (!dsm.isNewModelStateAlreadyTraversed()
                    && dsm.getCurrentState().getTraversalState().equals(IState.TraversalStateType.TRAVERSED)) {
                sharedData.pushQueue.add(new TrajectoryWrapper(trajectory));
            }

            dsm.undoLastTransformation();
        }

        List<? extends ITransition> transitions = dsm.getUntraversedTransitionsFromCurrentState();
        do {
            if (!transitions.isEmpty()) {

                ITransition transition = transitions.get(0);
                logger.debug("Next transition: " + transition.getId());
                return transition;

            } else {
                TrajectoryWrapper next = sharedData.pullQueue.poll();
                if (next == null) {

                    logger.debug("Reachd barrier of depth of " + dsm.getTrajectoryInfo().getDepthFromCrawlerRoot());

                    int actLevel = sharedData.actLevel;
                    int position = sharedData.numOfThreadsAtBarrier.incrementAndGet();
                    if (position == sharedData.maxNumberOfThreads) {
                        if (sharedData.pushQueue.size() == 0) {
                            sharedData.isAllExplored = true;
                        }
                        sharedData.pullQueue = sharedData.pushQueue;
                        sharedData.pushQueue = new ConcurrentLinkedQueue<TrajectoryWrapper>();
                        sharedData.maxNumberOfThreads = context.getGlobalContext().getThreadPool().getMaximumPoolSize();
                        sharedData.numOfThreadsAtBarrier.set(0);
                        if (sharedData.actLevel % 2 == 0) {
                            ++sharedData.actLevel;
                            sharedData.barrier1 = false;
                            sharedData.barrier2 = true;
                        } else {
                            ++sharedData.actLevel;
                            sharedData.barrier2 = false;
                            sharedData.barrier1 = true;
                        }
                    } else {
                        do {
                            try {
                                Thread.sleep(2);
                            } catch (InterruptedException e) {
                            }
                        } while (actLevel % 2 == 0 ? sharedData.barrier1 : sharedData.barrier2);
                    }
                } else {

                    while (dsm.undoLastTransformation()) {
                    }

                    for (ITransition t : next.transitionTrajectory) {
                        dsm.fireActivation(t);
                    }

                    logger.debug("Moved to state: " + dsm.getCurrentState().getId());

                }
                transitions = dsm.getUntraversedTransitionsFromCurrentState();
            }
        } while (!sharedData.isAllExplored);

        logger.debug("Design space is explored.");

        return null;

    }

    @Override
    public void newStateIsProcessed(ThreadContext context, boolean isAlreadyTraversed, ObjectiveValuesMap objectives,
            boolean constraintsNotSatisfied) {
    }

    @Override
    public void interrupted(ThreadContext context) {
        isInterrupted = true;
    }
}
