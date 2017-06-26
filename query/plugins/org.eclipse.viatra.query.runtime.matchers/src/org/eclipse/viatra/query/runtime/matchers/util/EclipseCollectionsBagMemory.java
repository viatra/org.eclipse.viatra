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

package org.eclipse.viatra.query.runtime.matchers.util;

import java.util.Iterator;

import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;

/**
 * Eclipse Collections-based multiset for tuples. Can contain duplicate occurrences of the same matching.
 * 
 * <p>Inherits Eclipse Collections' Object-to-Int primitive hashmap and counts the number of occurrences of each value. 
 * Element is deleted if # of occurences drops to 0.
 * 
 * @author Gabor Bergmann.
 * @since 1.7
 * @noreference
 */
public class EclipseCollectionsBagMemory<T> extends ObjectIntHashMap<T> implements IMultiset<T> {

    public EclipseCollectionsBagMemory() {
        super();
    }

    @Override
    public int getCount(T value) {
        return super.getIfAbsent(value, 0);
    }
    @Override
    public boolean containsNonZero(T value) {
        return super.containsKey(value);
    }
    @Override
    public boolean addOne(T value) {
        int oldCount = super.getIfAbsent(value, 0);

        super.put(value, oldCount + 1);

        return oldCount == 0;
    }

    @Override
    public boolean addPositive(T value, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("The count value must be positive!");
        }

        int oldCount = super.getIfAbsent(value, 0);

        super.put(value, oldCount + count);

        return oldCount == 0;
    }
    
    @Override
    public boolean addSigned(T value, int count) {
        int oldCount = super.getIfAbsent(value, 0);
        int newCount = oldCount + count;
        
        if (newCount < 0)
            throw new IllegalStateException(String.format(
                    "Cannot remove %d occurrences of value '%s' as only %d would remain in %s", 
                    count, value, newCount, this));
        else if (newCount == 0)
            super.removeKey(value);
        else // (newCount > 0)
            super.put(value, newCount);
       
        return oldCount == 0;
    }
    
    
    @Override
    public boolean removeOne(T value) {
        int oldCount = super.getIfAbsent(value, 0);
        if (oldCount == 0)
            throw new IllegalStateException(String.format(
                    "Cannot remove value '%s' that is not contained in %s", 
                    value, this));
        
        int rest = oldCount - 1;
        boolean empty = rest == 0;

        if (!empty) {
            super.put(value, rest);
        } else {
            super.remove(value);
        }

        return empty;
    }

    @Override
    public void clearAllOf(T value) {
        super.remove(value);
    }


    @Override
    public Iterator<T> iterator() {
        return super.keySet().iterator();
    }

    @Override
    public String toString() {
        return "TM" + super.toString();
    }

}
