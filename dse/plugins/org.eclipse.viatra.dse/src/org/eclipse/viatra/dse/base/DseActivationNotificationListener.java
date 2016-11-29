/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.base;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.statecode.IStateCoder;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.Agenda;
import org.eclipse.viatra.transformation.evm.api.event.ActivationState;
import org.eclipse.viatra.transformation.evm.api.event.EventType;
import org.eclipse.viatra.transformation.evm.notification.IActivationNotificationListener;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * This class is responsible for maintaining an activation - activation code bidirectional map for maintaining
 * activation codes incrementally.
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class DseActivationNotificationListener implements IActivationNotificationListener {

    protected IActivationNotificationListener defaultActivationListener;
    protected BiMap<Activation<?>, Object> activationIds;
    protected IStateCoder stateCoder;

    protected Set<Activation<?>> newActivations = new HashSet<>();
    protected Set<Activation<?>> removedActivations = new HashSet<>();
    
    public DseActivationNotificationListener(Agenda agenda, IStateCoder stateCoder) {
        defaultActivationListener = agenda.getActivationListener();
        activationIds = HashBiMap.create();
        this.stateCoder = stateCoder;
    }

    private Object createActivationCode(Activation<?> activation) {
        return stateCoder.createActivationCode((IPatternMatch) activation.getAtom());
    }

    @Override
    public void activationRemoved(Activation<?> activation, ActivationState oldState) {
        defaultActivationListener.activationRemoved(activation, oldState);
        newActivations.remove(activation);
        removedActivations.add(activation);
    }

    @Override
    public void activationCreated(Activation<?> activation, ActivationState inactiveState) {
        defaultActivationListener.activationCreated(activation, inactiveState);
        newActivations.add(activation);
        removedActivations.remove(activation);
    }

    @Override
    public void activationChanged(Activation<?> activation, ActivationState oldState, EventType event) {
        defaultActivationListener.activationChanged(activation, oldState, event);
        if (event.equals(EventType.RuleEngineEventType.FIRE)) {
            return;
        }
        throw new DSEException("Unhandled case of activation change. If this happens, it is a bug.");
    }

    public Object getActivationId(Activation<?> activation) {
        return activationIds.get(activation);
    }

    public Activation<?> getActivation(Object activationId) {
        return activationIds.inverse().get(activationId);
    }

    public BiMap<Activation<?>, Object> getActivationIds() {
        return activationIds;
    }
    
    public void updateActivationCodes() {
        for (Activation<?> activation : removedActivations) {
            activationIds.remove(activation);
        }
        for (Activation<?> activation : newActivations) {
            Object activationId = createActivationCode(activation);
            if (activationIds.containsValue(activationId)) {
                System.out.println("Same activation id.");
                return;
            }
            activationIds.put(activation, activationId);
        }
        removedActivations.clear();
        newActivations.clear();
    }
}
