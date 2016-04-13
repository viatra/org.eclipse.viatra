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
package org.eclipse.viatra.transformation.debug;

import org.eclipse.viatra.transformation.debug.model.ITransformationBreakpoint;
import org.eclipse.viatra.transformation.evm.api.Activation;

public interface ITransformationDebugListener {
    public void started();
    
    public void suspended();
    
    public void breakpointHit(ITransformationBreakpoint breakpoint);
    
    public void terminated();
    
    public void activationCreated(Activation<?> activation);

    public void activationFired(Activation<?> activation);
    
    public void displayNextActivation(Activation<?> act);
    
}
