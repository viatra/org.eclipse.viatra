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

import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategy;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.GlobalContext;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.monitor.PerformanceMonitorManager;
import org.eclipse.viatra.dse.objectives.ObjectiveValuesMap;

public class DepthFirstStrategy implements IStrategy {

    private static final String UNDO_TIMER = "undoTimer";
    private static final String GET_LOCAL_FIREABLE_TRANSITIONS = "getLocalFireableTransitions";

    public class SharedData {
        public int maxDepth;
    }

    private int baseDepth = 0;
    private int initMaxDepth = Integer.MAX_VALUE;
    private SharedData sharedData;
    private Random random = new Random();
    private Logger logger = Logger.getLogger(this.getClass());
    private boolean isInterrupted = false;

    public DepthFirstStrategy() {
    }

    public DepthFirstStrategy(int maxDepth) {
        initMaxDepth = maxDepth;
    }

    @Override
    public void init(ThreadContext context) {
        GlobalContext gc = context.getGlobalContext();
        if (gc.getSharedObject() == null) {
            sharedData = new SharedData();
            sharedData.maxDepth = initMaxDepth;
            gc.setSharedObject(sharedData);
        } else {
            sharedData = (SharedData) gc.getSharedObject();
        }

        baseDepth = context.getDesignSpaceManager().getTrajectoryInfo().getDepthFromRoot();
    }

    @Override
    public ITransition getNextTransition(ThreadContext context, boolean lastWasSuccesful) {

        if (isInterrupted) {
            return null;
        }

        DesignSpaceManager dsm = context.getDesignSpaceManager();
        PerformanceMonitorManager.startTimer(GET_LOCAL_FIREABLE_TRANSITIONS);
        List<? extends ITransition> transitions = dsm.getUntraversedTransitionsFromCurrentState();
        PerformanceMonitorManager.endTimer(GET_LOCAL_FIREABLE_TRANSITIONS);

        // backtrack
        while (transitions == null || transitions.isEmpty()
                || ((baseDepth + dsm.getTrajectoryInfo().getDepthFromCrawlerRoot() >= sharedData.maxDepth) && sharedData.maxDepth > 0)) {
            PerformanceMonitorManager.startTimer(UNDO_TIMER);
            boolean didUndo = dsm.undoLastTransformation();
            PerformanceMonitorManager.endTimer(UNDO_TIMER);
            if (!didUndo) {
                return null;
            }

            logger.debug("Backtracking as there aren't anymore transitions from this state: "
                    + dsm.getCurrentState().getId());

            PerformanceMonitorManager.startTimer(GET_LOCAL_FIREABLE_TRANSITIONS);
            transitions = dsm.getUntraversedTransitionsFromCurrentState();
            PerformanceMonitorManager.endTimer(GET_LOCAL_FIREABLE_TRANSITIONS);
        }

        if (transitions.size() > 1 && context.getGlobalContext().canStartNewThread()) {
            context.getGlobalContext().tryStartNewThread(context);
        }

        int index = random.nextInt(transitions.size());
        ITransition transition = transitions.get(index);

        logger.debug("Depth: " + dsm.getTrajectoryInfo().getDepthFromCrawlerRoot() + " Next transition: "
                + transition.getId() + " From state: " + transition.getFiredFrom().getId());

        return transition;
    }

    @Override
    public void newStateIsProcessed(ThreadContext context, boolean isAlreadyTraversed, ObjectiveValuesMap objectives,
            boolean constraintsNotSatisfied) {
        if (isAlreadyTraversed || constraintsNotSatisfied || (objectives.isSatisifiesHardObjectives())) {
            logger.debug("Backtrack. Already traversed: " + isAlreadyTraversed + ". Goal state: " + (objectives!=null)
                    + ". Constraints not satisfied: " + constraintsNotSatisfied);
            context.getDesignSpaceManager().undoLastTransformation();
        }
    }

    @Override
    public void interrupted(ThreadContext context) {
        isInterrupted = true;
    }

}
