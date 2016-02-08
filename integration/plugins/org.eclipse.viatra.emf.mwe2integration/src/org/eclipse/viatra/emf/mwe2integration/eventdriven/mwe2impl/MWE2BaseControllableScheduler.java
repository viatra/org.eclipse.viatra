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
package org.eclipse.viatra.emf.mwe2integration.eventdriven.mwe2impl;

import org.eclipse.incquery.runtime.evm.api.Executor;
import org.eclipse.incquery.runtime.evm.api.Scheduler;
import org.eclipse.viatra.emf.mwe2integration.eventdriven.IController;
import org.eclipse.viatra.emf.mwe2integration.eventdriven.ISchedulerController;

/**
 * An EVM scheduler that enables the workflow to explicitly control the execution of a fine-grained event-driven
 * transformation. As it implements the IController interface, its usage is similar to the MWE2ControllableExecutor.
 * Internally, however, this solution uses a custom Scheduler object instead of an Executor. Using a custom Scheduler
 * means, that the original IncQuery EVM scheduler will be overridden.
 * 
 * @author Peter Lunk
 *
 */
public class MWE2BaseControllableScheduler extends Scheduler implements IController {

    protected ISchedulerController<MWE2BaseControllableScheduler> controller;
    protected boolean finished = false;

    public boolean isFinished() {
        return finished;
    }

    protected MWE2BaseControllableScheduler(Executor executor,
            ISchedulerController<MWE2BaseControllableScheduler> controller) {
        super(executor);
        this.controller = controller;
    }

    public void run() {
        finished = false;
        schedule();
        finished = true;
    }

    /**
     * ISchedulerFactory implementation that enables the instantiation of MWE2BaseControllableScheduler objects. It is
     * used during the creation of the execution schema of the controllable EVM based event-driven transformation.
     * 
     * @author Peter Lunk
     *
     */
    public static class MWEBaseControllableSchedulerFactory implements ISchedulerFactory,
            ISchedulerController<MWE2BaseControllableScheduler> {

        private MWE2BaseControllableScheduler scheduler;

        @Override
        public Scheduler prepareScheduler(final Executor engine) {
            MWE2BaseControllableScheduler scheduler = new MWE2BaseControllableScheduler(engine, this);
            setScheduler(scheduler);
            return scheduler;
        }

        @Override
        public void setScheduler(MWE2BaseControllableScheduler scheduler) {
            this.scheduler = scheduler;

        }

        @Override
        public void run() {
            if (scheduler != null) {
                scheduler.run();
            }
        }

        @Override
        public boolean isFinished() {
            if (scheduler != null) {
                return scheduler.isFinished();
            }
            return false;
        }

    }

}
