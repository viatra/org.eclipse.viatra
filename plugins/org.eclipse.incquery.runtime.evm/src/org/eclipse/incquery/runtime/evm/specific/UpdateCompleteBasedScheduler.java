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
package org.eclipse.incquery.runtime.evm.specific;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.evm.api.Executor;
import org.eclipse.incquery.runtime.evm.api.Scheduler;
import org.eclipse.incquery.runtime.evm.update.IQBaseCallbackUpdateCompleteProvider;
import org.eclipse.incquery.runtime.evm.update.IUpdateCompleteListener;
import org.eclipse.incquery.runtime.evm.update.IUpdateCompleteProvider;
import org.eclipse.incquery.runtime.evm.update.TransactionUpdateCompleteProvider;
import org.eclipse.incquery.runtime.exception.IncQueryException;

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

    /**
     * Creates a scheduler factory that creates schedulers by registering to the
     *  after update callback on the NavigationHelper of the given engine.
     *    
     * @param engine
     * @return
     */
    public static UpdateCompleteBasedSchedulerFactory getIQBaseSchedulerFactory(final IncQueryEngine engine) {
        IQBaseCallbackUpdateCompleteProvider provider;
        try {
            provider = new IQBaseCallbackUpdateCompleteProvider(engine.getBaseIndex());
        } catch (IncQueryException e) {
            engine.getLogger().error("Base index not available in engine", e);
            return null;
        }
        return new UpdateCompleteBasedSchedulerFactory(provider);
    }
    
    /**
     * Creates a scheduler factory that creates schedulers by registering a listener
     *  for the transaction events on the given domain.
     *  
     * @param domain
     * @return
     */
    public static UpdateCompleteBasedSchedulerFactory getTransactionSchedulerFactory(final TransactionalEditingDomain domain) {
        TransactionUpdateCompleteProvider provider = new TransactionUpdateCompleteProvider(domain);
        return new UpdateCompleteBasedSchedulerFactory(provider);
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
         * @param provider
         *            the provider to set
         */
        public void setProvider(final IUpdateCompleteProvider provider) {
            this.provider = provider;
        }

        /**
         * Creates a scheduler factory for the given provider.
         * 
         * @param provider
         */
        public UpdateCompleteBasedSchedulerFactory(final IUpdateCompleteProvider provider) {
            this.provider = provider;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.incquery.runtime.evm.api.Scheduler.ISchedulerFactory#prepareScheduler()
         */
        @Override
        public Scheduler prepareScheduler(final Executor engine) {
            UpdateCompleteBasedScheduler scheduler = new UpdateCompleteBasedScheduler(engine);
            provider.addUpdateCompleteListener(scheduler, true);
            return scheduler;
        }

    }

}
