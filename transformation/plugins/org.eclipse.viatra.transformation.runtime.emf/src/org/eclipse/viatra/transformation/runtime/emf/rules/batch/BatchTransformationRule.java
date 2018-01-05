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

import java.util.Collections;

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

/**
 * Wrapper class for transformation rule definition to hide EVM specific internals.
 * 
 * Subclasses can simply override the abstract precondition and model manipulation methods.
 * 
 * @author Abel Hegedus, Zoltan Ujhelyi
 */
public class BatchTransformationRule<MATCH extends IPatternMatch, MATCHER extends ViatraQueryMatcher<MATCH>>
        implements ITransformationRule<MATCH, MATCHER> {
    /**
     * Lifecycle for a rule that does not store the list of fired activations; thus allows re-firing the same activation
     * again.
     */
    public final static ActivationLifeCycle STATELESS_RULE_LIFECYCLE = getStatelessRuleLifecycle();
            
    private static ActivationLifeCycle getStatelessRuleLifecycle() {
        ActivationLifeCycle cycle = ActivationLifeCycle.create(CRUDActivationStateEnum.INACTIVE);
        cycle.addStateTransition(CRUDActivationStateEnum.INACTIVE, CRUDEventTypeEnum.CREATED,
                CRUDActivationStateEnum.CREATED);
        cycle.addStateTransition(CRUDActivationStateEnum.CREATED, EventType.RuleEngineEventType.FIRE,
                CRUDActivationStateEnum.CREATED);
        cycle.addStateTransition(CRUDActivationStateEnum.CREATED, CRUDEventTypeEnum.DELETED,
                CRUDActivationStateEnum.INACTIVE);
        return UnmodifiableActivationLifeCycle.copyOf(cycle);
    }

    /**
     * Lifecycle for a rule that stores the list of fired activations; thus effectively forbids re-firing the same
     * activation.
     */
    public final static ActivationLifeCycle STATEFUL_RULE_LIFECYCLE = Lifecycles.getDefault(false, false);

    protected String ruleName;

    private final ActivationLifeCycle lifecycle;

    private RuleSpecification<MATCH> ruleSpec;

    private final IQuerySpecification<MATCHER> precondition;

    private final IMatchProcessor<MATCH> action;

    private final EventFilter<MATCH> filter;

    protected BatchTransformationRule() {
        this("", null, BatchTransformationRule.STATELESS_RULE_LIFECYCLE, null);
    }

    @SuppressWarnings("unchecked")
    public BatchTransformationRule(final String rulename, final IQuerySpecification<MATCHER> matcher,
            final ActivationLifeCycle lifecycle, final IMatchProcessor<MATCH> action) {
        this(rulename, matcher, lifecycle, action,
                ViatraQueryMatchEventFilter.createFilter(((MATCH) matcher.newEmptyMatch().toImmutable())));
    }

    public BatchTransformationRule(final String rulename, final IQuerySpecification<MATCHER> matcher,
            final ActivationLifeCycle lifecycle, final IMatchProcessor<MATCH> action, final EventFilter<MATCH> filter) {
        this.ruleName = rulename;
        this.precondition = matcher;
        this.action = action;
        this.lifecycle = lifecycle;
        this.filter = filter;
    }

    public BatchTransformationRule(final BatchTransformationRule<MATCH, MATCHER> rule,
            final EventFilter<MATCH> filter) {
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
    public RuleSpecification<MATCH> getRuleSpecification() {
        if (ruleSpec == null) {
            final Job<MATCH> job = Jobs.newStatelessJob(CRUDActivationStateEnum.CREATED, action);
            ruleSpec = Rules.newMatcherRuleSpecification(precondition, lifecycle, Collections.singleton(job),
                    getName());
        }
        return ruleSpec;
    }

    /**
     * Returns the query specification representing the pattern used as a precondition.
     */
    @Override
    public IQuerySpecification<MATCHER> getPrecondition() {
        return precondition;
    }

    /**
     * Return an IMatchProcessor representing the model manipulation executed by the rule.
     */
    public IMatchProcessor<MATCH> getAction() {
        return action;
    }

    @Override
    public EventFilter<? super MATCH> getFilter() {
        return filter;
    }
}
