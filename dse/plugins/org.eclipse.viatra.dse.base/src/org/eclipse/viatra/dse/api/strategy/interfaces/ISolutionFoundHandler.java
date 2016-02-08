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

import org.eclipse.viatra.dse.api.Solution;
import org.eclipse.viatra.dse.api.SolutionTrajectory;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.solutionstore.ISolutionStore;

/**
 * With this interface it is possible to subscribe to found solutions and execute custom code during exploration. Its
 * main purpose to be able to notify the user on a graphical interface about a new solution. This interface defines a
 * single method, which can be called by the {@link ISolutionStore} if a solution is found and its implementation
 * supports registering {@link ISolutionFoundHandler}s.
 * 
 * Depending on the implementation of the {@link ISolutionStore} implementation of this interface should be thread safe.
 * 
 * @author Andras Szabolcs Nagy
 */
public interface ISolutionFoundHandler {

    /**
     * 
     * @param trajectory
     *            The specific trajectory that has just been found.
     * @param solution
     *            The solution wrapping solution trajectories which lead to the same model state.
     * @param context
     *            The {@link ThreadContext} which contains necessary information.
     * @return The {@link ExecutationType} based on it's internal reasoning.
     */
    void solutionFound(SolutionTrajectory trajectory, Solution solution, ThreadContext context);

}
