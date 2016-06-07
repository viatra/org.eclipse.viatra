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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra.query.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.Inequality;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.PatternMatchCounter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * @author Grill Balázs
 *
 */
class PConstraintInfoInferrer {

    private final boolean allowInverseNavigation;
    private final boolean useIndex;
    
    /**
     * 
     */
    public PConstraintInfoInferrer(boolean allowInverseNavigation, boolean useIndex) {
        this.allowInverseNavigation = allowInverseNavigation;
        this.useIndex = useIndex;
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
            // Do not create mask info for exported parameter, for it is only a symbolic constraint
        } else if(pConstraint instanceof TypeConstraint){
            createConstraintInfoTypeConstraint(resultList, runtimeContext, (TypeConstraint)pConstraint);
        } else if (pConstraint instanceof Inequality){
            createConstraintInfoInequality(resultList, runtimeContext, (Inequality) pConstraint);
        } else {
            createConstraintInfoGeneric(resultList, runtimeContext, pConstraint);
        }
    }
    
    private void createConstraintInfoInequality(List<PConstraintInfo> resultList, IQueryRuntimeContext runtimeContext, Inequality inequality){
        // In case of inequality, all affected variables must be bound in order to execute
        Set<PVariable> affectedVariables = inequality.getAffectedVariables();
        doCreateConstraintInfos(runtimeContext, resultList, inequality, affectedVariables, Collections.singleton(affectedVariables));
    }
    
    private void createConstraintInfoGeneric(List<PConstraintInfo> resultList, IQueryRuntimeContext runtimeContext, PConstraint pConstraint){
        // Create constraint infos so that only single use variables can be unbound
        Set<PVariable> affectedVariables = pConstraint.getAffectedVariables();
        
        Set<PVariable> singleUseVariables = Sets.newHashSet();
        for (PVariable pVariable : affectedVariables) {
            Set<PConstraint> allReferringConstraints = pVariable.getReferringConstraints();
            // Filter out exported parameter constraints
            Set<ExportedParameter> referringExportedParameters = pVariable.getReferringConstraintsOfType(ExportedParameter.class);
            SetView<PConstraint> trueReferringConstraints = Sets.difference(allReferringConstraints, referringExportedParameters);
            if(trueReferringConstraints.size() == 1){
                singleUseVariables.add(pVariable);
            }
        }
        SetView<PVariable> nonSingleUseVariables = Sets.difference(affectedVariables, singleUseVariables);
        // Generate bindings by taking the unioning each element of the power set with the set of non-single use variables
        Set<Set<PVariable>> singleUseVariablesPowerSet = Sets.powerSet(singleUseVariables);
        Set<Set<PVariable>> bindings = Sets.newHashSet();
        for (Set<PVariable> set : singleUseVariablesPowerSet) {
            bindings.add(Sets.newHashSet(set));
        }
        for (Set<PVariable> set : bindings) {
            set.addAll(nonSingleUseVariables);
        }
        
        if(pConstraint instanceof PatternMatchCounter){
            // in cases of this type, the deduced variables will contain only the result variable
            final PVariable resultVariable = pConstraint.getDeducedVariables().iterator().next();
            Set<Set<PVariable>> additionalBindings = Sets.newHashSet();
            for (Set<PVariable> binding : bindings) {
                if(binding.contains(resultVariable)){
                    Collection<PVariable> filteredBinding = Collections2.filter(binding, new Predicate<PVariable>() {
                        @Override
                        public boolean apply(PVariable input) {
                            return input != resultVariable;
                        }
                    });
                    additionalBindings.add(Sets.newHashSet(filteredBinding));
                }
                
            }
            bindings.addAll(additionalBindings);
        }
        
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
                    affectedVariables, boundVariables), sameWithDifferentBindings, runtimeContext);
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
