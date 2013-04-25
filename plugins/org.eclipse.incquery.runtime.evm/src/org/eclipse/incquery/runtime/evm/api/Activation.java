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

package org.eclipse.incquery.runtime.evm.api;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.incquery.runtime.evm.api.event.Atom;

import com.google.common.base.Objects;

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
 * @param <Match>
 *            the type of the pattern match
 */
public class Activation {

    private Atom atom;
    private ActivationState state;
    private boolean enabled;
    private RuleInstance instance;
    private int cachedHash = -1;

    protected Activation(RuleInstance instance, Atom atom) {
        this.atom = checkNotNull(atom,"Cannot create activation with null patternmatch");
        this.instance = checkNotNull(instance,"Cannot create activation with null instance");
        this.state = ActivationState.INACTIVE;
    }

    public Atom getAtom() {
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
    public RuleInstance getInstance() {
        return instance;
    }

    /**
     * Should be only set through {@link RuleInstance#activationStateTransition}
     * 
     * @param state
     */
    protected void setState(final ActivationState state) {
        this.state = checkNotNull(state, "Activation state cannot be null!");
        enabled = instance.getSpecification().getEnabledStates().contains(state);
    }

    /**
     * The activation will be fired; the appropriate job of the instance will be executed based on the activation state.
     */
    public void fire(final Context context) {
        checkNotNull(context,"Cannot fire activation with null context");
        instance.fire(this, context);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Activation) {
            Activation other = (Activation) obj;
            return (other.instance.equals(this.instance)) && (other.atom.equals(this.atom)
                    /*&& (other.state == this.state*/);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (cachedHash == -1) {
            cachedHash = Objects.hashCode(instance, atom/*, state*/);
        }
        return cachedHash;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("match",atom).
                add("state",state).toString();
    }
}
