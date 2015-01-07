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
import org.eclipse.viatra.dse.api.strategy.interfaces.ISolutionFoundHandler;
import org.eclipse.viatra.dse.base.ThreadContext;

/**
 * Interface for saving and retrieving solution trajectories. <b>Implementation should be thread safe!</b>
 * 
 * @author Andras Szabolcs Nagy
 * 
 */
public interface ISolutionStore {

    /**
     * The types of responses that an object implementing {@link ISolutionStore} can give.
     */
    public enum StopExecutionType {
        /**
         * This execution thread should be stopped while the others can continue.
         */
        STOP_THREAD,
        /**
         * The whole design space exploration process should exit.
         */
        STOP_ALL,
        /**
         * The exploration should continue.
         */
        CONTINUE
    }

    /**
     * Notifies the solution store of a potential solution trajectory. Note that the {@code objectives} will never be
     * null and the implementation should be thread safe.
     * <p>
     * Also it determines whether the execution should stop or not, if a solution is found. It can have three different
     * responses:
     * </p>
     * 
     * <ul>
     * <li>{@link StopExecutionType#CONTINUE}: the execution should continue.</li>
     * <li>{@link StopExecutionType#STOP_THREAD}: this execution thread should be stopped while the others can continue.
     * </li>
     * <li>{@link StopExecutionType#STOP_ALL}: the whole design space exploration process should exit.</li>
     * </ul>
     * 
     * @param context
     *            Context with the actual state of the exploration process.
     * @param objectives
     *            Measurements returned by the used implementation of {@link ICheckGoalState}. Cannot be null (but empty
     *            is possible).
     * @return The {@link StopExecutionType} based on it's internal reasoning.
     */
    StopExecutionType newSolution(ThreadContext context, Map<String, Double> objectives);

    /**
     * Returns the currently stored solutions.
     * 
     * @return A collection of the currently stored {@link Solution}s.
     */
    Collection<Solution> getSolutions();

    /**
     * Registers a handler for executing custom code after a solution is found. When exactly it is called is determined
     * by the implementation.
     * 
     * @param handler
     *            The handler.
     */
    void registerSolutionFoundHandler(ISolutionFoundHandler handler);
}
