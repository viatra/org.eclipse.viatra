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
package org.eclipse.viatra.dse.statecode;

/**
 * Interface for a factory class that creates instances of {@link IStateCoder} objects. This is required because state
 * coders have to be created on-demand if the design space exploration process decides that a new thread is to be
 * spawned. Since each thread requires it's own working model instance and a state coder is linked to the underlying
 * model, a new {@link IStateCoder} needs to be created per processing thread.
 * 
 * @author Miklos Foldenyi, Andras Szabolcs Nagy
 * 
 */
public interface IStateCoderFactory {

    /**
     * Creates a new {@link IStateCoder} instance specific to this {@link IStateCoderFactory}.
     * 
     * @return the new {@link IStateCoder} instance specific to this working model.
     */
    IStateCoder createStateCoder();

}
