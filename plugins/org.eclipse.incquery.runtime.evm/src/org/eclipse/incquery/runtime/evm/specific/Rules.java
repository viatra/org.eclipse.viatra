/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.evm.specific;

import java.util.Collection;
import java.util.Set;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.evm.api.ActivationLifeCycle;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryEventRealm;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryEventSourceSpecification;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryFilterSemantics;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryMultiPatternMatchEventFilter;
import org.eclipse.incquery.runtime.evm.specific.event.IncQuerySinglePatternMatchEventFilter;
import org.eclipse.incquery.runtime.evm.specific.lifecycle.DefaultActivationLifeCycle;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.QuerySpecificationRegistry;

/**
 * 
 * Provides static methods acting on or generating a {@link RuleSpecification}. 
 * 
 * @author Abel Hegedus
 *
 */
public final class Rules {
    
    /**
     * Creates a {@link RuleSpecification} with the given query specification, life-cycle and jobs.
     * 
     * For default life-cycle implementations, see {@link DefaultActivationLifeCycle}.
     *        
     * @param querySpecification
     * @param lifecycle
     * @param jobs
     * @return
     */
    public static <Match extends IPatternMatch> RuleSpecification<Match> newMatcherRuleSpecification(IQuerySpecification<? extends IncQueryMatcher<Match>> querySpecification, ActivationLifeCycle lifecycle, Set<Job<Match>> jobs){
        return new RuleSpecification<Match>(IncQueryEventRealm.createSourceSpecification(querySpecification), lifecycle, jobs);
    }
    
    /**
     * Creates a {@link RuleSpecification} with the given query specification and jobs,
     *  using the {@link DefaultActivationLifeCycle#DEFAULT} life-cycle.
     * 
     * @param querySpecification
     * @param jobs
     * @return
     */
    public static <Match extends IPatternMatch> RuleSpecification<Match> newMatcherRuleSpecification(IQuerySpecification<? extends IncQueryMatcher<Match>> querySpecification, Set<Job<Match>> jobs){
        return newMatcherRuleSpecification(querySpecification, DefaultActivationLifeCycle.DEFAULT, jobs);
    }
    
    public static <Match extends IPatternMatch> RuleSpecification<Match> newMatcherRuleSpecification(IncQueryMatcher<Match> matcher, ActivationLifeCycle lifecycle, Set<Job<Match>> jobs){
        FavouredMatcherSourceSpecification<Match> sourceSpecification = new FavouredMatcherSourceSpecification<Match>(matcher);
        return new RuleSpecification<Match>(sourceSpecification, lifecycle, jobs);
    }

    private static final class FavouredMatcherSourceSpecification<Match extends IPatternMatch> extends IncQueryEventSourceSpecification<Match>{
        
        private final IncQueryMatcher<Match> matcher;
        
        /**
         * 
         */
        public FavouredMatcherSourceSpecification(IncQueryMatcher<Match> matcher) {
            super(getQuerySpecification(matcher));
            this.matcher = matcher;
        }
        
        @SuppressWarnings("unchecked")
        private static <Match extends IPatternMatch> IQuerySpecification<? extends IncQueryMatcher<Match>> getQuerySpecification(IncQueryMatcher<Match> matcher) {
            return (IQuerySpecification<? extends IncQueryMatcher<Match>>) QuerySpecificationRegistry.getOrCreateQuerySpecification(matcher.getPattern());
        }
        
        @Override
        protected IncQueryMatcher<Match> getMatcher(IncQueryEngine engine) throws IncQueryException {
            if(matcher.getEngine().equals(engine)) {
                return matcher;
            }
            return super.getMatcher(engine);
        }
    }
    
    /**
     * Creates an event filter that uses the IPatternMatch.isCompatibleWith to check event atoms.
     * 
     * <p/> Using the matches that are equal will result in equal filters.
     * 
     * @param filterMatch non-null match to use for filtering
     * @return the event filter
     */
    public static <Match extends IPatternMatch> EventFilter<Match> newSingleMatchFilter(Match filterMatch) {
        return IncQuerySinglePatternMatchEventFilter.createFilter(filterMatch);
    }
    
    /**
     * Creates a "multi OR" event filter that uses the IPatternMatch.isCompatibleWith to check event atoms against a collection
     * of filter (partial) matches. This variant uses the "OR" semantics, i.e. if any one of the matches is compatible,
     * then the event atom is processed.
     * 
     * @param filterMatchs non-null match to use for filtering
     * @return the event filter
     */
    public static <Match extends IPatternMatch> EventFilter<Match> newMultiMatchFilter(Collection<Match> filterMatches, IncQueryFilterSemantics semantics) {
        return IncQueryMultiPatternMatchEventFilter.createFilter(filterMatches, semantics);
    }
    
}
