/*******************************************************************************
 * Copyright (c) 2010-2018, Gabor Bergmann, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.memories.timely;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.tuple.ITuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuples;
import org.eclipse.viatra.query.runtime.matchers.util.Pair;

public final class TimelyNullaryMaskedTupleMemory<Timestamp extends Comparable<Timestamp>>
        extends AbstractTimelyTrivialMemory<Timestamp> {

    protected static final Set<Tuple> UNIT_RELATION = Collections.singleton(Tuples.staticArityFlatTupleOf());
    protected static final Set<Tuple> EMPTY_RELATION = Collections.emptySet();

    public TimelyNullaryMaskedTupleMemory(final TupleMask mask, final Object owner) {
        super(mask, owner);
        if (0 != mask.getSize()) {
            throw new IllegalArgumentException(mask.toString());
        }
    }

    @Override
    public int getKeysetSize() {
        return this.memory.isEmpty() ? 0 : 1;
    }

    @Override
    public Iterable<Tuple> getSignatures() {
        return this.memory.isEmpty() ? EMPTY_RELATION : UNIT_RELATION;
    }

    @Override
    public Collection<Tuple> get(final ITuple signature) {
        if (0 == signature.getSize()) {
            return this.memory.keySet();
        } else {
            return null;
        }
    }

    @Override
    public Map<Tuple, Timestamp> getWithTimestamp(final ITuple signature) {
        if (0 == signature.getSize()) {
            return this.memory.asMap();
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
