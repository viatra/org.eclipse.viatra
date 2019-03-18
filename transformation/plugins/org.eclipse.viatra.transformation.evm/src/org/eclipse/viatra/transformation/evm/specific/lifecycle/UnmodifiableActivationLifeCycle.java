/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific.lifecycle;

import java.util.Objects;

import org.eclipse.viatra.transformation.evm.api.ActivationLifeCycle;
import org.eclipse.viatra.transformation.evm.api.event.ActivationState;
import org.eclipse.viatra.transformation.evm.api.event.EventType;

/**
 * This class represents life-cycles that cannot be modified. 
 * Attempts to add new transitions will result in {@link UnsupportedOperationException}.
 * 
 * @author Abel Hegedus
 *
 */
public class UnmodifiableActivationLifeCycle extends ActivationLifeCycle{

    /**
     * @param inactiveState
     */
    protected UnmodifiableActivationLifeCycle(ActivationState inactiveState) {
        super(inactiveState);
    }

    @Override
    public boolean addStateTransition(final ActivationState from, final EventType event, final ActivationState to) {
        throw new UnsupportedOperationException("Life cycle is unmodifiable!");
    }
    
    /**
     * Internal method to add transitions to an unmodifiable life-cycle.
     *  
     * @param activationState
     * @param eventType
     * @param activationState2
     * @return
     */
    protected boolean internalAddStateTransition(final ActivationState activationState, final EventType eventType, final ActivationState activationState2) {
        return super.addStateTransition(activationState, eventType, activationState2);
    }
    
    /**
     * Creates an unmodifiable copy of the given life-cycle.
     * 
     * @param lifeCycle
     */
    public static UnmodifiableActivationLifeCycle copyOf(final ActivationLifeCycle lifeCycle) {
        if(lifeCycle instanceof UnmodifiableActivationLifeCycle) {
            return (UnmodifiableActivationLifeCycle) lifeCycle;
        } else {
            Objects.requireNonNull(lifeCycle,"Null life cycle cannot be copied!");
            UnmodifiableActivationLifeCycle lc = new UnmodifiableActivationLifeCycle(lifeCycle.getInactiveState());
            for (Transition cell : lifeCycle.getStateTransitions()) {
                lc.internalAddStateTransition(cell.getFrom(), cell.getEvent(), cell.getTo());
            }
            return lc; 
        }
    }
}
