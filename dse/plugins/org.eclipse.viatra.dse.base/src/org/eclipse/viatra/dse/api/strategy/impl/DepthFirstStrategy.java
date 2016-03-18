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

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategy;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.GlobalContext;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.IGetCertainTransitions.FilterOptions;
import org.eclipse.viatra.dse.designspace.api.IState;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.objectives.Fitness;
import org.eclipse.viatra.dse.solutionstore.SolutionStore;

public class DepthFirstStrategy implements IStrategy {

    private int maxDepth;
    private AtomicBoolean isInterrupted = new AtomicBoolean(false);
    private DesignSpaceManager dsm;
    private ThreadContext context;
    private SolutionStore solutionStore;
    
    private Logger logger = Logger.getLogger(getClass());
    private FilterOptions filter = new FilterOptions().untraversedOnly();

    private Random random = new Random(); 
    
    public DepthFirstStrategy(int maxDepth) {
        if (maxDepth <= 0) {
            this.maxDepth = Integer.MAX_VALUE;
        } else {
            this.maxDepth = maxDepth;
        }
    }
    
    @Override
    public void initStrategy(ThreadContext context) {
        GlobalContext globalContext = context.getGlobalContext();
        if (globalContext.getSharedObject() == null) {
            globalContext.setSharedObject(new Object());
            int maxThreads = globalContext.getThreadPool().getMaximumPoolSize();
            for (int i = 1; i < maxThreads; i++) {
                globalContext.tryStartNewThread(context, context.getModel(), true, new DepthFirstStrategy(maxDepth));
            }
        }
        
        this.context = context;
        dsm = context.getDesignSpaceManager();
        solutionStore = context.getGlobalContext().getSolutionStore2();
        
        logger.info("Initied");
    }

    @Override
    public void explore() {

        mainloop: do {

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

            if (dsm.getTrajectoryInfo().getDepthFromCrawlerRoot() >= maxDepth) {
                boolean isSuccessfulUndo = dsm.undoLastTransformation();
                if (!isSuccessfulUndo) {
                    logger.info("Reached max depth but cannot bactrack.");
                    break;
                } else {
                    logger.debug("Reached max depth, bactrack.");
                    continue;
                }
            }
            
            if (isInterrupted.get()) {
                logger.info("Interrupted, stop exploration.");
                break;
            }

            ITransition transition = null;
            do {
                Collection<? extends ITransition> transitions;

                do {
                    transitions = dsm.getTransitionsFromCurrentState(filter);
                    if (transitions.isEmpty()) {
                        boolean isSuccessfulUndo = dsm.undoLastTransformation();
                        if (!isSuccessfulUndo) {
                            logger.info("No more transitions from current state and cannot backtrack.");
                            break mainloop;
                        } else {
                            logger.debug("No more transitions from current state, backtrack.");
                            continue;
                        }
                    } 
                } while (transitions.isEmpty());

                int index = random.nextInt(transitions.size());
                
                Iterator<? extends ITransition> iterator = transitions.iterator();
                while (index-- > 0) {
                    iterator.next();
                }
                transition = iterator.next();
                
            } while (!transition.tryToLock());

            IState prevState = dsm.getCurrentState();
            dsm.fireActivation(transition);

            if (logger.isDebugEnabled()) {
                logger.debug("Transition "
                        + transition.getId()
                        + " fired from: "
                        + prevState.getId()
                        + " and reached: "
                        + dsm.getCurrentState().getId());
            }
            
            boolean loopInTrajectory = dsm.isCurentStateInTrajectory();
            if (loopInTrajectory) {
                boolean isSuccessfulUndo = dsm.undoLastTransformation();
                if (!isSuccessfulUndo) {
                    throw new DSEException("The new state is present in the trajectoy but cannot bactkrack. Should never happen!");
                } else {
                    logger.info("The new state is already visited in the trajectory, backtrack.");
                }
            }
            
//            boolean isAlreadyTraversed = dsm.isNewModelStateAlreadyTraversed();
//            if (isAlreadyTraversed) {
//                boolean isSuccessfulUndo = dsm.undoLastTransformation();
//                if (!isSuccessfulUndo) {
//                    logger.info("Already traversed state but cannot backtrack.");
//                    break;
//                } else {
//                    logger.debug("Already traversed state, backtrack.");
//                }
//            }

        } while (true);
        
        logger.info("Terminated.");
    }

    @Override
    public void interruptStrategy() {
        isInterrupted.set(true);
    }

}
