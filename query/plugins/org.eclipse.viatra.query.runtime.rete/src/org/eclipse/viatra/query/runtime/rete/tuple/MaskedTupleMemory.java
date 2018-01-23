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

package org.eclipse.viatra.query.runtime.rete.tuple;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.util.Clearable;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.matchers.util.IMemoryView;
import org.eclipse.viatra.query.runtime.matchers.util.IMultiLookup;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory.BucketType;
import org.eclipse.viatra.query.runtime.matchers.util.IMultiLookup.ChangeGranularity;
import org.eclipse.viatra.query.runtime.rete.network.Node;

/**
 * @author Gabor Bergmann
 * 
 *         Indexes a collection of Tuples by their signature (i.e. footprint, projection) obtained according to a mask.
 *         Must belong to an owner {@link Node}.
 *         
 *         <p> TODO experiment with memory-saving alternatives, 
 *          e.g. if the mask has width 1, 
 *          {@link #signatureToTuples} can be keyed by the unary values instead of tuples.
 *         
 *         
 * @noextend This class is not intended to be subclassed by clients.
 */
public class MaskedTupleMemory implements Clearable, Iterable<Tuple> {
    /**
     * Maps a signature tuple to the bucket of tuples with the given signature.
     * @since 2.0
     */
    protected IMultiLookup<Tuple, Tuple> signatureToTuples;

    /**
     * The mask by which the tuples are indexed.
     */
    protected TupleMask mask;

    /**
     * The node owning this memory. May be null.
     * @since 1.7
     */
    protected Node owner;

    /**
     * @param mask
     *            The mask used to index the matchings
     * @param owner the node that owns this memory
     * @since 1.7
     */
    public MaskedTupleMemory(TupleMask mask, Node owner) {
        this.mask = mask;
        this.owner = owner;
        signatureToTuples = CollectionsFactory.<Tuple, Tuple>createMultiLookup(
                Object.class, BucketType.SETS, Object.class);
    }

    /**
     * Adds a tuple occurrence to the memory
     * 
     * @param tuple new tuple added to the memory
     * 
     * @return true if new signature encountered (according to the mask)
     */
    public boolean add(Tuple tuple) {
        Tuple signature = mask.transform(tuple);
        return add(tuple, signature);
    }

    /**
     * Adds a tuple occurrence to the memory, with given signature
     * 
     * @param tuple new tuple added to the memory
     * @param signature precomputed footprint of the tuple according to the mask
     * 
     * @return true if new signature encountered (according to the mask)
     */
    @SuppressWarnings("unchecked")
    public boolean add(Tuple tuple, Tuple signature) {
        try {
            return signatureToTuples.addPair(signature, tuple) == ChangeGranularity.KEY;
        } catch (IllegalStateException ex) { // ignore worthless internal exception details
            throw new IllegalStateException(
                    String.format(
                            "Duplicate insertion of tuple %s into node %s", 
                            tuple, owner));
        }
    
    }

    /**
     * Removes a tuple occurrence from the memory
     * 
     * @param tuple old tuple removed from the memory
     * 
     * @return true if this was the the last occurrence of the signature (according to the mask)
     */
    public boolean remove(Tuple tuple) {
        Tuple signature = mask.transform(tuple);
        return remove(tuple, signature);
    }

    /**
     * Removes a tuple occurrence from the memory, with given signature
     * 
     * @param tuple old tuple removed from the memory
     * @param signature precomputed footprint of the tuple according to the mask
     * 
     * @return true if this was the the last occurrence of the signature (according to the mask)
     */
    public boolean remove(Tuple tuple, Tuple signature) {
        try {
            return signatureToTuples.removePair(signature, tuple) == ChangeGranularity.KEY;
        } catch (IllegalStateException ex) { // ignore worthless internal exception details
            throw new IllegalStateException(
                    String.format(
                            "Duplicate deletion of tuple %s from node %s", 
                            tuple, owner));
        }
    }

    /**
     * Retrieves tuples that have the specified signature
     * 
     * @return collection of tuples found, null if none
     */
    @SuppressWarnings("unchecked")
    public Collection<Tuple> get(Tuple signature) {
        IMemoryView<Tuple> bucket = signatureToTuples.lookup(signature);
        return bucket == null ? null : bucket.distinctValues();
    }

    public void clear() {
        signatureToTuples.clear();
    }

    /**
     * Retrieves a read-only view of exactly those signatures for which at least one tuple is stored
     * @since 2.0
     */
    public Iterable<Tuple> getSignatures() {
        return signatureToTuples.distinctKeys();
    }

    /**
     * Iterates over distinct tuples stored in the memory, regardless of their signatures.
     */
    public Iterator<Tuple> iterator() {
        return signatureToTuples.distinctValues().iterator();
    }


    @Override
    public String toString() {
        return "MTM<" + mask + "|" + signatureToTuples + ">";
    }

    /**
     * @return the total number of distinct tuples stored.
     * 
     * <p> This is currently not cached but computed on demand. 
     * It is therefore not efficient, and shall only be used for debug / profiling purposes.
     */
    public int getTotalSize() {
        int i = 0;
        for (Tuple key : signatureToTuples.distinctKeys()) {
            i += signatureToTuples.lookup(key).size();
        }
        return i;
    }
    
    /**
     * @return the number of distinct signatures of all stored tuples.
     */
    public int getKeysetSize() {
        return signatureToTuples.countKeys();
    }

    /**
     * The node owning this memory. May be null.
     * @since 1.7
     */
    public Node getOwner() {
        return owner;
    }

    
}
