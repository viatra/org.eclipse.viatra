/*******************************************************************************
 * Copyright (c) 2004-2013, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.runtime.emf.rules.batch;

import org.eclipse.viatra.query.runtime.api.IMatchProcessor;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;

public class BatchTransformationRuleFactory {

	public class BatchTransformationRuleBuilder<Match extends IPatternMatch, Matcher extends ViatraQueryMatcher<Match>> {
		
		private IQuerySpecification<Matcher> fPrecondition;
		private IMatchProcessor<Match> fAction;
		private String fName = "";
		private EventFilter<Match> fFilter;
		
		/**
		 * Sets the user-understandable name of the rule. Should be unique if set.
		 */
		public BatchTransformationRuleBuilder<Match, Matcher> name(String name) {
			this.fName = name;
			return this;
		}

		/**
		 * Sets the precondition query of the rule.
		 */
		public BatchTransformationRuleBuilder<Match, Matcher> precondition(IQuerySpecification<Matcher> precondition) {
			this.fPrecondition = precondition;
			return this;
		}

		/**
		 * Sets the model manipulation actions of the rule.
		 */
		public BatchTransformationRuleBuilder<Match, Matcher> action(IMatchProcessor<Match> action) {
			this.fAction = action;
			return this;
		}

		/**
		 * Sets the event filter of the rule.
		 */
		public BatchTransformationRuleBuilder<Match, Matcher> filter(EventFilter<Match> filter) {
		    this.fFilter = filter;
		    return this;
		}

		public BatchTransformationRule<Match, Matcher> build() {
		    if (fFilter == null) {
		        return new BatchTransformationRule<Match, Matcher>(fName, fPrecondition,
		                BatchTransformationRule.STATELESS_RULE_LIFECYCLE, fAction);
            } else {
    		    return new BatchTransformationRule<Match, Matcher>(fName, fPrecondition,
    	                BatchTransformationRule.STATELESS_RULE_LIFECYCLE, fAction, fFilter);
            }
		}
		
		public BatchTransformationRule<Match, Matcher> buildStateful() {
		    if (fFilter == null) {
		        return new BatchTransformationRule<Match, Matcher>(fName, fPrecondition,
		                BatchTransformationRule.STATEFUL_RULE_LIFECYCLE, fAction);
		    } else {
		        return new BatchTransformationRule<Match, Matcher>(fName, fPrecondition,
		                BatchTransformationRule.STATEFUL_RULE_LIFECYCLE, fAction, fFilter);
		    }
		}
	}
	
	public <Match extends IPatternMatch, Matcher extends ViatraQueryMatcher<Match>> BatchTransformationRuleBuilder<Match, Matcher> createRule() {
		return new BatchTransformationRuleBuilder<Match, Matcher>();
	}

}
