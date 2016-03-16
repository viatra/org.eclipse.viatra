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
package org.eclipse.viatra.dse.genetic.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.SolutionTrajectory;
import org.eclipse.viatra.dse.api.strategy.interfaces.LocalSearchStrategyBase;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.GlobalContext;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.IState.TraversalStateType;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.objectives.Fitness;
import org.eclipse.viatra.dse.objectives.impl.ConstraintsObjective;
import org.eclipse.viatra.dse.solutionstore.ISolutionStore;

public class InstanceGeneticStrategy extends LocalSearchStrategyBase {

    private enum WorkerState {
        NEXT_INSTANCE,
        MAKING_FEASIBLE,
        MUTATION,
        FITNESS_CALCULATION;
    }

    private GeneticSharedObject sharedObject;
    private InstanceData actInstanceData;
    private DesignSpaceManager dsm;
    private GlobalContext gc;

    private WorkerState state;

    private boolean correctionWasNeeded = false;
    private ThreadContext context;
    private ConstraintsObjective genObjective;
    private ISolutionStore solutionStore;

    @Override
    public void init(ThreadContext context) {

        this.context = context;
        gc = context.getGlobalContext();
        dsm = context.getDesignSpaceManager();

        state = WorkerState.NEXT_INSTANCE;

        Object so = gc.getSharedObject();
        if (so == null) {
            throw new DSEException("No GeneticSharedObject is set");
        }

        if (so instanceof GeneticSharedObject) {
            sharedObject = (GeneticSharedObject) so;
        } else {
            throw new DSEException("The shared object is not the type of GeneticSharedObject.");
        }
        
        genObjective = (ConstraintsObjective) context.getLeveledObjectives()[0][0];
        
        solutionStore = context.getGlobalContext().getSolutionStore();

    }

    @Override
    public ITransition getNextTransition(boolean lastWasSuccesful) {
        do {

            // Get next Instance
            if (state == WorkerState.NEXT_INSTANCE) {

                actInstanceData = null;
                correctionWasNeeded = false;

                while (actInstanceData == null) {
                    try {
                        actInstanceData = sharedObject.instancesToBeChecked.poll(10, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e1) {
                    }
                    if ((actInstanceData == null && !sharedObject.newPopulationIsNeeded.get())
                            || gc.isExceptionHappendInOtherThread()) {
                        return null;
                    }
                }

                // Go back to root
                while (dsm.undoLastTransformation()) {
                }

                state = WorkerState.MAKING_FEASIBLE;
            }
            if (state == WorkerState.MAKING_FEASIBLE) {
                if (actInstanceData.trajectory.isEmpty()) {
                    throw new DSEException("An empty trajectory is not feasible.");
                }

                int depthFromRoot = dsm.getTrajectoryInfo().getDepthFromRoot();
                while (depthFromRoot < actInstanceData.trajectory.size()) {
                    if (dsm.getCurrentState().getTraversalState() == TraversalStateType.CUT) {
                        break;
                    }

                    ITransition iTransition = actInstanceData.trajectory.get(depthFromRoot);
                    Collection<? extends ITransition> transitions = dsm.getTransitionsFromCurrentState();
                    Iterator<? extends ITransition> iterator = transitions.iterator();
                    boolean wasFeasibleTransition = false;
                    while (iterator.hasNext()) {
                        ITransition t = iterator.next();

                        if (t.getId().equals(iTransition.getId())) {
                            actInstanceData.trajectory.set(depthFromRoot, t);
                            if (t.isAssignedToFire()) {
                                wasFeasibleTransition = true;
                                dsm.fireActivation(t);
                                depthFromRoot = dsm.getTrajectoryInfo().getDepthFromRoot();
                                break;
                            } else {
                                return t;
                            }
                        }
                    }

                    if (!wasFeasibleTransition) {
                        actInstanceData.trajectory.remove(depthFromRoot);
                        if (!correctionWasNeeded) {
                            sharedObject.numOfCorrections.incrementAndGet();
                            correctionWasNeeded = true;
                        }
                    }
                }

                if (dsm.getCurrentState().getTraversalState() == TraversalStateType.CUT) {
                    for (int i = actInstanceData.trajectory.size() - 1; i >= depthFromRoot; --i) {
                        actInstanceData.trajectory.remove(i);
                    }
                    dsm.undoLastTransformation();
                }

                if (actInstanceData.trajectory.size() < 2) {
                    sharedObject.unfeasibleInstances.incrementAndGet();
                    state = WorkerState.NEXT_INSTANCE;
                } else {
                    state = WorkerState.FITNESS_CALCULATION;
                }

            }
            if (state == WorkerState.FITNESS_CALCULATION) {

                Fitness fitness = context.calculateFitness();
                actInstanceData.objectives = fitness;
                for (int i = 0; i<genObjective.getSoftConstraints().size(); i++) {
                    actInstanceData.violations.put(genObjective.getSoftName(i), genObjective.getSoftMatches().get(i));
                }
                actInstanceData.sumOfConstraintViolationMeauserement = fitness.get(genObjective.getName());

                if (sharedObject.addInstanceToBestSolutions.get()) {
                    SolutionTrajectory solutionTrajectory = dsm.createSolutionTrajectroy();
                    sharedObject.bestSolutions.put(actInstanceData, solutionTrajectory);
                    solutionStore.newSolution(context);
                    state = WorkerState.NEXT_INSTANCE;
                } else {
                    sharedObject.childPopulation.add(actInstanceData);
                    state = WorkerState.NEXT_INSTANCE;
                }
            }

        } while (sharedObject.newPopulationIsNeeded.get() || actInstanceData != null);

        return null;
    }

    @Override
    public void newStateIsProcessed(boolean isAlreadyTraversed, Fitness objectives, boolean constraintsNotSatisfied) {
    }

    @Override
    public void interrupted() {
    }
}
