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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.viatra.cep.core.api.engine.CEPEngine;
import org.eclipse.viatra.cep.core.metamodels.events.Event;

/**
 * Steam of {@link Event}s to be processed by the {@link CEPEngine}.
 * 
 * <p>
 * The {@link EventStream} leverages the notification mechanism of EMF, therefore it is an extension to the
 * {@link EObjectImpl} type.
 * 
 * @author Istvan David
 * 
 */
public class EventStream extends EObjectImpl {
    private Queue<Event> queue = new ConcurrentLinkedQueue<Event>();

    public EventStream() {
        super();
    }

    /**
     * Adds an {@link Event} to the event queue. Every new Event is published to the interested adapters via EMF's
     * notification mechanism.
     * 
     * @param event
     *            the {@link Event} to be added
     */
    public void push(Event event) {
        if (event == null) {
            return;
        }
        Queue<Event> oldQueue = this.queue;
        this.queue.add(event);
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.ADD, null, oldQueue, event));
            queue.remove(event);
        }
    }

    /**
     * Returns the whole queue of {@link Event}s.
     * 
     * @return the queue containing yet unprocessed {@link Event}s
     */
    public Queue<Event> getQueue() {
        return this.queue;
    }
}
