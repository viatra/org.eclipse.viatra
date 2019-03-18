/*******************************************************************************
 * Copyright (c) 2004-2013, Istvan David, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.runtime.emf.transformation.eventdriven;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.eclipse.viatra.query.runtime.api.GenericQueryGroup;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.transformation.evm.api.ExecutionSchema;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.Scheduler.ISchedulerFactory;
import org.eclipse.viatra.transformation.evm.api.adapter.AdaptableEVM;
import org.eclipse.viatra.transformation.evm.api.adapter.AdaptableEVMFactory;
import org.eclipse.viatra.transformation.evm.api.adapter.IAdapterConfiguration;
import org.eclipse.viatra.transformation.evm.api.adapter.IEVMAdapter;
import org.eclipse.viatra.transformation.evm.api.adapter.IEVMListener;
import org.eclipse.viatra.transformation.evm.api.resolver.ConflictResolver;
import org.eclipse.viatra.transformation.evm.specific.ExecutionSchemas;
import org.eclipse.viatra.transformation.evm.specific.Schedulers;
import org.eclipse.viatra.transformation.evm.specific.resolver.ArbitraryOrderConflictResolver;
import org.eclipse.viatra.transformation.runtime.emf.rules.EventDrivenTransformationRuleGroup;
import org.eclipse.viatra.transformation.runtime.emf.rules.eventdriven.EventDrivenTransformationRule;

public class EventDrivenTransformation {
    private ViatraQueryEngine queryEngine;
    private ExecutionSchema executionSchema;
    private Map<RuleSpecification<?>, EventDrivenTransformationRule<?, ?>> rules;

    public static class EventDrivenTransformationBuilder {
        private ConflictResolver conflictResolver;
        private ViatraQueryEngine engine;
        private ISchedulerFactory schedulerFactory;
        private List<EventDrivenTransformationRule<?, ?>> rules = new ArrayList<>();
        private List<IEVMAdapter> adapters = new ArrayList<>();
        private List<IEVMListener> listeners = new ArrayList<>();

        public EventDrivenTransformationBuilder setScope(EMFScope scope) {
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

        @SuppressWarnings("unchecked")
        /**
         * @throws ViatraQueryRuntimeException
         */
        public EventDrivenTransformation build() {
            Preconditions.checkState(engine != null, "ViatraQueryEngine must be set.");
            Map<RuleSpecification<?>, EventDrivenTransformationRule<?, ?>> rulesToAdd = new HashMap<>();

            if (schedulerFactory == null) {
                schedulerFactory = Schedulers.getQueryEngineSchedulerFactory(engine);
            }
            if (conflictResolver == null) {
                conflictResolver = new ArbitraryOrderConflictResolver();
            }

            AdaptableEVM vm = AdaptableEVMFactory.getInstance().createAdaptableEVM();
            vm.addAdapters(adapters);
            vm.addListeners(listeners);

            ExecutionSchema schema = (adapters.size() > 0 || listeners.size() > 0)
                    ? vm.createAdaptableExecutionSchema(engine, schedulerFactory, conflictResolver)
                    : ExecutionSchemas.createViatraQueryExecutionSchema(engine, schedulerFactory, conflictResolver);
            
            GenericQueryGroup.of(collectPreconditions()).prepare(engine);
            for (@SuppressWarnings("rawtypes") EventDrivenTransformationRule rule : rules) {
                schema.addRule(rule.getRuleSpecification(), rule.getFilter());
                rulesToAdd.put(rule.getRuleSpecification(), rule);
            }
            EventDrivenTransformation transformation = new EventDrivenTransformation(schema, engine);
            transformation.setRules(rulesToAdd);
            vm.initialize(engine);
            return transformation;
        }

        private Set<IQuerySpecification<?>> collectPreconditions() {
            return rules.stream().filter(Objects::nonNull).map(EventDrivenTransformationRule::getPrecondition)
                    .filter(Objects::nonNull).collect(Collectors.toSet());
        }

    }

    public static EventDrivenTransformationBuilder forScope(EMFScope scope) {
        return forEngine(ViatraQueryEngine.on(scope));
    }

    public static EventDrivenTransformationBuilder forEngine(ViatraQueryEngine engine) {
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
