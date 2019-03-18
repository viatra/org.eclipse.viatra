/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary;

import org.eclipse.viatra.dse.designspace.api.TrajectoryInfo;
import org.eclipse.viatra.dse.objectives.Fitness;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public class TrajectoryWithStateFitness extends TrajectoryFitness {

    public Object state;

    public TrajectoryWithStateFitness(Object[] trajectory, Object state, Fitness fitness) {
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
