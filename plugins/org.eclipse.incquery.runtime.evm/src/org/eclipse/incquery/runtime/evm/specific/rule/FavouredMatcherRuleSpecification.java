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
package org.eclipse.incquery.runtime.evm.specific.rule;

import java.util.Set;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.evm.api.ActivationLifeCycle;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.incquery.runtime.extensibility.QuerySpecificationRegistry;

/**
 * A rule specification that is created using a designated {@link IncQueryMatcher} already initialized
 * on an {@link IncQueryEngine}. When the rule is instantiated in a different engine, the query specification returned 
 * by the {@link QuerySpecificationRegistry} is used. 
 * 
 * @author Abel Hegedus
 *
 * @deprecated Use Rules.newSimpleMatcherRuleSpecification(Matcher) instead!
 */
public class FavouredMatcherRuleSpecification<Match extends IPatternMatch> extends SimpleMatcherRuleSpecification<Match> {

    private final IncQueryMatcher<Match> matcher;
    
    public FavouredMatcherRuleSpecification(IncQueryMatcher<Match> matcher, ActivationLifeCycle lifeCycle,
            final Set<Job<Match>> jobs) {
        super(getQuerySpecification(matcher), lifeCycle, jobs);
        this.matcher = matcher;
        throw new UnsupportedOperationException("Do not use this subclass of rule specification!");
    }

    @SuppressWarnings("unchecked")
    private static <Match extends IPatternMatch> IQuerySpecification<? extends IncQueryMatcher<Match>> getQuerySpecification(IncQueryMatcher<Match> matcher) {
        return (IQuerySpecification<? extends IncQueryMatcher<Match>>) QuerySpecificationRegistry.getOrCreateQuerySpecification(matcher.getPattern());
    }
    
//    @Override
//    protected IncQueryMatcher<Match> getMatcher(IncQueryEngine engine) throws IncQueryException {
//        if(matcher.getEngine().equals(engine)) {
//            return matcher;
//        }
//        return super.getMatcher(engine);
//    }
}
