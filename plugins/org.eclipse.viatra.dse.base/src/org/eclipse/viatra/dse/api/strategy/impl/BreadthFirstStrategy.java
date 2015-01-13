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
import java.util.Collections;
import java.util.LinkedList;

import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategy;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.IState;
import org.eclipse.viatra.dse.designspace.api.IState.TraversalStateType;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.designspace.api.TrajectoryInfo;
import org.eclipse.viatra.dse.objectives.ObjectiveValuesMap;

public class BreadthFirstStrategy implements IStrategy {

    private class TransitionWrapper {
        private ITransition transition;
        private TrajectoryInfo trajectory;
        private boolean isFirst = false;
        private IState state;

        public TransitionWrapper(ITransition transition, TrajectoryInfo trajectory, IState state) {
            this.transition = transition;
            this.trajectory = trajectory;
            this.state = state;
        }

    }

    private int maxDepth = 0;
    private LinkedList<TransitionWrapper> transitions = new LinkedList<TransitionWrapper>();
    private int actDepth = 0;
    private int remainingTransitions = 0;
    private int transitionsInNextLevel = 0;
    private TransitionWrapper t;
    private boolean isInterrupted = false;

    public BreadthFirstStrategy() {
    }

    public BreadthFirstStrategy(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    @Override
    public ITransition getNextTransition(ThreadContext context, boolean lastWasSuccesful) {

        // TODO: For some reason it keeps failing from time to time
        // For me it failed at the level of 10 and 11, in the 2/3 of the time
        if ((maxDepth > 0 && actDepth > maxDepth) || isInterrupted) {
            return null;
        }

        ITransition result = null;
        DesignSpaceManager dsm = context.getDesignSpaceManager();
        // clone trajectory, so we can get back to this state later.
        TrajectoryInfo currentTrajectoryInfo = dsm.getTrajectoryInfo().clone();

        Collection<? extends ITransition> transitionsFromCurrentState;

        // If the state is already traversed, or is in cut or goal state -> no
        // new transitions to add to the queue.
        if (!dsm.getCurrentState().getTraversalState().equals(TraversalStateType.TRAVERSED)
                || dsm.isNewModelStateAlreadyTraversed()) {
            transitionsFromCurrentState = Collections.emptyList();
        }
        // Else get all transitions from this state.
        else {
            transitionsFromCurrentState = dsm.getTransitionsFromCurrentState();
        }

        // Add transitions to the queue
        // System.out.println("Adding new transitions to the queue.");
        boolean isFirst = true;
        for (ITransition t : transitionsFromCurrentState) {
            transitions.addLast(new TransitionWrapper(t, currentTrajectoryInfo, dsm.getCurrentState()));
            if (isFirst) {
                transitions.getLast().isFirst = true;
                isFirst = false;
            }
        }

        // Variables for calculating actual depth
        --remainingTransitions;
        transitionsInNextLevel += transitionsFromCurrentState.size();

        if (transitions.isEmpty()) {
            result = null;
        } else if (!transitions.getFirst().isFirst) {
            dsm.undoLastTransformation();
            t = transitions.pollFirst();
            result = t.transition;
        } else {
            // Go back to the root
            for (int i = actDepth; i > 0; i--) {
                dsm.undoLastTransformation();
            }

            t = transitions.pollFirst();

            // Got to the parent state of the transition
            LinkedList<ITransition> trajectory = t.trajectory.getTransitionTrajectory();
            // if we will search in the next level, we should move forward once
            // more, cause actDepth is incremented later.
            int actTempDepth = actDepth + (remainingTransitions <= 0 ? 1 : 0);
            for (int i = trajectory.size() - (actTempDepth - 1); i < trajectory.size(); i++) {
                dsm.fireActivation(trajectory.get(i));
            }

            result = t.transition;
        }
        // Increment the actual depth if there are no more transitions in this
        // level.
        if (remainingTransitions <= 0) {
            actDepth++;
            remainingTransitions = transitionsInNextLevel;
            transitionsInNextLevel = 0;
        }

        return result;

    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    @Override
    public void init(ThreadContext context) {
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
