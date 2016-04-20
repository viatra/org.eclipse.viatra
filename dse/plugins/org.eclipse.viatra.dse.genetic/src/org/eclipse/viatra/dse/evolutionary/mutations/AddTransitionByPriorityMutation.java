/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary.mutations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.eclipse.viatra.dse.api.DSETransformationRule;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.designspace.api.TrajectoryInfo;
import org.eclipse.viatra.dse.evolutionary.TrajectoryWithStateFitness;
import org.eclipse.viatra.dse.evolutionary.interfaces.IMutation;
import org.eclipse.viatra.dse.objectives.Fitness;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public class AddTransitionByPriorityMutation implements IMutation {

    private Random rnd = new Random();
    public Map<DSETransformationRule<?, ?>, Integer> priorities;

    public AddTransitionByPriorityMutation(Map<DSETransformationRule<?, ?>, Integer> priorities) {
        this.priorities = priorities;
    }

    @Override
    public TrajectoryFitness mutate(TrajectoryFitness parent, ThreadContext context) {

        DesignSpaceManager dsm = context.getDesignSpaceManager();

        for (ITransition t : parent.trajectory) {
            dsm.fireActivation(t);
        }

        Collection<? extends ITransition> transitions = dsm.getTransitionsFromCurrentState();
        int size = transitions.size();
        if (size == 0) {
            dsm.undoUntilRoot();
            return null;
        }

        int bestPriority = Integer.MIN_VALUE;
        for (ITransition iTransition : transitions) {
            int priority = priorities.get(iTransition.getTransitionMetaData().rule).intValue();
            if (priority > bestPriority) {
                bestPriority = priority;
            }
        }
        List<ITransition> bestTrasitions = new ArrayList<ITransition>();
        for (ITransition iTransition : transitions) {
            if (priorities.get(iTransition.getTransitionMetaData().rule).intValue() == bestPriority) {
                bestTrasitions.add(iTransition);
            }
        }
        int index = rnd.nextInt(bestTrasitions.size());
        ITransition transition = bestTrasitions.get(index);

        dsm.fireActivation(transition);

        Fitness calculateFitness = context.calculateFitness();
        TrajectoryInfo trajectoryInfo = dsm.getTrajectoryInfo();
        TrajectoryFitness child = new TrajectoryWithStateFitness(trajectoryInfo, calculateFitness);

        dsm.undoUntilRoot();

        return child;
    }

}
