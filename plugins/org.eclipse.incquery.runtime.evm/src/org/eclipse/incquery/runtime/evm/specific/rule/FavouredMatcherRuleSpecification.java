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

import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.evm.api.ActivationLifeCycle;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.incquery.runtime.extensibility.MatcherFactoryRegistry;

/**
 * A rule specification that is created using a designated {@link IncQueryMatcher} already initialized
 * on an {@link IncQueryEngine}. When the rule is instantiated in a different engine, the factory returned 
 * by the {@link MatcherFactoryRegistry} is used. 
 * 
 * @author Abel Hegedus
 *
 * @deprecated Use Rules.newSimpleMatcherRuleSpecification(Matcher) instead!
 */
public class FavouredMatcherRuleSpecification<Match extends IPatternMatch> extends SimpleMatcherRuleSpecification<Match> {

    private final IncQueryMatcher<Match> matcher;
    
    public FavouredMatcherRuleSpecification(IncQueryMatcher<Match> matcher, ActivationLifeCycle lifeCycle,
            final Set<Job> jobs) {
        super(getFactory(matcher), lifeCycle, jobs);
        throw new UnsupportedOperationException("Do not use this subclass of rule specification!");
        //this.matcher = matcher;
    }

    @SuppressWarnings("unchecked")
    private static <Match extends IPatternMatch> IMatcherFactory<? extends IncQueryMatcher<Match>> getFactory(IncQueryMatcher<Match> matcher) {
        return (IMatcherFactory<? extends IncQueryMatcher<Match>>) MatcherFactoryRegistry.getOrCreateMatcherFactory(matcher.getPattern());
    }
    
//    @Override
//    protected IncQueryMatcher<Match> getMatcher(IncQueryEngine engine) throws IncQueryException {
//        if(matcher.getEngine().equals(engine)) {
//            return matcher;
//        }
//        return super.getMatcher(engine);
//    }
}
