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
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.evm.specific.scheduler.TimedScheduler.TimedSchedulerFactory;
import org.eclipse.incquery.runtime.evm.specific.scheduler.UpdateCompleteBasedScheduler.UpdateCompleteBasedSchedulerFactory;
import org.eclipse.incquery.runtime.evm.update.IQBaseCallbackUpdateCompleteProvider;
import org.eclipse.incquery.runtime.evm.update.IQEngineUpdateCompleteProvider;
import org.eclipse.incquery.runtime.evm.update.TransactionUpdateCompleteProvider;
import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * @author Abel Hegedus
 *
 */
public final class Schedulers {

    /**
     * 
     */
    private Schedulers() {
    }

    /**
     * Creates a scheduler factory that creates schedulers by registering to the
     *  after update callback on the NavigationHelper of the given engine.
     *    
     * @param engine
     * @return
     * @deprecated Change parameter to NavigationHelper (e.g. engine.getBaseIndex()) or use getIQEngineSchedulerFactory
     */
    public static UpdateCompleteBasedSchedulerFactory getIQBaseSchedulerFactory(final IncQueryEngine engine) {
        try {
            return getIQBaseSchedulerFactory(engine.getBaseIndex());
        } catch (IncQueryException e) {
            engine.getLogger().error("Base index not available in engine", e);
            return null;
        }
    }
    
    /**
     * Creates a scheduler factory that creates schedulers by registering to the
     *  after update callback on the NavigationHelper.
     *    
     * @param navigationHelper
     * @return
     */
    public static UpdateCompleteBasedSchedulerFactory getIQBaseSchedulerFactory(final NavigationHelper navigationHelper) {
        IQBaseCallbackUpdateCompleteProvider provider;
        provider = new IQBaseCallbackUpdateCompleteProvider(navigationHelper);
        return new UpdateCompleteBasedSchedulerFactory(provider);
    }
    
    
    
    /**
     * Creates a scheduler factory that creates schedulers by registering a
     *  model update listener on the given engine.
     *    
     * @param engine
     * @return
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
     * @return
     */
    public static UpdateCompleteBasedSchedulerFactory getTransactionSchedulerFactory(final TransactionalEditingDomain domain) {
        TransactionUpdateCompleteProvider provider = new TransactionUpdateCompleteProvider(domain);
        return new UpdateCompleteBasedSchedulerFactory(provider);
    }

    /**
     * Creates a scheduler factory with the given interval.
     * @param interval
     * @return
     */
    public static TimedSchedulerFactory getTimedSchedulerFactory(long interval) {
        return new TimedSchedulerFactory(interval);
    }
    
    
}
