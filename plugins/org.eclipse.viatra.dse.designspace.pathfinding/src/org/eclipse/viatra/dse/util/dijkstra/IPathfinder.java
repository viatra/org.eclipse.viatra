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
package org.eclipse.viatra.dse.util.dijkstra;

import org.eclipse.viatra.dse.api.Solution;
import org.eclipse.viatra.dse.api.SolutionTrajectory;
import org.eclipse.viatra.dse.designspace.api.IState;
import org.eclipse.viatra.dse.statecode.IStateSerializerFactory;

public interface IPathfinder {
    /**
     * Checks the underlying data structure to see if there is a better route to this solution than the currently known
     * best trajectory. It does not perform any calculations, just returns the data as it is currently stored.
     * 
     * @param s
     * @return
     */
    SolutionTrajectory getBestTrajectoryCheaply(Solution s, IState solutionState,
            IStateSerializerFactory stateSerializerFactory);

    /**
     * Checks the underlying data structure and updates it to see if there is a better route to this solution than the
     * currently known best trajectory. It performs any calculations necessary to provide the best results possible, and
     * returns the data as calculated.
     * 
     * @param s
     *            the {@link Solution} object we wish to update.
     * @return the {@link SolutionTrajectory} that represents the best trajectory we discovered so far.
     */
    SolutionTrajectory getBestTrajectoryCostly(Solution s, IState solutionState,
            IStateSerializerFactory stateSerializerFactory);

}
