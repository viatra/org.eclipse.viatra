/*******************************************************************************
 * Copyright (c) 2010-2018, Gabor Bergmann, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.memories;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.viatra.query.runtime.matchers.tuple.ITuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory.MemoryType;
import org.eclipse.viatra.query.runtime.matchers.util.IMemory;

/**
 * Common parts of nullary and identity specializations.
 * 
 * @noextend This class is not intended to be subclassed by clients.
 * @author Gabor Bergmann
 * @since 2.0
 */
abstract class AbstractTrivialMemory<Timestamp extends Comparable<Timestamp>> extends MaskedTupleMemory<Timestamp> {

    protected IMemory<Tuple> tuples;

    protected AbstractTrivialMemory(TupleMask mask, MemoryType bucketType, Object owner) {
        super(mask, owner);
        tuples = CollectionsFactory.createMemory(Object.class, bucketType);
    }
    
    @Override
    public Map<Tuple, Timestamp> getWithTimestamp(ITuple signature) {
        throw new UnsupportedOperationException("Default memories do not support timestamp-based lookup!");
    }

    @Override
    public void clear() {
        tuples.clear();
    }

    @Override
    public int getTotalSize() {
        return tuples.size();
    }

    @Override
    public Iterator<Tuple> iterator() {
        return tuples.iterator();
    }

}
