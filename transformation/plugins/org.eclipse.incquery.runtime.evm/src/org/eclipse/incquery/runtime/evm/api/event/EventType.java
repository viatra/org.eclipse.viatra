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
package org.eclipse.incquery.runtime.evm.api.event;

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
