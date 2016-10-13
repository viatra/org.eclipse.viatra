/*******************************************************************************
 * Copyright (c) 2004-2013, Abel Hegedus, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Abel Hegedus, Zoltan Ujhelyi - initial API and implementation
 *   Peter Lunk - revised Transformation API structure for adapter support
 *******************************************************************************/
package org.eclipse.viatra.transformation.runtime.emf.transformation.batch

import com.google.common.base.Predicate
import com.google.common.collect.Iterators
import com.google.common.collect.Sets
import java.util.List
import org.eclipse.viatra.query.runtime.api.GenericQueryGroup
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.transformation.evm.api.Activation
import org.eclipse.viatra.transformation.evm.api.ConflictSetIterator
import org.eclipse.viatra.transformation.evm.api.IExecutor
import org.eclipse.viatra.transformation.evm.api.RuleEngine
import org.eclipse.viatra.transformation.evm.api.RuleSpecification
import org.eclipse.viatra.transformation.evm.api.event.EventFilter
import org.eclipse.viatra.transformation.evm.api.resolver.ScopedConflictSet
import org.eclipse.viatra.transformation.runtime.emf.filters.MatchParameterFilter
import org.eclipse.viatra.transformation.runtime.emf.rules.BatchTransformationRuleGroup
import org.eclipse.viatra.transformation.runtime.emf.rules.batch.BatchTransformationRule

/**
 * Utility class for simple rule usage
 * 
 * @author Abel Hegedus, Zoltan Ujhelyi, Peter Lunk
 */
class BatchTransformationStatements {

    val ViatraQueryEngine queryEngine
    val RuleEngine ruleEngine
    val IExecutor executor

    package new(BatchTransformation transformation, IExecutor executor) {
        this.ruleEngine = transformation.ruleEngine
        this.executor = executor
        queryEngine = transformation.queryEngine
    }

    /**
     * Executes the selected rule with the selected filter as long as there
     * are possible matches of its preconditions and the break condition is
     * not fulfilled. The matches are executed one-by-one, in case of conflicts
     * only one of the conflicting matches will cause an execution. 
     */
    def <Match extends IPatternMatch> fireUntil(
        BatchTransformationRule<Match, ?> rule,
        Predicate<Match> breakCondition
    ) {
        executor.startExecution("Fire_until_transaction_condition_ruleName: " + rule.name)
        val filter = rule.ruleSpecification.createEmptyFilter
        rule.ruleSpecification.fireUntil(breakCondition, filter)
        executor.endExecution("Fire_until_transaction_condition_ruleName: " + rule.name)
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
        Pair<String, ? extends Object>... filterParameters
    ) {
        executor.startExecution("Fire_until_transaction_filter_condition_ruleName: " + rule.name)
        rule.ruleSpecification.fireUntil(breakCondition, new MatchParameterFilter(filterParameters))
        executor.endExecution("Fire_until_transaction_filter_condition_ruleName: " + rule.name)
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
        executor.startExecution("Fire_until_transaction_filter_condition_ruleName: " + rule.name)
        rule.ruleSpecification.fireUntil(breakCondition, filter)
        executor.endExecution("Fire_until_transaction_filter_condition_ruleName: " + rule.name)
    }

    /**
     * Executes the selected rules with the selected filter as long as there
     * are possible matches of any of their preconditions and the break condition is
     * not fulfilled. The matches are executed one-by-one, in case of conflicts
     * only one of the conflicting matches will cause an execution. 
     */
    def fireUntil(BatchTransformationRuleGroup rules, Predicate<IPatternMatch> breakCondition) {
        executor.startExecution("Fire_until_transaction_condition_ruleGroup")
        registerRules(rules)
        val ScopedConflictSet conflictSet = ruleEngine.createScopedConflictSet(rules.filteredRuleMap)

        conflictSet.fireUntil(breakCondition)

        conflictSet.dispose
        executor.endExecution("Fire_until_transaction_condition_ruleGroup")
    }

    /**
     * Executes the selected rule with the selected filter as long as there
     * are possible matches of its precondition. The matches are executed
     * one-by-one, in case of conflicts only one of the conflicting matches
     * will cause an execution. 
     */
    def <Match extends IPatternMatch> fireWhilePossible(BatchTransformationRule<Match, ?> rule) {
        executor.startExecution("Fire_while_possible_transaction_ruleName: " + rule.name)
        rule.fireUntil[false]
        executor.endExecution("Fire_while_possible_transaction_ruleName: " + rule.name)
    }

