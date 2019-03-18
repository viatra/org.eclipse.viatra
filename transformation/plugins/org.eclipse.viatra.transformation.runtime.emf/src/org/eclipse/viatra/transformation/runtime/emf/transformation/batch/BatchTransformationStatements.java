/**
 * Copyright (c) 2004-2013, Abel Hegedus, Zoltan Ujhelyi Peter Lunk and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.runtime.emf.transformation.batch;

import java.util.Collections;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.ConflictSetIterator;
import org.eclipse.viatra.transformation.evm.api.IExecutor;
import org.eclipse.viatra.transformation.evm.api.RuleEngine;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.viatra.transformation.evm.api.resolver.ScopedConflictSet;
import org.eclipse.viatra.transformation.runtime.emf.filters.MatchParameterFilter;
import org.eclipse.viatra.transformation.runtime.emf.rules.BatchTransformationRuleGroup;
import org.eclipse.viatra.transformation.runtime.emf.rules.batch.BatchTransformationRule;
import org.eclipse.viatra.transformation.runtime.emf.transformation.batch.BatchTransformation;

/**
 * Utility class for simple rule usage
 * 
 * @author Abel Hegedus, Zoltan Ujhelyi, Peter Lunk
 */
public class BatchTransformationStatements {

    private static final String FIRE_ONE_TRANSACTION_FILTER_RULE_NAME = "Fire_one_transaction_filter_ruleName: ";
    private static final String FIRE_ONE_TRANSACTION_RULE_NAME = "Fire_one_transaction_ruleName: ";
    private static final String FIRE_UNTIL_TRANSACTION_CONDITION_RULE_NAME = "Fire_until_transaction_condition_ruleName: ";
    private static final String FIRE_UNTIL_TRANSACTION_FILTER_CONDITION_RULE_NAME = "Fire_until_transaction_filter_condition_ruleName: ";
    private static final String FIRE_UNTIL_TRANSACTION_CONDITION_RULE_GROUP = "Fire_until_transaction_condition_ruleGroup";
    private static final String FIRE_WHILE_POSSIBLE_TRANSACTION_RULE_NAME = "Fire_while_possible_transaction_ruleName: ";
    private static final String FIRE_WHILE_POSSIBLE_TRANSACTION_FILTER_RULE_NAME = "Fire_while_possible_transaction_filter_ruleName: ";
    private static final String FIRE_WHILE_POSSIBLE_TRANSACTION_RULE_GROUP = "Fire_while_possible_transaction_ruleGroup";
    private static final String FIRE_ALL_CURRENT_TRANSACTION_RULE_NAME = "Fire_all_current_transaction_ruleName: ";
    private static final String FIRE_ALL_CURRENT_TRANSACTION_FILTER_RULE_NAME = "Fire_all_current_transaction_filter_ruleName: ";

    private final ViatraQueryEngine queryEngine;

    private final RuleEngine ruleEngine;

    private final IExecutor executor;
    private final BatchTransformation transformation;

    BatchTransformationStatements(final BatchTransformation transformation, final IExecutor executor) {
        this.transformation = transformation;
        this.ruleEngine = transformation.ruleEngine;
        this.executor = executor;
        this.queryEngine = transformation.queryEngine;
    }

    /**
     * Executes the selected rule with the selected filter as long as there are possible matches of its preconditions
     * and the break condition is not fulfilled. The matches are executed one-by-one, in case of conflicts only one of
     * the conflicting matches will be fired.
     * @since 2.0
     */
    public <Match extends IPatternMatch> void fireUntil(final BatchTransformationRule<Match, ?> rule,
            final Predicate<Match> breakCondition) {
        executor.startExecution(FIRE_UNTIL_TRANSACTION_CONDITION_RULE_NAME + rule.getName());
        fireUntil(rule.getRuleSpecification(), breakCondition, rule.getFilter());
        executor.endExecution(FIRE_UNTIL_TRANSACTION_CONDITION_RULE_NAME + rule.getName());
    }

