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

import java.util.Collection;
import java.util.List;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra.dse.api.TransformationRule;

/**
 * Logically this interface's methods should be in the {@link IDesignSpaceManager} interface, this is only for
 * separation.
 * 
 * @author Andras Szabolcs Nagy
 * 
 */
public interface IGetCertainTransitions {

    // ******** No jump
    // ****************************

    /**
     * Return the untraversed {@link ITransition}s which are start from the current {@link IState}.
     * 
     * @return The transitions.
     */
    List<? extends ITransition> getUntraversedTransitionsFromCurrentState();

    /**
     * Return all of the {@link ITransition}s which are start from the current {@link IState}.
     * 
     * @return The transitions.
     */
    Collection<? extends ITransition> getTransitionsFromCurrentState();

    /**
     * Return the untraversed {@link ITransition}s which are start from the current {@link IState} and represents an
     * activation of the given rule.
     * 
     * @param ruleFilter
     *            The {@link TransformationRule}.
     * @return The filtered transitions.
     */
    List<? extends ITransition> getUntraversedTransitionsFromCurrentState(
            TransformationRule<? extends IPatternMatch> ruleFilter);

    /**
     * Return all of the {@link ITransition}s which are start from the current {@link IState} and represents an
     * activation of the given rule.
     * 
     * @param ruleFilter
     *            The {@link TransformationRule}.
     * @return The filtered transitions.
     */
    List<? extends ITransition> getTransitionsFromCurrentState(TransformationRule<? extends IPatternMatch> ruleFilter);

    // *********** Short jump
    // ****************************************

    /**
     * Returns all untraversed {@link ITransition}s which are start from the {@code numOfStatesBack} previous
     * {@link IState}s. Returns the ones along the way back too, not only the one last state's.
     * 
     * @param numOfStatesBack
     *            The number of states to fall back.
     * @return The transitions.
     */
    List<? extends ITransition> getUntraversedTransitionsOnBackWay(int numOfStatesBack);

    /**
     * Returns all untraversed {@link ITransition}s which are start from the {@link IState}s which are not farther away
     * then the {@code distance}. Distance means hops along the transitions. If the distance is 0, it returns the same
     * {@link Transition}s as {@link IGetCertainTransitions#getUntraversedTransitionsFromCurrentState()} .
     * 
     * @param distance
     *            The distance.
     * @return The transitions.
     */
    List<? extends ITransition> getUntraversedTransitionsWithMaximumDistanceOf(int distance);

}
