/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Peter Lunk, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api;

import java.util.Objects;

/**
 * The scheduler is used to define when the executor of a schema should start its execution strategy.
 * 
 * @author Abel Hegedus, Peter Lunk
 * 
 */
public abstract class Scheduler {

    /**
     * Factory interface for preparing a scheduler for a given executor.
     * 
     * @author Abel Hegedus
     *
     */
    public interface ISchedulerFactory {

        /**
         * Creates a scheduler for the given executor.
         * 
         * @param execution
         */
        Scheduler prepareScheduler(final ScheduledExecution execution);

    }

    private ScheduledExecution execution;

    /**
     * Creates a scheduler for the given executor.
     * 
     * @param executor
     */
    protected Scheduler(final ScheduledExecution execution) {
        this.execution = Objects.requireNonNull(execution, "Cannot create scheduler with null VIATRA Scheduled Execution!");
    }

    /**
     * Notifies executor of "tick". Subclasses should call this method to generate "ticks".
     */
    protected void schedule() {
        execution.schedule();
    }

    public ScheduledExecution getExecution() {
        return execution;
    }

    /**
     * Disposes of the scheduler by disposing its executor.
     */
    public void dispose() {
        execution.dispose();
    }

}
