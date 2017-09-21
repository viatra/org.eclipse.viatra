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
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.util.Clearable;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.rete.network.Node;

/**
 * @author Gabor Bergmann
 * 
 *         Indexes a collection of Tuples according to their masks.
 * @noextend This class is not intended to be subclassed by clients.
 */
public class MaskedTupleMemory implements Clearable, Iterable<Tuple> {
    /**
     * Counts the number of occurences of each pattern. Element is deleted if # of occurences drops to 0.
     */
    protected Map<Tuple, Collection<Tuple>> matchings;

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
     */
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
    public boolean add(Tuple ps, Tuple signature) {
        Collection<Tuple> coll = matchings.get(signature);
        boolean change = (coll == null);

        if (change) {
            coll = CollectionsFactory.createSet();
            matchings.put(signature, coll);
        }
        if (!coll.add(ps)) {
            throw new IllegalStateException(
                    String.format(
                            "Duplicate insertion of tuple %s into node %s", 
                            ps, owner));
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
        Collection<Tuple> coll = matchings.get(signature);
        if (!coll.remove(ps)) {
            throw new IllegalStateException(
                    String.format(
                            "Duplicate deletion of tuple %s from node %s", 
                            ps, owner));
        }

        boolean change = coll.isEmpty();
        if (change)
            matchings.remove(signature);

        return change;
    }

    /**
     * Retrieves entries that have the specified signature
     * 
     * @return collection of matchings found
     */
    public Collection<Tuple> get(Tuple signature) {
        return matchings.get(signature);
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

    class MaskedPatternIterator implements Iterator<Tuple> {
        // private MaskedTupleMemory memory;
        Iterator<Collection<Tuple>> signatureGroup;
        Iterator<Tuple> element;

        public MaskedPatternIterator(MaskedTupleMemory memory) {
            // this.memory = memory;
            signatureGroup = memory.matchings.values().iterator();
            Set<Tuple> emptySet = Collections.emptySet();
            element = emptySet.iterator();
        }

        public boolean hasNext() {
            return (element.hasNext() || signatureGroup.hasNext());
        }

        public Tuple next() throws NoSuchElementException {
            if (element.hasNext())
                return element.next();
            else if (signatureGroup.hasNext()) {
                element = signatureGroup.next().iterator();
                return element.next();
            } else
                throw new NoSuchElementException();
        }

        /**
         * Not implemented
         */
        public void remove() {

        }

    }

    @Override
    public String toString() {
        return "MTM<" + mask + "|" + matchings + ">";
    }

    public int getTotalSize() {
        // return matchings.values().size(); // return the number of values (collections of tuples)
        // instead, flatten 
        int i = 0;
        for (Collection<Tuple> v : matchings.values()) {
            i+=v.size();
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
