/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api.adapter;

import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.event.ActivationState;
import org.eclipse.viatra.transformation.evm.api.event.EventType;
import org.eclipse.viatra.transformation.evm.notification.IActivationNotificationListener;

/**
 * Adaptable {@link IActivationNotificationListener} implementation. It utilizes the {@link AdaptableEVM} to enable
 * listening to activation state change events.
 * 
 * @author Peter Lunk
 *
 */
public class AdaptableActivationNotificationListener implements IActivationNotificationListener {
    private final IActivationNotificationListener delegatedListener;
    private final AdaptableEVM adapterContainer;

    public AdaptableActivationNotificationListener(IActivationNotificationListener delegatedListener,
            AdaptableEVM adapterContainer) {
        this.delegatedListener = delegatedListener;
        this.adapterContainer = adapterContainer;
    }

    @Override
    public void activationChanged(Activation<?> activation, ActivationState oldState, EventType event) {
        adapterContainer.activationChanged(activation, oldState, event);
        delegatedListener.activationChanged(activation, oldState, event);

    }

    @Override
    public void activationCreated(Activation<?> activation, ActivationState inactiveState) {
        adapterContainer.activationCreated(activation, inactiveState);
        delegatedListener.activationCreated(activation, inactiveState);
    }

    @Override
    public void activationRemoved(Activation<?> activation, ActivationState oldState) {
        adapterContainer.activationRemoved(activation, oldState);
        delegatedListener.activationRemoved(activation, oldState);
    }

}
