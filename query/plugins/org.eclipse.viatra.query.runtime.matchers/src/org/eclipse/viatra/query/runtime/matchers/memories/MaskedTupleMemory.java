/*******************************************************************************
 * Copyright (c) 2010-2018, Gabor Bergmann, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.memories;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.viatra.query.runtime.matchers.memories.timely.TimelyDefaultMaskedTupleMemory;
import org.eclipse.viatra.query.runtime.matchers.memories.timely.TimelyIdentityMaskedTupleMemory;
import org.eclipse.viatra.query.runtime.matchers.memories.timely.TimelyNullaryMaskedTupleMemory;
import org.eclipse.viatra.query.runtime.matchers.memories.timely.TimelyUnaryMaskedTupleMemory;
import org.eclipse.viatra.query.runtime.matchers.tuple.ITuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.util.Clearable;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory.MemoryType;

/**
 * Indexes a collection of Tuples by their signature (i.e. footprint, projection) obtained according to a mask.
 * May belong to an "owner" (for documentation / traceability purposes).
 * <p>
 * There are timeless and timely versions of the different memories. Timely versions associate timestamps with the stored tuples. 
 * A tuple with the same timestamp can be inserted multiple times to the timely memories. These memories can efficiently answer queries about the 
 * least timestamp associated with a tuple, and they can also enumerate all timestamps associated with given tuple(s).  
 *         
 * @noextend This class is not intended to be subclassed by clients.
 * @author Gabor Bergmann
 * @author Tamas Szabo
 * @since 2.0
 */
// NetworkStructureChangeSensitiveNodes have memories of type MaskedTupleMemory and they interact with that 
// both in the timeless and the timely behavior. This is the reason why timely and timeless versions of 
// the same methods exist on this class instead of in separate subclasses. If this would not be the case, 
// NetworkStructureChangeSensitiveNodes would need to cast all the time depending on the mode of behavior. 
public abstract class MaskedTupleMemory<Timestamp extends Comparable<Timestamp>> implements Clearable  {
    
    /**
     * Creates a new memory for the given owner that indexes tuples according to the given mask.
     */
    public static<T extends Comparable<T>> MaskedTupleMemory<T> create(TupleMask mask, MemoryType bucketType, Object owner) {
        return create(mask, bucketType, owner, false);
    }
    
    /**
     * Creates a new memory for the given owner that indexes tuples according to the given mask.
     * Clients can specify if the created memory should be timestamp-aware or not. 
     * 
     * @since 2.2
     */
    public static<T extends Comparable<T>> MaskedTupleMemory<T> create(TupleMask mask, MemoryType bucketType, Object owner, boolean isTimely) {
        if (isTimely) {
            if (bucketType != MemoryType.SETS) {
                throw new IllegalArgumentException("Timely memories only support SETS as the bucket type!");
            }
            if (mask.isIdentity()) {
                return new TimelyIdentityMaskedTupleMemory<T>(mask, owner);
            } else if (0 == mask.getSize()) {
                return new TimelyNullaryMaskedTupleMemory<T>(mask, owner);
            } else if (1 == mask.getSize()) {
                return new TimelyUnaryMaskedTupleMemory<T>(mask, owner);
            } else { 
                return new TimelyDefaultMaskedTupleMemory<T>(mask, owner);
            }
        } else {
            if (mask.isIdentity()) {                
                return new IdentityMaskedTupleMemory<T>(mask, bucketType, owner);
            } else if (0 == mask.getSize()) {
                return new NullaryMaskedTupleMemory<T>(mask, bucketType, owner);
            } else if (1 == mask.getSize()) {
                return new UnaryMaskedTupleMemory<T>(mask, bucketType, owner);
            } else { 
                return new DefaultMaskedTupleMemory<T>(mask, bucketType, owner);
            }
        }
    }
    
