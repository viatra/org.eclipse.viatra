/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific;

import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.scope.IBaseIndex;
import org.eclipse.viatra.transformation.evm.specific.scheduler.TimedScheduler.TimedSchedulerFactory;
import org.eclipse.viatra.transformation.evm.specific.scheduler.UpdateCompleteBasedScheduler.UpdateCompleteBasedSchedulerFactory;
import org.eclipse.viatra.transformation.evm.update.IQBaseCallbackUpdateCompleteProvider;
import org.eclipse.viatra.transformation.evm.update.QueryEngineUpdateCompleteProvider;

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
    public static UpdateCompleteBasedSchedulerFactory getQueryEngineSchedulerFactory(final ViatraQueryEngine engine) {
        QueryEngineUpdateCompleteProvider provider;
        provider = new QueryEngineUpdateCompleteProvider(engine);
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
