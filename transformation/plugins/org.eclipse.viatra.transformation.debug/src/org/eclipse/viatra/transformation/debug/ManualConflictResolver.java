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

import org.eclipse.viatra.transformation.debug.adapter.impl.AbstractTransformationAdapter;
import org.eclipse.viatra.transformation.debug.controller.IDebugController;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.resolver.ConflictSet;

/**
 * Adapter implementation that enables the user to define the execution order of conflicting rule activations
 * 
 * @author Peter Lunk
 *
 */
public class ManualConflictResolver extends AbstractTransformationAdapter{
    private ConflictSet conflictSet;
    private IDebugController ui;
    
    public ManualConflictResolver(IDebugController usedUI){
        ui = usedUI;
    }

    @Override
    public Activation<?> beforeFiring(Activation<?> activation) {
        ui.displayConflictingActivations(conflictSet.getConflictingActivations());
        
        Activation<?> temp  = ui.getSelectedActivation();
        
        if(temp!=null){
            return temp;          
        }
        return activation;
    }

    @Override
    public ConflictSet beforeSchedule(ConflictSet conflictSet) {
        this.conflictSet = conflictSet;
        return conflictSet;
    }
}
