/*******************************************************************************
 * Copyright (c) 2004-2008 Akos Horvath, Gergely Varro and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Akos Horvath, Gergely Varro - initial API and implementation from the VIATRA2 project
 *    Zoltan Ujhelyi - adaptation to VIATRA Query based engine
 *******************************************************************************/

 package org.eclipse.viatra.query.runtime.localsearch.plan;


import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ILocalSearchAdapter;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.matcher.LocalSearchMatcher;
import org.eclipse.viatra.query.runtime.localsearch.operations.IMatcherBasedOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
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
	private BiMap<Integer,PVariable> variableMapping;

	public BiMap<Integer, PVariable> getVariableMapping() {
		return variableMapping;
	}

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

    public SearchPlanExecutor(SearchPlan plan, ISearchContext context, Map<PVariable, Integer> variableMapping) {
		Preconditions.checkArgument(context != null, "Context cannot be null");
        this.plan = plan;
        this.context = context;
        HashBiMap<PVariable, Integer> tmpMapping = HashBiMap.create();
        tmpMapping.putAll(variableMapping);
        this.variableMapping = tmpMapping.inverse();
        operations = plan.getOperations();
        this.currentOperation = -1;
	}
   

    private void init(MatchingFrame frame) throws LocalSearchException {
    	if (currentOperation == -1) {
            currentOperation++;
            ISearchOperation operation = operations.get(currentOperation);
            for (ILocalSearchAdapter adapter : adapters) {
            	adapter.executorInitializing(this,frame);
            }
            addAdaptersWhenNeeded(operation,frame);
			operation.onInitialize(frame, context);
        } else if (currentOperation == operations.size()) {
            currentOperation--;
        } else {
            throw new LocalSearchException(LocalSearchException.PLAN_EXECUTION_ERROR);
        }
    }


    /**
     * Calculates the cost of the search plan.
	 */
	public double cost() {
		/* default generated stub */
		return 0.0;
	}

    public boolean execute(MatchingFrame frame) throws LocalSearchException {
        int upperBound = operations.size() - 1;
        init(frame);
        operationSelected(frame);
        while (currentOperation >= 0 && currentOperation <= upperBound) {
            if (operations.get(currentOperation).execute(frame, context)) {
                operationExecuted(frame);
                currentOperation++;
                operationSelected(frame);
                if (currentOperation <= upperBound) {
                    ISearchOperation operation = operations.get(currentOperation);
                    addAdaptersWhenNeeded(operation,frame);
					operation.onInitialize(frame, context);
                }
            } else {
                operationExecuted(frame);
                ISearchOperation operation = operations.get(currentOperation);
				operation.onBacktrack(frame, context);
                removeAdaptersWhenNeeded(operation);
				currentOperation--;
				operationSelected(frame);
            }
        }
        boolean matchFound = currentOperation > upperBound;
        if( matchFound ){
        	for (ILocalSearchAdapter adapter : adapters) {
				adapter.matchFound(this, frame);
			}
        }
		return matchFound;
    }
    
    public void resetPlan() {
    	currentOperation = -1;
    }
    
    public void printDebugInformation() {
    	for (int i = 0; i < operations.size(); i++) {
    		Logger.getRootLogger().debug("[" + i + "]\t" + operations.get(i).toString());
    	}
    }

    private void addAdaptersWhenNeeded(ISearchOperation currentSearchOperation, MatchingFrame frame) {
		LocalSearchMatcher calledMatcher = null;
		if (currentSearchOperation instanceof IMatcherBasedOperation) {
			calledMatcher = ((IMatcherBasedOperation) currentSearchOperation).getAndPrepareCalledMatcher(frame, context);
		}
		if(calledMatcher != null){
			for (ILocalSearchAdapter adapter : adapters) {
				calledMatcher.addAdapter(adapter);
			}
		}
	}

	private void removeAdaptersWhenNeeded(ISearchOperation currentSearchOperation) {
		LocalSearchMatcher calledMatcher = null;
		if(currentSearchOperation instanceof IMatcherBasedOperation){
			calledMatcher = ((IMatcherBasedOperation) currentSearchOperation).getCalledMatcher();
		}
		if(calledMatcher != null){
			for (ILocalSearchAdapter adapter : adapters) {
				calledMatcher.removeAdapter(adapter);
			}
		}
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

	public ISearchContext getContext() {
		return context;
	}

}
