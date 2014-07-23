/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.designspace.impl.emf;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.emf.common.util.EList;
import org.eclipse.viatra.dse.emf.designspace.Transition;


/**
 * Helper class that implements the {@link List} interface to provide a thread safe gateway to the underlying
 * {@link EList}. Only the used interface methods have been implemented, the rest throws
 * {@link UnsupportedOperationException}.
 * 
 * @author Miklos Foldenyi
 */
class EMFConcurrentTransitionList implements List<EMFThreadsafeTransition> {

    private final EList<Transition> internalEList;

    EMFConcurrentTransitionList(EList<Transition> internalEList) {
        this.internalEList = internalEList;
    }

    @Override
    public synchronized int size() {
        return internalEList.size();
    }

    @Override
    public synchronized boolean isEmpty() {
        return internalEList.isEmpty();
    }

    @Override
    public synchronized boolean contains(Object o) {
        if (o instanceof EMFThreadsafeTransition) {
            EMFThreadsafeTransition transition = (EMFThreadsafeTransition) o;
            return internalEList.contains(transition.getInternalTransition());
        }
        return false;
    }

    @Override
    public synchronized Iterator<EMFThreadsafeTransition> iterator() {
        return new Iterator<EMFThreadsafeTransition>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                synchronized (EMFConcurrentTransitionList.this) {
                    return internalEList.size() > index;
                }
            }

            @Override
            public EMFThreadsafeTransition next() {
                synchronized (EMFConcurrentTransitionList.this) {
                    return (EMFThreadsafeTransition) internalEList.get(index++).getThreadsafeFacade();
                }
            }

            @Override
            public void remove() {
                synchronized (EMFConcurrentTransitionList.this) {
                    internalEList.remove(--index);
                }
            }
        };
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
    public synchronized boolean add(EMFThreadsafeTransition e) {
        return internalEList.add(((EMFThreadsafeTransition) e).getInternalTransition());
    }

    @Override
    public synchronized boolean remove(Object o) {
        if (o instanceof EMFThreadsafeTransition) {
            EMFThreadsafeTransition transition = (EMFThreadsafeTransition) o;
            return internalEList.remove(transition.getInternalTransition());
        } else {
            return false;
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends EMFThreadsafeTransition> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends EMFThreadsafeTransition> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void clear() {
        internalEList.clear();
    }

    @Override
    public synchronized EMFThreadsafeTransition get(int index) {
        return (EMFThreadsafeTransition) (internalEList.get(index)).getThreadsafeFacade();
    }

    @Override
    public synchronized EMFThreadsafeTransition set(int index, EMFThreadsafeTransition element) {
        Transition previousTransition = internalEList.set(index,
                ((EMFThreadsafeTransition) element).getInternalTransition());
        if (previousTransition == null) {
            return null;
        } else {
            return (EMFThreadsafeTransition) previousTransition.getThreadsafeFacade();
        }
    }

    @Override
    public synchronized void add(int index, EMFThreadsafeTransition element) {
        internalEList.add(((EMFThreadsafeTransition) element).getInternalTransition());
    }

    @Override
    public synchronized EMFThreadsafeTransition remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<EMFThreadsafeTransition> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<EMFThreadsafeTransition> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<EMFThreadsafeTransition> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

}