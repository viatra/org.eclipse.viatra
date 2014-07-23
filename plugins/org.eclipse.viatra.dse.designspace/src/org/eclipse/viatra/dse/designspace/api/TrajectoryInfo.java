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
package org.eclipse.viatra.dse.designspace.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.SolutionTrajectory;
import org.eclipse.viatra.dse.api.TransformationRule;
import org.eclipse.viatra.dse.statecode.IStateSerializerFactory;

import com.google.common.base.Preconditions;

public class TrajectoryInfo implements Cloneable {

    // the ID of the state that this particular crawler has been initialized
    private final IState initialState;

    private IState currentState;

    // the list of transitions from the original root that led to the state
    // where this crawler has been initialized. It can be null, if this crawler
    // was initialized in the original root.
    private final TrajectoryInfo trajectoryUntilInitState;

    private final LinkedList<ITransition> transitionTrajectory = new LinkedList<ITransition>();

    public TrajectoryInfo(IState initialState, TrajectoryInfo initialTrajectory) {
        Preconditions.checkNotNull(initialState);

        this.initialState = initialState;
        this.currentState = initialState;

        if (initialTrajectory != null) {
            trajectoryUntilInitState = initialTrajectory.clone();
        } else {
            trajectoryUntilInitState = null;
        }
    }

    public void addStep(ITransition transition) {
        IState target = transition.getResultsIn();
        if (target == null) {
            throw new DSEException("addStep was callled with a Transition that has no target.");
        }

        currentState = target;
        transitionTrajectory.add(transition);
    }

    public void stepBack() {
        ITransition lastTransition = transitionTrajectory.pollLast();

        if (lastTransition == null) {
            throw new DSEException("Cannot step back any further!");
        }

        currentState = lastTransition.getFiredFrom();
    }

    public Object getInitialStateId() {
        return initialState;
    }

    public IState getCurrentState() {
        return currentState;
    }

    public ITransition getLastTransition() {
        return transitionTrajectory.peekLast();
    }

    public List<Object> getFullTransitionIdTrajectory() {
        List<Object> result;

        if (trajectoryUntilInitState != null) {
            result = trajectoryUntilInitState.getFullTransitionIdTrajectory();
        } else {
            result = new ArrayList<Object>();
        }

        for (ITransition object : transitionTrajectory) {
            result.add(object.getId());
        }

        return result;
    }

    public List<ITransition> getFullTransitionTrajectory() {
        List<ITransition> result;

        if (trajectoryUntilInitState != null) {
            result = trajectoryUntilInitState.getFullTransitionTrajectory();
        } else {
            result = new ArrayList<ITransition>();
        }

        for (ITransition object : transitionTrajectory) {
            result.add(object);
        }

        return result;
    }

    /**
     * Returns the distance from the Designspace root. It returns the number of transitions, so calling while in the
     * root will result in a value of 0.
     * 
     * @return the number of transitions of the shortest trajectory from root to the current state.
     */
    public int getDepthFromRoot() {
        int depth = 0;
        if (trajectoryUntilInitState != null) {
            depth += trajectoryUntilInitState.getDepthFromRoot();
        }
        return depth + getDepthFromCrawlerRoot();
    }

    public int getDepthFromCrawlerRoot() {
        return transitionTrajectory.size();
    }

    public SolutionTrajectory createSolutionTrajectory(final IStateSerializerFactory serializerFactory) {

        List<Object> activationIds;
        List<TransformationRule<? extends IPatternMatch>> rules;

        // Recursion
        if (trajectoryUntilInitState != null) {
            SolutionTrajectory solutionTrajectory = trajectoryUntilInitState
                    .createSolutionTrajectory(serializerFactory);
            activationIds = solutionTrajectory.getActivations();
            rules = solutionTrajectory.getTransformationRules();
        } else {
            activationIds = new ArrayList<Object>();
            rules = new ArrayList<TransformationRule<? extends IPatternMatch>>();
        }

        // Putting together the list of rules
        Iterator<ITransition> transitionIterator = transitionTrajectory.iterator();

        if (transitionIterator.hasNext()) {
            ITransition transition = transitionIterator.next();
            activationIds.add(transition.getId());
            rules.add(transition.getTransitionMetaData().rule);
        }
        while (transitionIterator.hasNext()) {
            ITransition transition = transitionIterator.next();
            activationIds.add(transition.getId());
            rules.add(transition.getTransitionMetaData().rule);
        }

        return new SolutionTrajectory(getFullTransitionIdTrajectory(), rules, serializerFactory);
    }

    public boolean canStepBack() {
        return !transitionTrajectory.isEmpty();
    }

    public LinkedList<ITransition> getTransitionTrajectory() {
        return transitionTrajectory;
    }

    @Override
    public TrajectoryInfo clone() {
        TrajectoryInfo clone = new TrajectoryInfo(initialState, trajectoryUntilInitState == null ? null
                : trajectoryUntilInitState.clone());

        Iterator<ITransition> transitionIterator = transitionTrajectory.iterator();
        while (transitionIterator.hasNext()) {
            clone.addStep(transitionIterator.next());
        }

        return clone;
    }
}
