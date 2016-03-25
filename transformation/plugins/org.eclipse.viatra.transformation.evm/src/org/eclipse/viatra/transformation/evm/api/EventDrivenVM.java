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
package org.eclipse.viatra.transformation.evm.api;

import static com.google.common.base.Preconditions.checkNotNull;

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
        checkNotNull(schedulerFactory, "Cannot create execution schema with null scheduler factory");
        checkNotNull(specifications, "Cannot create execution schema with null rule specification set");
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
