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
package org.eclipse.viatra.query.runtime.rete.network.mailbox.ddf;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.Receiver;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.communication.CommunicationGroup;
import org.eclipse.viatra.query.runtime.rete.network.communication.MessageSelector;
import org.eclipse.viatra.query.runtime.rete.network.communication.ddf.DifferentialTimestamp;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.AdaptableMailbox;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.FallThroughMailbox;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.Mailbox;

public class DifferentialMailbox implements AdaptableMailbox, FallThroughMailbox {

    protected TreeMap<DifferentialTimestamp, Map<Tuple, Integer>> queue;
    protected TreeMap<DifferentialTimestamp, Map<Tuple, Integer>> buffer;
    protected final Receiver receiver;
    protected final ReteContainer container;
    protected boolean delivering;
    protected Mailbox adapter;
    protected CommunicationGroup group;
    protected boolean fallThrough;

    public DifferentialMailbox(final Receiver receiver, final ReteContainer container) {
        this.receiver = receiver;
        this.container = container;
        this.queue = CollectionsFactory.createTreeMap();
        this.buffer = CollectionsFactory.createTreeMap();
        this.adapter = this;
    }

    protected TreeMap<DifferentialTimestamp, Map<Tuple, Integer>> getActiveQueue() {
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
    public void postMessage(final Direction direction, final Tuple update, final DifferentialTimestamp timestamp) {
        final TreeMap<DifferentialTimestamp, Map<Tuple, Integer>> activeQueue = getActiveQueue();

        Map<Tuple, Integer> tupleMap = activeQueue.get(timestamp);
        final boolean wasEmpty = tupleMap == null;
        boolean significantChange = false;

        if (tupleMap == null) {
            tupleMap = CollectionsFactory.createMap();
            activeQueue.put(timestamp, tupleMap);
            significantChange = true;
        }

        Integer count = tupleMap.get(update);
        if (count == null) {
            count = 0;
            significantChange = true;
        }

        if (direction == Direction.REVOKE) {
            count--;
        } else {
            count++;
        }

        if (count == 0) {
            tupleMap.remove(update);
            if (tupleMap.isEmpty()) {
                activeQueue.remove(timestamp);
            }
            significantChange = true;
        } else {
            tupleMap.put(update, count);
        }

        if (significantChange) {
            final Mailbox targetMailbox = this.adapter;
            final CommunicationGroup targetGroup = this.adapter.getCurrentGroup();

            if (wasEmpty) {
                targetGroup.notifyHasMessage(targetMailbox, timestamp);
            } else if (tupleMap.isEmpty()) {
                targetGroup.notifyLostAllMessages(targetMailbox, timestamp);
            }
        }
    }

    @Override
    public void deliverAll(final MessageSelector selector) {
        if (selector instanceof DifferentialTimestamp) {
            final DifferentialTimestamp timestamp = (DifferentialTimestamp) selector;
            // use the buffer during delivering so that there is a clear
            // separation between the stages
            this.delivering = true;
            // REMOVE the tuples associated with the selector, not just query them
            final Map<Tuple, Integer> tupleMap = this.queue.remove(timestamp);

            for (final Entry<Tuple, Integer> entry : tupleMap.entrySet()) {
                int count = entry.getValue();

                Direction direction;
                if (count < 0) {
                    direction = Direction.REVOKE;
                    count = -count;
                } else {
                    direction = Direction.INSERT;
                }

                for (int i = 0; i < count; i++) {
                    this.receiver.update(direction, entry.getKey(), timestamp);
                }
            }

            this.delivering = false;

            mergeBufferIntoQueue();
            this.buffer = CollectionsFactory.createTreeMap();
        } else {
            throw new IllegalArgumentException("Unsupported message selector " + selector);
        }
    }

    protected void mergeBufferIntoQueue() {
        for (final Entry<DifferentialTimestamp, Map<Tuple, Integer>> outerEntry : this.buffer.entrySet()) {
            final DifferentialTimestamp selector = outerEntry.getKey();
            final Map<Tuple, Integer> tupleMap = this.queue.get(selector);
            if (tupleMap == null) {
                this.queue.put(selector, outerEntry.getValue());
            } else {
                for (final Entry<Tuple, Integer> innerEntry : outerEntry.getValue().entrySet()) {
                    final Tuple tuple = innerEntry.getKey();
                    final Integer queueCount = tupleMap.get(tuple);
                    final Integer bufferCount = innerEntry.getValue();
                    if (queueCount == null) {
                        tupleMap.put(tuple, bufferCount);
                    } else {
                        final int sum = bufferCount + queueCount;
                        if (sum != 0) {
                            tupleMap.put(tuple, sum);
                        } else {
                            tupleMap.remove(tuple);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "DDF_MBOX (" + this.receiver + ") " + this.getActiveQueue();
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

    @Override
    public CommunicationGroup getCurrentGroup() {
        return this.group;
    }

    @Override
    public void setCurrentGroup(final CommunicationGroup group) {
        this.group = group;
    }

    @Override
    public boolean isFallThrough() {
        return this.fallThrough;
    }

    @Override
    public void setFallThrough(final boolean fallThrough) {
        this.fallThrough = fallThrough;
    }

}
