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
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.incquery.runtime.localsearch.matcher.ILocalSearchAdapter;
import org.eclipse.incquery.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.incquery.runtime.localsearch.matcher.LocalSearchMatcher;
import org.eclipse.incquery.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.incquery.runtime.localsearch.operations.check.BinaryTransitiveClosureCheck;
import org.eclipse.incquery.runtime.localsearch.operations.check.CountCheck;
import org.eclipse.incquery.runtime.localsearch.operations.check.NACOperation;
import org.eclipse.incquery.runtime.localsearch.operations.extend.CountOperation;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

/**
 * A search plan executor is used to execute {@link SearchPlan} instances.
 */
public class SearchPlanExecutor {

    private int currentOperation;
    SearchPlan plan;
    private List<ISearchOperation> operations;
    private ISearchContext context;
    private Set<ILocalSearchAdapter> adapters = Sets.newHashSet();

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
            adapter.planStarted(this);
        }
    } 

    private void planFinished() {
        for (ILocalSearchAdapter adapter : adapters) {
            adapter.planFinished(this);
        }
    }

    private void init(MatchingFrame frame) throws LocalSearchException {
        if (currentOperation == -1) {
            currentOperation++;
            ISearchOperation operation = operations.get(currentOperation);
			operation.onInitialize(frame, context);
			addAdaptersWhenNeeded(operation);
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
		/* default generated stub */
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
                    ISearchOperation operation = operations.get(currentOperation);
					operation.onInitialize(frame, context);
					addAdaptersWhenNeeded(operation);
                }
            } else {
                operationExecuted(frame);
                ISearchOperation operation = operations.get(currentOperation);
				operation.onBacktrack(frame, context);
                removeAdaptersWhenNeeded(operation);
				currentOperation--;
            }
            operationSelected(frame);
        }
        planFinished();
        return (currentOperation > upperBound);
    }
    
    public void resetPlan() {
    	currentOperation = -1;
    }
    
    public void printDebugInformation() {
    	for (int i = 0; i < operations.size(); i++) {
    		Logger.getRootLogger().debug("[" + i + "]\t" + operations.get(i).toString());
    	}
    }

    private void addAdaptersWhenNeeded(ISearchOperation currentSearchOperation) {
		LocalSearchMatcher calledMatcher = getCalledMatcherOfSearchOperation(currentSearchOperation);
		if(calledMatcher != null){
			for (ILocalSearchAdapter adapter : adapters) {
				calledMatcher.addAdapter(adapter);
			}
		}
	}

	private void removeAdaptersWhenNeeded(ISearchOperation currentSearchOperation) {
		LocalSearchMatcher calledMatcher = getCalledMatcherOfSearchOperation(currentSearchOperation);
		if(calledMatcher != null){
			for (ILocalSearchAdapter adapter : adapters) {
				calledMatcher.removeAdapter(adapter);
			}
		}
	}

	private LocalSearchMatcher getCalledMatcherOfSearchOperation(ISearchOperation currentSearchOperation) {
		LocalSearchMatcher calledMatcher = null;
		if (currentSearchOperation instanceof NACOperation) {
			calledMatcher = ((NACOperation) currentSearchOperation).getCalledMatcher();
		} else if (currentSearchOperation instanceof BinaryTransitiveClosureCheck) {
			calledMatcher = ((BinaryTransitiveClosureCheck) currentSearchOperation).getCalledMatcher();
		} else if (currentSearchOperation instanceof CountOperation) {
			calledMatcher = ((CountOperation) currentSearchOperation).getCalledMatcher();
		} else if (currentSearchOperation instanceof CountCheck) {
			calledMatcher = ((CountCheck) currentSearchOperation).getCalledMatcher();
		}
		return calledMatcher;
	}
    
    private void operationExecuted(MatchingFrame frame) {
        for (ILocalSearchAdapter adapter : adapters) {
            adapter.operationExecuted(this, frame);
        }
    }
    
    private void operationSelected(MatchingFrame frame) {
        for (ILocalSearchAdapter adapter : adapters) {
            adapter.operationSelected(this, frame);
        }
    }
    

}
