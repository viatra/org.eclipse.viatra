/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.cep.core.engine.timing;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.viatra.cep.core.metamodels.automaton.EventToken;
import org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone;

/**
 * A {@link Map} providing functionality for tracking the {@link EventToken}s entering and leaving {@link TimedZone}s.
 * 
 * @author Istvan David
 * 
 */
public class TimingTable extends HashMap<TimedZone, TokenInTimedZone> {

    private static final long serialVersionUID = -2846008369972274162L;

    private static TimingTable instance;

    public static TimingTable getInstance() {
        if (instance == null) {
            instance = new TimingTable();
        }
        return instance;
    }

    /**
     * Records the {@link EventToken} entering the {@link TimedZone} by generating a timestamp for this event.
     * 
     * @param timedZone
     *            the {@link TimedZone} the {@link EventToken} enters
     * @param eventToken
     *            the {@link EventToken} entering the {@link TimedZone}
     */
    public void enterTimedZone(TimedZone timedZone, EventToken eventToken) {
        put(timedZone, new TokenInTimedZone(eventToken, new Date().getTime()));
        eventToken.getTimedZones().add(timedZone);
    }

    /**
     * Evaluates whether an {@link EventToken} which previously entered a {@link TimedZone} is allowed to leave the
     * zone, i.e. whether the (upper or lower) time constraint is satisfied or it is timed out.
     * 
     * @param timedZone
     *            the {@link TimedZone} the {@link EventToken} attempts to leave
     * @param eventToken
     *            the {@link EventToken} attempting to leave the {@link TimedZone}
     * @return whether the {@link EventToken} is allowed to leave the {@link TimedZone} or not
     */
    public boolean leaveTimedZone(TimedZone timedZone, EventToken eventToken) {
        long currentTime = new Date().getTime();
        long timeWindow = timedZone.getTime();
        TokenInTimedZone tokenInTimedZone = get(timedZone);

        eventToken.getTimedZones().remove(timedZone);

        remove(eventToken);

        return (currentTime - tokenInTimedZone.getEntryTimeStamp()) <= timeWindow;
    }
}
