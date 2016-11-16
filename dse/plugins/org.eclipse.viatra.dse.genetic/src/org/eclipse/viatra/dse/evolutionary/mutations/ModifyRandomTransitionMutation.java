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

import java.util.Arrays;
import java.util.Random;

import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.TrajectoryInfo;
import org.eclipse.viatra.dse.evolutionary.TrajectoryWithStateFitness;
import org.eclipse.viatra.dse.evolutionary.interfaces.IMutation;
import org.eclipse.viatra.dse.objectives.Fitness;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public class ModifyRandomTransitionMutation implements IMutation {

    private Random rnd = new Random();

    @Override
    public TrajectoryFitness mutate(TrajectoryFitness parent, ThreadContext context) {

        Object[] trajectory = parent.trajectory;

        int trajectorySize = trajectory.length;

        if (trajectorySize < 1) {
            return null;
        }

        int index = rnd.nextInt(trajectorySize);

        context.executeTrajectoryWithoutStateCoding(trajectory, index);

        boolean succesful = context.executeRandomActivationId();
        if (!succesful) {
            context.backtrackUntilRoot();
            return null;
        }
        
        Object[] trajectoryEnd = Arrays.copyOfRange(trajectory, index + 1, trajectory.length);
        context.executeTrajectoryByTryingWithoutStateCoding(trajectoryEnd);

        Fitness calculateFitness = context.calculateFitness();
        TrajectoryInfo trajectoryInfo = context.getTrajectoryInfo();
        TrajectoryFitness child = new TrajectoryWithStateFitness(trajectoryInfo, calculateFitness);

        context.backtrackUntilRoot();

        return child;
    }

    @Override
    public IMutation createNew() {
        return new ModifyRandomTransitionMutation();
    }
}
