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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.transformation.evm.api.event.ActivationState;
import org.eclipse.viatra.transformation.evm.api.event.EventType;

/**
 * A life cycle describes how the state of an activation changes 
 * in response to an event. The internal representation is a state 
 * machine with each transition labeled with an activation life-cycle event.
 * 
 * @author Abel Hegedus
 * 
 */
public class ActivationLifeCycle {
    
    /**
     * @since 2.0
     */
    public static class Transition {
        private final ActivationState from;
        private final EventType event;
        private final ActivationState to;

        public Transition(ActivationState from, EventType event, ActivationState to) {
            super();
            Objects.requireNonNull(from, "From state cannot be null!");
            Objects.requireNonNull(event, "Event cannot be null!");
            Objects.requireNonNull(to, "To state cannot be null!");
            
            this.from = from;
            this.event = event;
            this.to = to;
        }

        public ActivationState getFrom() {
            return from;
        }

        public EventType getEvent() {
            return event;
        }

        public ActivationState getTo() {
            return to;
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, event, to);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Transition other = (Transition) obj;
            return Objects.equals(from, other.from) && Objects.equals(event, other.event)
                    && Objects.equals(to, other.to);
        }

        @Override
        public String toString() {
            return "Transition " + from + " --(" + event + ")--> " + to;
        }
        
        
    }
    
    //LinkedHashSet is used to maintain a deterministic ordering of transitions
    private final Set<Transition> transitions = new LinkedHashSet<>();
    private final Map<ActivationState, Map<EventType, Transition>> transitionMap = new HashMap<>();
    private final ActivationState inactiveState;
    
    protected ActivationLifeCycle(ActivationState inactiveState) {
        Preconditions.checkArgument(inactiveState != null, "Inactive state cannot be null");
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
        Objects.requireNonNull(currentState, "Cannot find next state for null current state");
        Objects.requireNonNull(event, "Cannot find next state for null event");
        
        return findTargetTransition(currentState, event).map(Transition::getTo).orElse(null);
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
        return addStateTransition(new Transition(from, event, to));
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
     * @since 2.0
     */
    public boolean addStateTransition(Transition transition) {
        Objects.requireNonNull(transition);
        
        findConflictingTransition(transition).filter(conflicting -> Objects.equals(transition, conflicting))
                .ifPresent(t -> {
                    transitions.remove(t);
                    if (transitionMap.containsKey(t.from)) {
                        transitionMap.get(t.from).remove(t.event);
                    }
                });
        if (transitions.contains(transition)) {
            return false;
        } else {
            transitionMap.computeIfAbsent(transition.from, tr -> new HashMap<>()).put(transition.event, transition);
            return transitions.add(transition);
        }
    }

    /**
     * Check that the life-cycle contains a transition from the given state.
     * 
     * @param state
     * @return true, if there is a transition from the given state, false otherwise
     */
    public boolean containsFrom(final ActivationState state) {
        return transitionMap.containsKey(state) && !transitionMap.get(state).isEmpty();
    }
    
    /**
     * Check that the life-cycle contains a transition to the given state.
     * 
     * @param state
     * @return true, if there is a transition to the given state, false otherwise
     */
    public boolean containsTo(final ActivationState state) {
        return transitions.stream().anyMatch(tr -> Objects.equals(tr.to, state));
    }
    
    /**
     * Creates a complete copy of the life-cycle
     * 
     * @param lifeCycle the life-cycée to be copied
     * @return the copy of the life-cycle
     */
    public static ActivationLifeCycle copyOf(final ActivationLifeCycle lifeCycle) {
        Objects.requireNonNull(lifeCycle,"Null life cycle cannot be copied!");
        
        ActivationLifeCycle lc = new ActivationLifeCycle(lifeCycle.inactiveState);
        lifeCycle.transitions.forEach(lc::addStateTransition);
        return lc;
    }
    
    public static ActivationLifeCycle create(final ActivationState inactiveState) {
        return new ActivationLifeCycle(inactiveState);
    }
    
    /**
     * Returns a copy of the transition table.
     * 
     * @since 2.0
     */
    public Set<Transition> getStateTransitions() {
        return new HashSet<>(transitions);
    }
    
    public ActivationState getInactiveState() {
        return inactiveState;
    }
    
    @Override
    public String toString() {
        return String.format("%s{transitions=%s}", getClass().getName(),
                transitions.stream().map(Object::toString).collect(Collectors.joining(", ")));
    }
    
    private Optional<Transition> findConflictingTransition(Transition tr) {
        return findTargetTransition(tr.from, tr.event);
    }
    
    private Optional<Transition> findTargetTransition(ActivationState from, EventType event) {
        return Optional.ofNullable(transitionMap.get(from)).map(table -> table.get(event));
    }
}
