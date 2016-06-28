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
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.ConstantValue
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint

/**
 * Cost function which calculates cost based on the cardinality of items in the runtime model, provided by the base indexer
 * 
 * @author Grill Balázs
 * @since 1.4
 */
class IndexerBasedConstraintCostFunction implements ICostFunction {
    static float MAX_COST = 250.0f
    static float DEFAULT_COST = MAX_COST - 100.0f

    override apply(IConstraintEvaluationContext input) {
        calculateCost(input.constraint, input)
    }
    
    protected def dispatch float calculateCost(ConstantValue constant, IConstraintEvaluationContext input) {
        return 1.0f;
    }

    protected def dispatch float calculateCost(TypeConstraint constraint, IConstraintEvaluationContext input) {
        val runtimeContext = input.runtimeContext
        val freeMaskVariables = input.freeVariables
        val boundMaskVariables = input.boundVariables
        var IInputKey supplierKey = (constraint as TypeConstraint).getSupplierKey()
        var long arity = supplierKey.getArity()
        if (arity == 1) {
            // unary constraint
            return calculateUnaryConstraintCost(constraint, input)
        } else if (arity == 2) {
            // binary constraint
            var long edgeCount = runtimeContext.countTuples(supplierKey, null)
            var srcVariable = (constraint as TypeConstraint).getVariablesTuple().get(0) as PVariable
            var dstVariable = (constraint as TypeConstraint).getVariablesTuple().get(1) as PVariable
            var isInverse = false
            // Check if inverse navigation is needed along the edge
            if (freeMaskVariables.contains(srcVariable) && boundMaskVariables.contains(dstVariable)) {
                isInverse = true
            }
            if (freeMaskVariables.contains(srcVariable) || freeMaskVariables.contains(dstVariable)) {
                // This case it is not a check
                // at least one of the variables are free, so calculate cost based on the meta or/and the runtime context
                return calculateBinaryExtendCost(supplierKey, srcVariable, dstVariable, isInverse, edgeCount, input)
            } else {
                // It is a check operation, both variables are bound
                return 1.0f
            }
        } else {
            // n-ary constraint
            throw new RuntimeException('''Cost calculation for arity «arity» is not implemented yet''')
        }
    }
    
    protected def float calculateBinaryExtendCost(IInputKey supplierKey, PVariable srcVariable, PVariable dstVariable, boolean isInverse, long edgeCount, IConstraintEvaluationContext input) {
        val runtimeContext = input.runtimeContext
        val freeMaskVariables = input.freeVariables
        val boundMaskVariables = input.boundVariables
        val constraint = input.constraint
        var metaContext = runtimeContext.getMetaContext()
        var Collection<InputKeyImplication> implications = metaContext.getImplications(supplierKey)
        // TODO prepare for cases when this info is not available - use only metamodel related cost calculation (see TODO at the beginning of the function)
        var long srcCount = -1
        var long dstCount = -1
        // Obtain runtime information
        for (InputKeyImplication implication : implications) {
            var List<Integer> impliedIndices = implication.getImpliedIndices()
            if (impliedIndices.size() == 1 && impliedIndices.contains(0)) {
                // Source key implication
                srcCount = runtimeContext.countTuples(implication.getImpliedKey(), null)
            } else if (impliedIndices.size() == 1 && impliedIndices.contains(1)) {
                // Target key implication
                dstCount = runtimeContext.countTuples(implication.getImpliedKey(), null)
            }
        
        }
        if (freeMaskVariables.contains(srcVariable) && freeMaskVariables.contains(dstVariable)) {
            return dstCount * srcCount
        } else {
            var long srcNodeCount = -1
            var long dstNodeCount = -1
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
                    return ((edgeCount) as float) / srcNodeCount
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
                var Map<Set<PVariable>, Set<PVariable>> functionalDependencies = constraint.getFunctionalDependencies(metaContext);
                var impliedVariables = functionalDependencies.get(boundMaskVariables)
                if(impliedVariables != null && impliedVariables.containsAll(freeMaskVariables)){
                    return 1.0f;
                } else {
                    return DEFAULT_COST
                }
            }
        }
    }
    
    protected def float calculateUnaryConstraintCost(TypeConstraint constraint, IConstraintEvaluationContext input) {
        var variable = constraint.getVariablesTuple().get(0) as PVariable
        if (input.boundVariables.contains(variable)) {
            return 0.9f
        } else {
            return input.runtimeContext.countTuples(constraint.supplierKey, null)
        }
    }

    def dispatch float calculateCost(ExportedParameter exportedParam, IConstraintEvaluationContext input) {
        return MAX_COST;
    }

    /**
     * Default cost calculation strategy
     */
    protected def dispatch float calculateCost(PConstraint constraint, IConstraintEvaluationContext input) {
        if (input.freeVariables.isEmpty) {
            return 1.0f;
        } else {
            return DEFAULT_COST
        }
    }
}
