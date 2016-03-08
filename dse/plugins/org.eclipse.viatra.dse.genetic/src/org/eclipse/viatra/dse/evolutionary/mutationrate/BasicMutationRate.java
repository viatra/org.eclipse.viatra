/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary.mutationrate;

import java.util.Collection;

import org.eclipse.viatra.dse.evolutionary.interfaces.IMutationRate;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public class BasicMutationRate implements IMutationRate {

    private double mutationChance;

    public BasicMutationRate() {
        this(0.5d);
    }

    public BasicMutationRate(double mutationChance) {
        this.mutationChance = mutationChance;
    }

    @Override
    public double getMutationChance(Collection<TrajectoryFitness> currentPopulation,
            Collection<TrajectoryFitness> survivedPopulation, Collection<TrajectoryFitness> parentPopulation) {
        return mutationChance;
    }

}
