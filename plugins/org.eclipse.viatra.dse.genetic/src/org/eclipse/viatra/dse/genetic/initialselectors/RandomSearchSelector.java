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

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.genetic.interfaces.IStoreChild;
import org.eclipse.viatra.dse.genetic.interfaces.InitialPopulationSelector;
import org.eclipse.viatra.dse.objectives.Fitness;

public class RandomSearchSelector extends InitialPopulationSelector {

    private ThreadContext context;
    private DesignSpaceManager dsm;
    private IStoreChild store;

    private int initialSizeOfPopulation;
    private int foundInstances = 0;
    private int minDepth;
    private int maxDepth;

    private int lengthRemaining;
    private Logger logger = Logger.getLogger(this.getClass());
    private Random random = new Random();
    private boolean isInterrupted = false;

    public RandomSearchSelector(int minDepth, int maxDepth) {
        this.minDepth = minDepth;
        this.maxDepth = maxDepth;
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
        lengthRemaining = newLength();
    }

    private int newLength() {
        return random.nextInt(maxDepth - minDepth) + minDepth;
    }

    @Override
    public ITransition getNextTransition(boolean lastWasSuccessful) {

        while (!(isInterrupted || foundInstances >= initialSizeOfPopulation)) {

            if (lengthRemaining > 0) {
                lengthRemaining--;

                Collection<? extends ITransition> transitions = dsm.getTransitionsFromCurrentState();
                int index = random.nextInt(transitions.size());
                ITransition transition = getByIndex(transitions, index);
                if (transition.isAssignedToFire()) {
                    dsm.fireActivation(transition);
                } else {
                    return transition;
                }
            } else {

                foundInstances++;
                store.addChild(context);
                while (dsm.undoLastTransformation());
                lengthRemaining = newLength();

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

}
