/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
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
 * A single crossover point on both parents' trajectories is selected randomly. All transitions beyond that point in
 * either trajectory is swapped between the two parent trajectories. The resulting trajectories are the children. <br/>
 * 
 */
public class OnePointCrossover implements ICrossoverTrajectories {

    private Random random = new Random();

    @Override
    public Collection<InstanceData> crossover(List<InstanceData> parents, ThreadContext context) {

        List<ITransition> parent1 = parents.get(0).trajectory;
        List<ITransition> parent2 = parents.get(1).trajectory;

        if (parent1.size() < 2 || parent2.size() < 2) {
            throw new DSEException("Cannot crossover with empty or one long parent trajectories");
        }

        int minSize = Math.min(parent1.size(), parent2.size());
        int index = random.nextInt(minSize - 1) + 1;

        List<ITransition> p11 = parent1.subList(0, index);
        List<ITransition> p12 = parent1.subList(index, parent1.size());

        List<ITransition> p21 = parent2.subList(0, index);
        List<ITransition> p22 = parent2.subList(index, parent2.size());

        ArrayList<ITransition> child1 = new ArrayList<ITransition>(p11);
        child1.addAll(p22);

        ArrayList<ITransition> child2 = new ArrayList<ITransition>(p21);
        child1.addAll(p12);

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
