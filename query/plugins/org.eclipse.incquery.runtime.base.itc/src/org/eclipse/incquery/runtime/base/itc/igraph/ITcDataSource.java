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

package org.eclipse.incquery.runtime.base.itc.igraph;

import java.util.List;
import java.util.Set;

import org.eclipse.incquery.runtime.base.itc.alg.misc.IGraphPathFinder;

/**
 * This interface defines those methods that a transitive reachability data source should provide. 
 * 
 * @author Tamas Szabo
 * 
 * @param <V>
 *            the type parameter of the node
 */
public interface ITcDataSource<V> {

    /**
     * Attach a transitive closure relation observer.
     * 
     * @param to
     *            the observer object
     */
    public void attachObserver(ITcObserver<V> to);

    /**
     * Detach a transitive closure relation observer.
     * 
     * @param to
     *            the observer object
     */
    public void detachObserver(ITcObserver<V> to);

    /**
     * Returns all nodes which are reachable from the source node.
     * 
     * @param source
     *            the source node
     * @return the set of target nodes
     */
    public Set<V> getAllReachableTargets(V source);

    /**
     * Returns all nodes from which the target node is reachable.
     * 
     * @param target
     *            the target node
     * @return the set of source nodes
     */
    public Set<V> getAllReachableSources(V target);

    /**
     * Returns true if the target node is reachable from the source node.
     * 
     * @param source
     *            the source node
     * @param target
     *            the target node
     * @return true if target is reachable from source, false otherwise
     */
    public boolean isReachable(V source, V target);
    
    /**
     * Returns a reachability path between the given source and target elements, or null if no such transitive reachability is present in the graph.  
     * The returned {@link List} contains the nodes along the path 
     * (this means that there is an edge in the graph between two consecutive nodes), including the source and target nodes.
     * A self loop (one edge) is indicated with the source node being present two times in the returned {@link List}.
     * <p/>
     * Note that the paths are not maintained incrementally and in worst case the complexity of the path construction is O(|V|+|E|) 
     * (one depth-first graph traversal). There is no guarantee that the given path is the shortest one between the given two nodes. 
     * 
     * @param source the source node
     * @param target the target node
     * @return a path between the nodes, or null if target is not reachable from source
     * @deprecated Use {@link #getPathFinder()} instead
     */
    @Deprecated
    public List<V> getReachabilityPath(V source, V target);    
    
    /**
     * The returned {@link IGraphPathFinder} can be used to retrieve paths between nodes using transitive reachability.
     * 
     * @return a path finder for the graph.
     */
    public IGraphPathFinder<V> getPathFinder();
    
    /**
     * Call this method to properly dispose the data structures of a transitive closure algorithm.
     */
    public void dispose();
}
