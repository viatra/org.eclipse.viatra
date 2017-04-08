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
import java.util.Map;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;

/**
 * Multiset for tuples. Can contain duplicate occurrences of the same matching.
 * 
 * @author Gabor Bergmann.
 * 
 */
public class TupleMemory implements Clearable, Collection<Tuple> {
    /**
     * Counts the number of occurrences of each pattern. Element is deleted if # of occurences drops to 0.
     */
    protected Map<Tuple, Integer> occurrences;

    /**
     * 
     */
    public TupleMemory() {
        super();
        occurrences = CollectionsFactory.getMap();
    }

    /**
     * Returns the number of occurrences of the given tuple.
     * 
     * @param ps
     *            the tuple
     * @return the number of occurrences
     * @since 1.6
     */
    public int get(Tuple ps) {
        Integer count = occurrences.get(ps);
        if (count == null) {
            return 0;
        } else {
            return count;
        }
    }

    /**
     * Adds one tuple occurrence to the memory.
     * 
     * @param ps
     *            the tuple
     * @return true if the tuple was not present before in the memory
     */
    @Override
    public boolean add(Tuple ps) {
        return add(ps, 1);
    }

    /**
     * Adds the given number of tuple occurrences to the memory. The count value must be a positive number.
     * 
     * @param ps
     *            the tuple
     * @param count
     *            the number of occurrences
     * @return true if the tuple was not present before in the memory
     * @since 1.6
     */
    public boolean add(Tuple ps, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("The count value must be positive!");
        }

        Integer oldCount = occurrences.get(ps);

        if (oldCount != null) {
            occurrences.put(ps, oldCount + count);
        } else {
            occurrences.put(ps, count);
        }

        return oldCount == null;
    }
    
    /**
     * Adds one tuple occurrence to the memory if the tuple was already contained in the memory. 
     * In this case the method returns true. If the tuple is not present in the memory, it will not be added and 
     * the method returns false.   
     * 
     * @param ps the tuple
     * @return true if the addition was successful, that is, the tuple was already present, false otherwise
     * @since 1.6
     */
    public boolean demandAdd(Tuple ps) {
        return demandAdd(ps, 1);
    }
    
    /**
     * Adds the given number of tuple occurrences to the memory if the tuple was already contained in the memory. 
     * In this case the method returns true. If the tuple is not present in the memory, it will not be added and 
     * the method returns false.   
     * 
     * @param ps the tuple
     * @param count the number of occurrences
     * @return true if the addition was successful, that is, the tuple was already present, false otherwise
     * @since 1.6
     */
    public boolean demandAdd(Tuple ps, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("The count value must be positive!");
        }

        Integer oldCount = occurrences.get(ps);

        if (oldCount != null) {
            occurrences.put(ps, oldCount + count);
        }

        return oldCount != null;
    }

    /**
     * Removes one occurrence of the given tuple from the memory
     * 
     * @return true if this was the the last occurrence of the tuple
     */
    public boolean remove(Tuple ps) {
        int rest = occurrences.get(ps) - 1;
        boolean empty = rest == 0;

        if (!empty) {
            occurrences.put(ps, rest);
        } else {
            occurrences.remove(ps);
        }

        return empty;
    }

    /**
     * Removes all occurrences of the given tuple from the memory.
     * 
     * @param ps
     *            the tuple to remove
     * @since 1.6
     */
    public void clear(Tuple ps) {
        occurrences.remove(ps);
    }

    @Override
    public void clear() {
        occurrences.clear();

    }

    @Override
    public Iterator<Tuple> iterator() {
        return occurrences.keySet().iterator();
    }

    @Override
    public boolean addAll(Collection<? extends Tuple> arg0) {
        boolean change = false;
        for (Tuple ps : arg0) {
            change |= add(ps);
        }
        return change;
    }

    @Override
    public boolean contains(Object arg) {
        return occurrences.containsKey(arg);
    }

    @Override
    public boolean containsAll(Collection<?> arg0) {
        return occurrences.keySet().containsAll(arg0);
    }

    @Override
    public boolean isEmpty() {
        return occurrences.isEmpty();
    }

    @Override
    public boolean remove(Object arg0) {
        return remove((Tuple) arg0);
    }

    @Override
    public boolean removeAll(Collection<?> arg0) {
        boolean change = false;
        for (Object o : arg0) {
            change |= remove(o);
        }
        return change;
    }

    @Override
    public boolean retainAll(Collection<?> arg0) {
        return occurrences.keySet().retainAll(arg0);
    }

    @Override
    public int size() {
        return occurrences.size();
    }

    @Override
    public Object[] toArray() {
        return toArray(new Object[occurrences.size()]);
    }

    @Override
    public <T> T[] toArray(T[] arg0) {
        return occurrences.keySet().toArray(arg0);
    }

    @Override
    public String toString() {
        return "TM" + occurrences.keySet();
    }

}
