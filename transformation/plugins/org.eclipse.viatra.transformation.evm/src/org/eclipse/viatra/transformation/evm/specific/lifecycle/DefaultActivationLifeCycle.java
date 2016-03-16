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
package org.eclipse.viatra.transformation.evm.specific.lifecycle;

import org.eclipse.viatra.transformation.evm.api.event.EventType;
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDEventTypeEnum;
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDActivationStateEnum;

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

    /**
     * Creates an activation life cycle with the default state transition map.
     * 
     * @param updateStateUsed
     *            if set, the Updated activation state is also used
     * @param disappearedStateUsed
     *            if set, the Disappeared activations state is also used
     */
    public DefaultActivationLifeCycle(
            final boolean updateStateUsed,
            final boolean disappearedStateUsed) {
        
        super(CRUDActivationStateEnum.INACTIVE);

        internalAddStateTransition(CRUDActivationStateEnum.INACTIVE, CRUDEventTypeEnum.CREATED,
                CRUDActivationStateEnum.CREATED);

        internalAddStateTransition(CRUDActivationStateEnum.CREATED, CRUDEventTypeEnum.DELETED,
                CRUDActivationStateEnum.INACTIVE);
        internalAddStateTransition(CRUDActivationStateEnum.CREATED, EventType.RuleEngineEventType.FIRE,
                CRUDActivationStateEnum.FIRED);

        if (updateStateUsed) {
            internalAddStateTransition(CRUDActivationStateEnum.FIRED, CRUDEventTypeEnum.UPDATED,
                    CRUDActivationStateEnum.UPDATED);
            internalAddStateTransition(CRUDActivationStateEnum.UPDATED, EventType.RuleEngineEventType.FIRE,
                    CRUDActivationStateEnum.FIRED);
            if (disappearedStateUsed) {
                internalAddStateTransition(CRUDActivationStateEnum.UPDATED, CRUDEventTypeEnum.DELETED,
                        CRUDActivationStateEnum.DELETED);
            } else {
                internalAddStateTransition(CRUDActivationStateEnum.UPDATED, CRUDEventTypeEnum.DELETED,
                        CRUDActivationStateEnum.INACTIVE);
            }
        }

        if (disappearedStateUsed) {
            internalAddStateTransition(CRUDActivationStateEnum.FIRED, CRUDEventTypeEnum.DELETED,
                    CRUDActivationStateEnum.DELETED);
            if(updateStateUsed){
                internalAddStateTransition(CRUDActivationStateEnum.DELETED, CRUDEventTypeEnum.CREATED,
                        CRUDActivationStateEnum.UPDATED);
            } else {
                internalAddStateTransition(CRUDActivationStateEnum.DELETED, CRUDEventTypeEnum.CREATED,
                        CRUDActivationStateEnum.FIRED);
            }
            internalAddStateTransition(CRUDActivationStateEnum.DELETED, EventType.RuleEngineEventType.FIRE,
                    CRUDActivationStateEnum.INACTIVE);
        } else {
            internalAddStateTransition(CRUDActivationStateEnum.FIRED, CRUDEventTypeEnum.DELETED,
                    CRUDActivationStateEnum.INACTIVE);
        }

    }
    
}
