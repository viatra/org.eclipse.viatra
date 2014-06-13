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
package org.eclipse.viatra.dse.api.strategy;

import java.util.Map;

import org.eclipse.viatra.dse.api.Solution;
import org.eclipse.viatra.dse.api.strategy.impl.SimpleStrategyComponent;
import org.eclipse.viatra.dse.api.strategy.interfaces.ICheckConstraints;
import org.eclipse.viatra.dse.api.strategy.interfaces.ICheckGoalState;
import org.eclipse.viatra.dse.api.strategy.interfaces.INextTransition;
import org.eclipse.viatra.dse.api.strategy.interfaces.ISolutionFound;
import org.eclipse.viatra.dse.api.strategy.interfaces.ISolutionFound.ExecutationType;
import org.eclipse.viatra.dse.api.strategy.interfaces.ITraversedStateFound;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.IState;
import org.eclipse.viatra.dse.designspace.api.ITransition;

/**
 * This class is a holder class that stores the building blocks of an exploration strategy.
 */
public class StrategyBase {

    private ICheckConstraints iCheckConstraints;
    private ICheckGoalState iCheckGoalState;
    private INextTransition iNextTransition;
    private ISolutionFound iSolutionFound;
    private ITraversedStateFound iTraversedStateFound;

    private final static SimpleStrategyComponent dummy = new SimpleStrategyComponent();

    public StrategyBase(INextTransition iNextTransition) {
        this(dummy, dummy, iNextTransition, dummy, dummy);
    }

    public StrategyBase(INextTransition iNextTransition, ICheckGoalState iCheckGoalState) {
        this(dummy, iCheckGoalState, iNextTransition, dummy, dummy);
    }

    public StrategyBase(INextTransition iNextTransition, ICheckGoalState iCheckGoalState, ISolutionFound iSolutionFound) {
        this(dummy, iCheckGoalState, iNextTransition, iSolutionFound, dummy);
    }

    public StrategyBase(INextTransition iNextTransition, ICheckGoalState iCheckGoalState,
            ISolutionFound iSolutionFound, ICheckConstraints iCheckConstraints) {
        this(iCheckConstraints, iCheckGoalState, iNextTransition, iSolutionFound, dummy);
    }

    public StrategyBase(ICheckConstraints iCheckConstraints, ICheckGoalState iCheckGoalState,
            INextTransition iNextTransition, ISolutionFound iSolutionFound, ITraversedStateFound iTraversedStateFound) {
        this.iCheckConstraints = iCheckConstraints;
        this.iCheckGoalState = iCheckGoalState;
        this.iNextTransition = iNextTransition;
        this.iSolutionFound = iSolutionFound;
        this.iTraversedStateFound = iTraversedStateFound;
    }

    /**
     * Delegates the call to {@link ICheckConstraints#checkConstraints(ThreadContext)}.
     * 
     * @see ICheckConstraints#checkConstraints(ThreadContext)
     */
    public boolean checkConstraints(ThreadContext context) {
        return iCheckConstraints.checkConstraints(context);
    }

    /**
     * Delegates the call to {@link ICheckGoalState#isGoalState(ThreadContext)}.
     * 
     * @see ICheckGoalState#isGoalState(ThreadContext)
     */
    public Map<String, Double> isGoalState(ThreadContext context) {
        return iCheckGoalState.isGoalState(context);
    }

    /**
     * Delegates the call to {@link INextTransition#getNextTransition(ThreadContext)}.
     * 
     * @see INextTransition#getNextTransition(ThreadContext)
     */
    public ITransition getNextTransition(ThreadContext context, boolean lastWasSuccessful) {
        return iNextTransition.getNextTransition(context, lastWasSuccessful);
    }

    /**
     * 
     * Delegates the call to {@link INextTransition#init(ThreadContext)}.
     * 
     * @see INextTransition#init(ThreadContext)
     */
    public void initINextTransition(ThreadContext context) {
        iNextTransition.init(context);
    }

    /**
     * 
     * Delegates the call to {@link INextTransition#newStateIsProcessed(ThreadContext, boolean, boolean, boolean)}.
     * 
     * @see INextTransition#newStateIsProcessed(ThreadContext, boolean, boolean, boolean)
     */
    public void newStateIsProcessed(ThreadContext context, boolean isAlreadyTraversed, boolean isGoalState,
            boolean areConstraintsSatisfied) {
        iNextTransition.newStateIsProcessed(context, isAlreadyTraversed, isGoalState, areConstraintsSatisfied);
    }

    /**
     * Delegates the call to {@link ISolutionFound#solutionFound(ThreadContext, Solution)}.
     * 
     * @see ISolutionFound#solutionFound(ThreadContext, Solution)
     */
    public ExecutationType solutionFound(ThreadContext context, Solution solution) {
        return iSolutionFound.solutionFound(context, solution);
    }

    /**
     * Delegates the call to {@link ITraversedStateFound#traversedStateFound(ThreadContext, TraversalStateType)} .
     * 
     * @see ITraversedStateFound#traversedStateFound(ThreadContext, TraversalStateType)
     */
    public void traversedStateFound(ThreadContext context, IState.TraversalStateType traversalState) {
        iTraversedStateFound.traversedStateFound(context, traversalState);
    }
}
