/*******************************************************************************
 * Copyright (c) 2004-2013, Istvan David, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan David - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.emf.runtime.transformation.eventdriven;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.evm.api.ExecutionSchema;
import org.eclipse.incquery.runtime.evm.api.resolver.ConflictResolver;
import org.eclipse.incquery.runtime.evm.specific.ExecutionSchemas;
import org.eclipse.incquery.runtime.evm.specific.Schedulers;
import org.eclipse.incquery.runtime.evm.specific.resolver.ArbitraryOrderConflictResolver;
import org.eclipse.incquery.runtime.evm.specific.scheduler.UpdateCompleteBasedScheduler.UpdateCompleteBasedSchedulerFactory;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.emf.runtime.rules.EventDrivenTransformationRuleGroup;
import org.eclipse.viatra.emf.runtime.rules.eventdriven.EventDrivenTransformationRule;
import org.eclipse.xtext.xbase.lib.Pair;

public class EventDrivenTransformation {
    private IncQueryEngine incQueryEngine;
    private UpdateCompleteBasedSchedulerFactory schedulerFactory;
    private ExecutionSchema executionSchema;
    private ConflictResolver conflictResolver;
    private List<EventDrivenTransformationRule<?, ?>> rules = new ArrayList<EventDrivenTransformationRule<?, ?>>();

    public static EventDrivenTransformation forScope(EMFScope scope) throws IncQueryException {
    	return new EventDrivenTransformation(scope);
    }
    
    /**
     * @deprecated Use {@link #forScope(EMFScope)} instead!
     */
    @Deprecated
    public static EventDrivenTransformation forSource(Notifier notifier) throws IncQueryException {
        return new EventDrivenTransformation(new EMFScope(notifier));
    }

    private EventDrivenTransformation(EMFScope scope) throws IncQueryException {
        incQueryEngine = IncQueryEngine.on(scope);
        schedulerFactory = Schedulers.getIQBaseSchedulerFactory(incQueryEngine.getBaseIndex());
        conflictResolver = new ArbitraryOrderConflictResolver();
    }

    /**
     * This method must be called in order to start the execution of the transformation!!!
     * @return
     */
    public EventDrivenTransformation create() {
        executionSchema = ExecutionSchemas.createIncQueryExecutionSchema(incQueryEngine, schedulerFactory);
        executionSchema.setConflictResolver(conflictResolver);
        for (EventDrivenTransformationRule<?, ?> rule : rules) {
            executionSchema.addRule(rule.getRuleSpecification());
        }
        return this;
    }

    public EventDrivenTransformation setDebugLevel(Level level) {
        executionSchema.getLogger().setLevel(level);
        return this;
    }

    public EventDrivenTransformation addRule(@SuppressWarnings("rawtypes") EventDrivenTransformationRule rule) {
        rules.add(rule);
        return this;
    }

    public EventDrivenTransformation addRules(EventDrivenTransformationRuleGroup ruleGroup) {
        for (Pair<?, ?> pair : ruleGroup) {
            Object key = pair.getKey();
            if (!(key instanceof EventDrivenTransformationRule)) {
                continue;
            }
            rules.add((EventDrivenTransformationRule<?, ?>) key);
        }
        return this;
    }

    public IncQueryEngine getIqEngine() {
        return incQueryEngine;
    }

    public ExecutionSchema getExecutionSchema() {
        return executionSchema;
    }

    public ConflictResolver getConflictResolver() {
        return conflictResolver;
    }

    public EventDrivenTransformation setConflictResolver(ConflictResolver conflictResolver) {
        this.conflictResolver = conflictResolver;
        return this;
    }

    public void useDebugInfo(boolean debug) {
        if (debug) {
            executionSchema.getLogger().setLevel(Level.DEBUG);
        }
    }
}
