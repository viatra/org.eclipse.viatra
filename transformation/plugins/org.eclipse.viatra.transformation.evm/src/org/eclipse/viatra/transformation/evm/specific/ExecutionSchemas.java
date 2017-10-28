/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *   Peter Lunk - revised EVM structure for adapter support
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific;

import java.util.Collections;
import java.util.Set;

import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.transformation.evm.api.EventDrivenVM;
import org.eclipse.viatra.transformation.evm.api.ExecutionSchema;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.Scheduler.ISchedulerFactory;
import org.eclipse.viatra.transformation.evm.api.resolver.ConflictResolver;
import org.eclipse.viatra.transformation.evm.specific.event.ViatraQueryEventRealm;

/**
 * @author Abel Hegedus, Peter Lunk
 *
 */
public class ExecutionSchemas {

    private ExecutionSchemas() {/*Utility class constructor*/}
    
    /**
     * Creates a new execution schema that is initialized over the given ViatraQueryEngine, creates an executor and
     * agenda with the given rule specifications and prepares a scheduler using the provided factory.
     * 
     * @param engine
     * @param schedulerFactory
     * @param specifications
     * @return the prepared execution schema
     */
    public static ExecutionSchema createViatraQueryExecutionSchema(final ViatraQueryEngine engine,
            final ISchedulerFactory schedulerFactory, final Set<RuleSpecification<?>> specifications) {
        return EventDrivenVM.createExecutionSchema(ViatraQueryEventRealm.create(engine), schedulerFactory,
                specifications);
    }

    /**
     * Creates a new execution schema that is initialized over the given ViatraQueryEngine, creates an executor and
     * agenda without rules and prepares a scheduler using the provided factory.
     * 
     * @param engine
     * @param schedulerFactory
     * @return the prepared execution schema
     */
    public static ExecutionSchema createViatraQueryExecutionSchema(final ViatraQueryEngine engine,
            final ISchedulerFactory schedulerFactory) {
        return createViatraQueryExecutionSchema(engine, schedulerFactory, Collections.<RuleSpecification<?>>emptySet());
    }

    /**
     * Creates a new execution schema that is initialized over the given {@link ViatraQueryEngine},
     * {@link ISchedulerFactory} and {@link ConflictResolver} creates an executor and agenda without rules, prepares a
     * scheduler using the provided factory and sets its scheduler factory accordingly.
     * 
     * @param engine
     * @param schedulerFactory
     * @return the prepared execution schema
     */
    public static ExecutionSchema createViatraQueryExecutionSchema(final ViatraQueryEngine engine,
            final ISchedulerFactory schedulerFactory, final ConflictResolver conflictResolver) {
        ExecutionSchema schema = createViatraQueryExecutionSchema(engine, schedulerFactory);
        schema.setConflictResolver(conflictResolver);
        return schema;
    }

}
