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
package org.eclipse.viatra.emf.runtime.rules.batch

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
import org.eclipse.incquery.runtime.evm.specific.lifecycle.DefaultActivationLifeCycle
import org.eclipse.incquery.runtime.evm.specific.lifecycle.UnmodifiableActivationLifeCycle
import org.eclipse.viatra.emf.runtime.rules.ITransformationRule

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
	public static val ActivationLifeCycle STATELESS_RULE_LIFECYCLE = {
		val cycle= ActivationLifeCycle.create(IncQueryActivationStateEnum::INACTIVE)
		
		cycle.addStateTransition(IncQueryActivationStateEnum::INACTIVE, IncQueryEventTypeEnum::MATCH_APPEARS, IncQueryActivationStateEnum::APPEARED)
		cycle.addStateTransition(IncQueryActivationStateEnum::APPEARED, RuleEngineEventType::FIRE, IncQueryActivationStateEnum::APPEARED)
		cycle.addStateTransition(IncQueryActivationStateEnum::APPEARED, IncQueryEventTypeEnum::MATCH_DISAPPEARS, IncQueryActivationStateEnum::INACTIVE)
		
		UnmodifiableActivationLifeCycle::copyOf(cycle)
	}
	/**
	 * Lifecycle for a rule that stores the list of fired activations; thus effectively forbids re-firing the same activation.
	 */
	public static val STATEFUL_RULE_LIFECYCLE = DefaultActivationLifeCycle::DEFAULT_NO_UPDATE_AND_DISAPPEAR

	protected String ruleName
	private val ActivationLifeCycle lifecycle
	RuleSpecification<Match> ruleSpec
	private val IQuerySpecification<Matcher> precondition
	private val IMatchProcessor<Match> action

	protected new() {
		this("", null, STATELESS_RULE_LIFECYCLE, null)
		
	}
	
	new(String rulename, IQuerySpecification<Matcher> matcher, ActivationLifeCycle lifecycle, IMatchProcessor<Match> action) {
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
		    val Job<Match> job = Jobs::newStatelessJob(IncQueryActivationStateEnum::APPEARED, action)
		    
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
	def IMatchProcessor<Match> getModelManipulation() {	
		action
	}	
}