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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.viatra.query.runtime.base.itc.alg.counting.CountingAlg;
import org.eclipse.viatra.query.runtime.base.itc.alg.dred.DRedTcRelation;
import org.eclipse.viatra.query.runtime.base.itc.alg.misc.DFSPathFinder;
import org.eclipse.viatra.query.runtime.base.itc.alg.misc.GraphHelper;
import org.eclipse.viatra.query.runtime.base.itc.alg.misc.IGraphPathFinder;
import org.eclipse.viatra.query.runtime.base.itc.alg.misc.Tuple;
import org.eclipse.viatra.query.runtime.base.itc.alg.misc.bfs.BFS;
import org.eclipse.viatra.query.runtime.base.itc.alg.misc.scc.SCC;
import org.eclipse.viatra.query.runtime.base.itc.alg.misc.scc.SCCResult;
import org.eclipse.viatra.query.runtime.base.itc.graphimpl.Graph;
import org.eclipse.viatra.query.runtime.base.itc.igraph.IBiDirectionalGraphDataSource;
import org.eclipse.viatra.query.runtime.base.itc.igraph.IBiDirectionalWrapper;
import org.eclipse.viatra.query.runtime.base.itc.igraph.IGraphDataSource;
import org.eclipse.viatra.query.runtime.base.itc.igraph.IGraphObserver;
import org.eclipse.viatra.query.runtime.base.itc.igraph.ITcDataSource;
import org.eclipse.viatra.query.runtime.base.itc.igraph.ITcObserver;

/**
 * Incremental SCC maintenance + counting algorithm.
 * 
 * @author Tamas Szabo
 * 
 * @param <V>
 *            the type parameter of the nodes in the graph data source
 */
public class IncSCCAlg<V> implements IGraphObserver<V>, ITcDataSource<V> {

    private static final long serialVersionUID = 6207002106223444807L;

    public UnionFind<V> sccs;
    public IBiDirectionalGraphDataSource<V> gds;
    private CountingAlg<V> counting;
    private Graph<V> reducedGraph;
    private IBiDirectionalGraphDataSource<V> reducedGraphIndexer;
    private List<ITcObserver<V>> observers;
    private CountingListener<V> countingListener;

    public IncSCCAlg(IGraphDataSource<V> graphDataSource) {

        if (graphDataSource instanceof IBiDirectionalGraphDataSource<?>) {
            gds = (IBiDirectionalGraphDataSource<V>) graphDataSource;
        } else {
            gds = new IBiDirectionalWrapper<V>(graphDataSource);
        }
        observers = new ArrayList<ITcObserver<V>>();
        sccs = new UnionFind<V>();
        reducedGraph = new Graph<V>();
        reducedGraphIndexer = new IBiDirectionalWrapper<V>(reducedGraph);
        countingListener = new CountingListener<V>(this);
        initalizeInternalDataStructures();
        gds.attachObserver(this);
    }

    private void initalizeInternalDataStructures() {
        SCCResult<V> _sccres = SCC.computeSCC(gds);
        Set<Set<V>> _sccs = _sccres.getSccs();

        for (Set<V> _set : _sccs) {
            sccs.makeSet(_set);
        }

        // Initalization of the reduced graph
        for (V n : sccs.setMap.keySet()) {
            reducedGraph.insertNode(n);
        }

        for (V source : gds.getAllNodes()) {
            final List<V> targetNodes = gds.getTargetNodes(source);
            if (targetNodes != null)
                for (V target : targetNodes) {
                    V sourceRoot = sccs.find(source);
                    V targetRoot = sccs.find(target);

                    if (!sourceRoot.equals(targetRoot)) {
                        reducedGraph.insertEdge(sourceRoot, targetRoot);
                    }
                }
        }

        counting = new CountingAlg<V>(reducedGraph);
    }

