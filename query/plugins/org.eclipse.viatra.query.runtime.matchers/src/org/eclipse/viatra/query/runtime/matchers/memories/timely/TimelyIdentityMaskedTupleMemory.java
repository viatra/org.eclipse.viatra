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
import java.util.Collections;
import java.util.Map;

import org.eclipse.viatra.query.runtime.matchers.tuple.ITuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.util.Pair;

public final class TimelyIdentityMaskedTupleMemory<Timestamp extends Comparable<Timestamp>>
        extends AbstractTimelyTrivialMemory<Timestamp> {

    public TimelyIdentityMaskedTupleMemory(final TupleMask mask, final Object owner) {
        super(mask, owner);
        if (!mask.isIdentity())
            throw new IllegalArgumentException(mask.toString());
    }

    @Override
    public int getKeysetSize() {
        return this.memory.size();
    }

    @Override
    public Iterable<Tuple> getSignatures() {
        return this.memory.keySet();
    }

    @Override
    public Collection<Tuple> get(final ITuple signature) {
        if (this.memory.containsKey(signature)) {
            return Collections.singleton((Tuple) signature);
        } else {
            return null;
        }
    }

    @Override
    public Map<Tuple, Timestamp> getWithTimestamp(final ITuple signature) {
        final Timestamp value = this.memory.get(signature);
        if (value != null) {
            return Collections.singletonMap((Tuple) signature, value);
        } else {
            return null;
        }
    }

    @Override
    public Pair<Timestamp, Timestamp> removeWithTimestamp(final Tuple tuple, final Tuple signature,
            final Timestamp timestamp) {
        try {
            return this.memory.remove(tuple, timestamp);
        } catch (final IllegalStateException e) {
            throw raiseDuplicateDeletion(tuple);
        }
    }

    @Override
    public Pair<Timestamp, Timestamp> addWithTimestamp(final Tuple tuple, final Tuple signature,
            final Timestamp timestamp) {
        return this.memory.put(tuple, timestamp);
    }

}
