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

import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.incquery.runtime.evm.api.event.ActivationState;
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;
import org.eclipse.incquery.runtime.evm.api.event.EventRealm;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * A rule engine is a facade for accessing the EVM, it allows
 * the retrieval of all activations and filtered access by state,
 *  specification or both. It also allows the addition or removal
 *  of rules.   
 * 
 * @author Abel Hegedus
 * 
 */
public class RuleEngine {

    /**
     * 
     */
    private static final String FILTER_MUST_BE_SPECIFIED = "Filter must be specified";
    private static final String RULE_SPECIFICATION_MUST_BE_SPECIFIED = "Rule specification must be specified!";
    private RuleBase ruleBase;

    /**
     * Creates a rule engine for the given ruleBase
     * 
     * @param ruleBase
     */
    protected RuleEngine(final RuleBase ruleBase) {
        this.ruleBase = checkNotNull(ruleBase, "Cannot create rule engine with null ruleBase!");
    }

    /**
     * Creates a rule engine for the given ruleBase
     * 
     * @param ruleBase
     * @return the created facade
     */
    public static RuleEngine create(final RuleBase ruleBase) {
        return new RuleEngine(ruleBase);
    }

    public void setConflictResolver(ConflictResolver<?> conflictResolver) {
        checkNotNull(conflictResolver, "Conflict resolver cannot be null!");
        ruleBase.getAgenda().setConflictResolver(conflictResolver);
    }

    /**
     * Adds a rule specification to the RuleBase.
     *  If the rule already exists, no change occurs in the set of rules.
     *  
     * @param specification
     * @return true if the rule was added, false if it already existed
     */
    public <EventAtom> boolean addRule(
            final RuleSpecification<EventAtom> specification) {
        return addRule(specification, false, specification.createEmptyFilter());
    }

    /**
     * Adds a rule specification to the RuleBase and fires all enabled activations if required.
     * If the rule already exists, no change occurs in the set of rules.
     * 
     * @param specification
     * @param fireNow if true, all enabled activations of the rule are fired immediately
     * @return true if the rule was added, false if it already existed
     */
    public <EventAtom> boolean addRule(
            final RuleSpecification<EventAtom> specification, boolean fireNow) {
                return addRule(specification, fireNow, specification.createEmptyFilter());
            }

    /**
     * Adds a rule specification to the RuleBase and fires all enabled activations if required.
     * If the rule already exists, no change occurs in the set of rules.
     * 
     * @param specification
     * @param fireNow if true, all enabled activations of the rule are fired immediately
     * @param filter the partial match to be used as a filter for activations
     * @return true if the rule was added, false if it already existed
     */
    public <EventAtom> boolean addRule(
            final RuleSpecification<EventAtom> specification, boolean fireNow, EventFilter<? super EventAtom> filter) {
        checkNotNull(filter, FILTER_MUST_BE_SPECIFIED);
        checkNotNull(specification, RULE_SPECIFICATION_MUST_BE_SPECIFIED);
        RuleInstance<EventAtom> instance = ruleBase.getInstance(specification, filter);
        boolean added = false;
        if(instance == null) {
            instance = ruleBase.instantiateRule(specification, filter);
            added = true;
        }
        if(fireNow) {
            fireActivations(instance);
        }
        return added;
    }

    /**
     * 
     * @return a copy of the multimap containing all activations
     */
    public Multimap<ActivationState, Activation<?>> getActivations() {
        return ImmutableMultimap.copyOf(ruleBase.getAgenda().getActivations());
    }

    /**
     * 
     * @return the next enabled activation if exists, selected by the conflict resolver
     */
    public Activation<?> getNextActivation() {
        return ruleBase.getAgenda().getNextActivation();
    }

    /**
     * 
     * @return an immutable set of conflicting activations
     */
    public Set<Activation<?>> getConflictingActivations() {
        return ImmutableSet.copyOf(ruleBase.getAgenda().getConflictingActivations());
        //return Collections.unmodifiableSet(ruleBase.getAgenda().getConflictingActivations());
    }
    
    /**
     * 
     * @param state
     * @return an immutable set of the activations in the given state
     */
    public Set<Activation<?>> getActivations(final IncQueryActivationStateEnum state) {
        checkNotNull(state, "Activation state must be specified!");
        return ImmutableSet.copyOf(ruleBase.getAgenda().getActivations(state));
    }

