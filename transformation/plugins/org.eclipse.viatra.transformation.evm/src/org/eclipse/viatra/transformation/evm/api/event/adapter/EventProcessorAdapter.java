/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api.event.adapter;

import java.util.Map;
import java.util.Objects;

import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleInstance;
import org.eclipse.viatra.transformation.evm.api.event.ActivationState;
import org.eclipse.viatra.transformation.evm.api.event.Event;

/**
 *  This class is the common supertype for default event processors
 *  in an event handler.
 *  
 * @author Abel Hegedus
 *
 */
public abstract class EventProcessorAdapter<EventAtom> {

    private final RuleInstance<EventAtom> instance;

    protected EventProcessorAdapter(RuleInstance<EventAtom> instance) {
        this.instance = instance;
    }
    
    /**
     * This method is called with the atom corresponding to the
     * activation that is affected by the event.
     * 
     * If the Activation exists, {@link #activationExists(Event, Activation)} is called 
     * with the Activation object, otherwise {@link #activationMissing(Event)} 
     * with the atom object.
     * 
     * @param event
     */
    public void processEvent(Event<EventAtom> event) {
        Objects.requireNonNull(event,"Cannot process null event!");
        
        Map<ActivationState, Activation<EventAtom>> activationMap = getInstance().getActivations(event.getEventAtom());
        if(activationMap.size() > 0) {
            Preconditions.checkArgument(activationMap.size() == 1, "%s activations in the same rule for the same match", activationMap.size() == 0 ? "No" : "Multiple");
            Activation<EventAtom> act = activationMap.values().iterator().next();
            activationExists(event, act);
        } else {
            activationMissing(event);
        }
    }
    
    /**
     * This method is called by findActivationForAtom if the activation
     * already exists for the given atom.
     * 
     * @param activation
     */
    protected abstract void activationExists(Event<EventAtom> event, Activation<EventAtom> activation);
    
    /**
     * This method is called by findActivationForAtom if the activation
     * does not exists for the given atom.
     * 
     * @param atom
     */
    protected abstract void activationMissing(Event<EventAtom> event);

    protected RuleInstance<EventAtom> getInstance() {
        return instance;
    }
    
}
