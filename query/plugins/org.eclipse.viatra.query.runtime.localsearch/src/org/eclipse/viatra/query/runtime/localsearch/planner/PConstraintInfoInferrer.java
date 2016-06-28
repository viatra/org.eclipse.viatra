/*******************************************************************************
 * Copyright (c) 2010-2016, Grill Balázs, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.planner;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra.query.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.viatra.query.runtime.localsearch.planner.cost.IConstraintEvaluationContext;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExpressionEvaluation;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.Inequality;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.PatternMatchCounter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author Grill Balázs
 *
 */
class PConstraintInfoInferrer {

    private static final class VariableNotDeducablePredicate implements Predicate<PVariable> {
        @Override
        public boolean apply(PVariable input) {
            return !input.isDeducable();
        }
    }

    private final boolean allowInverseNavigation;
    private final boolean useIndex;
    private final Function<IConstraintEvaluationContext, Float> costFunction;
    
    /**
     * 
     */
    public PConstraintInfoInferrer(boolean allowInverseNavigation, boolean useIndex, Function<IConstraintEvaluationContext, Float> costFunction) {
        this.allowInverseNavigation = allowInverseNavigation;
        this.useIndex = useIndex;
        this.costFunction = costFunction;
    }
    
    
    /**
     * Create all possible application condition for all constraint
     * 
     * @param constraintSet the set of constraints
     * @param runtimeContext the model dependent runtime contest
     * @return a collection of the wrapper PConstraintInfo objects with all the allowed application conditions
     */
    public List<PConstraintInfo> createPConstraintInfos(Set<PConstraint> constraintSet, IQueryRuntimeContext runtimeContext) {
        List<PConstraintInfo> constraintInfos = Lists.newArrayList();

        for (PConstraint pConstraint : constraintSet) {
            createPConstraintInfoDispatch(constraintInfos, pConstraint, runtimeContext);
        }
        return constraintInfos;
    }

    private void createPConstraintInfoDispatch(List<PConstraintInfo> resultList, PConstraint pConstraint, IQueryRuntimeContext runtimeContext){
        if(pConstraint instanceof ExportedParameter){
            createConstraintInfoExportedParameter(resultList, runtimeContext, (ExportedParameter) pConstraint);
        } else if(pConstraint instanceof TypeConstraint){
            createConstraintInfoTypeConstraint(resultList, runtimeContext, (TypeConstraint)pConstraint);
        } else if (pConstraint instanceof Inequality){
            createConstraintInfoInequality(resultList, runtimeContext, (Inequality) pConstraint);
        } else if (pConstraint instanceof ExpressionEvaluation){
            createConstraintInfoExpressionEvaluation(resultList, runtimeContext, (ExpressionEvaluation)pConstraint);
        } else if (pConstraint instanceof PatternMatchCounter){
            createConstraintInfoPatternMatchCounter(resultList, runtimeContext, (PatternMatchCounter) pConstraint);
        } else {
            createConstraintInfoGeneric(resultList, runtimeContext, pConstraint);
        }
    }
    
    private void createConstraintInfoExportedParameter(List<PConstraintInfo> resultList,
            IQueryRuntimeContext runtimeContext, ExportedParameter parameter) {
        // In case of an exported parameter constraint, the parameter must be bound in order to execute
        Set<PVariable> affectedVariables = parameter.getAffectedVariables();
        doCreateConstraintInfos(runtimeContext, resultList, parameter, affectedVariables, Collections.singleton(affectedVariables));
    }
    
    private void createConstraintInfoExpressionEvaluation(List<PConstraintInfo> resultList,
            IQueryRuntimeContext runtimeContext, ExpressionEvaluation expressionEvaluation) {
        // An expression evaluation can only have its output variable unbound. All other variables shall be bound
        PVariable output = expressionEvaluation.getOutputVariable();
        Set<Set<PVariable>> bindings = Sets.newHashSet();
        Set<PVariable> affectedVariables = expressionEvaluation.getAffectedVariables();
        // All variables bound -> check
        bindings.add(affectedVariables);
        // Output variable is not bound -> extend
        bindings.add(Sets.difference(affectedVariables, Collections.singleton(output)));
        doCreateConstraintInfos(runtimeContext, resultList, expressionEvaluation, affectedVariables, bindings);
    }

    private void createConstraintInfoInequality(List<PConstraintInfo> resultList, IQueryRuntimeContext runtimeContext, Inequality inequality){
        // In case of inequality, all affected variables must be bound in order to execute
        Set<PVariable> affectedVariables = inequality.getAffectedVariables();
        doCreateConstraintInfos(runtimeContext, resultList, inequality, affectedVariables, Collections.singleton(affectedVariables));
    }
    
    private void createConstraintInfoPatternMatchCounter(List<PConstraintInfo> resultList, IQueryRuntimeContext runtimeContext, PatternMatchCounter pConstraint){
        PVariable resultVariable = pConstraint.getResultVariable();
       
        Set<PVariable> affectedVariables = pConstraint.getAffectedVariables();
        
        // The only variables which can be unbound are the ones which cannot be deduced by any constraint and the result variable
        Set<PVariable> canBeUnboundVariables = Sets.union(Collections.singleton(resultVariable), Sets.filter(affectedVariables, new VariableNotDeducablePredicate()));
       
        Set<Set<PVariable>> bindings = calculatePossibleBindings(canBeUnboundVariables, affectedVariables);
        
        doCreateConstraintInfos(runtimeContext, resultList, pConstraint, affectedVariables, bindings);
    }
    
