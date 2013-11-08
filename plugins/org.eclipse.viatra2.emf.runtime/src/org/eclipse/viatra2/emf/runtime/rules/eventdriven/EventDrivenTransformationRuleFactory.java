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
package org.eclipse.viatra2.emf.runtime.rules.eventdriven;

import static org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum.APPEARED;
import static org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum.DISAPPEARED;
import static org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum.INACTIVE;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.evm.api.ActivationLifeCycle;
import org.eclipse.incquery.runtime.evm.api.event.EventType.RuleEngineEventType;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryEventTypeEnum;
import org.eclipse.incquery.runtime.evm.specific.lifecycle.DefaultActivationLifeCycle;
import org.eclipse.viatra2.emf.runtime.transformation.eventdriven.EventDrivenTransformationRule;
import org.eclipse.viatra2.emf.runtime.transformation.eventdriven.InconsistentEventSemanticsException;

public final class EventDrivenTransformationRuleFactory {

    private enum RuleSpecificationType {
        POINT_SEMANTICS_ACTION, INTERVAL_SEMANTICS_ACTIONS, USER_SPECIFIED;

        public static ActivationLifeCycle getLifeCycle(RuleSpecificationType ruleSpecificationType) {
            switch (ruleSpecificationType) {
            case POINT_SEMANTICS_ACTION:
                return DefaultActivationLifeCycle.DEFAULT_NO_UPDATE_AND_DISAPPEAR;
            case INTERVAL_SEMANTICS_ACTIONS:
                ActivationLifeCycle activationLifeCycle = ActivationLifeCycle.create(INACTIVE);
                activationLifeCycle.addStateTransition(INACTIVE, IncQueryEventTypeEnum.MATCH_APPEARS, APPEARED);
                activationLifeCycle.addStateTransition(APPEARED, RuleEngineEventType.FIRE, APPEARED);
                activationLifeCycle.addStateTransition(APPEARED, IncQueryEventTypeEnum.MATCH_DISAPPEARS, DISAPPEARED);
                return activationLifeCycle;
            case USER_SPECIFIED:
                return ActivationLifeCycle.create(INACTIVE); // dummy
            default:
                throw new IllegalArgumentException("Unsupported RuleSpecificationType.");
            }
        }
    }

    public class EventDrivenTransformationBuilder<Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> {
        private String name = "";
        private IQuerySpecification<Matcher> precondition;

        private RuleSpecificationType ruleSpecificationType;
        // POINT_SEMANTICS_ACTION
        private IMatchProcessor<Match> action;
        // INTERVAL_SEMANTICS_ACTIONS
        private IMatchProcessor<Match> actionOnAppear;
        private IMatchProcessor<Match> actionOnDisappear;
        // USER_SPECIFIED
        private IncQueryActivationStateEnum transition;
        private IMatchProcessor<Match> actionOnTransition;
        private ActivationLifeCycle userSpecifiedLifeCycle;

        public EventDrivenTransformationBuilder<Match, Matcher> name(String name) {
            this.name = name;
            return this;
        }

        public EventDrivenTransformationBuilder<Match, Matcher> precondition(IQuerySpecification<Matcher> precondition) {
            this.precondition = precondition;
            return this;
        }

        private boolean isActionConsistentWithPreviousContext(RuleSpecificationType ruleSpecificationType) {
            switch (ruleSpecificationType) {
            case POINT_SEMANTICS_ACTION:
                if (this.ruleSpecificationType == null) {
                    this.ruleSpecificationType = RuleSpecificationType.POINT_SEMANTICS_ACTION;
                    return true;
                }
            case INTERVAL_SEMANTICS_ACTIONS:
                if (this.ruleSpecificationType == null) {
                    this.ruleSpecificationType = RuleSpecificationType.INTERVAL_SEMANTICS_ACTIONS;
                    return true;
                }
            case USER_SPECIFIED:
                if (this.ruleSpecificationType == null) {
                    this.ruleSpecificationType = RuleSpecificationType.USER_SPECIFIED;
                    return true;
                }
            default:
                if (this.ruleSpecificationType.equals(ruleSpecificationType)) {
                    return true;
                }
                return false;
            }
        }

