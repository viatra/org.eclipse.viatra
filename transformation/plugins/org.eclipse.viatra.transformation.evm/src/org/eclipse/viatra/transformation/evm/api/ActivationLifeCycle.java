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
package org.eclipse.viatra.transformation.evm.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.viatra.transformation.evm.api.event.ActivationState;
import org.eclipse.viatra.transformation.evm.api.event.EventType;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * A life cycle describes how the state of an activation changes 
 * in response to an event. The internal representation is a state 
 * machine with each transition labeled with an activation life-cycle event.
 * 
 * @author Abel Hegedus
 * 
 */
public class ActivationLifeCycle {
    
    private Table<ActivationState, EventType, ActivationState> stateTransitionTable;
    private ActivationState inactiveState;
    
    /**
     * 
     */
    protected ActivationLifeCycle(ActivationState inactiveState) {
        checkArgument(inactiveState != null, "Inactive state cannot be null");
        this.inactiveState = inactiveState;
    }
    
    /**
     * Returns the state in the life cycle that is defined as the next state
     * from the given current state in response to the given event.
     * 
     * If there is no transition for the given event, the method returns null.
     * 
     * @param currentState the current state of the activation
     * @param event the event that causes the state transition
     * @return the next state if defined, null otherwise
     */
    public ActivationState nextActivationState(final ActivationState currentState, final EventType event) {
        checkNotNull(currentState, "Cannot find next state for null current state");
        checkNotNull(event, "Cannot find next state for null event");
        if(stateTransitionTable != null) {
            return stateTransitionTable.get(currentState, event);
        } else {
            return null;
        }
    }
    
    /**
     * Extends the life cycle with a new transition that is created 
     * from the given state, labeled with the given event and leading
     *  to the given state. 
     * 
     * If the (from,event) transition already exists in the life-cycle,
     * it is overwritten with the given to state.
     * 
     * @param from the source state of the transition
     * @param event the event causing the transition
     * @param to the target state of the transition
     * @return true, if the life-cycle changed
     */
    public boolean addStateTransition(final ActivationState from, final EventType event, final ActivationState to) {
        checkNotNull(from, "From state cannot be null!");
        checkNotNull(event, "Event cannot be null!");
        checkNotNull(to, "To state cannot be null!");
        if(stateTransitionTable == null) {
            stateTransitionTable = HashBasedTable.create();
        }
        if(to.equals(stateTransitionTable.get(from, event))) {
            return false;
        } else {
            stateTransitionTable.put(from, event, to);
            return true;
        }
    }

    /**
     * Check that the life-cycle contains a transition from the given state.
     * 
     * @param state
     * @return true, if there is a transition from the given state, false otherwise
     */
    public boolean containsFrom(final ActivationState state) {
        return stateTransitionTable.containsRow(state);
    }
    
    /**
     * Check that the life-cycle contains a transition to the given state.
     * 
     * @param state
     * @return true, if there is a transition to the given state, false otherwise
     */
    public boolean containsTo(final ActivationState state) {
        return stateTransitionTable.containsValue(state);
    }
    
    /**
     * Creates a complete copy of the life-cycle
     * 
     * @param lifeCycle the life-cycée to be copied
     * @return the copy of the life-cycle
     */
    public static ActivationLifeCycle copyOf(final ActivationLifeCycle lifeCycle) {
        checkNotNull(lifeCycle,"Null life cycle cannot be copied!");
        ActivationLifeCycle lc = new ActivationLifeCycle(lifeCycle.inactiveState);
        lc.stateTransitionTable = HashBasedTable.create(lifeCycle.stateTransitionTable);
        return lc;
    }
    
    public static ActivationLifeCycle create(final ActivationState inactiveState) {
        return new ActivationLifeCycle(inactiveState);
    }
    
    /**
     * Returns a copy of the transition table.
     * 
     * @return the copy of the stateTransitionTable
     */
    public Table<ActivationState, EventType, ActivationState> getStateTransitionTable() {
        return HashBasedTable.create(stateTransitionTable);
    }
    
    /**
     * @return the inactiveState
     */
    public ActivationState getInactiveState() {
        return inactiveState;
    }
    
    @Override
    public String toString() {
        return String.format("%s{table=%s}", getClass().getName(), stateTransitionTable);
    }
}
