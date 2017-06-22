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
import org.eclipse.viatra.query.runtime.base.api.BaseIndexOptions;
import org.eclipse.viatra.query.runtime.base.comprehension.EMFModelComprehension;
import org.eclipse.viatra.query.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.viatra.query.runtime.localsearch.planner.cost.IConstraintEvaluationContext;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryBackendContext;
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.AggregatorConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExpressionEvaluation;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.Inequality;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.PatternMatchCounter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.TypeFilterConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.ConstantValue;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author Grill Balázs
 * @noreference This class is not intended to be referenced by clients.
 */
class PConstraintInfoInferrer {

    private static final class VariableNotDeducablePredicate implements Predicate<PVariable> {
        @Override
        public boolean apply(PVariable input) {
            return input != null && !input.isDeducable();
        }
    }

    private final boolean useIndex;
    private final Function<IConstraintEvaluationContext, Double> costFunction;
    private final EMFModelComprehension modelComprehension;
    private final IQueryBackendContext context;
    
    public PConstraintInfoInferrer(boolean useIndex, 
            IQueryBackendContext backendContext, 
            Function<IConstraintEvaluationContext, Double> costFunction) {
        this.useIndex = useIndex;
        this.context = backendContext;
        this.costFunction = costFunction;
        this.modelComprehension = new EMFModelComprehension(new BaseIndexOptions());
    }
    
    
    /**
     * Create all possible application condition for all constraint
     * 
     * @param constraintSet the set of constraints
     * @param runtimeContext the model dependent runtime contest
     * @return a collection of the wrapper PConstraintInfo objects with all the allowed application conditions
     */
    public List<PConstraintInfo> createPConstraintInfos(Set<PConstraint> constraintSet) {
        List<PConstraintInfo> constraintInfos = Lists.newArrayList();

        for (PConstraint pConstraint : constraintSet) {
            createPConstraintInfoDispatch(constraintInfos, pConstraint);
        }
        return constraintInfos;
    }

    private void createPConstraintInfoDispatch(List<PConstraintInfo> resultList, PConstraint pConstraint){
        if(pConstraint instanceof ExportedParameter){
            createConstraintInfoExportedParameter(resultList, (ExportedParameter) pConstraint);
        } else if(pConstraint instanceof TypeConstraint){
            createConstraintInfoTypeConstraint(resultList, (TypeConstraint)pConstraint);
        } else if(pConstraint instanceof TypeFilterConstraint){
            createConstraintInfoTypeFilterConstraint(resultList, (TypeFilterConstraint)pConstraint);
        } else if(pConstraint instanceof ConstantValue){
            createConstraintInfoConstantValue(resultList, (ConstantValue)pConstraint);
        } else if (pConstraint instanceof Inequality){
            createConstraintInfoInequality(resultList, (Inequality) pConstraint);
        } else if (pConstraint instanceof ExpressionEvaluation){
            createConstraintInfoExpressionEvaluation(resultList, (ExpressionEvaluation)pConstraint);
        } else if (pConstraint instanceof AggregatorConstraint){
            createConstraintInfoAggregatorConstraint(resultList, pConstraint, ((AggregatorConstraint) pConstraint).getResultVariable());
        } else if (pConstraint instanceof PatternMatchCounter){
            createConstraintInfoAggregatorConstraint(resultList, pConstraint, ((PatternMatchCounter) pConstraint).getResultVariable());   
        } else if (pConstraint instanceof PositivePatternCall){
            createConstraintInfoPositivePatternCall(resultList, (PositivePatternCall) pConstraint);
        } else{
            createConstraintInfoGeneric(resultList, pConstraint);
        }
    }
    
    private void createConstraintInfoConstantValue(List<PConstraintInfo> resultList, 
            ConstantValue pConstraint) {
        // A ConstantValue constraint has a single variable, which is allowed to be unbound 
        // (extending through ConstantValue is considered a cheap operation)
        Set<PVariable> affectedVariables = pConstraint.getAffectedVariables();
        Set<Set<PVariable>> bindings = Sets.powerSet(affectedVariables);
        doCreateConstraintInfos(resultList, pConstraint, affectedVariables, bindings);
    }


    private void createConstraintInfoPositivePatternCall(List<PConstraintInfo> resultList, 
            PositivePatternCall pCall) {
        // A pattern call can have any of its variables unbound
        Set<PVariable> affectedVariables = pCall.getAffectedVariables();
        // IN parameters cannot be unbound and
        // OUT parameters cannot be bound
        Tuple variables = pCall.getVariablesTuple();
        final Set<PVariable> inVariables = Sets.newHashSet();
        Set<PVariable> inoutVariables = Sets.newHashSet();
        List<PParameter> parameters = pCall.getReferredQuery().getParameters();
        for(int i=0;i<parameters.size();i++){
            switch(parameters.get(i).getDirection()){
            case IN:
                inVariables.add((PVariable) variables.get(i));
                break;
            case INOUT:
                inoutVariables.add((PVariable) variables.get(i));
                break;
            case OUT:
            default:
                break;
            
            }
        }
        Iterable<Set<PVariable>> bindings = Iterables.transform(Sets.powerSet(inoutVariables), new Function<Set<PVariable>, Set<PVariable>>() {

            @Override
            public Set<PVariable> apply(Set<PVariable> input) {
                return Sets.union(inVariables, input);
            }
        });
        
        doCreateConstraintInfos(resultList, pCall, affectedVariables, bindings);
    }
    
    

    private void createConstraintInfoExportedParameter(List<PConstraintInfo> resultList, 
            ExportedParameter parameter) {
        // In case of an exported parameter constraint, the parameter must be bound in order to execute
        Set<PVariable> affectedVariables = parameter.getAffectedVariables();
        doCreateConstraintInfos(resultList, parameter, affectedVariables, Collections.singleton(affectedVariables));
    }
    
