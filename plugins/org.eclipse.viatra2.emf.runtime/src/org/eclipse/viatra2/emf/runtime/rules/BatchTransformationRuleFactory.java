package org.eclipse.viatra2.emf.runtime.rules;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;

public class BatchTransformationRuleFactory {

	public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> BatchTransformationRule<Match, Matcher> createRule(
			IQuerySpecification<Matcher> precondition, IMatchProcessor<Match> action) {
			return createRule("", precondition, action);
		}

		public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> BatchTransformationRule<Match, Matcher> createRule(String name,
			IQuerySpecification<Matcher> precondition, IMatchProcessor<Match> action) {
			return new BatchTransformationRule<Match, Matcher>(name, precondition, BatchTransformationRule.STATELESS_RULE_LIFECYCLE, action);
		}
		
		public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> BatchTransformationRule<Match, Matcher> createStatefulRule(
			IQuerySpecification<Matcher> precondition, IMatchProcessor<Match> action) {
			return createStatefulRule("", precondition, action);
		}

		public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> BatchTransformationRule<Match, Matcher> createStatefulRule(String name,
			IQuerySpecification<Matcher> precondition, IMatchProcessor<Match> action) {
			return new BatchTransformationRule<Match, Matcher>(name, precondition, BatchTransformationRule.STATEFUL_RULE_LIFECYCLE, action);
		}
}
