/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.matchers.memories.timely;

import java.util.Collection;
import java.util.Map;

import org.eclipse.viatra.query.runtime.matchers.tuple.ITuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.util.Pair;

public final class TimelyDefaultMaskedTupleMemory<Timestamp extends Comparable<Timestamp>>
        extends AbstractTimelyMaskedMemory<Timestamp, Tuple> {

    public TimelyDefaultMaskedTupleMemory(final TupleMask mask, final Object owner) {
        super(mask, owner);
    }

    @Override
    public Iterable<Tuple> getSignatures() {
        return this.memory.keySet();
    }

    @Override
    public Pair<Timestamp, Timestamp> removeWithTimestamp(final Tuple tuple, final Tuple signature,
            final Timestamp timestamp) {
        final Tuple key = mask.transform(tuple);
        return removeInternal(key, tuple, timestamp);
    }

    @Override
    public Pair<Timestamp, Timestamp> addWithTimestamp(final Tuple tuple, final Tuple signature,
            final Timestamp timestamp) {
        final Tuple key = this.mask.transform(tuple);
        return addInternal(key, tuple, timestamp);
    }

    @Override
    public Collection<Tuple> get(final ITuple signature) {
        return getInternal((Tuple) signature);
    }

    @Override
    public Map<Tuple, Timestamp> getWithTimestamp(final ITuple signature) {
        return getWithTimestampInternal((Tuple) signature);
    }

}
