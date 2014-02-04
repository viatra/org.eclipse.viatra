/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.matchers.planning;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.incquery.runtime.matchers.planning.helpers.BuildHelper;
import org.eclipse.incquery.runtime.matchers.psystem.DeferredPConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.EnumerablePConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExpressionEvaluation;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Inequality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.NegativePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.PatternMatchCounter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.BinaryTransitiveClosure;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.ConstantValue;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.Containment;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.Generalization;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.Instantiation;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeTernary;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;
import org.eclipse.incquery.runtime.matchers.tuple.TupleMask;

/**
 * @author Zoltan Ujhelyi
 * 
 */
public class SubPlanProcessor {

    private IOperationCompiler<?> compiler;

    public void setCompiler(IOperationCompiler<?> compiler) {
        this.compiler = compiler;
    }

    /**
     * Creates a subplan representing the evaluation of a selected enumerable constraint. If the method is called again
     * with the same parameter, a new plan instance is returned.
     * 
     * @param constraint
     * @return the initialized plan
     * @throws RetePatternBuildException
     */
    public SubPlan processEnumerableConstraint(EnumerablePConstraint constraint) throws QueryPlannerException {
        SubPlan plan = dispatchConstraint(constraint);
        plan.addConstraint(constraint);

        // check for any variable coincidences and enforce them
        plan = BuildHelper.enforceVariableCoincidences(compiler, plan);
        return plan;
    }
    
    public SubPlan processDeferredConstraint(DeferredPConstraint constraint, SubPlan parentPlan) throws QueryPlannerException {
        SubPlan plan = dispatchConstraint(constraint, parentPlan);
        plan.addConstraint(constraint);
        return plan;
    }

    private SubPlan dispatchConstraint(EnumerablePConstraint constraint) throws QueryPlannerException {
        if (constraint instanceof BinaryTransitiveClosure) {
            return processConstraint((BinaryTransitiveClosure) constraint);
        } else if (constraint instanceof ConstantValue) {
            return processConstraint((ConstantValue) constraint);
        } else if (constraint instanceof Containment) {
            return processConstraint((Containment) constraint);
        } else if (constraint instanceof Generalization) {
            return processConstraint((Generalization) constraint);
        } else if (constraint instanceof Instantiation) {
            return processConstraint((Instantiation) constraint);
        } else if (constraint instanceof PositivePatternCall) {
            return processConstraint((PositivePatternCall) constraint);
        } else if (constraint instanceof TypeBinary) {
            return processConstraint((TypeBinary) constraint);
        } else if (constraint instanceof TypeTernary) {
            return processConstraint((TypeTernary) constraint);
        } else if (constraint instanceof TypeUnary) {
            return processConstraint((TypeUnary) constraint);
        }
        throw new UnsupportedOperationException("Unknown enumerable constraint");
    }

    private SubPlan processConstraint(BinaryTransitiveClosure constraint) throws QueryPlannerException {
        SubPlan patternProduction = compiler.patternCallPlan(constraint.getVariablesTuple(),
                constraint.getSupplierKey());
        return compiler.buildTransitiveClosure(patternProduction);
    }

    private SubPlan processConstraint(ConstantValue constraint) {
        return compiler.buildStartingPlan(new Object[] { constraint.getSupplierKey() }, constraint.getVariablesTuple()
                .getElements());
    }

    private SubPlan processConstraint(Containment constraint) {
        if (constraint.isTransitive()) {
            return compiler.transitiveContainmentPlan(constraint.getVariablesTuple());
        } else {
            return compiler.directContainmentPlan(constraint.getVariablesTuple());
        }
    }

    private SubPlan processConstraint(Generalization constraint) {
        if (constraint.isTransitive()) {
            return compiler.transitiveGeneralizationPlan(constraint.getVariablesTuple());
        } else {
            return compiler.directGeneralizationPlan(constraint.getVariablesTuple());
        }
    }

    private SubPlan processConstraint(Instantiation constraint) {
        if (constraint.isTransitive()) {
            return compiler.transitiveInstantiationPlan(constraint.getVariablesTuple());
        } else {
            return compiler.directInstantiationPlan(constraint.getVariablesTuple());
        }
    }

    private SubPlan processConstraint(PositivePatternCall constraint) throws QueryPlannerException {
        return compiler.patternCallPlan(constraint.getVariablesTuple(), constraint.getSupplierKey());
    }

    private SubPlan processConstraint(TypeBinary constraint) {
        return compiler.binaryEdgeTypePlan(constraint.getVariablesTuple(), constraint.getSupplierKey());
    }

    private SubPlan processConstraint(TypeTernary constraint) {
        return compiler.ternaryEdgeTypePlan(constraint.getVariablesTuple(), constraint.getSupplierKey());
    }

