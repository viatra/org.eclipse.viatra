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
import org.eclipse.viatra.dse.api.strategy.impl.CheckAllConstraints;
import org.eclipse.viatra.dse.api.strategy.impl.CheckAllGoals;
import org.eclipse.viatra.dse.api.strategy.impl.ConfigurableSoultionFound;
import org.eclipse.viatra.dse.api.strategy.interfaces.ICheckConstraints;
import org.eclipse.viatra.dse.api.strategy.interfaces.ICheckGoalState;
import org.eclipse.viatra.dse.api.strategy.interfaces.INextTransition;
import org.eclipse.viatra.dse.api.strategy.interfaces.ISolutionFound;
import org.eclipse.viatra.dse.api.strategy.interfaces.ISolutionFound.ExecutationType;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.ITransition;

public class StrategyBase {

    private ICheckConstraints constraintsChecker;
    private ICheckGoalState goalStateChecker;
    private INextTransition iNextTransition;

    private ISolutionFound solutionFoundHandler;

    public StrategyBase(INextTransition iNextTransition) {
        this.iNextTransition = iNextTransition;
        constraintsChecker = new CheckAllConstraints();
        goalStateChecker = new CheckAllGoals();
        solutionFoundHandler = new ConfigurableSoultionFound(1);
    }

    public void setConstraintsChecker(ICheckConstraints iCheckConstraints) {
        this.constraintsChecker = iCheckConstraints;
    }

    public void setGoalStateChecker(ICheckGoalState iCheckGoalState) {
        this.goalStateChecker = iCheckGoalState;
    }

    public ICheckConstraints getConstraintsChecker() {
        return constraintsChecker;
    }

    public ICheckGoalState getGoalStateChecker() {
        return goalStateChecker;
    }

    public void setSolutionFoundHandler(ISolutionFound solutionFoundHandler) {
        this.solutionFoundHandler = solutionFoundHandler;
    }

    public ISolutionFound getSolutionFoundHandler() {
        return solutionFoundHandler;
    }

    /**
     * Delegates the call to {@link ICheckConstraints#checkConstraints(ThreadContext)}.
     * 
     * @see ICheckConstraints#checkConstraints(ThreadContext)
     */
    public boolean checkConstraints(ThreadContext context) {
        return constraintsChecker.checkConstraints(context);
    }

    /**
     * Delegates the call to {@link ICheckGoalState#isGoalState(ThreadContext)}.
     * 
     * @see ICheckGoalState#isGoalState(ThreadContext)
     */
    public Map<String, Double> isGoalState(ThreadContext context) {
        return goalStateChecker.isGoalState(context);
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
        return solutionFoundHandler.solutionFound(context, solution);
    }

}
