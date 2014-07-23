/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.api;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.specific.Jobs;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryEventRealm;
import org.eclipse.incquery.runtime.evm.specific.lifecycle.DefaultActivationLifeCycle;

/**
 * An instance of this class is a specification of a graph transformation rule on a given metamodel. Such a rule
 * consists of a left hand side (LHS), which is specified by an {@link IPatternMatch} and a right hand side (RHS), which
 * is specified by an {@link IMatchProcessor}.
 * 
 * @author Andras Szabolcs Nagy
 * 
 * @param <P>
 *            An IncQuery pattern match - left hand side of the rule
 */
public class TransformationRule<P extends IPatternMatch> extends RuleSpecification<P> {

    public interface ActivationCostProcessor<P> {

        public Map<String, Double> process(P match);

    }

    private IQuerySpecification<? extends IncQueryMatcher<P>> querySpecification;
    private IMatchProcessor<P> matchProcessor;

    private String name;
    private RuleMetaData metaData;

    private Map<String, Double> costs;
    private ActivationCostProcessor<P> activationCostProcessor;

    public TransformationRule(IQuerySpecification<? extends IncQueryMatcher<P>> querySpecification,
            IMatchProcessor<P> matchProcessor, RuleMetaData metaData) {
        super(IncQueryEventRealm.createSourceSpecification(querySpecification),
                DefaultActivationLifeCycle.DEFAULT_NO_UPDATE_AND_DISAPPEAR, TransformationRule
                        .<P> getJobs(matchProcessor));

        checkArgument(querySpecification != null);
        checkArgument(matchProcessor != null);

        this.querySpecification = querySpecification;
        this.matchProcessor = matchProcessor;
        this.metaData = metaData;

        this.name = querySpecification.getFullyQualifiedName() + "Rule";
    }

    public TransformationRule(IQuerySpecification<? extends IncQueryMatcher<P>> querySpecification,
            IMatchProcessor<P> matchProcessor) {
        this(querySpecification, matchProcessor, null);
    }

    // Helper method for constructor
    private static <M extends IPatternMatch> Set<Job<M>> getJobs(IMatchProcessor<M> matchProcessor) {
        Job<M> appearedMatch = Jobs.newStatelessJob(IncQueryActivationStateEnum.APPEARED, matchProcessor);
        Job<M> firedMatch = Jobs.newStatelessJob(IncQueryActivationStateEnum.FIRED, matchProcessor);

        Set<Job<M>> jobs = new HashSet<Job<M>>();

        jobs.add(appearedMatch);
        jobs.add(firedMatch);

        return jobs;
    }

    public Map<String, Double> measureCosts(IPatternMatch match) {
        if (activationCostProcessor != null) {
            @SuppressWarnings("unchecked")
            P typedMatch = (P) match;
            return activationCostProcessor.process(typedMatch);
        } else {
            return null;
        }
    }

    // ****** getters & setters
    // ****************************

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public IMatchProcessor<P> getMatchProcessor() {
        return matchProcessor;
    }

    public IQuerySpecification<? extends IncQueryMatcher<P>> getQuerySpecification() {
        return querySpecification;
    }

    public RuleMetaData getMetaData() {
        return metaData;
    }

    public Map<String, Double> getCosts() {
        return costs;
    }

    public void setCosts(Map<String, Double> costs) {
        this.costs = costs;
    }

    public ActivationCostProcessor<P> getActivationCostProcessor() {
        return activationCostProcessor;
    }

    public void setActivationCostProcessor(ActivationCostProcessor<P> activationCostProcessor) {
        this.activationCostProcessor = activationCostProcessor;
    }
}
