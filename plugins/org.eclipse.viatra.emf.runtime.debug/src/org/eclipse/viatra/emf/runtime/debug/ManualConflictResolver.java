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
package org.eclipse.viatra.emf.runtime.debug;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.resolver.ConflictSet;
import org.eclipse.viatra.emf.runtime.adapter.impl.AbstractTransformationAdapter;
import org.eclipse.viatra.emf.runtime.debug.ui.IDebuggerUI;

/**
 * Adapter implementation that enables the user to define the execution order of conflicting rule activations
 * @author Lunk Péter
 *
 */
public class ManualConflictResolver extends AbstractTransformationAdapter{
    private ConflictSet conflictSet;
    private IDebuggerUI ui;
    
    public ManualConflictResolver(IDebuggerUI usedUI){
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
