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


import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ILocalSearchAdaptable;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ILocalSearchAdapter;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.matchers.ViatraQueryRuntimeException;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;

/**
 * A search plan executor is used to execute {@link SearchPlan} instances.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class SearchPlanExecutor implements ILocalSearchAdaptable{

    private int currentOperation;
    SearchPlan plan;
    private List<ISearchOperation> operations;
    private final ISearchContext context;
    private final List<ILocalSearchAdapter> adapters = Lists.newCopyOnWriteArrayList();
    private final BiMap<Integer,PVariable> variableMapping;
    private final TupleMask parameterMask;

    public BiMap<Integer, PVariable> getVariableMapping() {
        return variableMapping;
    }

    public int getCurrentOperation() {
        return currentOperation;
    }
    
    public SearchPlan getSearchPlan() {
        return plan;
    }
    
    /**
     * @since 1.7
     */
    public TupleMask getParameterMask() {
        return parameterMask;
    }

    @Override
    public void addAdapters(List<ILocalSearchAdapter> adapters) {
        for(ILocalSearchAdapter adapter : adapters){
            if (!this.adapters.contains(adapter)){
                this.adapters.add(adapter);
                adapter.adapterRegistered(this);
            }
        }
    }

    @Override
    public void removeAdapters(List<ILocalSearchAdapter> adapters) {
        for (ILocalSearchAdapter adapter : adapters) {
            if (this.adapters.remove(adapter)){
                adapter.adapterUnregistered(this);
            }
        }
    }

    /**
     * @since 1.7
     */
    public SearchPlanExecutor(SearchPlan plan, ISearchContext context, Map<PVariable, Integer> variableMapping, TupleMask parameterMask) {
        Preconditions.checkArgument(context != null, "Context cannot be null");
        this.plan = plan;
        this.context = context;
        this.variableMapping = HashBiMap.<PVariable, Integer>create(variableMapping).inverse();
        operations = plan.getOperations();
        this.currentOperation = -1;
        this.parameterMask = parameterMask;
    }
   

    private void init(MatchingFrame frame) {
        if (currentOperation == -1) {
            currentOperation++;
            ISearchOperation operation = operations.get(currentOperation);
            if (!adapters.isEmpty()){
                for (ILocalSearchAdapter adapter : adapters) {
                    adapter.executorInitializing(this,frame);
                }
            }
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

    /**
     * @throws ViatraQueryRuntimeException
     */
    public boolean execute(MatchingFrame frame) {
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
                    operation.onInitialize(frame, context);
                }
            } else {
                operationExecuted(frame);
                ISearchOperation operation = operations.get(currentOperation);
                operation.onBacktrack(frame, context);
                currentOperation--;
                operationSelected(frame);
            }
        }
        boolean matchFound = currentOperation > upperBound;
        if (matchFound && !adapters.isEmpty()) {
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
    
    private void operationExecuted(MatchingFrame frame) {
        if (!adapters.isEmpty()){
            for (ILocalSearchAdapter adapter : adapters) {
                adapter.operationExecuted(this, frame);
            }
        }
    }
    
    private void operationSelected(MatchingFrame frame) {
        if (!adapters.isEmpty()){
            for (ILocalSearchAdapter adapter : adapters) {
                adapter.operationSelected(this, frame);
            }
        }
    }

    public ISearchContext getContext() {
        return context;
    }

    @Override
    public List<ILocalSearchAdapter> getAdapters() {
        return Collections.<ILocalSearchAdapter>unmodifiableList(this.adapters);
    }

    @Override
    public void addAdapter(ILocalSearchAdapter adapter) {
        addAdapters(Collections.singletonList(adapter));
    }

    @Override
    public void removeAdapter(ILocalSearchAdapter adapter) {
        removeAdapters(Collections.singletonList(adapter));
    }

    @Override
    public String toString() {
        if (operations == null) {
            return "Unspecified plan";
        } else {
            return Joiner.on("\n").join(operations);
        }
    }

}
