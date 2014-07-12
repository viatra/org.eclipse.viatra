/*******************************************************************************
 * Copyright (c) 2004-2008 Akos Horvath, Gergely Varro and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Akos Horvath, Gergely Varro - initial API and implementation from the VIATRA2 project
 *    Zoltan Ujhelyi - adaptation to EMF-IncQuery based engine
 *******************************************************************************/

 package org.eclipse.incquery.runtime.localsearch.plan;


import java.util.List;

import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.incquery.runtime.localsearch.matcher.ILocalSearchAdapter;
import org.eclipse.incquery.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.incquery.runtime.localsearch.operations.ISearchOperation;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * A search plan executor is used to execute {@link SearchPlan} instances.
 */
public class SearchPlanExecutor {

    private int currentOperation;
    SearchPlan plan;
    private List<ISearchOperation> operations;
    private ISearchContext context;
    private List<ILocalSearchAdapter> adapters = Lists.newLinkedList();

    public int getCurrentOperation() {
        return currentOperation;
    }
    
    public SearchPlan getSearchPlan() {
        return plan;
    }
    
    public void addAdapters(List<ILocalSearchAdapter> adapter) {
        this.adapters.addAll(adapter);
    }

    public void removeAdapters(List<ILocalSearchAdapter> adapter) {
        this.adapters.removeAll(adapter);
    }

    public SearchPlanExecutor(SearchPlan plan, ISearchContext context) {
        Preconditions.checkArgument(context != null, "Context cannot be null");
        this.plan = plan;
        this.context = context;
        operations = plan.getOperations();
        this.currentOperation = -1;
	}
    
    private void planStarted() {
        for (ILocalSearchAdapter adapter : adapters) {
            adapter.planStarted(plan, currentOperation);
        }
    }

    private void planFinished() {
        for (ILocalSearchAdapter adapter : adapters) {
            adapter.planFinished(plan, currentOperation);
        }
    }

    private void init(MatchingFrame frame) throws LocalSearchException {
        if (currentOperation == -1) {
            currentOperation++;
            operations.get(currentOperation).onInitialize(frame, context);
        } else if (currentOperation == operations.size()) {
            currentOperation--;
        } else {
            throw new LocalSearchException(LocalSearchException.PLAN_EXECUTION_ERROR);
        }
        operationSelected(frame);
    }


    /**
     * Calculates the cost of the search plan.
	 */
	public double cost() {
		/* default generated stub */;
		return 0.0;
	}

    public boolean execute(MatchingFrame frame) throws LocalSearchException {
        planStarted();
        int upperBound = operations.size() - 1;
        init(frame);
        while (currentOperation >= 0 && currentOperation <= upperBound) {
            if (operations.get(currentOperation).execute(frame, context)) {
                operationExecuted(frame);
                currentOperation++;
                if (currentOperation <= upperBound) {
                    operations.get(currentOperation).onInitialize(frame, context);
                }
            } else {
                operationExecuted(frame);
                operations.get(currentOperation).onBacktrack(frame, context);
                currentOperation--;
            }
            operationSelected(frame);
        }
        planFinished();
        return (currentOperation > upperBound);
    }
    
    private void operationExecuted(MatchingFrame frame) {
        for (ILocalSearchAdapter adapter : adapters) {
            adapter.operationExecuted(this, currentOperation, frame);
        }
    }
    
    private void operationSelected(MatchingFrame frame) {
        for (ILocalSearchAdapter adapter : adapters) {
            adapter.operationSelected(this, currentOperation, frame);
        }
    }
    
    public void resetPlan() {
        currentOperation = -1;
    }

    public void printDebugInformation() {
        for (int i = 0; i < operations.size(); i++) {
            System.out.println("[" + i + "]\t" + operations.get(i).toString());
        }
    }

}
