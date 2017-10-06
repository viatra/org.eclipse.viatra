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

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;

/**
 * Default mailbox implementation. 
 * <p> Usually, the mailbox performs counting of messages so that they can cancel each other out.
 * However, if marked as a fall-through mailbox, than update messages are delivered directly to the receiver node to reduce overhead. 
 * 
 * 
 * @author Tamas Szabo
 * @since 1.6
 */
public class DefaultMailbox implements Mailbox {
    
    private static int SIZE_THRESHOLD = 32; 

    protected Map<Tuple, Integer> queue;
    protected Map<Tuple, Integer> buffer;
    protected final Receiver receiver;
    protected final ReteContainer container;
    protected boolean delivering;
    protected final CommunicationTracker tracker;
    protected boolean fallThrough = false;
    /**
     * @since 1.7
     */
    protected CommunicationGroup currentGroup = null;
    private boolean queueSizeTresholdExceeded = false;
    private boolean bufferSizeTresholdExceeded = false;

    public DefaultMailbox() {
        this(null, null);
    }

    public DefaultMailbox(Receiver receiver, ReteContainer container) {
        this.receiver = receiver;
        this.container = container;
        this.tracker = container == null ? null : container.getTracker();
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
        if (fallThrough) {
            receiver.update(direction, update);
        } else {
            enqueue(direction, update);
        }
    }

    private void enqueue(Direction direction, Tuple update) {
        Map<Tuple, Integer> activeQueue = getActiveQueue();
        boolean wasEmpty = activeQueue.isEmpty();
        
        boolean significantChange;
        Integer count = activeQueue.get(update);
        if (count == null) {
            count = 0;
            significantChange = true;
            
            // is size threshold exceeded with the new tuple?
            // Since we check it again at each step, same effect as if it was (size >= SIZE_TRESHOLD)
            //  because if count grows larger then threshold, it must pass through threshold
            if (delivering)
                bufferSizeTresholdExceeded = bufferSizeTresholdExceeded || (activeQueue.size() == SIZE_THRESHOLD);
            else
                queueSizeTresholdExceeded = queueSizeTresholdExceeded || (activeQueue.size() == SIZE_THRESHOLD);
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
        
        if (significantChange && container != null) {
            if (wasEmpty) {
                currentGroup.notifyHasMessage(this, MessageKind.DEFAULT);
            } else if (activeQueue.isEmpty()) {
                currentGroup.notifyLostAllMessages(this, MessageKind.DEFAULT);
            }
        }
    }

    @Override
    public void deliverAll(MessageKind kind) {
        // use the buffer during delivering so that there is a clear separation between the stages
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

        // If queue was too big, it still has a lot of memory reserved that we should free up
        if (queueSizeTresholdExceeded) {
            this.queue = this.buffer;
            this.buffer = CollectionsFactory.createMap();
        } else { // otherwise, queue can be emptied and reused           
            this.queue.clear();
            Map<Tuple, Integer> tmpQueue = this.queue;
            this.queue = this.buffer;
            this.buffer = tmpQueue;
        }
        queueSizeTresholdExceeded = bufferSizeTresholdExceeded;
        bufferSizeTresholdExceeded = false;
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

    public boolean isFallThrough() {
        return fallThrough;
    }

    /**
     * Controlled by the {@link CommunicationTracker} which can determine 
     * based on node type and network topology whether fall-through is allowed.
     */
    public void setFallThrough(boolean fallThrough) {
        this.fallThrough = fallThrough;
    }

    public CommunicationGroup getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(CommunicationGroup currentGroup) {
        this.currentGroup = currentGroup;
    }

    
}