    /**
     * Initializes the contents of this memory based on the contents of another memory. 
     * The default value is associated with each tuple in the timely memories. 
     * 
     * @since 2.2
     */
    public void initializeWith(final MaskedTupleMemory<Timestamp> other, final Timestamp defaultValue) { 
        throw new UnsupportedOperationException("This is only supported by timely memory implementations!");
    }
       
    /**
     * Returns true of this memory is timely, false otherwise. 
     * 
     * @since 2.2
     */
    public boolean isTimely() {
        return false;
    }
    
    /**
     * The mask by which the tuples are indexed.
     */
    protected TupleMask mask;
    
    /**
     * The object "owning" this memory. May be null.
     * @since 1.7
     */
    protected Object owner;
    

    /**
     * The node owning this memory. May be null.
     * @since 2.0
     */
    public Object getOwner() {
        return owner;
    }

    /**
     * The mask according to which tuples are projected and indexed.
     * @since 2.0
     */
    public TupleMask getMask() {
        return mask;
    }

    /**
     * @return the number of distinct signatures of all stored tuples.
     */
    public abstract int getKeysetSize();

    /**
     * @return the total number of distinct tuples stored. 
     * Multiple copies of the same tuple, if allowed, are counted as one.
     * 
     * <p> This is currently not cached but computed on demand. 
     * It is therefore not efficient, and shall only be used for debug / profiling purposes.
     */
    public abstract int getTotalSize();

    /**
     * Iterates over distinct tuples stored in the memory, regardless of their signatures.
     */
    public abstract Iterator<Tuple> iterator();

    /**
     * Retrieves a read-only view of exactly those signatures for which at least one tuple is stored
     * @since 2.0
     */
    public abstract Iterable<Tuple> getSignatures();

    /**
     * Retrieves tuples that have the specified signature
     * 
     * @return collection of tuples found, null if none
     */
    public abstract Collection<Tuple> get(ITuple signature);
    
    /**
     * Retrieves the tuples and their associated timestamps that have the specified signature. 
     * 
     * @return the mappings from tuples to timestamps, null if there is no mapping for the signature
     * @since 2.2
     */
    public abstract Map<Tuple, Timestamp> getWithTimestamp(ITuple signature);

    /**
     * Retrieves tuples that have the specified signature
     * 
     * @return collection of tuples found, never null
     * @since 2.1
     */
    public Collection<Tuple> getOrEmpty(ITuple signature) {
        Collection<Tuple> result = get(signature);
        return result == null? Collections.emptySet() : result;
    }
    
    /**
     * @since 2.2
     */
    public Map<Tuple, Timestamp> getOrEmptyWithTimestamp(ITuple signature) {
        Map<Tuple, Timestamp> result = getWithTimestamp(signature);
        return result == null? Collections.emptyMap() : result;
    }

    /**
     * Removes a tuple occurrence from the memory with the given signature.
     * 
     * @param tuple the tuple to be removed from the memory
     * @param signature precomputed footprint of the tuple according to the mask
     * 
     * @return true if this was the the last occurrence of the signature (according to the mask)
     */
    public boolean remove(Tuple tuple, Tuple signature) {
        throw new UnsupportedOperationException("This is only supported by timeless memory implementations!");
    }
    
    /**
     * Removes a tuple occurrence from the memory with the given signature and timestamp. This operation is only allowed 
     * if the tuple has been already inserted to the memory with the same timestamp before. 
     * 
     * @param tuple the tuple to be removed from the memory
     * @param signature precomputed footprint of the tuple according to the mask
     * @param timestamp the timestamp associated with the tuple
     * 
     * @return A timestamp replacement where the old value represents the old least timestamp (before the removal)
     * and the new value represents the new least timestamp (after the removal) associated with the tuple. 
     * The old value can never be null, the new value may be null if the tuple is not present in the memory anymore.
     * @since 2.2
     */
    public TimestampReplacement<Timestamp> removeWithTimestamp(Tuple tuple, Tuple signature, Timestamp timestamp) {
        throw new UnsupportedOperationException("This is only supported by timely memory implementations!");
    }