    /**
     * Executes the selected rule with the selected filter as long as there
     * are possible matches of its preconditions. The matches are executed
     * one-by-one, in case of conflicts only one of the conflicting matches
     * will cause an execution. 
     */
    def <Match extends IPatternMatch> fireWhilePossible(BatchTransformationRule<Match, ?> rule,
        EventFilter<? super Match> filter) {
        executor.startExecution("Fire_while_possible_transaction_filter_ruleName: " + rule.name)
        rule.fireUntil([false], filter)
        executor.endExecution("Fire_while_possible_transaction_filter_ruleName: " + rule.name)
    }

    /**
     * Executes the selected rules with the selected filter as long as there
     * are possible matches of any of their preconditions. The matches are
     * executed one-by-one, in case of conflicts only one of the conflicting
     * matches will cause an execution. 
     */
    def fireWhilePossible(BatchTransformationRuleGroup rules) {
        executor.startExecution("Fire_while_possible_transaction_ruleGroup")
        rules.fireUntil[false]
        executor.endExecution("Fire_while_possible_transaction_ruleGroup")
    }

    /**
     * Executes the selected rule with the selected filter on its current match set of the precondition.
     */
    def <Match extends IPatternMatch> fireAllCurrent(BatchTransformationRule<Match, ?> rule) {
        executor.startExecution("Fire_all_current_transaction_ruleName: " + rule.name)
        val filter = rule.ruleSpecification.createEmptyFilter
        rule.ruleSpecification.fireAllCurrent(filter)
        executor.endExecution("Fire_all_current_transaction_ruleName: " + rule.name)
    }

    /**
     * Executes the selected rule with the selected filter on its current match set of the precondition.
     */
    def <Match extends IPatternMatch> fireAllCurrent(BatchTransformationRule<Match, ?> rule,
        Pair<String, ? extends Object>... parameterFilter) {
        executor.startExecution("Fire_all_current_transaction_filter_ruleName: " + rule.name)
        rule.ruleSpecification.fireAllCurrent(new MatchParameterFilter(parameterFilter))
        executor.endExecution("Fire_all_current_transaction_filter_ruleName: " + rule.name)
    }

    /**
     * Executes the selected rule with the selected filter on its current match set of the precondition.
     */
    def <Match extends IPatternMatch> fireAllCurrent(BatchTransformationRule<Match, ?> rule,
        EventFilter<? super Match> filter) {
        executor.startExecution("Fire_all_current_transaction_filter_ruleName: " + rule.name)
        rule.ruleSpecification.fireAllCurrent(filter)
        executor.endExecution("Fire_all_current_transaction_filter_ruleName: " + rule.name)
    }

    def <Match extends IPatternMatch> registerRule(RuleSpecification<Match> ruleSpecification) {
        ruleSpecification.registerRule(ruleSpecification.createEmptyFilter)
    }

    def <Match extends IPatternMatch> registerRule(RuleSpecification<Match> ruleSpecification,
        EventFilter<? super Match> filter) {
        ruleEngine.addRule(ruleSpecification, filter)
    }

    def registerRules(BatchTransformationRuleGroup rules) {
        val notNullPreconditions = rules.filterNull.map[precondition]
        GenericQueryGroup.of(Sets.newHashSet(notNullPreconditions)).prepare(queryEngine)
        rules.filterNull.forEach [
            ruleEngine.addRule(ruleSpecification, filter as EventFilter<IPatternMatch>)
        ]
    }
    
    /**
     * Returns the number of current activations of the rule.
     * 
     * @since 1.5
     */
    def <Match extends IPatternMatch> countAllCurrent(BatchTransformationRule<Match, ?> rule) {
        val filter = rule.ruleSpecification.createEmptyFilter
        rule.ruleSpecification.countAllCurrent(filter)
    }
    
    /**
     * Returns the number of current activations of the rule.
     * 
     * @since 1.5
     */
    def <Match extends IPatternMatch> countAllCurrent(BatchTransformationRule<Match, ?> rule,
        Pair<String, ? extends Object>... parameterFilter) {
        rule.ruleSpecification.countAllCurrent(new MatchParameterFilter(parameterFilter))
    }
    
    /**
     * Returns the number of current activations of the rule.
     * 
     * @since 1.5
     */
    def <Match extends IPatternMatch> countAllCurrent(BatchTransformationRule<Match, ?> rule,
        EventFilter<? super Match> filter) {
        rule.ruleSpecification.countAllCurrent(filter)
    }

    def <Match extends IPatternMatch> disposeRule(RuleSpecification<Match> ruleSpecification) {
        ruleSpecification.disposeRule(ruleSpecification.createEmptyFilter)
    }

