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

/**
 * This class is a strategy dependents variant of the {@link SimpleSolutionStore}, implementation of the
 * {@link ISolutionStore} interface, which stores all the found solution trajectory (i.e. trajectories, which satisfy
 * all the hard objectives). It can be configured to stop the exploration after a predefined number of solutions is
 * found.
 * 
 * It is strategy dependent, hence the responsibility of calling the {@link ISolutionStore#newSolution(ThreadContext)}
 * method and stop the execution if the method returns STOP, relies on the strategy implementation.
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class StrategyDependentSolutionStore extends SimpleSolutionStore {

    @Override
    public boolean isStrategyDependent() {
        return true;
    }

}
