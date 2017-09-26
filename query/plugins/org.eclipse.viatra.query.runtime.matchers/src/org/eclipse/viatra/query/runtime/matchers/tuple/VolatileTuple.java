/*******************************************************************************
 * Copyright (c) 2010-2017 Zoltan Ujhely, IncQuery Labs
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.matchers.tuple;

/**
 * Mutable tuple without explicit modification commands. In practical terms, the values stored in a volatile tuple can
 * be changed without any notification.
 * 
 * @author Zoltan Ujhelyi
 * @since 1.7
 * 
 */
public abstract class VolatileTuple extends AbstractTuple {

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ITuple))
            return false;
        final ITuple other = (ITuple) obj;
        return internalEquals(other);
    }

    @Override
    public int hashCode() {
        return doCalcHash();
    }

    /**
     * Creates an immutable tuple from the values stored in the tuple. The created tuple will not be updated when the
     * current tuple changes.
     */
    @Override
    public Tuple toImmutable() {
        return Tuples.flatTupleOf(getElements());
    }
}
