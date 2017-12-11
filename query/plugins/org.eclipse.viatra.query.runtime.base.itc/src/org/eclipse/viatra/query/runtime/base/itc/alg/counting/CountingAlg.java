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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.viatra.query.runtime.base.itc.alg.misc.DFSPathFinder;
import org.eclipse.viatra.query.runtime.base.itc.alg.misc.IGraphPathFinder;
import org.eclipse.viatra.query.runtime.base.itc.alg.misc.ITcRelation;
import org.eclipse.viatra.query.runtime.base.itc.igraph.IBiDirectionalGraphDataSource;
import org.eclipse.viatra.query.runtime.base.itc.igraph.IBiDirectionalWrapper;
import org.eclipse.viatra.query.runtime.base.itc.igraph.IGraphDataSource;
import org.eclipse.viatra.query.runtime.base.itc.igraph.IGraphObserver;
import org.eclipse.viatra.query.runtime.base.itc.igraph.ITcDataSource;
import org.eclipse.viatra.query.runtime.base.itc.igraph.ITcObserver;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;

/**
 * This class is the optimized implementation of the Counting algorithm.
 * 
 * @author Tamas Szabo
 * 
 * @param <V>
 *            the type parameter of the nodes in the graph data source
 */
public class CountingAlg<V> implements IGraphObserver<V>, ITcDataSource<V> {

    private CountingTcRelation<V> tc = null;
    private IBiDirectionalGraphDataSource<V> gds = null;
    private List<ITcObserver<V>> observers;

    /**
     * Constructs a new Counting algorithm and initializes the transitive closure relation with the given graph data
     * source. Attach itself on the graph data source as an observer.
     * 
     * @param gds
     *            the graph data source instance
     */
    public CountingAlg(IGraphDataSource<V> gds) {

        if (gds instanceof IBiDirectionalGraphDataSource<?>) {
            this.gds = (IBiDirectionalGraphDataSource<V>) gds;
        } else {
            this.gds = new IBiDirectionalWrapper<V>(gds);
        }

        observers = CollectionsFactory.<ITcObserver<V>>createObserverList();
        tc = new CountingTcRelation<V>(true);

        initTc();
        gds.attachObserver(this);
    }

    /**
     * Initializes the transitive closure relation.
     */
    private void initTc() {
        this.setTcRelation(CountingTcRelation.createFrom(gds));
    }

    @Override
    public void edgeInserted(V source, V target) {
        if (!source.equals(target)) {
            deriveTc(source, target, true);
        }
    }

    @Override
    public void edgeDeleted(V source, V target) {
        if (!source.equals(target)) {
            deriveTc(source, target, false);
        }
    }

    @Override
    public void nodeInserted(V n) {

    }

    @Override
    public void nodeDeleted(V n) {
        this.tc.deleteTupleEnd(n);
    }

    /**
     * Derives the transitive closure relation when an edge is inserted or deleted.
     * 
     * @param source
     *            the source of the edge
     * @param target
     *            the target of the edge
     * @param dCount
     *            the value is -1 if an edge was deleted and +1 if an edge was inserted
     */
    private void deriveTc(V source, V target, boolean isInsertion) {

        // if (dCount == 1 && isReachable(target, source)) {
        // System.out.println("The graph contains cycle with (" + source + ","+ target + ") edge!");
        // }

        CountingTcRelation<V> dtc = new CountingTcRelation<V>(false);
        Set<V> tupEnds = null;

        // 1. d(tc(x,y)) :- d(l(x,y))
        if (tc.updateTuple(source, target, isInsertion)) {
            dtc.updateTuple(source, target, true /* deltas implicitly have the same sign as isInsertion*/);
            notifyTcObservers(source, target, isInsertion);
        }

        // 2. d(tc(x,y)) :- d(l(x,z)) & tc(z,y)
        tupEnds = tc.getTupleEnds(target);
        if (tupEnds != null) {
            for (V tupEnd : tupEnds) {
                if (!tupEnd.equals(source)) {
                    if (tc.updateTuple(source, tupEnd, isInsertion)) {
                        dtc.updateTuple(source, tupEnd, true /* deltas implicitly have the same sign as isInsertion*/);
                        notifyTcObservers(source, tupEnd, isInsertion);
                    }
                }
            }
        }

        // 3. d(tc(x,y)) :- lv(x,z) & d(tc(z,y))
        CountingTcRelation<V> newTuples = dtc;
        CountingTcRelation<V> tmp = null;
        dtc = new CountingTcRelation<V>(false);
        
        Map<V, Integer> nodes = null;

        while (!newTuples.isEmpty()) {

            tmp = dtc;
            dtc = newTuples;
            newTuples = tmp;
            newTuples.clear();

            for (V tS : dtc.getTupleStarts()) {
                nodes = gds.getSourceNodes(tS);
                for (Entry<V, Integer> entry : nodes.entrySet()) {
                    for (int i = 0; i < entry.getValue(); i++) {
                        V nS = entry.getKey();
                        tupEnds = dtc.getTupleEnds(tS);
                        if (tupEnds != null) {
                            for (V tT : tupEnds) {
                                if (!nS.equals(tT)) {
                                    if (tc.updateTuple(nS, tT, isInsertion)) {
                                        newTuples.updateTuple(nS, tT, true /* deltas implicitly have the same sign as isInsertion*/);
                                        notifyTcObservers(nS, tT, isInsertion);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // System.out.println(tc);
    }

    public ITcRelation<V> getTcRelation() {
        return this.tc;
    }

    public void setTcRelation(CountingTcRelation<V> tc) {
        this.tc = tc;
    }

    @Override
    public boolean isReachable(V source, V target) {
        return tc.containsTuple(source, target);
    }

    @Override
    public void attachObserver(ITcObserver<V> to) {
        this.observers.add(to);

    }

    @Override
    public void detachObserver(ITcObserver<V> to) {
        this.observers.remove(to);
    }

    @Override
    public Set<V> getAllReachableTargets(V source) {
        return tc.getTupleEnds(source);
    }

    @Override
    public Set<V> getAllReachableSources(V target) {
        return tc.getTupleStarts(target);
    }

    private void notifyTcObservers(V source, V target, boolean isInsertion) {
        if (isInsertion) {
            for (ITcObserver<V> o : observers) {
                o.tupleInserted(source, target);
            }
        } else {
            for (ITcObserver<V> o : observers) {
                o.tupleDeleted(source, target);
            }
        }
    }

    @Override
    public void dispose() {
        tc.clear();
        this.gds.detachObserver(this);
    }

    @Override
    public IGraphPathFinder<V> getPathFinder() {
        return new DFSPathFinder<V>(gds, this);
    }
}