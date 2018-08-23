/**
 * Copyright (c) 2010-2016, Grill Balázs, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Grill Balázs - initial API and implementation
 */
package org.eclipse.viatra.query.runtime.localsearch.planner.cost.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.localsearch.planner.cost.IConstraintEvaluationContext;
import org.eclipse.viatra.query.runtime.localsearch.planner.cost.ICostFunction;
import org.eclipse.viatra.query.runtime.matchers.ViatraQueryRuntimeException;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.viatra.query.runtime.matchers.context.InputKeyImplication;
import org.eclipse.viatra.query.runtime.matchers.planning.helpers.FunctionalDependencyHelper;
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.analysis.QueryAnalyzer;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.AggregatorConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExpressionEvaluation;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.Inequality;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.NegativePatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.PatternMatchCounter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.TypeFilterConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.BinaryReflexiveTransitiveClosure;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.BinaryTransitiveClosure;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.ConstantValue;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;

/**
 * Cost function which calculates cost based on the cardinality of items in the runtime model
 * 
 * @author Grill Balázs
 * @since 1.4
 */
public abstract class StatisticsBasedConstraintCostFunction implements ICostFunction {
    protected static final double MAX_COST = 250.0;

    protected static final double DEFAULT_COST = StatisticsBasedConstraintCostFunction.MAX_COST - 100.0;

    public abstract long countTuples(final IConstraintEvaluationContext input, final IInputKey supplierKey);

    @Override
    public double apply(final IConstraintEvaluationContext input) {
        return this.calculateCost(input.getConstraint(), input);
    }

    protected double _calculateCost(final ConstantValue constant, final IConstraintEvaluationContext input) {
        return 0.0f;
    }

    protected double _calculateCost(final TypeConstraint constraint, final IConstraintEvaluationContext input) {
        final Collection<PVariable> freeMaskVariables = input.getFreeVariables();
        final Collection<PVariable> boundMaskVariables = input.getBoundVariables();
        IInputKey supplierKey = constraint.getSupplierKey();
        long arity = supplierKey.getArity();

        if ((arity == 1)) {
            // unary constraint
            return calculateUnaryConstraintCost(constraint, input);
        } else if ((arity == 2)) {
            // binary constraint
            long edgeCount = countTuples(input, supplierKey);
            PVariable srcVariable = ((PVariable) constraint.getVariablesTuple().get(0));
            PVariable dstVariable = ((PVariable) constraint.getVariablesTuple().get(1));
            boolean isInverse = false;
            // Check if inverse navigation is needed along the edge
            if ((freeMaskVariables.contains(srcVariable) && boundMaskVariables.contains(dstVariable))) {
                isInverse = true;
            }
            double binaryExtendCost = calculateBinaryExtendCost(supplierKey, srcVariable, dstVariable, isInverse,
                    edgeCount, input);
            // Make inverse navigation slightly more expensive than forward navigation
            // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=501078
            return (isInverse) ? binaryExtendCost + 1.0 : binaryExtendCost;
        } else {
            // n-ary constraint
            throw new UnsupportedOperationException("Cost calculation for arity " + arity + " is not implemented yet");
        }
    }

