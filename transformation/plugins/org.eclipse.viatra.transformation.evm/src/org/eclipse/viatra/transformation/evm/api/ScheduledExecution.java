/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *   Peter Lunk - revised EVM structure for adapter support
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api;

import org.eclipse.viatra.transformation.evm.api.event.EventRealm;

/**
 * The Scheduled Execution is responsible for firing enabled activations of its {@link RuleBase},
 * when its {@link Scheduler} notifies it.
 * 
 * @author Abel Hegedus, Peter Lunk
 *
 */
public class ScheduledExecution {
    private final IExecutor executor;
    private final RuleBase ruleBase;
    private boolean scheduling = false;
    private final String startMessage = "Executing started in " + this;
    private final String reentrantMessage = "Reentrant schedule call ignored in " + this;
    private final String endMessage = "Executing ended in " + this;
    
    
    /**
     * Creates an execution for the given {@link EventRealm}, {@link Executor} and {@link Agenda}.
     * Executions are usually created as part of an ExecutionSchema 
     * through the EventDrivenVM.createExecutionSchema methods.
     * 
     * @param eventRealm
     * @param executor
     * @param agenda
     */
    public ScheduledExecution(final EventRealm eventRealm, Agenda agenda, IExecutor executor) {
        this(new RuleBase(eventRealm, agenda), executor);
    }
    
    /**
     * Creates an execution for the given {@link EventRealm}.
     * Executions are usually created as part of an ExecutionSchema 
     * through the EventDrivenVM.createExecutionSchema methods.
     * 
     * @param eventRealm
     * @param executor
     * @param agenda
     */
    public ScheduledExecution(final EventRealm eventRealm) {
        this(new RuleBase(eventRealm, new Agenda()), new Executor());
    }
    
    /**
     * Creates an execution for the given {@link Executor} and {@link RuleBase}.
     * Executions are usually created as part of an ExecutionSchema 
     * through the EventDrivenVM.createExecutionSchema methods.
     * 
     * @param eventRealm
     * @param executor
     * @param ruleBase
     */
    public ScheduledExecution(RuleBase ruleBase, IExecutor executor) {
        this.ruleBase = ruleBase;
        this.executor = executor; 
    }
    
    public RuleBase getRuleBase() {
        return ruleBase;
    }
    
    public IExecutor getExecutor() {
        return executor;
    }
    
    protected void schedule() {
        
        if(!startScheduling()) {
            return;
        }
        
        executor.startExecution("Scheduling");
        executor.execute(new ConflictSetIterator(ruleBase.getAgenda().getConflictSet()));
        executor.endExecution("Scheduling");
        
        endScheduling();
    }
            
    /**
     * This method is called from schedule() to indicate that a new call
     * was received. If there is already scheduling in progress, that is 
     * logged and false is returned.
     * 
     * Otherwise, a new scheduling starts, which is logged and stored.
     * 
     * @return true, if the firing strategy can start, false otherwise
     */
    protected synchronized boolean startScheduling() {
        if(scheduling) {
            ruleBase.getLogger().trace(reentrantMessage);
            return false;
        } else {
            scheduling = true;
            ruleBase.getLogger().trace(startMessage);
            return true;
        }
    }
    
    /**
     * This method is called by schedule() to indicate that the firing 
     * strategy is finished its execution. This is logged and the scheduling
     * state is set to false.
     */
    protected synchronized void endScheduling() {
        ruleBase.getLogger().trace(endMessage);
        scheduling = false;
    }
    
    /**
     * Disposes of the execution by disposing its ruleBase.
     * 
     */
    protected void dispose() {
        ruleBase.dispose();
    }
}
