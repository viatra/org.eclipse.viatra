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
package org.eclipse.viatra2.emf.runtime.transformation.eventdriven;

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
import org.eclipse.viatra2.emf.runtime.rules.ITransformationRule;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;

@SuppressWarnings("unchecked")
public class EventDrivenTransformationRule<Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>>
        implements ITransformationRule<Match, Matcher> {
    private IQuerySpecification<Matcher> precondition;
    private RuleSpecification<Match> ruleSpecification;

    public EventDrivenTransformationRule(String name, IQuerySpecification<Matcher> precondition,
            IMatchProcessor<Match> action, ActivationLifeCycle lifeCycle) {
        Job<Match> job = Jobs.newStatelessJob(IncQueryActivationStateEnum.APPEARED, action);
        
        ruleSpecification = Rules.newMatcherRuleSpecification(precondition, lifeCycle,
                CollectionLiterals.newHashSet(job));
    }

    public EventDrivenTransformationRule(String name, IQuerySpecification<Matcher> precondition,
            IMatchProcessor<Match> actionOnAppear, IMatchProcessor<Match> actionOnDisappear,
            ActivationLifeCycle lifeCycle) {
        Job<Match> jobOnAppear = Jobs.newStatelessJob(IncQueryActivationStateEnum.APPEARED, actionOnAppear);
        Job<Match> jobOnDisappear = Jobs.newStatelessJob(IncQueryActivationStateEnum.DISAPPEARED, actionOnDisappear);
        
        ruleSpecification = Rules.newMatcherRuleSpecification(precondition, lifeCycle,
                CollectionLiterals.newHashSet(jobOnAppear, jobOnDisappear));
    }

    public EventDrivenTransformationRule(String name, IQuerySpecification<Matcher> precondition,
            IMatchProcessor<Match> action, IncQueryActivationStateEnum transition, ActivationLifeCycle lifeCycle) {
        Job<Match> job = Jobs.newStatelessJob(transition, action);
        ruleSpecification = Rules.newMatcherRuleSpecification(precondition, lifeCycle,
                CollectionLiterals.newHashSet(job));
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
