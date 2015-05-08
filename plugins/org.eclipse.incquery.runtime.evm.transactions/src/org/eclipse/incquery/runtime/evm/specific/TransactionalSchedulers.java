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
import org.eclipse.incquery.runtime.api.scope.IBaseIndex;
import org.eclipse.incquery.runtime.evm.specific.scheduler.TimedScheduler.TimedSchedulerFactory;
import org.eclipse.incquery.runtime.evm.specific.scheduler.UpdateCompleteBasedScheduler.UpdateCompleteBasedSchedulerFactory;
import org.eclipse.incquery.runtime.evm.update.IQBaseCallbackUpdateCompleteProvider;
import org.eclipse.incquery.runtime.evm.update.IQEngineUpdateCompleteProvider;
import org.eclipse.incquery.runtime.evm.update.TransactionUpdateCompleteProvider;

/**
 * @author Abel Hegedus
 *
 */
public final class TransactionalSchedulers {

    /**
     * 
     */
    private TransactionalSchedulers() {
    }

    /**
     * Creates a scheduler factory that creates schedulers by registering to the
     *  after update callback on the NavigationHelper.
     *    
     * @param index
     */
    public static UpdateCompleteBasedSchedulerFactory getIQBaseSchedulerFactory(final IBaseIndex index) {
        IQBaseCallbackUpdateCompleteProvider provider;
        provider = new IQBaseCallbackUpdateCompleteProvider(index);
        return new UpdateCompleteBasedSchedulerFactory(provider);
    }
    
    /**
     * Creates a scheduler factory that creates schedulers by registering a
     *  model update listener on the given engine.
     *    
     * @param engine
     */
    public static UpdateCompleteBasedSchedulerFactory getIQEngineSchedulerFactory(final IncQueryEngine engine) {
        IQEngineUpdateCompleteProvider provider;
        provider = new IQEngineUpdateCompleteProvider(engine);
        return new UpdateCompleteBasedSchedulerFactory(provider);
    }

    /**
     * Creates a scheduler factory that creates schedulers by registering a listener
     *  for the transaction events on the given domain.
     *  
     * @param domain
     */
    public static UpdateCompleteBasedSchedulerFactory getTransactionSchedulerFactory(final TransactionalEditingDomain domain) {
        TransactionUpdateCompleteProvider provider = new TransactionUpdateCompleteProvider(domain);
        return new UpdateCompleteBasedSchedulerFactory(provider);
    }

    /**
     * Creates a scheduler factory with the given interval.
     * @param interval
     */
    public static TimedSchedulerFactory getTimedSchedulerFactory(long interval) {
        return new TimedSchedulerFactory(interval);
    }
    
    
}
