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

import java.util.Set;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.evm.api.Scheduler.ISchedulerFactory;
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;
import org.eclipse.incquery.runtime.evm.api.event.EventRealm;
import org.eclipse.incquery.runtime.evm.specific.ExecutionSchemas;
import org.eclipse.incquery.runtime.evm.specific.RuleEngines;
/**
 * 
 * Utility class for creating new rule engines and execution schemes.
 * 
 * The static create methods use the provided parameters to set up the EVM
 * and return a facade object for accessing it.
 * 
 * @author Abel Hegedus
 * 
 */
public final class EventDrivenVM {

    private EventDrivenVM() {
    }
    
    /**
     * Creates a new rule engine that is initialized over the given
     * EventRealm and a rule base without rules.
    
     * @param eventRealm
     * @return the prepared rule engine
     */
    public static RuleEngine createRuleEngine(final EventRealm eventRealm) {
        RuleBase ruleBase = new RuleBase(eventRealm);
        return RuleEngine.create(ruleBase);
    }

    /**
     * Creates a new execution schema that is initialized over the given
     * EventRealm, creates an executor and rule base with the given
     *  rule specifications and prepares a scheduler using the provided factory.
     * 
     * @param eventRealm
     * @param schedulerFactory
     * @param specifications
     * @return the prepared execution schema
     */
    public static ExecutionSchema createExecutionSchema(final EventRealm eventRealm,
            final ISchedulerFactory schedulerFactory, final Set<RuleSpecification<?>> specifications) {
        checkNotNull(schedulerFactory, "Cannot create execution schema with null scheduler factory");
        checkNotNull(specifications, "Cannot create execution schema with null rule specification set");
        Executor executor = new Executor(eventRealm);
        RuleBase ruleBase = executor.getRuleBase();
        for (RuleSpecification<?> specification : specifications) {
            instantiateRuleInRuleBase(ruleBase, specification);
        }
        Scheduler scheduler = schedulerFactory.prepareScheduler(executor);
        return ExecutionSchema.create(scheduler);
    }

    private static <EventAtom> void instantiateRuleInRuleBase(RuleBase ruleBase, RuleSpecification<EventAtom> specification) {
        EventFilter<EventAtom> emptyFilter = specification.createEmptyFilter();
        ruleBase.instantiateRule(specification, emptyFilter);
    }

    /**
     * Creates a new execution schema that is initialized over the given
     * IncQueryEngine, creates an executor and agenda with the given
     *  rule specifications and prepares a scheduler using the provided factory.
     * 
     * @param engine
     * @param schedulerFactory
     * @param specifications
     * @return the prepared execution schema
     * @deprecated Use {@link ExecutionSchemas#createIncQueryExecutionSchema(IncQueryEngine, ISchedulerFactory, Set)} instead
     */
    public static ExecutionSchema createIncQueryExecutionSchema(final IncQueryEngine engine,
            final ISchedulerFactory schedulerFactory, final Set<RuleSpecification<?>> specifications) {
                return ExecutionSchemas.createIncQueryExecutionSchema(engine, schedulerFactory, specifications);
            }

    /**
     * Creates a new execution schema that is initialized over the given
     * IncQueryEngine, creates an executor and agenda without rules and
     *  prepares a scheduler using the provided factory.
     * 
     * @param engine
     * @param schedulerFactory
     * @return the prepared execution schema
     * @deprecated Use {@link ExecutionSchemas#createIncQueryExecutionSchema(IncQueryEngine,ISchedulerFactory)} instead
     */
    public static ExecutionSchema createIncQueryExecutionSchema(final IncQueryEngine engine,
            final ISchedulerFactory schedulerFactory) {
                return ExecutionSchemas.createIncQueryExecutionSchema(engine, schedulerFactory);
            }
    
    /**
     * Creates a new rule engine that is initialized over the given
     * IncQueryEngine and an agenda without rules.
    
     * @param engine
     * @return the prepared rule engine
     * @deprecated Use {@link RuleEngines#createIncQueryRuleEngine(IncQueryEngine)} instead
     */
    public static RuleEngine createIncQueryRuleEngine(final IncQueryEngine engine) {
        return RuleEngines.createIncQueryRuleEngine(engine);
    }

    /**
     * Creates a new rule engine that is initialized over the given
     * IncQueryEngine and an agenda with the given rule specifications.
    
     * @param engine
     * @param specifications
     * @return the prepared rule engine
     * @deprecated Use {@link RuleEngines#createIncQueryRuleEngine(IncQueryEngine,Set)} instead
     */
    public static RuleEngine createIncQueryRuleEngine(final IncQueryEngine engine,
            final Set<RuleSpecification<?>> specifications) {
                return RuleEngines.createIncQueryRuleEngine(engine, specifications);
            }
    
}
