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
package org.eclipse.incquery.runtime.evm.api;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.incquery.runtime.evm.api.event.AbstractRuleInstanceBuilder;
import org.eclipse.incquery.runtime.evm.api.event.ActivationState;
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;
import org.eclipse.incquery.runtime.evm.api.event.EventRealm;
import org.eclipse.incquery.runtime.evm.api.event.EventSourceSpecification;

import com.google.common.base.Objects;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * A rule specification specifies how the set of individual
 * rule activations and their states are computed, what jobs (operations)
 * to perform when an activation is executed, and how events affect the 
 * state of the activations based on a life-cycle model.
 * 
 * @author Abel Hegedus
 * 
 */
public class RuleSpecification<EventAtom> {

    private final ActivationLifeCycle lifeCycle;
    private final Multimap<ActivationState, Job<EventAtom>> jobs;
    private final Set<ActivationState> enabledStates;
    private final EventSourceSpecification<EventAtom> sourceSpecification;

    /**
     * Creates a specification with the given life-cycle and job set.
     * 
     * @param lifeCycle
     * @param jobs
     */
    public RuleSpecification(final EventSourceSpecification<EventAtom> sourceSpecification, final ActivationLifeCycle lifeCycle,
            final Set<Job<EventAtom>> jobs) {
        checkArgument(sourceSpecification != null, "Cannot create rule specification with null source definition!");
        this.sourceSpecification = sourceSpecification;
        this.lifeCycle = ActivationLifeCycle.copyOf(lifeCycle);
        this.jobs = HashMultimap.create();
        Set<ActivationState> states = new TreeSet<ActivationState>();
        if (jobs != null && !jobs.isEmpty()) {
            for (Job<EventAtom> job : jobs) {
                ActivationState state = job.getActivationState();
                this.jobs.put(state, job);
                states.add(state);
            }
        }
        this.enabledStates = ImmutableSet.copyOf(states);
    }
    
    /**
     * Instantiates the rule on the given EventRealm with the given filter
     * .
     * @param eventRealm
     * @param filter
     * @return the instantiated rule
     */
    protected RuleInstance<EventAtom> instantiateRule(final EventRealm eventRealm, final EventFilter<EventAtom> filter) {
//        boolean valid = eventRealm.validateSourceSpecification(sourceSpecification, filter);
//        if(valid) {
//            @SuppressWarnings("unchecked")
//            EventRealm<EventAtom> realRealm = (EventRealm<EventAtom>) eventRealm;
//            EventSource<EventAtom> eventSource = eventRealm.createSource(sourceSpecification);
//            EventHandler<EventAtom> eventHandler = eventSource.createHandler(filter);
            RuleInstance<EventAtom> ruleInstance = new RuleInstance<EventAtom>(this);
            AbstractRuleInstanceBuilder<EventAtom> builder = sourceSpecification.getRuleInstanceBuilder(eventRealm);
            builder.prepareRuleInstance(ruleInstance, filter);
            return ruleInstance;
//        }
//        return null;
        
    }
    
    /**
     * @return the sourceSpecification
     */
    public EventSourceSpecification<EventAtom> getSourceSpecification() {
        return sourceSpecification;
    }
    
    public EventFilter<EventAtom> createEmptyFilter() {
        return sourceSpecification.createEmptyFilter();
    }
    
    /**
     * @return the lifeCycle
     */
    public ActivationLifeCycle getLifeCycle() {
        return lifeCycle;
    }

    /**
     * @return the enabledStates
     */
    public Set<ActivationState> getEnabledStates() {
        return enabledStates;
    }

    /**
     * Returns the jobs specified for the given state.
     * 
     * @param state
     * @return the collection of jobs
     */
    public Collection<Job<EventAtom>> getJobs(final ActivationState state) {
        return jobs.get(state);
    }

    /**
     * @return the jobs
     */
    public Multimap<ActivationState, Job<EventAtom>> getJobs() {
        return jobs;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("lifecycle", lifeCycle).add("jobs", jobs).toString();
    }
}