    /**
     * 
     * @param specification
     * @return the immutable set of activations of the given specification
     */
    public <EventAtom> Set<Activation<EventAtom>> getActivations(final RuleSpecification<EventAtom> specification) {
        return getActivations(specification, specification.createEmptyFilter());
    }

    /**
     * 
     * @param specification
     * @param filter
     * @return the immutable set of activations of the given filtered specification
     */
    public <EventAtom> Set<Activation<EventAtom>> getActivations(final RuleSpecification<EventAtom> specification, EventFilter<? super EventAtom> filter) {
        checkNotNull(specification, RULE_SPECIFICATION_MUST_BE_SPECIFIED);
        checkNotNull(filter, FILTER_MUST_BE_SPECIFIED);
        return ImmutableSet.copyOf(ruleBase.getInstance(specification, filter).getAllActivations());
    }

    /**
     * 
     * @param specification
     * @param state
     * @return the immutable set of activations of the given specification
     * with the given state
     */
    public <EventAtom> Set<Activation<EventAtom>> getActivations(final RuleSpecification<EventAtom> specification,
            final ActivationState state) {
        return getActivations(specification, specification.createEmptyFilter(), state);
    }

    /**
     * TODO javadoc
     * @param specification 
     * @param filter 
     * @param state
     * @return the immutable set of activations of the given specification
     * with the given state
     */
    public <EventAtom> Set<Activation<EventAtom>> getActivations(
            final RuleSpecification<EventAtom> specification, EventFilter<? super EventAtom> filter, final ActivationState state) {
        checkNotNull(specification, RULE_SPECIFICATION_MUST_BE_SPECIFIED);
        checkNotNull(state, "Activation state must be specified!");
        checkNotNull(filter, FILTER_MUST_BE_SPECIFIED);
        return ImmutableSet.copyOf(ruleBase.getInstance(specification, filter).getActivations(state));
    }
    
    

    /**
     * Fires all activations of the given rule instance.
     * 
     * @param instance
     */
    protected <EventAtom> void fireActivations(RuleInstance<EventAtom> instance) {
        Context context = new Context();
        for (Activation<EventAtom> act : instance.getAllActivations()) {
            act.fire(context);
        }
    }

    /**
     * 
     * @return the immutable set of rules in the EVM
     */
    public Set<RuleSpecification<?>> getRuleSpecifications() {
        return ImmutableSet.copyOf(ruleBase.getRuleSpecificationMultimap().keySet());
    }
    
    public Multimap<RuleSpecification<?>, EventFilter<?>> getRuleSpecificationMultimap(){
        return ImmutableMultimap.copyOf(ruleBase.getRuleSpecificationMultimap());
    }

    /**
     * Removes the given not-filtered rule from the EVM.
     * 
     * @param specification
     * @return true, if the rule existed
     */
    public <EventAtom> boolean removeRule(final RuleSpecification<EventAtom> specification) {
        checkNotNull(specification, RULE_SPECIFICATION_MUST_BE_SPECIFIED);
        return ruleBase.removeRule(specification, specification.createEmptyFilter());
    }

    /**
     * Removes the given filtered rule from the EVM.
     * 
     * @param specification
     * @param filter the partial match used as a filter
     * @return true, if the rule existed
     */
    public <EventAtom> boolean removeRule(
            final RuleSpecification<EventAtom> specification, EventFilter<? super EventAtom> filter) {
        checkNotNull(specification, RULE_SPECIFICATION_MUST_BE_SPECIFIED);
        checkNotNull(filter, FILTER_MUST_BE_SPECIFIED);
        return ruleBase.removeRule(specification, filter);
    }

    /**
     * @return the rule base
     */
    protected RuleBase getRuleBase() {
        return ruleBase;
    }
    
    /**
     * 
     * @return the event realm of the rule base
     */
    public EventRealm getEventRealm() {
        return ruleBase.getEventRealm();
    }
    
    public Logger getLogger() {
        return ruleBase.getLogger();
    }

    /**
     * Disposes of the rule base.
     */
    public void dispose() {
        ruleBase.dispose();
    }
}
