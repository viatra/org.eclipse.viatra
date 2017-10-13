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
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.util.Clearable;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory.MarkedSet;
import org.eclipse.viatra.query.runtime.rete.network.Node;

/**
 * @author Gabor Bergmann
 * 
 *         Indexes a collection of Tuples according to their masks.
 *         Must belong to an owner {@link Node}.
 * @noextend This class is not intended to be subclassed by clients.
 */
public class MaskedTupleMemory implements Clearable, Iterable<Tuple> {
    /**
     * Maps a signature tuple to the bucket of tuples with the given signature.
     * <p> Invariant: Value is either <ul>
     *  <li>a {@link Tuple} if there is only that one tuple in the bucket, or </li>
     *  <li>a {@link MarkedSet} if there are 2 or more tuples in the in the bucket.</li>
     * </ul>
     * If the signature has no associated tuples in the memory, the map must not contain the signature as key.
     */
    protected Map<Tuple, Object> matchings;

    /**
     * The mask used to index the matchings
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
     * @deprecated use {@link #MaskedTupleMemory(TupleMask, Node)}
     */
    @Deprecated
    public MaskedTupleMemory(TupleMask mask) {
        this(mask, null);
    }

    /**
     * @param mask
     *            The mask used to index the matchings
     * @param owner the node that owns this memory
     * @since 1.7
     */
    public MaskedTupleMemory(TupleMask mask, Node owner) {
        this.mask = mask;
        this.owner = owner;
        matchings = CollectionsFactory.createMap();
    }

    /**
     * Adds a pattern occurence to the memory
     * 
     * @param ps
     * 
     * @return true if new signature encountered
     */
    public boolean add(Tuple ps) {
        Tuple signature = mask.transform(ps);
        return add(ps, signature);
    }

    /**
     * Adds a pattern occurence to the memory, with given signature
     * 
     * @param ps
     * @param signature
     * 
     * @return true if new signature encountered
     */
    @SuppressWarnings("unchecked")
    public boolean add(Tuple ps, Tuple signature) {
        Object old = matchings.get(signature);
        boolean change = (old == null);

        if (change) { // key was not present
            matchings.put(signature, ps);
        } else { // key was already present
            MarkedSet<Tuple> coll;
            if (old instanceof MarkedSet) { // ... as set
                coll = (MarkedSet<Tuple>) old;
            } else { // ... as singleton
                coll = CollectionsFactory.createMarkedSet();
                coll.add((Tuple) old);
                matchings.put(signature, coll);
            }
            if (!coll.add(ps)) {
                throw new IllegalStateException(
                        String.format(
                                "Duplicate insertion of tuple %s into node %s", 
                                ps, owner));
            }
        }

        return change;
    }

    /**
     * Removes a pattern occurence from the memory
     * 
     * @return true if this was the the last occurence of the signature
     */
    public boolean remove(Tuple ps) {
        Tuple signature = mask.transform(ps);
        return remove(ps, signature);
    }

    /**
     * Removes a pattern occurence from the memory, with given signature
     * 
     * @return true if this was the the last occurence of the signature
     */
    public boolean remove(Tuple ps, Tuple signature) {
        Object old = matchings.get(signature);
        if (old instanceof MarkedSet<?>) { // collection
            @SuppressWarnings("unchecked")
            MarkedSet<Tuple> coll = (MarkedSet<Tuple>) old;
            if (coll.remove(ps)) {
                if (1 == coll.size()) { // only one remains
                    Tuple remainingSingleton = coll.iterator().next();
                    matchings.put(signature, remainingSingleton);
                }
                return false; // at least one remains anyway
            }
        } else if (ps.equals(old)) { // matching singleton
            matchings.remove(signature);
            return true;
        }
        throw new IllegalStateException(
                String.format(
                        "Duplicate deletion of tuple %s from node %s", 
                        ps, owner));
    }

    /**
     * Retrieves entries that have the specified signature
     * 
     * @return collection of matchings found
     */
    @SuppressWarnings("unchecked")
    public Collection<Tuple> get(Tuple signature) {
        Object bucket = matchings.get(signature);
        if (bucket instanceof MarkedSet<?>) {
            return (MarkedSet<Tuple>) bucket;
        } else { // singleton or empty
            if (bucket == null) 
                return null;
            return Collections.singleton((Tuple)bucket);
        }
    }

    public void clear() {
        matchings.clear();
    }

    /**
     * Retrieves a read-only collection of exactly those signatures for which at least one tuple is stored
     * 
     * @return collection of significant signatures
     */
    public Collection<Tuple> getSignatures() {
        return matchings.keySet();
    }

    public Iterator<Tuple> iterator() {
        return new MaskedPatternIterator(this);
    }

    
    private static final Iterator<Tuple> EMPTY_ITERATOR = Collections.<Tuple>emptySet().iterator(); 
    class MaskedPatternIterator implements Iterator<Tuple> {
        Iterator<Object> signatureGroup;
        Iterator<Tuple> element;
        
        public MaskedPatternIterator(MaskedTupleMemory memory) {
            signatureGroup = memory.matchings.values().iterator();
            element = EMPTY_ITERATOR;
        }

        public boolean hasNext() {
            return (element.hasNext() || signatureGroup.hasNext());
        }

        @SuppressWarnings("unchecked")
        public Tuple next() throws NoSuchElementException {
            if (element.hasNext())
                return element.next();
            else if (signatureGroup.hasNext()) {
                Object bucket = signatureGroup.next();
                if (bucket instanceof MarkedSet<?>) {
                    element = ((MarkedSet<Tuple>) bucket).iterator();
                    return element.next();
                } else {
                    element = EMPTY_ITERATOR;
                    return (Tuple) bucket;
                }
            } else
                throw new NoSuchElementException();
        }

        /**
         * Not implemented
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    @Override
    public String toString() {
        return "MTM<" + mask + "|" + matchings + ">";
    }

    public int getTotalSize() {
        int i = 0;
        for (Object v : matchings.values()) {
            if (v instanceof MarkedSet<?>) {
                i+=((MarkedSet<?>) v).size();
            } else {
                i++;
            }
        }
        return i;
    }
    
    public int getKeysetSize() {
        return matchings.keySet().size();
    }

    /**
     * The node owning this memory. May be null.
     * @since 1.7
     */
    public Node getOwner() {
        return owner;
    }

    
}
