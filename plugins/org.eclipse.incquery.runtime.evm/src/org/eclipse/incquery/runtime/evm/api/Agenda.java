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

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.evm.notification.IActivationNotificationListener;
import org.eclipse.incquery.runtime.evm.specific.ComparingConflictResolver;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

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
                conflictSet.removeActivation(activation);
                break;
            default:
                if (activation.isEnabled()) {
                    conflictSet.addActivation(activation);
                } else {
                    conflictSet.removeActivation(activation);
                }
                getActivations().put(state, activation);
                break;
            }
        }
    }

    
    private Multimap<ActivationState, Activation<?>> activations;
    private ConflictSet conflictSet;
    private final IActivationNotificationListener activationListener;
    private final RuleBase ruleBase;
    
    /**
     * 
     */
    public Agenda(RuleBase ruleBase, ConflictResolver<?> conflictResolver) {
        this.ruleBase = ruleBase;
        activations = HashMultimap.create();
        this.conflictSet = conflictResolver.createConflictSet();
        this.activationListener = new DefaultActivationNotificationListener();
    }
    
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
    @Deprecated
    public Set<Activation<?>> getEnabledActivations() {
        return conflictSet.getEnabledActivations();
    }
    /**
     * @return the enabledActivations
     */
    public Activation<?> getNextActivation() {
        return conflictSet.getNextActivation();
    }

    /**
     * @return the activationListener
     */
    public IActivationNotificationListener getActivationListener() {
        return activationListener;
    }
    
    /**
     * 
     * @param resolver
     */
    public void setConflictResolver(ConflictResolver<?> resolver) {
        ConflictSet set = resolver.createConflictSet();
        for (Activation<?> act : conflictSet.getEnabledActivations()) {
            set.addActivation(act);
        }
        this.conflictSet = set;
    }

    /**
     * Allows the setting of a comparator to be used for ordering activations.
     * 
     * @param activationComparator
     */
    @Deprecated
    public void setActivationComparator(final Comparator<Activation<?>> activationComparator) {
        Preconditions.checkNotNull(activationComparator, "Comparator cannot be null!");
        ComparingConflictResolver resolver = new ComparingConflictResolver(activationComparator);
        setConflictResolver(resolver);
    }

    /**
     * @return the activationComparator
     */
    @Deprecated
    public Comparator<Activation<?>> getActivationComparator() {
        if(conflictSet.getConflictResolver() instanceof ComparingConflictResolver) {
            ComparingConflictResolver resolver = (ComparingConflictResolver) conflictSet.getConflictResolver();
            return resolver.getComparator();
        }
        return null;
    }
}
