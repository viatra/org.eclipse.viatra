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
import java.util.List;
import java.util.Queue;

import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.IState;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.designspace.api.TrajectoryInfo;
import org.eclipse.viatra.dse.genetic.interfaces.IInitialPopulationSelector;
import org.eclipse.viatra.dse.genetic.interfaces.IStoreChild;

public class BFSSelector implements IInitialPopulationSelector {

    private DesignSpaceManager dsm;

    private Queue<List<ITransition>> pushQueue = new ArrayDeque<List<ITransition>>();
    private Queue<List<ITransition>> pullQueue = new ArrayDeque<List<ITransition>>();

    private int minDepthOfFirstPopulation;
    private int initialSizeOfPopulation;
    private int foundInstances = 0;

    private IStoreChild store;

    public BFSSelector() {
        this.minDepthOfFirstPopulation = 2;
        this.initialSizeOfPopulation = 30;
    }

    public BFSSelector(int minDepthOfFirstPopulation, int initialSizeOfPopulation) {
        this.minDepthOfFirstPopulation = minDepthOfFirstPopulation;
        this.initialSizeOfPopulation = initialSizeOfPopulation;
    }

    @Override
    public void setChildStore(IStoreChild store) {
        this.store = store;
    }

    @Override
    public void init(ThreadContext context) {
        if (store == null) {
            throw new DSEException("No IStoreChild is set for the BFSSelector");
        }
        dsm = context.getDesignSpaceManager();
    }

    @Override
    public ITransition getNextTransition(ThreadContext context, boolean lastWasSuccessful) {

        TrajectoryInfo trajectory = dsm.getTrajectoryInfo();

        if (trajectory.canStepBack()) {
            // push to queue only, if not already traversed, not goal
            // and not cut
            if (!dsm.isNewModelStateAlreadyTraversed()
                    && dsm.getCurrentState().getTraversalState().equals(IState.TraversalStateType.TRAVERSED)) {

                pushQueue.add(new ArrayList<ITransition>(dsm.getTrajectoryInfo().getTransitionTrajectory()));

                // Create child if certain depth is reached
                if (minDepthOfFirstPopulation <= trajectory.getDepthFromRoot()) {
                    store.addChild(context);
                    ++foundInstances;

                    // Start workers when population is ready
                    if (initialSizeOfPopulation <= foundInstances) {
                        return null;
                    }

                }
            }
            dsm.undoLastTransformation();
        }

        List<? extends ITransition> transitions = dsm.getUntraversedTransitionsFromCurrentState();
        do {
            if (!transitions.isEmpty()) {
                return transitions.get(0);
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
                transitions = dsm.getUntraversedTransitionsFromCurrentState();
            }
        } while (true);
    }

    @Override
    public void newStateIsProcessed(ThreadContext context, boolean isAlreadyTraversed, boolean isGoalState,
            boolean constraintsNotSatisfied) {
    }

}
