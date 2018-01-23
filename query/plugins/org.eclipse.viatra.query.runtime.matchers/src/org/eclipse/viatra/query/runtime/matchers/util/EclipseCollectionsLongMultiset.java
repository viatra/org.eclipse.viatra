/*******************************************************************************
 * Copyright (c) 2010-2018, Gabor Bergmann, IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.util;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.collections.impl.map.mutable.primitive.LongIntHashMap;

/**
 * @author Gabor Bergmann
 * @since 2.0
 * TODO refactor common methods with {@link EclipseCollectionsMultiset}
 */
public class EclipseCollectionsLongMultiset extends LongIntHashMap implements IMultiset<Long> {

    @Override
    public boolean addOne(Long value) {
        int oldCount = super.getIfAbsent(value, 0);

        super.put(value, oldCount + 1);

        return oldCount == 0;
    }

    @Override
    public boolean addSigned(Long value, int count) {
        int oldCount = super.getIfAbsent(value, 0);
        int newCount = oldCount + count;
        
        boolean becomesZero = newCount == 0;
        if (newCount < 0)
            throw new IllegalStateException(String.format(
                    "Cannot remove %d occurrences of value '%s' as only %d would remain in %s", 
                    count, value, newCount, this));
        else if (becomesZero)
            super.removeKey(value);
        else // (newCount > 0)
            super.put(value, newCount);
       
        return becomesZero || oldCount == 0;
    }

    @Override
    public boolean removeOne(Long value) {
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
    public void clearAllOf(Long value) {
        super.remove(value);
    }

    @Override
    public int getCount(Long value) {
        return super.getIfAbsent(value, 0);
    }

    @Override
    public boolean containsNonZero(Long value) {
        return super.containsKey(value);
    }

    @Override
    public Iterator<Long> iterator() {
        return EclipseCollectionsLongSetMemory.iteratorOf(super.keySet());
    }

    @Override
    public boolean addPositive(Long value, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("The count value must be positive!");
        }

        int oldCount = super.getIfAbsent(value, 0);

        super.put(value, oldCount + count);

        return oldCount == 0;
    }

    @Override
    public Set<Long> distinctValues() {
        return new EclipseCollectionsLongSetMemory.SetWrapper(super.keySet());
    }


}