    protected double calculateBinaryExtendCost(final IInputKey supplierKey, final PVariable srcVariable,
            final PVariable dstVariable, final boolean isInverse, final long edgeCount,
            final IConstraintEvaluationContext input) {
        final Collection<PVariable> freeMaskVariables = input.getFreeVariables();
        final PConstraint constraint = input.getConstraint();
        IQueryMetaContext metaContext = input.getRuntimeContext().getMetaContext();

        Collection<InputKeyImplication> implications = metaContext.getImplications(supplierKey);

        double srcCount = -1;
        double dstCount = -1;
        // Obtain runtime information
        for (final InputKeyImplication implication : implications) {
            if (! implication.getImpliedKey().isEnumerable()) 
                continue;
            
            List<Integer> impliedIndices = implication.getImpliedIndices();
            if (impliedIndices.size() == 1 && impliedIndices.contains(0)) {
                // Source key implication
                long estimate = this.countTuples(input, implication.getImpliedKey());
                if (srcCount < 0 || srcCount > estimate) srcCount = estimate;
            } else if (impliedIndices.size() == 1 && impliedIndices.contains(1)) {
                // Target key implication
                long estimate = this.countTuples(input, implication.getImpliedKey());
                if (dstCount < 0 || dstCount > estimate) dstCount = estimate;
            }
        }

        if (freeMaskVariables.contains(srcVariable) && freeMaskVariables.contains(dstVariable)) {
            // both variables unbound, iterate over edges 
            if (edgeCount > -1) {
                return edgeCount;
            } else { // edge count unknown, estimate from product of node counts
                return (dstCount > -1 ? dstCount : DEFAULT_COST) * 
                       (srcCount > -1 ? srcCount : DEFAULT_COST);                
            }
                
        } else {
            double srcNodeCount = (isInverse) ? dstCount : srcCount;
            double dstNodeCount = (isInverse) ? srcCount : dstCount;

            double costEstimate = Double.POSITIVE_INFINITY;
            
            if (!freeMaskVariables.contains(srcVariable) && !freeMaskVariables.contains(dstVariable)) {
                // both variables bound, this is a simple check
                costEstimate = Math.min(costEstimate, 0.9);
            }
            if (srcNodeCount > -1 && edgeCount > -1) {
                // The end nodes had implied (type) constraint and both nodes and adjacent edges are indexed
                double amortizedEdgeCount = (srcNodeCount == 0) ? 0.0 : ((double) edgeCount) / srcNodeCount;
                costEstimate = Math.min(costEstimate, amortizedEdgeCount);
            } 
            if (srcNodeCount > -1 && dstNodeCount > -1 && navigatesThroughFunctionalDependencyInverse(input, constraint)) {
                // Both of the end nodes had implied (type) constraint, and 
                // due to a reverse functional dependency, the destination count is an upper bound for the edge count
                double amortizedEdgeLimit = (srcNodeCount == 0) ? 0.0 : ((double) dstNodeCount) / srcNodeCount;
                costEstimate = Math.min(costEstimate, amortizedEdgeLimit);
            }
            if (srcNodeCount > -1 && dstNodeCount > -1 && edgeCount < 0) {
                // Both of the end nodes had implied (type) constraint
                // If count is 0, no such element exists in the model, so there will be no branching
                // TODO rethink, why dstNodeCount / srcNodeCount instead of dstNodeCount? 
                // The universally valid bound would be something like sparseEdgeEstimate = dstNodeCount + 1.0
                // If we assume sparseness, we can reduce it by a SPARSENESS_FACTOR (e.g. 0.1). 
                // Alternatively, discount dstNodeCount * srcNodeCount on a SPARSENESS_EXPONENT (e.g 0.75) and then amortize over srcNodeCount.
                double sparseEdgeEstimate = srcNodeCount != 0 ? Math.max(1.0, ((double) dstNodeCount) / srcNodeCount) : 1.0;
                costEstimate = Math.min(costEstimate, sparseEdgeEstimate);
            } 
            if (navigatesThroughFunctionalDependency(input, constraint)) {
                // At most one destination value
                costEstimate = Math.min(costEstimate, 1.0); 
            }
            
            return (Double.POSITIVE_INFINITY == costEstimate) ? DEFAULT_COST : costEstimate;

        }
    }

    /**
     * @since 1.7
     */
    protected boolean navigatesThroughFunctionalDependency(final IConstraintEvaluationContext input,
            final PConstraint constraint) {
        return navigatesThroughFunctionalDependency(input, constraint, input.getBoundVariables(), input.getFreeVariables());
    }
    /**
     * @since 2.1
     */
    protected boolean navigatesThroughFunctionalDependencyInverse(final IConstraintEvaluationContext input,
            final PConstraint constraint) {
        return navigatesThroughFunctionalDependency(input, constraint, input.getFreeVariables(), input.getBoundVariables());
    }
    /**
     * @since 2.1
     */
    protected boolean navigatesThroughFunctionalDependency(final IConstraintEvaluationContext input,
            final PConstraint constraint, Collection<PVariable> determining, Collection<PVariable> determined) {
        final QueryAnalyzer queryAnalyzer = input.getQueryAnalyzer();
        final Map<Set<PVariable>, Set<PVariable>> functionalDependencies = queryAnalyzer
                .getFunctionalDependencies(Collections.singleton(constraint), false);
        final Set<PVariable> impliedVariables = FunctionalDependencyHelper.closureOf(determining,
                functionalDependencies);
        return ((impliedVariables != null) && impliedVariables.containsAll(determined));
    }
    
    protected double calculateUnaryConstraintCost(final TypeConstraint constraint,
            final IConstraintEvaluationContext input) {
        PVariable variable = (PVariable) constraint.getVariablesTuple().get(0);
        if (input.getBoundVariables().contains(variable)) {
            return 0.9;
        } else {
            return countTuples(input, constraint.getSupplierKey()) + 1.0;
        }
    }

    protected double _calculateCost(final ExportedParameter exportedParam, final IConstraintEvaluationContext input) {
        return 0.0;
    }

    protected double _calculateCost(final TypeFilterConstraint exportedParam,
            final IConstraintEvaluationContext input) {
        return 0.0;
    }

