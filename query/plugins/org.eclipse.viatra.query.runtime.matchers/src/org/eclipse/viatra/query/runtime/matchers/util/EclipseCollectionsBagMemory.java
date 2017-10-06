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
public abstract class EclipseCollectionsBagMemory<T> extends ObjectIntHashMap<T> implements IMemory<T> {

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
