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
package org.eclipse.viatra.query.runtime.rete.network.mailbox;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.rete.network.CommunicationGroup;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.MessageKind;
import org.eclipse.viatra.query.runtime.rete.network.Receiver;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;

/**
 * Default mailbox implementation.
 * <p>
 * Usually, the mailbox performs counting of messages so that they can cancel each other out. However, if marked as a
 * fall-through mailbox, than update messages are delivered directly to the receiver node to reduce overhead.
 * 
 * @author Tamas Szabo
 * @since 2.0
 */
public class DefaultMailbox implements AdaptableMailbox {

    private static int SIZE_TRESHOLD = 127;

    protected Map<Tuple, Integer> queue;
    protected Map<Tuple, Integer> buffer;
    protected final Receiver receiver;
    protected final ReteContainer container;
    protected boolean delivering;
    protected Mailbox adapter;

    /**
     * @since 1.7
     */
    protected CommunicationGroup group = null;

    public DefaultMailbox(final Receiver receiver, final ReteContainer container) {
        this.receiver = receiver;
        this.container = container;
        this.queue = CollectionsFactory.createMap();
        this.buffer = CollectionsFactory.createMap();
    }

    protected Map<Tuple, Integer> getActiveQueue() {
        if (this.delivering) {
            return this.buffer;
        } else {
            return this.queue;
        }
    }

    @Override
    public Mailbox getAdapter() {
        return this.adapter;
    }

    @Override
    public void setAdapter(final Mailbox adapter) {
        this.adapter = adapter;
    }

    @Override
    public boolean isEmpty() {
        return getActiveQueue().isEmpty();
    }

    @Override
    public void postMessage(final Direction direction, final Tuple update) {
        final Map<Tuple, Integer> activeQueue = getActiveQueue();
        final boolean wasEmpty = activeQueue.isEmpty();

        boolean significantChange;
        Integer count = activeQueue.get(update);
        if (count == null) {
            count = 0;
            significantChange = true;
        } else {
            significantChange = false;
        }

        if (direction == Direction.REVOKE) {
            count--;
        } else {
            count++;
        }

        if (count == 0) {
            activeQueue.remove(update);
            significantChange = true;
        } else {
            activeQueue.put(update, count);
        }

        if (significantChange) {
            final CommunicationGroup targetGroup = this.getAdapter() != null ? this.getAdapter().getCurrentGroup()
                    : this.getCurrentGroup();
            final Mailbox targetMailbox = this.getAdapter() != null ? this.getAdapter() : this;

            if (wasEmpty) {
                targetGroup.notifyHasMessage(targetMailbox, MessageKind.DEFAULT);
            } else if (activeQueue.isEmpty()) {
                targetGroup.notifyLostAllMessages(targetMailbox, MessageKind.DEFAULT);
            }
        }
    }

    @Override
    public void deliverAll(final MessageKind kind) {
        if (kind == MessageKind.DEFAULT) {
            // use the buffer during delivering so that there is a clear
            // separation
            // between the stages
            this.delivering = true;

            for (Entry<Tuple, Integer> entry : this.queue.entrySet()) {
                int count = entry.getValue();

                Direction direction;
                if (count < 0) {
                    direction = Direction.REVOKE;
                    count = -count;
                } else {
                    direction = Direction.INSERT;
                }

                for (int i = 0; i < count; i++) {
                    this.receiver.update(direction, entry.getKey());
                }
            }

            this.delivering = false;

            if (queue.size() > SIZE_TRESHOLD) {
                this.queue = this.buffer;
                this.buffer = CollectionsFactory.createMap();
            } else {
                this.queue.clear();
                final Map<Tuple, Integer> tmpQueue = this.queue;
                this.queue = this.buffer;
                this.buffer = tmpQueue;
            }
        } else {
            throw new IllegalArgumentException("Unsupported message kind " + kind);
        }
    }

    @Override
    public String toString() {
        return "D_MBOX (" + this.receiver + ") " + this.getActiveQueue();
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

    public CommunicationGroup getCurrentGroup() {
        return this.group;
    }

    public void setCurrentGroup(final CommunicationGroup group) {
        this.group = group;
    }

}