    protected double _calculateCost(final PositivePatternCall patternCall, final IConstraintEvaluationContext input) {
        final Map<Set<PVariable>, Set<PVariable>> dependencies = input.getQueryAnalyzer()
                .getFunctionalDependencies(Collections.singleton(patternCall), false);
        final Set<PVariable> boundOrImplied = FunctionalDependencyHelper.closureOf(input.getBoundVariables(),
                dependencies);
        final List<PParameter> parameters = patternCall.getReferredQuery().getParameters();
        double result = 1.0;
        // TODO this is currently works with declared types only. For better results, information from
        // the Type inferrer should be included in the PSystem
        for (int i = 0; (i < parameters.size()); i++) {
            final PVariable variable = patternCall.getVariableInTuple(i);
            final IInputKey type = parameters.get(i).getDeclaredUnaryType();
            double multiplier = (boundOrImplied.contains(variable)) ? 0.9 : (type == null) ? DEFAULT_COST : countTuples(input, type);
            result *= multiplier;
        }
        return result;
    }

    /**
     * @since 1.7
     */
    protected double _calculateCost(final ExpressionEvaluation evaluation, final IConstraintEvaluationContext input) {
        return _calculateCost((PConstraint)evaluation, input);
    }
    
    /**
     * @since 1.7
     */
    protected double _calculateCost(final Inequality inequality, final IConstraintEvaluationContext input) {
        return _calculateCost((PConstraint)inequality, input);
    }
    
    /**
     * @since 1.7
     */
    protected double _calculateCost(final AggregatorConstraint aggregator, final IConstraintEvaluationContext input) {
        return _calculateCost((PConstraint)aggregator, input);
    }
    
    /**
     * @since 1.7
     */
    protected double _calculateCost(final NegativePatternCall call, final IConstraintEvaluationContext input) {
        return _calculateCost((PConstraint)call, input);
    }
    
    /**
     * @since 1.7
     */
    protected double _calculateCost(final PatternMatchCounter counter, final IConstraintEvaluationContext input) {
        return _calculateCost((PConstraint)counter, input);
    }
    
    /**
     * @since 1.7
     */
    protected double _calculateCost(final BinaryTransitiveClosure closure, final IConstraintEvaluationContext input) {
        return _calculateCost((PConstraint)closure, input);
    }
    
    /**
     * @since 2.0
     */
    protected double _calculateCost(final BinaryReflexiveTransitiveClosure closure, final IConstraintEvaluationContext input) {
        return _calculateCost((PConstraint)closure, input);
    }
    
    /**
     * Default cost calculation strategy
     */
    protected double _calculateCost(final PConstraint constraint, final IConstraintEvaluationContext input) {
        if (input.getFreeVariables().isEmpty()) {
            return 1.0;
        } else {
            return StatisticsBasedConstraintCostFunction.DEFAULT_COST;
        }
    }

    /**
     * @throws ViatraQueryRuntimeException
     */
    public double calculateCost(final PConstraint constraint, final IConstraintEvaluationContext input) {
        Preconditions.checkArgument(constraint != null, "Set constraint value correctly");
        if (constraint instanceof ExportedParameter) {
            return _calculateCost((ExportedParameter) constraint, input);
        } else if (constraint instanceof TypeFilterConstraint) {
            return _calculateCost((TypeFilterConstraint) constraint, input);
        } else if (constraint instanceof ConstantValue) {
            return _calculateCost((ConstantValue) constraint, input);
        } else if (constraint instanceof PositivePatternCall) {
            return _calculateCost((PositivePatternCall) constraint, input);
        } else if (constraint instanceof TypeConstraint) {
            return _calculateCost((TypeConstraint) constraint, input);
        } else if (constraint instanceof ExpressionEvaluation) {
            return _calculateCost((ExpressionEvaluation) constraint, input);
        } else if (constraint instanceof Inequality) {
            return _calculateCost((Inequality) constraint, input);
        } else if (constraint instanceof AggregatorConstraint) {
            return _calculateCost((AggregatorConstraint) constraint, input);
        } else if (constraint instanceof NegativePatternCall) {
            return _calculateCost((NegativePatternCall) constraint, input);
        } else if (constraint instanceof PatternMatchCounter) {
            return _calculateCost((PatternMatchCounter) constraint, input);
        } else if (constraint instanceof BinaryTransitiveClosure) {
            return _calculateCost((BinaryTransitiveClosure) constraint, input);
        } else if (constraint instanceof BinaryReflexiveTransitiveClosure) {
            return _calculateCost((BinaryReflexiveTransitiveClosure) constraint, input);
        } else {
            // Default cost calculation
            return _calculateCost(constraint, input);
        }
    }
}
