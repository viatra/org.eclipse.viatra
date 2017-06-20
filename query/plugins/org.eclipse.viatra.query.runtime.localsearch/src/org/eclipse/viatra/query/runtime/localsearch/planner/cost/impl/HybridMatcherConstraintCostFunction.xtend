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
package org.eclipse.viatra.query.runtime.localsearch.planner.cost.impl

import org.eclipse.viatra.query.runtime.localsearch.planner.cost.IConstraintEvaluationContext
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.ConstantValue
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.PositivePatternCall
import org.eclipse.viatra.query.runtime.matchers.tuple.FlatTuple
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple

/**
 * This cost function is intended to be used on hybrid configuration, with the strict restriction than any
 * non-flattened positive pattern call is executed with Rete engine. This implementation provides the exact number
 * of matches by invoking the result provider for the called pattern.
 *
 */
class HybridMatcherConstraintCostFunction extends IndexerBasedConstraintCostFunction {
    
    override protected _calculateCost(PositivePatternCall patternCall, IConstraintEvaluationContext input) {
        // Determine local constant constraints which is used to filter results
        val variables = patternCall.variablesTuple
        val variablesSet = variables.distinctElements;
        val constantMap = <PVariable, Object>newHashMap()
        patternCall.PSystem.constraints.forEach[
            if (it instanceof ConstantValue){
                val variable = it.variablesTuple.get(0) as PVariable
                if (variablesSet.contains(variable) && input.boundVariables.contains(variable)){
                    constantMap.put(variable, it.supplierKey)
                }
            }
        ]
        
        // Determine filter
        val filter = newArrayOfSize(variables.size)
        for(var i=0;i<variables.size;i++){
            filter.set(i, constantMap.get(variables.get(i)))
        }

        // aggregate keys are the bound and not filtered variables
        // These will be fixed in runtime, but unknown at planning time
        // This is represented by indices to ease working with result tuples
        val variableIndices = variables.invertIndex;
        val aggregateKeys = input.boundVariables.filter[!constantMap.containsKey(it)].map[variableIndices.get(it)];

        val resultProvider = input.resultProviderAccess.getResultProvider(patternCall.referredQuery, null)
        val aggregatedCounts = <Tuple, Integer>newHashMap()
        
        // Iterate over all matches and count together matches that has equal values on
        // aggregateKeys positions. The cost of the pattern call is considered to be the
        // Maximum of these counted values
        resultProvider.getAllMatches(filter).fold(null as Tuple -> 0, [currentMax, match |
            val extracted = match.extract(aggregateKeys)
            val count = if (aggregatedCounts.containsKey(extracted)){
                aggregatedCounts.get(extracted)+1
            }else{
                1
            }
            if (currentMax.key === null || count > currentMax.value){
                extracted -> count
            }else{
                currentMax
            }
        ]).value
    }
    
    private def Tuple extract(Tuple tuple, Iterable<Integer> indices){
        val list = newLinkedList();
        for(Integer index : indices){
            list.add(tuple.get(index))
        }
        return new FlatTuple(list.toArray)
    }
    
}