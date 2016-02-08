/*******************************************************************************
 * Copyright (c) 2010-2015, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.genetic.crossovers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.genetic.core.InstanceData;
import org.eclipse.viatra.dse.genetic.interfaces.ICrossoverTrajectories;

/**
 * Makes two child trajectories from two parent trajectories. <br/>
 * <br/>
 * One transition is selected randomly in the first parent and an other one in the second. The two new children will be
 * derived by swapping these transitions.
 * 
 * @author Andras Szabolcs Nagy
 */
public class SwapTransitionCrossover implements ICrossoverTrajectories {

    private Random random = new Random();

    @Override
    public Collection<InstanceData> crossover(List<InstanceData> parents, ThreadContext context) {

        List<ITransition> parent1 = parents.get(0).trajectory;
        List<ITransition> parent2 = parents.get(1).trajectory;

        if (parent1.size() < 2 || parent2.size() < 2) {
            throw new DSEException("Cannot crossover with empty or one long parent trajectories");
        }

        int index1 = random.nextInt(parent1.size() - 1) + 1;
        int index2 = random.nextInt(parent2.size() - 1) + 1;

        ITransition t1 = parent1.get(index1);
        ITransition t2 = parent1.get(index2);

        ArrayList<ITransition> child1 = new ArrayList<ITransition>(parent1);
        child1.set(index1, t2);

        ArrayList<ITransition> child2 = new ArrayList<ITransition>(parent2);
        child1.set(index2, t1);

        return Arrays.asList(new InstanceData(child1), new InstanceData(child2));

    }

    @Override
    public int numberOfNeededParents() {
        return 2;
    }

    @Override
    public int numberOfCreatedChilds() {
        return 2;
    }

}