    @Override
    public void edgeInserted(V source, V target) {
        V sourceRoot = sccs.find(source);
        V targetRoot = sccs.find(target);

        // Different SCC
        if (!sourceRoot.equals(targetRoot)) {

            // source is reachable from target?
            if (counting.isReachable(targetRoot, sourceRoot)) {

                Set<V> predecessorRoots = counting.getAllReachableSources(sourceRoot);
                Set<V> successorRoots = counting.getAllReachableTargets(targetRoot);

                // 1. intersection of source and target roots, these will be in the merged SCC
                Set<V> isectRoots = CollectionHelper.intersection(predecessorRoots, successorRoots);
                isectRoots.add(sourceRoot);
                isectRoots.add(targetRoot);

                // notifications must be issued before Union-Find modifications
                if (observers.size() > 0) {
                    Set<V> sourceSCCs = new HashSet<V>();
                    Set<V> targetSCCs = new HashSet<V>();

                    sourceSCCs.add(sourceRoot);
                    sourceSCCs.addAll(predecessorRoots);
                    targetSCCs.add(targetRoot);
                    targetSCCs.addAll(successorRoots);

                    // tracing back to actual nodes
                    for (V sourceSCC : sourceSCCs) {
                        for (V targetSCC : CollectionHelper.difference(targetSCCs,
                                counting.getAllReachableTargets(sourceSCC))) {
                            boolean needsNotification = false;

                            //Case 1. sourceSCC and targetSCC are the same and it is a one sized scc. 
                            //Issue notifications only if there is no self-loop present at the moment
                            if (sourceSCC.equals(targetSCC) && sccs.setMap.get(sourceSCC).size() == 1
                                    && GraphHelper.getEdgeCount(sccs.setMap.get(sourceSCC).iterator().next(), gds) == 0) {
                                needsNotification = true;
                            } 
                            //Case 2. sourceSCC and targetSCC are different sccs. 
                            else if (!sourceSCC.equals(targetSCC)) {
                                needsNotification = true;
                            }
                            // if self loop is already present omit the notification
                            if (needsNotification) {
                                notifyTcObservers(sccs.setMap.get(sourceSCC), sccs.setMap.get(targetSCC),
                                        Direction.INSERT);
                            }
                        }
                    }
                }

                // 2. delete edges, nodes
                List<V> sourceSCCs = new ArrayList<V>();
                List<V> targetSCCs = new ArrayList<V>();

                for (V r : isectRoots) {
                    List<V> sourceSCCsOfSCC = getSourceSCCsOfSCC(r);
                    List<V> targetSCCsOfSCC = getTargetSCCsOfSCC(r);

                    for (V sourceSCC : sourceSCCsOfSCC) {
                        if (!sourceSCC.equals(r)) {
                            reducedGraph.deleteEdge(sourceSCC, r);
                        }
                    }

                    for (V targetSCC : targetSCCsOfSCC) {
                        if (!isectRoots.contains(targetSCC) && !r.equals(targetSCC)) {
                            reducedGraph.deleteEdge(r, targetSCC);
                        }
                    }

                    sourceSCCs.addAll(sourceSCCsOfSCC);
                    targetSCCs.addAll(targetSCCsOfSCC);
                }

                for (V r : isectRoots) {
                    reducedGraph.deleteNode(r);
                }

                // 3. union
                Iterator<V> iterator = isectRoots.iterator();
                V newRoot = iterator.next();
                while (iterator.hasNext()) {
                    newRoot = sccs.union(newRoot, iterator.next());
                }

                // 4. add new node
                reducedGraph.insertNode(newRoot);

                // 5. add edges
                Set<V> containedNodes = sccs.setMap.get(newRoot);

                for (V sourceSCC : sourceSCCs) {
                    if (!containedNodes.contains(sourceSCC) && !sourceSCC.equals(newRoot)) {
                        reducedGraph.insertEdge(sourceSCC, newRoot);
                    }
                }
                for (V targetSCC : targetSCCs) {
                    if (!containedNodes.contains(targetSCC) && !targetSCC.equals(newRoot)) {
                        reducedGraph.insertEdge(newRoot, targetSCC);
                    }
                }
            } else {
                if (observers.size() > 0 && GraphHelper.getEdgeCount(source, target, gds) == 1) {
                    counting.attachObserver(countingListener);
                }
                reducedGraph.insertEdge(sourceRoot, targetRoot);
                counting.detachObserver(countingListener);
            }
        } else {
            // Notifications about self-loops
            if (observers.size() > 0 && sccs.setMap.get(sourceRoot).size() == 1
                    && GraphHelper.getEdgeCount(source, target, gds) == 1) {
                notifyTcObservers(source, source, Direction.INSERT);
            }
        }
    }

