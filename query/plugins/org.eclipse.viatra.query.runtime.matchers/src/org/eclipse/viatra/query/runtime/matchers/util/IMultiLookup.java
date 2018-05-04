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

/**
 * A multi-map that associates sets / multisets / delta sets of values to each key.
 * 
 * @author Gabor Bergmann
 * @since 2.0
 */
public interface IMultiLookup<Key, Value> {
    
    /**
     * Returns a (read-only) bucket of values associated with the given key.
     * Clients must not modify the returned bucket.
     * @param key a key for which associated values are sought
     * @return null if key not found, a bucket of values otherwise
     */
    public IMemoryView<Value> lookup(Key key);
    
    /**
     * Returns a (read-only) bucket of values associated with the given key.
     * Clients must not modify the returned bucket.
     * @param key a key for which associated values are sought (may not be of Key type)
     * @return null if key not found, a bucket of values otherwise
     */
    public IMemoryView<Value> lookupUnsafe(Object key);
    
    /**
     * @return the set of distinct keys that have values associated.
     */
    public Iterable<Key> distinctKeys();
    
    /**
     * @return the number of distinct keys that have values associated.
     */
    public int countKeys();
    
    /**
     * Iterates once over each distinct value.
     */
    public Iterable<Value> distinctValues();
    
    /**
     * How significant was the change?     * 
     * @author Gabor Bergmann
     */
    public enum ChangeGranularity {
        /**
         * First key-value pair with given key inserted, or last pair with given key deleted.
         * (In case of delta maps, also if last negative key-value pair with given key neutralized.)
         */
        KEY, 
        /**
         * First occurrence of given key-value pair inserted, or last occurrence of the pair deleted, while key still has values associated.
         * (In case of delta maps, also if last negative occurrence of key-value pair neutralized.)
         */
        VALUE, 
        /**
         * Duplicate key-value pair inserted or deleted.
         */
        DUPLICATE
    }
    
    /**
     * Adds key-value pair to the lookup structure.
     * @return the granularity of the change
     * @throws IllegalStateException if addition would cause duplication that is not permitted 
     */
    public ChangeGranularity addPair(Key key, Value value);
    /**
     * Removes key-value pair from the lookup structure.
     * @return the granularity of the change
     * @throws IllegalStateException if removing non-existing element that is not permitted 
     */
    public ChangeGranularity removePair(Key key, Value value);
    
    /**
     * Empties out the lookup structure.
     */
    public void clear();

}
