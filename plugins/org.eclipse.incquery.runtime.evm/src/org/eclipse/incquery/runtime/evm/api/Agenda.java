/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Abel Hegedus - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.evm.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.evm.notification.IActivationNotificationListener;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;

/**
 * An Agenda is associated to each EMF instance model (more precisely {@link IncQueryEngine} and 
 * it is responsible for creating, managing and disposing rules in
 * the Rule Engine. It provides an unmodifiable view for the collection of applicable activations.
 * 
 * @author Tamas Szabo
 * 
 */
public class Agenda {

    /**
     * This class is responsible for handling notifications sent by rule instances when an activation changes state.
     * 
     * By default, the listener logs the change event and refreshes the activation collections.
     * 
     * @author Abel Hegedus
     *
     */
    private final class DefaultActivationNotificationListener implements IActivationNotificationListener {
        @Override
        public void activationChanged(final Activation<? extends IPatternMatch> activation,
                final ActivationState oldState, final ActivationLifeCycleEvent event) {
            Agenda.this.iqEngine.getLogger().debug(
                    String.format("%s: %s -- %s --> %s", activation, oldState, event, activation.getState()));
            activations.remove(oldState, activation);
            ActivationState state = activation.getState();
            switch (state) {
            case INACTIVE:
                enabledActivations.remove(activation);
                break;
            default:
                if (activation.isEnabled()) {
                    enabledActivations.add(activation);
                } else {
                    enabledActivations.remove(activation);
                }
                activations.put(state, activation);
                break;
            }
        }
    }

    private final IncQueryEngine iqEngine;
    private final Map<RuleSpecification<? extends IPatternMatch>, RuleInstance<? extends IPatternMatch>> ruleInstanceMap;
    private Multimap<ActivationState, Activation<?>> activations;
    private Set<Activation<?>> enabledActivations;
    private final IActivationNotificationListener activationListener;
    private Comparator<Activation<?>> activationComparator;

    /**
     * Instantiates a new Agenda instance with the given {@link IncQueryEngine}.
     * 
     * @param iqEngine
     *            the {@link IncQueryEngine} instance
     */
    protected Agenda(final IncQueryEngine iqEngine) {
        this.iqEngine = checkNotNull(iqEngine, "Cannot create Agenda with null IncQueryEngine");
        this.ruleInstanceMap = new HashMap<RuleSpecification<? extends IPatternMatch>, RuleInstance<? extends IPatternMatch>>();
        this.activations = HashMultimap.create();
        this.enabledActivations = Sets.newHashSet();

        this.activationListener = new DefaultActivationNotificationListener();

    }

    /**
     * Instantiates the given specification over the IncQueryEngine of the Agenda.
     * If the specification was already instantiated, the existing instance is returned.
     * 
     * @param specification the rule to be instantiated
     * @return the created or existing rule instance
     */
    protected <Match extends IPatternMatch> RuleInstance<Match> instantiateRule(
            final RuleSpecification<Match> specification) {
        checkNotNull(specification, "Cannot instantiate null rule!");
        if(ruleInstanceMap.containsKey(specification)) {
            return getInstance(specification);
        }
        RuleInstance<Match> rule = specification.instantiateRule(iqEngine);
        rule.addActivationNotificationListener(activationListener, true);
        ruleInstanceMap.put(specification, rule);
        return rule;
    }

    /**
     * Removes and disposes of a rule instance. 
     * @param instance
     * @return true, if the instance was part of the Agenda
     */
    protected <Match extends IPatternMatch> boolean removeRule(
            final RuleInstance<Match> instance) {
        checkNotNull(instance, "Cannot remve null rule instance!");
        return removeRule(instance.getSpecification());
    }

    /**
     * Removes and disposes of a rule instance with the given specification.
     * 
     * @param specification
     * @return true, if the specification had an instance in the Agenda
     */
    protected <Match extends IPatternMatch> boolean removeRule(
            final RuleSpecification<Match> specification) {
        checkNotNull(specification, "Cannot remve null rule specification!");
        RuleInstance<? extends IPatternMatch> instance = ruleInstanceMap
                .get(specification);
        if (instance != null) {
            instance.dispose();
            ruleInstanceMap.remove(specification);
            return true;
        }
        return false;
    }

    /**
     * Disposes of each rule instance manaed by the agenda.
     * 
     */
    protected void dispose() {
        for (RuleInstance<? extends IPatternMatch> instance : ruleInstanceMap
                .values()) {
            instance.dispose();
        }
    }

    /**
     * @return the iqEngine
     */
    public IncQueryEngine getIncQueryEngine() {
        return iqEngine;
    }

    /**
     * @return an unmodifiable view of the ruleInstanceMap
     */
    public Map<RuleSpecification<? extends IPatternMatch>, RuleInstance<? extends IPatternMatch>> getRuleInstanceMap() {
        return Collections.unmodifiableMap(ruleInstanceMap);
    }

    /**
     * @return an immutable copy of the set of rule instances
     */
    public Set<RuleInstance<? extends IPatternMatch>> getRuleInstances() {
        return ImmutableSet.copyOf(ruleInstanceMap.values());
    }

    /**
     * Returns the instance managed by the Agenda for the given specification.
     * 
     * @param specification
     * @return the instance, if it exists, null otherwise
     */
    @SuppressWarnings("unchecked")
    public <Match extends IPatternMatch> RuleInstance<Match> getInstance(
            final RuleSpecification<Match> specification) {
        checkNotNull(specification, "Cannot get instance for null specification");
        return (RuleInstance<Match>) ruleInstanceMap.get(specification);
    }

    /**
     * @return the activations
     */
    public Multimap<ActivationState, Activation<?>> getActivations() {
        return activations;
    }

    /**
     * @return the enabledActivations
     */
    public Set<Activation<?>> getEnabledActivations() {
        return enabledActivations;
    }

    /**
     * Returns the activations that are in the given state
     * 
     * @param state
     * @return the activations in the given state
     */
    public Collection<Activation<?>> getActivations(final ActivationState state) {
        return activations.get(state);
    }

    /**
     * Returns the activations for the given specification, if it has
     * an instance in the Agenda.
     *
     * @param specification
     * @return the activations for the specification, if exists, empty set otherwise
     */
    public <Match extends IPatternMatch> Collection<Activation<Match>> getActivations(
            final RuleSpecification<Match> specification) {
        RuleInstance<Match> instance = getInstance(specification);
        if (instance == null) {
            return Collections.emptySet();
        } else {
            return instance.getAllActivations();
        }
    }

    /**
     * 
     * @return all activations in a single collection
     */
    public Collection<Activation<?>> getAllActivations() {
        return activations.values();
    }

    /**
     * Allows the setting of a comparator to be used for ordering activations.
     * 
     * @param activationComparator
     */
    public void setActivationComparator(final Comparator<Activation<?>> activationComparator) {
        checkNotNull(activationComparator, "Comparator cannot be null!");
        this.activationComparator = activationComparator;
        TreeMultimap<ActivationState, Activation<?>> newActivations = TreeMultimap.create(Ordering.natural(),
                activationComparator);
        newActivations.putAll(activations);
        activations = newActivations;

        TreeSet<Activation<?>> newEnabledActivations = Sets.newTreeSet(activationComparator);
        newEnabledActivations.addAll(enabledActivations);
        enabledActivations = newEnabledActivations;
    }
    
    /**
     * @return the activationComparator
     */
    public Comparator<Activation<?>> getActivationComparator() {
        return activationComparator;
    }

}
