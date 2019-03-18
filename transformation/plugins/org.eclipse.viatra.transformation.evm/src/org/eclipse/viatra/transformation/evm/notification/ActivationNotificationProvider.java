/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.transformation.evm.notification;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.event.ActivationState;
import org.eclipse.viatra.transformation.evm.api.event.EventType;

/**
 * Classes implement this interface to provide notifications about the changes in the collection of activations within
 * the AbstractRule Engine.
 * 
 * @author Tamas Szabo
 * 
 */
public abstract class ActivationNotificationProvider implements IActivationNotificationProvider {

    private Set<IActivationNotificationListener> activationNotificationListeners;

    public ActivationNotificationProvider() {
        this.activationNotificationListeners = new HashSet<IActivationNotificationListener>();
    }

    @Override
    public boolean addActivationNotificationListener(final IActivationNotificationListener listener,
            final boolean fireNow) {
        boolean notContained = this.activationNotificationListeners.add(listener);
        if (notContained) {
            listenerAdded(listener, fireNow);
        }
        return notContained;
    }

    /**
     * Called when a new listener is added to the provider.
     * 
     * @param listener
     * @param fireNow if true, all existing activations should be sent as appear notifications,
     *  with inactive old state.
     */
    protected abstract void listenerAdded(final IActivationNotificationListener listener, final boolean fireNow);

    @Override
    public boolean removeActivationNotificationListener(final IActivationNotificationListener listener) {
        return this.activationNotificationListeners.remove(listener);
    }

    /**
     * Notifies listeners about an activation change.
     * 
     * @param activation
     * @param oldState
     * @param event
     */
    public void notifyActivationChanged(final Activation<?> activation,
            final ActivationState oldState, final EventType event) {
        for (IActivationNotificationListener listener : this.activationNotificationListeners) {
            listener.activationChanged(activation, oldState, event);
        }
    }
    
    /**
     * Notifies listeners about an activation creation.
     * 
     * @param activation
     * @param inactiveState
     */
    public void notifyActivationCreated(final Activation<?> activation,
            final ActivationState inactiveState) {
        for (IActivationNotificationListener listener : this.activationNotificationListeners) {
            listener.activationCreated(activation, inactiveState);
        }
    }
    
    /**
     * Notifies listeners about an activation removal.
     * 
     * @param activation
     * @param oldState
     */
    public void notifyActivationRemoved(final Activation<?> activation,
            final ActivationState oldState) {
        for (IActivationNotificationListener listener : this.activationNotificationListeners) {
            listener.activationRemoved(activation, oldState);
        }
    }
    
    

    /**
     * Disposes of the provider by unregistering all listeners.
     */
    public void dispose() {
        this.activationNotificationListeners.clear();
    }

}
