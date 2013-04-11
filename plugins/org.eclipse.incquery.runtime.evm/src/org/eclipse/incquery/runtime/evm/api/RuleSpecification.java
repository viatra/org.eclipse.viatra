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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;

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
public abstract class RuleSpecification<Match extends IPatternMatch> {

    private final ActivationLifeCycle lifeCycle;
    private final Multimap<ActivationState, Job<Match>> jobs;
    private final Comparator<Match> comparator;
    private final Set<ActivationState> enabledStates;

    /**
     * Creates a specification with the given life-cycle and job set.
     * 
     * @param lifeCycle
     * @param jobs
     */
    public RuleSpecification(final ActivationLifeCycle lifeCycle,
            final Set<Job<Match>> jobs) {
        this(lifeCycle, jobs, null);
    }

    /**
     * Creates a specification with the given life-cycle, job set and
     * activation comparator.
     * 
     * @param lifeCycle
     * @param jobs
     * @param comparator
     */
    public RuleSpecification(final ActivationLifeCycle lifeCycle,
            final Set<Job<Match>> jobs, final Comparator<Match> comparator) {
        this.lifeCycle = checkNotNull(ActivationLifeCycle.copyOf(lifeCycle),
                "Cannot create rule specification with null life cycle!");
        this.jobs = HashMultimap.create();
        Set<ActivationState> states = new TreeSet<ActivationState>();
        if (jobs != null && !jobs.isEmpty()) {
            for (Job<Match> job : jobs) {
                ActivationState state = job.getActivationState();
                this.jobs.put(state, job);
                states.add(state);
            }
        }
        this.enabledStates = ImmutableSet.copyOf(states);
        this.comparator = comparator;
    }
    
    /**
     * Instantiates the rule on the given IncQueryEngine with the given filter
     * .
     * @param engine
     * @param filter
     * @return the instantiated rule
     */
    protected abstract RuleInstance<Match> instantiateRule(final IncQueryEngine engine, final Match filter);
    
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
    public Collection<Job<Match>> getJobs(final ActivationState state) {
        return jobs.get(state);
    }

    /**
     * @return the jobs
     */
    public Multimap<ActivationState, Job<Match>> getJobs() {
        return jobs;
    }

    /**
     * @return the comparator
     */
    public Comparator<Match> getComparator() {
        return comparator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("lifecycle", lifeCycle).add("jobs", jobs).add("comparator", comparator).toString();
    }
}
