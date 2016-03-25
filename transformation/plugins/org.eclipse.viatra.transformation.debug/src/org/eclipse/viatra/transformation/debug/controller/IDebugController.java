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
package org.eclipse.viatra.transformation.debug.controller;

import java.util.Set;

import org.eclipse.viatra.transformation.debug.DebuggerActions;
import org.eclipse.viatra.transformation.evm.api.Activation;

/**
 * Interface that defines methods to interact with the user. These are used by the the VIATRA
 * {@link org.eclipse.viatra.transformation.debug.TransformationDebugListener} class, to inform the user about the
 * transformation context, and receive user input.
 * 
 * @author Peter Lunk
 *
 */
public interface IDebugController {
    public void displayTransformationContext(Activation<?> act);
    
    public void displayConflictingActivations(Set<Activation<?>> activations);
    
    public DebuggerActions getDebuggerAction();
    
    public Activation<?> getSelectedActivation();
}
