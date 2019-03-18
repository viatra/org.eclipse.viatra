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

public class CrowdedTournementParentSelector implements IParentSelectionStrategy {

    private Random rnd;
    private List<TrajectoryFitness> parentPopulation;

    @Override
    public void init(List<TrajectoryFitness> actualParentPopulation) {
        this.parentPopulation = actualParentPopulation;
        rnd = new Random();
    }

    @Override
    public TrajectoryFitness getNextParent() {

        int index1 = rnd.nextInt(parentPopulation.size());
        int index2 = rnd.nextInt(parentPopulation.size());
        while (index2 == index1)
            index2 = rnd.nextInt(parentPopulation.size());
        TrajectoryFitness parentCandidate1 = parentPopulation.get(index1);
        TrajectoryFitness parentCandidate2 = parentPopulation.get(index2);

        if (parentCandidate1.rank < parentCandidate2.rank) {
            return parentCandidate1;
        } else if (parentCandidate1.rank > parentCandidate2.rank) {
            return parentCandidate2;
        } else {
            if (parentCandidate2.crowdingDistance > parentCandidate1.crowdingDistance) {
                return parentCandidate2;
            } else {
                return parentCandidate1;
            }
        }
    }

    @Override
    public IParentSelectionStrategy createNew() {
        return new CrowdedTournementParentSelector();
    }

}
