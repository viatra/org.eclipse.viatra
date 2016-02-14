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

import com.google.common.base.Predicate
import com.google.common.collect.ImmutableSet
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.transformation.evm.api.Activation
import org.eclipse.viatra.transformation.evm.api.Context
import org.eclipse.viatra.transformation.evm.api.RuleEngine
import org.eclipse.viatra.transformation.evm.api.RuleSpecification
import org.eclipse.viatra.transformation.evm.api.event.EventFilter
import org.eclipse.viatra.transformation.evm.api.resolver.ScopedConflictSet
import org.eclipse.viatra.transformation.runtime.emf.filters.MatchParameterFilter
import org.eclipse.viatra.transformation.runtime.emf.rules.BatchTransformationRuleGroup
import org.eclipse.viatra.transformation.runtime.emf.transformation.batch.BatchTransformation
import com.google.common.collect.Sets
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.GenericQueryGroup

/**
 * Utility class for simple rule usage
 * 
 * @author Abel Hegedus, Zoltan Ujhelyi
 *
 */
class BatchTransformationStatements {
	
	val ViatraQueryEngine queryEngine
	val RuleEngine ruleEngine
	val Context context
	
	new(BatchTransformation transformation) {
		ruleEngine = transformation.ruleEngine
		context = transformation.context
		queryEngine = transformation.queryEngine
	}
	
	new(RuleEngine ruleEngine, ViatraQueryEngine queryEngine, Context context) {
		this.ruleEngine = ruleEngine
		this.context = context
		this.queryEngine = queryEngine
	}
	
	/**
	 * Executes the selected rule with the selected filter as long as there
	 * are possible matches of its preconditions and the break condition is
	 * not fulfilled. The matches are executed one-by-one, in case of conflicts
	 * only one of the conflicting matches will cause an execution. 
 	 */
	def <Match extends IPatternMatch> fireUntil (
		BatchTransformationRule<Match, ?> rule, Predicate<Match> breakCondition
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
		BatchTransformationRule<Match, ?> rule,
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
		BatchTransformationRule<Match, ?> rule,
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
	def fireUntil(BatchTransformationRuleGroup rules, Predicate<IPatternMatch> breakCondition) {
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

		conflictSet.fireUntil(breakCondition)		
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
	def <Match extends IPatternMatch> fireWhilePossible(BatchTransformationRule<Match, ?> rule) {
		rule.fireUntil[false]	
	}
	
	/**
	 * Executes the selected rule with the selected filter as long as there
	 * are possible matches of its preconditions. The matches are executed
	 * one-by-one, in case of conflicts only one of the conflicting matches
	 * will cause an execution. 
 	 */
	def <Match extends IPatternMatch> fireWhilePossible(BatchTransformationRule<Match, ?> rule, EventFilter<? super Match> filter) {
		rule.fireUntil([false], filter)	
	}
	
	
	/**
	 * Executes the selected rules with the selected filter as long as there
	 * are possible matches of any of their preconditions. The matches are
	 * executed one-by-one, in case of conflicts only one of the conflicting
	 * matches will cause an execution. 
 	 */
	def fireWhilePossible(BatchTransformationRuleGroup rules) {
		rules.fireUntil[false]
	}
	
	/**
	 * Executes the selected rule with the selected filter on its current match set of the precondition.
 	 */
	def <Match extends IPatternMatch> fireAllCurrent(BatchTransformationRule<Match, ?> rule) {
		val filter = rule.ruleSpecification.createEmptyFilter
		rule.ruleSpecification.fireAllCurrent(filter)
	}
	/**
	 * Executes the selected rule with the selected filter on its current match set of the precondition.
 	 */
	def <Match extends IPatternMatch> fireAllCurrent(BatchTransformationRule<Match, ?> rule, Pair<String, Object>... parameterFilter) {
		rule.ruleSpecification.fireAllCurrent(new MatchParameterFilter(parameterFilter))
	}
	/**
	 * Executes the selected rule with the selected filter on its current match set of the precondition.
 	 */
	def <Match extends IPatternMatch> fireAllCurrent(BatchTransformationRule<Match, ?> rule, EventFilter<? super Match> filter) {
		rule.ruleSpecification.fireAllCurrent(filter)
	}

	private def <Match extends IPatternMatch> fireAllCurrent(
		RuleSpecification<Match> ruleSpecification,
		EventFilter<? super Match> filter
	) {
		registerRule(ruleSpecification, filter)
		val ScopedConflictSet conflictSet = ruleEngine.createScopedConflictSet(ruleSpecification, filter)

		val conflictingActivations = ImmutableSet.copyOf(conflictSet.conflictingActivations)
		for(act : conflictingActivations){
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

	def registerRules(BatchTransformationRuleGroup rules) {
		val notNullPreconditions = rules.filterNull.map[precondition]
		GenericQueryGroup.of(Sets.newHashSet(notNullPreconditions)).prepare(queryEngine)
		rules.filterNull.forEach[
				ruleEngine.addRule(ruleSpecification, filter as EventFilter<IPatternMatch>)
		]
	}
	
	def <Match extends IPatternMatch> disposeRule(RuleSpecification<Match> ruleSpecification) {
		ruleSpecification.disposeRule(ruleSpecification.createEmptyFilter)	
	}
	
	def <Match extends IPatternMatch> disposeRule(RuleSpecification<Match> ruleSpecification, EventFilter<? super Match> filter) {
		ruleEngine.removeRule(ruleSpecification, filter)
	}

	def disposeRules(BatchTransformationRuleGroup rules) {
		rules.filterNull.forEach[
				ruleEngine.removeRule(ruleSpecification, filter as EventFilter<IPatternMatch>)
		]
	}
	
    /**
	 * Selects and fires an activation of the selected rule with a corresponding filter.</p>
	 * 
	 * <p><strong>Warning</strong>: the selection criteria is not specified - it is neither random nor controllable
	 */
	def <Match extends IPatternMatch> fireOne(BatchTransformationRule<Match, ?> rule) {
		val filter = rule.ruleSpecification.createEmptyFilter
		rule.ruleSpecification.fireOne(filter)
	}
	
	/**
	 * Selects and fires an activation of the selected rule with a corresponding filter.</p>
	 * 
	 * <p><strong>Warning</strong>: the selection criteria is not specified - it is neither random nor controllable
	 */
	def <Match extends IPatternMatch> fireOne(BatchTransformationRule<Match, ?> rule, EventFilter<? super Match> filter) {
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
