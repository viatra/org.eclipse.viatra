/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api;

import java.util.Objects;

/**
 * An execution schema is a special rule engine and a facade
 *  for accessing the EVM. In addition to the RuleEngine,
 *  it uses a scheduler for firing activations.
 * 
 * @author Abel Hegedus
 * 
 */
public class ExecutionSchema extends RuleEngine {

    private Scheduler scheduler;

    /**
     * Creates a facade for the EVM represented by the given scheduler
     * 
     * @param scheduler
     */
    protected ExecutionSchema(final Scheduler scheduler) {
        super(Objects.requireNonNull(scheduler, "Cannot create trigger engine with null scheduler!").getExecution().getRuleBase());
        this.scheduler = scheduler;
    }

    /**
     * Creates a facede for the EVM represented by the given scheduler.
     * 
     * @param scheduler
     * @return the created facade
     */
    public static ExecutionSchema create(final Scheduler scheduler) {
        return new ExecutionSchema(scheduler);
    }

    /**
     * Disposes of the scheduler.
     */
    @Override
    public void dispose() {
        scheduler.dispose();
    }

    /**
     * @return the scheduler
     */
    protected Scheduler getScheduler() {
        return scheduler;
    }
    
    /**
     * @return the context of the executor
     */
    public Context getContext() {
        return scheduler.getExecution().getExecutor().getContext();
    }

    /**
     * Starts the executor without waiting for a scheduling event.
     * Can be used for executing enabled activations of added rules
     *  without waiting for or otherwise forcing a scheduling event.
     */
    public void startUnscheduledExecution() {
        scheduler.getExecution().schedule();
    }
}
