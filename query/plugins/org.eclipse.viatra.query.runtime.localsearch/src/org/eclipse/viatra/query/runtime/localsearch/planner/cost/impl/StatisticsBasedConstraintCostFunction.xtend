/** 
 * Copyright (c) 2010-2016, Grill Balázs, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Grill Balázs - initial API and implementation
 */
package org.eclipse.viatra.query.runtime.localsearch.planner.cost.impl

import java.util.Collection
import java.util.List
import java.util.Map
import java.util.Set
import org.eclipse.viatra.query.runtime.localsearch.planner.cost.IConstraintEvaluationContext
import org.eclipse.viatra.query.runtime.localsearch.planner.cost.ICostFunction
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey
import org.eclipse.viatra.query.runtime.matchers.context.InputKeyImplication
import org.eclipse.viatra.query.runtime.matchers.planning.helpers.FunctionalDependencyHelper
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.TypeFilterConstraint
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.ConstantValue
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.PositivePatternCall
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint

/**
 * Cost function which calculates cost based on the cardinality of items in the runtime model
 * 
 * @author Grill Balázs
 * @since 1.4
 */
abstract class StatisticsBasedConstraintCostFunction implements ICostFunction {
    protected static double MAX_COST = 250.0
    protected static double DEFAULT_COST = MAX_COST - 100.0

    def abstract long countTuples(IConstraintEvaluationContext input, IInputKey supplierKey);

    override apply(IConstraintEvaluationContext input) {
        calculateCost(input.constraint, input)
    }
    
    protected def dispatch double calculateCost(ConstantValue constant, IConstraintEvaluationContext input) {
        return 0.0f;
    }

    protected def dispatch double calculateCost(TypeConstraint constraint, IConstraintEvaluationContext input) {
        val freeMaskVariables = input.freeVariables
        val boundMaskVariables = input.boundVariables
        var IInputKey supplierKey = (constraint as TypeConstraint).getSupplierKey()
        var long arity = supplierKey.getArity()
        if (arity == 1) {
            // unary constraint
            return calculateUnaryConstraintCost(constraint, input)
        } else if (arity == 2) {
            // binary constraint
            var long edgeCount = countTuples(input, supplierKey)
            var srcVariable = (constraint as TypeConstraint).getVariablesTuple().get(0) as PVariable
            var dstVariable = (constraint as TypeConstraint).getVariablesTuple().get(1) as PVariable
            var isInverse = false
            // Check if inverse navigation is needed along the edge
            if (freeMaskVariables.contains(srcVariable) && boundMaskVariables.contains(dstVariable)) {
                isInverse = true
            }
            return calculateBinaryExtendCost(supplierKey, srcVariable, dstVariable, isInverse, edgeCount, input)
        } else {
            // n-ary constraint
            throw new RuntimeException('''Cost calculation for arity «arity» is not implemented yet''')
        }
    }
    
    protected def double calculateBinaryExtendCost(IInputKey supplierKey, PVariable srcVariable, PVariable dstVariable, boolean isInverse, long edgeCount, IConstraintEvaluationContext input) {
        val freeMaskVariables = input.freeVariables
        val boundMaskVariables = input.boundVariables
        val constraint = input.constraint
        var metaContext = input.runtimeContext.getMetaContext()
        val queryAnalyzer = input.queryAnalyzer
        
        var Collection<InputKeyImplication> implications = metaContext.getImplications(supplierKey)
        // TODO prepare for cases when this info is not available - use only metamodel related cost calculation (see TODO at the beginning of the function)
        var double srcCount = -1
        var double dstCount = -1
        // Obtain runtime information
        for (InputKeyImplication implication : implications) {
            var List<Integer> impliedIndices = implication.getImpliedIndices()
            if (impliedIndices.size() == 1 && impliedIndices.contains(0)) {
                // Source key implication
                srcCount = countTuples(input, implication.getImpliedKey())
            } else if (impliedIndices.size() == 1 && impliedIndices.contains(1)) {
                // Target key implication
                dstCount = countTuples(input, implication.getImpliedKey())
            }
        
        }
        if (freeMaskVariables.contains(srcVariable) && freeMaskVariables.contains(dstVariable)) {
            return dstCount * srcCount
        } else {
            var double srcNodeCount = -1
            var double dstNodeCount = -1
            if (isInverse) {
                srcNodeCount = dstCount
                dstNodeCount = srcCount
            } else {
                srcNodeCount = srcCount
                dstNodeCount = dstCount
            }
            
            if (srcNodeCount > -1 && edgeCount > -1) {
                // The end nodes had implied (type) constraint and both nodes and adjacent edges are indexed
                if (srcNodeCount == 0) {
                    return 0
                } else {
                    return ((edgeCount) as double) / srcNodeCount
                }
            } else if (srcCount > -1 && dstCount > -1) {
                // Both of the end nodes had implied (type) constraint
                if(srcCount != 0) {
                    return dstNodeCount / srcNodeCount
                } else {
                    // No such element exists in the model, so the traversal will backtrack at this point
                    return 1.0f;
                }
            } else {
                // At least one of the end variables had no restricting type information
                // Strategy: try to navigate along many-to-one relations
                var Map<Set<PVariable>, Set<PVariable>> functionalDependencies = 
                    queryAnalyzer.getFunctionalDependencies(#{constraint}, false);
                var impliedVariables = functionalDependencies.get(boundMaskVariables)
                if(impliedVariables != null && impliedVariables.containsAll(freeMaskVariables)){
                    return 1.0;
                } else {
                    return DEFAULT_COST
                }
            }
        }
    }
    
    protected def double calculateUnaryConstraintCost(TypeConstraint constraint, IConstraintEvaluationContext input) {
        var variable = constraint.getVariablesTuple().get(0) as PVariable
        if (input.boundVariables.contains(variable)) {
            return 0.9
        } else {
            return countTuples(input, constraint.supplierKey)+DEFAULT_COST
        }
    }

    def dispatch double calculateCost(ExportedParameter exportedParam, IConstraintEvaluationContext input) {
        return 0.0;
    }
    
    def dispatch double calculateCost(TypeFilterConstraint exportedParam, IConstraintEvaluationContext input) {
        return 0.0;
    }

    def dispatch double calculateCost(PositivePatternCall patternCall, IConstraintEvaluationContext input){
        val dependencies = input.queryAnalyzer.getFunctionalDependencies(#{patternCall}, false)
        val boundOrImplied = FunctionalDependencyHelper.closureOf( input.boundVariables, dependencies)
        val parameters = patternCall.referredQuery.parameters
        var result = 1.0
        // TODO this is currently works with declared types only. For better results, information from 
        // the Type inferrer should be included in the PSystem
        for(var i=0;i<parameters.size;i++){
            val variable = patternCall.getVariableInTuple(i)
            result = result * if (boundOrImplied.contains(variable)){
                    0.9
                } else {
                    val type = parameters.get(i).declaredUnaryType
                    if (type == null){
                        DEFAULT_COST
                    } else {
                        countTuples(input, type)
                    }
                }  
        }
        return result
    }
    
    /**
     * Default cost calculation strategy
     */
    protected def dispatch double calculateCost(PConstraint constraint, IConstraintEvaluationContext input) {
        if (input.freeVariables.isEmpty) {
            return 1.0;
        } else {
            return DEFAULT_COST
        }
    }
}
