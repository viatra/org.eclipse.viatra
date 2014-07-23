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

import java.util.List;

import org.eclipse.viatra.dse.api.SolutionTrajectory;

/**
 * Defines the responsibility of the DesignSpaceManager.
 * 
 * @author Andras Szabolcs Nagy
 * 
 */
public interface IDesignSpaceManager extends IGetCertainTransitions {

    /**
     * Checks the current state if it is visited yet or not.
     * 
     * @return True if the currents state is already visited.
     */
    boolean isNewModelStateAlreadyTraversed();

    /**
     * Calling this method will execute the transformation denoted by {@link ITransition transitionToFire}, and updates
     * the DesignSpace to reflect the changes.
     * 
     * @param transitionToFire
     */
    void fireActivation(final ITransition transition);

    /**
     * Undoes the last fired transformation.
     * 
     * @return False if there is nothing to undo (we are at the root). Otherwise true.
     */
    boolean undoLastTransformation();

    /**
     * Returns the current trajectory from the root (along the "undo transitions"). Maybe there are cycles in the
     * trajectory: if the strategy does not backtrack, when an already traversed state found.
     * 
     * Root will be the firedFrom attribute of the first {@link ITransition}. If the initial state is a Goal state, the
     * List contains no Transitions.
     * 
     * @return The transitions along the trajectory.
     */
    List<Object> getTrajectoryFromRoot();

    /**
     * Returns the current trajectory from the root without cycles in it. Cycles can appear if the strategy does not
     * backtrack, when an already traversed state found.
     * 
     * Root will be the firedFrom attribute of the first {@link Transition}. If the initial state is a Goal state, the
     * List contains no Transitions.
     * 
     * @return The transitions along the trajectory.
     */
    List<Object> getTrajectoryFromRootAcyclic();

    /**
     * Returns the shortest trajectory from the root to the current state.
     * 
     * Root will be the firedFrom attribute of the first {@link Transition}. If the initial state is a Goal state, the
     * List contains no Transitions.
     * 
     * @return The transitions along the trajectory.
     */
    List<Object> getTrajectoryFromRootAcyclicShortest();

    /**
     * Returns the {@link IState state} in from the {@link IDesignSpace design space} which corresponds to this
     * processing thread's working model's state.
     * 
     * @return the {@link IState} object.
     */
    IState getCurrentState();

    /**
     * Creates a {@link SolutionTrajectory} object that leads to the {@link IState state} returned by
     * {@link #getCurrentState()}.
     * 
     * @return the {@link SolutionTrajectory} object.
     */
    SolutionTrajectory createSolutionTrajectroy();

    /**
     * Returns the {@link TrajectoryInfo} object of this processing thread.
     * 
     * @return the {@link TrajectoryInfo} object.
     */
    TrajectoryInfo getTrajectoryInfo();

    /**
     * Saves the internal state of the design space in some form. It is for debugging purposes only!
     */
    void saveDesignSpace();
}
