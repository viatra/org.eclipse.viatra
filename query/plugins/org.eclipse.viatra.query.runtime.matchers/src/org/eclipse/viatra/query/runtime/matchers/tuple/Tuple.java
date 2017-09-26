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

package org.eclipse.viatra.query.runtime.matchers.tuple;

/**
 * Immutable tuple. Obtain instances via utility class {@link Tuples}.
 * @author Gabor Bergmann
 * 
 */
public abstract class Tuple extends AbstractTuple {

    /**
     * Caches precalculated hash value
     */
    protected int cachedHash;

    /**
     * Creates a Tuple instance Derivatives should call calcHash()
     */
    protected Tuple() {
        // calcHash();
    }

    /**
     * Hash calculation. Overrides should keep semantics.
     */
    void calcHash() {
        cachedHash = doCalcHash();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof ITuple) {
            final ITuple other = (ITuple) obj;
            return cachedHash == other.hashCode() && internalEquals(other);
        }
        return false;
    }

    @Override
    public int hashCode() {
        // Calculated by #calcHash
        return cachedHash;
    }

    public Tuple replaceAll(Object obsolete, Object replacement) {
        Object[] oldElements = getElements();
        Object[] newElements = new Object[oldElements.length];
        for (int i = 0; i < oldElements.length; ++i) {
            newElements[i] = obsolete.equals(oldElements[i]) ? replacement : oldElements[i];
        }
        return Tuples.flatTupleOf(newElements);
    }

    @Override
    public Tuple toImmutable() {
        return this;
    }

}
