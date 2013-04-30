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
package org.eclipse.incquery.runtime.evm.specific.lifecycle;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.incquery.runtime.evm.api.ActivationLifeCycle;
import org.eclipse.incquery.runtime.evm.api.event.ActivationState;
import org.eclipse.incquery.runtime.evm.api.event.EventType;

import com.google.common.collect.Table.Cell;

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
     * @return
     */
    public static UnmodifiableActivationLifeCycle copyOf(final ActivationLifeCycle lifeCycle) {
        if(lifeCycle instanceof UnmodifiableActivationLifeCycle) {
            return (UnmodifiableActivationLifeCycle) lifeCycle;
        } else {
            checkNotNull(lifeCycle,"Null life cycle cannot be copied!");
            UnmodifiableActivationLifeCycle lc = new UnmodifiableActivationLifeCycle(lifeCycle.getInactiveState());
            for (Cell<ActivationState, EventType, ActivationState> cell : lifeCycle.getStateTransitionTable().cellSet()) {
                lc.internalAddStateTransition(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
            }
            return lc; 
        }
    }
}
