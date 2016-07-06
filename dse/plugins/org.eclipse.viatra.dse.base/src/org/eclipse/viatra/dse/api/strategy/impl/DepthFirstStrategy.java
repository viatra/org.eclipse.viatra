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
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.objectives.Fitness;

public class DepthFirstStrategy implements IStrategy {

    private int maxDepth;
    private AtomicBoolean isInterrupted = new AtomicBoolean(false);
    private ThreadContext context;
    
    private Logger logger = Logger.getLogger(getClass());

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
        if (context.getSharedObject() == null) {
            context.setSharedObject(new Object());
            while (context.tryStartNewThread(new DepthFirstStrategy(maxDepth)) != null) {
            }
        }
        
        this.context = context;
        
        logger.info("Initied");
    }

    @Override
    public void explore() {

        mainloop: do {

            boolean globalConstraintsAreSatisfied = context.checkGlobalConstraints();
            if (!globalConstraintsAreSatisfied) {
                boolean isSuccessfulUndo = context.backtrack();
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
                context.newSolution();
                boolean isSuccessfulUndo = context.backtrack();
                if (!isSuccessfulUndo) {
                    logger.info("Found a solution but cannot backtrack.");
                    break;
                } else {
                    logger.debug("Found a solution, backtrack.");
                    continue;
                }
            }

            if (context.getDepth() >= maxDepth) {
                boolean isSuccessfulUndo = context.backtrack();
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

            Object activationId = null;
            Collection<Object> activationIds;

            do {
                activationIds = context.getUntraversedActivationIds();
                if (activationIds.isEmpty()) {
                    boolean isSuccessfulUndo = context.backtrack();
                    if (!isSuccessfulUndo) {
                        logger.info("No more transitions from current state and cannot backtrack.");
                        break mainloop;
                    } else {
                        logger.debug("No more transitions from current state, backtrack.");
                        continue;
                    }
                } 
            } while (activationIds.isEmpty());

            int index = random.nextInt(activationIds.size());
            
            Iterator<Object> iterator = activationIds.iterator();
            while (index-- > 0) {
                iterator.next();
            }
            activationId = iterator.next();
                
            Object prevStateId = context.getCurrentStateId();
            context.executeAcitvationId(activationId);

            if (logger.isDebugEnabled()) {
                logger.debug("Activation id: "
                        + activationId
                        + " fired from: "
                        + prevStateId
                        + " and reached: "
                        + context.getCurrentStateId());
            }
            
            boolean loopInTrajectory = context.isCurrentStateInTrajectory();
            if (loopInTrajectory) {
                boolean isSuccessfulUndo = context.backtrack();
                if (!isSuccessfulUndo) {
                    throw new DSEException("The new state is present in the trajectoy but cannot bactkrack. Should never happen!");
                } else {
                    logger.info("The new state is already visited in the trajectory, backtrack.");
                }
            }
            
        } while (true);
        
        logger.info("Terminated.");
    }

    @Override
    public void interruptStrategy() {
        isInterrupted.set(true);
    }

}
