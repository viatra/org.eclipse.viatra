/*******************************************************************************
 * Copyright (c) 2004-2013, Istvan David, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan David - initial API and implementation
 *   Peter Lunk - revised Transformation API structure for adapter support
 *******************************************************************************/
package org.eclipse.viatra.transformation.runtime.emf.transformation.eventdriven;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.eclipse.viatra.query.runtime.api.GenericQueryGroup;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.transformation.evm.api.ExecutionSchema;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.Scheduler.ISchedulerFactory;
import org.eclipse.viatra.transformation.evm.api.adapter.AdaptableEVM;
import org.eclipse.viatra.transformation.evm.api.adapter.IAdapterConfiguration;
import org.eclipse.viatra.transformation.evm.api.adapter.IEVMAdapter;
import org.eclipse.viatra.transformation.evm.api.adapter.IEVMListener;
import org.eclipse.viatra.transformation.evm.api.resolver.ConflictResolver;
import org.eclipse.viatra.transformation.evm.specific.ExecutionSchemas;
import org.eclipse.viatra.transformation.evm.specific.Schedulers;
import org.eclipse.viatra.transformation.evm.specific.resolver.ArbitraryOrderConflictResolver;
import org.eclipse.viatra.transformation.runtime.emf.rules.EventDrivenTransformationRuleGroup;
import org.eclipse.viatra.transformation.runtime.emf.rules.eventdriven.EventDrivenTransformationRule;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class EventDrivenTransformation {
    private ViatraQueryEngine queryEngine;
    private ExecutionSchema executionSchema;
    private Map<RuleSpecification<?>, EventDrivenTransformationRule<?, ?>> rules;

    public static class EventDrivenTransformationBuilder {
        private ConflictResolver conflictResolver;
        private ViatraQueryEngine engine;
        private ISchedulerFactory schedulerFactory;
        private List<EventDrivenTransformationRule<?, ?>> rules = Lists.newArrayList();
        private List<IEVMAdapter> adapters = Lists.newArrayList();
        private List<IEVMListener> listeners = Lists.newArrayList();

        public EventDrivenTransformationBuilder setScope(EMFScope scope) throws ViatraQueryException {
            this.engine = ViatraQueryEngine.on(scope);
            return this;
        }

        public EventDrivenTransformationBuilder setQueryEngine(ViatraQueryEngine engine) {
            this.engine = engine;
            return this;
        }

        public EventDrivenTransformationBuilder addAdapter(IEVMAdapter adapter) {
            this.adapters.add(adapter);
            return this;
        }

        public EventDrivenTransformationBuilder addListener(IEVMListener listener) {
            this.listeners.add(listener);
            return this;
        }

        public EventDrivenTransformationBuilder addAdapterConfiguration(IAdapterConfiguration config) {
            this.listeners.addAll(config.getListeners());
            this.adapters.addAll(config.getAdapters());
            return this;
        }

        public EventDrivenTransformationBuilder setSchedulerFactory(ISchedulerFactory schedulerFactory) {
            this.schedulerFactory = schedulerFactory;
            return this;
        }

        public EventDrivenTransformationBuilder setConflictResolver(ConflictResolver resolver) {
            this.conflictResolver = resolver;
            return this;
        }

        public EventDrivenTransformationBuilder addRule(EventDrivenTransformationRule<?, ?> rule) {
            rules.add(rule);
            return this;
        }

        public EventDrivenTransformationBuilder addRules(EventDrivenTransformationRuleGroup ruleGroup) {
            for (EventDrivenTransformationRule<?, ?> rule : ruleGroup) {
                rules.add(rule);
            }
            return this;
        }

        public EventDrivenTransformation build() throws ViatraQueryException {
            Preconditions.checkState(engine != null, "ViatraQueryEngine must be set.");
            Map<RuleSpecification<?>, EventDrivenTransformationRule<?, ?>> rulesToAdd = Maps.newHashMap();

            if (schedulerFactory == null) {
                schedulerFactory = Schedulers.getQueryEngineSchedulerFactory(engine);
            }
            if (conflictResolver == null) {
                conflictResolver = new ArbitraryOrderConflictResolver();
            }

            AdaptableEVM vm = new AdaptableEVM();
            vm.addAdapters(adapters);
            vm.addListeners(listeners);

            ExecutionSchema schema = (adapters.size() > 0 || listeners.size() > 0)
                    ? vm.createAdaptableExecutionSchema(engine, schedulerFactory, conflictResolver)
                    : ExecutionSchemas.createViatraQueryExecutionSchema(engine, schedulerFactory, conflictResolver);

            Iterable<IQuerySpecification<?>> preconditions = collectPreconditions();
            GenericQueryGroup.of(Sets.newHashSet(preconditions)).prepare(engine);
            for (EventDrivenTransformationRule<?, ?> rule : rules) {
                schema.addRule(rule.getRuleSpecification());
                rulesToAdd.put(rule.getRuleSpecification(), rule);
            }
            EventDrivenTransformation transformation = new EventDrivenTransformation(schema, engine);
            transformation.setRules(rulesToAdd);
            return transformation;
        }

        private Iterable<IQuerySpecification<?>> collectPreconditions() {
            Iterable<EventDrivenTransformationRule<?, ?>> notNullRules = Iterables.filter(rules, Predicates.notNull());
            Iterable<IQuerySpecification<?>> preconditions = Iterables.transform(notNullRules,
                    new Function<EventDrivenTransformationRule<?, ?>, IQuerySpecification<?>>() {
                        @Override
                        public IQuerySpecification<?> apply(EventDrivenTransformationRule<?, ?> rule) {
                            return rule.getPrecondition();
                        }
                    });
            return Iterables.filter(preconditions, Predicates.notNull());
        }

    }

    public static EventDrivenTransformationBuilder forScope(EMFScope scope) throws ViatraQueryException {
        return forEngine(ViatraQueryEngine.on(scope));
    }

    public static EventDrivenTransformationBuilder forEngine(ViatraQueryEngine engine) throws ViatraQueryException {
        return new EventDrivenTransformationBuilder().setQueryEngine(engine);
    }

    private EventDrivenTransformation(ExecutionSchema executionSchema, ViatraQueryEngine queryEngine) {
        this.executionSchema = executionSchema;
        this.queryEngine = queryEngine;
    }

    public EventDrivenTransformation setDebugLevel(Level level) {
        executionSchema.getLogger().setLevel(level);
        return this;
    }

    public ViatraQueryEngine getQueryEngine() {
        return queryEngine;
    }

    public ExecutionSchema getExecutionSchema() {
        return executionSchema;
    }

    public void useDebugInfo(boolean debug) {
        if (debug) {
            executionSchema.getLogger().setLevel(Level.DEBUG);
        }
    }

    public Map<RuleSpecification<?>, EventDrivenTransformationRule<?, ?>> getTransformationRules() {
        return rules;
    }

    protected void setRules(Map<RuleSpecification<?>, EventDrivenTransformationRule<?, ?>> rules) {
        this.rules = rules;
    }

    public void dispose() {
        executionSchema.dispose();
    }
}
