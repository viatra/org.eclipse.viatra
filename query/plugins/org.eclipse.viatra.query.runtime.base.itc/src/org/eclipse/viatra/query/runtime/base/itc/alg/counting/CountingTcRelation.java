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

package org.eclipse.viatra.query.runtime.base.itc.alg.counting;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.viatra.query.runtime.base.itc.alg.misc.ITcRelation;
import org.eclipse.viatra.query.runtime.base.itc.alg.misc.topsort.TopologicalSorting;
import org.eclipse.viatra.query.runtime.base.itc.igraph.IBiDirectionalGraphDataSource;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.matchers.util.IMultiset;

/**
 * Transitive closure relation implementation for the Counting algorithm.
 * 
 * @author Tamas Szabo
 * 
 * @param <V>
 */
public class CountingTcRelation<V> implements ITcRelation<V> {

    private Map<V, IMultiset<V>> tuplesForward = null;
    private Map<V, IMultiset<V>> tuplesBackward = null;

    protected CountingTcRelation(boolean backwardIndexing) {
        tuplesForward = CollectionsFactory.createMap();
        if (backwardIndexing)
            tuplesBackward = CollectionsFactory.createMap();
    }
    
    protected boolean isEmpty() {
        return this.tuplesForward.isEmpty();
    }

    protected void clear() {
        this.tuplesForward.clear();

        if (tuplesBackward != null) {
            this.tuplesBackward.clear();
        }
    }

    protected void union(CountingTcRelation<V> rA) {
        for (Entry<V, IMultiset<V>> entry : rA.tuplesForward.entrySet()) {
            V source = entry.getKey();
            IMultiset<V> targetBag = entry.getValue();
            for (V target : targetBag.keySet()) {
                this.addTuple(source, target, targetBag.getCount(target));
            }
        }
    }

    public int getCount(V source, V target) {
        if (tuplesForward.containsKey(source) && tuplesForward.get(source).containsNonZero(target)) {
            return tuplesForward.get(source).getCount(target);
        } else {
            return 0;
        }
    }

    /**
     * Returns true if the tc relation did not contain previously such a tuple that is defined by (source,target), false
     * otherwise (in this case count is incremented with the given count parameter).
     * 
     * @param source
     *            the source of the tuple
     * @param target
     *            the target of the tuple
     * @param count
     *            the count of the tuple, must be positive
     * @return true if the relation did not contain previously the tuple
     */
    public boolean addTuple(V source, V target, int count) {

        IMultiset<V> sMap = null;
        IMultiset<V> tMap = null;

        if (tuplesBackward != null) {
            tMap = tuplesBackward.get(target);

            if (tMap == null) {
                sMap = CollectionsFactory.<V>createMultiset();
                sMap.addPositive(source, count);
                tuplesBackward.put(target, sMap);
            } else {
                tMap.addPositive(source, count);
            }
        }

        sMap = tuplesForward.get(source);

        if (sMap == null) {
            tMap = CollectionsFactory.createMultiset();
            tMap.addPositive(target, count);
            tuplesForward.put(source, tMap);
            return true;
        } else {
            boolean newTarget = sMap.addPositive(target, count);
            return newTarget;
        }
    }
    
    /**
     * Derivation count of the tuple  (source,target) is incremented or decremented.
     * Returns true iff updated to / from zero derivation count.
     * @since 1.7
     */
    public boolean updateTuple(V source, V target, boolean isInsertion) {

        IMultiset<V> sMap = null;
        IMultiset<V> tMap = null;

        if (tuplesBackward != null) {
            tMap = tuplesBackward.get(target);

            if (tMap == null) {
                tMap = CollectionsFactory.<V>createMultiset();
                if (isInsertion) tMap.addOne(source); else /* should not happen, will throw */ tMap.removeOne(source);
                tuplesBackward.put(target, tMap);
            } else {
                if (isInsertion) {
                    tMap.addOne(source);
                } else {
                    if (tMap.removeOne(source)) {
                        if (tMap.isEmpty())
                            tuplesBackward.remove(target);
                    }
                }
            }
        }

        sMap = tuplesForward.get(source);

        if (sMap == null) {
            sMap = CollectionsFactory.createMultiset();
            if (isInsertion) sMap.addOne(target); else /* should not happen, will throw */ sMap.removeOne(target);
            tuplesForward.put(source, sMap);
            return true;
        } else {
            if (isInsertion) {
                return sMap.addOne(target);
            } else {
                boolean last = sMap.removeOne(target);
                if (last) {
                    if (sMap.isEmpty())
                        tuplesForward.remove(source);
                }
                return last;
            }
            
        }
    }

