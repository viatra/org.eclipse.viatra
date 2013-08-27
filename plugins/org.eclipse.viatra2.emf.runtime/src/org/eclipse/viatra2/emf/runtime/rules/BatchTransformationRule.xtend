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
import org.eclipse.incquery.runtime.evm.api.Job
import org.eclipse.incquery.runtime.evm.api.RuleSpecification
import org.eclipse.incquery.runtime.evm.specific.Jobs
import org.eclipse.incquery.runtime.evm.specific.Rules
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum
import org.eclipse.incquery.runtime.evm.specific.lifecycle.DefaultActivationLifeCycle

/**
 * Wrapper class for transformation rule definition to hide EVM specific internals.
 * 
 * Subclasses can simply override the abstract precondition and model manipulation methods.
 *
 * @author Abel Hegedus, Zoltan Ujhelyi
 *
 */
class BatchTransformationRule<Match extends IPatternMatch,Matcher extends IncQueryMatcher<Match>> {
	
	protected String ruleName
	RuleSpecification<Match> ruleSpec
	private val IQuerySpecification<Matcher> precondition
	private val IMatchProcessor<Match> action

	new() {
		precondition = null
		action = null
		ruleName = ""
	}
	
	new(IQuerySpecification<Matcher> precondition, IMatchProcessor<Match> action) {
		this("", precondition, action)
	}

	new(String rulename, IQuerySpecification<Matcher> matcher, IMatchProcessor<Match> action) {
		this.precondition = matcher
		this.action = action
	}
	
    def getRuleName() {
    	ruleName
    }

	/**
	 * Returns a RuleSpecification that can be added to a rule engine.
	 */
    def getRuleSpec(){
    	if(ruleSpec == null){
		    val querySpec = precondition
		    val Job<Match> stJob = Jobs::newStatelessJob(IncQueryActivationStateEnum::APPEARED, action)
			val Job<Match> job = Jobs::newRecordingJob(stJob)
		    
		    ruleSpec = Rules::newMatcherRuleSpecification(querySpec, DefaultActivationLifeCycle::DEFAULT_NO_UPDATE_AND_DISAPPEAR, newHashSet(job))
    	}
    	ruleSpec
    }
	
	/**
	 * Returns the IMatcherFactory representing the pattern used as a precondition.
	 */
	def IQuerySpecification<Matcher> getPrecondition() {
		precondition
	}
	
	/**
	 * Return an IMatchProcessor representing the model manipulation executed by the rule.
	 */
	def IMatchProcessor<Match> getModelManipulation() {	
		action
	}	
}