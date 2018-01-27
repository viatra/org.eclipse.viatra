/**
 * Copyright (c) 2004-2013, Abel Hegedus, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Abel Hegedus, Zoltan Ujhelyi - initial API and implementation
 *   Peter Lunk - revised Transformation API structure for adapter support
 */
package org.eclipse.viatra.transformation.runtime.emf.transformation.batch;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.viatra.query.runtime.api.GenericQueryGroup;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQueryGroup;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
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
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * Utility class for simple rule usage
 * 
 * @author Abel Hegedus, Zoltan Ujhelyi, Peter Lunk
 */
@SuppressWarnings("all")
public class BatchTransformationStatements {
  private final ViatraQueryEngine queryEngine;
  
  private final RuleEngine ruleEngine;
  
  private final IExecutor executor;
  
  BatchTransformationStatements(final BatchTransformation transformation, final IExecutor executor) {
    this.ruleEngine = transformation.ruleEngine;
    this.executor = executor;
    this.queryEngine = transformation.queryEngine;
  }
  
  /**
   * Executes the selected rule with the selected filter as long as there
   * are possible matches of its preconditions and the break condition is
   * not fulfilled. The matches are executed one-by-one, in case of conflicts
   * only one of the conflicting matches will cause an execution.
   */
  public <Match extends IPatternMatch> void fireUntil(final BatchTransformationRule<Match, ?> rule, final Predicate<Match> breakCondition) {
    String _name = rule.getName();
    String _plus = ("Fire_until_transaction_condition_ruleName: " + _name);
    this.executor.startExecution(_plus);
    RuleSpecification<Match> _ruleSpecification = rule.getRuleSpecification();
    final EventFilter<Match> filter = _ruleSpecification.createEmptyFilter();
    RuleSpecification<Match> _ruleSpecification_1 = rule.getRuleSpecification();
    this.<Match>fireUntil(_ruleSpecification_1, breakCondition, filter);
    String _name_1 = rule.getName();
    String _plus_1 = ("Fire_until_transaction_condition_ruleName: " + _name_1);
    this.executor.endExecution(_plus_1);
  }
  
  /**
   * Executes the selected rule with the selected filter as long as there
   * are possible matches of its precondition and the break condition is
   * not fulfilled. The matches are executed one-by-one, in case of conflicts
   * only one of the conflicting matches will cause an execution.
   */
  public <Match extends IPatternMatch> void fireUntil(final BatchTransformationRule<Match, ?> rule, final Predicate<Match> breakCondition, final Pair<String, ?>... filterParameters) {
    String _name = rule.getName();
    String _plus = ("Fire_until_transaction_filter_condition_ruleName: " + _name);
    this.executor.startExecution(_plus);
    RuleSpecification<Match> _ruleSpecification = rule.getRuleSpecification();
    MatchParameterFilter _matchParameterFilter = new MatchParameterFilter(filterParameters);
    this.<Match>fireUntil(_ruleSpecification, breakCondition, _matchParameterFilter);
    String _name_1 = rule.getName();
    String _plus_1 = ("Fire_until_transaction_filter_condition_ruleName: " + _name_1);
    this.executor.endExecution(_plus_1);
  }
  
  /**
   * Executes the selected rule with the selected filter as long as there
   * are possible matches of its precondition and the break condition is
   * not fulfilled. The matches are executed one-by-one, in case of conflicts
   * only one of the conflicting matches will cause an execution.
   */
  public <Match extends IPatternMatch> void fireUntil(final BatchTransformationRule<Match, ?> rule, final Predicate<Match> breakCondition, final EventFilter<? super Match> filter) {
    String _name = rule.getName();
    String _plus = ("Fire_until_transaction_filter_condition_ruleName: " + _name);
    this.executor.startExecution(_plus);
    RuleSpecification<Match> _ruleSpecification = rule.getRuleSpecification();
    this.<Match>fireUntil(_ruleSpecification, breakCondition, filter);
    String _name_1 = rule.getName();
    String _plus_1 = ("Fire_until_transaction_filter_condition_ruleName: " + _name_1);
    this.executor.endExecution(_plus_1);
  }
  