    private void createConstraintInfoExpressionEvaluation(List<PConstraintInfo> resultList, 
            ExpressionEvaluation expressionEvaluation) {
        // An expression evaluation can only have its output variable unbound. All other variables shall be bound
        PVariable output = expressionEvaluation.getOutputVariable();
        Set<Set<PVariable>> bindings = Sets.newHashSet();
        Set<PVariable> affectedVariables = expressionEvaluation.getAffectedVariables();
        // All variables bound -> check
        bindings.add(affectedVariables);
        // Output variable is not bound -> extend
        bindings.add(Sets.difference(affectedVariables, Collections.singleton(output)));
        doCreateConstraintInfos(resultList, expressionEvaluation, affectedVariables, bindings);
    }

    private void createConstraintInfoTypeFilterConstraint(List<PConstraintInfo> resultList, 
            TypeFilterConstraint filter){
        // In case of type filter, all affected variables must be bound in order to execute
        Set<PVariable> affectedVariables = filter.getAffectedVariables();
        doCreateConstraintInfos(resultList, filter, affectedVariables, Collections.singleton(affectedVariables));
    }
    
    private void createConstraintInfoInequality(List<PConstraintInfo> resultList, 
            Inequality inequality){
        // In case of inequality, all affected variables must be bound in order to execute
        Set<PVariable> affectedVariables = inequality.getAffectedVariables();
        doCreateConstraintInfos(resultList, inequality, affectedVariables, Collections.singleton(affectedVariables));
    }
    
    private void createConstraintInfoAggregatorConstraint(List<PConstraintInfo> resultList, 
            PConstraint pConstraint, PVariable resultVariable){
        Set<PVariable> affectedVariables = pConstraint.getAffectedVariables();
        
        // The only variables which can be unbound are the ones which cannot be deduced by any constraint and the result variable
        Set<PVariable> canBeUnboundVariables = Sets.union(Collections.singleton(resultVariable), Sets.filter(affectedVariables, new VariableNotDeducablePredicate()));
       
        Set<Set<PVariable>> bindings = calculatePossibleBindings(canBeUnboundVariables, affectedVariables);
        
        doCreateConstraintInfos(resultList, pConstraint, affectedVariables, bindings);
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
    
    private void createConstraintInfoGeneric(List<PConstraintInfo> resultList, PConstraint pConstraint){
        Set<PVariable> affectedVariables = pConstraint.getAffectedVariables();
        
        // The only variables which can be unbound are the ones which cannot be deduced by any constraint
        Set<PVariable> canBeUnboundVariables = Sets.filter(affectedVariables, new VariableNotDeducablePredicate());
       
        Set<Set<PVariable>> bindings = calculatePossibleBindings(canBeUnboundVariables, affectedVariables);
        
        doCreateConstraintInfos(resultList, pConstraint, affectedVariables, bindings);
    }
    
    private boolean canPerformInverseNavigation(EStructuralFeature feature){
        return ( // Feature has opposite (this only possible for references)
                 hasEOpposite(feature) 
                 ||
                 (feature instanceof EReference) && ((EReference)feature).isContainment()
                 || (   // Or indexing is enabled, and the feature can be indexed (it's not a non-well-behaving 
                        // derived feature) and it's a reference TODO this constraint might not be needed. It shall be possible to find elements by an attribute value using the base indexer
                        useIndex && modelComprehension.representable(feature) && (feature instanceof EReference)
                 ));
    }
    
    private void createConstraintInfoTypeConstraint(List<PConstraintInfo> resultList, 
            TypeConstraint typeConstraint) {
        Set<PVariable> affectedVariables = typeConstraint.getAffectedVariables();
        Set<Set<PVariable>> bindings = null;
        
        IInputKey inputKey = typeConstraint.getSupplierKey();
        if(inputKey.isEnumerable()){
            bindings = Sets.powerSet(affectedVariables);
        }else{
            // For not enumerable types, this constraint can only be a check
            bindings = Collections.singleton(affectedVariables);
        }
        
        if(inputKey instanceof EStructuralFeatureInstancesKey){
            final EStructuralFeature feature = ((EStructuralFeatureInstancesKey) inputKey).getEmfKey();
            if(!canPerformInverseNavigation(feature)){
                // When inverse navigation is not allowed or not possible, filter out operation masks, where
                // the first variable would be free AND the feature is an EReference and has no EOpposite
                bindings = excludeUnnavigableOperationMasks(typeConstraint, bindings);
            }
        }
        doCreateConstraintInfos(resultList, typeConstraint, affectedVariables, bindings);
    }
    
    private void doCreateConstraintInfos(List<PConstraintInfo> constraintInfos,
            PConstraint pConstraint, Set<PVariable> affectedVariables, Iterable<Set<PVariable>> bindings) {
        Set<PConstraintInfo> sameWithDifferentBindings = Sets.newHashSet();
        for (Set<PVariable> boundVariables : bindings) {
            PConstraintInfo info = new PConstraintInfo(pConstraint, boundVariables, Sets.difference(
                    affectedVariables, boundVariables), sameWithDifferentBindings, 
                    context, costFunction);
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
            if(!boundVariablesSet.isEmpty() && !boundVariablesSet.contains(firstVariable)){
                elementsToRemove.add(boundVariablesSet);
            }
        }
        bindings = Sets.difference(bindings, elementsToRemove);
        return bindings;
    }
    
    private boolean hasEOpposite(EStructuralFeature feature) {
        if(feature instanceof EReference){
            EReference eOpposite = ((EReference) feature).getEOpposite();
            if(eOpposite != null){
                return true;
            }
        }
        return false;
    }
    
}
