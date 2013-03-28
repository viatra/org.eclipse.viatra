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
package org.eclipse.incquery.runtime.evm.specific;

import org.eclipse.incquery.runtime.evm.api.ActivationLifeCycleEvent;
import org.eclipse.incquery.runtime.evm.api.ActivationState;

/**
 * This is the default implementation for an activation life cycle.
 * 
 * The following is the summary of the possible transitions, in the form of StateFrom -Event-> StateTo (U : Update state
 * used, D : Disappeared state used), :
 * <ul>
 * <li>Inactive -Match Appears-> Appeared</li>
 * <li>Appeared -Match Disappears-> Inactive</li>
 * <li>Appeared -Activation fires-> Fired</li>
 * <li>Fired -Match Updates-> Updated (U)</li>
 * <li>Updated -Activation fires-> Fired (U)</li>
 * <li>Updated -Match Disappears-> Inactive (U) / Disappeared (UD)</li>
 * <li>Fired -Match Disappears-> Inactive / Disappeared (D)</li>
 * <li>Disappeared -Match Appears-> Fired (D)</li>
 * <li>Disappeared -Activation fires-> Inactive (D)</li>
 * </ul>
 * 
 * @author Abel Hegedus
 * 
 */
public final class DefaultActivationLifeCycle extends UnmodifiableActivationLifeCycle {

    public static final DefaultActivationLifeCycle DEFAULT = new DefaultActivationLifeCycle();
    public static final DefaultActivationLifeCycle DEFAULT_NO_UPDATE = new DefaultActivationLifeCycle(false, true);
    public static final DefaultActivationLifeCycle DEFAULT_NO_DISAPPEAR = new DefaultActivationLifeCycle(true, false);
    public static final DefaultActivationLifeCycle DEFAULT_NO_UPDATE_AND_DISAPPEAR = new DefaultActivationLifeCycle(false, false);

    /**
     * Creates an activation life cycle with the default state transition map.
     * 
     * @param updateStateUsed
     *            if set, the Updated activation state is also used
     * @param disappearedStateUsed
     *            if set, the Disappeared activations state is also used
     */
    public DefaultActivationLifeCycle(final boolean updateStateUsed, final boolean disappearedStateUsed) {

        internalAddStateTransition(ActivationState.INACTIVE, ActivationLifeCycleEvent.MATCH_APPEARS,
                ActivationState.APPEARED);

        internalAddStateTransition(ActivationState.APPEARED, ActivationLifeCycleEvent.MATCH_DISAPPEARS,
                ActivationState.INACTIVE);
        internalAddStateTransition(ActivationState.APPEARED, ActivationLifeCycleEvent.ACTIVATION_FIRES,
                ActivationState.FIRED);

        if (updateStateUsed) {
            internalAddStateTransition(ActivationState.FIRED, ActivationLifeCycleEvent.MATCH_UPDATES,
                    ActivationState.UPDATED);
            internalAddStateTransition(ActivationState.UPDATED, ActivationLifeCycleEvent.ACTIVATION_FIRES,
                    ActivationState.FIRED);
            if (disappearedStateUsed) {
                internalAddStateTransition(ActivationState.UPDATED, ActivationLifeCycleEvent.MATCH_DISAPPEARS,
                        ActivationState.DISAPPEARED);
            } else {
                internalAddStateTransition(ActivationState.UPDATED, ActivationLifeCycleEvent.MATCH_DISAPPEARS,
                        ActivationState.INACTIVE);
            }
        }

        if (disappearedStateUsed) {
            internalAddStateTransition(ActivationState.FIRED, ActivationLifeCycleEvent.MATCH_DISAPPEARS,
                    ActivationState.DISAPPEARED);
            internalAddStateTransition(ActivationState.DISAPPEARED, ActivationLifeCycleEvent.MATCH_APPEARS,
                    ActivationState.FIRED);
            internalAddStateTransition(ActivationState.DISAPPEARED, ActivationLifeCycleEvent.ACTIVATION_FIRES,
                    ActivationState.INACTIVE);
        } else {
            internalAddStateTransition(ActivationState.FIRED, ActivationLifeCycleEvent.MATCH_DISAPPEARS,
                    ActivationState.INACTIVE);
        }

    }

    /**
     * Creates an activation life cycle with the default state transition map using both Updated and Disappeared states.
     */
    public DefaultActivationLifeCycle() {
        this(true, true);
    }

}