    public void deleteTupleEnd(V tupleEnd) {
        this.tuplesForward.remove(tupleEnd);

        if (tuplesForward.keySet() != null) {
            Set<V> tmp = CollectionsFactory.createSet(tuplesForward.keySet());

            for (V key : tmp) {
                IMultiset<V> pairs = this.tuplesForward.get(key);
                pairs.clearAllOf(tupleEnd);
                if (pairs.isEmpty())
                    this.tuplesForward.remove(key);
            }
        }

        if (tuplesBackward != null) {
            this.tuplesBackward.remove(tupleEnd);

            if (tuplesBackward.keySet() != null) {
                Set<V> tmp = CollectionsFactory.createSet(tuplesBackward.keySet());

                for (V key : tmp) {
                    IMultiset<V> pairs = this.tuplesBackward.get(key);
                    pairs.clearAllOf(tupleEnd);
                    if (pairs.isEmpty()) {
                        this.tuplesBackward.remove(key);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TcRelation = ");

        for (Entry<V, IMultiset<V>> outerEntry : this.tuplesForward.entrySet()) {
            V source = outerEntry.getKey();
            IMultiset<V> targets = outerEntry.getValue();
            for (V target: targets.keySet()) {
                sb.append("{(" + source + "," + target + ")," + targets.getCount(target) + "} ");
            }
        }
        return sb.toString();
    }

    @Override
    public Set<V> getTupleEnds(V source) {
        IMultiset<V> tupEnds = tuplesForward.get(source);
        if (tupEnds == null)
            return null;
        return tupEnds.keySet();
    }

    /**
     * Returns the set of nodes from which the target node is reachable.
     * 
     * @param target
     *            the target node
     * @return the set of source nodes
     */
    public Set<V> getTupleStarts(V target) {
        if (tuplesBackward != null) {
            IMultiset<V> tupStarts = tuplesBackward.get(target);
            if (tupStarts == null)
                return null;
            return tupStarts.keySet();
        } else {
            return null;
        }
    }

    @Override
    public Set<V> getTupleStarts() {
        Set<V> nodes = CollectionsFactory.createSet(tuplesForward.keySet());
        return nodes;
    }

    /**
     * Returns true if a (source, target) node is present in the transitive closure relation, false otherwise.
     * 
     * @param source
     *            the source node
     * @param target
     *            the target node
     * @return true if tuple is present, false otherwise
     */
    public boolean containsTuple(V source, V target) {
        if (tuplesForward.containsKey(source)) {
            if (tuplesForward.get(source).containsNonZero(target))
                return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        } else {
            CountingTcRelation<V> aTR = (CountingTcRelation<V>) obj;

            return tuplesForward.equals(aTR.tuplesForward);
        }
    }

    @Override
    public int hashCode() {
        return tuplesForward.hashCode();
    }

    public static <V> CountingTcRelation<V> createFrom(IBiDirectionalGraphDataSource<V> gds) {
        List<V> topologicalSorting = TopologicalSorting.compute(gds);
        CountingTcRelation<V> tc = new CountingTcRelation<V>(true);
        Collections.reverse(topologicalSorting);
        for (V n : topologicalSorting) {
            Map<V, Integer> sourceNodes = gds.getSourceNodes(n);
            Set<V> tupEnds = tc.getTupleEnds(n);
            for (Entry<V, Integer> entry : sourceNodes.entrySet()) {
                for (int i = 0; i < entry.getValue(); i++) {
                    V s = entry.getKey();
                    tc.updateTuple(s, n, true);
                    if (tupEnds != null) {
                        for (V t : tupEnds) {
                            tc.updateTuple(s, t, true);
                        }
                    }
                }
            }
        }

        return tc;
    }
}
