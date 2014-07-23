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

import java.io.IOException;
import java.util.Map;

/**
 * <p>
 * This is the abstract interface of a designs space representation.
 * </p>
 * 
 * <p>
 * A {@link IDesignSpace design space} is storing {@link IState states} and {@link ITransition transitions} that
 * interconnect. A transition is either an incoming or an outgoing transition with regard to a state. Every state except
 * the first (referred to as "root state") has at least one incoming transition, and can have any number of outgoing
 * transitions.
 * </p>
 * 
 * 
 * <p>
 * For statistics about the design space itself the {@link #getNumberOfStates()} and {@link #getNumberOfTransitions()}
 * methods can be used.
 * </p>
 * 
 * <p>
 * <b>Multithreading</b>
 * </p>
 * 
 * <p>
 * If parallel processing is enabled, (which is the default case) the {@link ITransition} and {@link IState} interface
 * implementations <b>must be thread safe</b>. In this case the {@link ITransition#tryToLock() locking mechanism} must
 * also be used to avoid race conditions.
 * </p>
 * 
 * 
 */

public interface IDesignSpace {

    /**
     * Returns the root states ({@link IState} without initial inTransition).
     * 
     * @return an array of root states.
     */
    IState[] getRoot();

    /**
     * Adds root (an {@link IState} without initial inTransition) to the design space.
     * 
     * @param root
     */
    void addRoot(IState root);

    /**
     * 
     * @param sourceStateId
     * @param sourceTransitionId
     * @param newStateId
     * @param outgoingTransitionIds
     * 
     * @return true if the new state was created due to this call, false if it has already been created before.
     */
    boolean addState(ITransition sourceTransition, Object newStateId,
            Map<Object, TransitionMetaData> outgoingTransitionIds);

    IState getStateById(final Object id);

    long getNumberOfStates();

    long getNumberOfTransitions();

    void saveDesignSpace(String fileName) throws IOException;

    void addDesignSpaceChangedListener(IDesignSpaceChangeHandler changeEvent);

    void removeDesignSpaceChangedListener(IDesignSpaceChangeHandler changeEvent);
}
