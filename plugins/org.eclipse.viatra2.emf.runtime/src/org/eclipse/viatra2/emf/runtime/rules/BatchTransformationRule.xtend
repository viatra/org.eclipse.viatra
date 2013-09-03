/*******************************************************************************
 * Copyright (c) 2004-2013, Abel Hegedus, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.runtime.rules

import org.eclipse.incquery.runtime.api.IMatchProcessor
import org.eclipse.incquery.runtime.api.IPatternMatch
import org.eclipse.incquery.runtime.api.IQuerySpecification
import org.eclipse.incquery.runtime.api.IncQueryMatcher
import org.eclipse.incquery.runtime.evm.api.ActivationLifeCycle
import org.eclipse.incquery.runtime.evm.api.Job
import org.eclipse.incquery.runtime.evm.api.RuleSpecification
import org.eclipse.incquery.runtime.evm.api.event.EventType.RuleEngineEventType
import org.eclipse.incquery.runtime.evm.specific.Jobs
import org.eclipse.incquery.runtime.evm.specific.Rules
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryEventTypeEnum
import org.eclipse.incquery.runtime.evm.specific.lifecycle.UnmodifiableActivationLifeCycle
import org.eclipse.incquery.runtime.evm.specific.lifecycle.DefaultActivationLifeCycle

/**
 * Wrapper class for transformation rule definition to hide EVM specific internals.
 * 
 * Subclasses can simply override the abstract precondition and model manipulation methods.
 *
 * @author Abel Hegedus, Zoltan Ujhelyi
 *
 */
class BatchTransformationRule<Match extends IPatternMatch,Matcher extends IncQueryMatcher<Match>> implements ITransformationRule<Match,Matcher> {
	
	/**
	 * Lifecycle for a rule that does not store the list of fired activations; thus allows re-firing the same activation again. 
	 */
	static val ActivationLifeCycle STATELESS_RULE_LIFECYCLE = {
		val cycle= ActivationLifeCycle.create(IncQueryActivationStateEnum::INACTIVE)
		
		cycle.addStateTransition(IncQueryActivationStateEnum::INACTIVE, IncQueryEventTypeEnum::MATCH_APPEARS, IncQueryActivationStateEnum::APPEARED)
		cycle.addStateTransition(IncQueryActivationStateEnum::APPEARED, RuleEngineEventType::FIRE, IncQueryActivationStateEnum::APPEARED)
		cycle.addStateTransition(IncQueryActivationStateEnum::APPEARED, IncQueryEventTypeEnum::MATCH_DISAPPEARS, IncQueryActivationStateEnum::INACTIVE)
		
		UnmodifiableActivationLifeCycle::copyOf(cycle)
	}
	/**
	 * Lifecycle for a rule that stores the list of fired activations; thus effectively forbids re-firing the same activation.
	 */
	static val STATEFUL_RULE_LIFECYCLE = DefaultActivationLifeCycle::DEFAULT_NO_UPDATE_AND_DISAPPEAR

	protected String ruleName
	private val ActivationLifeCycle lifecycle
	RuleSpecification<Match> ruleSpec
	private val IQuerySpecification<Matcher> precondition
	private val IMatchProcessor<Match> action

	def static <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> createRule(
		IQuerySpecification<Matcher> precondition, IMatchProcessor<Match> action) {
		createRule("", precondition, action)
	}

	def static <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> createRule(String name,
		IQuerySpecification<Matcher> precondition, IMatchProcessor<Match> action) {
		new BatchTransformationRule(name, precondition, STATELESS_RULE_LIFECYCLE, action)
	}
	
	def static <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> createStatefulRule(
		IQuerySpecification<Matcher> precondition, IMatchProcessor<Match> action) {
		createStatefulRule("", precondition, action)
	}

	def static <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> createStatefulRule(String name,
		IQuerySpecification<Matcher> precondition, IMatchProcessor<Match> action) {
		new BatchTransformationRule(name, precondition, STATEFUL_RULE_LIFECYCLE, action)
	}

	protected new() {
		this("", null, STATELESS_RULE_LIFECYCLE, null)
		
	}
	
	protected new(String rulename, IQuerySpecification<Matcher> matcher, ActivationLifeCycle lifecycle, IMatchProcessor<Match> action) {
		this.precondition = matcher
		this.action = action
		this.lifecycle = lifecycle
	}
	
    def getRuleName() {
    	ruleName
    }

	/**
	 * Returns a RuleSpecification that can be added to a rule engine.
	 */
    override getRuleSpecification(){
    	if(ruleSpec == null){
		    val querySpec = precondition
		    val Job<Match> stJob = Jobs::newStatelessJob(IncQueryActivationStateEnum::APPEARED, action)
			val Job<Match> job = Jobs::newRecordingJob(stJob)
		    
		    ruleSpec = Rules::newMatcherRuleSpecification(querySpec, lifecycle, newHashSet(job))
    	}
    	ruleSpec
    }
	
	/**
	 * Returns the IMatcherFactory representing the pattern used as a precondition.
	 */
	override IQuerySpecification<Matcher> getPrecondition() {
		precondition
	}
	
	/**
	 * Return an IMatchProcessor representing the model manipulation executed by the rule.
	 */
	override IMatchProcessor<Match> getModelManipulation() {	
		action
	}	
}