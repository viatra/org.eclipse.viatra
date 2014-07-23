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
package org.eclipse.viatra.dse.genetic.mutations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.TransformationRule;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.genetic.core.InstanceData;
import org.eclipse.viatra.dse.genetic.interfaces.IMutateTrajectory;
import org.eclipse.viatra.dse.guidance.Guidance;
import org.eclipse.viatra.dse.guidance.RuleInfo;

public class AddTransitionByPriorityMutation implements IMutateTrajectory {

    private Random rnd = new Random();

    @Override
    public InstanceData mutate(InstanceData originalTrajectory, ThreadContext context) {

        if (originalTrajectory.trajectory.isEmpty()) {
            throw new DSEException("Can't mutate an empty trajectory");
        }

        ArrayList<ITransition> result = new ArrayList<ITransition>(originalTrajectory.trajectory);

        ITransition lastTransition = result.get(result.size() - 1);
        Collection<? extends ITransition> availableTransitions = lastTransition.getResultsIn().getOutgoingTransitions();

        if (availableTransitions.isEmpty()) {
            // TODO other solution then exception
            throw new DSEException("Trajectory cannot be extended.");
        }

        Guidance guidance = context.getGuidance();
        if (guidance == null) {
            throw new DSEException("Guidance is missing for AddTransitionByPriorityMutation.");
        }
        Map<TransformationRule<? extends IPatternMatch>, RuleInfo> ruleInfos = guidance.getRuleInfos();

        double bestPriority = Double.MIN_VALUE;
        for (ITransition iTransition : availableTransitions) {
            double priority = ruleInfos.get(iTransition.getTransitionMetaData().rule).getPriority();
            if (priority > bestPriority) {
                bestPriority = priority;
            }
        }
        List<ITransition> bestTrasitions = new ArrayList<ITransition>();
        for (ITransition iTransition : availableTransitions) {
            if (ruleInfos.get(iTransition.getTransitionMetaData().rule).getPriority() == bestPriority) {
                bestTrasitions.add(iTransition);
            }
        }
        int index = rnd.nextInt(bestTrasitions.size());
        ITransition transition = bestTrasitions.get(index);

        result.add(transition);

        return new InstanceData(result);
    }

}
