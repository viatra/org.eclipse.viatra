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
import java.util.Set;

import org.eclipse.incquery.runtime.evm.api.event.ActivationState;
import org.eclipse.incquery.runtime.evm.api.event.EventType;
import org.eclipse.incquery.runtime.evm.notification.IActivationNotificationListener;

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
        public void activationChanged(final Activation<?> activation,
                final ActivationState oldState, final EventType event) {
            ruleBase.getLogger().debug(
                    String.format("%s -- %s --> %s on %s", oldState, event, activation.getState(), activation));
            getActivations().remove(oldState, activation);
            ActivationState state = activation.getState();
            if(state.isInactive()) {
                conflictSet.removeActivation(activation);
            } else {
                refreshActivation(activation, state);
            }
        }

        @Override
        public void activationCreated(Activation<?> activation, ActivationState inactiveState) {
            ruleBase.getLogger().debug(
                    String.format("%s -- CREATE --> %s on %s", inactiveState, activation.getState(), activation));
            ActivationState state = activation.getState();
            refreshActivation(activation, state);
        }

        @Override
        public void activationRemoved(Activation<?> activation, ActivationState oldState) {
            ruleBase.getLogger().debug(
                    String.format("%s -- REMOVE --> %s on %s", oldState, activation.getState(), activation));
            getActivations().remove(oldState, activation);
            conflictSet.removeActivation(activation);
        }

        private void refreshActivation(final Activation<?> activation, ActivationState state) {
            if (activation.isEnabled()) {
                conflictSet.addActivation(activation);
            } else {
                conflictSet.removeActivation(activation);
            }
            getActivations().put(state, activation);
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
     * @return the activation selected as next in order by the conflict resolver
     */
    public Activation<?> getNextActivation() {
        return conflictSet.getNextActivation();
    }

    /**
     * @return the set of activations that are considered equal by the conflict resolver
     */
    public Set<Activation<?>> getNextActivations() {
        return conflictSet.getNextActivations();
    }

    /**
     * @return the set of activations in conflict (i.e. enabled)
     */
    public Set<Activation<?>> getConflictingActivations() {
        return conflictSet.getConflictingActivations();
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
        for (Activation<?> act : conflictSet.getConflictingActivations()) {
            set.addActivation(act);
        }
        this.conflictSet = set;
    }

}
