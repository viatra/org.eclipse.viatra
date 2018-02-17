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

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.collections.api.LongIterable;
import org.eclipse.collections.api.iterator.LongIterator;
import org.eclipse.collections.api.set.primitive.LongSet;
import org.eclipse.collections.impl.set.mutable.primitive.LongHashSet;

/**
 * @author Gabor Bergmann
 * @since 2.0
 */
public class EclipseCollectionsLongSetMemory implements ISetMemory<Long> {

    LongHashSet wrapped = new LongHashSet();

    @Override
    public boolean addOne(Long value) {
        return wrapped.add(value);
    }

    @Override
    public boolean addSigned(Long value, int count) {
        if (count == 1) return addOne(value);
        else if (count == -1) return removeOne(value); 
        else throw new IllegalStateException();
    }

    @Override
    public boolean removeOne(Long value) {
        if (!wrapped.remove(value))
            throw new IllegalStateException();
        return true;
    }

    @Override
    public void clearAllOf(Long value) {
        wrapped.remove(value);
    }

    @Override
    public void clear() {
        wrapped.clear();
    }

    @Override
    public int getCount(Long value) {
        return wrapped.contains(value) ? 1 : 0;
    }

    @Override
    public boolean containsNonZero(Long value) {
        return wrapped.contains(value);
    }

    @Override
    public int size() {
        return wrapped.size();
    }

    @Override
    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

    @Override
    public Iterator<Long> iterator() {
        return iteratorOf(wrapped);
    }

    @Override
    public Set<Long> distinctValues() {
        return new SetWrapper(wrapped);
    }
    /**
     * Helper for iterating a LongIterable
     */
    public static Iterator<Long> iteratorOf(LongIterable wrapped) {
        return new Iterator<Long>() {
            LongIterator longIterator = wrapped.longIterator();

            @Override
            public boolean hasNext() {
                return longIterator.hasNext();
            }

            @Override
            public Long next() {
                return longIterator.next();
            }
        };
    }
    /**
     * Helper that presents a primitive collection as a Set view
     * @author Gabor Bergmann
     */
    public static final class SetWrapper implements Set<Long> {
        private LongSet wrapped;

        /**
         * @param wrapped
         */
        public SetWrapper(LongSet wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public int size() {
            return wrapped.size();
        }

        @Override
        public boolean isEmpty() {
            return wrapped.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return o instanceof Long &&  wrapped.contains((Long)o);
        }

        @Override
        public Iterator<Long> iterator() {
            return iteratorOf(wrapped);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            for (Object object : c) {
                if (contains(object))
                    return true;
            }
            return false;
        }

        @Override
        public Object[] toArray() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(Long e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends Long> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }
    }

            
}
