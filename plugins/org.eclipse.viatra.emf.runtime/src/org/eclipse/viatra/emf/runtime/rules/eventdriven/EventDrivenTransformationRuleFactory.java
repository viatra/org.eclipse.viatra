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
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum;
import org.eclipse.incquery.runtime.evm.specific.lifecycle.DefaultActivationLifeCycle;
import org.eclipse.viatra.emf.runtime.filters.MatchParameterFilter;
import org.eclipse.viatra.emf.runtime.transformation.eventdriven.InconsistentEventSemanticsException;
import org.eclipse.xtext.xbase.lib.Pair;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class EventDrivenTransformationRuleFactory {

	public class EventDrivenTransformationBuilder<Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> {
		private String name = "";
		private IQuerySpecification<Matcher> precondition;
		private Multimap<IncQueryActivationStateEnum, IMatchProcessor<Match>> stateActions = HashMultimap.create();
		private ActivationLifeCycle lifeCycle = DefaultActivationLifeCycle.DEFAULT_NO_UPDATE_AND_DISAPPEAR;
		private EventFilter<? super Match> filter;

		public EventDrivenTransformationBuilder<Match, Matcher> name(String name) {
			this.name = name;
			return this;
		}

		public EventDrivenTransformationBuilder<Match, Matcher> precondition(
				IQuerySpecification<Matcher> precondition) {
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

		public EventDrivenTransformationBuilder<Match, Matcher> filter(Pair<String, Object>... parameters) {
			return this.filter(new MatchParameterFilter(parameters));
		}

		public EventDrivenTransformationBuilder<Match, Matcher> filter(EventFilter<? super Match> filter) {
			Preconditions.checkState(this.filter == null, "Filter was already set previously.");
			this.filter = filter;
			return this;
		}

		public EventDrivenTransformationRule<Match, Matcher> build() {
			Preconditions.checkState(!stateActions.isEmpty(), "The rule has no actions added.");
			return createRule(name, precondition, stateActions, lifeCycle, filter);
		}
	}

	public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> EventDrivenTransformationBuilder<Match, Matcher> createRule() {
		return new EventDrivenTransformationBuilder<Match, Matcher>();
	}
	
	public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> EventDrivenTransformationBuilder<Match, Matcher> createRule(String name) {
        return new EventDrivenTransformationBuilder<Match, Matcher>().name(name);
    }

	private <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> EventDrivenTransformationRule<Match, Matcher> createRule(
			String name, IQuerySpecification<Matcher> precondition,
			Multimap<IncQueryActivationStateEnum, IMatchProcessor<Match>> stateActions, ActivationLifeCycle lifeCycle,
			EventFilter<? super Match> filter) {
		return new EventDrivenTransformationRule<Match, Matcher>(name, precondition, stateActions, lifeCycle, filter);
	}

	/**
	 * Replaces a filter of an existing transformation rule, but keeps all other properties intact
	 * 
	 * @param rule
	 * @param filter
	 * @return
	 */
	public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> EventDrivenTransformationRule<Match, Matcher> filterRule(
			EventDrivenTransformationRule<Match, Matcher> rule, EventFilter<? super Match> filter) {
		return new EventDrivenTransformationRule<Match, Matcher>(rule, filter);
	}
}