        public EventDrivenTransformationBuilder<Match, Matcher> action(IMatchProcessor<Match> action)
                throws InconsistentEventSemanticsException {
            RuleSpecificationType ruleSpectype = RuleSpecificationType.POINT_SEMANTICS_ACTION;
            if (!isActionConsistentWithPreviousContext(ruleSpectype)) {
                throw new InconsistentEventSemanticsException(ruleSpecificationType.toString(), ruleSpectype.toString());
            }
            this.action = action;
            return this;
        }

        public EventDrivenTransformationBuilder<Match, Matcher> onAppear(IMatchProcessor<Match> action)
                throws InconsistentEventSemanticsException {
            RuleSpecificationType ruleSpectype = RuleSpecificationType.INTERVAL_SEMANTICS_ACTIONS;
            if (!isActionConsistentWithPreviousContext(ruleSpectype)) {
                throw new InconsistentEventSemanticsException(ruleSpecificationType.toString(), ruleSpectype.toString());
            }
            this.actionOnAppear = action;
            return this;
        }

        public EventDrivenTransformationBuilder<Match, Matcher> onDisappear(IMatchProcessor<Match> action)
                throws InconsistentEventSemanticsException {
            RuleSpecificationType ruleSpectype = RuleSpecificationType.INTERVAL_SEMANTICS_ACTIONS;
            if (!isActionConsistentWithPreviousContext(ruleSpectype)) {
                throw new InconsistentEventSemanticsException(ruleSpecificationType.toString(), ruleSpectype.toString());
            }
            this.actionOnDisappear = action;
            return this;
        }

        public EventDrivenTransformationBuilder<Match, Matcher> addTransition(IncQueryActivationStateEnum transition,
                IMatchProcessor<Match> actionOnTransition) throws InconsistentEventSemanticsException {
            RuleSpecificationType ruleSpectype = RuleSpecificationType.USER_SPECIFIED;
            if (!isActionConsistentWithPreviousContext(ruleSpectype)) {
                throw new InconsistentEventSemanticsException(ruleSpecificationType.toString(), ruleSpectype.toString());
            }
            this.transition = transition;
            this.actionOnTransition = actionOnTransition;
            return this;
        }

        public EventDrivenTransformationBuilder<Match, Matcher> addLifeCycle(ActivationLifeCycle lifeCycle)
                throws InconsistentEventSemanticsException {
            if (!isActionConsistentWithPreviousContext(RuleSpecificationType.USER_SPECIFIED)) {
                throw new InconsistentEventSemanticsException();
            }
            this.userSpecifiedLifeCycle = lifeCycle;
            return this;
        }

        public EventDrivenTransformationRule<Match, Matcher> build() {
            switch (ruleSpecificationType) {
            case POINT_SEMANTICS_ACTION:
                return createRule(name, precondition, action, RuleSpecificationType.getLifeCycle(ruleSpecificationType));
            case INTERVAL_SEMANTICS_ACTIONS:
                return createRule(name, precondition, actionOnAppear, actionOnDisappear,
                        RuleSpecificationType.getLifeCycle(ruleSpecificationType));
            case USER_SPECIFIED:
                return createRule(name, precondition, transition, actionOnTransition, userSpecifiedLifeCycle);
            default:
                return null;
            }
        }
    }

    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> EventDrivenTransformationBuilder<Match, Matcher> createRule() {
        return new EventDrivenTransformationBuilder<Match, Matcher>();
    }

    private <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> EventDrivenTransformationRule<Match, Matcher> createRule(
            String name, IQuerySpecification<Matcher> precondition, IMatchProcessor<Match> action,
            ActivationLifeCycle lifeCycle) {
        return new EventDrivenTransformationRule<Match, Matcher>(name, precondition, action, lifeCycle);
    }

    private <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> EventDrivenTransformationRule<Match, Matcher> createRule(
            String name, IQuerySpecification<Matcher> precondition, IMatchProcessor<Match> actionOnAppear,
            IMatchProcessor<Match> actionOnDisappear, ActivationLifeCycle lifeCycle) {
        return new EventDrivenTransformationRule<Match, Matcher>(name, precondition, actionOnAppear, actionOnDisappear,
                lifeCycle);
    }

    private <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> EventDrivenTransformationRule<Match, Matcher> createRule(
            String name, IQuerySpecification<Matcher> precondition, IncQueryActivationStateEnum transition,
            IMatchProcessor<Match> action, ActivationLifeCycle lifeCycle) {
        return new EventDrivenTransformationRule<Match, Matcher>(name, precondition, action, transition, lifeCycle);
    }
}