    /**
     * Executes the selected rule with the selected filter as long as there are possible matches of its precondition and
     * the break condition is not fulfilled. The matches are executed one-by-one, in case of conflicts only one of the
     * conflicting matches will be fired.
     * @since 2.0
     */
    @SafeVarargs
    public final <Match extends IPatternMatch> void fireUntil(final BatchTransformationRule<Match, ?> rule,
            final Predicate<Match> breakCondition, final Entry<String, ?>... filterParameters) {
        executor.startExecution(FIRE_UNTIL_TRANSACTION_FILTER_CONDITION_RULE_NAME + rule.getName());
        fireUntil(rule.getRuleSpecification(), breakCondition, new MatchParameterFilter(filterParameters));
        executor.endExecution(FIRE_UNTIL_TRANSACTION_FILTER_CONDITION_RULE_NAME + rule.getName());
    }

    /**
     * Executes the selected rule with the selected filter as long as there are possible matches of its precondition and
     * the break condition is not fulfilled. The matches are executed one-by-one, in case of conflicts only one of the
     * conflicting matches will be fired.
     * @since 2.0
     */
    public <Match extends IPatternMatch> void fireUntil(final BatchTransformationRule<Match, ?> rule,
            final Predicate<Match> breakCondition, final EventFilter<? super Match> filter) {
        executor.startExecution(FIRE_UNTIL_TRANSACTION_FILTER_CONDITION_RULE_NAME + rule.getName());
        fireUntil(rule.getRuleSpecification(), breakCondition, filter);
        executor.endExecution(FIRE_UNTIL_TRANSACTION_FILTER_CONDITION_RULE_NAME + rule.getName());
    }

    /**
     * Executes the selected rules with the selected filter as long as there are possible matches of any of their
     * preconditions and the break condition is not fulfilled. The matches are executed one-by-one, in case of conflicts
     * only one of the conflicting matches will be fired.
     * @since 2.0
     */
    public void fireUntil(final BatchTransformationRuleGroup rules, final Predicate<IPatternMatch> breakCondition) {
        executor.startExecution(FIRE_UNTIL_TRANSACTION_CONDITION_RULE_GROUP);
        registerRulesInternal(rules);
        final ScopedConflictSet conflictSet = ruleEngine.createScopedConflictSet(rules.getFilteredRuleMap());
        this.executor.execute(new ConflictSetIterator(conflictSet, breakCondition));
        conflictSet.dispose();
        executor.endExecution(FIRE_UNTIL_TRANSACTION_CONDITION_RULE_GROUP);
    }

    /**
     * Executes all rules of the transformation with the selected filter as long as there are possible matches of any of their
     * preconditions and the break condition is not fulfilled. The matches are executed one-by-one, in case of conflicts
     * only one of the conflicting matches will be fired.
     * @since 2.2
     */
    public void fireUntil(final Predicate<IPatternMatch> breakCondition) {
        fireUntil(transformation.getTransformationRuleGroup(), breakCondition);
    }
    /**
     * Executes the selected rule with the selected filter as long as there are possible matches of its precondition.
     * The matches are executed one-by-one, in case of conflicts only one of the conflicting matches will be fired.
     */
    public <Match extends IPatternMatch> void fireWhilePossible(final BatchTransformationRule<Match, ?> rule) {
        executor.startExecution(FIRE_WHILE_POSSIBLE_TRANSACTION_RULE_NAME + rule.getName());
        fireUntil(rule, it -> false);
        executor.endExecution(FIRE_WHILE_POSSIBLE_TRANSACTION_RULE_NAME + rule.getName());
    }

    /**
     * Executes the selected rule with the selected filter as long as there are possible matches of its preconditions.
     * The matches are executed one-by-one, in case of conflicts only one of the conflicting matches will be fired.
     */
    public <Match extends IPatternMatch> void fireWhilePossible(final BatchTransformationRule<Match, ?> rule,
            final EventFilter<? super Match> filter) {
        this.executor.startExecution(FIRE_WHILE_POSSIBLE_TRANSACTION_FILTER_RULE_NAME + rule.getName());
        this.<Match> fireUntil(rule, it -> false, filter);
        this.executor.endExecution(FIRE_WHILE_POSSIBLE_TRANSACTION_FILTER_RULE_NAME + rule.getName());
    }

    /**
     * Executes the selected rules with the default filter as long as there are possible matches of any of their
     * preconditions. The matches are executed one-by-one, in case of conflicts only one of the conflicting matches will
     * be fired.
     */
    public void fireWhilePossible(final BatchTransformationRuleGroup rules) {
        executor.startExecution(FIRE_WHILE_POSSIBLE_TRANSACTION_RULE_GROUP);
        fireUntil(rules, it -> false);
        executor.endExecution(FIRE_WHILE_POSSIBLE_TRANSACTION_RULE_GROUP);
    }

