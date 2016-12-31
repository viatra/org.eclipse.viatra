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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.context.IPosetComparator;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.rete.tuple.Clearable;

/**
 * A monotonicity aware mailbox implementation. The mailbox uses an {@link IPosetComparator} to identify if a pair of
 * REVOKE - INSERT updates represent a monotone change pair. The mailbox is used by {@link MonotonicityAwareReceiver}s.
 * 
 * @author Tamas Szabo
 */
public class MonotonicityAwareMailbox implements Mailbox {

    protected MessageIndexer monotoneQueue;
    protected MessageIndexer antiMonotoneQueue;
    protected MessageIndexer monotoneBuffer;
    protected MessageIndexer antiMonotoneBuffer;
    protected boolean deliveringMonotone;
    protected boolean deliveringAntiMonotone;
    protected final MonotonicityAwareReceiver receiver;
    protected final ReteContainer container;
    protected final CommunicationTracker tracker;
    protected final TupleMask groupMask;

    public MonotonicityAwareMailbox(final MonotonicityAwareReceiver receiver, final ReteContainer container) {
        this.receiver = receiver;
        this.container = container;
        this.tracker = container.getTracker();
        this.groupMask = receiver.getCoreMask();
        this.monotoneQueue = new MessageIndexer();
        this.antiMonotoneQueue = new MessageIndexer();
        this.monotoneBuffer = new MessageIndexer();
        this.antiMonotoneBuffer = new MessageIndexer();
        this.deliveringMonotone = false;
        this.deliveringAntiMonotone = false;
    }

    protected MessageIndexer getActiveMonotoneQueue() {
        if (deliveringMonotone) {
            return monotoneBuffer;
        } else {
            return monotoneQueue;
        }
    }

    protected MessageIndexer getActiveAntiMonotoneQueue() {
        if (deliveringAntiMonotone) {
            return antiMonotoneBuffer;
        } else {
            return antiMonotoneQueue;
        }
    }

    @Override
    public void postMessage(final Direction direction, final Tuple update) {
        final MessageIndexer monotoneQueue = getActiveMonotoneQueue();
        final MessageIndexer antiMonotoneQueue = getActiveAntiMonotoneQueue();
        final boolean wasPresentAsMonotone = monotoneQueue.getCount(update) != 0;
        final boolean wasPresentAsAntiMonotone = antiMonotoneQueue.getCount(update) != 0;
        final TupleMask coreMask = receiver.getCoreMask();

        // it cannot happen that it was present in both
        assert !(wasPresentAsMonotone && wasPresentAsAntiMonotone);

        if (direction == Direction.INSERT) {
            if (wasPresentAsAntiMonotone) {
                // it was a non-monotone one before
                antiMonotoneQueue.insert(update);
            } else {
                // it was a monotone one before or did not exist at all
                monotoneQueue.insert(update);

                // if it was not present in the monotone queue before, then
                // we need to check whether it makes REVOKE updates monotone
                if (!wasPresentAsMonotone) {
                    final Set<Tuple> counterParts = tryFindCounterPart(update, false, true);
                    for (final Tuple counterPart : counterParts) {
                        final int count = antiMonotoneQueue.getCount(counterPart);
                        assert count < 0;
                        antiMonotoneQueue.update(counterPart, -count);
                        monotoneQueue.update(counterPart, count);
                    }
                }
            }
        } else {
            if (wasPresentAsAntiMonotone) {
                // it was an anti-monotone one before
                antiMonotoneQueue.delete(update);
            } else if (wasPresentAsMonotone) {
                // it was a monotone one before
                monotoneQueue.delete(update);

                // and we need to check whether the monotone REVOKE updates still have a reinforcing counterpart
                final Set<Tuple> candidates = new HashSet<Tuple>();
                final Tuple key = coreMask.transform(update);
                for (final Entry<Tuple, Integer> entry : monotoneQueue.getTuplesByGroup(key).entrySet()) {
                    if (entry.getValue() < 0) {
                        final Tuple candidate = entry.getKey();
                        final Set<Tuple> counterParts = tryFindCounterPart(candidate, true, false);
                        if (counterParts.isEmpty()) {
                            // all of them are gone
                            candidates.add(candidate);
                        }
                    }
                }

                // move the candidates from the monotone queue to the anti-monotone queue because they do not have a
                // counterpart anymore
                for (final Tuple candidate : candidates) {
                    final int count = monotoneQueue.getCount(candidate);
                    assert count < 0;
                    monotoneQueue.update(candidate, -count);
                    antiMonotoneQueue.update(candidate, count);
                }
            } else {
                // it did not exist before
                final Set<Tuple> counterParts = tryFindCounterPart(update, true, false);
                if (counterParts.isEmpty()) {
                    // there is no tuple that would make this update monotone
                    antiMonotoneQueue.delete(update);
                } else {
                    // there is a reinforcing counterpart
                    monotoneQueue.delete(update);
                }
            }
        }

        if (container != null) {
            if (antiMonotoneQueue.isEmpty()) {
                tracker.notifyLostAllMessages(this, MessageKind.ANTI_MONOTONE);
            } else {
                tracker.notifyHasMessage(this, MessageKind.ANTI_MONOTONE);
            }

            if (monotoneQueue.isEmpty()) {
                tracker.notifyLostAllMessages(this, MessageKind.MONOTONE);
            } else {
                tracker.notifyHasMessage(this, MessageKind.MONOTONE);
            }
        }
    }

