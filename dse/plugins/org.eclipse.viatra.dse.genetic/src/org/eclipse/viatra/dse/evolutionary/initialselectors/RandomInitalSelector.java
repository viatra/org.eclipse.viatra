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

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.evolutionary.TrajectoryWithStateFitness;
import org.eclipse.viatra.dse.evolutionary.interfaces.IInitialPopulationSelector;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public class RandomInitalSelector implements IInitialPopulationSelector {

    private ThreadContext context;
    private DesignSpaceManager dsm;
    private Set<TrajectoryFitness> initialPopulation;

    private int populationSize;
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
        initialPopulation = new HashSet<TrajectoryFitness>(populationSize);
    }

    private int newLength() {
        return random.nextInt(maxDepth - minDepth) + minDepth;
    }

    @Override
    public void explore() {

        while (!(isInterrupted || initialPopulation.size() >= populationSize)) {

            while (lengthRemaining > 0) {
                lengthRemaining--;

                context.executeRandomActivationId();

                boolean gcSatisfied = context.checkGlobalConstraints();
                if (!gcSatisfied) {
                    context.backtrack();
                    lengthRemaining++;
                }

            }

            initialPopulation.add(new TrajectoryWithStateFitness(dsm.getTrajectoryInfo(), context.calculateFitness()));
            dsm.undoUntilRoot();
            lengthRemaining = newLength();
        }
    }

    @Override
    public void interruptStrategy() {
        isInterrupted = true;
    }

    @Override
    public Set<TrajectoryFitness> getInitialPopulation() {
        return initialPopulation;
    }

}
