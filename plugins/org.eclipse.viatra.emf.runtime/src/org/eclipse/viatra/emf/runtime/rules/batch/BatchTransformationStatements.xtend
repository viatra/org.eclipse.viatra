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

import com.google.common.base.Predicate
import org.eclipse.incquery.runtime.api.IPatternMatch
import org.eclipse.incquery.runtime.api.IncQueryEngine
import org.eclipse.incquery.runtime.evm.api.Activation
import org.eclipse.incquery.runtime.evm.api.Context
import org.eclipse.incquery.runtime.evm.api.RuleEngine
import org.eclipse.incquery.runtime.evm.api.RuleSpecification
import org.eclipse.incquery.runtime.evm.api.event.EventFilter
import org.eclipse.incquery.runtime.evm.api.resolver.ScopedConflictSet
import org.eclipse.viatra.emf.runtime.filters.MatchParameterFilter
import org.eclipse.viatra.emf.runtime.rules.ITransformationRule
import org.eclipse.viatra.emf.runtime.rules.TransformationRuleGroup
import org.eclipse.viatra.emf.runtime.transformation.batch.BatchTransformation
import org.eclipse.xtext.xbase.lib.Pair

/**
 * Utility class for simple rule usage
 * 
 * @author Abel Hegedus, Zoltan Ujhelyi
 *
 */
class BatchTransformationStatements {
	
	val IncQueryEngine iqEngine
	val RuleEngine ruleEngine
	val Context context
	
	new(BatchTransformation transformation) {
		ruleEngine = transformation.ruleEngine
		context = transformation.context
		iqEngine = transformation.iqEngine
	}
	
	new(RuleEngine ruleEngine, IncQueryEngine iqEngine, Context context) {
		this.ruleEngine = ruleEngine
		this.context = context
		this.iqEngine = iqEngine
	}
	
	/**
	 * Executes the selected rule with the selected filter as long as there
	 * are possible matches of its preconditions and the break condition is
	 * not fulfilled. The matches are executed one-by-one, in case of conflicts
	 * only one of the conflicting matches will cause an execution. 
 	 */
	def <Match extends IPatternMatch> fireUntil (
		ITransformationRule<Match, ?> rule, Predicate<Match> breakCondition
	) {
		val filter = rule.ruleSpecification.createEmptyFilter
		rule.ruleSpecification.fireUntil(breakCondition, filter)
	}

	/**
	 * Executes the selected rule with the selected filter as long as there
	 * are possible matches of its precondition and the break condition is
	 * not fulfilled. The matches are executed one-by-one, in case of conflicts
	 * only one of the conflicting matches will cause an execution. 
 	 */
	def <Match extends IPatternMatch> fireUntil(
		ITransformationRule<Match, ?> rule,
		Predicate<Match> breakCondition,
		Pair<String, Object>... filterParameters
	) {
		rule.ruleSpecification.fireUntil(breakCondition, new MatchParameterFilter(filterParameters))
	}
	/**
	 * Executes the selected rule with the selected filter as long as there
	 * are possible matches of its precondition and the break condition is
	 * not fulfilled. The matches are executed one-by-one, in case of conflicts
	 * only one of the conflicting matches will cause an execution. 
 	 */
	def <Match extends IPatternMatch> fireUntil(
		ITransformationRule<Match, ?> rule,
		Predicate<Match> breakCondition,
		EventFilter<? super Match> filter
	) {
		rule.ruleSpecification.fireUntil(breakCondition, filter)
	}
	
	/**
	 * Executes the selected rules with the selected filter as long as there
	 * are possible matches of any of their preconditions and the break condition is
	 * not fulfilled. The matches are executed one-by-one, in case of conflicts
	 * only one of the conflicting matches will cause an execution. 
 	 */
	def fireUntil(TransformationRuleGroup rules, Predicate<IPatternMatch> breakCondition) {
		registerRules(rules)
		val ScopedConflictSet conflictSet = ruleEngine.createScopedConflictSet(rules.filteredRuleMap)
		
		conflictSet.fireUntil(breakCondition)
		
		conflictSet.dispose
	}
	
	private def <Match extends IPatternMatch> fireUntil(
		RuleSpecification<Match> ruleSpecification,
		Predicate<Match> breakCondition,
		EventFilter<? super Match> filter
	) {
		registerRule(ruleSpecification, filter)
		val ScopedConflictSet conflictSet = ruleEngine.createScopedConflictSet(ruleSpecification, filter)
		println('''== Executing activations of «ruleSpecification» with filter «filter» ==''')

		conflictSet.fireUntil(breakCondition)		
		println('''== Execution finished of «ruleSpecification» with filter «filter» ==''')
		conflictSet.dispose
		
		disposeRule(ruleSpecification, filter)
	}
	
	private def <Match extends IPatternMatch> fireUntil(ScopedConflictSet conflictSet, Predicate<Match> breakCondition) {
		var Activation<Match> act
		var continue = true
		while (continue && ((act = conflictSet.nextActivation as Activation<Match>) != null)) {
			act.fireActivation
			continue = !breakCondition.apply(act.atom)
		}
	}
	
