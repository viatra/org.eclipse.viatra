/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.planner.compiler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.localsearch.operations.generic.GenericTypeCheck;
import org.eclipse.viatra.query.runtime.localsearch.operations.generic.GenericTypeExtend;
import org.eclipse.viatra.query.runtime.localsearch.operations.generic.GenericTypeExtendSingleValue;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.TypeFilterConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;

/**
 * @author Zoltan Ujhelyi
 * @since 1.7
 *
 */
public class GenericOperationCompiler extends AbstractOperationCompiler {

    public GenericOperationCompiler(IQueryRuntimeContext runtimeContext) {
        super(runtimeContext);
    }

    @Override
    protected void createCheck(TypeFilterConstraint typeConstraint, Map<PVariable, Integer> variableMapping)
            throws QueryProcessingException {
        IInputKey inputKey = typeConstraint.getInputKey();
        Tuple tuple = typeConstraint.getVariablesTuple();
        int[] positions = new int[tuple.getSize()];
        for (int i = 0; i < tuple.getSize(); i++) {
            PVariable variable = (PVariable) tuple.get(i);
            positions[i] = variableMapping.get(variable);
        }
        operations.add(new GenericTypeCheck(inputKey, positions, TupleMask.fromSelectedIndices(variableMapping.size(), positions)));
        
    }
    
    @Override
    protected void createCheck(TypeConstraint typeConstraint, Map<PVariable, Integer> variableMapping)
            throws QueryProcessingException {
        IInputKey inputKey = typeConstraint.getSupplierKey();
        Tuple tuple = typeConstraint.getVariablesTuple();
        int[] positions = new int[tuple.getSize()];
        for (int i = 0; i < tuple.getSize(); i++) {
            PVariable variable = (PVariable) tuple.get(i);
            positions[i] = variableMapping.get(variable);
        }
        operations.add(new GenericTypeCheck(inputKey, positions, TupleMask.fromSelectedIndices(variableMapping.size(), positions)));
    }
    
    @Override
    protected void createExtend(TypeConstraint typeConstraint, Map<PVariable, Integer> variableMapping) {
        IInputKey inputKey = typeConstraint.getSupplierKey();
        Tuple tuple = typeConstraint.getVariablesTuple();
        
        int[] positions = new int[tuple.getSize()];
        List<Integer> boundVariableIndices = new ArrayList<>();
        List<Integer> boundVariables = new ArrayList<>();
        Set<Integer> unboundVariables = new HashSet<>();
        for (int i = 0; i < tuple.getSize(); i++) {
            PVariable variable = (PVariable) tuple.get(i);
            Integer position = variableMapping.get(variable);
            positions[i] = position;
            if (variableBindings.get(typeConstraint).contains(position)) {
                boundVariableIndices.add(i);
                boundVariables.add(position);
            } else {
                unboundVariables.add(position);
            }
        }
        TupleMask indexerMask = TupleMask.fromSelectedIndices(inputKey.getArity(), boundVariableIndices);
        TupleMask callMask = TupleMask.fromSelectedIndices(variableMapping.size(), boundVariables);
        if (unboundVariables.size() == 1) {
            operations.add(new GenericTypeExtendSingleValue(inputKey, positions, callMask, indexerMask, unboundVariables.iterator().next()));
        } else {
            operations.add(new GenericTypeExtend(inputKey, positions, callMask, indexerMask, unboundVariables));
        }

    }



}
