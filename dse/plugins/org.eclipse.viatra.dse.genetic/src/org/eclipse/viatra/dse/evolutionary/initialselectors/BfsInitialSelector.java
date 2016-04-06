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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.IGetCertainTransitions.FilterOptions;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.designspace.api.TrajectoryInfo;
import org.eclipse.viatra.dse.evolutionary.interfaces.IInitialPopulationSelector;
import org.eclipse.viatra.dse.objectives.Fitness;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public class BfsInitialSelector implements IInitialPopulationSelector {

    private DesignSpaceManager dsm;

    private Queue<ITransition[]> queue = new ArrayDeque<ITransition[]>();

    private int minDepthOfFirstPopulation;
    private int populationSize;
    private float chanceOfSelection = 1;

    private List<TrajectoryFitness> initialPopulation;

    private Random random = new Random();

    private boolean isInterrupted = false;

    private FilterOptions filterOptions;

    private ThreadContext context;

    private TrajectoryInfo trajectoryInfo;

    public BfsInitialSelector() {
        this(2, 1);
    }

    public BfsInitialSelector(float chanceOfSelection) {
        this(chanceOfSelection, 2);
    }

    public BfsInitialSelector(float chanceOfSelection, int minDepthOfFirstPopulation) {
        this.minDepthOfFirstPopulation = minDepthOfFirstPopulation;
        this.chanceOfSelection = chanceOfSelection;
        filterOptions = new FilterOptions().nothingIfCut().nothingIfGoal().untraversedOnly();
    }

    @Override
    public void initStrategy(ThreadContext context) {
        this.context = context;
        initialPopulation = new ArrayList<TrajectoryFitness>(populationSize);
        dsm = context.getDesignSpaceManager();
        trajectoryInfo = dsm.getTrajectoryInfo();
    }

    @Override
    public void explore() {

        while (!(isInterrupted || initialPopulation.size() >= populationSize)) {
            Collection<? extends ITransition> transitions = dsm.getTransitionsFromCurrentState(filterOptions);

            for (ITransition iTransition : transitions) {
                dsm.fireActivation(iTransition);
                if (!dsm.isNewModelStateAlreadyTraversed()) {
                    ITransition[] trajectory = trajectoryInfo.getTransitionTrajectory().toArray(new ITransition[0]);
                    queue.add(trajectory);
                    if (minDepthOfFirstPopulation <= trajectory.length && random.nextFloat() < chanceOfSelection) {
                        Fitness fitness = context.calculateFitness();
                        initialPopulation.add(new TrajectoryFitness(trajectory, fitness));
                        if (populationSize <= initialPopulation.size()) {
                            dsm.undoUntilRoot();
                            return;
                        }
                    }
                }
                dsm.undoLastTransformation();
            }

            dsm.undoUntilRoot();
            ITransition[] nextTrajectory = queue.poll();
            for (ITransition iTransition : nextTrajectory) {
                dsm.fireActivation(iTransition);
            }
        }
        dsm.undoUntilRoot();

    }

    @Override
    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    @Override
    public void interruptStrategy() {
        isInterrupted = true;
    }

    @Override
    public List<TrajectoryFitness> getInitialPopulation() {
        return initialPopulation;
    }

}
