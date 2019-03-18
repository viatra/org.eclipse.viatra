/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
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

    @Override
    public IParentSelectionStrategy createNew() {
        return new RandomParentSelector();
    }

}
