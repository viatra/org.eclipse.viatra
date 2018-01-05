/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Abel Hegedus - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.transformation.evm.api;

import java.util.Objects;

import org.eclipse.viatra.transformation.evm.api.event.ActivationState;

/**
 * An {@link Activation} is a created for a {@link RuleInstance} when the preconditions (LHS) are fully satisfied with
 * some domain model elements and the instance becomes eligible for execution.
 * 
 * <p>
 * An Activation holds a state, a pattern match, the corresponding instance 5nstance. The state of the
 * Activation can be either Inactive, Appeared, Disappeared, Upgraded or Fired, while its actual
 * state will be managed by the life-cycle of its instance.
 * 
 * @author Tamas Szabo
 * 
 * @param <EventAtom>
 *            the type of the pattern match
 */
public class Activation<EventAtom> {

    private EventAtom atom;
    private ActivationState state;
    private boolean enabled;
    private RuleInstance<EventAtom> instance;
    private int cachedHash = -1;

    protected Activation(RuleInstance<EventAtom> instance, EventAtom atom, ActivationState initState) {
        this.atom = Objects.requireNonNull(atom,"Cannot create activation with null patternmatch");
        this.instance = Objects.requireNonNull(instance,"Cannot create activation with null instance");
        this.state = Objects.requireNonNull(initState, "Cannot create activation with null initial state");
    }

    public EventAtom getAtom() {
        return atom;
    }

    public ActivationState getState() {
        return state;
    }
    
    /**
     * An activatio is enabled, if the there are jobs corresponding
     *  to the state of the activation.
     * 
     * @return true, if there are jobs for the current state
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * @return the instance
     */
    public RuleInstance<EventAtom> getInstance() {
        return instance;
    }

    /**
     * Should be only set through {@link RuleInstance#activationStateTransition}
     * 
     * @param state
     */
    protected void setState(final ActivationState state) {
        this.state = Objects.requireNonNull(state, "Activation state cannot be null!");
        enabled = instance.getSpecification().getEnabledStates().contains(state);
    }

    /**
     * The activation will be fired; the appropriate job of the instance will be executed based on the activation state.
     */
    public void fire(final Context context) {
        Objects.requireNonNull(context,"Cannot fire activation with null context");
        instance.fire(this, context);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Activation) {
            Activation<?> other = (Activation<?>) obj;
            return (Objects.equals(other.instance, this.instance)) && (Objects.equals(other.atom, this.atom)
                    /*&& (other.state == this.state*/);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (cachedHash == -1) {
            cachedHash = Objects.hash(instance, atom/*, state*/);
        }
        return cachedHash;
    }
    
    @Override
    public String toString() {
        return String.format("%s{atom=%s, state=%s}", getClass().getName(), atom, state);
    }
}
