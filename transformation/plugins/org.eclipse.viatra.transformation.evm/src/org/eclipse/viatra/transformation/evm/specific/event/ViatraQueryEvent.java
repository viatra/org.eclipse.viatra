/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific.event;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.transformation.evm.api.event.Event;
import org.eclipse.viatra.transformation.evm.api.event.EventType;
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDEventTypeEnum;

/**
 * @author Abel Hegedus
 *
 */
public class ViatraQueryEvent<Match extends IPatternMatch> implements Event<Match> {

    private Match eventMatch;
    private CRUDEventTypeEnum eventType;
    
    @Override
    public EventType getEventType() {
        return eventType;
    }

    @Override
    public Match getEventAtom() {
        return eventMatch;
    }
    
    protected ViatraQueryEvent(CRUDEventTypeEnum eventType, Match eventMatch) {
        Preconditions.checkArgument(eventType != null, "Cannot create event with null type!");
        Preconditions.checkArgument(eventMatch != null, "Cannot create event with null match!");
        this.eventType = eventType;
        this.eventMatch = eventMatch;
    }

}
