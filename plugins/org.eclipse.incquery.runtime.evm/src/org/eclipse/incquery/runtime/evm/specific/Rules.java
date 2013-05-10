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

import java.util.Set;

import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.evm.api.ActivationLifeCycle;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryEventRealm;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryEventSourceSpecification;
import org.eclipse.incquery.runtime.evm.specific.lifecycle.DefaultActivationLifeCycle;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.MatcherFactoryRegistry;

/**
 * 
 * Provides static methods acting on or generating a {@link RuleSpecification}. 
 * 
 * @author Abel Hegedus
 *
 */
public final class Rules {
    
    /**
     * Creates a {@link RuleSpecification} with the given factory, life-cycle and jobs.
     * 
     * For default life-cycle implementations, see {@link DefaultActivationLifeCycle}.
     *        
     * @param factory
     * @param lifecycle
     * @param jobs
     * @return
     */
    public static <Match extends IPatternMatch> RuleSpecification<Match> newSimpleMatcherRuleSpecification(IMatcherFactory<? extends IncQueryMatcher<Match>> factory, ActivationLifeCycle lifecycle, Set<Job<Match>> jobs){
        return new RuleSpecification<Match>(IncQueryEventRealm.createSourceSpecification(factory), lifecycle, jobs);
    }
    
    /**
     * Creates a {@link RuleSpecification} with the given factory and jobs,
     *  using the {@link DefaultActivationLifeCycle#DEFAULT} life-cycle.
     * 
     * @param factory
     * @param jobs
     * @return
     */
    public static <Match extends IPatternMatch> RuleSpecification<Match> newSimpleMatcherRuleSpecification(IMatcherFactory<? extends IncQueryMatcher<Match>> factory, Set<Job<Match>> jobs){
        return newSimpleMatcherRuleSpecification(factory, DefaultActivationLifeCycle.DEFAULT, jobs);
    }
    
    public static <Match extends IPatternMatch> RuleSpecification<Match> newSimpleMatcherRuleSpecification(IncQueryMatcher<Match> matcher, ActivationLifeCycle lifecycle, Set<Job<Match>> jobs){
        FavouredMatcherSourceSpecification<Match> sourceSpecification = new FavouredMatcherSourceSpecification<Match>(matcher);
        return new RuleSpecification<Match>(sourceSpecification, lifecycle, jobs);
    }

    private static final class FavouredMatcherSourceSpecification<Match extends IPatternMatch> extends IncQueryEventSourceSpecification<Match>{
        
        private final IncQueryMatcher<Match> matcher;
        
        /**
         * 
         */
        public FavouredMatcherSourceSpecification(IncQueryMatcher<Match> matcher) {
            super(getFactory(matcher));
            this.matcher = matcher;
        }
        
        @SuppressWarnings("unchecked")
        private static <Match extends IPatternMatch> IMatcherFactory<? extends IncQueryMatcher<Match>> getFactory(IncQueryMatcher<Match> matcher) {
            return (IMatcherFactory<? extends IncQueryMatcher<Match>>) MatcherFactoryRegistry.getOrCreateMatcherFactory(matcher.getPattern());
        }
        
        @Override
        protected IncQueryMatcher<Match> getMatcher(IncQueryEngine engine) throws IncQueryException {
            if(matcher.getEngine().equals(engine)) {
                return matcher;
            }
            return super.getMatcher(engine);
        }
    }
    
}