    /**
     * Executes all rules of the transformation with the default as long as there are possible matches of any of their
     * preconditions. The matches are executed one-by-one, in case of conflicts only one of the conflicting matches will
     * be fired.
     * 
     * @since 2.2
     */
    public void fireWhilePossible() {
        fireWhilePossible(transformation.getTransformationRuleGroup());
    }
    /**
     * Executes the selected rule with the selected filter on its current match set of the precondition.
     */
    public <Match extends IPatternMatch> void fireAllCurrent(final BatchTransformationRule<Match, ?> rule) {
        executor.startExecution(FIRE_ALL_CURRENT_TRANSACTION_RULE_NAME + rule.getName());
        fireAllCurrent(rule.getRuleSpecification(), rule.getFilter());
        executor.endExecution(FIRE_ALL_CURRENT_TRANSACTION_RULE_NAME + rule.getName());
    }

    /**
     * Executes the selected rule with the selected filter on its current match set of the precondition.
     * @since 2.0
     */
    @SafeVarargs
    public final <Match extends IPatternMatch> void fireAllCurrent(final BatchTransformationRule<Match, ?> rule,
            final Entry<String, ?>... parameterFilter) {
        executor.startExecution(FIRE_ALL_CURRENT_TRANSACTION_FILTER_RULE_NAME + rule.getName());
        fireAllCurrent(rule.getRuleSpecification(), new MatchParameterFilter(parameterFilter));
        executor.endExecution(FIRE_ALL_CURRENT_TRANSACTION_FILTER_RULE_NAME + rule.getName());
    }

    /**
     * Executes the selected rule with the selected filter on its current match set of the precondition.
     */
    public <Match extends IPatternMatch> void fireAllCurrent(final BatchTransformationRule<Match, ?> rule,
            final EventFilter<? super Match> filter) {
        executor.startExecution(FIRE_ALL_CURRENT_TRANSACTION_FILTER_RULE_NAME + rule.getName());
        fireAllCurrent(rule.getRuleSpecification(), filter);
        executor.endExecution(FIRE_ALL_CURRENT_TRANSACTION_FILTER_RULE_NAME + rule.getName());
    }

    /**
     * @deprecated Added to the API as an error, should have been internal helper method.
     */
    @Deprecated
    public <Match extends IPatternMatch> boolean registerRule(final RuleSpecification<Match> ruleSpecification) {
        return registerRule(ruleSpecification, ruleSpecification.createEmptyFilter());
    }

    /**
     * @deprecated This method was added to the API as an error, should not be available for transformation developers.
     */
    @Deprecated
    public <Match extends IPatternMatch> boolean registerRule(final RuleSpecification<Match> ruleSpecification,
            final EventFilter<? super Match> filter) {
        return registerRuleInternal(ruleSpecification, filter);
    }
    
    private <Match extends IPatternMatch> boolean registerRuleInternal(final RuleSpecification<Match> ruleSpecification,
            final EventFilter<? super Match> filter) {
        return ruleEngine.addRule(ruleSpecification, filter);
    }
    
    /**
     * @deprecated This method was added to the API as an error, should not be available for transformation developers.
     */
    @Deprecated
    public void registerRules(final BatchTransformationRuleGroup rules) {
        registerRulesInternal(rules);
    }
    
    private void registerRulesInternal(final BatchTransformationRuleGroup rules) {
        rules.prepareQueryEngine(this.queryEngine);
        rules.stream().filter(Objects::nonNull).forEach(it -> it.registerRule(ruleEngine));
    }


    /**
     * Returns the number of current activations of the rule.
     * 
     * @since 1.5
     */
    public <Match extends IPatternMatch> int countAllCurrent(final BatchTransformationRule<Match, ?> rule) {
        return countAllCurrent(rule.getRuleSpecification(), rule.getFilter());
    }