  /**
   * Executes the selected rules with the selected filter as long as there
   * are possible matches of any of their preconditions and the break condition is
   * not fulfilled. The matches are executed one-by-one, in case of conflicts
   * only one of the conflicting matches will cause an execution.
   */
  public void fireUntil(final BatchTransformationRuleGroup rules, final Predicate<IPatternMatch> breakCondition) {
    this.executor.startExecution("Fire_until_transaction_condition_ruleGroup");
    this.registerRules(rules);
    Multimap<RuleSpecification<?>, EventFilter<?>> _filteredRuleMap = rules.getFilteredRuleMap();
    final ScopedConflictSet conflictSet = this.ruleEngine.createScopedConflictSet(_filteredRuleMap);
    this.<IPatternMatch>fireUntil(conflictSet, breakCondition);
    conflictSet.dispose();
    this.executor.endExecution("Fire_until_transaction_condition_ruleGroup");
  }
  
  /**
   * Executes the selected rule with the selected filter as long as there
   * are possible matches of its precondition. The matches are executed
   * one-by-one, in case of conflicts only one of the conflicting matches
   * will cause an execution.
   */
  public <Match extends IPatternMatch> void fireWhilePossible(final BatchTransformationRule<Match, ?> rule) {
    String _name = rule.getName();
    String _plus = ("Fire_while_possible_transaction_ruleName: " + _name);
    this.executor.startExecution(_plus);
    final Predicate<Match> _function = new Predicate<Match>() {
      @Override
      public boolean apply(final Match it) {
        return false;
      }
    };
    this.<Match>fireUntil(rule, _function);
    String _name_1 = rule.getName();
    String _plus_1 = ("Fire_while_possible_transaction_ruleName: " + _name_1);
    this.executor.endExecution(_plus_1);
  }
  
  /**
   * Executes the selected rule with the selected filter as long as there
   * are possible matches of its preconditions. The matches are executed
   * one-by-one, in case of conflicts only one of the conflicting matches
   * will cause an execution.
   */
  public <Match extends IPatternMatch> void fireWhilePossible(final BatchTransformationRule<Match, ?> rule, final EventFilter<? super Match> filter) {
    String _name = rule.getName();
    String _plus = ("Fire_while_possible_transaction_filter_ruleName: " + _name);
    this.executor.startExecution(_plus);
    final Predicate<Match> _function = new Predicate<Match>() {
      @Override
      public boolean apply(final Match it) {
        return false;
      }
    };
    this.<Match>fireUntil(rule, _function, filter);
    String _name_1 = rule.getName();
    String _plus_1 = ("Fire_while_possible_transaction_filter_ruleName: " + _name_1);
    this.executor.endExecution(_plus_1);
  }
  
  /**
   * Executes the selected rules with the selected filter as long as there
   * are possible matches of any of their preconditions. The matches are
   * executed one-by-one, in case of conflicts only one of the conflicting
   * matches will cause an execution.
   */
  public void fireWhilePossible(final BatchTransformationRuleGroup rules) {
    this.executor.startExecution("Fire_while_possible_transaction_ruleGroup");
    final Predicate<IPatternMatch> _function = new Predicate<IPatternMatch>() {
      @Override
      public boolean apply(final IPatternMatch it) {
        return false;
      }
    };
    this.fireUntil(rules, _function);
    this.executor.endExecution("Fire_while_possible_transaction_ruleGroup");
  }
  
  /**
   * Executes the selected rule with the selected filter on its current match set of the precondition.
   */
  public <Match extends IPatternMatch> void fireAllCurrent(final BatchTransformationRule<Match, ?> rule) {
    String _name = rule.getName();
    String _plus = ("Fire_all_current_transaction_ruleName: " + _name);
    this.executor.startExecution(_plus);
    RuleSpecification<Match> _ruleSpecification = rule.getRuleSpecification();
    final EventFilter<Match> filter = _ruleSpecification.createEmptyFilter();
    RuleSpecification<Match> _ruleSpecification_1 = rule.getRuleSpecification();
    this.<Match>fireAllCurrent(_ruleSpecification_1, filter);
    String _name_1 = rule.getName();
    String _plus_1 = ("Fire_all_current_transaction_ruleName: " + _name_1);
    this.executor.endExecution(_plus_1);
  }
  
