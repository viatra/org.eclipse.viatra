/*******************************************************************************
 * Copyright (c) 2004-2015, Marton Bur, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur, Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.runtime.emf.changemonitor;

import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.matchers.ViatraQueryRuntimeException;

public abstract class IChangeMonitor {

    public IChangeMonitor(ViatraQueryEngine engine) {

    }

    /**
     * Sets the model whose changes are observed. Also creates an initial checkpoint with no changes registered.
     * 
     * @param deployment
     *            the deployment model
     * @param engine
     *            engine associated with the
     * @throws ViatraQueryRuntimeException
     */
    public abstract void startMonitoring();

    /**
     * Creates a checkpoint which means: <li>Model changes since the last checkpont are saved</li> <li>The model changes
     * in the future are tracked separately from the changes before the checkpoint</li>
     * 
     * @return the DTO containing the changed elements since the last checkpoint
     */
    public abstract ChangeDelta createCheckpoint();

    /**
     * Returns all changed elements between the last two checkpoints
     * 
     * @return the DTO containing the changed elements
     */
    public abstract ChangeDelta getDeltaSinceLastCheckpoint();

}
