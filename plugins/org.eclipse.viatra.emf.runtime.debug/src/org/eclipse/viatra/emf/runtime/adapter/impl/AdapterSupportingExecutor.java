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
package org.eclipse.viatra.emf.runtime.adapter.impl;

import java.util.List;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.Context;
import org.eclipse.incquery.runtime.evm.api.Executor;
import org.eclipse.incquery.runtime.evm.api.event.EventRealm;
import org.eclipse.incquery.runtime.evm.api.resolver.ChangeableConflictSet;
import org.eclipse.viatra.emf.runtime.adapter.ITransformationAdapter;

/**
 * EVM executor that uses VIATRA transformation adapters to add external functions to certain points of a VIATRA based
 * Event-driven transformation.
 * 
 * @author Lunk Péter
 *
 */
public class AdapterSupportingExecutor extends Executor {

    protected List<ITransformationAdapter> adapters;
    
    public AdapterSupportingExecutor(EventRealm eventRealm) {
        super(eventRealm);
    }
    
    public AdapterSupportingExecutor(EventRealm eventRealm, List<ITransformationAdapter> adapters) {
        super(eventRealm);
        this.adapters = adapters;
    }
    
    public AdapterSupportingExecutor(EventRealm eventRealm, Context context){
        super(eventRealm, context);
    }
    
    public AdapterSupportingExecutor(EventRealm eventRealm, Context context, List<ITransformationAdapter> adapters){
        super(eventRealm, context);
        this.adapters = adapters;
    }
    
    @Override
    protected void schedule() {
        
        if(!startScheduling()) {
            return;
        }
        
        Activation<?> nextActivation = null;
        ChangeableConflictSet conflictSet = getRuleBase().getAgenda().getConflictSet();
        //Functionality done on schedule
        for (ITransformationAdapter iTransformationAdapter : adapters) {
            iTransformationAdapter.onSchedule(conflictSet);
        }
        
        while((nextActivation = conflictSet.getNextActivation()) != null) {
            getRuleBase().getLogger().debug("Executing: " + nextActivation + " in " + this);
            //Functionality done on firing
            for (ITransformationAdapter iTransformationAdapter : adapters) {
                iTransformationAdapter.onFiring(nextActivation);
            }
            
            nextActivation.fire(getContext());
            //Do something after firing
            for (ITransformationAdapter iTransformationAdapter : adapters) {
                iTransformationAdapter.afterFiring(nextActivation);
            }
            
        }
        
        //Do something after scheduling
        for (ITransformationAdapter iTransformationAdapter : adapters) {
            iTransformationAdapter.afterSchedule(conflictSet);
        }
        endScheduling();
    }
}
