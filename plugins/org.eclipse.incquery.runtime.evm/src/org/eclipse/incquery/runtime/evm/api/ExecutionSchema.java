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
package org.eclipse.incquery.runtime.evm.api;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.incquery.runtime.api.IPatternMatch;

/**
 * @author Abel Hegedus
 * 
 */
public class ExecutionSchema extends RuleEngine {

    private Scheduler scheduler;

    protected ExecutionSchema(final Scheduler scheduler) {
        super(checkNotNull(scheduler, "Cannot create trigger engine with null scheduler!").getExecutor().getAgenda());
        this.scheduler = scheduler;
    }

    public static ExecutionSchema create(final Scheduler scheduler) {
        return new ExecutionSchema(scheduler);
    }

    public void dispose() {
        scheduler.dispose();
    }

    /**
     * @return the scheduler
     */
    protected Scheduler getScheduler() {
        return scheduler;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.evm.api.RuleEngine#fireActivations(boolean, org.eclipse.incquery.runtime.evm.api.RuleInstance)
     */
    @Override
    protected <Match extends IPatternMatch> void fireActivations(
            boolean fireNow, RuleInstance<Match> instance) {
        scheduler.schedule();
    }

}
