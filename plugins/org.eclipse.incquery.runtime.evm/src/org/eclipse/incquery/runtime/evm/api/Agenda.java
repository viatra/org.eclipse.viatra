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

import org.eclipse.incquery.runtime.api.IPatternMatch;
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
        public void activationChanged(final Activation<? extends IPatternMatch> activation,
                final ActivationState oldState, final ActivationLifeCycleEvent event) {
            ruleBase.getIncQueryEngine().getLogger().debug(
                    String.format("%s -- %s --> %s on %s", oldState, event, activation.getState(), activation));
            getActivations().remove(oldState, activation);
            if(removeOnChange) {
                getEnabledActivations().remove(activation);
            }
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

    
    private final Multimap<ActivationState, Activation<?>> activations;
    private Set<Activation<?>> enabledActivations;
    private final IActivationNotificationListener activationListener;
    private final RuleBase ruleBase;
    private boolean removeOnChange;
    
    /**
     * 
     */
    public Agenda(RuleBase ruleBase, IActivationOrdering activationOrdering) {
        this.ruleBase = ruleBase;
        activations = HashMultimap.create();
        this.removeOnChange = activationOrdering.removeActivationOnChange();
        this.enabledActivations = activationOrdering.createActivationContainer();
        this.activationListener = new DefaultActivationNotificationListener();
    }
    
    public void setActivationOrdering(IActivationOrdering activationOrdering) {
        Set<Activation<?>> set = activationOrdering.createActivationContainer();
        removeOnChange = activationOrdering.removeActivationOnChange();
        set.addAll(enabledActivations);
        enabledActivations = set;
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
    public Set<Activation<?>> getEnabledActivations() {
        return enabledActivations;
    }

    /**
     * @return the activationListener
     */
    public IActivationNotificationListener getActivationListener() {
        return activationListener;
    }
    
}