    /**
     * Returns the number of current activations of the rule.
     * 
     * @since 2.0
     */
    @SafeVarargs
    public final <Match extends IPatternMatch> int countAllCurrent(final BatchTransformationRule<Match, ?> rule,
            final Entry<String, ?>... parameterFilter) {
        return countAllCurrent(rule.getRuleSpecification(), new MatchParameterFilter(parameterFilter));
    }

    /**
     * Returns the number of current activations of the rule.
     * 
     * @since 1.5
     */
    public <Match extends IPatternMatch> int countAllCurrent(final BatchTransformationRule<Match, ?> rule,
            final EventFilter<? super Match> filter) {
        return countAllCurrent(rule.getRuleSpecification(), filter);
    }
    
    /**
     * Returns whether the given rule can be fired
     * 
     * @since 2.2
     */
    public <Match extends IPatternMatch> boolean hasCurrent(final BatchTransformationRule<Match, ?> rule) {
        return hasCurrent(rule, rule.getFilter());
    }

    /**
     * Returns whether the given rule can be fired
     * 
     * @since 2.2
     */
    @SafeVarargs
    public final <Match extends IPatternMatch> boolean hasCurrent(final BatchTransformationRule<Match, ?> rule,
            final Entry<String, ?>... parameterFilter) {
        return hasCurrent(rule, new MatchParameterFilter(parameterFilter));
    }

    /**
     * Returns whether the given rule can be fired
     * 
     * @since 2.2
     */
    public <Match extends IPatternMatch> boolean hasCurrent(final BatchTransformationRule<Match, ?> rule,
            final EventFilter<? super Match> filter) {
        return queryEngine.getMatcher(rule.getPrecondition()).streamAllMatches().anyMatch(filter::isProcessable);
    }

    /**
     * Returns whether at least a single rule in the given group can be fired using its default filters.
     * 
     * @since 2.2
     */
    public boolean hasCurrent(final BatchTransformationRuleGroup rules) {
        return rules.stream().anyMatch(this::hasCurrent);
    }
    
    /**
     * Returns whether at least a single rule of the transformation can be fired using its default filters.
     * 
     * @since 2.2
     */
    public boolean hasCurrent() {
        return transformation.getTransformationRuleGroup().stream().anyMatch(this::hasCurrent);
    }
    
    /**
     * @deprecated This method was added to the API as an error, should not be available for transformation developers.
     */
    @Deprecated
    public <Match extends IPatternMatch> boolean disposeRule(final RuleSpecification<Match> ruleSpecification) {
        return disposeRuleInternal(ruleSpecification, ruleSpecification.createEmptyFilter());
    }

    /**
     * @deprecated This method was added to the API as an error, should not be available for transformation developers.
     */
    @Deprecated
    public <Match extends IPatternMatch> boolean disposeRule(final RuleSpecification<Match> ruleSpecification,
            final EventFilter<? super Match> filter) {
        return this.disposeRuleInternal(ruleSpecification, filter);
    }
    
    private <Match extends IPatternMatch> boolean disposeRuleInternal(final RuleSpecification<Match> ruleSpecification,
            final EventFilter<? super Match> filter) {
        return ruleEngine.removeRule(ruleSpecification, filter);
    }
    
    private void disposeRulesInternal(final BatchTransformationRuleGroup rules) {
        for (BatchTransformationRule<?, ?> rule : rules) {
            rule.unregisterRule(ruleEngine);
        }
    }
    
    /**
     * @deprecated This method was added to the API as an error, should not be available for transformation developers.
     */
    @Deprecated
    public void disposeRules(final BatchTransformationRuleGroup rules) {
        rules.stream().filter(Objects::nonNull).forEach(it -> it.unregisterRule(ruleEngine));
    }

    /**
     * Selects and fires an activation of the selected rule with the default filter of the rule.
     * </p>
     * 
     * <p>
     * <strong>Warning</strong>: the selection criteria undefined - it is neither random nor controllable
     */
    public <Match extends IPatternMatch> void fireOne(final BatchTransformationRule<Match, ?> rule) {
        executor.startExecution(FIRE_ONE_TRANSACTION_RULE_NAME + rule.getName());
        fireOne(rule.getRuleSpecification(), rule.getFilter());
        executor.endExecution(FIRE_ONE_TRANSACTION_RULE_NAME + rule.getName());
    }

