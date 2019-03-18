/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary.interfaces;

import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public interface IMutation {

    /**
     * Based on the parent solution, it creates one child solution by leaving the DSE in the child solution's state
     * (model and trajectory is reachable via the context). If the child generation was unsuccessful, it backtracks to
     * the initial model: leaving the state with an empty trajectory.
     * 
     * @param parent
     *            The parent solution (trajectory)
     * @param context
     *            Context of the design space exploration
     * @return True if the child generation was successful and the context is in the child's state.
     */
    boolean mutate(TrajectoryFitness parent, ThreadContext context);

    /**
     * Creates a new instance. Required for multi-threaded execution. Can return the same instance (e.g. this) if the
     * implementation is stateless.
     * 
     * @return An instance.
     */
    IMutation createNew();

}
