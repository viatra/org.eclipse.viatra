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
package org.eclipse.viatra.transformation.runtime.emf.transformation.eventdriven;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.viatra.query.runtime.api.GenericQueryGroup;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.transformation.evm.api.ExecutionSchema;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.resolver.ConflictResolver;
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

        private final String SCHEMA_ERROR = "Cannot set both Conflict Resolver and Execution Schema properties.";

        private ConflictResolver resolver;
        private ViatraQueryEngine engine;
        private ExecutionSchema schema;
        private List<EventDrivenTransformationRule<?, ?>> rules = Lists.newArrayList();

        public EventDrivenTransformationBuilder setConflictResolver(ConflictResolver resolver) {
            Preconditions.checkState(schema == null, SCHEMA_ERROR);
            this.resolver = resolver;
            return this;
        }

        public EventDrivenTransformationBuilder setScope(EMFScope scope) throws ViatraQueryException {
            this.engine = ViatraQueryEngine.on(scope);
            return this;
        }

        public EventDrivenTransformationBuilder setEngine(ViatraQueryEngine engine) {
            this.engine = engine;
            return this;
        }

        public EventDrivenTransformationBuilder setSchema(ExecutionSchema schema) {
            Preconditions.checkState(resolver == null, SCHEMA_ERROR);
            this.schema = schema;
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
            
            if (schema == null) {
                final ExecutionSchemaBuilder builder = new ExecutionSchemaBuilder().setEngine(engine);
                if (resolver != null) {
                    builder.setConflictResolver(resolver);
                }
                schema = builder.build();
            }
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
			Iterable<IQuerySpecification<?>> preconditions = Iterables.transform(notNullRules, new Function<EventDrivenTransformationRule<?, ?>, IQuerySpecification<?>>(){
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
        return new EventDrivenTransformationBuilder().setEngine(engine);
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

    public void setRules(Map<RuleSpecification<?>, EventDrivenTransformationRule<?, ?>> rules) {
        this.rules = rules;
    }
    
    public void dispose() {
        executionSchema.dispose();
    }
}
