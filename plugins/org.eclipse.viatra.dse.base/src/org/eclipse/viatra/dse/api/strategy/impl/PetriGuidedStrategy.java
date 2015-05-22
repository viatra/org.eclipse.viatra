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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.viatra.dse.api.DSETransformationRule;
import org.eclipse.viatra.dse.api.strategy.interfaces.LocalSearchStrategyBase;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.IGetCertainTransitions.FilterOptions;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.objectives.Fitness;

public class PetriGuidedStrategy implements LocalSearchStrategyBase {

    private List<DSETransformationRule<?, ?>> petriTrajectory;
    private int actIndex = 0;
    private List<Integer> trajectoyIndexes = new ArrayList<Integer>();
    private BreadthFirstStrategy breadthFirstSearch;
    private boolean lastWasPetriTurn = true;
    private boolean isInterrupted = false;
    private FilterOptions filterOptions;
    private ThreadContext context;

    @Override
    public void init(ThreadContext context) {
        this.context = context;
        filterOptions = new FilterOptions().nothingIfCut().nothingIfGoal().untraversedOnly();
    }

    @Override
    public ITransition getNextTransition(boolean lastWasSuccesful) {

        if (isInterrupted) {
            return null;
        }

        // get important variables from context
        DesignSpaceManager dsm = context.getDesignSpaceManager();
        if (petriTrajectory == null) {
            petriTrajectory = context.getGuidance().getPetriNetAbstractionResult().getSolutions().get(0)
                    .getTrajectory();
        }
        Collection<? extends ITransition> transitions = dsm.getTransitionsFromCurrentState(filterOptions);

        // backtrack if there is no more unfired transition from here
        // don't backtrack if breadth first search is running
        while (lastWasPetriTurn && (transitions == null || transitions.isEmpty())) {
            if (!dsm.undoLastTransformation()) {
                return null;
            }

            // update data for this search
            int undoneTransitionsDepth = dsm.getTrajectoryInfo().getDepthFromCrawlerRoot() + 1;
            Integer lastOccurrenceOfPetriTransition = trajectoyIndexes.get(trajectoyIndexes.size() - 1);

            if (lastOccurrenceOfPetriTransition.equals(undoneTransitionsDepth)) {
                actIndex--;
                trajectoyIndexes.remove(trajectoyIndexes.size() - 1);
            }

            transitions = dsm.getTransitionsFromCurrentState(filterOptions);
        }

        DSETransformationRule<?, ?> nextPetriRule = petriTrajectory.get(actIndex);

        for (ITransition t : transitions) {
            if (t.getTransitionMetaData().rule.equals(nextPetriRule)) {
                actIndex++;
                trajectoyIndexes.add(dsm.getTrajectoryInfo().getDepthFromCrawlerRoot() + 1);
                lastWasPetriTurn = true;
                breadthFirstSearch = new BreadthFirstStrategy();
                return t;
            }
        }
        lastWasPetriTurn = false;

        return breadthFirstSearch.getNextTransition(lastWasSuccesful);
    }

    @Override
    public void newStateIsProcessed(boolean isAlreadyTraversed, Fitness fitness, boolean constraintsNotSatisfied) {
    }

    @Override
    public void interrupted() {
        isInterrupted = true;
    }
}
