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
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.evm.api.event.Atom;
import org.eclipse.incquery.runtime.evm.api.event.EmptyAtom;
import org.eclipse.incquery.runtime.evm.api.event.EventSource;

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

    /**
     * @return the ruleBase
     */
    protected RuleBase getAgenda() {
        return ruleBase;
    }
    
    public void setConflictResolver(ConflictResolver<?> conflictResolver) {
        checkNotNull(conflictResolver, "Conflict resolver cannot be null!");
        ruleBase.getAgenda().setConflictResolver(conflictResolver);
    }
    
    /**
     * 
     * @return the IncQueryEngine of the ruleBase
     */
    public EventSource getEventSource() {
        return ruleBase.getEventSource();
    }

    /**
     * 
     * @return a copy of the multimap containing all activations
     */
    public Multimap<ActivationState, Activation> getActivations() {
        return ImmutableMultimap.copyOf(ruleBase.getAgenda().getActivations());
    }

    /**
     * 
     * @return an immutable set of conflicting activations
     */
    public Set<Activation> getConflictingActivations() {
        return ImmutableSet.copyOf(ruleBase.getAgenda().getConflictingActivations());
        //return Collections.unmodifiableSet(ruleBase.getAgenda().getConflictingActivations());
    }
    
    /**
     * 
     * @return the first enabled activation if exists
     */
    public Activation getFirstEnabledActivation() {
        return ruleBase.getAgenda().getNextActivation();
    }
    
    /**
     * 
     * @param state
     * @return an immutable set of the activations in the given state
     */
    public Set<Activation> getActivations(final ActivationState state) {
        checkNotNull(state, "Activation state must be specified!");
        return ImmutableSet.copyOf(ruleBase.getAgenda().getActivations(state));
    }

    /**
     * 
     * @param specification
     * @return the immutable set of activations of the given specification
     */
    public Set<Activation> getActivations(
            final RuleSpecification specification) {
        checkNotNull(specification, RULE_SPECIFICATION_MUST_BE_SPECIFIED);
        return ImmutableSet.copyOf(ruleBase.getInstance(specification, EmptyAtom.INSTANCE).getAllActivations());
    }

    /**
     * 
     * @param specification
     * @param state
     * @return the immutable set of activations of the given specification
     * with the given state
     */
    public <Match extends IPatternMatch> Set<Activation> getActivations(
            final RuleSpecification specification, final ActivationState state) {
        checkNotNull(specification, RULE_SPECIFICATION_MUST_BE_SPECIFIED);
        checkNotNull(state, "Activation state must be specified!");
        return ImmutableSet.copyOf(ruleBase.getInstance(specification, EmptyAtom.INSTANCE).getActivations(state));
    }
    
    

    /**
     * Adds a rule specification to the RuleBase.
     *  If the rule already exists, it is returned instead of a new one.
     *  
     * @param specification
     */
    public <Match extends IPatternMatch> void addRule(
            final RuleSpecification specification) {
        addRule(specification, false, EmptyAtom.INSTANCE);
    }
    
    /**
     * Adds a rule specification to the RuleBase and fires all enabled activations if required.
     * If the rule already exists, it is returned instead of a new one.
     * 
     * @param specification
     * @param fireNow if true, all enabled activations of the new rule are fired immediately
     */
    public void addRule(
            final RuleSpecification specification, boolean fireNow) {
                addRule(specification, fireNow, EmptyAtom.INSTANCE);
            }

    /**
     * Adds a rule specification to the RuleBase and fires all enabled activations if required.
     * If the rule already exists, it is returned instead of a new one.
     * 
     * @param specification
     * @param fireNow if true, all enabled activations of the new rule are fired immediately
     * @param filter the partial match to be used as a filter for activations
     */
    public void addRule(
            final RuleSpecification specification, boolean fireNow, Atom filter) {
        checkNotNull(filter, FILTER_MUST_BE_SPECIFIED);
        checkNotNull(specification, RULE_SPECIFICATION_MUST_BE_SPECIFIED);
        checkArgument(!filter.isMutable(), "Cannot instantiate rule with mutable filter!");
        RuleInstance instance;
        instance = ruleBase.instantiateRule(specification, filter);
        if(fireNow) {
            fireActivations(instance);
        }
    }

    /**
     * Fires all activations of the given rule instance.
     * 
     * @param instance
     */
    protected void fireActivations(RuleInstance instance) {
        Context context = new Context();
        for (Activation act : instance.getAllActivations()) {
            act.fire(context);
        }
    }

    /**
     * 
     * @return the immutable set of rules in the EVM
     */
    public Set<RuleSpecification> getRules() {
        return ImmutableSet.copyOf(ruleBase.getRuleInstanceMap().keySet());
    }

    /**
     * Removes the given not-filtered rule from the EVM.
     * 
     * @param specification
     * @return true, if the rule existed
     */
    public boolean removeRule(final RuleSpecification specification) {
        checkNotNull(specification, RULE_SPECIFICATION_MUST_BE_SPECIFIED);
        return ruleBase.removeRule(specification, EmptyAtom.INSTANCE);
    }

    /**
     * Removes the given filtered rule from the EVM.
     * 
     * @param specification
     * @param filter the partial match used as a filter
     * @return true, if the rule existed
     */
    public boolean removeRule(
            final RuleSpecification specification, Atom filter) {
        checkNotNull(specification, RULE_SPECIFICATION_MUST_BE_SPECIFIED);
        checkNotNull(filter, FILTER_MUST_BE_SPECIFIED);
        return ruleBase.removeRule(specification, filter);
    }

    /**
     * Disposes of the ruleBase.
     */
    public void dispose() {
        ruleBase.dispose();
    }
}
