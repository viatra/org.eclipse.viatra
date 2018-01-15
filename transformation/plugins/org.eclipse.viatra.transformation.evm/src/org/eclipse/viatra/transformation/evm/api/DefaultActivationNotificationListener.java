/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *   Peter Lunk - revised EVM structure for adapter support
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api;

import java.util.HashSet;

import org.eclipse.viatra.transformation.evm.api.event.ActivationState;
import org.eclipse.viatra.transformation.evm.api.event.EventType;
import org.eclipse.viatra.transformation.evm.notification.IActivationNotificationListener;

/**
 * This class is responsible for handling notifications sent by rule instances when an activation changes state.
 *
 * By default, the listener logs the change event and refreshes the activation collections.
 *
 * @author Abel Hegedus, Peter Lunk
 *
 */
public final class DefaultActivationNotificationListener implements IActivationNotificationListener {
    private Agenda agenda;
    
    DefaultActivationNotificationListener(Agenda agenda){
        this.agenda = agenda;
    }
    
    @Override
    public void activationChanged(final Activation<?> activation,
            final ActivationState oldState, final EventType event) {
        if(agenda.getLogger().isDebugEnabled()){
            agenda.getLogger().debug(
                String.format("%s -- %s --> %s on %s", oldState, event, activation.getState(), activation));
        }
        agenda.getActivations().remove(oldState, activation);
        final ActivationState state = activation.getState();
        if(!state.isInactive()) {
            agenda.getActivations().computeIfAbsent(state, st -> new HashSet<>()).add(activation);
        }
        agenda.getConflictSetUpdater().activationChanged(activation, oldState, event);
    }

    @Override
    public void activationCreated(final Activation<?> activation, final ActivationState inactiveState) {
        if(agenda.getLogger().isDebugEnabled()){
            agenda.getLogger().debug(
                String.format("%s -- CREATE --> %s on %s", inactiveState, activation.getState(), activation));
        }
        agenda.getConflictSetUpdater().activationCreated(activation, inactiveState);
        final ActivationState state = activation.getState();
        agenda.getActivations().computeIfAbsent(state, st -> new HashSet<>()).add(activation);
    }

    @Override
    public void activationRemoved(final Activation<?> activation, final ActivationState oldState) {
        if(agenda.getLogger().isDebugEnabled()){
            agenda.getLogger().debug(
                String.format("%s -- REMOVE --> %s on %s", oldState, activation.getState(), activation));
        }
        agenda.getActivations().remove(oldState, activation);
        agenda.getConflictSetUpdater().activationRemoved(activation, oldState);
    }
}