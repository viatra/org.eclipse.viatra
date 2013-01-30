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

import org.apache.log4j.Level;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.evm.api.Scheduler.ISchedulerFactory;

import com.google.common.collect.ImmutableSet;
/**
 * 
 * @author Abel Hegedus
 * 
 */
public final class EventDrivenVM {

    private EventDrivenVM() {
    }

    private static boolean debug = true;

    public static ExecutionSchema createExecutionSchema(final IncQueryEngine engine,
            final ISchedulerFactory schedulerFactory, final Set<RuleSpecification<? extends IPatternMatch, ? extends IncQueryMatcher<? extends IPatternMatch>>> ruleSpecifications) {
        checkNotNull(schedulerFactory, "Cannot create trigger engine with null scheduler factory");
        checkNotNull(ruleSpecifications, "Cannot create trigger engine with null rule specification set");
        Executor executor = new Executor(engine);
        setLoggerLevelToDebug(engine);
        Agenda agenda = executor.getAgenda();
        for (RuleSpecification<?, ?> ruleSpecification : ruleSpecifications) {
            agenda.instantiateRule(ruleSpecification);
        }
        Scheduler scheduler = schedulerFactory.prepareScheduler(executor);
        return ExecutionSchema.create(scheduler);
    }
    
    public static ExecutionSchema createExecutionSchema(final IncQueryEngine engine,
            final ISchedulerFactory schedulerFactory, final RuleSpecification<? extends IPatternMatch, ? extends IncQueryMatcher<? extends IPatternMatch>>... ruleSpecifications) {
        return createExecutionSchema(engine, schedulerFactory, ImmutableSet.copyOf(ruleSpecifications));
    }

    /**
     * @param engine
     */
    private static void setLoggerLevelToDebug(final IncQueryEngine engine) {
        if (debug) {
            engine.getLogger().setLevel((Level) Level.DEBUG);
        }
    }
    
    public static ExecutionSchema createExecutionSchema(final IncQueryEngine engine,
            final ISchedulerFactory schedulerFactory) {
        checkNotNull(schedulerFactory, "Cannot create trigger engine with null scheduler factory");
        Executor executor = new Executor(engine);
        setLoggerLevelToDebug(engine);
        Scheduler scheduler = schedulerFactory.prepareScheduler(executor);
        return ExecutionSchema.create(scheduler);
    }
    
    public static RuleEngine createRuleEngine(final IncQueryEngine engine,
            final Set<RuleSpecification<?, ?>> ruleSpecifications) {
        checkNotNull(ruleSpecifications, "Cannot create rule engine with null rule specification set");
        Agenda agenda = new Agenda(engine);
        setLoggerLevelToDebug(engine);
        for (RuleSpecification<?, ?> ruleSpecification : ruleSpecifications) {
            agenda.instantiateRule(ruleSpecification);
        }

        return RuleEngine.create(agenda);
    }
    
    public static RuleEngine createRuleEngine(final IncQueryEngine engine,
            final RuleSpecification<?, ?>... ruleSpecifications) {
        return createRuleEngine(engine, ImmutableSet.copyOf(ruleSpecifications));
    }
    
    public static RuleEngine createRuleEngine(final IncQueryEngine engine) {
        Agenda agenda = new Agenda(engine);
        setLoggerLevelToDebug(engine);
        return RuleEngine.create(agenda);
    }
}
