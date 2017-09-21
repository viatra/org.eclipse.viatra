/*******************************************************************************
 * Copyright (c) 2010-2017, Gabor Bergmann, IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.util;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Default implementation based on Java Collections.
 * 
 * <p>Inherits from java.util.HashMap to realize a Object-to-Int map and counts the number of occurrences of each value. 
 * Element is deleted if # of occurences drops to 0.
 * 
 * @author Gabor Bergmann
 * @since 1.7
 */
public class JavaBagMemory<T> extends HashMap<T, Integer> implements IMultiset<T> {

    private static final long serialVersionUID = 1L;
    
    @Override
    public int getCount(T value) {
        Integer count = super.get(value);
        if (count == null) {
            return 0;
        } else {
            return count;
        }
    }
    @Override
    public boolean containsNonZero(T value) {
        return super.containsKey(value);
    }

    @Override
    public boolean addOne(T value) {
        Integer oldCount = super.get(value);

        if (oldCount != null) {
            super.put(value, oldCount + 1);
        } else {
            super.put(value, 1);
        }

        return oldCount == null;
    }

    @Override
    public boolean addPositive(T value, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("The count value must be positive!");
        }

        Integer oldCount = super.get(value);

        if (oldCount != null) {
            super.put(value, oldCount + count);
        } else {
            super.put(value, count);
        }

        return oldCount == null;
    }
    
    @Override
    public boolean addSigned(T value, int count) {
        Integer oldCount = super.get(value);

        int newCount = (oldCount == null) ? count : oldCount + count;
        
        if (newCount < 0) {
            throw new IllegalStateException(String.format(
                    "Cannot remove %d occurrences of value '%s' as only %d would remain in %s", 
                    count, value, newCount, this));
        } else if (newCount != 0) {
            super.put(value, newCount);
        } else { // newCount == 0
            super.remove(value);
        }

        return oldCount == null;
    }
    
    @Override
    public boolean removeOne(T value) {
        Integer oldCount = super.get(value);
        if (oldCount == null)
            throw new IllegalStateException(String.format("Cannot remove value '%s' that is not contained in %s", value, this));
        
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
