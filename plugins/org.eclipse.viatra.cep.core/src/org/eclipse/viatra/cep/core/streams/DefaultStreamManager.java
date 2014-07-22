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

package org.eclipse.viatra.cep.core.streams;

import java.util.List;

import org.eclipse.viatra.cep.core.engine.IEventModelManager;
import org.eclipse.viatra.cep.core.metamodels.events.Event;

import com.google.common.collect.Lists;

/**
 * Default implementation of the {@link IStreamManager} interface.
 * 
 * @author Istvan David
 * 
 */
public class DefaultStreamManager implements IStreamManager {
    private static IStreamManager instance;
    private IEventModelManager listener;

    private static final int MAX_STREAMS = 1;
    private List<EventStream> eventStreams = Lists.newArrayList();

    /**
     * @param eventModelManager
     *            the {@link IEventModelManager} the {@link EventStream}s will deliver {@link Event}s for
     * @return a new {@link IStreamManager}
     */
    public static IStreamManager getInstance(IEventModelManager eventModelManager) {
        if (instance == null) {
            instance = new DefaultStreamManager(eventModelManager);
        }
        return instance;
    }

    private DefaultStreamManager(IEventModelManager eventModelManager) {
        this.listener = eventModelManager;
    }

    @Override
    public EventStream newEventStream() {
        if (eventStreams.isEmpty() || eventStreams.size() < MAX_STREAMS) {
            EventStream newEventStream = new EventStream();
            eventStreams.add(newEventStream);
            listener.registerNewEventStream(newEventStream);
            return newEventStream;
        }

        // here some intelligence should be included to select the appropriate stream
        return eventStreams.get(0);
    }

    @Override
    public List<EventStream> getEventStreams() {
        return eventStreams;
    }
}
