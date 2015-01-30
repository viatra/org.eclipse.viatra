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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Set;

import org.apache.log4j.Level;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.evm.api.ExecutionSchema;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.api.resolver.ConflictResolver;
import org.eclipse.incquery.runtime.evm.specific.ExecutionSchemas;
import org.eclipse.incquery.runtime.evm.specific.Schedulers;
import org.eclipse.incquery.runtime.evm.specific.resolver.ArbitraryOrderConflictResolver;
import org.eclipse.incquery.runtime.evm.specific.scheduler.UpdateCompleteBasedScheduler.UpdateCompleteBasedSchedulerFactory;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.emf.runtime.rules.EventDrivenTransformationRuleGroup;
import org.eclipse.xtext.xbase.lib.Pair;

import com.google.common.collect.Sets;

public class EventDrivenTransformation {
    private IncQueryEngine incQueryEngine;
    private UpdateCompleteBasedSchedulerFactory schedulerFactory;
    private ExecutionSchema executionSchema;
    private ConflictResolver conflictResolver;
    private Set<EventDrivenTransformationRule<?, ?>> rules = Sets.newHashSet();

    /**
     * @deprecated Use {@link #forSource(Notifier)} instead.
     */
    @Deprecated
    public static EventDrivenTransformation forResource(ResourceSet resourceSet) {
        return new EventDrivenTransformation(resourceSet);
    }

    /**
     * @deprecated Use {@link #forSource(Notifier)} instead.
     */
    @Deprecated
    public static EventDrivenTransformation forResource(Resource resource) {
        checkArgument(resource.getResourceSet() != null);
        return new EventDrivenTransformation(resource.getResourceSet());
    }

    public static EventDrivenTransformation forSource(Notifier notifier) {
        return new EventDrivenTransformation(notifier);
    }

    /**
     * @deprecated Use {@link #EventDrivenTransformation(Notifier)} instead.
     */
    @Deprecated
    private EventDrivenTransformation(ResourceSet resourceSet) {
        try {
            incQueryEngine = IncQueryEngine.on(resourceSet);
            schedulerFactory = Schedulers.getIQBaseSchedulerFactory(incQueryEngine.getBaseIndex());
            conflictResolver = new ArbitraryOrderConflictResolver();
        } catch (IncQueryException e) {
            e.printStackTrace();
        }
    }

    private EventDrivenTransformation(Notifier source) {
        try {
            incQueryEngine = IncQueryEngine.on(new EMFScope(source));
            schedulerFactory = Schedulers.getIQBaseSchedulerFactory(incQueryEngine.getBaseIndex());
            conflictResolver = new ArbitraryOrderConflictResolver();
        } catch (IncQueryException e) {
            e.printStackTrace();
        }
    }

    public EventDrivenTransformation create() {
        Set<RuleSpecification<?>> ruleSpecifications = Sets.newHashSet();

        for (EventDrivenTransformationRule<?, ?> rule : rules) {
            ruleSpecifications.add(rule.getRuleSpecification());
        }

        executionSchema = ExecutionSchemas.createIncQueryExecutionSchema(incQueryEngine, schedulerFactory,
                ruleSpecifications);
        executionSchema.setConflictResolver(conflictResolver);

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
