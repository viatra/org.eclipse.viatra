/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary.parentselectors;

import java.util.List;
import java.util.Random;

import org.eclipse.viatra.dse.evolutionary.interfaces.IParentSelectionStrategy;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public class RandomParentSelector implements IParentSelectionStrategy {

    private Random rnd = new Random();
    private List<TrajectoryFitness> parentPopulation;

    @Override
    public void init(List<TrajectoryFitness> actualParentPopulation) {
        this.parentPopulation = actualParentPopulation;
    }

    @Override
    public TrajectoryFitness getNextParent() {
        int index = rnd.nextInt(parentPopulation.size());
        return parentPopulation.get(index);
    }

}
