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
package org.eclipse.incquery.databinding.runtime.collection;

import java.util.Set;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.base.itc.alg.incscc.Direction;
import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.Context;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;
import org.eclipse.incquery.runtime.evm.specific.ExecutionSchemas;
import org.eclipse.incquery.runtime.evm.specific.Jobs;
import org.eclipse.incquery.runtime.evm.specific.Rules;
import org.eclipse.incquery.runtime.evm.specific.Schedulers;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum;
import org.eclipse.incquery.runtime.evm.specific.lifecycle.DefaultActivationLifeCycle;

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
    public static <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> RuleSpecification<Match> createRuleSpecification(
            IObservablePatternMatchCollectionUpdate<Match> observableCollectionUpdate, IQuerySpecification<Matcher> querySpecification) {

        Set<Job<Match>> jobs = getObservableCollectionJobs(observableCollectionUpdate);
        return Rules.newMatcherRuleSpecification(querySpecification, DefaultActivationLifeCycle.DEFAULT_NO_UPDATE, jobs);
    }
    
    /**
     * Creates the rule used for updating the results.
     * 
     * @param observableCollectionUpdate
     *            the observable collection to handle
     * @param matcher
     *            the {@link IncQueryMatcher} used to create the rule
     */
    public static <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> RuleSpecification<Match> createRuleSpecification(
            IObservablePatternMatchCollectionUpdate<Match> observableCollectionUpdate, Matcher matcher) {
        
        Set<Job<Match>> jobs = getObservableCollectionJobs(observableCollectionUpdate);
        return Rules.newMatcherRuleSpecification(matcher, DefaultActivationLifeCycle.DEFAULT_NO_UPDATE, jobs);
    }

    private static <Match extends IPatternMatch> Set<Job<Match>> getObservableCollectionJobs(
            IObservablePatternMatchCollectionUpdate<Match> observableCollectionUpdate) {
        Job<Match> insertJob = Jobs.newErrorLoggingJob(Jobs.newStatelessJob(IncQueryActivationStateEnum.APPEARED,
                new ObservableCollectionProcessor<Match>(Direction.INSERT, observableCollectionUpdate)));
        Job<Match> deleteJob = Jobs.newErrorLoggingJob(Jobs.newStatelessJob(IncQueryActivationStateEnum.DISAPPEARED,
                new ObservableCollectionProcessor<Match>(Direction.DELETE, observableCollectionUpdate)));
        return ImmutableSet.of(insertJob, deleteJob);
    }

    public static <Match extends IPatternMatch> void prepareRuleEngine(IncQueryEngine engine, RuleSpecification<Match> specification, Match filter) {
        RuleEngine ruleEngine = ExecutionSchemas.createIncQueryExecutionSchema(engine,
                Schedulers.getIQEngineSchedulerFactory(engine));
        if(filter != null) {
            EventFilter<Match> matchFilter = Rules.newMatchFilter(filter);
			ruleEngine.addRule(specification, matchFilter);
            fireActivations(ruleEngine, specification, matchFilter);
        } else {
            ruleEngine.addRule(specification);
            fireActivations(ruleEngine, specification, specification.createEmptyFilter());
        }
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
    
//    public static <Match extends IPatternMatch> void addPrioritizedRuleSpecification(RuleEngine engine,
//            RuleSpecification<Match> specification, int priority, Match filter) {
//        Comparator<Activation<?>> comparator = engine.getActivationComparator();
//        if (comparator instanceof RulePriorityActivationComparator) {
//            ((RulePriorityActivationComparator) comparator).setRuleSpecificationPriority(specification, priority);
//        }
//        engine.addRule(specification, true, filter);
//    }

}
