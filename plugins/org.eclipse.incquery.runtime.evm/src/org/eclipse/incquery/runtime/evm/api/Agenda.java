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

import org.eclipse.incquery.runtime.evm.api.event.ActivationState;
import org.eclipse.incquery.runtime.evm.api.event.EventType;
import org.eclipse.incquery.runtime.evm.api.resolver.ChangeableConflictSet;
import org.eclipse.incquery.runtime.evm.api.resolver.ConflictResolver;
import org.eclipse.incquery.runtime.evm.api.resolver.ConflictSetUpdater;
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

    private final Multimap<ActivationState, Activation<?>> activations;
    private ChangeableConflictSet conflictSet;
    private final IActivationNotificationListener activationListener;
    private final RuleBase ruleBase;
    private ConflictSetUpdater updatingListener;

    /**
     *
     */
    public Agenda(final RuleBase ruleBase, final ConflictResolver<?> conflictResolver) {
        this.ruleBase = ruleBase;
        activations = HashMultimap.create();
        this.conflictSet = conflictResolver.createConflictSet();
        this.updatingListener = new ConflictSetUpdater(conflictSet);
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
     * @return the activationListener
     */
    public IActivationNotificationListener getActivationListener() {
        return activationListener;
    }

    /**
     *
     * @param resolver
     */
    public void setConflictResolver(final ConflictResolver<?> resolver) {
        final ChangeableConflictSet set = resolver.createConflictSet();
        for (final Activation<?> act : conflictSet.getConflictingActivations()) {
            set.addActivation(act);
        }
        updatingListener = new ConflictSetUpdater(set);
        this.conflictSet = set;
    }

    /**
     * @return the conflictSet
     */
    public ChangeableConflictSet getConflictSet() {
        return conflictSet;
    }

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
            final ActivationState state = activation.getState();
            if(!state.isInactive()) {
                getActivations().put(state, activation);
            }
            updatingListener.activationChanged(activation, oldState, event);
        }

        @Override
        public void activationCreated(final Activation<?> activation, final ActivationState inactiveState) {
            ruleBase.getLogger().debug(
                    String.format("%s -- CREATE --> %s on %s", inactiveState, activation.getState(), activation));
            updatingListener.activationCreated(activation, inactiveState);
            final ActivationState state = activation.getState();
            getActivations().put(state, activation);
        }

        @Override
        public void activationRemoved(final Activation<?> activation, final ActivationState oldState) {
            ruleBase.getLogger().debug(
                    String.format("%s -- REMOVE --> %s on %s", oldState, activation.getState(), activation));
            getActivations().remove(oldState, activation);
            updatingListener.activationRemoved(activation, oldState);
        }
    }
}

