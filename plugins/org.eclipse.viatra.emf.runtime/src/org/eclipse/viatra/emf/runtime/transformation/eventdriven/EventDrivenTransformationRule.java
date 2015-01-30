/*******************************************************************************
 * Copyright (c) 2004-2013, Istvan David, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan David - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.emf.runtime.transformation.eventdriven;

import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.evm.api.ActivationLifeCycle;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.specific.Jobs;
import org.eclipse.incquery.runtime.evm.specific.Rules;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum;
import org.eclipse.viatra.emf.runtime.rules.ITransformationRule;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class EventDrivenTransformationRule<Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>>
        implements ITransformationRule<Match, Matcher> {
    private IQuerySpecification<Matcher> precondition;
    private RuleSpecification<Match> ruleSpecification;

    public EventDrivenTransformationRule(String name, IQuerySpecification<Matcher> precondition,
            Multimap<IncQueryActivationStateEnum, IMatchProcessor<Match>> stateActions, ActivationLifeCycle lifeCycle) {
        Set<Job<Match>> jobs = Sets.newHashSet();

        for (Entry<IncQueryActivationStateEnum, IMatchProcessor<Match>> stateAction : stateActions.entries()) {
            IncQueryActivationStateEnum state = stateAction.getKey();
            IMatchProcessor<Match> action = stateAction.getValue();

            jobs.add(Jobs.newStatelessJob(state, action));
        }

        ruleSpecification = Rules.newMatcherRuleSpecification(precondition, lifeCycle, jobs);
    }

    @Override
    public RuleSpecification<Match> getRuleSpecification() {
        return ruleSpecification;
    }

    @Override
    public IQuerySpecification<Matcher> getPrecondition() {
        return precondition;
    }
}
