/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api.resolver;

import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.event.ActivationState;
import org.eclipse.viatra.transformation.evm.api.event.EventType;
import org.eclipse.viatra.transformation.evm.notification.IActivationNotificationListener;

/**
 * @author Abel Hegedus
 *
 */
public class ConflictSetUpdater implements IActivationNotificationListener {
    /**
     *
     */
    private final ChangeableConflictSet changeableConflictSet;

    /**
     * @param changeableConflictSet
     */
    public ConflictSetUpdater(final ChangeableConflictSet changeableConflictSet) {
        this.changeableConflictSet = changeableConflictSet;
    }

    @Override
    public void activationRemoved(final Activation<?> activation, final ActivationState oldState) {
        changeableConflictSet.removeActivation(activation);
    }

    @Override
    public void activationCreated(final Activation<?> activation, final ActivationState inactiveState) {
        final ActivationState state = activation.getState();
        refreshActivation(activation, state);
    }

    @Override
    public void activationChanged(final Activation<?> activation, final ActivationState oldState, final EventType event) {
        final ActivationState state = activation.getState();
        if(state.isInactive()) {
            changeableConflictSet.removeActivation(activation);
        } else {
            refreshActivation(activation, state);
        }
    }

    private void refreshActivation(final Activation<?> activation, final ActivationState state) {
        if (activation.isEnabled()) {
            changeableConflictSet.addActivation(activation);
        } else {
            changeableConflictSet.removeActivation(activation);
        }
    }
}