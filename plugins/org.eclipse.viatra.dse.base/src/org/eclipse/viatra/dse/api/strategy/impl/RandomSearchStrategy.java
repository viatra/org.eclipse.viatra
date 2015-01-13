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

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.viatra.dse.api.strategy.interfaces.IExplorerThread;
import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategy;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.GlobalContext;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.designspace.api.TrajectoryInfo;
import org.eclipse.viatra.dse.objectives.ObjectiveValuesMap;

public class RandomSearchStrategy implements IStrategy {

    private class SharedData {
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
    private boolean isInterrupted = false;

    public RandomSearchStrategy(int minDepth, int maxDepth, int numberOfTries) {
        shared = new SharedData(minDepth, maxDepth, numberOfTries);
    }

    private RandomSearchStrategy() {
    }

    @Override
    public void init(ThreadContext context) {
        dsm = context.getDesignSpaceManager();
        trajectoryInfo = dsm.getTrajectoryInfo();
        gc = context.getGlobalContext();

        Object sharedObject = gc.getSharedObject();
        if (sharedObject == null) {
            gc.setSharedObject(shared);
            while (tryStartNewThread(context) != null) {
            }
        } else {
            shared = (SharedData) sharedObject;
        }
    }

    @Override
    public ITransition getNextTransition(ThreadContext context, boolean lastWasSuccessful) {

        if (isInterrupted) {
            return null;
        }

        do {
            if (trajectoryInfo.getDepthFromRoot() < maxDepth) {

                Collection<? extends ITransition> transitions = dsm.getTransitionsFromCurrentState();
                int index = rnd.nextInt(transitions.size());
                ITransition transition = getByIndex(transitions, index);
                if (transition.isAssignedToFire()) {
                    dsm.fireActivation(transition);
                } else {
                    return transition;
                }

            } else if ((nth = shared.triesLeft.getAndDecrement()) > 0) {

                while (dsm.undoLastTransformation()) {
                }

                tryStartNewThread(context);

                maxDepth = rnd.nextInt(shared.maxDepth - shared.minDepth) + shared.minDepth;

            } else {
                return null;
            }
        } while (gc.getState().equals(GlobalContext.ExplorationProcessState.RUNNING));

        return null;
    }

    private IExplorerThread tryStartNewThread(ThreadContext context) {
        return gc.tryStartNewThread(context, context.getModelRoot(), true, new RandomSearchStrategy());
    }

    @Override
    public void newStateIsProcessed(ThreadContext context, boolean isAlreadyTraversed, ObjectiveValuesMap objectives,
            boolean constraintsNotSatisfied) {
        if (constraintsNotSatisfied) {
            dsm.undoLastTransformation();
        }
    }

    @Override
    public void interrupted(ThreadContext context) {
        isInterrupted = true;
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
