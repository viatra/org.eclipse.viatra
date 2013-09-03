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

import com.google.common.base.Predicate
import org.eclipse.incquery.runtime.api.IPatternMatch
import org.eclipse.incquery.runtime.evm.api.Activation
import org.eclipse.incquery.runtime.evm.api.Context
import org.eclipse.incquery.runtime.evm.api.RuleEngine
import org.eclipse.incquery.runtime.evm.api.RuleSpecification
import org.eclipse.incquery.runtime.evm.api.event.EventFilter
import org.eclipse.incquery.runtime.evm.api.resolver.ScopedConflictSet

/**
 * Utility class for simple rule usage
 * 
 * @author Abel Hegedus, Zoltan Ujhelyi
 *
 */
class TransformationStatements {
	
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
	def <Match extends IPatternMatch> until (
		ITransformationRule<Match, ?> rule, Predicate<Match> breakCondition
	) {
		val filter = rule.ruleSpecification.createEmptyFilter
		rule.ruleSpecification.until(breakCondition, filter)
	}

	/**
	 * Executes the selected rule with the selected filter as long as there
	 * are possible matches of its preconditions. The matches are executed
	 * one-by-one, in case of conflicts only one of the conflicting matches
	 * will cause an execution. 
 	 */
	def <Match extends IPatternMatch> until(
		ITransformationRule<Match, ?> rule,
		Predicate<Match> breakCondition,
		EventFilter<? super Match> filter
	) {
		rule.ruleSpecification.until(breakCondition, filter)
	}
	
	private def <Match extends IPatternMatch> until(
		RuleSpecification<Match> ruleSpecification,
		Predicate<Match> breakCondition,
		EventFilter<? super Match> filter
	) {
		registerRule(ruleSpecification, filter)
		val ScopedConflictSet conflictSet = ruleEngine.createScopedConflictSet(ruleSpecification, filter)

		println('''== Executing activations of «ruleSpecification» with filter «filter» ==''')
		var Activation<Match> act
		var exit = false
		while (!exit && ((act = conflictSet.nextActivation as Activation<Match>) != null)) {
			act.fireActivation
			exit = breakCondition.apply(act.atom)
		}
		println('''== Execution finished of «ruleSpecification» with filter «filter» ==''')
		conflictSet.dispose
		
		disposeRule(ruleSpecification, filter)
	}
	
	/**
	 * Executes the selected rule with the selected filter on its current match set of the precondition.
 	 */
	def <Match extends IPatternMatch> forall(ITransformationRule<Match, ?> rule) {
		val filter = rule.ruleSpecification.createEmptyFilter
		rule.ruleSpecification.forall(filter)
	}
	/**
	 * Executes the selected rule with the selected filter on its current match set of the precondition.
 	 */
	def <Match extends IPatternMatch> forall(ITransformationRule<Match, ?> rule, EventFilter<? super Match> filter) {
		rule.ruleSpecification.forall(filter)
	}

	private def <Match extends IPatternMatch> forall(
		RuleSpecification<Match> ruleSpecification,
		EventFilter<? super Match> filter
	) {
		registerRule(ruleSpecification, filter)
		val ScopedConflictSet conflictSet = ruleEngine.createScopedConflictSet(ruleSpecification, filter)

		println('''== Executing activations of «ruleSpecification» with filter «filter» ==''')
		for(act : conflictSet.conflictingActivations){
			(act as Activation<Match>).fireActivation
		}
		println('''== Execution finished of «ruleSpecification» with filter «filter» ==''')
		
		disposeRule(ruleSpecification, filter)
		conflictSet.dispose
	}

	def <Match extends IPatternMatch> registerRule(RuleSpecification<Match> ruleSpecification) {
		ruleSpecification.registerRule(ruleSpecification.createEmptyFilter)	
	}
	
	def <Match extends IPatternMatch> registerRule(RuleSpecification<Match> ruleSpecification, EventFilter<? super Match> filter) {
		ruleEngine.addRule(ruleSpecification, filter)
	}
	
	def <Match extends IPatternMatch> disposeRule(RuleSpecification<Match> ruleSpecification) {
		ruleSpecification.disposeRule(ruleSpecification.createEmptyFilter)	
	}
	
	def <Match extends IPatternMatch> disposeRule(RuleSpecification<Match> ruleSpecification, EventFilter<? super Match> filter) {
		ruleEngine.removeRule(ruleSpecification, filter)
	}

    /**
	 * Selects and fires an activation of the selected rule with a corresponding filter.</p>
	 * 
	 * <p><strong>Warning</strong>: the selection criteria is not specified - it is neither random nor controllable
	 */
	def <Match extends IPatternMatch> choose(ITransformationRule<Match, ?> rule) {
		val filter = rule.ruleSpecification.createEmptyFilter
		rule.ruleSpecification.choose(filter)
	}
	
	/**
	 * Selects and fires an activation of the selected rule with a corresponding filter.</p>
	 * 
	 * <p><strong>Warning</strong>: the selection criteria is not specified - it is neither random nor controllable
	 */
	def <Match extends IPatternMatch> choose(ITransformationRule<Match, ?> rule, EventFilter<? super Match> filter) {
		rule.ruleSpecification.choose(filter)
	}
		
	private def <Match extends IPatternMatch> choose(RuleSpecification<Match> ruleSpecification, EventFilter<? super Match> filter) {
		val conflictSet = ruleEngine.createScopedConflictSet(ruleSpecification, filter)
		
		val act = conflictSet.conflictingActivations as Activation<Match>
		fireActivation(act)
		
		conflictSet.dispose
	}


	private def fireActivation (Activation<?> act) {
		if (act != null && act.enabled) {
			act.fire(context)
		}
		return
	}
}
