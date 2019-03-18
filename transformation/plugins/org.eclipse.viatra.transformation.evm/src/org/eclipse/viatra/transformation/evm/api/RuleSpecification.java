/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.transformation.evm.api.event.AbstractRuleInstanceBuilder;
import org.eclipse.viatra.transformation.evm.api.event.ActivationState;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.viatra.transformation.evm.api.event.EventRealm;
import org.eclipse.viatra.transformation.evm.api.event.EventSourceSpecification;

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
    private final Map<ActivationState, Set<Job<EventAtom>>> jobs;
    private final Set<ActivationState> enabledStates;
    private final EventSourceSpecification<EventAtom> sourceSpecification;
    private String name;

    /**
     * Creates a specification with the given life-cycle and job set.
     * 
     * @param lifeCycle
     * @param jobs
     */
    public RuleSpecification(final EventSourceSpecification<EventAtom> sourceSpecification, final ActivationLifeCycle lifeCycle,
            final Set<Job<EventAtom>> jobs) {
        Preconditions.checkArgument(sourceSpecification != null, "Cannot create rule specification with null source definition!");
        this.sourceSpecification = sourceSpecification;
        this.lifeCycle = ActivationLifeCycle.copyOf(lifeCycle);
        this.jobs = new HashMap<>();
        Set<ActivationState> states = new HashSet<ActivationState>();
        if (jobs != null && !jobs.isEmpty()) {
            for (Job<EventAtom> job : jobs) {
                ActivationState state = job.getActivationState();
                this.jobs.computeIfAbsent(state, st -> new HashSet<>()).add(job);
                states.add(state);
            }
        }
        this.enabledStates = Collections.unmodifiableSet(new HashSet<>(states));
    }
    
    /**
     * Creates a specification with a certain name based on the given life-cycle and job set.
     * 
     * @param lifeCycle
     * @param jobs
     * @param name
     */
    public RuleSpecification(final EventSourceSpecification<EventAtom> sourceSpecification, final ActivationLifeCycle lifeCycle,
            final Set<Job<EventAtom>> jobs, String name) {
        this(sourceSpecification, lifeCycle, jobs);
        this.name = name;
    }
    
    /**
     * Instantiates the rule on the given EventRealm with the given filter
     * .
     * @param eventRealm
     * @param filter
     * @return the instantiated rule
     */
    protected RuleInstance<EventAtom> instantiateRule(final EventRealm eventRealm, final EventFilter<? super EventAtom> filter) {
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
    
    public EventSourceSpecification<EventAtom> getSourceSpecification() {
        return sourceSpecification;
    }
    
    public EventFilter<EventAtom> createEmptyFilter() {
        return sourceSpecification.createEmptyFilter();
    }
    
// NOTE we don't want to restrict filtering by atoms  
//    public EventFilter<EventAtom> createFilter(EventAtom atom){
//        return sourceSpecification.createFilter(atom);
//    }

    public ActivationLifeCycle getLifeCycle() {
        return lifeCycle;
    }

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
     * @since 2.0
     */
    public Map<ActivationState, Set<Job<EventAtom>>> getJobs() {
        return jobs;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%s{lifecycle=%s, jobs=%s}", getClass().getName(), lifeCycle, jobs);
    }
}