	/**
	 * Executes the selected rule with the selected filter as long as there
	 * are possible matches of its precondition. The matches are executed
	 * one-by-one, in case of conflicts only one of the conflicting matches
	 * will cause an execution. 
 	 */
	def <Match extends IPatternMatch> fireWhilePossible(ITransformationRule<Match, ?> rule) {
		rule.fireUntil[false]	
	}
	
	/**
	 * Executes the selected rule with the selected filter as long as there
	 * are possible matches of its preconditions. The matches are executed
	 * one-by-one, in case of conflicts only one of the conflicting matches
	 * will cause an execution. 
 	 */
	def <Match extends IPatternMatch> fireWhilePossible(ITransformationRule<Match, ?> rule, EventFilter<? super Match> filter) {
		rule.fireUntil([false], filter)	
	}
	
	
	/**
	 * Executes the selected rules with the selected filter as long as there
	 * are possible matches of any of their preconditions. The matches are
	 * executed one-by-one, in case of conflicts only one of the conflicting
	 * matches will cause an execution. 
 	 */
	def fireWhilePossible(TransformationRuleGroup rules) {
		rules.fireUntil[false]
	}
	
	/**
	 * Executes the selected rule with the selected filter on its current match set of the precondition.
 	 */
	def <Match extends IPatternMatch> fireAllCurrent(ITransformationRule<Match, ?> rule) {
		val filter = rule.ruleSpecification.createEmptyFilter
		rule.ruleSpecification.fireAllCurrent(filter)
	}
	/**
	 * Executes the selected rule with the selected filter on its current match set of the precondition.
 	 */
	def <Match extends IPatternMatch> fireAllCurrent(ITransformationRule<Match, ?> rule, Pair<String, Object>... parameterFilter) {
		rule.ruleSpecification.fireAllCurrent(new MatchParameterFilter(parameterFilter))
	}
	/**
	 * Executes the selected rule with the selected filter on its current match set of the precondition.
 	 */
	def <Match extends IPatternMatch> fireAllCurrent(ITransformationRule<Match, ?> rule, EventFilter<? super Match> filter) {
		rule.ruleSpecification.fireAllCurrent(filter)
	}

	private def <Match extends IPatternMatch> fireAllCurrent(
		RuleSpecification<Match> ruleSpecification,
		EventFilter<? super Match> filter
	) {
		registerRule(ruleSpecification, filter)
		val ScopedConflictSet conflictSet = ruleEngine.createScopedConflictSet(ruleSpecification, filter)

		for(act : conflictSet.conflictingActivations){
			(act as Activation<Match>).fireActivation
		}
		
		disposeRule(ruleSpecification, filter)
		conflictSet.dispose
	}

	def <Match extends IPatternMatch> registerRule(RuleSpecification<Match> ruleSpecification) {
		ruleSpecification.registerRule(ruleSpecification.createEmptyFilter)	
	}
	
	def <Match extends IPatternMatch> registerRule(RuleSpecification<Match> ruleSpecification, EventFilter<? super Match> filter) {
		ruleEngine.addRule(ruleSpecification, filter)
	}
	
	def registerRules(TransformationRuleGroup rules) {
		rules.forEach[
			if (value != null)
				ruleEngine.addRule(key.ruleSpecification, value)
			else
				ruleEngine.addRule(key.ruleSpecification)
		]
	}
	
	def <Match extends IPatternMatch> disposeRule(RuleSpecification<Match> ruleSpecification) {
		ruleSpecification.disposeRule(ruleSpecification.createEmptyFilter)	
	}
	
	def <Match extends IPatternMatch> disposeRule(RuleSpecification<Match> ruleSpecification, EventFilter<? super Match> filter) {
		ruleEngine.removeRule(ruleSpecification, filter)
	}

	def disposeRules(TransformationRuleGroup rules) {
		rules.forEach[
			if (value != null)
				ruleEngine.removeRule(key.ruleSpecification, value)
			else
				ruleEngine.removeRule(key.ruleSpecification)
		]
	}
	
    /**
	 * Selects and fires an activation of the selected rule with a corresponding filter.</p>
	 * 
	 * <p><strong>Warning</strong>: the selection criteria is not specified - it is neither random nor controllable
	 */
	def <Match extends IPatternMatch> fireOne(ITransformationRule<Match, ?> rule) {
		val filter = rule.ruleSpecification.createEmptyFilter
		rule.ruleSpecification.fireOne(filter)
	}
	
	/**
	 * Selects and fires an activation of the selected rule with a corresponding filter.</p>
	 * 
	 * <p><strong>Warning</strong>: the selection criteria is not specified - it is neither random nor controllable
	 */
	def <Match extends IPatternMatch> fireOne(ITransformationRule<Match, ?> rule, EventFilter<? super Match> filter) {
		rule.ruleSpecification.fireOne(filter)
	}
		
	private def <Match extends IPatternMatch> fireOne(RuleSpecification<Match> ruleSpecification, EventFilter<? super Match> filter) {
		registerRule(ruleSpecification, filter)
		val conflictSet = ruleEngine.createScopedConflictSet(ruleSpecification, filter)
		
		val act = conflictSet.conflictingActivations.head as Activation<Match>
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
