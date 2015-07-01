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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.DSETransformationRule;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.genetic.interfaces.IStoreChild;
import org.eclipse.viatra.dse.genetic.interfaces.InitialPopulationSelector;
import org.eclipse.viatra.dse.objectives.Fitness;

import com.google.common.collect.Lists;

public class FixedPrioritySelector extends InitialPopulationSelector {

    private DesignSpaceManager dsm;

    private int initialSizeOfPopulation;
    private int foundInstances = 0;
    private Map<Object, Double> bestPriorityInState = new HashMap<Object, Double>();
    private Map<DSETransformationRule<?, ?>, Integer> priorities;

    private IStoreChild store;

    private Random random = new Random();

    private Logger logger = Logger.getLogger(this.getClass());

    private boolean isInterrupted = false;

    private ThreadContext context;

    public FixedPrioritySelector withPriorities(Map<DSETransformationRule<?, ?>, Integer> priorities) {
        this.priorities = priorities;
        return this;
    }

    @Override
    public void setChildStore(IStoreChild store) {
        this.store = store;
    }

    @Override
    public void setPopulationSize(int populationSize) {
        initialSizeOfPopulation = populationSize;
    }

    @Override
    public void init(ThreadContext context) {
        this.context = context;
        if (store == null) {
            throw new DSEException("No IStoreChild is set for the BFSSelector");
        }
        dsm = context.getDesignSpaceManager();
        if (priorities == null) {
            throw new DSEException("Priorities has not been set for this strategy.");
        }
    }

    @Override
    public ITransition getNextTransition(boolean lastWasSuccessful) {

        if (isInterrupted || foundInstances >= initialSizeOfPopulation) {
            return null;
        }

        Collection<? extends ITransition> transitions = dsm.getTransitionsFromCurrentState();
        while (transitions == null || transitions.isEmpty()) {
            if (!dsm.undoLastTransformation()) {
                return null;
            }

            transitions = dsm.getTransitionsFromCurrentState();
        }

        do {

            Double bestPriority = bestPriorityInState.get(dsm.getCurrentState().getId());
            if (bestPriority == null) {
                bestPriority = getBestPriority(dsm.getTransitionsFromCurrentState());
                bestPriorityInState.put(dsm.getCurrentState().getId(), bestPriority);
            }
            List<ITransition> bestTrasitions = Lists.newArrayList();
            for (ITransition iTransition : dsm.getTransitionsFromCurrentState()) {
                if (priorities.get(iTransition.getTransitionMetaData().rule).intValue() == bestPriority) {
                    bestTrasitions.add(iTransition);
                }
            }

            if (!bestTrasitions.isEmpty()) {
                int index = random.nextInt(bestTrasitions.size());
                ITransition iTransition = bestTrasitions.get(index);
                logger.debug("Transition fired: " + iTransition.getId() + " from " + iTransition.getFiredFrom().getId());

                if (iTransition.isAssignedToFire()) {
                    dsm.fireActivation(iTransition);
                    return getNextTransition(true);
                }

                return iTransition;
            }

            logger.debug("Backtrack");

        } while (!dsm.undoLastTransformation());

        return null;
    }

    @Override
    public void newStateIsProcessed(boolean isAlreadyTraversed, Fitness fitness, boolean constraintsNotSatisfied) {
        if (constraintsNotSatisfied) {
            dsm.undoLastTransformation();
        } else if (fitness.isSatisifiesHardObjectives()) {
            foundInstances++;
            logger.debug("Found goal state " + foundInstances + "/" + initialSizeOfPopulation);
            store.addChild(context);
            if (foundInstances < initialSizeOfPopulation) {
                while (dsm.undoLastTransformation());
            }
        }
    }

    @Override
    public void interrupted() {
        isInterrupted = true;
    }

    private double getBestPriority(Collection<? extends ITransition> transitions) {
        int bestPriority;
        bestPriority = Integer.MIN_VALUE;
        for (ITransition iTransition : transitions) {
            int priority = priorities.get(iTransition.getTransitionMetaData().rule).intValue();
            if (priority > bestPriority) {
                bestPriority = priority;
            }
        }
        return bestPriority;
    }

}
