/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan David - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.cep.core.engine.timing;

import org.eclipse.viatra.cep.core.metamodels.automaton.EventToken;
import org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone;

/**
 * DTO for representing {@link EventToken}s augmented with a timestamp upon previously entering a {@link TimedZone}.
 * 
 * @author Istvan David
 * 
 */
public class TokenInTimedZone {
    private EventToken eventToken;
    private Long entryTimeStamp;

    public TokenInTimedZone(EventToken eventToken, Long entryTimeStamp) {
        this.eventToken = eventToken;
        this.entryTimeStamp = entryTimeStamp;
    }

    public EventToken getEventToken() {
        return eventToken;
    }

    public Long getEntryTimeStamp() {
        return entryTimeStamp;
    }
}
