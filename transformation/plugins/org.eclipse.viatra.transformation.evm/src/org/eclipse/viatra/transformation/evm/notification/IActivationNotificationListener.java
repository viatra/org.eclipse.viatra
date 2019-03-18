/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.transformation.evm.notification;

import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.event.ActivationState;
import org.eclipse.viatra.transformation.evm.api.event.EventType;

/**
 * The interface is used to observe the changes in the collection of activations.
 * 
 * <p/>
 * An implementing class is for example the RuleBase which is called back by the RuleInstance
 * when those have updated the activations after an EMF operation.
 * 
 * @author Tamas Szabo
 * 
 */
public interface IActivationNotificationListener {

    /**
     * This method is called by {@link ActivationNotificationProvider} to relay changes to listeners.
     * 
     * <p/>
     * Implementing classes can use the old state, the event, and the new state (available from the activation).
     * 
     * @param activation
     * @param oldState
     * @param event
     */
    void activationChanged(final Activation<?> activation, final ActivationState oldState,
            final EventType event);

    void activationCreated(final Activation<?> activation, final ActivationState inactiveState);

    void activationRemoved(final Activation<?> activation, final ActivationState oldState);
    
}
