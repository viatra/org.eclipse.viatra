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
package org.eclipse.incquery.runtime.evm.api.event.adapter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.RuleInstance;
import org.eclipse.incquery.runtime.evm.api.event.ActivationState;
import org.eclipse.incquery.runtime.evm.api.event.Event;

/**
 *  This class is the common supertype for default event processors
 *  in an event handler.
 *  
 * @author Abel Hegedus
 *
 */
public abstract class EventProcessorAdapter<EventAtom> {

    private final RuleInstance<EventAtom> instance;

    /**
     * 
     */
    protected EventProcessorAdapter(RuleInstance<EventAtom> instance) {
        this.instance = instance;
    }
    
    /**
     * This method is called with the atom corresponding to the
     * activation that is affected by the event.
     * 
     * If the Activation exists, {@link #activationExists(Event, Activation) 8)} is called 
     * with the Activation object, otherwise {@link #activationMissing(Event)} 
     * with the atom object.
     * 
     * @param event
     */
    public void processEvent(Event<EventAtom> event) {
        checkNotNull(event,"Cannot process null event!");
        
        Map<ActivationState, Activation<EventAtom>> column = getInstance().getActivations().column(event.getEventAtom());
        if(column.size() > 0) {
            checkArgument(column.size() == 1, String.format("%s activations in the same rule for the same match",column.size() == 0 ? "No" : "Multiple"));
            Activation<EventAtom> act = column.values().iterator().next();
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

    /**
     * @return the instance
     */
    protected RuleInstance<EventAtom> getInstance() {
        return instance;
    }
    
}
