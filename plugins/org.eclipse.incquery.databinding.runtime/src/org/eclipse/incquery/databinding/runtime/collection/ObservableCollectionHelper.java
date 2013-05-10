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

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.base.itc.alg.incscc.Direction;
import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.Context;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.specific.ExecutionSchemas;
import org.eclipse.incquery.runtime.evm.specific.Rules;
import org.eclipse.incquery.runtime.evm.specific.Schedulers;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryEventRealm;
import org.eclipse.incquery.runtime.evm.specific.job.StatelessJob;
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
     * Job implementation with error handling that logs exceptions.
     * 
     * @author Abel Hegedus
     */
    private static final class ObservableCollectionJob<Match extends IPatternMatch> extends StatelessJob<Match> {
        private ObservableCollectionJob(IncQueryActivationStateEnum incQueryActivationStateEnum, IMatchProcessor<Match> matchProcessor) {
            super(incQueryActivationStateEnum, matchProcessor);
        }

        @Override
        protected void handleError(Activation<? extends Match> activation, Exception exception, Context context) {
            IncQueryEngine.getDefaultLogger().error("Exception occurred while updating observable collection!",
                    exception);
        }
    }

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
     * @param factory
     *            the {@link IMatcherFactory} used to create the rule
     */
    public static <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> RuleSpecification<Match> createRuleSpecification(
            IObservablePatternMatchCollectionUpdate<Match> observableCollectionUpdate, IMatcherFactory<Matcher> factory) {

        Set<Job<Match>> jobs = getObservableCollectionJobs(observableCollectionUpdate);
        return Rules.newSimpleMatcherRuleSpecification(factory, DefaultActivationLifeCycle.DEFAULT_NO_UPDATE, jobs);
    }
    
    /**
     * Creates the rule used for updating the results.
     * 
     * @param observableCollectionUpdate
     *            the observable collection to handle
     * @param factory
     *            the {@link IncQueryMatcher} used to create the rule
     */
    public static <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> RuleSpecification<Match> createRuleSpecification(
            IObservablePatternMatchCollectionUpdate<Match> observableCollectionUpdate, Matcher matcher) {
        
        Set<Job<Match>> jobs = getObservableCollectionJobs(observableCollectionUpdate);
        return Rules.newSimpleMatcherRuleSpecification(matcher, DefaultActivationLifeCycle.DEFAULT_NO_UPDATE, jobs);
    }

    private static <Match extends IPatternMatch> Set<Job<Match>> getObservableCollectionJobs(
            IObservablePatternMatchCollectionUpdate<Match> observableCollectionUpdate) {
        Job<Match> insertJob = new ObservableCollectionJob<Match>(IncQueryActivationStateEnum.APPEARED,
                new ObservableCollectionProcessor<Match>(Direction.INSERT, observableCollectionUpdate));
        Job<Match> deleteJob = new ObservableCollectionJob<Match>(IncQueryActivationStateEnum.DISAPPEARED,
                new ObservableCollectionProcessor<Match>(Direction.DELETE, observableCollectionUpdate));
        return ImmutableSet.of(insertJob, deleteJob);
    }

    public static <Match extends IPatternMatch> void prepareRuleEngine(IncQueryEngine engine, RuleSpecification<Match> specification, Match filter) {
        RuleEngine ruleEngine = ExecutionSchemas.createIncQueryExecutionSchema(engine,
                Schedulers.getIQBaseSchedulerFactory(engine));
        if(filter != null) {
            ruleEngine.addRule(specification, true, IncQueryEventRealm.createFilter(filter));
        } else {
            ruleEngine.addRule(specification, true);
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
