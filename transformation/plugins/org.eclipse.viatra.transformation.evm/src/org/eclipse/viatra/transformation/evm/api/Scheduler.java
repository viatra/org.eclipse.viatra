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

import static com.google.common.base.Preconditions.checkNotNull;

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
        this.execution = checkNotNull(execution, "Cannot create scheduler with null VIATRA Scheduled Execution!");
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
