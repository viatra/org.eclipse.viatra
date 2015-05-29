/*******************************************************************************
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.emf.mwe2integration.eventdriven;

/**
 * An interface that defines methods for explicitly controlling the execution of a fine-grained, event-driven model
 * transformation step.
 * 
 * @author Peter Lunk
 *
 */
public interface IController {

    /**
     * Starts the execution of the given step
     */
    public void run();

    /**
     * If the Step has finished it returns true
     * 
     * @return
     */
    public boolean isFinished();
}
