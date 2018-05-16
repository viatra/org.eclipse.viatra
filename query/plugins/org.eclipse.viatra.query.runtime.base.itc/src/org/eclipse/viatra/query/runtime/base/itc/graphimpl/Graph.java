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

package org.eclipse.viatra.query.runtime.base.itc.graphimpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.eclipse.viatra.query.runtime.base.itc.alg.misc.scc.SCC;
import org.eclipse.viatra.query.runtime.base.itc.alg.misc.scc.SCCResult;
import org.eclipse.viatra.query.runtime.base.itc.igraph.IBiDirectionalGraphDataSource;
import org.eclipse.viatra.query.runtime.base.itc.igraph.IGraphDataSource;
import org.eclipse.viatra.query.runtime.base.itc.igraph.IGraphObserver;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory.MemoryType;
import org.eclipse.viatra.query.runtime.matchers.util.IMemoryView;
import org.eclipse.viatra.query.runtime.matchers.util.IMultiLookup;

public class Graph<V> implements IGraphDataSource<V>, IBiDirectionalGraphDataSource<V> {

    // source -> target -> count
    private IMultiLookup<V, V> outgoingEdges;
    // target -> source -> count
    private IMultiLookup<V, V> incomingEdges;
    
    private Set<V> nodes;
    
    private List<IGraphObserver<V>> observers;

    public Graph() {
        outgoingEdges = CollectionsFactory.createMultiLookup(Object.class, MemoryType.MULTISETS, Object.class);
        incomingEdges = CollectionsFactory.createMultiLookup(Object.class, MemoryType.MULTISETS, Object.class);
        nodes = CollectionsFactory.createSet();
        observers = CollectionsFactory.createObserverList();
    }

    public void insertEdge(V source, V target) {
        outgoingEdges.addPair(source, target);
        incomingEdges.addPair(target, source);
        
        for (IGraphObserver<V> go : observers) {
            go.edgeInserted(source, target);
        }
    }

    /**
     * No-op if trying to delete edge that does not exist
     * @since 2.0
     * @see #deleteEdgeIfExists(Object, Object)
     */
    public void deleteEdgeIfExists(V source, V target) {
        boolean containedEdge = outgoingEdges.lookupOrEmpty(source).containsNonZero(target);
        if (containedEdge) {
            deleteEdgeThatExists(source, target);
        }
    }
    
    /**
     * @throws IllegalStateException if trying to delete edge that does not exist
     * @since 2.0
     * @see #deleteEdgeIfExists(Object, Object)
     */
    public void deleteEdgeThatExists(V source, V target) {
        outgoingEdges.removePair(source, target);
        incomingEdges.removePair(target, source);
        for (IGraphObserver<V> go : observers) {
            go.edgeDeleted(source, target);
        }
    }
    
    /**
     * @deprecated use explicitly {@link #deleteEdgeThatExists(Object, Object)} 
     * or {@link #deleteEdgeIfExists(Object, Object)} instead. 
     * To preserve backwards compatibility, this method delegates to the latter.
     * 
     */
    @Deprecated
    public void deleteEdge(V source, V target) {
        deleteEdgeIfExists(source, target);
    }

    
    

    public void insertNode(V node) {
        if (nodes.add(node)) {
            for (IGraphObserver<V> go : observers) {
                go.nodeInserted(node);
            }            
        }
    }

    public void deleteNode(V node) {
        if (nodes.remove(node)) {
            for (IGraphObserver<V> go : observers) {
                go.nodeDeleted(node);
            }
        }
    }

    @Override
    public void attachObserver(IGraphObserver<V> go) {
        observers.add(go);
    }

    @Override
    public void attachAsFirstObserver(IGraphObserver<V> observer) {
        observers.add(0, observer);
    }

    @Override
    public void detachObserver(IGraphObserver<V> go) {
        observers.remove(go);
    }

    @Override
    public Set<V> getAllNodes() {
        return nodes;
    }

    @Override
    public IMemoryView<V> getTargetNodes(V source) {
        return outgoingEdges.lookupOrEmpty(source);
    }

    @Override
    public IMemoryView<V> getSourceNodes(V target) {
        return incomingEdges.lookupOrEmpty(target);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("nodes = ");
        for (V n : getAllNodes()) {
            sb.append(n.toString());
            sb.append(" ");
        }
        sb.append(" edges = ");
        for (V source: outgoingEdges.distinctKeys()) {
            IMemoryView<V> targets = outgoingEdges.lookup(source);
            for (V target : targets.distinctValues()) {
                int count = targets.getCount(target);
                for (int i = 0; i < count; i++) {
                    sb.append("(" + source + "," + target + ") ");
                }
            }
        }
        return sb.toString();
    }

    private static final String[] colors = new String[] { "yellow", "blue", "red", "green", "gray", "cyan" };

    /**
     * @since 2.0
     */
    public String generateDot(boolean colorSCCs, Function<V, String> nameMapper, Function<V, String> colorMapper) {
        Map<V, String> colorMap = new HashMap<V, String>();

        if (colorSCCs) {
            SCCResult<V> result = SCC.computeSCC(this);
            Set<Set<V>> sccs = result.getSccs();

            int i = 0;
            for (Set<V> scc : sccs) {
                if (scc.size() > 1) {
                    for (V node : scc) {
                        String color = colorMap.get(node);
                        if (color == null) {
                            colorMap.put(node, colors[i % colors.length]);
                        } else {
                            colorMap.put(node, colorMap.get(node) + ":" + colors[i % colors.length]);
                        }
                    }
                    i++;
                }
            }

            // if a node has no color yet, then make it white
            for (V node : getAllNodes()) {
                if (!colorMap.containsKey(node)) {
                    colorMap.put(node, "white");
                }
            }
        } else {
            for (V node : getAllNodes()) {
                colorMap.put(node, "white");
            }
        }

        if (colorMapper != null) {
            for (V node : getAllNodes()) {
                colorMap.put(node, colorMapper.apply(node));
            }
        }

        StringBuilder builder = new StringBuilder();
        builder.append("digraph g {\n");

        for (V node : getAllNodes()) {
            String nodePresentation = nameMapper == null ? node.toString() : nameMapper.apply(node);
            builder.append("\"" + nodePresentation + "\"");
            builder.append("[style=filled,fillcolor=" + colorMap.get(node) + "]");
            builder.append(";\n");
        }

        for (V source: outgoingEdges.distinctKeys()) {
            IMemoryView<V> targets = outgoingEdges.lookup(source);
            String sourcePresentation = nameMapper == null ? source.toString() : nameMapper.apply(source);
            for (V target : targets.distinctValues()) {
                int count = targets.getCount(target);
                String targetPresentation = nameMapper == null ? target.toString() : nameMapper.apply(target);
                for (int i = 0; i < count; i++) {
                    builder.append("\"" + sourcePresentation + "\" -> \"" + targetPresentation + "\";\n");
                }
            }
        }

        builder.append("}");
        return builder.toString();
    }

    /**
     * @since 1.6
     */
    public String generateDot() {
        return generateDot(false, null, null);
    }

    public Integer[] deleteRandomEdge() {
        return null;
    }

    public Integer[] insertRandomEdge() {
        return null;
    }
}
