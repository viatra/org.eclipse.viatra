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
package org.eclipse.viatra.dse.guidance.dependencygraph.interfaces;

import java.util.Collection;
import java.util.Set;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra.dse.api.PatternWithCardinality;
import org.eclipse.viatra.dse.api.TransformationRule;

public interface IDependencyGraph {

    void addNode(TransformationRule<? extends IPatternMatch> transformationRule);

    void addNode(PatternWithCardinality pattern, NodeType nodeType);

    void addEdge(INode fromNode, INode toNode, EdgeType type, EModelElement modelElement, int numOfElements);

    IEdge getEdge(INode fromNode, INode toNode);

    Set<INode> getNodes();

    Set<IEdge> getEdges();

    INode getNodeByTransformationRule(TransformationRule<? extends IPatternMatch> rule);

    INode getNodeByGoalPattern(PatternWithCardinality pattern);

    INode getNodeByConstraint(PatternWithCardinality pattern);

    Collection<INode> getRuleNodes();

    Collection<INode> getGoalNodes();

    Collection<INode> getConstraintNodes();

    void save(String filename);

}