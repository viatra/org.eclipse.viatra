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

import org.eclipse.viatra.dse.api.strategy.impl.CheckAllConstraints;
import org.eclipse.viatra.dse.api.strategy.interfaces.ICheckConstraints;
import org.eclipse.viatra.dse.api.strategy.interfaces.INextTransition;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.objectives.ObjectiveValuesMap;

public class Strategy {

    private ICheckConstraints constraintsChecker;
    private INextTransition iNextTransition;

    public Strategy(INextTransition iNextTransition) {
        this.iNextTransition = iNextTransition;
        constraintsChecker = new CheckAllConstraints();
    }

    public void setConstraintsChecker(ICheckConstraints iCheckConstraints) {
        this.constraintsChecker = iCheckConstraints;
    }

    public ICheckConstraints getConstraintsChecker() {
        return constraintsChecker;
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
     * Delegates the call to {@link INextTransition#getNextTransition(ThreadContext)}.
     * 
     * @see INextTransition#getNextTransition(ThreadContext)
     */
    public ITransition getNextTransition(ThreadContext context, boolean lastWasSuccessful) {
        return iNextTransition.getNextTransition(context, lastWasSuccessful);
    }

    /**
     * Delegates the call to {@link INextTransition#init(ThreadContext)}.
     * 
     * @see INextTransition#init(ThreadContext)
     */
    public void initINextTransition(ThreadContext context) {
        iNextTransition.init(context);
    }

    /**
     * Delegates the call to {@link INextTransition#newStateIsProcessed(ThreadContext, boolean, boolean, boolean)}.
     * 
     * @see INextTransition#newStateIsProcessed(ThreadContext, boolean, boolean, boolean)
     */
    public void newStateIsProcessed(ThreadContext context, boolean isAlreadyTraversed, ObjectiveValuesMap objectives,
            boolean areConstraintsSatisfied) {
        iNextTransition.newStateIsProcessed(context, isAlreadyTraversed, objectives, areConstraintsSatisfied);
    }

    /**
     * Delegates the call to {@link INextTransition#interrupted(ThreadContext)}.
     * 
     * @see INextTransition#interrupted(ThreadContext)
     */
    public void interrupted(ThreadContext context) {
        iNextTransition.interrupted(context);
    }

}
