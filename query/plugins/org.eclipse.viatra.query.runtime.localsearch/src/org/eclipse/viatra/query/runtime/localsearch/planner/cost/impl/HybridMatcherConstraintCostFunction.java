/*******************************************************************************
 * Copyright (c) 2010-2016, Grill Balázs, IncQuery Labs Ltd
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.planner.cost.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.localsearch.planner.cost.IConstraintEvaluationContext;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.ConstantValue;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuples;

import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * This cost function is intended to be used on hybrid configuration, with the strict restriction than any
 * non-flattened positive pattern call is executed with Rete engine. This implementation provides the exact number
 * of matches by invoking the result provider for the called pattern.
 *
 */
public class HybridMatcherConstraintCostFunction extends IndexerBasedConstraintCostFunction {
    
    @Override
    protected double _calculateCost(PositivePatternCall patternCall, IConstraintEvaluationContext input) {
        // Determine local constant constraints which is used to filter results
        Tuple variables = patternCall.getVariablesTuple();
        Set<Object> variablesSet = variables.getDistinctElements();
        final Map<PVariable, Object> constantMap = new HashMap<>();
        for (PConstraint _constraint : patternCall.getPSystem().getConstraints()) {
            if (_constraint instanceof ConstantValue){
                ConstantValue constraint = (ConstantValue) _constraint;
                PVariable variable = (PVariable) constraint.getVariablesTuple().get(0);
                if (variablesSet.contains(variable) && input.getBoundVariables().contains(variable)) {
                    constantMap.put(variable, constraint.getSupplierKey());
                }
            }
        }
        
        // Determine filter
        Object[] filter = new Object[variables.getSize()];
        for(int i=0; i < variables.getSize(); i++){
            filter[i] = constantMap.get(variables.get(i));
        }

        // aggregate keys are the bound and not filtered variables
        // These will be fixed in runtime, but unknown at planning time
        // This is represented by indices to ease working with result tuples
        final Map<Object, Integer> variableIndices = variables.invertIndex();
        Iterable<Integer> aggregateKeys = Iterables.transform(Iterables.filter(input.getBoundVariables(), new Predicate<PVariable>() {

            @Override
            public boolean apply(PVariable input) {
                return !constantMap.containsKey(input);
            }
        }), Functions.forMap(variableIndices));

        IQueryResultProvider resultProvider = input.resultProviderAccess().getResultProvider(patternCall.getReferredQuery(), null);
        Map<Tuple, Integer> aggregatedCounts = new HashMap<>();
        
        // Iterate over all matches and count together matches that has equal values on
        // aggregateKeys positions. The cost of the pattern call is considered to be the
        // Maximum of these counted values
        Collection<? extends Tuple> matches = resultProvider.getAllMatches(filter);
        
        int result = 0;
        for (Tuple match : matches) {
            Tuple extracted = extract(match, aggregateKeys);
            int count = (aggregatedCounts.containsKey(extracted)) 
                ? aggregatedCounts.get(extracted) + 1
                : 1;                
            aggregatedCounts.put(extracted, count);
            if (result < count) {
                result = count;
            }
        }

        return result;
    }
    
    private Tuple extract(Tuple tuple, Iterable<Integer> indices){
        List<Object> list = new LinkedList<>();
        for(Integer index : indices){
            list.add(tuple.get(index));
        }
        return Tuples.flatTupleOf(list.toArray());
    }
    
}