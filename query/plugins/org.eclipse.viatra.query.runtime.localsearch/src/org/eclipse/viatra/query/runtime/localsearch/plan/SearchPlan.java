/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;

/**
 * A SearchPlan stores a collection of SearchPlanOperations for a fixed order of variables.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class SearchPlan {

    private final List<ISearchOperation> operations;
    private final Map<Integer, PVariable> variableMapping;
    private final TupleMask parameterMask;
    private final PBody body;
    
    /**
     * @since 2.0
     */
    public SearchPlan(PBody body, List<ISearchOperation> operations, TupleMask parameterMask, Map<PVariable, Integer> variableMapping) {
        this.body = body;
        this.operations = Collections.unmodifiableList(new ArrayList<>(operations));
        this.parameterMask = parameterMask;
        this.variableMapping = Collections.unmodifiableMap(variableMapping.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getValue(), entry -> entry.getKey())));
    }


    /**
     * Returns an immutable list of operations stored in the plan.
     * @return the operations
     */
    public List<ISearchOperation> getOperations() {
        return operations;
    }

    /**
     * Returns an immutable map of variable mappings for the plan
     * @since 2.0
     */
    public Map<Integer, PVariable> getVariableMapping() {
        return variableMapping;
    }

    /**
     * Returns the index of a given operation in the plan
     * @since 2.0
     */
    public int getOperationIndex(ISearchOperation operation) {
        return operations.indexOf(operation);
    }

    /**
     * @since 2.0
     */
    public TupleMask getParameterMask() {
        return parameterMask;
    }
    
    /**
     * @since 2.0
     */
    public PBody getSourceBody() {
        return body;
    }

}
