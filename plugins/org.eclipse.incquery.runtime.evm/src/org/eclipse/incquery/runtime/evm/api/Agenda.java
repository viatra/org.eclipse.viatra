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

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.evm.notification.IActivationNotificationListener;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;

/**
 * Sole purpose is the management all and ordering of enabled activations!
 * 
 * @author Abel Hegedus
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
            ruleBase.getIncQueryEngine().getLogger().debug(
                    String.format("%s -- %s --> %s on %s", oldState, event, activation.getState(), activation));
            getActivations().remove(oldState, activation);
            ActivationState state = activation.getState();
            switch (state) {
            case INACTIVE:
                getEnabledActivations().remove(activation);
                break;
            default:
                if (activation.isEnabled()) {
                    getEnabledActivations().add(activation);
                } else {
                    getEnabledActivations().remove(activation);
                }
                getActivations().put(state, activation);
                break;
            }
        }
    }

    
    private Multimap<ActivationState, Activation<?>> activations;
    //private IActivationOrdering<?> activationOrdering;
    //private Object activationContainer;
    private Set<Activation<?>> enabledActivations;
    private final IActivationNotificationListener activationListener;
    private final RuleBase ruleBase;
    private Comparator<Activation<?>> activationComparator;
    
    /**
     * 
     */
    public Agenda(RuleBase ruleBase, IActivationOrdering<?> activationOrdering) {
        this.ruleBase = ruleBase;
        activations = HashMultimap.create();
        this.enabledActivations = Sets.newHashSet();
        //this.activationOrdering = activationOrdering;
        //this.activationContainer = activationOrdering.createActivationContainer();
        this.activationListener = new DefaultActivationNotificationListener();
    }
    
//    public void setActivationOrdering(IActivationOrdering<?> activationOrdering) {
//        this.activationOrdering = activationOrdering;
//    }

    /**
     * @return the activations
     */
    public Multimap<ActivationState, Activation<?>> getActivations() {
        return activations;
    }

    /**
     * Returns the activations that are in the given state
     * 
     * @param state
     * @return the activations in the given state
     */
    public Collection<Activation<?>> getActivations(final ActivationState state) {
        return getActivations().get(state);
    }
    
    /**
     * 
     * @return all activations in a single collection
     */
    public Collection<Activation<?>> getAllActivations() {
        return getActivations().values();
    }
    
    /**
     * @return the enabledActivations
     */
    public Set<Activation<?>> getEnabledActivations() {
//        return activationOrdering.getActivations(activationContainer);
        return enabledActivations;
    }

    /**
     * @return the activationListener
     */
    public IActivationNotificationListener getActivationListener() {
        return activationListener;
    }
    
    /**
     * Allows the setting of a comparator to be used for ordering activations.
     * 
     * @param activationComparator
     */
    public void setActivationComparator(final Comparator<Activation<?>> activationComparator) {
        Preconditions.checkNotNull(activationComparator, "Comparator cannot be null!");
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
