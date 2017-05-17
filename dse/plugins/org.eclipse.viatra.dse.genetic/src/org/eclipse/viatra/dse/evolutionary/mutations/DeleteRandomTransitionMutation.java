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

import java.util.Random;

import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.evolutionary.interfaces.IMutation;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public class DeleteRandomTransitionMutation implements IMutation {

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

        context.executeTrajectoryByTryingWithoutStateCoding(trajectory, index + 1, trajectory.length);

        return true;
    }

    @Override
    public IMutation createNew() {
        return new DeleteRandomTransitionMutation();
    }
}
