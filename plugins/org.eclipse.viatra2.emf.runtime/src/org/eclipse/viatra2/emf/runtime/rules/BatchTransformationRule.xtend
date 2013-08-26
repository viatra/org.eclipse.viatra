/*******************************************************************************
 * Copyright (c) 2004-2013, Abel Hegedus and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.runtime.rules

import org.eclipse.incquery.runtime.api.IMatchProcessor
import org.eclipse.incquery.runtime.api.IPatternMatch
import org.eclipse.incquery.runtime.api.IQuerySpecification
import org.eclipse.incquery.runtime.api.IncQueryMatcher
import org.eclipse.incquery.runtime.evm.api.Activation
import org.eclipse.incquery.runtime.evm.api.Context
import org.eclipse.incquery.runtime.evm.api.Job
import org.eclipse.incquery.runtime.evm.api.RuleEngine
import org.eclipse.incquery.runtime.evm.api.event.EventFilter
import org.eclipse.incquery.runtime.evm.specific.Jobs
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum
import org.eclipse.incquery.runtime.evm.specific.lifecycle.DefaultActivationLifeCycle
import org.eclipse.incquery.runtime.evm.api.RuleSpecification
import org.eclipse.incquery.runtime.evm.specific.Rules
import org.eclipse.viatra2.emf.runtime.transformation.BatchTransformation

/**
 * Wrapper class for transformation rule definition to hide EVM specific internals.
 * 
 * Subclasses can simply override the abstract precondition and model manipulation methods.
 *
 * @author Abel Hegedus
 *
 */
abstract class BatchTransformationRule<Match extends IPatternMatch,Matcher extends IncQueryMatcher<Match>> {
	
	protected String ruleName
	RuleSpecification<Match> ruleSpec

    def getRuleName() {
    	ruleName
    }

	/**
	 * Returns a RuleSpecification that can be added to a rule engine.
	 * TODO move this to {@link BatchTransformation}
	 */
    def getRuleSpec(){
    	if(ruleSpec == null){
		    val querySpec = precondition
		    ruleSpec = Rules::newMatcherRuleSpecification(querySpec, getLifeCycle, getJobs)
    	}
    	ruleSpec
    }
	
	def protected getLifeCycle() {
		DefaultActivationLifeCycle::DEFAULT_NO_UPDATE_AND_DISAPPEAR
	}
	
	def protected getJobs() {
		val proc = getModelManipulation
		val Job<Match> stJob = Jobs::newStatelessJob(IncQueryActivationStateEnum::APPEARED, proc)
		val Job<Match> job = Jobs::newRecordingJob(stJob)
		return <Job<Match>>newHashSet(job)
	}
	
	/**
	 * Returns the IMatcherFactory representing the pattern used as a precondition.
	 */
	def IQuerySpecification<Matcher> getPrecondition()
	
	/**
	 * Return an IMatchProcessor representing the model manipulation executed by the rule.
	 */
	def IMatchProcessor<Match> getModelManipulation()
	
	private def firstActivation(RuleEngine engine) {
		engine.getActivations(ruleSpec, IncQueryActivationStateEnum::APPEARED).head
	}
	
	private def firstActivation(RuleEngine engine, EventFilter<Match> filter) {
		engine.getActivations(ruleSpec, filter, IncQueryActivationStateEnum::APPEARED).head
	}
	
	private def fireActivation(Activation<Match> act, Context context) {
		if(act != null && act.enabled){
 			act.fire(context)
		}
	}
	
	/**
	 * Goes through each possibly activation of the transformation rule and executes them.
	 * These activations are each in Appeared state.
	 */
	def fireEachActivation(RuleEngine ruleEngine, Context context){
		println('''== Executing activations of «ruleName» ==''')
		
 		var Activation<Match> act
 		while((act = ruleEngine.firstActivation) != null){
 			act.fireActivation(context)
 		}
	}
	
	/**
	 * Goes through each possibly activation of the transformation rule that fulfill a filter and execute them.
	 * These activations are each in Appeared state.
	 */
	def fireEachActivation(RuleEngine ruleEngine, Context context, EventFilter<Match> filter) {
		println('''== Executing activations of «ruleName» with filter «filter» ==''')
		var Activation<Match> act
		while((act = ruleEngine.firstActivation(filter)) != null){
 			act.fireActivation(context)
 		}
	}
	
	/**
	 * Selects one activation of the rule and executes it. 
	 */
	def fireOneActivation(RuleEngine ruleEngine, Context context){
		println('''== Executing one activation of «ruleName» ==''')
		
 		var Activation<Match> act = ruleEngine.firstActivation
 		act.fireActivation(context)
		
	}
	
	/**
	 * Selects one activation of the rule that fulfills a filtering match, and executes it. 
	 */
	def fireOneActivation(RuleEngine ruleEngine, Context context, EventFilter<Match> filter){
		println('''== Executing one activation of «ruleName» with filter «filter»==''')
		
 		var Activation<Match> act = ruleEngine.firstActivation(filter)
 		act.fireActivation(context)
		
	}

	
}