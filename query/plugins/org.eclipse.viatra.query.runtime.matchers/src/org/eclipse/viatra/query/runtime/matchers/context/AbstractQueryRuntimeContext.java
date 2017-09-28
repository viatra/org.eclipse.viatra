/*******************************************************************************
 * Copyright (c) 2010-2016, Grill Balázs, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.context;

import org.eclipse.viatra.query.runtime.matchers.tuple.ITuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;

/**
 * This class is intended to be extended by implementors. The main purpose of this abstract implementation to protect
 * implementors from future changes in the interface.
 * 
 * @author Grill Balázs
 * @since 1.4
 *
 */
public abstract class AbstractQueryRuntimeContext implements IQueryRuntimeContext {

    @SuppressWarnings("deprecation")
    @Override
    public void ensureIndexed(IInputKey key, IndexingService service) {
        ensureIndexed(key);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isIndexed(IInputKey key, IndexingService service) {
        return isIndexed(key);
    }

    @SuppressWarnings("deprecation")
    @Override
    public int countTuples(IInputKey key, TupleMask seedMask, ITuple seed) {
        // Fallback for original implementation to make existing runtime context implementations backward compatible
        return countTuples(key, seedMask.revertFrom(seed));
    }

    @SuppressWarnings("deprecation")
    @Override
    public Iterable<Tuple> enumerateTuples(IInputKey key, TupleMask seedMask, ITuple seed) {
        // Fallback for original implementation to make existing runtime context implementations backward compatible
        return enumerateTuples(key, seedMask.revertFrom(seed));
    }

    @SuppressWarnings("deprecation")
    @Override
    public Iterable<? extends Object> enumerateValues(IInputKey key, TupleMask seedMask, ITuple seed) {
        // Fallback for original implementation to make existing runtime context implementations backward compatible
        return enumerateValues(key, seedMask.revertFrom(seed));
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean containsTuple(IInputKey key, TupleMask seedMask, ITuple seed) {
        // Fallback for original implementation to make existing runtime context implementations backward compatible
        return containsTuple(key, seed.toImmutable());
    }

}
