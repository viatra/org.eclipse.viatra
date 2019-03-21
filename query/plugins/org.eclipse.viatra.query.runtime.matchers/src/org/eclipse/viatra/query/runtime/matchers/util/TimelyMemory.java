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

import org.eclipse.viatra.query.runtime.matchers.memories.TimestampReplacement;
import org.eclipse.viatra.query.runtime.matchers.tuple.ITuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

/**
 * A timely memory implementation that keeps track of mappings between tuples and their timestamps. It can
 * efficiently answer the least timestamp associated with a given tuple, and it can also enumerate all timestamps for a
 * given tuple known to this memory.
 * 
 * @author Tamas Szabo
 * @since 2.2
 */
public class TimelyMemory<Timestamp extends Comparable<Timestamp>> implements Clearable {

    protected final Map<Tuple, TreeMap<Timestamp, Integer>> orderedTimestampMemory;
    protected final Map<Tuple, Timestamp> leastTimestampMemory;

    public TimelyMemory() {
        this.orderedTimestampMemory = CollectionsFactory.createMap();
        this.leastTimestampMemory = CollectionsFactory.createMap();
    }

    public TimestampReplacement<Timestamp> put(final Tuple tuple, final Timestamp timestamp) {
        final Timestamp oldLeast = this.leastTimestampMemory.get(tuple);
        final TreeMap<Timestamp, Integer> timestampMap = this.orderedTimestampMemory.computeIfAbsent(tuple,
                key -> CollectionsFactory.createTreeMap());
        timestampMap.merge(timestamp, 1, (o, n) -> o + 1);
        Timestamp newLeast = null;
        if (oldLeast == null || timestamp.compareTo(oldLeast) < 0) {
            newLeast = timestamp;
            this.leastTimestampMemory.put(tuple, newLeast);
        } else {
            newLeast = oldLeast;
        }
        return new TimestampReplacement<Timestamp>(oldLeast, newLeast);
    }

    public TimestampReplacement<Timestamp> remove(final Tuple tuple, final Timestamp timestamp) {
        final Timestamp oldLeast = this.leastTimestampMemory.get(tuple);
        final TreeMap<Timestamp, Integer> timestampMap = this.orderedTimestampMemory.get(tuple);

        if (timestampMap == null) {
            throw new IllegalStateException(
                    "Tuple " + tuple + " was not present in the memory when attempting removal!");
        }

        final Integer count = timestampMap.get(timestamp);

        if (count == null) {
            throw new IllegalStateException("Timestamp " + timestamp + " for tuple " + tuple
                    + " was not present in the memory when attempting removal!");
        }
        
        Timestamp newLeast = null;
        if (count == 1) {
            timestampMap.remove(timestamp);
            if (timestampMap.isEmpty()) {
                this.orderedTimestampMemory.remove(tuple);
                this.leastTimestampMemory.remove(tuple);
                // newLeast = null;
            } else {
                final Timestamp firstKey = timestampMap.firstKey();
                this.leastTimestampMemory.put(tuple, firstKey);
                newLeast = firstKey;
            }
        } else {
            timestampMap.put(timestamp, count - 1);
            newLeast = oldLeast;
        }

        return new TimestampReplacement<Timestamp>(oldLeast, newLeast);
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
