/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.integration.zest.viewer;

import java.util.Map;

import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.zest.fx.jface.ZestFxJFaceModule;

import com.google.inject.Module;

public class ModifiableZestContentViewer extends ZestContentViewer {

    public ModifiableZestContentViewer() {
        this(new ViatraZestModule());
    }

    public ModifiableZestContentViewer(Module module) {
        super(module);
    }

    public ModifiableZestContentViewer(ZestFxJFaceModule module) {
        super(module);
    }

    public void addNode(Object contentNode) {
        Node node = createNode(contentNode, (IGraphEdgeContentProvider) getContentProvider(), getLabelProvider());
        Graph rootGraph = getRootGraph();
        rootGraph.getNodes().add(node);
    }

    public void removeNode(Object contentNode) {
        final Map<Object, Node> nodeMap = contentNodeMap;
        if (nodeMap.containsKey(contentNode)) {
            Node node = nodeMap.get(contentNode);
            final Graph graph = node.getGraph();
            if (graph != null) {
                graph.getNodes().remove(node);
            }
        }
    }

    public void addEdge(Object contentEdge) {
        final Map<Object, Edge> edgeMap = contentEdgeMap;
        IGraphEdgeContentProvider contentProvider = (IGraphEdgeContentProvider) getContentProvider();
        Object contentSourceNode = contentProvider.getSource(contentEdge);
        Object contentTargetNode = contentProvider.getTarget(contentEdge);
        final Map<Object, Node> nodeMap = getContentNodeMap();
        Node sourceNode = nodeMap.get(contentSourceNode);
        Node targetNode = nodeMap.get(contentTargetNode);
        Edge edge = createEdge(getLabelProvider(), contentEdge, sourceNode, targetNode);
        edgeMap.put(contentEdge, edge);

        Graph graph = sourceNode.getGraph();
        graph.getEdges().add(edge);
    }

    public void removeEdge(Object contentEdge) {
        final Map<Object, Edge> edgeMap = contentEdgeMap;

        if (edgeMap.containsKey(contentEdge)) {
            Edge edge = edgeMap.get(contentEdge);
            edgeMap.remove(contentEdge, edge);

            Graph graph = edge.getGraph();
            if (graph != null) {
                graph.getEdges().remove(edge);
            }
        }
    }
}
