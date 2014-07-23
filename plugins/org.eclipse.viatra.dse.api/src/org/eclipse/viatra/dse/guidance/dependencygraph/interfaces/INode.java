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

import java.util.Set;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra.dse.api.PatternWithCardinality;
import org.eclipse.viatra.dse.api.TransformationRule;

public interface INode {

    NodeType getType();

    Set<IEdge> getOutEdges();

    Set<IEdge> getInEdges();

    TransformationRule<? extends IPatternMatch> getTransformationRule();

    PatternWithCardinality getConstraint();

    PatternWithCardinality getGoalPattern();

    Set<IEdge> getInTriggerEdges();

    Set<IEdge> getInInhibitEdges();

    Set<IEdge> getOutTriggerEdges();

    Set<IEdge> getOutInhibitEdges();

}