/*******************************************************************************
 * Copyright (c) 2010-2019, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.util;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.viatra.query.runtime.matchers.tuple.ITuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

/**
 * A timestamp-aware memory implementation that keeps track of mappings between tuples and their timestamps. 
 * It can efficiently answer the least timestamp associated with a given tuple, and it can also 
 * enumerate all timestamps for a given tuple known to this memory.
 * 
 * @author Tamas Szabo
 * @since 2.2
 */
public class TimestampAwareMemory<Timestamp extends Comparable<Timestamp>> implements Clearable {

    protected final Map<Tuple, TreeMap<Timestamp, Integer>> orderedTimestampMemory;
    protected final Map<Tuple, Timestamp> leastTimestampMemory;

    public TimestampAwareMemory() {
        this.orderedTimestampMemory = CollectionsFactory.createMap();
        this.leastTimestampMemory = CollectionsFactory.createMap();
    }

    public Pair<Timestamp, Timestamp> put(final Tuple tuple, final Timestamp timestamp) {
        final Timestamp oldLeast = this.leastTimestampMemory.get(tuple);
        TreeMap<Timestamp, Integer> timestampMap = this.orderedTimestampMemory.get(tuple);

        if (timestampMap == null) {
            timestampMap = CollectionsFactory.createTreeMap();
            this.orderedTimestampMemory.put(tuple, timestampMap);
        }

        final int count = timestampMap.getOrDefault(timestamp, 0) + 1;

        timestampMap.put(timestamp, count);

        final Timestamp newLeast = timestampMap.firstKey();
        this.leastTimestampMemory.put(tuple, newLeast);
        return new Pair<Timestamp, Timestamp>(oldLeast, newLeast);
    }

    public Pair<Timestamp, Timestamp> remove(final Tuple tuple, final Timestamp timestamp) {
        final Timestamp oldLeast = this.leastTimestampMemory.get(tuple);
        TreeMap<Timestamp, Integer> timestampMap = this.orderedTimestampMemory.get(tuple);

        if (timestampMap == null) {
            throw new IllegalStateException(
                    "Tuple " + tuple + " was not present in the memory when attempting removal!");
        }

        final Integer count = timestampMap.get(timestamp);

        if (count == null) {
            throw new IllegalStateException("Timestamp " + timestamp + " for tuple " + tuple
                    + " was not present in the memory when attempting removal!");
        }

        if (count == 1) {
            timestampMap.remove(timestamp);
            if (timestampMap.isEmpty()) {
                this.orderedTimestampMemory.remove(tuple);
                this.leastTimestampMemory.remove(tuple);
            } else {
                this.leastTimestampMemory.put(tuple, timestampMap.firstKey());
            }
        } else {
            timestampMap.put(timestamp, count - 1);
        }
        
        final Timestamp newLeast = this.leastTimestampMemory.get(tuple);

        return new Pair<Timestamp, Timestamp>(oldLeast, newLeast);
    }

    public boolean isEmpty() {
        return this.leastTimestampMemory.isEmpty();
    }

    public int size() {
        return this.leastTimestampMemory.size();
    }

    public Set<Tuple> keySet() {
        return this.leastTimestampMemory.keySet();
    }

    public Map<Tuple, Timestamp> asMap() {
        return this.leastTimestampMemory;
    }

    /**
     * Returns the least timestamp stored for the given tuple.
     */
    public Timestamp get(final ITuple tuple) {
        return this.leastTimestampMemory.get(tuple);
    }

    @Override
    public void clear() {
        this.orderedTimestampMemory.clear();
        this.leastTimestampMemory.clear();
    }

    public boolean containsKey(final ITuple tuple) {
        return this.leastTimestampMemory.containsKey(tuple);
    }

}
