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
package org.eclipse.viatra.dse.guidance.criterias;

import java.util.Collection;

import org.eclipse.viatra.dse.guidance.CriteriaContext;
import org.eclipse.viatra.dse.guidance.ICriteria;
import org.eclipse.viatra.dse.guidance.RuleInfo;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.IEdge;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.INode;

public class CutOffPermanentlyDisabledRule implements ICriteria {

    @Override
    public EvaluationResult evaluate(CriteriaContext context) {

        Collection<INode> ruleNodes = context.getDependencyGraph().getRuleNodes();

        // for each disable rules
        for (INode ruleNode : ruleNodes) {

            RuleInfo ruleInfo = context.getRuleInfos().get(ruleNode.getTransformationRule());

            if (!ruleInfo.isEnabled()) {

                // if there is no firable trigger edge then cut off
                for (IEdge edge : ruleNode.getInTriggerEdges()) {
                    INode node = edge.getFromNode();
                    RuleInfo precedingRuleInfo = context.getRuleInfos().get(node.getTransformationRule());

                    // TODO have to check backwardly
                    if (precedingRuleInfo.getRemainingApp() > 0) {
                        return EvaluationResult.NONE;
                    }
                }
            }

        }

        return EvaluationResult.CUT_OFF;
    }

}
