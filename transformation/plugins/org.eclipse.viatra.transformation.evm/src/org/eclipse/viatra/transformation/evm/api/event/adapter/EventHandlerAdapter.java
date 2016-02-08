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
package org.eclipse.viatra.transformation.evm.api.event.adapter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.eclipse.viatra.transformation.evm.api.RuleInstance;
import org.eclipse.viatra.transformation.evm.api.event.Event;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.viatra.transformation.evm.api.event.EventHandler;
import org.eclipse.viatra.transformation.evm.api.event.EventSource;
import org.eclipse.viatra.transformation.evm.api.event.EventType;

/**
 * @author Abel Hegedus
 *
 */
public abstract class EventHandlerAdapter<EventAtom> implements EventHandler<EventAtom> {

    private final EventSource<EventAtom> source;
    private final EventFilter<? super EventAtom> filter;
    private final RuleInstance<EventAtom> instance;
    private Map<EventType, EventProcessorAdapter<EventAtom>> processors;
 
    protected EventHandlerAdapter(EventSource<EventAtom> source, EventFilter<? super EventAtom> filter, RuleInstance<EventAtom> instance) {
        checkArgument(source != null, "Cannot create handler with null source");
        checkArgument(filter != null, "Cannot create handler with null filter");
        checkArgument(instance != null, "Cannot create handler with null instance");
        this.instance = instance;
        this.source = source;
        this.filter = filter;
    }
    
    protected void prepareEventHandler() {
        processors = checkNotNull(prepareEventProcessors(), "Prepared event processor map was null!");
        instance.setHandler(this);
    }
    
    /**
     * Returns a map of event processor adapters related to the different event types. The method will be only called
     * once, so there is no need to cache the results internally.
     * 
     * @return
     */
    protected abstract Map<EventType, EventProcessorAdapter<EventAtom>> prepareEventProcessors();

    @Override
    public void handleEvent(Event<EventAtom> event) {
        EventAtom eventAtom = event.getEventAtom();
        if(filter.isProcessable(eventAtom)) {
            EventType eventType = event.getEventType();
            if(processors.containsKey(eventType)) {
                EventProcessorAdapter<EventAtom> processor = processors.get(eventType);
                processor.processEvent(event);
            }
        }
    }

    @Override
    public EventSource<EventAtom> getSource() {
        return source;
    }

    @Override
    public EventFilter<? super EventAtom> getEventFilter() {
        return filter;
    }

    /**
     * @return the instance
     */
    protected RuleInstance<EventAtom> getInstance() {
        return instance;
    }

}
