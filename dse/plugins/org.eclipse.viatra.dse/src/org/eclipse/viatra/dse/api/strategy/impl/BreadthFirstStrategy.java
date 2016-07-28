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
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategy;
import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategyFactory;
import org.eclipse.viatra.dse.base.GlobalContext;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.objectives.Fitness;
import org.eclipse.viatra.dse.solutionstore.SolutionStore;

public class BreadthFirstStrategy implements IStrategy {

    private static final class BfsSharedObject {
        private final ConcurrentLinkedQueue<Object[]> trajectoryQueue1 = new ConcurrentLinkedQueue<>();
        private final ConcurrentLinkedQueue<Object[]> trajectoryQueue2 = new ConcurrentLinkedQueue<>();

        private final AtomicBoolean pushToQueue1 = new AtomicBoolean(false);
        private final AtomicBoolean designSpaceTraversed = new AtomicBoolean(false);

        public final CyclicBarrier barrier;

        public BfsSharedObject(int numberOfThreads) {
            barrier = new CyclicBarrier(numberOfThreads, new Runnable() {
                @Override
                public void run() {
                    boolean oldValue = pushToQueue1.get();
                    pushToQueue1.set(!oldValue);
                    if (trajectoryQueue1.isEmpty() && trajectoryQueue2.isEmpty()) {
                        designSpaceTraversed.set(true);
                    }
                }
            });
        }

        public Object[] poll() {
            if (pushToQueue1.get()) {
                return trajectoryQueue2.poll();
            } else {
                return trajectoryQueue1.poll();
            }
        }

        public void push(Object[] trajectory) {
            if (pushToQueue1.get()) {
                trajectoryQueue1.add(trajectory);
            } else {
                trajectoryQueue2.add(trajectory);
            }
        }

        public boolean isDesignSpaceTraversed() {
            return designSpaceTraversed.get();
        }
    }

    private int maxDepth = 0;
    private BfsSharedObject shared;
    private boolean isInterrupted = false;
    private ThreadContext context;
    private Logger logger = Logger.getLogger(getClass());
    private SolutionStore solutionStore;
    private boolean isFirstThread = false;

    public BreadthFirstStrategy() {
    }

    public BreadthFirstStrategy(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    @Override
    public void initStrategy(ThreadContext context) {
        this.context = context;
        this.solutionStore = context.getGlobalContext().getSolutionStore();

        GlobalContext globalContext = context.getGlobalContext();
        if (globalContext.getSharedObject() == null) {
            isFirstThread = true;
            shared = new BfsSharedObject(globalContext.getThreadPool().getMaximumPoolSize());
            globalContext.setSharedObject(shared);
        } else {
            shared = (BfsSharedObject) globalContext.getSharedObject();
        }
    }

    @Override
    public void explore() {

        if (isFirstThread) {

            boolean globalConstraintsAreSatisfied = context.checkGlobalConstraints();
            if (!globalConstraintsAreSatisfied) {
                logger.info("Global contraint is not satisifed in the first state. Terminate.");
                return;
            }

            Fitness fitness = context.calculateFitness();
            if (fitness.isSatisifiesHardObjectives()) {
                context.newSolution();
                logger.info("First state is a solution. Terminate.");
                return;
            }

            Object[] currentTrajectory = context.getTrajectory().toArray(new Object[0]);

            shared.push(currentTrajectory);

            startThreads();
        } else {
            try {
                shared.barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
            }
        }

        mainLoop: while (!isInterrupted && !shared.isDesignSpaceTraversed()) {

            Object[] next = shared.poll();
            while (next == null) {
                try {
                    logger.debug("Reached barrier.");
                    shared.barrier.await();
                } catch (InterruptedException | BrokenBarrierException e1) {
                }
                if (isInterrupted || shared.isDesignSpaceTraversed()) {
                    break mainLoop;
                }
                next = shared.poll();
            }

            context.backtrackUntilRoot();

            for (Object a : next) {
                context.executeAcitvationId(a);
            }

            Collection<Object> activationIds = context.getCurrentActivationIds();
            int i = activationIds.size() - 1;

            while (!isInterrupted && i >= 0) {

                Iterator<Object> iterator = activationIds.iterator();
                int index = i--;
                while (iterator.hasNext() && index > 0) {
                    index--;
                    iterator.next();
                }
                Object activationIdToTry = iterator.next();

                context.executeAcitvationId(activationIdToTry);

                if (context.isCurrentStateAlreadyTraversed()) {
                    logger.info("The new state is already visited.");
                } else if (!context.checkGlobalConstraints()) {
                    logger.debug("Global contraint is not satisifed.");
                } else if (context.calculateFitness().isSatisifiesHardObjectives()) {
                    solutionStore.newSolution(context);
                    logger.debug("Found a solution.");
                } else if (maxDepth > 0 && context.getDepth() >= maxDepth) {
                    logger.debug("Reached max depth.");
                } else {
                    Object[] currentTrajectory = context.getTrajectory().toArray(new Object[0]);
                    shared.push(currentTrajectory);
                }

                context.backtrack();
            }

        }
    }

    private void startThreads() {
        context.startAllThreads(new IStrategyFactory() {
            @Override
            public IStrategy createStrategy() {
                return new BreadthFirstStrategy(maxDepth);
            }
        });
    }

    @Override
    public void interruptStrategy() {
        isInterrupted = true;
        shared.barrier.reset();
    }

}
