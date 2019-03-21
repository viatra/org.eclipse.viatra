/*******************************************************************************
 * Copyright (c) 2010-2018, Gabor Bergmann, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.memories.timely;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.viatra.query.runtime.matchers.memories.MaskedTupleMemory;
import org.eclipse.viatra.query.runtime.matchers.memories.TimestampReplacement;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.matchers.util.TimelyMemory;

abstract class AbstractTimelyMaskedMemory<Timestamp extends Comparable<Timestamp>, KeyType>
        extends MaskedTupleMemory<Timestamp> {

    protected final Map<KeyType, TimelyMemory<Timestamp>> memory;

    public AbstractTimelyMaskedMemory(final TupleMask mask, final Object owner) {
        super(mask, owner);
        this.memory = CollectionsFactory.createMap();
    }

    @Override
    public void initializeWith(final MaskedTupleMemory<Timestamp> other, final Timestamp defaultValue) {
        final Iterator<Tuple> itr = other.iterator();
        while (itr.hasNext()) {
            this.addWithTimestamp(itr.next(), defaultValue);
        }
    }

    @Override
    public void clear() {
        this.memory.clear();
    }

    @Override
    public int getKeysetSize() {
        return this.memory.keySet().size();
    }

    @Override
    public int getTotalSize() {
        int i = 0;
        for (final Entry<KeyType, TimelyMemory<Timestamp>> entry : this.memory.entrySet()) {
            i += entry.getValue().size();
        }
        return i;
    }

    @Override
    public Iterator<Tuple> iterator() {
        return this.memory.values().stream().flatMap(e -> e.keySet().stream()).iterator();
    }

    protected Collection<Tuple> getInternal(final KeyType key) {
        final Map<Tuple, Timestamp> values = this.getWithTimestampInternal(key);
        if (values == null) {
            return null;
        } else {
            return values.keySet();
        }
    }

    public Map<Tuple, Timestamp> getWithTimestampInternal(final KeyType key) {
        final TimelyMemory<Timestamp> values = this.memory.get(key);
        if (values == null) {
            return null;
        } else {
            return values.asMap();
        }
    }

    protected TimestampReplacement<Timestamp> removeInternal(final KeyType key, final Tuple tuple,
            final Timestamp timestamp) {
        final TimelyMemory<Timestamp> values = this.memory.get(key);
        TimestampReplacement<Timestamp> result = null;
        if (values == null) {
            throw raiseDuplicateDeletion(tuple);
        }
        try {
            result = values.remove(tuple, timestamp);
        } catch (final IllegalStateException e) {
            throw raiseDuplicateDeletion(tuple);
        }
        if (values.isEmpty()) {
            this.memory.remove(key);
        }
        return result;
    }

    protected TimestampReplacement<Timestamp> addInternal(final KeyType key, final Tuple tuple, final Timestamp timestamp) {
        TimelyMemory<Timestamp> values = this.memory.get(key);
        if (values == null) {
            values = new TimelyMemory<Timestamp>();
            this.memory.put(key, values);
        }
        return values.put(tuple, timestamp);
    }

    @Override
    public TimestampReplacement<Timestamp> removeWithTimestamp(final Tuple tuple, final Timestamp timestamp) {
        return removeWithTimestamp(tuple, null, timestamp);
    }

    @Override
    public TimestampReplacement<Timestamp> addWithTimestamp(final Tuple tuple, final Timestamp timestamp) {
        return addWithTimestamp(tuple, null, timestamp);
    }

}
