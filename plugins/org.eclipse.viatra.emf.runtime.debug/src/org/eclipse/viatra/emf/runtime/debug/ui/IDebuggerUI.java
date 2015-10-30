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
package org.eclipse.viatra.emf.runtime.debug.ui;

import java.util.Set;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.viatra.emf.runtime.debug.TransformationDebugger.DebuggerActions;

/**
 * Interface that defines methods for interacting with the user. These are used by the the VIATRA
 * {@link org.eclipse.viatra.emf.runtime.debug.TransformationDebugger} class, to inform the user about the
 * transformation context, and receive user input.
 * 
 * @author Lunk Péter
 *
 */
public interface IDebuggerUI {
    public void displayTransformationContext(Activation<?> act);
    
    public void displayConflictingActivations(Set<Activation<?>> activations);
    
    public DebuggerActions getDebuggerAction();
    
    public Activation<?> getSelectedActivation();
}
