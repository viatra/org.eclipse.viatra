/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.databinding.runtime.collection;

import java.util.Set;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.IncQueryEngine;
import org.eclipse.viatra.query.runtime.api.IncQueryMatcher;
import org.eclipse.viatra.query.runtime.base.itc.alg.incscc.Direction;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.Context;
import org.eclipse.viatra.transformation.evm.api.Job;
import org.eclipse.viatra.transformation.evm.api.RuleEngine;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.viatra.transformation.evm.specific.ExecutionSchemas;
import org.eclipse.viatra.transformation.evm.specific.Jobs;
import org.eclipse.viatra.transformation.evm.specific.Lifecycles;
import org.eclipse.viatra.transformation.evm.specific.Rules;
import org.eclipse.viatra.transformation.evm.specific.Schedulers;
import org.eclipse.viatra.transformation.evm.specific.event.IncQueryActivationStateEnum;
import org.eclipse.viatra.transformation.evm.specific.job.SequentialProcessorsJob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Utility class to prepare a rule in an agenda for an observable collection. For use cases, see
 * {@link ObservablePatternMatchSet} and {@link ObservablePatternMatchList}.
 * 
 * @author Abel Hegedus
 * 
 */
public final class ObservableCollectionHelper {

    /**
     * Constructor hidden for utility class
     */
    private ObservableCollectionHelper() {
    }

    /**
     * Creates the rule used for updating the results.
     * 
     * @param observableCollectionUpdate
     *            the observable collection to handle
     * @param querySpecification
     *            the {@link IQuerySpecification} used to create the rule
     */
    protected static <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> RuleSpecification<Match> createRuleSpecification(
            IObservablePatternMatchCollectionUpdate<Match> observableCollectionUpdate, IQuerySpecification<Matcher> querySpecification) {

        Set<Job<Match>> jobs = getObservableCollectionJobs(observableCollectionUpdate);
        return Rules.newMatcherRuleSpecification(querySpecification, Lifecycles.getDefault(false, true), jobs);
    }
    
    /**
     * Creates the rule used for updating the results including changes in feature values.
     * 
     * @param observableCollectionUpdate
     *            the observable collection to handle
     * @param querySpecification
     *            the {@link IQuerySpecification} used to create the rule
     */
    protected static <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> RuleSpecification<Match> createUpdatingRuleSpecification(
            IObservablePatternMatchCollectionUpdate<Match> observableCollectionUpdate,
            IQuerySpecification<Matcher> querySpecification) {

        Set<Job<Match>> jobs = getObservableCollectionJobs(observableCollectionUpdate);
        Job<Match> updateJob = Jobs.newErrorLoggingJob(new SequentialProcessorsJob<Match>(
                IncQueryActivationStateEnum.UPDATED, ImmutableList.of(
                        new ObservableCollectionProcessor<Match>(Direction.DELETE, observableCollectionUpdate),
                        new ObservableCollectionProcessor<Match>(Direction.INSERT, observableCollectionUpdate)
                )));
        ImmutableSet<Job<Match>> allJobs = ImmutableSet.<Job<Match>> builder().addAll(jobs).add(updateJob).build();
        return Rules.newMatcherRuleSpecification(querySpecification, Lifecycles.getDefault(true, true), allJobs);
    }
    
    /**
     * Creates the rule used for updating the results.
     * 
     * @param observableCollectionUpdate
     *            the observable collection to handle
     * @param matcher
     *            the {@link IncQueryMatcher} used to create the rule
     */
    protected static <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> RuleSpecification<Match> createRuleSpecification(
            IObservablePatternMatchCollectionUpdate<Match> observableCollectionUpdate, Matcher matcher) {
        
        Set<Job<Match>> jobs = getObservableCollectionJobs(observableCollectionUpdate);
        return Rules.newMatcherRuleSpecification(matcher, Lifecycles.getDefault(false, true), jobs);
    }

    private static <Match extends IPatternMatch> Set<Job<Match>> getObservableCollectionJobs(
            IObservablePatternMatchCollectionUpdate<Match> observableCollectionUpdate) {
        Job<Match> insertJob = Jobs.newErrorLoggingJob(Jobs.newStatelessJob(IncQueryActivationStateEnum.APPEARED,
                new ObservableCollectionProcessor<Match>(Direction.INSERT, observableCollectionUpdate)));
        Job<Match> deleteJob = Jobs.newErrorLoggingJob(Jobs.newStatelessJob(IncQueryActivationStateEnum.DISAPPEARED,
                new ObservableCollectionProcessor<Match>(Direction.DELETE, observableCollectionUpdate)));
        return ImmutableSet.of(insertJob, deleteJob);
    }

    protected static <Match extends IPatternMatch> RuleEngine prepareRuleEngine(IncQueryEngine engine, RuleSpecification<Match> specification, EventFilter<Match> filter) {
        RuleEngine ruleEngine = ExecutionSchemas.createIncQueryExecutionSchema(engine,
                Schedulers.getIQEngineSchedulerFactory(engine));
		ruleEngine.addRule(specification, filter);
        fireActivations(ruleEngine, specification, filter);
        return ruleEngine;
    }
    
    /**
     * 'Naive' firing of all activations - only works if they do not conflict. In data binding this is always true.
     * @param ruleEngine
     * @param specification
     * @param filter
     */
    static <Match extends IPatternMatch> void fireActivations(final RuleEngine ruleEngine, final RuleSpecification<Match> specification, final EventFilter<Match> filter) {
    	Set<Activation<Match>> activations = ruleEngine.getActivations(specification, filter);
    	Context context = Context.create();
    	
    	for (Activation<Match> activation : activations) {
    		activation.fire(context);
    	}
    }
    
}
