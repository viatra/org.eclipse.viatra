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

import org.eclipse.viatra.query.runtime.api.IMatchProcessor;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.transformation.evm.api.ActivationLifeCycle;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.viatra.transformation.evm.specific.Lifecycles;
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDActivationStateEnum;
import org.eclipse.viatra.transformation.runtime.emf.filters.MatchParameterFilter;
import org.eclipse.viatra.transformation.runtime.emf.transformation.eventdriven.InconsistentEventSemanticsException;
import org.eclipse.xtext.xbase.lib.Pair;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class EventDrivenTransformationRuleFactory {

	public class EventDrivenTransformationRuleBuilder<Match extends IPatternMatch, Matcher extends ViatraQueryMatcher<Match>> {
		private String name = "";
		private IQuerySpecification<Matcher> precondition;
		private Multimap<CRUDActivationStateEnum, IMatchProcessor<Match>> stateActions = HashMultimap.create();
		private EventFilter<? super Match> filter;
		private boolean isUpdateJobAdded = false;
		private boolean isDeleteJobAdded = false;
		private ActivationLifeCycle lifeCycle = null;
		
		
		public EventDrivenTransformationRuleBuilder<Match, Matcher> name(String name) {
			this.name = name;
			return this;
		}

		public EventDrivenTransformationRuleBuilder<Match, Matcher> precondition(
				IQuerySpecification<Matcher> precondition) {
			this.precondition = precondition;
			return this;
		}

		public EventDrivenTransformationRuleBuilder<Match, Matcher> action(IMatchProcessor<Match> action) {
			return action(CRUDActivationStateEnum.CREATED, action);
		}

		public EventDrivenTransformationRuleBuilder<Match, Matcher> action(CRUDActivationStateEnum state,
				IMatchProcessor<Match> action) {
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
		    
			stateActions.put(state, action);
			return this;
		}

		public EventDrivenTransformationRuleBuilder<Match, Matcher> addLifeCycle(ActivationLifeCycle lifeCycle)
				throws InconsistentEventSemanticsException {
			this.lifeCycle = lifeCycle;
			return this;
		}

		public EventDrivenTransformationRuleBuilder<Match, Matcher> filter(Pair<String, Object>... parameters) {
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

	public <Match extends IPatternMatch, Matcher extends ViatraQueryMatcher<Match>> EventDrivenTransformationRuleBuilder<Match, Matcher> createRule() {
		return new EventDrivenTransformationRuleBuilder<Match, Matcher>();
	}
	
	private <Match extends IPatternMatch, Matcher extends ViatraQueryMatcher<Match>> EventDrivenTransformationRule<Match, Matcher> createRule(
			String name, IQuerySpecification<Matcher> precondition,
			Multimap<CRUDActivationStateEnum, IMatchProcessor<Match>> stateActions, ActivationLifeCycle lifeCycle,
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
}