    @Override
    public void edgeDeleted(V source, V target) {
        V sourceRoot = sccs.find(source);
        V targetRoot = sccs.find(target);

        if (!sourceRoot.equals(targetRoot)) {
            if (observers.size() > 0 && GraphHelper.getEdgeCount(source, target, gds) == 0) {
                counting.attachObserver(countingListener);
            }
            reducedGraph.deleteEdge(sourceRoot, targetRoot);
            counting.detachObserver(countingListener);
        } else {
            // get the graph for the scc whose root is sourceRoot
            Graph<V> g = GraphHelper.getSubGraph(sccs.setMap.get(sourceRoot), gds);

            // if source is not reachable from target anymore
            if (!BFS.isReachable(source, target, g)) {
                List<V> reachableSources = (reducedGraphIndexer.getSourceNodes(sourceRoot) == null) ? 
                		new ArrayList<V>() : new ArrayList<V>(reducedGraphIndexer.getSourceNodes(sourceRoot));
                List<V> reachableTargets = (reducedGraphIndexer.getTargetNodes(sourceRoot) == null) ? 
                		new ArrayList<V>() : new ArrayList<V>(reducedGraphIndexer.getTargetNodes(sourceRoot));

                SCCResult<V> _newSccs = SCC.computeSCC(g);

                // delete scc node (and with its edges too)
				for (V s : reachableSources) {
					reducedGraph.deleteEdge(s, sourceRoot);
				}

				for (V t : reachableTargets) {
					reducedGraph.deleteEdge(sourceRoot, t);
				}

                sccs.deleteSet(sourceRoot);
                reducedGraph.deleteNode(sourceRoot);

                Set<Set<V>> newSCCs = _newSccs.getSccs();
                Set<V> newSCCRoots = new HashSet<V>();

                // add new nodes and edges to the reduced graph
                for (Set<V> newSCC : newSCCs) {
                    V newRoot = sccs.makeSet(newSCC);
                    reducedGraph.insertNode(newRoot);
                    newSCCRoots.add(newRoot);
                }
                for (V newSCCRoot : newSCCRoots) {
                    List<V> sourceSCCsOfSCC = getSourceSCCsOfSCC(newSCCRoot);
                    List<V> targetSCCsOfSCC = getTargetSCCsOfSCC(newSCCRoot);

                    for (V sourceSCC : sourceSCCsOfSCC) {
                        if (!sourceSCC.equals(newSCCRoot)) {
                            reducedGraph.insertEdge(sccs.find(sourceSCC), newSCCRoot);
                        }
                    }
                    for (V targetSCC : targetSCCsOfSCC) {
                        if (!newSCCRoots.contains(targetSCC) && !targetSCC.equals(newSCCRoot))
                            reducedGraph.insertEdge(newSCCRoot, targetSCC);
                    }
                }

                // Must be after the union-find modifications
                if (observers.size() > 0) {
                    V newSourceRoot = sccs.find(source);
                    V newTargetRoot = sccs.find(target);

                    Set<V> sourceSCCs = counting.getAllReachableSources(newSourceRoot);
                    sourceSCCs.add(newSourceRoot);

                    Set<V> targetSCCs = counting.getAllReachableTargets(newTargetRoot);
                    targetSCCs.add(newTargetRoot);

                    for (V sourceSCC : sourceSCCs) {
                        for (V targetSCC : CollectionHelper.difference(targetSCCs,
                                counting.getAllReachableTargets(sourceSCC))) {
                            boolean needsNotification = false;

                            //Case 1. sourceSCC and targetSCC are the same and it is a one sized scc. 
                            //Issue notifications only if there is no self-loop present at the moment
                            if (sourceSCC.equals(targetSCC) && sccs.setMap.get(sourceSCC).size() == 1
                                    && GraphHelper.getEdgeCount(sccs.setMap.get(sourceSCC).iterator().next(), gds) == 0) {
                                needsNotification = true;
                            } 
                            //Case 2. sourceSCC and targetSCC are different sccs. 
                            else if (!sourceSCC.equals(targetSCC)) {
                                needsNotification = true;
                            }
                            // if self loop is already present omit the notification
                            if (needsNotification) {
                                notifyTcObservers(sccs.setMap.get(sourceSCC), sccs.setMap.get(targetSCC),
                                        Direction.DELETE);
                            }
                        }
                    }
                }
            } else {
                // only handle self-loop notifications - sourceRoot equals to targetRoot
                if (observers.size() > 0 && sccs.setMap.get(sourceRoot).size() == 1
                        && GraphHelper.getEdgeCount(source, target, gds) == 0) {
                    notifyTcObservers(source, source, Direction.DELETE);
                }
            }
        }
    }

