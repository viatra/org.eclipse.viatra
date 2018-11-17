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
package org.eclipse.viatra.transformation.runtime.emf.rules.eventdriven;

import java.util.AbstractMap.SimpleEntry;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.transformation.evm.api.ActivationLifeCycle;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.viatra.transformation.evm.specific.Lifecycles;
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDActivationStateEnum;
import org.eclipse.viatra.transformation.runtime.emf.filters.MatchParameterFilter;

public class EventDrivenTransformationRuleFactory {

    public class EventDrivenTransformationRuleBuilder<Match extends IPatternMatch, Matcher extends ViatraQueryMatcher<Match>> {
        private String name = "";
        private IQuerySpecification<Matcher> precondition;
        private Map<CRUDActivationStateEnum, Consumer<Match>> stateActions = new EnumMap<>(CRUDActivationStateEnum.class);
        private EventFilter<? super Match> filter;
        private boolean isUpdateJobAdded = false;
        private boolean isDeleteJobAdded = false;
        private ActivationLifeCycle lifeCycle = null;
        
        /**
         * @deprecated Use {@link #EventDrivenTransformationRuleFactory(IQuerySpecification)} instead
         */
        @Deprecated
        public EventDrivenTransformationRuleBuilder() {}
        
        /**
         * @since 2.1
         */
        public EventDrivenTransformationRuleBuilder(IQuerySpecification<Matcher> precondition) {
            this.precondition = precondition;
        }
        
        public EventDrivenTransformationRuleBuilder<Match, Matcher> name(String name) {
            this.name = name;
            return this;
        }

        /**
         * @deprecated Use {@link #EventDrivenTransformationRuleFactory(IQuerySpecification)} constructor instead
         */
        @Deprecated
        public EventDrivenTransformationRuleBuilder<Match, Matcher> precondition(
                IQuerySpecification<Matcher> precondition) {
            this.precondition = precondition;
            return this;
        }

        /**
         * @since 2.0
         */
        public EventDrivenTransformationRuleBuilder<Match, Matcher> action(Consumer<Match> action) {
            return action(CRUDActivationStateEnum.CREATED, action);
        }

        /**
         * @since 2.0
         */
        public EventDrivenTransformationRuleBuilder<Match, Matcher> action(CRUDActivationStateEnum state,
                Consumer<Match> action) {
            switch(state) {
            case CREATED:
                break;
            case UPDATED:
                isUpdateJobAdded = true;
                break;
            case DELETED:
                isDeleteJobAdded = true;
                break;
            default:
                throw new IllegalArgumentException("Unsupported activation state for action");
            }
            Preconditions.checkArgument(!stateActions.containsKey(state), "Duplicate action for state %s", state.toString());
            stateActions.put(state, action);
            return this;
        }

        public EventDrivenTransformationRuleBuilder<Match, Matcher> addLifeCycle(ActivationLifeCycle lifeCycle) {
            this.lifeCycle = lifeCycle;
            return this;
        }

        /**
         * @since 2.0
         */
        @SafeVarargs
        public final EventDrivenTransformationRuleBuilder<Match, Matcher> filter(Entry<String, Object>... parameters) {
            return this.filter(new MatchParameterFilter(parameters));
        }

        public EventDrivenTransformationRuleBuilder<Match, Matcher> filter(EventFilter<? super Match> filter) {
            Preconditions.checkState(this.filter == null, "Filter was already set previously.");
            this.filter = filter;
            return this;
        }

        public EventDrivenTransformationRule<Match, Matcher> build() {
            Preconditions.checkState(!stateActions.isEmpty(), "The rule has no actions added.");
            if (lifeCycle == null) {
                lifeCycle = Lifecycles.getDefault(isUpdateJobAdded, isDeleteJobAdded);
            }
            return createRule(name, precondition, stateActions, lifeCycle, filter);
        }
    }

    /**
     * @deprecated Use {@link #createRule(IQuerySpecification)} instead
     */
    @Deprecated
    public <Match extends IPatternMatch, Matcher extends ViatraQueryMatcher<Match>> EventDrivenTransformationRuleBuilder<Match, Matcher> createRule() {
        return new EventDrivenTransformationRuleBuilder<Match, Matcher>();
    }
    
    /**
     * @since 2.1
     */
    public <Match extends IPatternMatch, Matcher extends ViatraQueryMatcher<Match>> EventDrivenTransformationRuleBuilder<Match, Matcher> createRule(IQuerySpecification<Matcher> precondition) {
        return new EventDrivenTransformationRuleBuilder<Match, Matcher>(precondition);
    }
    
    private <Match extends IPatternMatch, Matcher extends ViatraQueryMatcher<Match>> EventDrivenTransformationRule<Match, Matcher> createRule(
            String name, IQuerySpecification<Matcher> precondition,
            Map<CRUDActivationStateEnum, Consumer<Match>> stateActions, ActivationLifeCycle lifeCycle,
            EventFilter<? super Match> filter) {
        return new EventDrivenTransformationRule<Match, Matcher>(name, precondition, stateActions, lifeCycle, filter);
    }

    /**
     * Replaces a filter of an existing transformation rule, but keeps all other properties intact
     */
    public <Match extends IPatternMatch, Matcher extends ViatraQueryMatcher<Match>> EventDrivenTransformationRule<Match, Matcher> filterRule(
            EventDrivenTransformationRule<Match, Matcher> rule, EventFilter<? super Match> filter) {
        return new EventDrivenTransformationRule<Match, Matcher>(rule, filter);
    }
    
    /**
     * @since 2.0
     */
    public static SimpleEntry<String, Object> operator_mappedTo(String key, Object value) {
        return new SimpleEntry<String, Object>(key, value);
    }
}
