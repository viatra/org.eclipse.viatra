/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary.initialselectors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.evolutionary.interfaces.IInitialPopulationSelector;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public class RandomInitalSelector implements IInitialPopulationSelector {

    private ThreadContext context;
    private DesignSpaceManager dsm;
    private List<TrajectoryFitness> initialPopulation;

    private int populationSize;
    private int foundInstances = 0;
    private int minDepth;
    private int maxDepth;

    private int lengthRemaining;
    private Random random = new Random();
    private boolean isInterrupted = false;

    public RandomInitalSelector(int minDepth, int maxDepth) {
        this.minDepth = minDepth;
        this.maxDepth = maxDepth;
    }

    @Override
    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    @Override
    public void initStrategy(ThreadContext context) {
        this.context = context;
        dsm = context.getDesignSpaceManager();
        lengthRemaining = newLength();
        initialPopulation = new ArrayList<TrajectoryFitness>(populationSize);
    }

    private int newLength() {
        return random.nextInt(maxDepth - minDepth) + minDepth;
    }

    @Override
    public void explore() {

        while (!(isInterrupted || foundInstances >= populationSize)) {

            while (lengthRemaining > 0) {
                lengthRemaining--;

                Collection<? extends ITransition> transitions = dsm.getTransitionsFromCurrentState();
                int index = random.nextInt(transitions.size());
                ITransition transition = getByIndex(transitions, index);
                dsm.fireActivation(transition);

                boolean gcSatisfied = context.checkGlobalConstraints();
                if (!gcSatisfied) {
                    dsm.undoLastTransformation();
                    lengthRemaining++;
                    // TODO could do smarter
                }

            }

            foundInstances++;
            initialPopulation.add(new TrajectoryFitness(dsm.getTrajectoryInfo(), context.calculateFitness()));
            while (dsm.undoLastTransformation())
                ;
            lengthRemaining = newLength();
        }
    }

    @Override
    public void interruptStrategy() {
        isInterrupted = true;
    }

    private static ITransition getByIndex(Collection<? extends ITransition> availableTransitions, int index) {
        int i = 0;
        Iterator<? extends ITransition> iterator = availableTransitions.iterator();
        while (iterator.hasNext()) {
            ITransition transition = iterator.next();
            if (i == index) {
                return transition;
            } else {
                ++i;
            }
        }
        throw new IndexOutOfBoundsException("size: " + availableTransitions.size() + ", index: " + index);
    }

    @Override
    public Collection<TrajectoryFitness> getInitialPopulation() {
        return initialPopulation;
    }

}