    /**
     * Selects and fires an activation of the selected rule with a corresponding filter
     * </p>
     * 
     * <p>
     * <strong>Warning</strong>: the selection criteria is undefined - it is neither random nor controllable
     * @since 2.0
     */
    @SafeVarargs
    public final <Match extends IPatternMatch> void fireOne(final BatchTransformationRule<Match, ?> rule,
            final Entry<String, ?>... parameterFilter) {
        executor.startExecution((FIRE_ONE_TRANSACTION_FILTER_RULE_NAME + rule.getName()));
        fireOne(rule.getRuleSpecification(), new MatchParameterFilter(parameterFilter));
        executor.endExecution((FIRE_ONE_TRANSACTION_FILTER_RULE_NAME + rule.getName()));
    }

    /**
     * Selects and fires an activation of the selected rule with a corresponding filter.
     * </p>
     * 
     * <p>
     * <strong>Warning</strong>: the selection criteria is undefined - it is neither random nor controllable
     */
    public <Match extends IPatternMatch> void fireOne(final BatchTransformationRule<Match, ?> rule,
            final EventFilter<? super Match> filter) {
        executor.startExecution(FIRE_ONE_TRANSACTION_FILTER_RULE_NAME + rule.getName());
        fireOne(rule.getRuleSpecification(), filter);
        executor.endExecution(FIRE_ONE_TRANSACTION_FILTER_RULE_NAME + rule.getName());
    }

    private <Match extends IPatternMatch> boolean fireOne(final RuleSpecification<Match> ruleSpecification,
            final EventFilter<? super Match> filter) {
        registerRuleInternal(ruleSpecification, filter);
        final ScopedConflictSet conflictSet = ruleEngine.createScopedConflictSet(ruleSpecification, filter);
        conflictSet.getConflictingActivations().stream().findFirst()
                .ifPresent(head -> executor.execute(Collections.<Activation<?>>singleton(head).iterator()));
        conflictSet.dispose();
        return disposeRuleInternal(ruleSpecification, filter);
    }

    private <Match extends IPatternMatch> boolean fireAllCurrent(final RuleSpecification<Match> ruleSpecification,
            final EventFilter<? super Match> filter) {
        registerRuleInternal(ruleSpecification, filter);
        
        final ScopedConflictSet conflictSet = ruleEngine.createScopedConflictSet(ruleSpecification, filter);
        executor.execute(conflictSet.getConflictingActivations().iterator());
        
        conflictSet.dispose();
        return disposeRuleInternal(ruleSpecification, filter);
    }
    
    /**
     * Executes the selected rules with their default filter on its current match set of the precondition.
     * 
     * @since 2.2
     */
    public void fireAllCurrent(final BatchTransformationRuleGroup rules) {
        registerRulesInternal(rules);
        
        final ScopedConflictSet conflictSet = ruleEngine.createScopedConflictSet(rules.getFilteredRuleMap());
        executor.execute(conflictSet.getConflictingActivations().iterator());
        
        conflictSet.dispose();        
        disposeRulesInternal(rules);
    }
    
    /**
     * Executes the selected rules with their default filter on its current match set of the precondition.
     * 
     * @since 2.2
     */
    public void fireAllCurrent() {
        fireAllCurrent(transformation.getTransformationRuleGroup());
    }

    private <Match extends IPatternMatch> int countAllCurrent(final RuleSpecification<Match> ruleSpecification,
            final EventFilter<? super Match> filter) {
        registerRuleInternal(ruleSpecification, filter);
        final ScopedConflictSet conflictSet = ruleEngine.createScopedConflictSet(ruleSpecification, filter);
        int count = conflictSet.getConflictingActivations().size();
        conflictSet.dispose();
        
        disposeRuleInternal(ruleSpecification, filter);
        return count;
    }

    private <Match extends IPatternMatch> boolean fireUntil(final RuleSpecification<Match> ruleSpecification,
            final Predicate<Match> breakCondition, final EventFilter<? super Match> filter) {
        registerRuleInternal(ruleSpecification, filter);
        final ScopedConflictSet conflictSet = ruleEngine.createScopedConflictSet(ruleSpecification, filter);
        this.executor.execute(
                new ConflictSetIterator(ruleEngine.createScopedConflictSet(ruleSpecification, filter), breakCondition));
        conflictSet.dispose();
        return disposeRuleInternal(ruleSpecification, filter);
    }

}