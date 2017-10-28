/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.base.itc.alg.incscc;

import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;

/**
 * @author Tamas Szabo
 * 
 */
public class CollectionHelper {

    private CollectionHelper() {/*Utility class constructor*/}
    
    /**
     * Returns the intersection of two sets. It calls {@link Set#retainAll(java.util.Collection)} but returns a new set
     * containing the elements of the intersection.
     * 
     * @param set1
     *            the first set (can be null, interpreted as empty)
     * @param set2
     *            the second set (can be null, interpreted as empty)
     * @return the intersection of the sets
     * @since 1.7
     */
    public static <V> Set<V> intersectionMutable(Set<V> set1, Set<V> set2) {
        if (set1 == null || set2 == null) 
            return CollectionsFactory.createSet();
        
        Set<V> intersection = CollectionsFactory.createSet(set1);
        intersection.retainAll(set2);
        return intersection;
    }

    /**
     * @deprecated renamed to {@link #intersectionMutable(Set, Set)} to clarify
     */
    @Deprecated
    public static <V> Set<V> intersection(Set<V> set1, Set<V> set2) {
        return intersectionMutable(set1, set2);
    }

    
    /**
     * Returns the difference of two sets (S1\S2). It calls {@link Set#removeAll(java.util.Collection)} but returns a
     * new set containing the elements of the difference.
     * 
     * @param set1
     *            the first set (can be null, interpreted as empty)
     * @param set2
     *            the second set (can be null, interpreted as empty)
     * @return the difference of the sets
     * @since 1.7
     */
    public static <V> Set<V> differenceMutable(Set<V> set1, Set<V> set2) {
        if (set1 == null)
            return CollectionsFactory.createSet();
        
        Set<V> difference = CollectionsFactory.createSet(set1);
        if (set2 != null) difference.removeAll(set2);
        return difference;
    }
    
    /**
     * @deprecated renamed to {@link #differenceMutable(Set, Set)} to clarify
     */
    @Deprecated
    public static <V> Set<V> difference(Set<V> set1, Set<V> set2) {
        return differenceMutable(set1, set2);
    }

}