    protected Set<Tuple> tryFindCounterPart(final Tuple first, final boolean findPositiveCounterPart,
            final boolean findAllCounterParts) {
        final MessageIndexer monotoneQueue = getActiveMonotoneQueue();
        final MessageIndexer antiMonotoneQueue = getActiveAntiMonotoneQueue();
        final TupleMask coreMask = receiver.getCoreMask();
        final TupleMask posetMask = receiver.getPosetMask();
        final IPosetComparator posetComparator = receiver.getPosetComparator();
        final Set<Tuple> result = new HashSet<Tuple>();
        final Tuple firstKey = coreMask.transform(first);
        final Tuple firstValue = posetMask.transform(first);

        if (findPositiveCounterPart) {
            for (final Entry<Tuple, Integer> entry : monotoneQueue.getTuplesByGroup(firstKey).entrySet()) {
                final Tuple secondValue = posetMask.transform(entry.getKey());
                if (entry.getValue() > 0 && posetComparator.isLessOrEqual(firstValue, secondValue)) {
                    result.add(entry.getKey());
                    if (!findAllCounterParts) {
                        return result;
                    }
                }
            }
        } else {
            for (final Entry<Tuple, Integer> entry : antiMonotoneQueue.getTuplesByGroup(firstKey).entrySet()) {
                final Tuple secondValue = posetMask.transform(entry.getKey());
                if (posetComparator.isLessOrEqual(secondValue, firstValue)) {
                    result.add(entry.getKey());
                    if (!findAllCounterParts) {
                        return result;
                    }
                }
            }
        }

        return result;
    }

    @Override
    public void deliverAll(final MessageKind kind) {
        if (kind == MessageKind.ANTI_MONOTONE) {
            // use the buffer during delivering so that there is a clear separation between the stages
            this.deliveringAntiMonotone = true;

            for (final Tuple group : antiMonotoneQueue.getGroups()) {
                for (final Entry<Tuple, Integer> entry : antiMonotoneQueue.getTuplesByGroup(group).entrySet()) {
                    final Tuple update = entry.getKey();
                    final int count = entry.getValue();
                    assert count < 0;
                    for (int i = 0; i < Math.abs(count); i++) {
                        this.receiver.update(Direction.REVOKE, update, false);
                    }
                }
            }

            this.deliveringAntiMonotone = false;
            final MessageIndexer tmp = antiMonotoneQueue;
            antiMonotoneQueue = antiMonotoneBuffer;
            antiMonotoneBuffer = tmp;
            antiMonotoneBuffer.clear();
        } else if (kind == MessageKind.MONOTONE) {
            // use the buffer during delivering so that there is a clear separation between the stages
            this.deliveringMonotone = true;

            for (final Tuple group : monotoneQueue.getGroups()) {
                for (final Entry<Tuple, Integer> entry : monotoneQueue.getTuplesByGroup(group).entrySet()) {
                    final Tuple update = entry.getKey();
                    final int count = entry.getValue();
                    assert count != 0;
                    final Direction direction = count < 0 ? Direction.REVOKE : Direction.INSERT;
                    for (int i = 0; i < Math.abs(count); i++) {
                        this.receiver.update(direction, update, true);
                    }
                }
            }

            this.deliveringMonotone = false;
            final MessageIndexer tmp = monotoneQueue;
            monotoneQueue = monotoneBuffer;
            monotoneBuffer = tmp;
            monotoneBuffer.clear();
        }
    }

    @Override
    public String toString() {
        return "MONO_MBOX (" + receiver + ") " + monotoneQueue + " " + antiMonotoneQueue;
    }

    @Override
    public Receiver getReceiver() {
        return receiver;
    }

    @Override
    public void clear() {
        monotoneQueue.clear();
        antiMonotoneQueue.clear();
        monotoneBuffer.clear();
        antiMonotoneBuffer.clear();
    }

    protected class MessageIndexer implements Clearable {

        // group -> full tuple -> count - there is no need to perform lookups based on the poset value
        protected final Map<Tuple, Map<Tuple, Integer>> indexer;

        public MessageIndexer() {
            this.indexer = new LinkedHashMap<Tuple, Map<Tuple, Integer>>();
        }

        public Map<Tuple, Integer> getTuplesByGroup(final Tuple group) {
            final Map<Tuple, Integer> valueMap = indexer.get(group);
            if (valueMap == null) {
                return Collections.emptyMap();
            } else {
                return Collections.unmodifiableMap(valueMap);
            }
        }
        
        public int getCount(final Tuple update) {
            final Tuple group = groupMask.transform(update);
            final Integer count = getTuplesByGroup(group).get(update);
            if (count == null) {
                return 0;
            } else {
                return count;
            }
        }

        public Set<Tuple> getGroups() {
            return Collections.unmodifiableSet(indexer.keySet());
        }
        
        public void insert(final Tuple update) {
            update(update, 1);
        }
        
        public void delete(final Tuple update) {
            update(update, -1);
        }

        public void update(final Tuple update, final int delta) {
            final Tuple group = groupMask.transform(update);
            Map<Tuple, Integer> valueMap = indexer.get(group);

            if (valueMap == null) {
                valueMap = new HashMap<Tuple, Integer>();
                indexer.put(group, valueMap);
            }

            final Integer oldCount = valueMap.get(update);
            final int newCount = (oldCount == null ? 0 : oldCount) + delta;

            if (newCount == 0) {
                valueMap.remove(update);
                if (valueMap.isEmpty()) {
                    indexer.remove(group);
                }
            } else {
                valueMap.put(update, newCount);
            }
        }
        
        public boolean isEmpty() {
            return indexer.isEmpty();
        }

        @Override
        public void clear() {
            indexer.clear();
        }

    }

}