    /**
     * Removes a tuple occurrence from the memory. 
     * 
     * @param tuple the tuple to be removed from the memory
     * 
     * @return true if this was the the last occurrence of the signature (according to the mask)
     */
    public boolean remove(Tuple tuple) {
        throw new UnsupportedOperationException("This is only supported by timeless memory implementations!");
    }

    /**
     * Removes a tuple occurrence from the memory with the given timestamp. This operation is only allowed 
     * if the tuple has been already inserted to the memory with the same timestamp before. 
     * 
     * @param tuple the tuple to be removed from the memory
     * @param timestamp the timestamp associated with the tuple
     * 
     * @return A timestamp replacement where the old value represents the old least timestamp (before the removal)
     * and the new value represents the new least timestamp (after the removal) associated with the tuple. 
     * The old value can never be null, the new value may be null if the tuple is not present in the memory anymore.   
     * @since 2.2
     */
    public TimestampReplacement<Timestamp> removeWithTimestamp(Tuple tuple, Timestamp timestamp) {
        throw new UnsupportedOperationException("This is only supported by timely memory implementations!");
    }
    
    /**
     * Adds a tuple occurrence to the memory with the given signature.
     * 
     * @param tuple the tuple to be added to the memory
     * @param signature precomputed footprint of the tuple according to the mask
     * 
     * @return true if new signature encountered (according to the mask)
     */
    public boolean add(Tuple tuple, Tuple signature) {
        throw new UnsupportedOperationException("This is only supported by timeless memory implementations!");
    }

    /**
     * Adds a tuple occurrence to the memory with the given signature and timestamp.
     * 
     * @param tuple the tuple to be added to the memory
     * @param signature precomputed footprint of the tuple according to the mask
     * @param timestamp the timestamp associated with the tuple
     * 
     * @return A timestamp replacement where the old value represents the old least timestamp (before the addition)
     * and the new value represents the new least timestamp (after the addition) associated with the tuple. 
     * The old value may be null if the tuple was not present in the memory before, the new value can never be null.   
     * @since 2.2
     */
    public TimestampReplacement<Timestamp> addWithTimestamp(Tuple tuple, Tuple signature, Timestamp timestamp) {
        throw new UnsupportedOperationException("This is only supported by timely memory implementations!");
    }
    
    /**
     * Adds a tuple occurrence to the memory.
     * 
     * @param tuple the tuple to be added to the memory
     * 
     * @return true if new signature encountered (according to the mask)
     */
    public boolean add(Tuple tuple) {
        throw new UnsupportedOperationException("This is only supported by timeless memory implementations!");
    }
    
    /**
     * Adds a tuple occurrence to the memory with the given timestamp. 
     * 
     * @param tuple the tuple to be added to the memory
     * @param timestamp the timestamp associated with the tuple
     * 
     * @return A timestamp replacement where the old value represents the old least timestamp (before the addition)
     * and the new value represents the new least timestamp (after the addition) associated with the tuple. 
     * The old value may be null if the tuple was not present in the memory before, the new value can never be null.    
     * @since 2.2
     */
    public TimestampReplacement<Timestamp> addWithTimestamp(Tuple tuple, Timestamp timestamp) {
        throw new UnsupportedOperationException("This is only supported by timely memory implementations!");
    }

    protected MaskedTupleMemory(TupleMask mask, Object owner) {
        super();
        this.mask = mask;
        this.owner = owner;
    }

    protected IllegalStateException raiseDuplicateInsertion(Tuple tuple) {
        return new IllegalStateException(
                String.format(
                        "Duplicate insertion of tuple %s into %s", 
                        tuple, owner));
    }

    protected IllegalStateException raiseDuplicateDeletion(Tuple tuple) {
        return new IllegalStateException(
                String.format(
                        "Duplicate deletion of tuple %s from %s", 
                        tuple, owner));
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName()+"<" + mask + ">@" + owner;
    }


}