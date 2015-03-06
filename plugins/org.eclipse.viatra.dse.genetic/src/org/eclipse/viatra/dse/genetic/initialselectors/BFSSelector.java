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
package org.eclipse.viatra.dse.genetic.initialselectors;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.IState;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.designspace.api.TrajectoryInfo;
import org.eclipse.viatra.dse.designspace.api.IGetCertainTransitions.FilterOptions;
import org.eclipse.viatra.dse.genetic.interfaces.IInitialPopulationSelector;
import org.eclipse.viatra.dse.genetic.interfaces.IStoreChild;
import org.eclipse.viatra.dse.objectives.Fitness;

public class BFSSelector implements IInitialPopulationSelector {

    private DesignSpaceManager dsm;

    private Queue<List<ITransition>> pushQueue = new ArrayDeque<List<ITransition>>();
    private Queue<List<ITransition>> pullQueue = new ArrayDeque<List<ITransition>>();

    private int minDepthOfFirstPopulation;
    private int initialSizeOfPopulation;
    private int foundInstances = 0;
    private float chanceOfSelection = 1;

    private IStoreChild store;

    private Random random = new Random();

    private boolean isInterrupted = false;

    private FilterOptions filterOptions;

    private ThreadContext context;

    public BFSSelector() {
        this(2, 1);
    }

    public BFSSelector(float chanceOfSelection) {
        this(chanceOfSelection, 2);
    }

    public BFSSelector(float chanceOfSelection, int minDepthOfFirstPopulation) {
        this.minDepthOfFirstPopulation = minDepthOfFirstPopulation;
        this.chanceOfSelection = chanceOfSelection;
        filterOptions = new FilterOptions().nothingIfCut().nothingIfGoal().untraversedOnly();
    }

    @Override
    public void setChildStore(IStoreChild store) {
        this.store = store;
    }

    @Override
    public void init(ThreadContext context) {
        this.context = context;
        if (store == null) {
            throw new DSEException("No IStoreChild is set for the BFSSelector");
        }
        dsm = context.getDesignSpaceManager();
    }

    @Override
    public ITransition getNextTransition(boolean lastWasSuccessful) {

        if (isInterrupted) {
            return null;
        }

        TrajectoryInfo trajectory = dsm.getTrajectoryInfo();

        if (trajectory.canStepBack()) {
            // push to queue only, if not already traversed, not goal
            // and not cut
            if (!dsm.isNewModelStateAlreadyTraversed()
                    && dsm.getCurrentState().getTraversalState().equals(IState.TraversalStateType.TRAVERSED)) {

                pushQueue.add(new ArrayList<ITransition>(dsm.getTrajectoryInfo().getTransitionTrajectory()));

                // Create child if certain depth is reached
                if (minDepthOfFirstPopulation <= trajectory.getDepthFromRoot()) {

                    if (random.nextFloat() < chanceOfSelection) {
                        store.addChild(context);
                        ++foundInstances;
                        // Start workers when population is ready
                        if (initialSizeOfPopulation <= foundInstances) {
                            return null;
                        }
                    }

                }
            }
            dsm.undoLastTransformation();
        }

        Collection<? extends ITransition> transitions = dsm.getTransitionsFromCurrentState(filterOptions);
        do {
            if (!transitions.isEmpty()) {
                return transitions.iterator().next();
            } else {
                List<ITransition> next = pullQueue.poll();
                if (next != null) {
                    while (dsm.undoLastTransformation()) {
                    }
                    for (ITransition t : next) {
                        dsm.fireActivation(t);
                    }
                } else {

                    if (pushQueue.size() == 0) {
                        throw new DSEException("Design space is too small for given population size.");
                    }
                    pullQueue = pushQueue;
                    pushQueue = new ArrayDeque<List<ITransition>>();
                }
                transitions = dsm.getTransitionsFromCurrentState(filterOptions);
            }
        } while (true);
    }

    @Override
    public void newStateIsProcessed(boolean isAlreadyTraversed, Fitness objectives, boolean constraintsNotSatisfied) {
    }

    @Override
    public void setPopulationSize(int populationSize) {
        initialSizeOfPopulation = populationSize;
    }

    @Override
    public void interrupted() {
        isInterrupted = true;
    }

}
