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

import java.util.Set;

import org.eclipse.viatra.transformation.evm.api.event.Event;
import org.eclipse.viatra.transformation.evm.api.event.EventHandler;
import org.eclipse.viatra.transformation.evm.api.event.EventRealm;
import org.eclipse.viatra.transformation.evm.api.event.EventSource;
import org.eclipse.viatra.transformation.evm.api.event.EventSourceSpecification;

import com.google.common.collect.Sets;

/**
 * @author Abel Hegedus
 *
 */
public abstract class EventSourceAdapter<EventAtom> implements EventSource<EventAtom> {

    private final EventSourceSpecification<EventAtom> specification;
    private final EventRealm realm;
    private final Set<EventHandler<EventAtom>> handlers;

    public EventSourceAdapter(EventSourceSpecification<EventAtom> specification, EventRealm realm) {
        checkArgument(realm != null, "Cannot create event source for null realm!");
        checkArgument(specification != null, "Cannot create event source for null source specification!");
        this.specification = specification;
        this.realm = realm;
        this.handlers = Sets.newHashSet();
    }

    
    @Override
    public EventSourceSpecification<EventAtom> getSourceSpecification() {
        return specification;
    }

    @Override
    public EventRealm getRealm() {
        return realm;
    }

    public boolean addHandler(EventHandler<EventAtom> handler) {
        checkArgument(handler != null, "Handler cannot be null!");
        boolean empty = handlers.isEmpty();
        beforeHandlerAdded(handler, empty);
        boolean added = handlers.add(handler);
        if(handlers.add(handler)) {
            afterHandlerAdded(handler, empty);
        }
        return added;
    }
    


    public boolean removeHandler(EventHandler<EventAtom> handler) {
        checkArgument(handler != null, "Handler cannot be null!");
        beforeHandlerRemoved(handler, handlers.size() == 1);
        boolean removed = handlers.remove(handler);
        if(removed) {
            afterHandlerRemoved(handler, handlers.isEmpty());
        }
        return removed;
    }

    public void notifyHandlers(Event<EventAtom> event) {
        for (EventHandler<EventAtom> handler : handlers) {
            handler.handleEvent(event);
        }
    }

    protected void beforeHandlerAdded(EventHandler<EventAtom> handler, boolean handlersEmpty) {}
    protected void afterHandlerAdded(EventHandler<EventAtom> handler, boolean firstHandler) {}

    protected void beforeHandlerRemoved(EventHandler<EventAtom> handler, boolean lastHandler) {}
    protected void afterHandlerRemoved(EventHandler<EventAtom> handler, boolean handlersEmpty) {}

    protected abstract void prepareSource();
    
    @Override
    public void dispose() {
        for (EventHandler<EventAtom> handler : this.handlers) {
            handler.dispose();
        }
        this.handlers.clear(); // in case handler didn't remove itself
    }

}
