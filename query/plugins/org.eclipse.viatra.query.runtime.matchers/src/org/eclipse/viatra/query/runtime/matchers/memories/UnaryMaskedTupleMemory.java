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
import java.util.Iterator;

import org.eclipse.viatra.query.runtime.matchers.tuple.ITuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuples;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.matchers.util.IMemoryView;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory.MemoryType;
import org.eclipse.viatra.query.runtime.matchers.util.IMultiLookup.ChangeGranularity;
import org.eclipse.viatra.query.runtime.matchers.util.IMultiLookup;

/**
 * Specialized for unary mask; tuples are indexed by a single column as opposed to a projection (signature) tuple.
 * 
 * @author Gabor Bergmann
 * @since 2.0
 */
public final class UnaryMaskedTupleMemory extends MaskedTupleMemory {

    protected IMultiLookup<Object, Tuple> columnToTuples;
    protected final int keyPosition;

    /**
     * @param mask
     *            The mask used to index the matchings
     * @param owner the object "owning" this memory
     * @param bucketType the kind of tuple collection maintained for each indexer bucket
     * @since 2.0
     */
    UnaryMaskedTupleMemory(TupleMask mask, MemoryType bucketType, Object owner) {
        super(mask, owner);
        if (1 != mask.getSize()) throw new IllegalArgumentException(mask.toString());
        
        columnToTuples = CollectionsFactory.<Object, Tuple>createMultiLookup(
                Object.class, bucketType, Object.class);
        keyPosition = mask.indices[0];
    }
    
    @Override
    public void clear() {
        columnToTuples.clear();
    }

    @Override
    public int getKeysetSize() {
        return columnToTuples.countKeys();
    }

    @Override
    public int getTotalSize() {
        int i = 0;
        for (Object key : columnToTuples.distinctKeys()) {
            i += columnToTuples.lookup(key).size();
        }
        return i;
    }

    @Override
    public Iterator<Tuple> iterator() {
        return columnToTuples.distinctValues().iterator();
    }

    @Override
    public Iterable<Tuple> getSignatures() {
        return () -> {
            Iterator<Object> wrapped = columnToTuples.distinctKeys().iterator();
            return new Iterator<Tuple>() {
                @Override
                public boolean hasNext() {
                    return wrapped.hasNext();
                }
                @Override
                public Tuple next() {
                    Object key = wrapped.next();
                    return Tuples.staticArityFlatTupleOf(key);
                }
            };
        };
    }

    @Override
    public Collection<Tuple> get(ITuple signature) {
        Object key = signature.get(0);
        IMemoryView<Tuple> bucket = columnToTuples.lookup(key);
        return bucket == null ? null : bucket.distinctValues();
    }

    @Override
    public boolean remove(Tuple tuple, Tuple signature) {
        return removeInternal(tuple, tuple.get(keyPosition));
    }

    @Override
    public boolean remove(Tuple tuple) {
        return removeInternal(tuple, tuple.get(keyPosition));
    }

    @Override
    public boolean add(Tuple tuple, Tuple signature) {
        return addInternal(tuple, tuple.get(keyPosition));
    }

    @Override
    public boolean add(Tuple tuple) {
        return addInternal(tuple, tuple.get(keyPosition));
    }
    
    protected boolean addInternal(Tuple tuple, Object key) {
        try {
            return columnToTuples.addPair(key, tuple) == ChangeGranularity.KEY;
        } catch (IllegalStateException ex) { // ignore worthless internal exception details
            throw raiseDuplicateInsertion(tuple);
        }
    }
    
    protected boolean removeInternal(Tuple tuple, Object key) {
        try {
            return columnToTuples.removePair(key, tuple) == ChangeGranularity.KEY;
        } catch (IllegalStateException ex) { // ignore worthless internal exception details
            throw raiseDuplicateDeletion(tuple);
        }
    }

}