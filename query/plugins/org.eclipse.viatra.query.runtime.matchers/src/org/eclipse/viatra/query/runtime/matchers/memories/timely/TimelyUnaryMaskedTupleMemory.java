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

import org.eclipse.viatra.query.runtime.matchers.memories.TimestampReplacement;
import org.eclipse.viatra.query.runtime.matchers.tuple.ITuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuples;

public final class TimelyUnaryMaskedTupleMemory<Timestamp extends Comparable<Timestamp>>
        extends AbstractTimelyMaskedMemory<Timestamp, Object> {

    protected final int keyPosition;

    public TimelyUnaryMaskedTupleMemory(final TupleMask mask, final Object owner) {
        super(mask, owner);
        if (1 != mask.getSize())
            throw new IllegalArgumentException(mask.toString());
        this.keyPosition = mask.indices[0];
    }

    @Override
    public Iterable<Tuple> getSignatures() {
        return () -> {
            final Iterator<Object> wrapped = this.memory.keySet().iterator();
            return new Iterator<Tuple>() {
                @Override
                public boolean hasNext() {
                    return wrapped.hasNext();
                }

                @Override
                public Tuple next() {
                    final Object key = wrapped.next();
                    return Tuples.staticArityFlatTupleOf(key);
                }
            };
        };
    }

    @Override
    public TimestampReplacement<Timestamp> removeWithTimestamp(final Tuple tuple, final Tuple signature,
            final Timestamp timestamp) {
        final Object key = tuple.get(keyPosition);
        return removeInternal(key, tuple, timestamp);
    }

    @Override
    public TimestampReplacement<Timestamp> addWithTimestamp(final Tuple tuple, final Tuple signature,
            final Timestamp timestamp) {
        final Object key = tuple.get(keyPosition);
        return addInternal(key, tuple, timestamp);
    }

    @Override
    public Collection<Tuple> get(final ITuple signature) {
        return getInternal(signature.get(0));
    }

    @Override
    public Map<Tuple, Timestamp> getWithTimestamp(final ITuple signature) {
        return getWithTimestampInternal(signature.get(0));
    }

}