  /**
   * Executes the selected rule with the selected filter on its current match set of the precondition.
   */
  public <Match extends IPatternMatch> void fireAllCurrent(final BatchTransformationRule<Match, ?> rule, final Pair<String, ?>... parameterFilter) {
    String _name = rule.getName();
    String _plus = ("Fire_all_current_transaction_filter_ruleName: " + _name);
    this.executor.startExecution(_plus);
    RuleSpecification<Match> _ruleSpecification = rule.getRuleSpecification();
    MatchParameterFilter _matchParameterFilter = new MatchParameterFilter(parameterFilter);
    this.<Match>fireAllCurrent(_ruleSpecification, _matchParameterFilter);
    String _name_1 = rule.getName();
    String _plus_1 = ("Fire_all_current_transaction_filter_ruleName: " + _name_1);
    this.executor.endExecution(_plus_1);
  }
  
  /**
   * Executes the selected rule with the selected filter on its current match set of the precondition.
   */
  public <Match extends IPatternMatch> void fireAllCurrent(final BatchTransformationRule<Match, ?> rule, final EventFilter<? super Match> filter) {
    String _name = rule.getName();
    String _plus = ("Fire_all_current_transaction_filter_ruleName: " + _name);
    this.executor.startExecution(_plus);
    RuleSpecification<Match> _ruleSpecification = rule.getRuleSpecification();
    this.<Match>fireAllCurrent(_ruleSpecification, filter);
    String _name_1 = rule.getName();
    String _plus_1 = ("Fire_all_current_transaction_filter_ruleName: " + _name_1);
    this.executor.endExecution(_plus_1);
  }
  
  public <Match extends IPatternMatch> boolean registerRule(final RuleSpecification<Match> ruleSpecification) {
    EventFilter<Match> _createEmptyFilter = ruleSpecification.createEmptyFilter();
    return this.<Match>registerRule(ruleSpecification, _createEmptyFilter);
  }
  
  public <Match extends IPatternMatch> boolean registerRule(final RuleSpecification<Match> ruleSpecification, final EventFilter<? super Match> filter) {
    return this.ruleEngine.<Match>addRule(ruleSpecification, filter);
  }
  
