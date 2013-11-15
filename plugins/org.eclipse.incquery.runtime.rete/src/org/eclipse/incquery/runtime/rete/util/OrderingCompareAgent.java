/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.rete.util;

/**
 * Comparing agent for an ordering. Terminology: the "preferred" item will register as LESS.
 * 
 * @author Gabor Bergmann
 * 
 */
public abstract class OrderingCompareAgent<T> {
    protected T a;
    protected T b;

    /**
     * @param a
     * @param b
     */
    public OrderingCompareAgent(T a, T b) {
        super();
        this.a = a;
        this.b = b;
    }

    int result = 0;

    protected abstract void doCompare();

    /**
     * @return the result
     */
    public int compare() {
        doCompare();
        return result;
    }

    // COMPARISON HELPERS
    protected boolean isUnknown() {
        return result == 0;
    }

    /**
     * @pre result == 0
     */
    protected boolean consider(int partial) {
        if (isUnknown())
            result = partial;
        return isUnknown();
    }

    protected boolean swallowBoolean(boolean x) {
        return x;
    }

    // PREFERENCE FUNCTIONS
    protected static int dontCare() {
        return 0;
    }

    protected static int preferTrue(boolean b1, boolean b2) {
        return (b1 ^ b2) ? (b1 ? -1 : +1) : 0;
    }

    protected static int preferFalse(boolean b1, boolean b2) {
        return (b1 ^ b2) ? (b2 ? -1 : +1) : 0;
    }

    protected static <U> int preferLess(Comparable<U> c1, U c2) {
        return c1.compareTo(c2);
    }

    protected static <U> int preferMore(Comparable<U> c1, U c2) {
        return -c1.compareTo(c2);
    }

}
