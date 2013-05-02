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
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.QuerySpecificationRegistry;

/**
 * A rule specification that is created using a designated {@link IncQueryMatcher} already initialized
 * on an {@link IncQueryEngine}. When the rule is instantiated in a different engine, the query specification returned 
 * by the {@link QuerySpecificationRegistry} is used. 
 * 
 * @author Abel Hegedus
 *
 */
public class FavouredMatcherRuleSpecification<Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> extends SimpleMatcherRuleSpecification<Match, Matcher> {

    private final Matcher matcher;
    
    public FavouredMatcherRuleSpecification(Matcher matcher, ActivationLifeCycle lifeCycle,
            final Set<Job> jobs) {
        super(getQuerySpecification(matcher), lifeCycle, jobs);
        this.matcher = matcher;
    }

    @SuppressWarnings("unchecked")
    private static <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> IQuerySpecification<Matcher> getQuerySpecification(Matcher matcher) {
        return (IQuerySpecification<Matcher>) QuerySpecificationRegistry.getOrCreateQuerySpecification(matcher.getPattern());
    }
    
    @Override
    protected Matcher getMatcher(IncQueryEngine engine) throws IncQueryException {
        if(matcher.getEngine().equals(engine)) {
            return matcher;
        }
        return super.getMatcher(engine);
    }
}