  public void registerRules(final BatchTransformationRuleGroup rules) {
    try {
      Iterable<BatchTransformationRule<?, ?>> _filterNull = IterableExtensions.<BatchTransformationRule<?, ?>>filterNull(rules);
      final Function1<BatchTransformationRule<?, ?>, IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> _function = new Function1<BatchTransformationRule<?, ?>, IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>>() {
        @Override
        public IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> apply(final BatchTransformationRule<?, ?> it) {
          return it.getPrecondition();
        }
      };
      final Iterable<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> notNullPreconditions = IterableExtensions.<BatchTransformationRule<?, ?>, IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>>map(_filterNull, _function);
      HashSet<IQuerySpecification<?>> _newHashSet = Sets.<IQuerySpecification<?>>newHashSet(notNullPreconditions);
      IQueryGroup _of = GenericQueryGroup.of(_newHashSet);
      _of.prepare(this.queryEngine);
      Iterable<BatchTransformationRule<?, ?>> _filterNull_1 = IterableExtensions.<BatchTransformationRule<?, ?>>filterNull(rules);
      final Procedure1<BatchTransformationRule<?, ?>> _function_1 = new Procedure1<BatchTransformationRule<?, ?>>() {
        @Override
        public void apply(final BatchTransformationRule<?, ?> it) {
          RuleSpecification<? extends IPatternMatch> _ruleSpecification = it.getRuleSpecification();
          EventFilter<?> _filter = it.getFilter();
          BatchTransformationStatements.this.ruleEngine.addRule(_ruleSpecification, ((EventFilter<IPatternMatch>) _filter));
        }
      };
      IterableExtensions.<BatchTransformationRule<?, ?>>forEach(_filterNull_1, _function_1);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  /**
   * Returns the number of current activations of the rule.
   * 
   * @since 1.5
   */
  public <Match extends IPatternMatch> int countAllCurrent(final BatchTransformationRule<Match, ?> rule) {
    int _xblockexpression = (int) 0;
    {
      RuleSpecification<Match> _ruleSpecification = rule.getRuleSpecification();
      final EventFilter<Match> filter = _ruleSpecification.createEmptyFilter();
      RuleSpecification<Match> _ruleSpecification_1 = rule.getRuleSpecification();
      _xblockexpression = this.<Match>countAllCurrent(_ruleSpecification_1, filter);
    }
    return _xblockexpression;
  }
  
  /**
   * Returns the number of current activations of the rule.
   * 
   * @since 1.5
   */
  public <Match extends IPatternMatch> int countAllCurrent(final BatchTransformationRule<Match, ?> rule, final Pair<String, ?>... parameterFilter) {
    RuleSpecification<Match> _ruleSpecification = rule.getRuleSpecification();
    MatchParameterFilter _matchParameterFilter = new MatchParameterFilter(parameterFilter);
    return this.<Match>countAllCurrent(_ruleSpecification, _matchParameterFilter);
  }
  
  /**
   * Returns the number of current activations of the rule.
   * 
   * @since 1.5
   */
  public <Match extends IPatternMatch> int countAllCurrent(final BatchTransformationRule<Match, ?> rule, final EventFilter<? super Match> filter) {
    RuleSpecification<Match> _ruleSpecification = rule.getRuleSpecification();
    return this.<Match>countAllCurrent(_ruleSpecification, filter);
  }
  
  public <Match extends IPatternMatch> boolean disposeRule(final RuleSpecification<Match> ruleSpecification) {
    EventFilter<Match> _createEmptyFilter = ruleSpecification.createEmptyFilter();
    return this.<Match>disposeRule(ruleSpecification, _createEmptyFilter);
  }
  
  public <Match extends IPatternMatch> boolean disposeRule(final RuleSpecification<Match> ruleSpecification, final EventFilter<? super Match> filter) {
    return this.ruleEngine.<Match>removeRule(ruleSpecification, filter);
  }
  
  public void disposeRules(final BatchTransformationRuleGroup rules) {
    Iterable<BatchTransformationRule<?, ?>> _filterNull = IterableExtensions.<BatchTransformationRule<?, ?>>filterNull(rules);
    final Procedure1<BatchTransformationRule<?, ?>> _function = new Procedure1<BatchTransformationRule<?, ?>>() {
      @Override
      public void apply(final BatchTransformationRule<?, ?> it) {
        RuleSpecification<? extends IPatternMatch> _ruleSpecification = it.getRuleSpecification();
        EventFilter<?> _filter = it.getFilter();
        BatchTransformationStatements.this.ruleEngine.removeRule(_ruleSpecification, ((EventFilter<IPatternMatch>) _filter));
      }
    };
    IterableExtensions.<BatchTransformationRule<?, ?>>forEach(_filterNull, _function);
  }
  
  /**
   * Selects and fires an activation of the selected rule with no corresponding filter.</p>
   * 
   * <p><strong>Warning</strong>: the selection criteria is not specified - it is neither random nor controllable
   */
  public <Match extends IPatternMatch> void fireOne(final BatchTransformationRule<Match, ?> rule) {
    String _name = rule.getName();
    String _plus = ("Fire_one_transaction_ruleName: " + _name);
    this.executor.startExecution(_plus);
    RuleSpecification<Match> _ruleSpecification = rule.getRuleSpecification();
    final EventFilter<Match> filter = _ruleSpecification.createEmptyFilter();
    RuleSpecification<Match> _ruleSpecification_1 = rule.getRuleSpecification();
    this.<Match>fireOne(_ruleSpecification_1, filter);
    String _name_1 = rule.getName();
    String _plus_1 = ("Fire_one_transaction_ruleName: " + _name_1);
    this.executor.endExecution(_plus_1);
  }
  
  /**
   * Selects and fires an activation of the selected rule with a corresponding filter</p>
   * 
   * <p><strong>Warning</strong>: the selection criteria is not specified - it is neither random nor controllable
   */
  public <Match extends IPatternMatch> void fireOne(final BatchTransformationRule<Match, ?> rule, final Pair<String, ?>... parameterFilter) {
    String _name = rule.getName();
    String _plus = ("Fire_one_transaction_filter_ruleName: " + _name);
    this.executor.startExecution(_plus);
    RuleSpecification<Match> _ruleSpecification = rule.getRuleSpecification();
    MatchParameterFilter _matchParameterFilter = new MatchParameterFilter(parameterFilter);
    this.<Match>fireOne(_ruleSpecification, _matchParameterFilter);
    String _name_1 = rule.getName();
    String _plus_1 = ("Fire_one_transaction_filter_ruleName: " + _name_1);
    this.executor.endExecution(_plus_1);
  }
  
  /**
   * Selects and fires an activation of the selected rule with a corresponding filter.</p>
   * 
   * <p><strong>Warning</strong>: the selection criteria is not specified - it is neither random nor controllable
   */
  public <Match extends IPatternMatch> void fireOne(final BatchTransformationRule<Match, ?> rule, final EventFilter<? super Match> filter) {
    String _name = rule.getName();
    String _plus = ("Fire_one_transaction_filter_ruleName: " + _name);
    this.executor.startExecution(_plus);
    RuleSpecification<Match> _ruleSpecification = rule.getRuleSpecification();
    this.<Match>fireOne(_ruleSpecification, filter);
    String _name_1 = rule.getName();
    String _plus_1 = ("Fire_one_transaction_filter_ruleName: " + _name_1);
    this.executor.endExecution(_plus_1);
  }
  
  private <Match extends IPatternMatch> boolean fireOne(final RuleSpecification<Match> ruleSpecification, final EventFilter<? super Match> filter) {
    boolean _xblockexpression = false;
    {
      this.<Match>registerRule(ruleSpecification, filter);
      final ScopedConflictSet conflictSet = this.ruleEngine.<Match>createScopedConflictSet(ruleSpecification, filter);
      Set<Activation<?>> _conflictingActivations = conflictSet.getConflictingActivations();
      Activation<?> _head = IterableExtensions.<Activation<?>>head(_conflictingActivations);
      final UnmodifiableIterator<Activation<?>> iterator = Iterators.<Activation<?>>singletonIterator(((Activation<?>) _head));
      this.executor.execute(iterator);
      conflictSet.dispose();
      _xblockexpression = this.<Match>disposeRule(ruleSpecification, filter);
    }
    return _xblockexpression;
  }
  
  private <Match extends IPatternMatch> boolean fireAllCurrent(final RuleSpecification<Match> ruleSpecification, final EventFilter<? super Match> filter) {
    boolean _xblockexpression = false;
    {
      this.<Match>registerRule(ruleSpecification, filter);
      final ScopedConflictSet conflictSet = this.ruleEngine.<Match>createScopedConflictSet(ruleSpecification, filter);
      Set<Activation<?>> _conflictingActivations = conflictSet.getConflictingActivations();
      List<Activation<?>> list = CollectionLiterals.<Activation<?>>newArrayList(((Activation<?>[])Conversions.unwrapArray(_conflictingActivations, Activation.class)));
      Iterator<Activation<?>> _iterator = list.iterator();
      this.executor.execute(_iterator);
      conflictSet.dispose();
      _xblockexpression = this.<Match>disposeRule(ruleSpecification, filter);
    }
    return _xblockexpression;
  }
  
  private <Match extends IPatternMatch> int countAllCurrent(final RuleSpecification<Match> ruleSpecification, final EventFilter<? super Match> filter) {
    this.<Match>registerRule(ruleSpecification, filter);
    final ScopedConflictSet conflictSet = this.ruleEngine.<Match>createScopedConflictSet(ruleSpecification, filter);
    Set<Activation<?>> _conflictingActivations = conflictSet.getConflictingActivations();
    int count = _conflictingActivations.size();
    conflictSet.dispose();
    this.<Match>disposeRule(ruleSpecification, filter);
    return count;
  }
  
  private <Match extends IPatternMatch> boolean fireUntil(final RuleSpecification<Match> ruleSpecification, final Predicate<Match> breakCondition, final EventFilter<? super Match> filter) {
    boolean _xblockexpression = false;
    {
      this.<Match>registerRule(ruleSpecification, filter);
      final ScopedConflictSet conflictSet = this.ruleEngine.<Match>createScopedConflictSet(ruleSpecification, filter);
      ScopedConflictSet _createScopedConflictSet = this.ruleEngine.<Match>createScopedConflictSet(ruleSpecification, filter);
      ConflictSetIterator _conflictSetIterator = new ConflictSetIterator(_createScopedConflictSet, breakCondition);
      this.executor.execute(_conflictSetIterator);
      conflictSet.dispose();
      _xblockexpression = this.<Match>disposeRule(ruleSpecification, filter);
    }
    return _xblockexpression;
  }
  
  private <Match extends IPatternMatch> void fireUntil(final ScopedConflictSet conflictSet, final Predicate<Match> breakCondition) {
    ConflictSetIterator _conflictSetIterator = new ConflictSetIterator(conflictSet, breakCondition);
    this.executor.execute(_conflictSetIterator);
  }
}
