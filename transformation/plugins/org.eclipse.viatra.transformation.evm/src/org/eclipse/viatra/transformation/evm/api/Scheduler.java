/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The scheduler is used to define when the executor of a schema should
 * start its execution strategy.
 * 
 * @author Abel Hegedus
 * 
 */
public abstract class Scheduler {
    
    /**
     * Factory interface for preparing a scheduler for a given executor.
     * 
     * @author Abel Hegedus
     *
     */
    public interface ISchedulerFactory{
       
        /**
         * Creates a scheduler for the given executor.
         * 
         * @param executor
         */
        Scheduler prepareScheduler(final Executor executor);

    }

    private Executor executor;

    /**
     * Creates a scheduler for the given executor.
     * 
     * @param executor
     */
    protected Scheduler(final Executor executor) {
        this.executor = checkNotNull(executor, "Cannot create scheduler with null IncQuery Engine!");
    }

    /**
     * Notifies executor of "tick". Subclasses should call this method to generate "ticks".
     */
    protected void schedule() {
        executor.schedule();
    }
    
    /**
     * @return the executor
     */
    public Executor getExecutor() {
        return executor;
    }
    
    /**
     * Disposes of the scheduler by disposing its executor.
     */
    public void dispose() {
        executor.dispose();
    }
    
}
