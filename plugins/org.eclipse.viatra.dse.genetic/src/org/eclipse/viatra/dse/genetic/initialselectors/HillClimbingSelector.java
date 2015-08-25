/*******************************************************************************
 * Copyright (c) 2010-2015, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.genetic.initialselectors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.genetic.interfaces.IStoreChild;
import org.eclipse.viatra.dse.genetic.interfaces.InitialPopulationSelector;
import org.eclipse.viatra.dse.objectives.Fitness;
import org.eclipse.viatra.dse.objectives.ObjectiveComparatorHelper;

public class HillClimbingSelector extends InitialPopulationSelector {

    private ThreadContext context;
    private DesignSpaceManager dsm;
    private IStoreChild store;
    private ObjectiveComparatorHelper objectiveComparatorHelper;
    private Fitness prevFitness;

    private int initialSizeOfPopulation;
    private int foundInstances = 0;
    private int trialsToMake = 1;

    private boolean transformationPhase = true;
    private List<ITransition> triedTransitions = new ArrayList<ITransition>();
    private Logger logger = Logger.getLogger(this.getClass());
    private Random random = new Random();
    private boolean isInterrupted = false;
    private ITransition lastTransition;

    public HillClimbingSelector withTrialsToMake(int trialsToMake) {
        this.trialsToMake = trialsToMake;
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
        objectiveComparatorHelper = context.getObjectiveComparatorHelper();
        prevFitness = context.calculateFitness();
    }

    @Override
    public ITransition getNextTransition(boolean lastWasSuccessful) {

        while (!(isInterrupted || foundInstances >= initialSizeOfPopulation)) {

            if (transformationPhase) {
                transformationPhase = false;
                Collection<? extends ITransition> allTransitions = dsm.getTransitionsFromCurrentState();
                ArrayList<ITransition> transitions = new ArrayList<ITransition>(allTransitions.size());
                for (ITransition iTransition : allTransitions) {
                    transitions.add(iTransition);
                }
                transitions.removeAll(triedTransitions);
                int index = random.nextInt(transitions.size());
                lastTransition = transitions.get(index);
                
                if (lastTransition.isAssignedToFire()) {
                    dsm.fireActivation(lastTransition);
                } else {
                    return lastTransition;
                }
            } else {
                transformationPhase = true;
                Fitness fitness = context.calculateFitness();
                
                int compareResult = objectiveComparatorHelper.compare(fitness, prevFitness);
                
                if (compareResult == -1) {
                    // prev was better than the new one
                    triedTransitions.add(lastTransition);
                    dsm.undoLastTransformation();
                    int tiredTrans = triedTransitions.size();
                    int availableTrans = dsm.getTransitionsFromCurrentState().size();
                    if (trialsToMake <= tiredTrans || availableTrans <= tiredTrans) {
                        foundInstances++;
                        store.addChild(context);
                        while (dsm.undoLastTransformation());
                        triedTransitions.clear();
                        prevFitness = context.calculateFitness();
                    }
                }
                else {
                    triedTransitions.clear();
                    prevFitness = fitness;
                }
            }
        }
        return null;
    }

    @Override
    public void newStateIsProcessed(boolean isAlreadyTraversed, Fitness fitness, boolean constraintsNotSatisfied) {
    }

    @Override
    public void interrupted() {
        isInterrupted = true;
    }

}
