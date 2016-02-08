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
package org.eclipse.viatra.emf.mwe2integration.debug;

import java.util.List;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.Context;
import org.eclipse.incquery.runtime.evm.api.event.EventRealm;
import org.eclipse.incquery.runtime.evm.api.resolver.ChangeableConflictSet;
import org.eclipse.viatra.emf.mwe2integration.eventdriven.IController;
import org.eclipse.viatra.emf.mwe2integration.eventdriven.mwe2impl.MWE2ControlledExecutor;
import org.eclipse.viatra.emf.runtime.adapter.ITransformationAdapter;

/**
 * EVM executor that uses VIATRA transformation adapters to add external functions to certain points of a VIATRA based
 * Event-driven transformation. This executor can be used to incorporate any VIATRA based event driven transformation
 * into MWE2 workflows.
 * 
 * @author Peter Lunk
 *
 */
public class MWE2ControlledAdaptableExecutor extends MWE2ControlledExecutor implements IController{
    protected List<ITransformationAdapter> adapters;
       
    
    public MWE2ControlledAdaptableExecutor(EventRealm eventRealm) {
        super(eventRealm);
    }
    
    public MWE2ControlledAdaptableExecutor(EventRealm eventRealm, List<ITransformationAdapter> adapters) {
        super(eventRealm);
        this.adapters = adapters;
    }
    
    public MWE2ControlledAdaptableExecutor(EventRealm eventRealm, Context context){
        super(eventRealm, context);
    }
    
    public MWE2ControlledAdaptableExecutor(EventRealm eventRealm, Context context, List<ITransformationAdapter> adapters){
        super(eventRealm, context);
        this.adapters = adapters;
    }
        
    @Override
    protected void schedule() {
        if (canRun) {
            finished = false;

            doSchedule();

            finished = true;
            canRun = false;
            scheduled = false;
        } else if (!scheduled) {
            scheduled = true;
        }
    }

    @Override
    public void run() {
        if (scheduled) {
            finished = false;

            doSchedule();

            finished = true;
            canRun = false;
            scheduled = false;
        } else if (!canRun) {
            canRun = true;
        }
        
    }

    
    private void doSchedule() {
        if(!startScheduling()) {
            return;
        }
        
        Activation<?> nextActivation = null;
        ChangeableConflictSet conflictSet = getRuleBase().getAgenda().getConflictSet();
        //Functionality done on schedule
        for (ITransformationAdapter iTransformationAdapter : adapters) {
            conflictSet = (ChangeableConflictSet) iTransformationAdapter.beforeSchedule(conflictSet);
        }
        
        while((nextActivation = conflictSet.getNextActivation()) != null) {
            getRuleBase().getLogger().debug("Executing: " + nextActivation + " in " + this);
            //Functionality done on firing
            for (ITransformationAdapter iTransformationAdapter : adapters) {
                nextActivation = iTransformationAdapter.beforeFiring(nextActivation);
            }
            
            nextActivation.fire(getContext());
            //Do something after firing
            for (ITransformationAdapter iTransformationAdapter : adapters) {
                nextActivation = iTransformationAdapter.afterFiring(nextActivation);
            }
            
        }
        
        //Do something after scheduling
        for (ITransformationAdapter iTransformationAdapter : adapters) {
            conflictSet = (ChangeableConflictSet) iTransformationAdapter.afterSchedule(conflictSet);
        }
        endScheduling();
    }

    @Override
    public boolean isFinished() {
        return finished;
    }
}
