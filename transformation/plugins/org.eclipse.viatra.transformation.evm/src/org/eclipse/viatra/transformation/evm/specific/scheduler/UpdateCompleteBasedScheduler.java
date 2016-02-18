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
package org.eclipse.viatra.transformation.evm.specific.scheduler;

import org.eclipse.viatra.transformation.evm.api.Executor;
import org.eclipse.viatra.transformation.evm.api.Scheduler;
import org.eclipse.viatra.transformation.evm.update.IUpdateCompleteListener;
import org.eclipse.viatra.transformation.evm.update.IUpdateCompleteProvider;

/**
 * This scheduler uses update complete events to schedule its executor.
 * 
 * It provides two default implementations, one using the NavigationHelper
 *  after update callback, the other uses Transaction commit events.
 * 
 * @author Abel Hegedus
 * 
 */
public class UpdateCompleteBasedScheduler extends Scheduler implements IUpdateCompleteListener {

    private UpdateCompleteBasedSchedulerFactory factory;
    
    @Override
    public void updateComplete() {
        schedule();
    }

    /**
     * Creates a scheduler for the given executor.
     */
    protected UpdateCompleteBasedScheduler(final Executor executor) {
        super(executor);
    }

    @Override
    public void dispose() {
        factory.provider.removeUpdateCompleteListener(this);
        super.dispose();
    }

    /**
     * This scheduler factory implementation uses an update complete provider that sends notifications 
     * to the prepared schedulers.
     *  
     * @author Abel Hegedus
     *
     */
    public static class UpdateCompleteBasedSchedulerFactory implements ISchedulerFactory {

        private IUpdateCompleteProvider provider;

        /**
         * @return the provider
         */
        public IUpdateCompleteProvider getProvider() {
            return provider;
        }

        /**
         * Creates a scheduler factory for the given provider.
         * 
         * @param provider
         */
        public UpdateCompleteBasedSchedulerFactory(final IUpdateCompleteProvider provider) {
            this.provider = provider;
        }

        @Override
        public Scheduler prepareScheduler(final Executor engine) {
            UpdateCompleteBasedScheduler scheduler = new UpdateCompleteBasedScheduler(engine);
            scheduler.factory = this;
            provider.addUpdateCompleteListener(scheduler, true);
            return scheduler;
        }
        
    }

}
