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
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.evm.api.ActivationLifeCycle;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;

/**
 * This class implements a rule specification that uses a single matcher factory to prepare instances. 
 * 
 * @author Abel Hegedus
 * @deprecated Use Rules.newSimpleMatcherRuleSpecification instead!
 */
public class SimpleMatcherRuleSpecification<Match extends IPatternMatch> extends RuleSpecification {
    
    private final IMatcherFactory<? extends IncQueryMatcher<Match>> factory;
    
    /**
     * Creates a specification with the given factory, life-cycle and job list.
     * 
     * @param factory
     * @param lifeCycle
     * @param jobs
     */
    public SimpleMatcherRuleSpecification(final IMatcherFactory<? extends IncQueryMatcher<Match>> factory, final ActivationLifeCycle lifeCycle,
            final Set<Job> jobs) {
        super(null, lifeCycle, jobs);
        throw new UnsupportedOperationException("Do not use this subclass of rule specification!");
        //this.factory = checkNotNull(factory, "Cannot create rule specification with null matcher factory!");
    }

//    @Override
//    protected RuleInstance instantiateRule(EventRealm eventRealm, Atom filter) {
//        SimpleMatcherRuleInstance<Match> ruleInstance = null;
//        if(eventRealm instanceof IncQueryEventRealm) {
//            IncQueryEngine engine = ((IncQueryEventRealm) eventRealm).getEngine();
//            try {
//                IncQueryMatcher<Match> matcher = getMatcher(engine);
//                ruleInstance = new SimpleMatcherRuleInstance<Match>(this, filter);
//                ruleInstance.prepareInstance(matcher);
//            } catch (IncQueryException e) {
//                engine.getLogger().error(String.format("Could not initialize matcher for pattern %s in rule specification %s",factory.getPatternFullyQualifiedName(),this), e);
//            }
//        } else {
//            eventRealm.getLogger().error("Cannot instantiate rule with EvenSource " + eventRealm + "! Should be IncQueryEventRealm.");
//        }
//        return ruleInstance;
//    }
//
//    protected IncQueryMatcher<Match> getMatcher(IncQueryEngine engine) throws IncQueryException {
//        IncQueryMatcher<Match> matcher = factory.getMatcher(engine);
//        return matcher;
//    }
//    
//    /*
//     * (non-Javadoc)
//     * 
//     * @see java.lang.Object#toString()
//     */
//    @Override
//    public String toString() {
//        return Objects.toStringHelper(this).add("pattern", factory.getPatternFullyQualifiedName())
//                .add("lifecycle", getLifeCycle()).add("jobs", getJobs()).toString();
//    }
}
