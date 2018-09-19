/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.plan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.viatra.query.runtime.localsearch.matcher.CallWithAdornment;
import org.eclipse.viatra.query.runtime.localsearch.matcher.MatcherReference;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.matchers.planning.SubPlan;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;

/**
 * This class is responsible for storing the results of the planner and operation compiler for a selected body.
 * @since 2.0
 */
public class SearchPlanForBody {

    private PBody body;
    private Map<PVariable, Integer> variableKeys;
    private int[] parameterKeys;
    private SubPlan plan;
    private List<ISearchOperation> compiledOperations;
    private Collection<CallWithAdornment> dependencies;
    
    public SearchPlanForBody(PBody body, Map<PVariable, Integer> variableKeys,
            SubPlan plan, List<ISearchOperation> compiledOperations, Collection<CallWithAdornment> dependencies) {
        super();
        this.body = body;
        this.variableKeys = variableKeys;
        this.plan = plan;
        List<PVariable> parameters = body.getSymbolicParameterVariables();
        parameterKeys = new int[parameters.size()];
        for (int i=0; i<parameters.size(); i++) {
            parameterKeys[i] = variableKeys.get(parameters.get(i));
        }
        this.compiledOperations = new ArrayList<>(compiledOperations.size()+1);
        this.compiledOperations.addAll(compiledOperations);
        
        this.dependencies = new ArrayList<>(dependencies);
    }

    public PBody getBody() {
        return body;
    }

    public Map<PVariable, Integer> getVariableKeys() {
        return variableKeys;
    }

    public int[] getParameterKeys() {
        return Arrays.copyOf(parameterKeys, parameterKeys.length);
    }

    public List<ISearchOperation> getCompiledOperations() {
        return compiledOperations;
    }

    public SubPlan getPlan() {
        return plan;
    }
    
    public Collection<CallWithAdornment> getDependencies() {
        return dependencies;
    }
    
    public TupleMask calculateParameterMask() {
        return TupleMask.fromSelectedIndices(variableKeys.size(), parameterKeys);
    }
    
    @Override
    public String toString() {
        return compiledOperations.stream().map(Object::toString).collect(Collectors.joining("\n"));
    }
    
}
