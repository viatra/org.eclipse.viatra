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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra.dse.api.PatternWithCardinality;
import org.eclipse.viatra.dse.api.TransformationRule;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.IEdge;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.INode;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.NodeType;

import com.google.common.base.Preconditions;

public class Node implements INode {

    private Set<IEdge> outEdges = new HashSet<IEdge>();
    private Set<IEdge> inEdges = new HashSet<IEdge>();

    private NodeType nodeType;

    private TransformationRule<? extends IPatternMatch> transformationRule;
    private PatternWithCardinality constraint;
    private PatternWithCardinality goalPattern;

    public Node(TransformationRule<? extends IPatternMatch> transformationRule) {
        this.transformationRule = transformationRule;
        nodeType = NodeType.RULE;
    }

    public Node(PatternWithCardinality pattern, NodeType nodeType) {
        Preconditions.checkState(!nodeType.equals(NodeType.RULE));
        this.constraint = pattern;
        this.goalPattern = pattern;
        this.nodeType = nodeType;
    }

    @Override
    public NodeType getType() {
        return nodeType;
    }

    @Override
    public Set<IEdge> getOutEdges() {
        return outEdges;
    }

    @Override
    public Set<IEdge> getInEdges() {
        return inEdges;
    }

    @Override
    public TransformationRule<? extends IPatternMatch> getTransformationRule() {
        if (!nodeType.equals(NodeType.RULE)) {
            throw new UnsupportedOperationException();
        }
        return transformationRule;
    }

    @Override
    public PatternWithCardinality getConstraint() {
        if (!nodeType.equals(NodeType.CONSTRAINT)) {
            throw new UnsupportedOperationException();
        }
        return constraint;
    }

    @Override
    public PatternWithCardinality getGoalPattern() {
        if (!nodeType.equals(NodeType.GOAL)) {
            throw new UnsupportedOperationException();
        }
        return goalPattern;
    }

    @Override
    public Set<IEdge> getInTriggerEdges() {
        Set<IEdge> edges = new HashSet<IEdge>();
        for (IEdge edge : inEdges) {
            if (edge.isTrigger()) {
                edges.add(edge);
            }
        }
        return edges;
    }

    @Override
    public Set<IEdge> getInInhibitEdges() {
        Set<IEdge> edges = new HashSet<IEdge>();
        for (IEdge edge : inEdges) {
            if (edge.isInhibit()) {
                edges.add(edge);
            }
        }
        return edges;
    }

    @Override
    public Set<IEdge> getOutTriggerEdges() {
        Set<IEdge> edges = new HashSet<IEdge>();
        for (IEdge edge : outEdges) {
            if (edge.isTrigger()) {
                edges.add(edge);
            }
        }
        return edges;
    }

    @Override
    public Set<IEdge> getOutInhibitEdges() {
        Set<IEdge> edges = new HashSet<IEdge>();
        for (IEdge edge : outEdges) {
            if (edge.isInhibit()) {
                edges.add(edge);
            }
        }
        return edges;
    }
}
