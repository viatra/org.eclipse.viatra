/*******************************************************************************
 * Copyright (c) 2010-2016, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.network;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

/**
 * Default mailbox implementation. The mailbox performs counting of messages so that they can cancel each other out.
 * 
 * @author Tamas Szabo
 */
public class DefaultMailbox implements Mailbox {

    protected Map<Tuple, Integer> queue;
    protected Map<Tuple, Integer> buffer;
    protected final Receiver receiver;
    protected final ReteContainer container;
    protected boolean delivering;
    protected final CommunicationTracker tracker;

    public DefaultMailbox() {
        this(null, null);
    }

    public DefaultMailbox(Receiver receiver, ReteContainer container) {
        this.receiver = receiver;
        this.container = container;
        this.tracker = container == null ? null : container.getTracker();
        this.queue = new LinkedHashMap<Tuple, Integer>();
        this.buffer = new LinkedHashMap<Tuple, Integer>();
    }

    protected Map<Tuple, Integer> getActiveQueue() {
        if (this.delivering) {
            return this.buffer;
        } else {
            return this.queue;
        }
    }

    protected Integer get(Tuple key) {
        return getActiveQueue().get(key);
    }

    protected boolean isEmpty() {
        return getActiveQueue().isEmpty();
    }

    protected Set<Tuple> keySet() {
        return getActiveQueue().keySet();
    }

    @Override
    public void postMessage(Direction direction, Tuple update) {
        Map<Tuple, Integer> activeQueue = getActiveQueue();

        Integer count = activeQueue.get(update);
        if (count == null) {
            count = 0;
        }

        if (direction == Direction.REVOKE) {
            count--;
        } else {
            count++;
        }

        if (count == 0) {
            activeQueue.remove(update);
        } else {
            activeQueue.put(update, count);
        }

        if (container != null) {
            if (activeQueue.isEmpty()) {
                tracker.notifyLostAllMessages(this, MessageKind.DEFAULT);
            } else {
                tracker.notifyHasMessage(this, MessageKind.DEFAULT);
            }
        }
    }

    @Override
    public void deliverAll(MessageKind kind) {
        // use the buffer during delivering so that there is a clear separation between the stages
        this.delivering = true;

        for (Tuple update : this.queue.keySet()) {
            int count = this.queue.get(update);
            Direction direction = count < 0 ? Direction.REVOKE : Direction.INSERT;
            for (int i = 0; i < Math.abs(count); i++) {
                this.receiver.update(direction, update);
            }
        }

        this.delivering = false;

        Map<Tuple, Integer> tmpQueue = this.queue;
        this.queue = this.buffer;
        this.buffer = tmpQueue;
        this.buffer.clear();
    }

    @Override
    public String toString() {
        return "MBOX (" + this.receiver + ") " + this.queue;
    }

    @Override
    public Receiver getReceiver() {
        return this.receiver;
    }

    @Override
    public void clear() {
        this.queue.clear();
        this.buffer.clear();
    }

}