    @Override
    public void nodeInserted(V n) {
        sccs.makeSet(n);
        reducedGraph.insertNode(n);
    }

    @Override
    public void nodeDeleted(V n) {
        List<V> sources = gds.getSourceNodes(n);
        List<V> targets = gds.getTargetNodes(n);

        if (sources != null) {
            for (V source : sources) {
                edgeDeleted(source, n);
            }
        }

        if (targets != null) {
            for (V target : gds.getTargetNodes(n)) {
                edgeDeleted(n, target);
            }
        }

        sccs.deleteSet(n);
    }

    @Override
    public void attachObserver(ITcObserver<V> to) {
        observers.add(to);
    }

    @Override
    public void detachObserver(ITcObserver<V> to) {
        observers.remove(to);
    }

    @Override
    public Set<V> getAllReachableTargets(V source) {
        V sourceRoot = sccs.find(source);
        Set<V> containedNodes = sccs.setMap.get(sourceRoot);
        Set<V> targets = new HashSet<V>();

        if (containedNodes.size() > 1 || GraphHelper.getEdgeCount(source, gds) == 1) {
            targets.addAll(containedNodes);
        }

        Set<V> rootSet = counting.getAllReachableTargets(sourceRoot);
        if (rootSet != null) {
            for (V _root : rootSet) {
                targets.addAll(sccs.setMap.get(_root));
            }
        }

        return targets;
    }

    @Override
    public Set<V> getAllReachableSources(V target) {
        V targetRoot = sccs.find(target);
        Set<V> containedNodes = sccs.setMap.get(targetRoot);
        Set<V> sources = new HashSet<V>();

        if (containedNodes.size() > 1 || GraphHelper.getEdgeCount(target, gds) == 1) {
            sources.addAll(containedNodes);
        }

        Set<V> rootSet = counting.getAllReachableSources(targetRoot);
        if (rootSet != null) {
            for (V _root : rootSet) {
                sources.addAll(sccs.setMap.get(_root));
            }
        }
        return sources;
    }

    @Override
    public boolean isReachable(V source, V target) {
        V sourceRoot = sccs.find(source);
        V targetRoot = sccs.find(target);

        if (sourceRoot.equals(targetRoot))
            return true;
        else
            return counting.isReachable(sourceRoot, targetRoot);
    }
    
    @Override
    public List<V> getReachabilityPath(V source, V target) {
    	if (!isReachable(source, target)) {
    		return null;
    	}
    	else {
    		Set<V> sccsInSubGraph = CollectionHelper.intersection(counting.getAllReachableTargets(source), counting.getAllReachableSources(target));
    		sccsInSubGraph.add(sccs.find(source));
    		sccsInSubGraph.add(sccs.find(target));
    		Set<V> nodesInSubGraph = new HashSet<V>();
    		
    		for (V sccRoot : sccsInSubGraph) {
    			nodesInSubGraph.addAll(sccs.setMap.get(sccRoot));
    		}
 
    		return GraphHelper.constructPath(source, target, nodesInSubGraph, gds);
    	}
    }

