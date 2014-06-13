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
import org.eclipse.viatra.dse.base.ThreadContext;

/**
 * This interface is the part of the strategy building blocks. Defines a method which determines whether the execution
 * must stop or not, if a solution is found.
 * 
 * @author Andras Szabolcs Nagy
 */
public interface ISolutionFound {

    /**
     * <p>
     * Determines whether the execution should stop or not, if a solution is found. It can have three different
     * responses:
     * </p>
     * 
     * 
     * <ul>
     * <li>{@link ExecutationType#CONTINUE}: the execution should continue.</li>
     * <li>{@link ExecutationType#STOP_THREAD}: this execution thread should be stopped.</li>
     * <li>{@link ExecutationType#STOP_ALL}: the whole design space exploration process should exit.</li>
     * </ul>
     * 
     * @param context
     *            The {@link ThreadContext} which contains necessary information.
     * @param solution
     *            The specific solution that has just been found.
     * @return the {@link ExecutationType} based on it's internal reasoning.
     */
    ExecutationType solutionFound(ThreadContext context, Solution solution);

    /**
     * The types of responses that an object implementing {@link ISolutionFound} can give.
     */
    public enum ExecutationType {
        STOP_THREAD, STOP_ALL, CONTINUE
    }
}
