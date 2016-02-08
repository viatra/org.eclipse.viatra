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

package org.eclipse.incquery.runtime.base.itc.alg.misc.scc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.incquery.runtime.base.itc.igraph.IGraphDataSource;

/**
 * Efficient algorithms to compute the Strongly Connected Components in a directed graph.
 * 
 * @author Tamas Szabo
 * 
 * @param <V>
 *            the type parameter of the nodes in the graph
 */
public class SCC<V> {

    public static long sccId = 0;

    /**
     * Computes the SCCs for the given graph and returns them as a multiset. (Iterative version of Tarjan's algorithm)
     * 
     * @param g
     *            the directed graph data source
     * @return the set of SCCs
     */
    public static <V> SCCResult<V> computeSCC(IGraphDataSource<V> g) {
        int index = 0;
        Set<Set<V>> ret = new HashSet<Set<V>>();
        
        // stores the lowlink and index information for the given node
        Map<V, SCCProperty> nodeMap = new HashMap<V, SCCProperty>();
        
        // stores all target nodes of a given node - the list will be modified
        Map<V, List<V>> targetNodeMap = new HashMap<V, List<V>>();
        
        // stores those target nodes for a given node which have not been visited 
        Map<V, Set<V>> notVisitedMap = new HashMap<V, Set<V>>();

        // stores the nodes during the traversal
        Stack<V> nodeStack = new Stack<V>();
        
        // stores the nodes which belong to an scc (there can be many sccs in the stack at the same time)
        Stack<V> sccStack = new Stack<V>();

        boolean sink = false, finishedTraversal = true;

        // initialize all nodes with 0 index and 0 lowlink
        Set<V> allNodes = g.getAllNodes();
        for (V n : allNodes) {
            nodeMap.put(n, new SCCProperty(0, 0));
        }

        for (V n : allNodes) {
            // if the node has not been visited yet
            if (nodeMap.get(n).getIndex() == 0) {
                nodeStack.push(n);

                while (!nodeStack.isEmpty()) {
                    V currentNode = nodeStack.peek();
                    sink = false;
                    finishedTraversal = false;
                    SCCProperty prop = nodeMap.get(currentNode);

                    if (nodeMap.get(currentNode).getIndex() == 0) {
                        index++;
                        sccStack.push(currentNode);
                        prop.setIndex(index);
                        prop.setLowlink(index);

                        notVisitedMap.put(currentNode, new HashSet<V>());

                        // storing the target nodes of the actual node
                        if (g.getTargetNodes(currentNode) != null) {
                            targetNodeMap.put(currentNode, new ArrayList<V>(g.getTargetNodes(currentNode)));
                        }
                    }

                    if (targetNodeMap.get(currentNode) != null) {
                        
                        // remove node from stack, the exploration of its children has finished
                        if (targetNodeMap.get(currentNode).size() == 0) {
                            targetNodeMap.remove(currentNode);

                            nodeStack.pop();

                            List<V> targets = g.getTargetNodes(currentNode);
                            if (targets != null) {
                                for (V targetNode : g.getTargetNodes(currentNode)) {
                                    if (notVisitedMap.get(currentNode).contains(targetNode)) {
                                        prop.setLowlink(Math.min(prop.getLowlink(), nodeMap.get(targetNode)
                                                .getLowlink()));
                                    } else if (sccStack.contains(targetNode)) {
                                        prop.setLowlink(Math.min(prop.getLowlink(), nodeMap.get(targetNode).getIndex()));
                                    }
                                }
                            }

                            finishedTraversal = true;
                        } else {
                            V targetNode = targetNodeMap.get(currentNode).remove(0);
                            // if the targetNode has not yet been visited push it to the stack
                            // and mark it in the notVisitedMap
                            if (nodeMap.get(targetNode).getIndex() == 0) {
                                notVisitedMap.get(currentNode).add(targetNode);
                                nodeStack.add(targetNode);
                            }
                        }
                    }
                    // if currentNode has no target nodes
                    else {
                        nodeStack.pop();
                        sink = true;
                    }

                    // create scc if node is a sink or an scc has been found
                    if ((sink || finishedTraversal) && (prop.getLowlink() == prop.getIndex())) {
                        Set<V> sc = new HashSet<V>();
                        V targetNode = null;

                        do {
                            targetNode = sccStack.pop();
                            sc.add(targetNode);
                        } while (!targetNode.equals(currentNode));

                        ret.add(sc);
                    }
                }
            }
        }

        return new SCCResult<V>(ret, g);
    }
}