    /**
     * 
     * @param canBeUnboundVariables Variables which are allowed to be unbound
     * @param affectedVariables All affected variables
     * @return The set of possible bound variable sets
     */
    private Set<Set<PVariable>> calculatePossibleBindings(Set<PVariable> canBeUnboundVariables, Set<PVariable> affectedVariables){
        final Set<PVariable> mustBindVariables = Sets.difference(affectedVariables, canBeUnboundVariables);
        return Sets.newHashSet(Iterables.transform(Sets.powerSet(canBeUnboundVariables), new Function<Set<PVariable>, Set<PVariable>>() {

            @Override
            public Set<PVariable> apply(Set<PVariable> input) {
                // deducible variables shall need to be bound before executing this constraint
                return Sets.union(input, mustBindVariables);
            }
        }));
    }
    
    private void createConstraintInfoGeneric(List<PConstraintInfo> resultList, IQueryRuntimeContext runtimeContext, PConstraint pConstraint){
        Set<PVariable> affectedVariables = pConstraint.getAffectedVariables();
        
        // The only variables which can be unbound are the ones which cannot be deduced by any constraint
        Set<PVariable> canBeUnboundVariables = Sets.filter(affectedVariables, new VariableNotDeducablePredicate());
       
        Set<Set<PVariable>> bindings = calculatePossibleBindings(canBeUnboundVariables, affectedVariables);
        
        doCreateConstraintInfos(runtimeContext, resultList, pConstraint, affectedVariables, bindings);
    }
    
    private void createConstraintInfoTypeConstraint(List<PConstraintInfo> resultList, IQueryRuntimeContext runtimeContext, TypeConstraint typeConstraint) {
        Set<PVariable> affectedVariables = typeConstraint.getAffectedVariables();
        Set<Set<PVariable>> bindings = Sets.powerSet(affectedVariables);
        
        if(!allowInverseNavigation){
            // When inverse navigation is not allowed, filter out operation masks, where
            // the first variable would be free AND the feature is an EReference and has no EOpposite
            bindings = excludeUnnavigableOperationMasks(typeConstraint, bindings);
        } else {
            // Also do the above case, if it is an EReference with no EOpposite, or is an EAttribute
            IInputKey inputKey = typeConstraint.getSupplierKey();
            if(inputKey instanceof EStructuralFeatureInstancesKey){
                final EStructuralFeature feature = ((EStructuralFeatureInstancesKey) inputKey).getEmfKey();
                if(feature instanceof EReference){
                    if(!useIndex){                        
                        bindings = excludeUnnavigableOperationMasks(typeConstraint, bindings);
                    }
                } else {
                    bindings = excludeUnnavigableOperationMasks(typeConstraint, bindings);
                }
            }
        }
        doCreateConstraintInfos(runtimeContext, resultList, typeConstraint, affectedVariables, bindings);
    }
    
    private void doCreateConstraintInfos(IQueryRuntimeContext runtimeContext, List<PConstraintInfo> constraintInfos,
            PConstraint pConstraint, Set<PVariable> affectedVariables, Set<Set<PVariable>> bindings) {
        Set<PConstraintInfo> sameWithDifferentBindings = Sets.newHashSet();
        for (Set<PVariable> boundVariables : bindings) {
            PConstraintInfo info = new PConstraintInfo(pConstraint, boundVariables, Sets.difference(
                    affectedVariables, boundVariables), sameWithDifferentBindings, runtimeContext, costFunction);
            constraintInfos.add(info);
            sameWithDifferentBindings.add(info);
        }
    }
    
    private Set<Set<PVariable>> excludeUnnavigableOperationMasks(TypeConstraint typeConstraint, Set<Set<PVariable>> bindings) {
        PVariable firstVariable = typeConstraint.getVariableInTuple(0);
        Iterator<Set<PVariable>> iterator = bindings.iterator();
        Set<Set<PVariable>>elementsToRemove = Sets.newHashSet();
        while (iterator.hasNext()) {
            Set<PVariable> boundVariablesSet = iterator.next();
            if(!boundVariablesSet.isEmpty() && !boundVariablesSet.contains(firstVariable) && !hasEOpposite(typeConstraint)){
                elementsToRemove.add(boundVariablesSet);
            }
        }
        bindings = Sets.difference(bindings, elementsToRemove);
        return bindings;
    }
    
    private boolean hasEOpposite(TypeConstraint typeConstraint) {
        IInputKey supplierKey = typeConstraint.getSupplierKey();
        if(supplierKey instanceof EStructuralFeatureInstancesKey){
            EStructuralFeature wrappedKey = ((EStructuralFeatureInstancesKey) supplierKey).getWrappedKey();
            if(wrappedKey instanceof EReference){
                EReference eOpposite = ((EReference) wrappedKey).getEOpposite();
                if(eOpposite != null){
                    return true;
                }
            }
        }
        return false;
    }
    
}
