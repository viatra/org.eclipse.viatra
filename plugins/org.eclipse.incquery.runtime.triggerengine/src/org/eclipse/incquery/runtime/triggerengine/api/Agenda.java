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

package org.eclipse.incquery.runtime.triggerengine.api;

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
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.triggerengine.api.ActivationLifeCycle.ActivationLifeCycleEvent;
import org.eclipse.incquery.runtime.triggerengine.notification.IActivationNotificationListener;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;

/**
 * An Agenda is associated to each EMF instance model (more precisely {@link IncQueryEngine} and 
 * it is responsible for creating, managing and disposing rules in
 * the Rule Engine. It provides an unmodifiable view for the collection of applicable activations.
 * 
 * <p>
 * One must register an {@link IActivationNotificationListener} in order to receive notifications automatically about
 * the changes in the collection of activations.
 * 
 * <p>
 * The Trigger Engine is a collection of strategies which can be used to fire these activations with pre-defined
 * timings. Such strategies include the {@link AutomaticFiringStrategy} and {@link TimedFiringStrategy} at the current
 * state of development.
 * 
 * <p>
 * 
 * One may define whether multiple firing of the same activation is allowed; that is, only the Appeared state will be
 * used from the lifecycle of {@link Activation}s and consecutive firing of a previously applied {@link Activation} is
 * possible. For more information on the lifecycle see {@link Activation}. Multiple firing is used for example in Design
 * Space Exploration scenarios.
 * 
 * TODO rewrite code comments - multiple firing is implicitly allowed by defining a job that wokrs in the Fired state -
 * life-cycle is defined separately (see {@link ActivationLifeCycle}) - {@link Scheduler} and {@link Executor} are
 * separated from Agenda
 * 
 * @author Tamas Szabo
 * 
 */
public class Agenda {

    private final IncQueryEngine iqEngine;
    private final Map<RuleSpecification<? extends IPatternMatch, ? extends IncQueryMatcher<? extends IPatternMatch>>, RuleInstance<? extends IPatternMatch, ? extends IncQueryMatcher<? extends IPatternMatch>>> ruleInstanceMap;
    private Multimap<ActivationState, Activation<?>> activations;
    private Set<Activation<?>> enabledActivations;
    private final IActivationNotificationListener activationListener;

    /**
     * Instantiates a new Agenda instance with the given {@link IncQueryEngine}.
     * 
     * @param iqEngine
     *            the {@link IncQueryEngine} instance
     */
    protected Agenda(final IncQueryEngine iqEngine) {
        this.iqEngine = checkNotNull(iqEngine, "Cannot create Agenda with null IncQueryEngine");
        this.ruleInstanceMap = new HashMap<RuleSpecification<? extends IPatternMatch, ? extends IncQueryMatcher<? extends IPatternMatch>>, RuleInstance<? extends IPatternMatch, ? extends IncQueryMatcher<? extends IPatternMatch>>>();
        this.activations = HashMultimap.create();
        this.enabledActivations = Sets.newHashSet();

        this.activationListener = new IActivationNotificationListener() {

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
        };

    }

    /*
     * public Notifier getNotifier() { return iqEngine.getEmfRoot(); }
     */

    protected <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> RuleInstance<Match, Matcher> instantiateRule(
            final RuleSpecification<Match, Matcher> specification) {
        RuleInstance<Match, Matcher> rule = specification.instantiateRule(iqEngine);
        rule.addActivationNotificationListener(activationListener, true);
        ruleInstanceMap.put(specification, rule);
        return rule;
    }

    protected <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> boolean removeRule(
            final RuleInstance<Match, Matcher> rule) {
        if (ruleInstanceMap.containsValue(rule)) {
            rule.removeActivationNotificationListener(activationListener);
            rule.dispose();
            ruleInstanceMap.remove(rule.getSpecification());
            return true;
        }
        return false;
    }

    protected <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> boolean removeRule(
            final RuleSpecification<Match, Matcher> ruleSpecification) {
        RuleInstance<? extends IPatternMatch, ? extends IncQueryMatcher<? extends IPatternMatch>> rule = ruleInstanceMap
                .get(ruleSpecification);
        if (rule != null) {
            rule.removeActivationNotificationListener(activationListener);
            rule.dispose();
            ruleInstanceMap.remove(rule);
            return true;
        }
        return false;
    }

    protected void dispose() {
        for (RuleInstance<? extends IPatternMatch, ? extends IncQueryMatcher<? extends IPatternMatch>> rule : ruleInstanceMap
                .values()) {
            rule.dispose();
        }
    }

    /**
     * @return the iqEngine
     */
    public IncQueryEngine getIncQueryEngine() {
        return iqEngine;
    }

    /**
     * @return the ruleInstanceMap
     */
    public Map<RuleSpecification<? extends IPatternMatch, ? extends IncQueryMatcher<? extends IPatternMatch>>, RuleInstance<? extends IPatternMatch, ? extends IncQueryMatcher<? extends IPatternMatch>>> getRuleInstanceMap() {
        return Collections.unmodifiableMap(ruleInstanceMap);
    }

    /**
     * @return the set of rule instances
     */
    public Set<RuleInstance<? extends IPatternMatch, ? extends IncQueryMatcher<? extends IPatternMatch>>> getRuleInstances() {
        return ImmutableSet.copyOf(ruleInstanceMap.values());
    }

    @SuppressWarnings("unchecked")
    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> RuleInstance<Match, Matcher> getInstance(
            final RuleSpecification<Match, Matcher> ruleSpecification) {
        return (RuleInstance<Match, Matcher>) ruleInstanceMap.get(ruleSpecification);
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

    public Collection<Activation<?>> getActivations(final ActivationState state) {
        return activations.get(state);
    }

    public <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> Collection<Activation<Match>> getActivations(
            final RuleSpecification<Match, Matcher> ruleSpecification) {
        RuleInstance<Match, Matcher> instance = getInstance(ruleSpecification);
        if (instance == null) {
            return Collections.emptySet();
        } else {
            return instance.getAllActivations();
        }
    }

    /**
     * 
     * @return
     */
    public Collection<Activation<?>> getAllActivations() {
        return activations.values();
    }

    protected void addActivationOrdering(final Comparator<Activation<?>> activationComparator) {
        if (activationComparator != null) {
            TreeMultimap<ActivationState, Activation<?>> newActivations = TreeMultimap.create(null,
                    activationComparator);
            newActivations.putAll(activations);
            activations = newActivations;

            TreeSet<Activation<?>> newEnabledActivations = Sets.newTreeSet(activationComparator);
            newEnabledActivations.addAll(enabledActivations);
            enabledActivations = newEnabledActivations;
        }
    }

}
