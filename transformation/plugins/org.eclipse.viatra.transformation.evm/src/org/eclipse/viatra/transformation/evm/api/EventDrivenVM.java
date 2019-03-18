/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Peter Lunk, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api;

import java.util.Objects;
import java.util.Set;

import org.eclipse.viatra.transformation.evm.api.Scheduler.ISchedulerFactory;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.viatra.transformation.evm.api.event.EventRealm;
/**
 * 
 * Utility class for creating new rule engines and execution schemes.
 * 
 * The static create methods use the provided parameters to set up the EVM
 * and return a facade object for accessing it.
 * 
 * @author Abel Hegedus, Peter Lunk
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
        RuleBase ruleBase = new RuleBase(eventRealm, new Agenda());
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
        Objects.requireNonNull(schedulerFactory, "Cannot create execution schema with null scheduler factory");
        Objects.requireNonNull(specifications, "Cannot create execution schema with null rule specification set");
        IExecutor executor = new Executor();
        RuleBase ruleBase = new RuleBase(eventRealm, new Agenda());
        ScheduledExecution execution = new ScheduledExecution(ruleBase, executor);
        for (RuleSpecification<?> specification : specifications) {
            instantiateRuleInRuleBase(ruleBase, specification);
        }
        Scheduler scheduler = schedulerFactory.prepareScheduler(execution);
        return ExecutionSchema.create(scheduler);
    }

    private static <EventAtom> void instantiateRuleInRuleBase(RuleBase ruleBase, RuleSpecification<EventAtom> specification) {
        EventFilter<EventAtom> emptyFilter = specification.createEmptyFilter();
        ruleBase.instantiateRule(specification, emptyFilter);
    }

}
