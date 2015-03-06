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
package org.eclipse.viatra.dse.api.strategy.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.strategy.interfaces.IStrategy;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.IGetCertainTransitions.FilterOptions;
import org.eclipse.viatra.dse.designspace.api.IState.TraversalStateType;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.objectives.Fitness;
import org.eclipse.viatra.dse.objectives.ObjectiveComparatorHelper;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;
import org.eclipse.viatra.dse.solutionstore.ISolutionStore;

public class HillClimbingStrategy implements IStrategy {

    enum HillClimbingStrategyState {
        TRY_AND_SAVE,
        COMPARE_AND_STEP
    }

    private DesignSpaceManager dsm;

    private double percentOfOpenedStates;
    private boolean interrupted;
    private HillClimbingStrategyState state = HillClimbingStrategyState.TRY_AND_SAVE;

    private int triedTransitions = 0;
    private Fitness bestFitness = null;
    private Random rnd = new Random();

    private ObjectiveComparatorHelper objectiveComparatorHelper;

    private ISolutionStore solutionStore;

    private Logger logger = Logger.getLogger(getClass());

    private FilterOptions filterOptions;

    public HillClimbingStrategy() {
        this(1.001);
    }

    public HillClimbingStrategy(double percentOfOpenedStates) {
        this.percentOfOpenedStates = percentOfOpenedStates;
        filterOptions = new FilterOptions().nothingIfCut().untraversedOnly();
    }

    @Override
    public void init(ThreadContext context) {
        dsm = context.getDesignSpaceManager();
        objectiveComparatorHelper = context.getObjectiveComparatorHelper();
        solutionStore = context.getGlobalContext().getSolutionStore();
        if (!solutionStore.isStrategyDependent()) {
            throw new DSEException("This strategy needs a strategy dependent solution store.");
        }
    }

    @Override
    public ITransition getNextTransition(ThreadContext context, boolean lastWasSuccessful) {

        while (!interrupted) {

            if (dsm.getTransitionsFromCurrentState().size() <= 0) {
                if (dsm.getCurrentState().getTraversalState() == TraversalStateType.GOAL) {
                    solutionStore.newSolution(context);
                }
                logger.debug("Reached end of design space.");
                return null;
            }

            Collection<? extends ITransition> transitions = dsm.getTransitionsFromCurrentState(filterOptions);

            if (transitions.size() <= 0) {
                logger.debug("No more transitions to try.");
                state = HillClimbingStrategyState.COMPARE_AND_STEP;
            }

            if (state == HillClimbingStrategyState.TRY_AND_SAVE) {
                ITransition transition;
                if (percentOfOpenedStates >= 1) {
                    transition = transitions.iterator().next();
                } else {
                    if (percentOfOpenedStates <= triedTransitions / dsm.getTransitionsFromCurrentState().size()) {
                        state = HillClimbingStrategyState.COMPARE_AND_STEP;
                        continue;
                    } else {
                        int index = rnd.nextInt(transitions.size());
                        Iterator<? extends ITransition> iterator = transitions.iterator();
                        while (iterator.hasNext() && index != 0) {
                            index--;
                            iterator.next();
                        }
                        transition = iterator.next();
                    }
                }
                logger.debug("Trying " + transition.getId());
                return transition;
            } else {
                logger.debug("Comparing fitnesses.");

                TrajectoryFitness bestTrajectoryFitness = objectiveComparatorHelper.getRandomBest();
                objectiveComparatorHelper.clearTrajectoryFitnesses();
                
                int compare = objectiveComparatorHelper.compare(bestFitness, bestTrajectoryFitness.fitness);
                
                if (compare >= 0) {
                    context.calculateFitness();
                    solutionStore.newSolution(context);
                    logger.debug(dsm.getTrajectoryInfo().toString());
                    return null;
                }
                else {
                    bestFitness = bestTrajectoryFitness.fitness;
                }

                triedTransitions = 0;
                state = HillClimbingStrategyState.TRY_AND_SAVE;

                ITransition bestTransition = bestTrajectoryFitness.trajectory[0];
                logger.debug("Best transition: " + bestTransition.getId() + " with fitness " + bestFitness);
                dsm.fireActivation(bestTransition);
            }

        }

        return null;
    }

    @Override
    public void newStateIsProcessed(ThreadContext context, boolean isAlreadyTraversed, Fitness fitness,
            boolean constraintsNotSatisfied) {

        if (dsm.getTrajectoryInfo().getDepthFromRoot() == 0) {
            bestFitness = fitness;
            logger.debug("Fitness of the root state: " + fitness);
            return;
        }
        
        if (state == HillClimbingStrategyState.TRY_AND_SAVE) {
            if (!constraintsNotSatisfied) {
                logger.debug("Fitness of last transformation: " + fitness);
                objectiveComparatorHelper.addTrajectoryFitness(new TrajectoryFitness(dsm.getTrajectoryInfo().getLastTransition(), fitness));
            } else {
                logger.debug("Global constraints are unsatisfied.");
            }
            dsm.undoLastTransformation();
        }

    }

    @Override
    public void interrupted(ThreadContext context) {
        interrupted = true;
    }

}
