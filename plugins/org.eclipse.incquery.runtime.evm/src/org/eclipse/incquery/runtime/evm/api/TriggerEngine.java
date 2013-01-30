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
import org.eclipse.incquery.runtime.api.IncQueryMatcher;

/**
 * @author Abel Hegedus
 * 
 */
public class TriggerEngine extends RuleEngine {

    private Scheduler scheduler;

    protected TriggerEngine(final Scheduler scheduler) {
        super(checkNotNull(scheduler, "Cannot create trigger engine with null scheduler!").getExecutor().getAgenda());
        this.scheduler = scheduler;
    }

    public static TriggerEngine create(final Scheduler scheduler) {
        return new TriggerEngine(scheduler);
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
    protected <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> void fireActivations(
            boolean fireNow, RuleInstance<Match, Matcher> instance) {
        scheduler.schedule();
    }

}