    def <Match extends IPatternMatch> disposeRule(RuleSpecification<Match> ruleSpecification,
        EventFilter<? super Match> filter) {
        ruleEngine.removeRule(ruleSpecification, filter)
    }

    def disposeRules(BatchTransformationRuleGroup rules) {
        rules.filterNull.forEach [
            ruleEngine.removeRule(ruleSpecification, filter as EventFilter<IPatternMatch>)
        ]
    }

    /**
     * Selects and fires an activation of the selected rule with no corresponding filter.</p>
     * 
     * <p><strong>Warning</strong>: the selection criteria is not specified - it is neither random nor controllable
     */
    def <Match extends IPatternMatch> fireOne(BatchTransformationRule<Match, ?> rule) {
        executor.startExecution("Fire_one_transaction_ruleName: " + rule.name)
        val filter = rule.ruleSpecification.createEmptyFilter
        rule.ruleSpecification.fireOne(filter)
        executor.endExecution("Fire_one_transaction_ruleName: " + rule.name)
    }

    /**
     * Selects and fires an activation of the selected rule with a corresponding filter</p>
     * 
     * <p><strong>Warning</strong>: the selection criteria is not specified - it is neither random nor controllable
     */
    def <Match extends IPatternMatch> fireOne(BatchTransformationRule<Match, ?> rule,
        Pair<String, ? extends Object>... parameterFilter) {
        executor.startExecution("Fire_one_transaction_filter_ruleName: " + rule.name)
        rule.ruleSpecification.fireOne(new MatchParameterFilter(parameterFilter))
        executor.endExecution("Fire_one_transaction_filter_ruleName: " + rule.name)
    }

    /**
     * Selects and fires an activation of the selected rule with a corresponding filter.</p>
     * 
     * <p><strong>Warning</strong>: the selection criteria is not specified - it is neither random nor controllable
     */
    def <Match extends IPatternMatch> fireOne(BatchTransformationRule<Match, ?> rule,
        EventFilter<? super Match> filter) {
        executor.startExecution("Fire_one_transaction_filter_ruleName: " + rule.name)
        rule.ruleSpecification.fireOne(filter)
        executor.endExecution("Fire_one_transaction_filter_ruleName: " + rule.name)
    }

    private def <Match extends IPatternMatch> fireOne(RuleSpecification<Match> ruleSpecification,
        EventFilter<? super Match> filter) {

        registerRule(ruleSpecification, filter)
        val conflictSet = ruleEngine.createScopedConflictSet(ruleSpecification, filter)
        val iterator = Iterators.singletonIterator(conflictSet.conflictingActivations.head as Activation<?>)

        executor.execute(iterator)

        conflictSet.dispose
        disposeRule(ruleSpecification, filter)
    }

    private def <Match extends IPatternMatch> fireAllCurrent(
        RuleSpecification<Match> ruleSpecification,
        EventFilter<? super Match> filter
    ) {
        registerRule(ruleSpecification, filter)

        val ScopedConflictSet conflictSet = ruleEngine.createScopedConflictSet(ruleSpecification, filter)
        var List<Activation<?>> list = newArrayList(conflictSet.conflictingActivations)

        executor.execute(list.iterator)

        conflictSet.dispose
        disposeRule(ruleSpecification, filter)
    }
    
    private def <Match extends IPatternMatch> countAllCurrent(
        RuleSpecification<Match> ruleSpecification,
        EventFilter<? super Match> filter
    ) {
        registerRule(ruleSpecification, filter)

        val ScopedConflictSet conflictSet = ruleEngine.createScopedConflictSet(ruleSpecification, filter)
        var count = conflictSet.conflictingActivations.size

        conflictSet.dispose
        disposeRule(ruleSpecification, filter)
        return count
    }

    private def <Match extends IPatternMatch> fireUntil(
        RuleSpecification<Match> ruleSpecification,
        Predicate<Match> breakCondition,
        EventFilter<? super Match> filter
    ) {
        registerRule(ruleSpecification, filter)
        val ScopedConflictSet conflictSet = ruleEngine.createScopedConflictSet(ruleSpecification, filter)

        executor.execute(
            new ConflictSetIterator(ruleEngine.createScopedConflictSet(ruleSpecification, filter), breakCondition))

        conflictSet.dispose

        disposeRule(ruleSpecification, filter)
    }

    private def <Match extends IPatternMatch> fireUntil(ScopedConflictSet conflictSet,
        Predicate<Match> breakCondition) {

        executor.execute(new ConflictSetIterator(conflictSet, breakCondition))
    }

}
