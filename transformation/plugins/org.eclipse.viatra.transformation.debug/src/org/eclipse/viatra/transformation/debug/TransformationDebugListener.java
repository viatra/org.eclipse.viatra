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

import org.eclipse.viatra.transformation.debug.controller.IDebugController;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.adapter.AbstractEVMListener;

/**
 * Listener implementation that displays the transformation context using a
 * specific {@link IDebugController} implementation
 * 
 * @author Peter Lunk
 *
 */
public class TransformationDebugListener extends AbstractEVMListener{
    private IDebugController ui;
    
    public TransformationDebugListener(IDebugController usedUI) {
        ui = usedUI;
    }
    
    @Override
    public void beforeFiring(Activation<?> activation) {
        ui.displayTransformationContext(activation);
    }
}