    // for JUnit
    public boolean checkTcRelation(DRedTcRelation<V> tc) {

        for (V s : tc.getTupleStarts()) {
            for (V t : tc.getTupleEnds(s)) {
                if (!isReachable(s, t))
                    return false;
            }
        }

        for (V root : counting.getTcRelation().getTupleStarts()) {
            for (V end : counting.getTcRelation().getTupleEnds(root)) {
                for (V s : sccs.setMap.get(root)) {
                    for (V t : sccs.setMap.get(end)) {
                        if (!tc.containsTuple(s, t))
                            return false;
                    }
                }
            }
        }

        return true;
    }
    
    /**
     * Return the SCCs from which the SCC represented by the root node is reachable. Note that an SCC can be present
     * multiple times in the returned list (multiple edges between the two SCCs).
     * 
     * @param root
     * @return the list of reachable target SCCs
     */
    private List<V> getSourceSCCsOfSCC(V root) {
        List<V> sourceSCCs = new ArrayList<V>();

        for (V containedNode : this.sccs.setMap.get(root)) {
            List<V> sourceNodes = this.gds.getSourceNodes(containedNode);

            if (sourceNodes != null) {
                for (V source : sourceNodes) {
                    sourceSCCs.add(this.sccs.find(source));
                }
            }
        }

        return sourceSCCs;
    }

    /**
     * Return the SCCs which are reachable from the SCC represented by the root node. Note that an SCC can be present
     * multiple times in the returned list (multiple edges between the two SCCs).
     * 
     * @param root
     * @return the list of reachable target SCCs
     */
    private List<V> getTargetSCCsOfSCC(V root) {

        List<V> targetSCCs = new ArrayList<V>();

        for (V containedNode : this.sccs.setMap.get(root)) {
            List<V> targetNodes = this.gds.getTargetNodes(containedNode);

            if (targetNodes != null) {
                for (V target : targetNodes) {
                    targetSCCs.add(this.sccs.find(target));
                }
            }
        }

        return targetSCCs;
    }

    @Override
    public void dispose() {
        gds.detachObserver(this);
        counting.dispose();
    }

    /**
     * Call this method to notify the observers of the transitive closure relation. The tuples used in the notification
     * will be the Descartes product of the two sets given.
     * 
     * @param sources
     *            the source nodes
     * @param targets
     *            the target nodes
     * @param direction
     */
    protected void notifyTcObservers(Set<V> sources, Set<V> targets, Direction direction) {
        for (V s : sources) {
            for (V t : targets) {
                notifyTcObservers(s, t, direction);
            }
        }
    }

    private void notifyTcObservers(V source, V target, Direction direction) {
        for (ITcObserver<V> observer : observers) {
            if (direction == Direction.INSERT) {
                observer.tupleInserted(source, target);
            }
            if (direction == Direction.DELETE) {
                observer.tupleDeleted(source, target);
            }
        }
    }

    public Set<Tuple<V>> getTcRelation() {
        Set<Tuple<V>> resultSet = new HashSet<Tuple<V>>();

        for (V sourceRoot : sccs.setMap.keySet()) {
            Set<V> sources = sccs.setMap.get(sourceRoot);
            if (sources.size() > 1 || GraphHelper.getEdgeCount(sources.iterator().next(), gds) == 1) {
                for (V source : sources) {
                    for (V target : sources) {
                        resultSet.add(new Tuple<V>(source, target));
                    }
                }
            }

            Set<V> reachableTargets = counting.getAllReachableTargets(sourceRoot);
            if (reachableTargets != null) {
                for (V targetRoot : reachableTargets) {
                    for (V source : sources) {
                        for (V target : sccs.setMap.get(targetRoot)) {
                            resultSet.add(new Tuple<V>(source, target));
                        }
                    }
                }
            }
        }

        return resultSet;
    }

    public boolean isIsolated(V node) {
        List<V> targets = gds.getTargetNodes(node);
        List<V> sources = gds.getSourceNodes(node);
        return ((targets == null) || (targets.isEmpty())) && ((sources == null) || (sources.isEmpty()));
    }

    @Override
    public IGraphPathFinder<V> getPathFinder() {
        return new DFSPathFinder<V>(gds, this);
    }
    
}
