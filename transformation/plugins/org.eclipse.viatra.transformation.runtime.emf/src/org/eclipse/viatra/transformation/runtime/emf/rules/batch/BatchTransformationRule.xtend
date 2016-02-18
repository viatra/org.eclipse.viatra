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
package org.eclipse.viatra.transformation.runtime.emf.rules.batch

import org.eclipse.viatra.query.runtime.api.IMatchProcessor
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.transformation.evm.api.ActivationLifeCycle
import org.eclipse.viatra.transformation.evm.api.Job
import org.eclipse.viatra.transformation.evm.api.RuleSpecification
import org.eclipse.viatra.transformation.evm.api.event.EventFilter
import org.eclipse.viatra.transformation.evm.api.event.EventType.RuleEngineEventType
import org.eclipse.viatra.transformation.evm.specific.Jobs
import org.eclipse.viatra.transformation.evm.specific.Rules
import org.eclipse.viatra.transformation.evm.specific.event.ViatraQueryMatchEventFilter
import org.eclipse.viatra.transformation.evm.specific.lifecycle.UnmodifiableActivationLifeCycle
import org.eclipse.viatra.transformation.runtime.emf.rules.ITransformationRule
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDActivationStateEnum
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDEventTypeEnum
import org.eclipse.viatra.transformation.evm.specific.Lifecycles

/**
 * Wrapper class for transformation rule definition to hide EVM specific internals.
 * 
 * Subclasses can simply override the abstract precondition and model manipulation methods.
 *
 * @author Abel Hegedus, Zoltan Ujhelyi
 *
 */
class BatchTransformationRule<Match extends IPatternMatch,Matcher extends ViatraQueryMatcher<Match>> implements ITransformationRule<Match,Matcher> {
	
	/**
	 * Lifecycle for a rule that does not store the list of fired activations; thus allows re-firing the same activation again. 
	 */
	public static val ActivationLifeCycle STATELESS_RULE_LIFECYCLE = {
		val cycle= ActivationLifeCycle.create(CRUDActivationStateEnum::INACTIVE)
		
		cycle.addStateTransition(CRUDActivationStateEnum::INACTIVE, CRUDEventTypeEnum::CREATED, CRUDActivationStateEnum::CREATED)
		cycle.addStateTransition(CRUDActivationStateEnum::CREATED, RuleEngineEventType::FIRE, CRUDActivationStateEnum::CREATED)
		cycle.addStateTransition(CRUDActivationStateEnum::CREATED, CRUDEventTypeEnum::DELETED, CRUDActivationStateEnum::INACTIVE)
		
		UnmodifiableActivationLifeCycle::copyOf(cycle)
	}
	/**
	 * Lifecycle for a rule that stores the list of fired activations; thus effectively forbids re-firing the same activation.
	 */
	public static val STATEFUL_RULE_LIFECYCLE = Lifecycles::getDefault(false, false)

	protected String ruleName
	private val ActivationLifeCycle lifecycle
	RuleSpecification<Match> ruleSpec
	private val IQuerySpecification<Matcher> precondition
	private val IMatchProcessor<Match> action
	private val EventFilter<Match> filter

	protected new() {
		this("", null, STATELESS_RULE_LIFECYCLE, null)
		
	}
	
	new(String rulename, IQuerySpecification<Matcher> matcher, ActivationLifeCycle lifecycle, IMatchProcessor<Match> action) {
	    this(rulename, matcher, lifecycle, action, ViatraQueryMatchEventFilter.createFilter(matcher.newEmptyMatch.toImmutable as Match))
	}
	new(String rulename, IQuerySpecification<Matcher> matcher, ActivationLifeCycle lifecycle, IMatchProcessor<Match> action, EventFilter<Match> filter) {
		this.ruleName = rulename
		this.precondition = matcher
		this.action = action
		this.lifecycle = lifecycle
		this.filter = filter
	}
	new(BatchTransformationRule<Match, Matcher> rule, EventFilter<Match> filter) {
	    this.ruleName = rule.ruleName
	    this.precondition = rule.precondition
	    this.action = rule.action
	    this.lifecycle = rule.lifecycle
	    this.filter = filter
	}
	
	/**
	 * @deprecated Use {#getName) instead
	 */
	@Deprecated()
    def getRuleName() {
    	getName()
    }

    override getName() {
        ruleName
    }

	/**
	 * Returns a RuleSpecification that can be added to a rule engine.
	 */
    override getRuleSpecification(){
    	if(ruleSpec == null){
		    val querySpec = precondition
		    val Job<Match> job = Jobs::newStatelessJob(CRUDActivationStateEnum::CREATED, action)
		    
		    ruleSpec = Rules::newMatcherRuleSpecification(querySpec, lifecycle, newHashSet(job))
    	}
    	ruleSpec
    }
	
	/**
	 * Returns the query specification representing the pattern used as a precondition.
	 */
	override IQuerySpecification<Matcher> getPrecondition() {
		precondition
	}
	
	/**
	 * Return an IMatchProcessor representing the model manipulation executed by the rule.
	 */
	def IMatchProcessor<Match> getAction() {	
		action
	}	
	
	override EventFilter<? super Match> getFilter() {
	    filter
	}
}