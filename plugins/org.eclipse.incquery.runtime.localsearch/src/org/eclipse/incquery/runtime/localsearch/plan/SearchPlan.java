/*******************************************************************************
 * Copyright (c) 2004-2008 Akos Horvath, Gergely Varro and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Akos Horvath, Gergely Varro - initial API and implementation
 *    Zoltan Ujhelyi - adaptation to EMF-IncQuery based engine
 *******************************************************************************/

 package org.eclipse.incquery.runtime.localsearch.plan;


import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.incquery.runtime.localsearch.operations.ISearchOperation;

/**
 * A SearchPlan is an unmodifiable array of 
 * SearchPlanOperations. PatternSignatures are supposed
 * to be fixed 
 */
public class SearchPlan {
    private int currentOperation;

    private ISearchOperation[] operations;
	
    public SearchPlan(ISearchOperation[] operations) {
		this.operations = operations;
        this.currentOperation = -1;
	}
    
    private void init(MatchingFrame frame) throws LocalSearchException {
        if (currentOperation == -1) {
            currentOperation++;
            operations[currentOperation].onInitialize(frame);
        } else if (currentOperation == operations.length) {
            currentOperation--;
        } else {
            throw new LocalSearchException(LocalSearchException.PLAN_EXECUTION_ERROR);
        }
    }
	
	/**
     * Calculates the cost of the search plan.
	 */
	public double cost() {
		/* default generated stub */;
		return 0.0;
	}

    public boolean execute(MatchingFrame frame) throws LocalSearchException {
        int upperBound = operations.length - 1;
        init(frame);
        while (currentOperation >= 0 && currentOperation <= upperBound) {
            if (operations[currentOperation].execute(frame)) {
                currentOperation++;
                if (currentOperation <= upperBound) {
                    operations[currentOperation].onInitialize(frame);
                }
            } else {
                operations[currentOperation].onBacktrack(frame);
                currentOperation--;
            }
        }
        return (currentOperation > upperBound);
    }
    
    public void printDebugInformation() {
        for (int i = 0; i < operations.length; i++) {
            System.out.println("[" + i + "]\t" + operations[i].toString());
        }
    }
}
