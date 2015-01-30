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
package org.eclipse.viatra.emf.runtime.rules.batch;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra.emf.runtime.rules.batch.BatchTransformationRule;

public class BatchTransformationRuleFactory {

	public class BatchTransformationRuleBuilder<Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> {
		
		private IQuerySpecification<Matcher> fPrecondition;
		private IMatchProcessor<Match> fAction;
		private String fName = "";
		
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


		public BatchTransformationRule<Match, Matcher> build() {
			return createRule(fName, fPrecondition, fAction);
		}
		
		public BatchTransformationRule<Match, Matcher> buildStateful() {
			return createStatefulRule(fName, fPrecondition, fAction);
		}
	}
	
	public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> BatchTransformationRuleBuilder<Match, Matcher> createRule() {
		return new BatchTransformationRuleBuilder<Match, Matcher>();
	}
	
	public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> BatchTransformationRule<Match, Matcher> createRule(
			IQuerySpecification<Matcher> precondition,
			IMatchProcessor<Match> action) {
		return createRule("", precondition, action);
	}

	public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> BatchTransformationRule<Match, Matcher> createRule(
			String name, IQuerySpecification<Matcher> precondition,
			IMatchProcessor<Match> action) {
		return new BatchTransformationRule<Match, Matcher>(name, precondition,
				BatchTransformationRule.STATELESS_RULE_LIFECYCLE, action);
	}

	public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> BatchTransformationRule<Match, Matcher> createStatefulRule(
			IQuerySpecification<Matcher> precondition,
			IMatchProcessor<Match> action) {
		return createStatefulRule("", precondition, action);
	}

	public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> BatchTransformationRule<Match, Matcher> createStatefulRule(
			String name, IQuerySpecification<Matcher> precondition,
			IMatchProcessor<Match> action) {
		return new BatchTransformationRule<Match, Matcher>(name, precondition,
				BatchTransformationRule.STATEFUL_RULE_LIFECYCLE, action);
	}
}
