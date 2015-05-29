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

import java.util.List;

import org.apache.log4j.Level;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.evm.api.ExecutionSchema;
import org.eclipse.incquery.runtime.evm.api.resolver.ConflictResolver;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.emf.runtime.rules.EventDrivenTransformationRuleGroup;
import org.eclipse.viatra.emf.runtime.rules.eventdriven.EventDrivenTransformationRule;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class EventDrivenTransformation {
    private IncQueryEngine incQueryEngine;
    private ExecutionSchema executionSchema;

    public static class EventDrivenTransformationBuilder {

        private final String SCHEMA_ERROR = "Cannot set both Conflict Resolver and Execution Schema properties.";

        private ConflictResolver resolver;
        private IncQueryEngine engine;
        private ExecutionSchema schema;
        private List<EventDrivenTransformationRule<?, ?>> rules = Lists.newArrayList();

        public EventDrivenTransformationBuilder setConflictResolver(ConflictResolver resolver) {
            Preconditions.checkState(schema == null, SCHEMA_ERROR);
            this.resolver = resolver;
            return this;
        }

        public EventDrivenTransformationBuilder setScope(EMFScope scope) throws IncQueryException {
            this.engine = IncQueryEngine.on(scope);
            return this;
        }

        public EventDrivenTransformationBuilder setEngine(IncQueryEngine engine) {
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
        
        /**
         * @deprecated Use {@link #build()} instead.
         */
        public EventDrivenTransformation create() throws IncQueryException {
            return build();
        }

        public EventDrivenTransformation build() throws IncQueryException {
            Preconditions.checkState(engine != null, "IncQueryEngine must be set.");
            if (schema == null) {
                final ExecutionSchemaBuilder builder = new ExecutionSchemaBuilder().setEngine(engine);
                if (resolver != null) {
                    builder.setConflictResolver(resolver);
                }
                schema = builder.build();
            }
            for (EventDrivenTransformationRule<?, ?> rule : rules) {
                schema.addRule(rule.getRuleSpecification());
            }
            return new EventDrivenTransformation(schema, engine);

        }
    }

    public static EventDrivenTransformationBuilder forScope(EMFScope scope) throws IncQueryException {
        return forEngine(IncQueryEngine.on(scope));
    }
    
    public static EventDrivenTransformationBuilder forEngine(IncQueryEngine engine) throws IncQueryException {
        return new EventDrivenTransformationBuilder().setEngine(engine);
    }
    
    /**
     * @deprecated Use {@link #forScope(EMFScope)} or {@link #forEngine(IncQueryEngine)} instead!
     */
    @Deprecated
    public static EventDrivenTransformationBuilder forSource(Notifier notifier) throws IncQueryException {
        return EventDrivenTransformation.forScope(new EMFScope(notifier));
    }

    private EventDrivenTransformation(ExecutionSchema executionSchema, IncQueryEngine incQueryEngine) {
        this.executionSchema = executionSchema;
        this.incQueryEngine = incQueryEngine;
    }

    public EventDrivenTransformation setDebugLevel(Level level) {
        executionSchema.getLogger().setLevel(level);
        return this;
    }

    public IncQueryEngine getIqEngine() {
        return incQueryEngine;
    }

    public ExecutionSchema getExecutionSchema() {
        return executionSchema;
    }

    public void useDebugInfo(boolean debug) {
        if (debug) {
            executionSchema.getLogger().setLevel(Level.DEBUG);
        }
    }
}
