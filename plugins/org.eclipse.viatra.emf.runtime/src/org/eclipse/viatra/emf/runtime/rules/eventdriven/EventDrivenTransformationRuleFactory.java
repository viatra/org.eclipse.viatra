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
package org.eclipse.viatra.emf.runtime.rules.eventdriven;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.evm.api.ActivationLifeCycle;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum;
import org.eclipse.incquery.runtime.evm.specific.lifecycle.DefaultActivationLifeCycle;
import org.eclipse.viatra.emf.runtime.transformation.eventdriven.EventDrivenTransformationRule;
import org.eclipse.viatra.emf.runtime.transformation.eventdriven.InconsistentEventSemanticsException;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class EventDrivenTransformationRuleFactory {
    
    public class EventDrivenTransformationBuilder<Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> {
        private String name = "";
        private IQuerySpecification<Matcher> precondition;
        private Multimap<IncQueryActivationStateEnum, IMatchProcessor<Match>> stateActions = HashMultimap.create();
        private ActivationLifeCycle lifeCycle = DefaultActivationLifeCycle.DEFAULT_NO_UPDATE_AND_DISAPPEAR;

        public EventDrivenTransformationBuilder<Match, Matcher> name(String name) {
            this.name = name;
            return this;
        }

        public EventDrivenTransformationBuilder<Match, Matcher> precondition(IQuerySpecification<Matcher> precondition) {
            this.precondition = precondition;
            return this;
        }

        public EventDrivenTransformationBuilder<Match, Matcher> action(IMatchProcessor<Match> action) {
            return action(IncQueryActivationStateEnum.APPEARED, action);
        }

        public EventDrivenTransformationBuilder<Match, Matcher> action(IncQueryActivationStateEnum state,
                IMatchProcessor<Match> action) {
            stateActions.put(state, action);
            return this;
        }

        public EventDrivenTransformationBuilder<Match, Matcher> addLifeCycle(ActivationLifeCycle lifeCycle)
                throws InconsistentEventSemanticsException {
            this.lifeCycle = lifeCycle;
            return this;
        }

        public EventDrivenTransformationRule<Match, Matcher> build() {
            if (stateActions.isEmpty()) {
                return null;
            }
            return createRule(name, precondition, stateActions, lifeCycle);
        }
    }

    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> EventDrivenTransformationBuilder<Match, Matcher> createRule() {
        return new EventDrivenTransformationBuilder<Match, Matcher>();
    }

    private <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> EventDrivenTransformationRule<Match, Matcher> createRule(
            String name, IQuerySpecification<Matcher> precondition,
            Multimap<IncQueryActivationStateEnum, IMatchProcessor<Match>> stateActions, ActivationLifeCycle lifeCycle) {
        return new EventDrivenTransformationRule<Match, Matcher>(name, precondition, stateActions, lifeCycle);
    }
}
