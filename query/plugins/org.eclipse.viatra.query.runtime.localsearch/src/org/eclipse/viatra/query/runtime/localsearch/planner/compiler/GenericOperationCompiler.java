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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.localsearch.operations.generic.GenericTypeCheck;
import org.eclipse.viatra.query.runtime.localsearch.operations.generic.GenericTypeExtend;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.TypeFilterConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

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
        Integer[] positions = new Integer[tuple.getSize()];
        for (int i = 0; i < tuple.getSize(); i++) {
            PVariable variable = (PVariable) tuple.get(i);
            positions[i] = variableMapping.get(variable);
        }
        operations.add(new GenericTypeCheck(inputKey, positions));
        
    }
    
    @Override
    protected void createCheck(TypeConstraint typeConstraint, Map<PVariable, Integer> variableMapping)
            throws QueryProcessingException {
        IInputKey inputKey = typeConstraint.getSupplierKey();
        Tuple tuple = typeConstraint.getVariablesTuple();
        Integer[] positions = new Integer[tuple.getSize()];
        for (int i = 0; i < tuple.getSize(); i++) {
            PVariable variable = (PVariable) tuple.get(i);
            positions[i] = variableMapping.get(variable);
        }
        operations.add(new GenericTypeCheck(inputKey, positions));
    }
    
    @Override
    protected void createExtend(TypeConstraint typeConstraint, Map<PVariable, Integer> variableMapping) {
        IInputKey inputKey = typeConstraint.getSupplierKey();
        Tuple tuple = typeConstraint.getVariablesTuple();
        
        Integer[] positions = new Integer[tuple.getSize()];
        Set<Integer> boundVariables = new HashSet<>();
        for (int i = 0; i < tuple.getSize(); i++) {
            PVariable variable = (PVariable) tuple.get(i);
            Integer position = variableMapping.get(variable);
            positions[i] = position;
            if (variableBindings.get(typeConstraint).contains(position)) {
                boundVariables.add(position);
            }
        }
        operations.add(new GenericTypeExtend(inputKey, positions, boundVariables));

    }



}
