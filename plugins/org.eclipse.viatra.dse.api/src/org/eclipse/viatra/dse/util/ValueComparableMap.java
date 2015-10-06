/*******************************************************************************
 * Copyright (c) 2010-2015, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.base.Functions;
import com.google.common.collect.Ordering;

/**
 * This class is originated from this source with minor modifications:
 * http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java/3420912#3420912
 * 
 * @author Andras Szabolcs Nagy
 *
 * @param <K>
 *            key
 * @param <V>
 *            value
 */
public class ValueComparableMap<K, V> extends TreeMap<K, V> {

    // A map for doing lookups on the keys for comparison so we don't get infinite loops
    private final Map<K, V> valueMap;

    public ValueComparableMap(final Ordering<? super V> partialValueOrdering, Comparator<K> c) {
        this(partialValueOrdering, c, new HashMap<K, V>());
    }

    private ValueComparableMap(Ordering<? super V> partialValueOrdering, Comparator<K> secondaryKeyComparator,
            HashMap<K, V> valueMap) {
        super(partialValueOrdering // Apply the value ordering
                .onResultOf(Functions.forMap(valueMap)) // On the result of getting the value for the key from the map
                .compound(secondaryKeyComparator)); // as well as ensuring that the keys don't get clobbered
        this.valueMap = valueMap;
    }

    public V put(K k, V v) {
        if (valueMap.containsKey(k)) {
            // remove the key in the sorted set before adding the key again
            remove(k);
        }
        valueMap.put(k, v); // To get "real" unsorted values for the comparator
        return super.put(k, v); // Put it in value order
    }
}
