/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.guidance.dependencygraph.simpleimpl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.PatternWithCardinality;
import org.eclipse.viatra.dse.api.TransformationRule;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.EdgeType;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.IDependencyGraph;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.IEdge;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.INode;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.NodeType;

public class DependencyGraph implements IDependencyGraph {

    private final Set<INode> nodes = new HashSet<INode>();
    private final Set<IEdge> edges = new HashSet<IEdge>();

    private final Map<TransformationRule<? extends IPatternMatch>, INode> nodesByTransformationRule = new HashMap<TransformationRule<? extends IPatternMatch>, INode>();
    private final Map<PatternWithCardinality, INode> nodesByGoalPattern = new HashMap<PatternWithCardinality, INode>();
    private final Map<PatternWithCardinality, INode> nodesByConstraint = new HashMap<PatternWithCardinality, INode>();

    @Override
    public void addNode(TransformationRule<? extends IPatternMatch> transformationRule) {
        Node node = new Node(transformationRule);
        nodes.add(node);
        nodesByTransformationRule.put(node.getTransformationRule(), node);
    }

    @Override
    public void addNode(PatternWithCardinality pattern, NodeType nodeType) {
        Node node = new Node(pattern, nodeType);
        nodes.add(node);
        switch (node.getType()) {
        case CONSTRAINT:
            nodesByConstraint.put(node.getConstraint(), node);
            break;
        case GOAL:
            nodesByGoalPattern.put(node.getGoalPattern(), node);
            break;
        case RULE:
            throw new DSEException("It is not a rule node!");
        }
    }

    @Override
    public void addEdge(INode fromNode, INode toNode, EdgeType type, EModelElement modelElement, int numOfElements) {
        IEdge edge = getEdge(fromNode, toNode);
        if (edge != null) {
            edge.addEdgeAtom(type, modelElement, numOfElements);
        } else {
            edge = new Edge(toNode, fromNode);
            edge.addEdgeAtom(type, modelElement, numOfElements);
            edges.add(edge);
        }
        toNode.getInEdges().add(edge);
        fromNode.getOutEdges().add(edge);
        edges.add(edge);
    }

    @Override
    public IEdge getEdge(INode fromNode, INode toNode) {
        for (IEdge edge : fromNode.getOutEdges()) {
            if (edge.getToNode() == toNode) {
                return edge;
            }
        }
        return null;
    }

    @Override
    public Set<INode> getNodes() {
        return nodes;
    }

    @Override
    public Set<IEdge> getEdges() {
        return edges;
    }

    @Override
    public INode getNodeByTransformationRule(TransformationRule<? extends IPatternMatch> rule) {
        return nodesByTransformationRule.get(rule);
    }

    @Override
    public INode getNodeByGoalPattern(PatternWithCardinality pattern) {
        return nodesByGoalPattern.get(pattern);
    }

    @Override
    public INode getNodeByConstraint(PatternWithCardinality pattern) {
        return nodesByConstraint.get(pattern);
    }

    @Override
    public Collection<INode> getRuleNodes() {
        return nodesByTransformationRule.values();
    }

    @Override
    public Collection<INode> getGoalNodes() {
        return nodesByGoalPattern.values();
    }

    @Override
    public Collection<INode> getConstraintNodes() {
        return nodesByConstraint.values();
    }

    @Override
    public void save(String filename) {
    }
}
