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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Comparator;
import java.util.Set;

import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.evm.api.ActivationLifeCycle;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.incquery.runtime.evm.api.RuleInstance;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;

import com.google.common.base.Objects;

/**
 * This class implements a rule specification that uses a single matcher factory to prepare instances. 
 * 
 * @author Abel Hegedus
 *
 */
public class SimpleMatcherRuleSpecification<Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> extends RuleSpecification<Match> {
    
    private final IMatcherFactory<Matcher> factory;
    
    /**
     * Creates a specification with the given factory, life-cycle and job list.
     * 
     * @param factory
     * @param lifeCycle
     * @param jobs
     */
    public SimpleMatcherRuleSpecification(final IMatcherFactory<Matcher> factory, final ActivationLifeCycle lifeCycle,
            final Set<Job<Match>> jobs) {
        this(factory, lifeCycle, jobs, null);
        
    }

    /**
     * Creates a specification with the given factory, life-cycle, job list and activation comparator.
     * 
     * @param factory
     * @param lifeCycle
     * @param jobs
     * @param comparator
     */
    public SimpleMatcherRuleSpecification(final IMatcherFactory<Matcher> factory, final ActivationLifeCycle lifeCycle,
            final Set<Job<Match>> jobs, final Comparator<Match> comparator) {
        super(lifeCycle, jobs, comparator);
        this.factory = checkNotNull(factory, "Cannot create rule specification with null matcher factory!");
    }
    
    @Override
    protected RuleInstance<Match> instantiateRule(final IncQueryEngine engine) {
        SimpleMatcherRuleInstance<Match,Matcher> ruleInstance = new SimpleMatcherRuleInstance<Match,Matcher>(this);
        Matcher matcher;
        try {
            matcher = factory.getMatcher(engine);
            ruleInstance.prepareInstance(matcher);
        } catch (IncQueryException e) {
            engine.getLogger().error(String.format("Could not initialize matcher for pattern %s in rule specification %s",factory.getPatternFullyQualifiedName(),this), e);
        }
        return ruleInstance;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("pattern", factory.getPatternFullyQualifiedName())
                .add("lifecycle", getLifeCycle()).add("jobs", getJobs()).add("comparator", getComparator()).toString();
    }
}
