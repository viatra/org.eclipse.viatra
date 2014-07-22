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
