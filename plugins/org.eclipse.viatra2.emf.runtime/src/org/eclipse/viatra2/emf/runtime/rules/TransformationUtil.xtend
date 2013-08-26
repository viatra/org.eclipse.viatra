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

import org.eclipse.incquery.runtime.evm.api.RuleEngine
import org.eclipse.incquery.runtime.evm.api.RuleSpecification
import org.eclipse.incquery.runtime.evm.api.event.EventFilter
import org.eclipse.incquery.runtime.evm.api.Context
import org.eclipse.incquery.runtime.evm.api.Activation
import org.eclipse.incquery.runtime.api.IPatternMatch
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum

/**
 * Utility class for simple rule usage
 * 
 * @author Abel Hegedus
 *
 */
class TransformationUtil {
	
	val RuleEngine ruleEngine
	val Context context
	
	new(RuleEngine ruleEngine, Context context) {
		this.ruleEngine = ruleEngine
		this.context = context
	}
	
	/**
	 * Executes the selected rule with the selected filter as long as there
	 * are possible matches of its preconditions. The matches are executed
	 * one-by-one, in case of conflicts only one of the conflicting matches
	 * will cause an execution. 
 	 */
	def <Match extends IPatternMatch> batchExecution(
		RuleSpecification<Match> ruleSpecification,
		EventFilter<? super Match> filter
	) {

		ruleEngine.addRule(ruleSpecification, filter)

		println('''== Executing activations of «ruleSpecification» with filter «filter» ==''')
		var Activation<Match> act
		while ((act = ruleSpecification.firstActivation(filter)) != null) {
			act.fireActivation
		}
		println('''== Execution finished of «ruleSpecification» with filter «filter» ==''')
		ruleEngine.removeRule(ruleSpecification, filter)

	}

	/**
	 * Executes the selected rule with the selected filter as long as there
	 * are possible matches of its preconditions. The matches are executed
	 * one-by-one, in case of conflicts only one of the conflicting matches
	 * will cause an execution. 
 	 */
	def <Match extends IPatternMatch> batchExecution(
		RuleSpecification<Match> ruleSpecification
	) {
		val filter = ruleSpecification.createEmptyFilter
		ruleSpecification.batchExecution(filter)
	}

	private def <Match extends IPatternMatch> firstActivation(
		RuleSpecification<Match> ruleSpecification,
		EventFilter<? super Match> filter
	) {
		ruleEngine.getActivations(ruleSpecification, filter, IncQueryActivationStateEnum::APPEARED).head
	}

	private def <Match extends IPatternMatch> fireActivation(
		Activation<Match> act
	) {
		if (act != null && act.enabled) {
			act.fire(context)
		}
	}
}
