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
import java.util.Iterator;

import org.eclipse.viatra.query.runtime.matchers.tuple.ITuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.util.Clearable;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory.MemoryType;

/**
 *         Indexes a collection of Tuples by their signature (i.e. footprint, projection) obtained according to a mask.
 *         May belong to an "owner" (for documentation / traceability purposes).
 *         
 *         <p> TODO experiment with memory-saving alternatives, 
 *          e.g. if the mask has width 1, 
 *          {@link #signatureToTuples} can be keyed by the unary values instead of tuples.
 *         
 *         
 * @noextend This class is not intended to be subclassed by clients.
 * @author Gabor Bergmann
 * @since 2.0
 */
public abstract class MaskedTupleMemory implements Clearable, Iterable<Tuple>  {

    /**
     * Creates a new memory for the given owner that indexes tuples according to the given mask.
     */
    public static MaskedTupleMemory create(TupleMask mask, MemoryType bucketType, Object owner) {
        if (mask.isIdentity()) 
            return new IdentityMaskedTupleMemory(mask, bucketType, owner);
        else if (0 == mask.getSize())
            return new NullaryMaskedTupleMemory(mask, bucketType, owner);
        else if (1 == mask.getSize())
            return new UnaryMaskedTupleMemory(mask, bucketType, owner);
        else 
            return new DefaultMaskedTupleMemory(mask, bucketType, owner);
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
     * Removes a tuple occurrence from the memory, with given signature
     * 
     * @param tuple old tuple removed from the memory
     * @param signature precomputed footprint of the tuple according to the mask
     * 
     * @return true if this was the the last occurrence of the signature (according to the mask)
     */
    public abstract boolean remove(Tuple tuple, Tuple signature);

    /**
     * Removes a tuple occurrence from the memory
     * 
     * @param tuple old tuple removed from the memory
     * 
     * @return true if this was the the last occurrence of the signature (according to the mask)
     */
    public abstract boolean remove(Tuple tuple);

    /**
     * Adds a tuple occurrence to the memory, with given signature
     * 
     * @param tuple new tuple added to the memory
     * @param signature precomputed footprint of the tuple according to the mask
     * 
     * @return true if new signature encountered (according to the mask)
     */
    public abstract boolean add(Tuple tuple, Tuple signature);

    /**
     * Adds a tuple occurrence to the memory
     * 
     * @param tuple new tuple added to the memory
     * 
     * @return true if new signature encountered (according to the mask)
     */
    public abstract boolean add(Tuple tuple);

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