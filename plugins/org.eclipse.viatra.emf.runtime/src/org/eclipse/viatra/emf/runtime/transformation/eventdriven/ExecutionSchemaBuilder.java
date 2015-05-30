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
package org.eclipse.viatra.emf.runtime.transformation.eventdriven;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.evm.api.ExecutionSchema;
import org.eclipse.incquery.runtime.evm.api.Scheduler.ISchedulerFactory;
import org.eclipse.incquery.runtime.evm.api.resolver.ConflictResolver;
import org.eclipse.incquery.runtime.evm.specific.ExecutionSchemas;
import org.eclipse.incquery.runtime.evm.specific.Schedulers;
import org.eclipse.incquery.runtime.evm.specific.resolver.ArbitraryOrderConflictResolver;
import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * A builder class for initializing execution schemas
 * 
 * @author Zoltan Ujhelyi
 *
 */
public class ExecutionSchemaBuilder {

    private ISchedulerFactory schedulerFactory;
    private IncQueryEngine incQueryEngine;
    private ConflictResolver conflictResolver = new ArbitraryOrderConflictResolver();

    public ExecutionSchemaBuilder setScheduler(ISchedulerFactory schedulerFactory) {
        this.schedulerFactory = schedulerFactory;
        return this;
    }

    public ExecutionSchemaBuilder setEngine(IncQueryEngine engine) {
        this.incQueryEngine = engine;
        return this;
    }

    public ExecutionSchemaBuilder setConflictResolver(ConflictResolver conflictResolver) {
        this.conflictResolver = conflictResolver;
        return this;
    }

    public ExecutionSchema build() throws IncQueryException {
        if (schedulerFactory == null) {
            schedulerFactory = Schedulers.getIQBaseSchedulerFactory(incQueryEngine.getBaseIndex());
        }

        final ExecutionSchema schema = ExecutionSchemas.createIncQueryExecutionSchema(incQueryEngine,
                schedulerFactory);
        schema.setConflictResolver(conflictResolver);
        return schema;
    }
}