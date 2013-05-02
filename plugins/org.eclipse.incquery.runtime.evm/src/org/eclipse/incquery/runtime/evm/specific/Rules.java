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

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.evm.api.ActivationLifeCycle;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.specific.lifecycle.DefaultActivationLifeCycle;
import org.eclipse.incquery.runtime.evm.specific.rule.SimpleMatcherRuleSpecification;

/**
 * 
 * Provides static methods acting on or generating a {@link RuleSpecification}. 
 * 
 * @author Abel Hegedus
 *
 */
public final class Rules {
    
    /**
     * Creates a {@link SimpleMatcherRuleSpecification} with the given query specification, life-cycle and jobs.
     * 
     * For default life-cycle implementations, see {@link DefaultActivationLifeCycle}.
     *        
     * @param querySpecification
     * @param lifecycle
     * @param jobs
     * @return
     */
    public static <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> RuleSpecification newSimpleMatcherRuleSpecification(IQuerySpecification<Matcher> querySpecification, ActivationLifeCycle lifecycle, Set<Job> jobs){
        return new SimpleMatcherRuleSpecification<Match, Matcher>(querySpecification, lifecycle, jobs);
    }
    
    /**
     * Creates a {@link SimpleMatcherRuleSpecification} with the given query specification and jobs,
     *  using the {@link DefaultActivationLifeCycle#DEFAULT} life-cycle.
     * 
     * @param querySpecification
     * @param jobs
     * @return
     */
    public static <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> RuleSpecification newSimpleMatcherRuleSpecification(IQuerySpecification<Matcher> querySpecification, Set<Job> jobs){
        return newSimpleMatcherRuleSpecification(querySpecification, DefaultActivationLifeCycle.DEFAULT, jobs);
    }

}
