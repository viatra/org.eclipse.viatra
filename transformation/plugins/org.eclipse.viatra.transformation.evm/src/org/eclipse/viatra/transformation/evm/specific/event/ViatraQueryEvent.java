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
package org.eclipse.viatra.transformation.evm.specific.event;

import static com.google.common.base.Preconditions.checkArgument;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
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
        checkArgument(eventType != null, "Cannot create event with null type!");
        checkArgument(eventMatch != null, "Cannot create event with null match!");
        this.eventType = eventType;
        this.eventMatch = eventMatch;
    }

}
