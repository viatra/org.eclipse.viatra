/*******************************************************************************
 * Copyright (c) 2004-2008 Akos Horvath, Gergely Varro and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Akos Horvath, Gergely Varro - initial API and implementation from the VIATRA2 project
 *    Zoltan Ujhelyi - adaptation to EMF-IncQuery engine
 *******************************************************************************/

package org.eclipse.incquery.runtime.localsearch;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;



public class MatchingTable extends AbstractCollection<MatchingFrame> {
    private Map<MatchingKey,Collection<MatchingFrame>> matchings;
    
    private final class MatchingIterator implements Iterator<MatchingFrame> {
        Iterator<Entry<MatchingKey, Collection<MatchingFrame>>> iterator;
        
        private MatchingIterator() {
            iterator = matchings.entrySet().iterator();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public MatchingFrame next() {
            if (hasNext()) {
                final Collection<MatchingFrame> frames = iterator.next().getValue();
                Iterator<MatchingFrame> frameIterator = frames.iterator();
                if (frameIterator.hasNext()) {
                    return frameIterator.next();
                } else {
                    throw new NoSuchElementException();
                }
            } else {
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public MatchingTable() {
        matchings = new HashMap<MatchingKey, Collection<MatchingFrame>>();
    }
    
    @Override
    public Iterator<MatchingFrame> iterator() {
        return new MatchingIterator();
    }

    @Override
    public int size() {
        return matchings.keySet().size();
    }

    public void put(MatchingKey key, MatchingFrame value) {
        Collection<MatchingFrame> coll = matchings.get(key);
        if (coll == null) {
            coll = new HashSet<MatchingFrame>();
            matchings.put(key, coll);
        }
        coll.add(value);
    }
}
