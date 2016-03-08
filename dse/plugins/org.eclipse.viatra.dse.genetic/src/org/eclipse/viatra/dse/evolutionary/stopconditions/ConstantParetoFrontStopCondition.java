/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary.stopconditions;

import java.util.Collection;

import org.eclipse.viatra.dse.evolutionary.interfaces.IStopCondition;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public class ConstantParetoFrontStopCondition implements IStopCondition {

    private int constantIterations;

    /**
     * 
     * @param constantIterations
     */
    public ConstantParetoFrontStopCondition(int constantIterations) {
        this.constantIterations = constantIterations;
    }

    @Override
    public boolean checkStopCondition(Collection<TrajectoryFitness> survivedPopulation) {
        for (TrajectoryFitness trajectory : survivedPopulation) {
            if (trajectory.rank == 1 && trajectory.survive < constantIterations) {
                return false;
            }
        }
        return true;
    }

}
