/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific.crud;

import org.eclipse.viatra.transformation.evm.api.event.EventType;

/**
 * This enumeration defines the set of events noticed by EVM given a CRUD (Create/Read/Update/Delete) operation set.
 * 
 * @author Abel Hegedus
 *
 */
public enum CRUDEventTypeEnum implements EventType{
    
    CREATED, DELETED, UPDATED;
    
}