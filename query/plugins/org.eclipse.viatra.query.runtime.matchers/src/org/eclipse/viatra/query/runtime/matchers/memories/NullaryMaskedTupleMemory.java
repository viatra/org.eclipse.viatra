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
package org.eclipse.viatra.query.runtime.matchers.memories;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.tuple.ITuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuples;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory.MemoryType;

/**
 * Specialized for nullary mask; tuples are stored as a simple set/multiset memory.
 * 
 * @author Gabor Bergmann
 * @since 2.0
 */
public final class NullaryMaskedTupleMemory<Timestamp extends Comparable<Timestamp>> extends AbstractTrivialMemory<Timestamp> {

    protected static final Set<Tuple> UNIT_RELATION = 
            Collections.singleton(Tuples.staticArityFlatTupleOf());
    protected static final Set<Tuple> EMPTY_RELATION = 
            Collections.emptySet();
    /**
     * @param mask
     *            The mask used to index the matchings
     * @param owner the object "owning" this memory
     * @param bucketType the kind of tuple collection maintained for each indexer bucket
     * @since 2.0
     */
    public NullaryMaskedTupleMemory(TupleMask mask, MemoryType bucketType, Object owner) {
        super(mask, bucketType, owner);
        if (0 != mask.getSize()) throw new IllegalArgumentException(mask.toString());
    }

    @Override
    public int getKeysetSize() {
        return tuples.isEmpty() ? 0 : 1;
    }

    @Override
    public Iterable<Tuple> getSignatures() {
        return tuples.isEmpty() ? EMPTY_RELATION : UNIT_RELATION;
    }

    @Override
    public Collection<Tuple> get(ITuple signature) {
        if (0 == signature.getSize())
            return tuples.distinctValues(); 
        else return null;
    }

    @Override
    public boolean remove(Tuple tuple, Tuple signature) {
        tuples.removeOne(tuple);
        return tuples.isEmpty();
    }

    @Override
    public boolean remove(Tuple tuple) {
        return remove(tuple, null);
    }

    @Override
    public boolean add(Tuple tuple, Tuple signature) {
        boolean wasEmpty = tuples.isEmpty();
        tuples.addOne(tuple);
        return wasEmpty;
    }

    @Override
    public boolean add(Tuple tuple) {
        return add(tuple, null);
    }

}
