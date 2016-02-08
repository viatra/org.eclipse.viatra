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
package org.eclipse.incquery.runtime.evm.specific.event;

import static com.google.common.base.Preconditions.checkArgument;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.evm.api.event.Event;
import org.eclipse.incquery.runtime.evm.api.event.EventType;

/**
 * @author Abel Hegedus
 *
 */
public class IncQueryEvent<Match extends IPatternMatch> implements Event<Match> {

    private Match eventMatch;
    private IncQueryEventTypeEnum eventType;
    
    @Override
    public EventType getEventType() {
        return eventType;
    }

    @Override
    public Match getEventAtom() {
        return eventMatch;
    }
    
    protected IncQueryEvent(IncQueryEventTypeEnum eventType, Match eventMatch) {
        checkArgument(eventType != null, "Cannot create event with null type!");
        checkArgument(eventMatch != null, "Cannot create event with null match!");
        this.eventType = eventType;
        this.eventMatch = eventMatch;
    }

}
