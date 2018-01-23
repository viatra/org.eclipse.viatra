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
     * @return true if the given value is contained with a nonzero multiplicity
     */
    boolean containsNonZero(T value);

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

}