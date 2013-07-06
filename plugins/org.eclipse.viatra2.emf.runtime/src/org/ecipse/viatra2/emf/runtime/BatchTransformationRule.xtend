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
package org.ecipse.viatra2.emf.runtime

import org.eclipse.incquery.runtime.api.IMatchProcessor
import org.eclipse.incquery.runtime.api.IPatternMatch
import org.eclipse.incquery.runtime.api.IncQueryMatcher
import org.eclipse.incquery.runtime.evm.api.Job
import org.eclipse.incquery.runtime.evm.api.RuleEngine
import org.eclipse.incquery.runtime.evm.specific.lifecycle.DefaultActivationLifeCycle
import org.eclipse.incquery.runtime.evm.api.Activation
import org.eclipse.incquery.runtime.evm.api.Context
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum
import org.eclipse.incquery.runtime.evm.specific.Jobs
import org.eclipse.incquery.runtime.api.IQuerySpecification

/**
 * Wrapper class for transformation rule definition to hide EVM specific internals.
 * 
 * Subclasses can simply override the abstract precondition and modelmanipulation methods.
 *
 * @author Abel Hegedus
 *
 */
abstract class BatchTransformationRule<Match extends IPatternMatch,Matcher extends IncQueryMatcher<Match>> extends RuleSpecificationFactory<Match,Matcher> {
	
	override protected getLifeCycle() {
		DefaultActivationLifeCycle::DEFAULT_NO_UPDATE_AND_DISAPPEAR
	}
	
	override protected getJobs() {
		val proc = getModelManipulation
		val Job<Match> stJob = Jobs::newStatelessJob(IncQueryActivationStateEnum::APPEARED, proc)
		val Job<Match> job = Jobs::newRecordingJob(stJob)
		return <Job<Match>>newHashSet(job)
	}
	
	override protected getQuerySpecification() {
		getPrecondition
	}
	
	/**
	 * Returns the IMatcherFactory representing the pattern used as a precondition.
	 */
	def protected IQuerySpecification<Matcher> getPrecondition()
	
	/**
	 * Return an IMatchProcessor representing the model manipulation executed by the rule.
	 */
	def protected IMatchProcessor<Match> getModelManipulation()
	
	/**
	 * Goes through each possibly activation of the transformation rule and executes them.
	 * These activations are each in Appeared state.
	 */
	def fireEachActivation(RuleEngine ruleEngine, Context context){
		println("== Executing activations of " + ruleName + " ==")
		
 		var Activation<Match> act = ( null ) while((act = ruleEngine.getActivations(getRuleSpec, IncQueryActivationStateEnum::APPEARED).head) != null){
 			if(act.enabled){
	 			act.fire(context)
 			}
 		}
	}
	
	/**
	 * Selects one activation of the rule and executes it. 
	 */
	def fireOneActivation(RuleEngine ruleEngine, Context context){
		println("== Executing one activation of " + ruleName + " ==")
		
 		var Activation<Match> act = ruleEngine.getActivations(getRuleSpec, IncQueryActivationStateEnum::APPEARED).head
		if(act != null && act.enabled){
 			act.fire(context)
		}
	}
	
}