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

import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Set;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;

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

    private static final String RULE_SPECIFICATION_MUST_BE_SPECIFIED = "Rule specification must be specified!";
    private Agenda agenda;

    /**
     * Creates a rule engine for the given agenda
     * 
     * @param agenda
     */
    protected RuleEngine(final Agenda agenda) {
        this.agenda = checkNotNull(agenda, "Cannot create rule engine with null agenda!");
    }

    /**
     * Creates a rule engine for the given agenda
     * 
     * @param agenda
     * @return the created facade
     */
    public static RuleEngine create(final Agenda agenda) {
        return new RuleEngine(agenda);
    }

    /**
     * @return the agenda
     */
    protected Agenda getAgenda() {
        return agenda;
    }
    
    public void setActivationComparator(Comparator<Activation<?>> comparator) {
        checkNotNull(comparator, "Comparator cannot be null!");
        agenda.setActivationComparator(comparator);
    }
    
    public Comparator<Activation<?>> getActivationComparator() {
        return agenda.getActivationComparator();
    }

    /**
     * 
     * @return the IncQueryEngine of the agenda
     */
    public IncQueryEngine getIncQueryEngine() {
        return getAgenda().getIncQueryEngine();
    }

    /**
     * 
     * @return a copy of the multimap containing all activations
     */
    public Multimap<ActivationState, Activation<?>> getActivations() {
        return ImmutableMultimap.copyOf(agenda.getActivations());
    }

    /**
     * Important: firing an activation, or other events may cause 
     * {@link ConcurrentModificationException} if you try to iterate
     * on this set.
     * 
     * @return an unmodifiable view of the set of enabled activations
     */
    public Set<Activation<?>> getEnabledActivations() {
        return Collections.unmodifiableSet(agenda.getEnabledActivations());
    }
    
    /**
     * 
     * @return the first enabled activation if exists
     */
    public Activation<?> getFirstEnabledActivation() {
        Set<Activation<?>> enabledActivations = agenda.getEnabledActivations();
        if(enabledActivations.isEmpty()) {
            return null;
        } else {
            return enabledActivations.iterator().next();
        }
    }    
    
    /**
     * 
     * @param state
     * @return an immutable set of the activations in the given state
     */
    public Set<Activation<?>> getActivations(final ActivationState state) {
        checkNotNull(state, "Activation state must be specified!");
        return ImmutableSet.copyOf(agenda.getActivations(state));
    }

    /**
     * 
     * @param specification
     * @return the immutable set of activations of the given specification
     */
    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> Set<Activation<Match>> getActivations(
            final RuleSpecification<Match> specification) {
        checkNotNull(specification, RULE_SPECIFICATION_MUST_BE_SPECIFIED);
        return ImmutableSet.copyOf(agenda.getActivations(specification));
    }

    /**
     * 
     * @param specification
     * @param state
     * @return the immutable set of activations of the given specification
     * with the given state
     */
    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> Set<Activation<Match>> getActivations(
            final RuleSpecification<Match> specification, final ActivationState state) {
        checkNotNull(specification, RULE_SPECIFICATION_MUST_BE_SPECIFIED);
        checkNotNull(state, "Activation state must be specified!");
        return ImmutableSet.copyOf(agenda.getInstance(specification).getActivations(state));
    }

    /**
     * Adds a rule specification to the Agenda.
     *  If the rule already exists, it is returned instead of a new one.
     *  
     * @param specification
     */
    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> void addRule(
            final RuleSpecification<Match> specification) {
        addRule(specification, false);
    }
    
    /**
     * Adds a rule specification to the Agenda and fires all enabled activations if required.
     * If the rule already exists, it is returned instead of a new one.
     * 
     * @param specification
     * @param fireNow if true, all enabled activations of the new rule are fired immediately
     */
    public <Match extends IPatternMatch> void addRule(
            final RuleSpecification<Match> specification, boolean fireNow) {
        checkNotNull(specification, RULE_SPECIFICATION_MUST_BE_SPECIFIED);
        RuleInstance<Match> instance = agenda.instantiateRule(specification);
        if(fireNow) {
            fireActivations(instance);
        }
    }

    /**
     * Fires all activations of the given rule instance.
     * 
     * @param instance
     */
    protected <Match extends IPatternMatch> void fireActivations(RuleInstance<Match> instance) {
        Context context = new Context();
        for (Activation<Match> act : instance.getAllActivations()) {
            act.fire(context);
        }
    }

    /**
     * 
     * @return the immutable set of rules in the EVM
     */
    public Set<RuleSpecification<? extends IPatternMatch>> getRules() {
        return ImmutableSet.copyOf(agenda.getRuleInstanceMap().keySet());
    }

    /**
     * Removes the given rule from the EVM.
     * 
     * @param specification
     * @return true, if the rule existed
     */
    public <Match extends IPatternMatch> boolean removeRule(
            final RuleSpecification<Match> specification) {
        checkNotNull(specification, RULE_SPECIFICATION_MUST_BE_SPECIFIED);
        return agenda.removeRule(specification);
    }

    /**
     * Disposes of the agenda.
     */
    public void dispose() {
        agenda.dispose();
    }
}
