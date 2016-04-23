/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary;

import org.eclipse.viatra.dse.designspace.api.IState;
import org.eclipse.viatra.dse.designspace.api.TrajectoryInfo;
import org.eclipse.viatra.dse.objectives.Fitness;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public class TrajectoryWithStateFitness extends TrajectoryFitness {

    public Object state;

    public TrajectoryWithStateFitness(Object[] trajectory, IState state, Fitness fitness) {
        super(trajectory, fitness);
        this.state = state;
    }

    public TrajectoryWithStateFitness(TrajectoryInfo trajectoryInfo, Fitness fitness) {
        super(trajectoryInfo, fitness);
        state = trajectoryInfo.getCurrentStateId();
    }

    public TrajectoryWithStateFitness(Object transition, Object state, Fitness fitness) {
        super(transition, fitness);
        this.state = state;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TrajectoryWithStateFitness) {
            return ((TrajectoryWithStateFitness) obj).state.equals(state);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return state.hashCode();
    }
}
