/*******************************************************************************
 * Copyright (c) 2004-2015, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.runtime.emf.transformation.eventdriven;

import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.exception.IncQueryException;
import org.eclipse.viatra.transformation.evm.api.ExecutionSchema;
import org.eclipse.viatra.transformation.evm.api.Executor;
import org.eclipse.viatra.transformation.evm.api.Scheduler;
import org.eclipse.viatra.transformation.evm.api.Scheduler.ISchedulerFactory;
import org.eclipse.viatra.transformation.evm.api.resolver.ConflictResolver;
import org.eclipse.viatra.transformation.evm.specific.Schedulers;
import org.eclipse.viatra.transformation.evm.specific.event.IncQueryEventRealm;
import org.eclipse.viatra.transformation.evm.specific.resolver.ArbitraryOrderConflictResolver;

/**
 * A builder class for initializing execution schemas
 * 
 * @author Zoltan Ujhelyi
 *
 */
public class ExecutionSchemaBuilder {

    private ISchedulerFactory schedulerFactory;
    private ViatraQueryEngine queryEngine;
    private ConflictResolver conflictResolver = new ArbitraryOrderConflictResolver();
    private Executor executor;

    public ExecutionSchemaBuilder setScheduler(ISchedulerFactory schedulerFactory) {
        this.schedulerFactory = schedulerFactory;
        return this;
    }

    public ExecutionSchemaBuilder setEngine(ViatraQueryEngine engine) {
        this.queryEngine = engine;
        return this;
    }

    public ExecutionSchemaBuilder setConflictResolver(ConflictResolver conflictResolver) {
        this.conflictResolver = conflictResolver;
        return this;
    }
    
    public ExecutionSchemaBuilder setExecutor(Executor executor) {
        this.executor = executor;
        return this;
    }

    public ExecutionSchema build() throws IncQueryException {
        if (schedulerFactory == null) {
            schedulerFactory = Schedulers.getQueryEngineSchedulerFactory(queryEngine);
        }
        if (executor == null) {
            executor = new Executor(IncQueryEventRealm.create(queryEngine));
        }

        Scheduler scheduler = schedulerFactory.prepareScheduler(executor);
        final ExecutionSchema schema = ExecutionSchema.create(scheduler);
        
        schema.setConflictResolver(conflictResolver);
        return schema;
    }
}
