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
package org.eclipse.viatra.dse.guidance;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra.dse.api.TransformationRule;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.IDependencyGraph;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.IEdge;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.INode;

public class CriteriaContext {

    private final Guidance guidance;

    public CriteriaContext(Guidance guidance) {
        this.guidance = guidance;
    }

    public IDependencyGraph getDependencyGraph() {
        return guidance.getDependencyGraph();
    }

    public Map<TransformationRule<? extends IPatternMatch>, RuleInfo> getRuleInfos() {
        return guidance.getRuleInfos();
    }

    public int getInInhibitNodesTransitiveCardinality(INode node) {
        HashSet<INode> inhibitNodes = new HashSet<INode>();
        inhibitNodes.add(node);
        getInInhibitNodesTransitive(node, inhibitNodes);
        return inhibitNodes.size() - 1;
    }

    private void getInInhibitNodesTransitive(INode node, Set<INode> inhibitNodes) {
        for (IEdge edge : node.getInInhibitEdges()) {

            INode parentNode = edge.getFromNode();

            // if the node was not seen earlier, then add and process
            if (!inhibitNodes.contains(parentNode)) {
                inhibitNodes.add(parentNode);
                getInInhibitNodesTransitive(parentNode, inhibitNodes);
            }
        }
    }

    public int getOutTriggerNodesTransitiveCardinality(INode node) {
        HashSet<INode> triggerNodes = new HashSet<INode>();
        triggerNodes.add(node);
        getOutTriggerNodesTransitive(node, triggerNodes);
        return triggerNodes.size() - 1;
    }

    private void getOutTriggerNodesTransitive(INode node, Set<INode> triggerNodes) {
        for (IEdge edge : node.getOutTriggerEdges()) {

            INode parentNode = edge.getToNode();

            // if the node was not seen earlier, then add and process
            if (!triggerNodes.contains(parentNode)) {
                triggerNodes.add(parentNode);
                getOutTriggerNodesTransitive(parentNode, triggerNodes);
            }
        }
    }
}
