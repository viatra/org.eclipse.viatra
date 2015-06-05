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

import org.eclipse.incquery.runtime.evm.api.event.EventType;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryEventTypeEnum;

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
 * <li>Disappeared -Match Appears-> Fired (D) / Updated (UD) </li>
 * <li>Disappeared -Activation fires-> Inactive (D)</li>
 * </ul>
 * 
 * @author Abel Hegedus
 * 
 */
public final class DefaultActivationLifeCycle extends UnmodifiableActivationLifeCycle {

    public static final DefaultActivationLifeCycle DEFAULT = new DefaultActivationLifeCycle(true, true);
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
    protected DefaultActivationLifeCycle(
            final boolean updateStateUsed,
            final boolean disappearedStateUsed) {
        
        super(IncQueryActivationStateEnum.INACTIVE);

        internalAddStateTransition(IncQueryActivationStateEnum.INACTIVE, IncQueryEventTypeEnum.MATCH_APPEARS,
                IncQueryActivationStateEnum.APPEARED);

        internalAddStateTransition(IncQueryActivationStateEnum.APPEARED, IncQueryEventTypeEnum.MATCH_DISAPPEARS,
                IncQueryActivationStateEnum.INACTIVE);
        internalAddStateTransition(IncQueryActivationStateEnum.APPEARED, EventType.RuleEngineEventType.FIRE,
                IncQueryActivationStateEnum.FIRED);

        if (updateStateUsed) {
            internalAddStateTransition(IncQueryActivationStateEnum.FIRED, IncQueryEventTypeEnum.MATCH_UPDATES,
                    IncQueryActivationStateEnum.UPDATED);
            internalAddStateTransition(IncQueryActivationStateEnum.UPDATED, EventType.RuleEngineEventType.FIRE,
                    IncQueryActivationStateEnum.FIRED);
            if (disappearedStateUsed) {
                internalAddStateTransition(IncQueryActivationStateEnum.UPDATED, IncQueryEventTypeEnum.MATCH_DISAPPEARS,
                        IncQueryActivationStateEnum.DISAPPEARED);
            } else {
                internalAddStateTransition(IncQueryActivationStateEnum.UPDATED, IncQueryEventTypeEnum.MATCH_DISAPPEARS,
                        IncQueryActivationStateEnum.INACTIVE);
            }
        }

        if (disappearedStateUsed) {
            internalAddStateTransition(IncQueryActivationStateEnum.FIRED, IncQueryEventTypeEnum.MATCH_DISAPPEARS,
                    IncQueryActivationStateEnum.DISAPPEARED);
            if(updateStateUsed){
                internalAddStateTransition(IncQueryActivationStateEnum.DISAPPEARED, IncQueryEventTypeEnum.MATCH_APPEARS,
                        IncQueryActivationStateEnum.UPDATED);
            } else {
                internalAddStateTransition(IncQueryActivationStateEnum.DISAPPEARED, IncQueryEventTypeEnum.MATCH_APPEARS,
                        IncQueryActivationStateEnum.FIRED);
            }
            internalAddStateTransition(IncQueryActivationStateEnum.DISAPPEARED, EventType.RuleEngineEventType.FIRE,
                    IncQueryActivationStateEnum.INACTIVE);
        } else {
            internalAddStateTransition(IncQueryActivationStateEnum.FIRED, IncQueryEventTypeEnum.MATCH_DISAPPEARS,
                    IncQueryActivationStateEnum.INACTIVE);
        }

    }
    
    /**
     * Creates an activation life cycle with the default state transition map using both Updated and Disappeared states.
     * 
     * @deprecated Use {@link DefaultActivationLifeCycle(true, true, false)} instead.
     */
    public DefaultActivationLifeCycle() {
        this(true, true);
    }

}
