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

import java.util.Set;

/**
 * A read-only view on a memory containing a positive or negative number of equal() copies for some values.
 * During iterations, each distinct value is iterated only once.
 * 
 * <p> See {@link IMemory}.
 * 
 * @author Gabor Bergmann
 *
 * @since 2.0
 */
public interface IMemoryView<T> extends Iterable<T> {

    /**
     * Returns the number of occurrences of the given value.
     * 
     * @return the number of occurrences
     */
    int getCount(T value);

    /**
     * Returns the number of occurrences of the given value (which may be of any type).
     * 
     * @return the number of occurrences
     */
    int getCountUnsafe(Object value);

    /**
     * @return true if the given value is contained with a nonzero multiplicity
     */
    boolean containsNonZero(T value);

    /**
     * @return true if the given value (which may be of any type) is contained with a nonzero multiplicity
     */
    boolean containsNonZeroUnsafe(Object value);
        
    /**
     * @return the number of distinct values 
     */
    int size();

    /**
     * 
     * @return iff contains at least one value with non-zero occurrences
     */
    boolean isEmpty();

    /**
     * The set of distinct values
     */
    Set<T> distinctValues();
    

    /**
     * Where supported, returns the stored element that is equal to the given value, or null if none.
     * Useful for canonicalization in case of non-identity equals(). 
     * 
     * <p> For collections that do not support canonicalization, simply returns the argument if contained, null if none.
     * 
     * @return a value equal to the argument if such a value is stored, or null if none
     */
    default T theContainedVersionOf(T value) {
        if (containsNonZero(value)) return value; else return null;
    }

    /**
     * Where supported, returns the stored element that is equal to the given value (of any type), 
     * or null if none. 
     * Useful for canonicalization in case of non-identity equals(). 
     * 
     * <p> For collections that do not support canonicalization, simply returns the argument if contained, null if none.
     *
     * @return a value equal to the argument if such a value is stored, or null if none
     */
    @SuppressWarnings("unchecked")
    default T theContainedVersionOfUnsafe(Object value) {
        if (containsNonZeroUnsafe(value)) return (T) value; else return null;
    }
    

}