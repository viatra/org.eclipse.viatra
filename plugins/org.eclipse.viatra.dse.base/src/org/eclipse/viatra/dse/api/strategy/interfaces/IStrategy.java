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
package org.eclipse.viatra.dse.api.strategy.interfaces;

import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.objectives.ObjectiveValuesMap;

/**
 * This interface is the part of the strategy building blocks. Defines a method for selecting the next step in the
 * design space.
 * 
 * @author Andras Szabolcs Nagy
 * 
 */
public interface IStrategy {

    /**
     * Called once before the first {@link IStrategy#getNextTransition(ThreadContext)} is called for every new
     * thread.
     * 
     * @param context
     *            The {@link ThreadContext} which contains necessary informations.
     */
    void init(ThreadContext context);

    /**
     * Returns the next {@link ITransition} to fire, the next step in the design space. It can be a quite complex method
     * or a simple depth first search.
     * 
     * @param context
     *            The {@link ThreadContext} which contains necessary informations.
     * @param lastWasSuccessful
     *            False if the last returned transition was already fired by someone, otherwise true.
     * @return An {@link ITransition} which is <b>not traversed</b> yet. Null if there is no more to fire.
     */
    ITransition getNextTransition(ThreadContext context, boolean lastWasSuccessful);

    /**
     * Called after the chosen transition is fired and the new state has been processed.
     * 
     * @param context
     *            The {@link ThreadContext} which contains necessary informations.
     * @param isAlreadyTraversed
     *            True if the new state is already traversed in the past.
     * @param objectives
     *            A map containing the objectives. It can also be empty (it is a goal state) or null (it is not a goal
     *            state).
     * @param areConstraintsSatisfied
     *            True if the new state doesn't satisfies the global constraints.
     */
    void newStateIsProcessed(ThreadContext context, boolean isAlreadyTraversed, ObjectiveValuesMap objectives,
            boolean constraintsNotSatisfied);

    /**
     * Called if the exploration process is interrupted for example by timeout. Exit by returning null in the
     * {@link IStrategy#getNextTransition(ThreadContext, boolean)} method witch is called right after this one.
     * 
     * @param context
     *            The {@link ThreadContext} which contains necessary informations.
     */
    void interrupted(ThreadContext context);
}
