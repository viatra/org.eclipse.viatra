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
package org.eclipse.incquery.runtime.evm.api.resolver;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.event.ActivationState;
import org.eclipse.incquery.runtime.evm.api.event.EventType;
import org.eclipse.incquery.runtime.evm.notification.IActivationNotificationListener;

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