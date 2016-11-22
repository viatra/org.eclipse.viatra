/*******************************************************************************
 * Copyright (c) 2010-2016, Grill Balázs, IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.profiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ILocalSearchAdaptable;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ILocalSearchAdapter;
import org.eclipse.viatra.query.runtime.localsearch.matcher.LocalSearchMatcher;
import org.eclipse.viatra.query.runtime.localsearch.matcher.MatcherReference;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.localsearch.plan.SearchPlanExecutor;

import com.google.common.collect.ImmutableList;

/**
 * This is a simple {@link ILocalSearchAdapter} which capable of counting
 * each search operation execution then printing it in human readably form 
 * (along with the executed plans) using {@link #toString()}
 * @author Grill Balázs
 * @since 1.5
 *
 */
public class LocalSearchProfilerAdapter implements ILocalSearchAdapter {

    private final Map<MatcherReference, PlanProfile> profile = new HashMap<MatcherReference, PlanProfile>();
    
    private final Map<SearchPlanExecutor, int[]> currentBodies = new HashMap<SearchPlanExecutor, int[]>();
    
    private class PlanProfile{
        
        final int[][] bodies;
        final ArrayList<List<ISearchOperation>> operations;
        
        public PlanProfile(LocalSearchMatcher lsMatcher) {
            ImmutableList<SearchPlanExecutor> plan = lsMatcher.getPlan();
            bodies = new int[plan.size()][];
            operations = new ArrayList<List<ISearchOperation>>(plan.size());
            for(int i=0;i<bodies.length;i++){
                List<ISearchOperation> ops = plan.get(i).getSearchPlan().getOperations();
                operations.add(i,ops); 
                bodies[i]=new int[ops.size()];
            }
        }
        
        public void register(LocalSearchMatcher lsMatcher){
            ImmutableList<SearchPlanExecutor> plan = lsMatcher.getPlan();
            for(int i=0;i<bodies.length;i++){
                currentBodies.put(plan.get(i), bodies[i]);
            }
        }
        
        public void unRegister(LocalSearchMatcher lsMatcher){
            for(SearchPlanExecutor executor : lsMatcher.getPlan()){
                currentBodies.remove(executor);
            }
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            for(int i=0;i<bodies.length;i++){
                sb.append("\tbody #");sb.append(i);sb.append("(\n");
                for(int j=0;j<operations.get(i).size();j++){
                    sb.append("\t\t");sb.append(bodies[i][j]);
                    sb.append("\t");sb.append(operations.get(i).get(j));
                    sb.append("\n");
                }
                sb.append("\t)\n");
            }
            sb.append("}\n");
            return sb.toString();
        }
        
    }
    
    @Override
    public void adapterRegistered(ILocalSearchAdaptable adaptable) {
    }

    @Override
    public void adapterUnregistered(ILocalSearchAdaptable adaptable) {
    }

    @Override
    public void patternMatchingStarted(LocalSearchMatcher lsMatcher) {
        MatcherReference key = new MatcherReference(lsMatcher.getPlanDescriptor().getQuery(), lsMatcher.getPlanDescriptor().getAdornment());
        PlanProfile pp = profile.get(key);
        if (pp == null){
            pp = new PlanProfile(lsMatcher);
            profile.put(key, pp);
        }
        pp.register(lsMatcher);
    }

    @Override
    public void patternMatchingFinished(LocalSearchMatcher lsMatcher) {
        MatcherReference key = new MatcherReference(lsMatcher.getPlanDescriptor().getQuery(), lsMatcher.getPlanDescriptor().getAdornment());
        PlanProfile pp = profile.get(key);
        if (pp != null){
            pp.unRegister(lsMatcher);
        }
    }

    @Override
    public void planChanged(SearchPlanExecutor oldPlanExecutor, SearchPlanExecutor newPlanExecutor) {
    }

    @Override
    public void operationSelected(SearchPlanExecutor planExecutor, MatchingFrame frame) {
    }

    @Override
    public void operationExecuted(SearchPlanExecutor planExecutor, MatchingFrame frame) {
        int[] bodyProfile = currentBodies.get(planExecutor);
        bodyProfile[planExecutor.getCurrentOperation()]++;
    }

    @Override
    public void matchFound(SearchPlanExecutor planExecutor, MatchingFrame frame) {
    }

    @Override
    public void executorInitializing(SearchPlanExecutor searchPlanExecutor, MatchingFrame frame) {
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (java.util.Map.Entry<MatcherReference, PlanProfile> entry: profile.entrySet()){
            sb.append(entry.getKey());
            sb.append("\n");
            sb.append(entry.getValue());
        }
        return sb.toString();
    }
    
}