    private SubPlan processConstraint(TypeUnary constraint) {
        return compiler.unaryTypePlan(constraint.getVariablesTuple(), constraint.getSupplierKey());
    }

    private SubPlan dispatchConstraint(DeferredPConstraint constraint, SubPlan parentPlan) throws QueryPlannerException {
        if (constraint instanceof Equality) {
            return processConstraint((Equality)constraint, parentPlan);
        } else if (constraint instanceof ExportedParameter) {
            return processConstraint((ExportedParameter)constraint, parentPlan);
        } else if (constraint instanceof Inequality) {
            return processConstraint((Inequality)constraint, parentPlan);
        } else if (constraint instanceof NegativePatternCall) {
            return processConstraint((NegativePatternCall)constraint, parentPlan);
        } else if (constraint instanceof PatternMatchCounter) {
            return processConstraint((PatternMatchCounter)constraint, parentPlan);
        } else if (constraint instanceof ExpressionEvaluation) {
            return processConstraint((ExpressionEvaluation)constraint, parentPlan);
        }
        throw new UnsupportedOperationException("Unknown deferred constraint");
    }
    
    private SubPlan processConstraint(Equality constraint, SubPlan parentPlan) {
        if (constraint.isMoot())
            return parentPlan;

        Integer index1 = parentPlan.getVariablesIndex().get(constraint.getWho());
        Integer index2 = parentPlan.getVariablesIndex().get(constraint.getWithWhom());
        if (index1 != null && index2 != null) {
            if (index1.equals(index2))
                return parentPlan;
            else
                return compiler.buildEqualityChecker(parentPlan, new int[] { index1, index2 });
        } else if (index1 == null) {
            // TODO build copierNode here
        }
        return null;
    }
    
    private SubPlan processConstraint(ExportedParameter constraint, SubPlan parentPlan) {
        return parentPlan;
    }
    
    private SubPlan processConstraint(Inequality constraint, SubPlan parentPlan) {
        Map<Object, Integer> variablesIndex = parentPlan.getVariablesIndex();
        return compiler.buildInjectivityChecker(parentPlan, variablesIndex.get(constraint.getWho()),
                new int[] { variablesIndex.get(constraint.getWithWhom()) });
    }
    private SubPlan processConstraint(NegativePatternCall constraint, SubPlan parentPlan) throws QueryPlannerException {
        SubPlan sidePlan = constraint.getSidePlan(compiler);
        BuildHelper.JoinHelper joinHelper = new BuildHelper.JoinHelper(parentPlan, sidePlan);
        return compiler.buildBetaNode(parentPlan, sidePlan, joinHelper.getPrimaryMask(), joinHelper.getSecondaryMask(),
                joinHelper.getComplementerMask(), true);
    }
    private SubPlan processConstraint(PatternMatchCounter constraint, SubPlan parentPlan) throws QueryPlannerException {
        SubPlan sidePlan = constraint.getSidePlan(compiler);
        BuildHelper.JoinHelper joinHelper = new BuildHelper.JoinHelper(parentPlan, sidePlan);
        Integer resultPositionLeft = parentPlan.getVariablesIndex().get(constraint.getResultVariable());
        TupleMask primaryMask = joinHelper.getPrimaryMask();
        TupleMask secondaryMask = joinHelper.getSecondaryMask();
        final SubPlan counterBetaPlan = compiler.buildCounterBetaNode(parentPlan, sidePlan, primaryMask, secondaryMask,
                joinHelper.getComplementerMask(), constraint.getResultVariable());
        if (resultPositionLeft == null) {
            return counterBetaPlan;
        } else {
            int resultPositionFinal = counterBetaPlan.getVariablesTuple().getSize() - 1; // appended to the last position
            final SubPlan equalityCheckerPlan = 
                    compiler.buildEqualityChecker(counterBetaPlan, new int[]{resultPositionFinal, resultPositionLeft});
            return compiler.buildTrimmer(equalityCheckerPlan, TupleMask.omit(resultPositionFinal, 1+resultPositionFinal), false);
        }
    }
    
    private SubPlan processConstraint(ExpressionEvaluation constraint, SubPlan parentPlan) {
        Map<String, Integer> tupleNameMap = new HashMap<String, Integer>();
        for (String name : constraint.getEvaluator().getInputParameterNames()) {
            Map<Object, Integer> index = parentPlan.getVariablesIndex();
            PVariable variable = constraint.getPSystem().getVariableByNameChecked(name);
            Integer position = index.get(variable);
            tupleNameMap.put(name, position);
        }
        if (constraint.getOutputVariable() == null) {
            return compiler.buildPredicateChecker(constraint.getEvaluator(), tupleNameMap, parentPlan);
        } else { 
            return compiler.buildFunctionEvaluator(constraint.getEvaluator(), tupleNameMap, parentPlan, constraint.getOutputVariable());
        }
    }
}
