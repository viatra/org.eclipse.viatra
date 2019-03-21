/*******************************************************************************
 * Copyright (c) 2010-2016, Gabor Bergmann, IncQueryLabs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.single;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.NetworkStructureChangeSensitiveNode;
import org.eclipse.viatra.query.runtime.rete.network.Receiver;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.communication.Timestamp;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.Mailbox;

/**
 * Node that sends tuples off to different buckets (attached as children of type {@link DiscriminatorBucketNode}), based
 * on the value of a given column.
 * 
 * <p>
 * Tuple contents and bucket keys have already been wrapped using {@link IQueryRuntimeContext#wrapElement(Object)}
 * 
 * @author Gabor Bergmann
 * @since 1.5
 */
public class DiscriminatorDispatcherNode extends SingleInputNode implements NetworkStructureChangeSensitiveNode {

    private int discriminationColumnIndex;
    private Map<Object, DiscriminatorBucketNode> buckets = new HashMap<>();
    private Map<Object, Mailbox> bucketMailboxes = new HashMap<>();

    /**
     * @param reteContainer
     */
    public DiscriminatorDispatcherNode(ReteContainer reteContainer, int discriminationColumnIndex) {
        super(reteContainer);
        this.discriminationColumnIndex = discriminationColumnIndex;
    }

    @Override
    public void update(Direction direction, Tuple updateElement, Timestamp timestamp) {
        Object dispatchKey = updateElement.get(discriminationColumnIndex);
        Mailbox bucketMailBox = bucketMailboxes.get(dispatchKey);
        if (bucketMailBox != null) {
            bucketMailBox.postMessage(direction, updateElement, timestamp);
        }
    }

    public int getDiscriminationColumnIndex() {
        return discriminationColumnIndex;
    }

    @Override
    public void pullInto(final Collection<Tuple> collector, final boolean flush) {
        propagatePullInto(collector, flush);
    }
    
    @Override
    public void pullIntoWithTimestamp(final Map<Tuple, Timestamp> collector, final boolean flush) {
        propagatePullIntoWithTimestamp(collector, flush);
    }

    /**
     * @since 2.2
     */
    public void pullIntoFiltered(final Collection<Tuple> collector, final Object bucketKey, final boolean flush) {
        final ArrayList<Tuple> unfiltered = new ArrayList<Tuple>();
        propagatePullInto(unfiltered, flush);
        for (Tuple tuple : unfiltered) {
            if (bucketKey.equals(tuple.get(discriminationColumnIndex))) {
                collector.add(tuple);
            }
        }
    }
    
    /**
     * @since 2.2
     */
    public void pullIntoWithTimestampFiltered(final Map<Tuple, Timestamp> collector, final Object bucketKey, final boolean flush) {
        final Map<Tuple, Timestamp> unfiltered = new HashMap<Tuple, Timestamp>();
        propagatePullIntoWithTimestamp(unfiltered, flush);
        for (final Entry<Tuple, Timestamp> entry : unfiltered.entrySet()) {
            if (bucketKey.equals(entry.getKey().get(discriminationColumnIndex))) {
                collector.put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void appendChild(Receiver receiver) {
        super.appendChild(receiver);
        if (receiver instanceof DiscriminatorBucketNode) {
            DiscriminatorBucketNode bucket = (DiscriminatorBucketNode) receiver;
            Object bucketKey = bucket.getBucketKey();
            DiscriminatorBucketNode old = buckets.put(bucketKey, bucket);
            if (old != null)
                throw new IllegalStateException();
            bucketMailboxes.put(bucketKey, this.getCommunicationTracker().proxifyMailbox(this, bucket.getMailbox()));
        }
    }
    
    /**
     * @since 2.2
     */
    public Map<Object, Mailbox> getBucketMailboxes() {
        return this.bucketMailboxes;
    }

    @Override
    public void networkStructureChanged() {
        bucketMailboxes.clear();
        for (Receiver receiver : children) {
            if (receiver instanceof DiscriminatorBucketNode) {
                DiscriminatorBucketNode bucket = (DiscriminatorBucketNode) receiver;
                Object bucketKey = bucket.getBucketKey();
                bucketMailboxes.put(bucketKey, this.getCommunicationTracker().proxifyMailbox(this, bucket.getMailbox()));
            }
        }
    }

    @Override
    public void removeChild(Receiver receiver) {
        super.removeChild(receiver);
        if (receiver instanceof DiscriminatorBucketNode) {
            DiscriminatorBucketNode bucket = (DiscriminatorBucketNode) receiver;
            Object bucketKey = bucket.getBucketKey();
            DiscriminatorBucketNode old = buckets.remove(bucketKey);
            if (old != bucket)
                throw new IllegalStateException();
            bucketMailboxes.remove(bucketKey);
        }
    }

    @Override
    protected String toStringCore() {
        return super.toStringCore() + '<' + discriminationColumnIndex + '>';
    }

}
