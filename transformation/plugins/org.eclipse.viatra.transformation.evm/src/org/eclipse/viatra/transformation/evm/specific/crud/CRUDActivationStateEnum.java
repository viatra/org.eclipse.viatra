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

package org.eclipse.viatra.transformation.evm.specific.crud;


/**
 * 
 * This enumeration represents the possible states of an Activation with a CRUD (create/read/update/delete) operations.
 * 
 * @author Abel Hegedus
 *
 */
public enum CRUDActivationStateEnum implements org.eclipse.viatra.transformation.evm.api.event.ActivationState {
    INACTIVE, CREATED, FIRED, UPDATED, DELETED;
    
    @Override
    public boolean isInactive() {
        return (this == INACTIVE);
    }
}
