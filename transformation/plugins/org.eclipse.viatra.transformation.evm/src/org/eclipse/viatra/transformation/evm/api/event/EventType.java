/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api.event;

/**
 * Interface for specifying a type of event that is provided by an event source.
 * 
 * @author Abel Hegedus
 *
 */
public interface EventType {
    
    public enum RuleEngineEventType implements EventType {
        FIRE;
    }
    
    /**
     * Event type to use in case CRUD (Create/Read/Update/Dispose) events. 
     */
    public enum DynamicEventType implements EventType {
        APPEARED, UPDATED, DISAPPEARED;
    }
}
