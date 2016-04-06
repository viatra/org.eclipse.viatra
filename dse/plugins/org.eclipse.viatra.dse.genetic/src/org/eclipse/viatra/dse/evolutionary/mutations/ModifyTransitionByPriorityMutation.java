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

import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.DSETransformationRule;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.designspace.api.TrajectoryInfo;
import org.eclipse.viatra.dse.evolutionary.interfaces.IMutation;
import org.eclipse.viatra.dse.genetic.core.GeneticHelper;
import org.eclipse.viatra.dse.objectives.Fitness;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public class ModifyTransitionByPriorityMutation implements IMutation {

    private Random rnd = new Random();
    private Map<DSETransformationRule<?, ?>, Integer> priorities;

    public ModifyTransitionByPriorityMutation(Map<DSETransformationRule<?, ?>, Integer> priorities) {
        this.priorities = priorities;
    }

    @Override
    public TrajectoryFitness mutate(TrajectoryFitness parent, ThreadContext context) {

        DesignSpaceManager dsm = context.getDesignSpaceManager();
        ITransition[] trajectory = parent.trajectory;

        int trajectorySize = trajectory.length;
        int index = rnd.nextInt(trajectorySize);
        for (int i = 0; i < index; i++) {
            dsm.fireActivation(trajectory[i]);
        }

        Collection<? extends ITransition> transitions = dsm.getTransitionsFromCurrentState();
        int transitionsSize = transitions.size();
        if (transitionsSize == 0) {
            throw new DSEException("No transition is found for mutation.");
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
        index = rnd.nextInt(bestTrasitions.size());
        ITransition transition = bestTrasitions.get(index);

        dsm.fireActivation(transition);

        for (int i = index + 1; i < trajectorySize; i++) {
            GeneticHelper.tryFireRightTransition(dsm, trajectory[i]);
        }

        Fitness calculateFitness = context.calculateFitness();
        TrajectoryInfo trajectoryInfo = dsm.getTrajectoryInfo();
        TrajectoryFitness child = new TrajectoryFitness(trajectoryInfo, calculateFitness);

        dsm.undoUntilRoot();

        return child;
    }

}
