/**
 * Copyright (c) 2004-2013, Abel Hegedus, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Abel Hegedus, Zoltan Ujhelyi - initial API and implementation
 */
package org.eclipse.viatra.transformation.runtime.emf.rules.batch;

import java.util.HashSet;
import org.eclipse.viatra.query.runtime.api.IMatchProcessor;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.transformation.evm.api.ActivationLifeCycle;
import org.eclipse.viatra.transformation.evm.api.Job;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.viatra.transformation.evm.api.event.EventType;
import org.eclipse.viatra.transformation.evm.specific.Jobs;
import org.eclipse.viatra.transformation.evm.specific.Lifecycles;
import org.eclipse.viatra.transformation.evm.specific.Rules;
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDActivationStateEnum;
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDEventTypeEnum;
import org.eclipse.viatra.transformation.evm.specific.event.ViatraQueryMatchEventFilter;
import org.eclipse.viatra.transformation.evm.specific.lifecycle.UnmodifiableActivationLifeCycle;
import org.eclipse.viatra.transformation.runtime.emf.rules.ITransformationRule;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function0;

/**
 * Wrapper class for transformation rule definition to hide EVM specific internals.
 * 
 * Subclasses can simply override the abstract precondition and model manipulation methods.
 * 
 * @author Abel Hegedus, Zoltan Ujhelyi
 */
@SuppressWarnings("all")
public class BatchTransformationRule<Match extends IPatternMatch, Matcher extends ViatraQueryMatcher<Match>> implements ITransformationRule<Match, Matcher> {
  /**
   * Lifecycle for a rule that does not store the list of fired activations; thus allows re-firing the same activation again.
   */
  public final static ActivationLifeCycle STATELESS_RULE_LIFECYCLE = new Function0<ActivationLifeCycle>() {
    public ActivationLifeCycle apply() {
      UnmodifiableActivationLifeCycle _xblockexpression = null;
      {
        final ActivationLifeCycle cycle = ActivationLifeCycle.create(CRUDActivationStateEnum.INACTIVE);
        cycle.addStateTransition(CRUDActivationStateEnum.INACTIVE, CRUDEventTypeEnum.CREATED, CRUDActivationStateEnum.CREATED);
        cycle.addStateTransition(CRUDActivationStateEnum.CREATED, EventType.RuleEngineEventType.FIRE, CRUDActivationStateEnum.CREATED);
        cycle.addStateTransition(CRUDActivationStateEnum.CREATED, CRUDEventTypeEnum.DELETED, CRUDActivationStateEnum.INACTIVE);
        _xblockexpression = UnmodifiableActivationLifeCycle.copyOf(cycle);
      }
      return _xblockexpression;
    }
  }.apply();
  
  /**
   * Lifecycle for a rule that stores the list of fired activations; thus effectively forbids re-firing the same activation.
   */
  public final static ActivationLifeCycle STATEFUL_RULE_LIFECYCLE = Lifecycles.getDefault(false, false);
  
  protected String ruleName;
  
  private final ActivationLifeCycle lifecycle;
  
  private RuleSpecification<Match> ruleSpec;
  
  private final IQuerySpecification<Matcher> precondition;
  
  private final IMatchProcessor<Match> action;
  
  private final EventFilter<Match> filter;
  
  protected BatchTransformationRule() {
    this("", null, BatchTransformationRule.STATELESS_RULE_LIFECYCLE, null);
  }
  
  public BatchTransformationRule(final String rulename, final IQuerySpecification<Matcher> matcher, final ActivationLifeCycle lifecycle, final IMatchProcessor<Match> action) {
    this(rulename, matcher, lifecycle, action, ViatraQueryMatchEventFilter.<Match>createFilter(((Match) matcher.newEmptyMatch().toImmutable())));
  }
  
  public BatchTransformationRule(final String rulename, final IQuerySpecification<Matcher> matcher, final ActivationLifeCycle lifecycle, final IMatchProcessor<Match> action, final EventFilter<Match> filter) {
    this.ruleName = rulename;
    this.precondition = matcher;
    this.action = action;
    this.lifecycle = lifecycle;
    this.filter = filter;
  }
  
  public BatchTransformationRule(final BatchTransformationRule<Match, Matcher> rule, final EventFilter<Match> filter) {
    this.ruleName = rule.ruleName;
    this.precondition = rule.precondition;
    this.action = rule.action;
    this.lifecycle = rule.lifecycle;
    this.filter = filter;
  }
  
  @Override
  public String getName() {
    return this.ruleName;
  }
  
  /**
   * Returns a RuleSpecification that can be added to a rule engine.
   */
  @Override
  public RuleSpecification<Match> getRuleSpecification() {
    RuleSpecification<Match> _xblockexpression = null;
    {
      if ((this.ruleSpec == null)) {
        final IQuerySpecification<Matcher> querySpec = this.precondition;
        final Job<Match> job = Jobs.<Match>newStatelessJob(CRUDActivationStateEnum.CREATED, this.action);
        HashSet<Job<Match>> _newHashSet = CollectionLiterals.<Job<Match>>newHashSet(job);
        String _name = this.getName();
        RuleSpecification<Match> _newMatcherRuleSpecification = Rules.<Match>newMatcherRuleSpecification(querySpec, this.lifecycle, _newHashSet, _name);
        this.ruleSpec = _newMatcherRuleSpecification;
      }
      _xblockexpression = this.ruleSpec;
    }
    return _xblockexpression;
  }
  
  /**
   * Returns the query specification representing the pattern used as a precondition.
   */
  @Override
  public IQuerySpecification<Matcher> getPrecondition() {
    return this.precondition;
  }
  
  /**
   * Return an IMatchProcessor representing the model manipulation executed by the rule.
   */
  public IMatchProcessor<Match> getAction() {
    return this.action;
  }
  
  @Override
  public EventFilter<? super Match> getFilter() {
    return this.filter;
  }
}
