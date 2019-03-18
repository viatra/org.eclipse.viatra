/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary.mutations;

import java.util.Random;

import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.evolutionary.interfaces.IMutation;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public class ModifyRandomTransitionMutation implements IMutation {

    private Random rnd = new Random();

    @Override
    public boolean mutate(TrajectoryFitness parent, ThreadContext context) {

        Object[] trajectory = parent.trajectory;

        int trajectorySize = trajectory.length;

        if (trajectorySize < 1) {
            return false;
        }

        int index = rnd.nextInt(trajectorySize);

        context.executeTrajectoryWithMinimalBacktrackWithoutStateCoding(trajectory, index);

        boolean succesful = context.executeRandomActivationId();
        if (succesful) {
            context.executeTrajectoryByTryingWithoutStateCoding(trajectory, index + 1, trajectory.length);
        }

        return succesful;
    }

    @Override
    public IMutation createNew() {
        return new ModifyRandomTransitionMutation();
    }
}
