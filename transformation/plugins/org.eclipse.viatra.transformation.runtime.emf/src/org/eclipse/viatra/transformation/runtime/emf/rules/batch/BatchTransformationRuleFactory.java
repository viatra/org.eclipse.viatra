/*******************************************************************************
 * Copyright (c) 2004-2013, Zoltan Ujhelyi and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.runtime.emf.rules.batch;

import java.util.function.Consumer;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;

public class BatchTransformationRuleFactory {

    public class BatchTransformationRuleBuilder<Match extends IPatternMatch, Matcher extends ViatraQueryMatcher<Match>> {
        
        private IQuerySpecification<Matcher> fPrecondition;
        private Consumer<Match> fAction;
        private String fName = "";
        private EventFilter<? super Match> fFilter;
        
        /**
         * @deprecated Use {@link #BatchTransformationRuleFactory(IQuerySpecification)} instead
         */
        @Deprecated
        public BatchTransformationRuleBuilder() {}
        
        /**
         * @since 2.1
         */
        public BatchTransformationRuleBuilder(IQuerySpecification<Matcher> precondition) {
            fPrecondition = precondition;
        }
        
        /**
         * Sets the user-understandable name of the rule. Should be unique if set.
         */
        public BatchTransformationRuleBuilder<Match, Matcher> name(String name) {
            this.fName = name;
            return this;
        }

        /**
         * Sets the precondition query of the rule.
         * @deprecated Use {@link #BatchTransformationRuleFactory(IQuerySpecification)} instead
         */
        @Deprecated
        public BatchTransformationRuleBuilder<Match, Matcher> precondition(IQuerySpecification<Matcher> precondition) {
            this.fPrecondition = precondition;
            return this;
        }

        /**
         * Sets the model manipulation actions of the rule.
         * @since 2.0
         */
        public BatchTransformationRuleBuilder<Match, Matcher> action(Consumer<Match> action) {
            this.fAction = action;
            return this;
        }

        /**
         * Sets the event filter of the rule.
         * @since 1.3
         */
        public BatchTransformationRuleBuilder<Match, Matcher> filter(EventFilter<? super Match> filter) {
            this.fFilter = filter;
            return this;
        }

        public BatchTransformationRule<Match, Matcher> build() {
            if (fFilter == null) {
                return new BatchTransformationRule<>(fName, fPrecondition,
                        BatchTransformationRule.STATELESS_RULE_LIFECYCLE, fAction);
            } else {
                return new BatchTransformationRule<>(fName, fPrecondition,
                        BatchTransformationRule.STATELESS_RULE_LIFECYCLE, fAction, fFilter);
            }
        }
        
        public BatchTransformationRule<Match, Matcher> buildStateful() {
            if (fFilter == null) {
                return new BatchTransformationRule<>(fName, fPrecondition,
                        BatchTransformationRule.STATEFUL_RULE_LIFECYCLE, fAction);
            } else {
                return new BatchTransformationRule<>(fName, fPrecondition,
                        BatchTransformationRule.STATEFUL_RULE_LIFECYCLE, fAction, fFilter);
            }
        }
    }
    
    /**
     * @deprecated Use {@link #createRule(IQuerySpecification)} instead
     */
    @Deprecated
    public <Match extends IPatternMatch, Matcher extends ViatraQueryMatcher<Match>> BatchTransformationRuleBuilder<Match, Matcher> createRule() {
        return new BatchTransformationRuleBuilder<>();
    }

    /**
     * @since 2.1
     */
    public <Match extends IPatternMatch, Matcher extends ViatraQueryMatcher<Match>> BatchTransformationRuleBuilder<Match, Matcher> createRule(IQuerySpecification<Matcher> precondition) {
        return new BatchTransformationRuleBuilder<>(precondition);
    }
}
