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
package org.eclipse.viatra.dse.solutionstore;

import java.util.Collection;
import java.util.Map;

import org.eclipse.viatra.dse.api.Solution;
import org.eclipse.viatra.dse.api.strategy.interfaces.ICheckGoalState;
import org.eclipse.viatra.dse.base.ThreadContext;

/**
 * Interface for saving and retrieving solution trajectories.
 * 
 * @author Andras Szabolcs Nagy
 * 
 */
public interface ISolutionStore {

    /**
     * Notifies the solution store of a potential solution trajectory.
     * 
     * @param context
     *            Context with the actual state of the exploration process.
     * @param measurements
     *            Measurements returned by the used implementation of {@link ICheckGoalState}. Can be null.
     * @return The newly created solution or null if it is not considered as a solution.
     */
    Solution newSolution(ThreadContext context, Map<String, Double> measurements);

    /**
     * Returns the currently stored solutions.
     * 
     * @return
     */
    Collection<Solution> getSolutions();
}
