/*******************************************************************************
 * Copyright (c) 2010-2014, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.localsearch.planner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.incquery.runtime.localsearch.matcher.integration.LocalSearchHintKeys;
import org.eclipse.incquery.runtime.localsearch.planner.util.OperationCostComparator;
import org.eclipse.incquery.runtime.matchers.backend.IQueryBackendHintProvider;
import org.eclipse.incquery.runtime.matchers.context.IInputKey;
import org.eclipse.incquery.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.incquery.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.incquery.runtime.matchers.planning.SubPlan;
import org.eclipse.incquery.runtime.matchers.planning.SubPlanFactory;
import org.eclipse.incquery.runtime.matchers.planning.operations.PApply;
import org.eclipse.incquery.runtime.matchers.planning.operations.PProject;
import org.eclipse.incquery.runtime.matchers.planning.operations.PStart;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.PatternMatchCounter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeConstraint;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * This class contains the logic for local search plan calculation based on costs of the operations.
 * Its name refers to the fact that the strategy tries to use as much information as available about
 * the model on which the matching is initiated. When no runtime info is available, it falls back to 
 * the information available from the metamodel durint operation cost calculation.
 * 
 * The implementation is based on the paper "Gergely Varró, Frederik Deckwerth, Martin Wieber, and Andy Schürr: 
 * An algorithm for generating model-sensitive search plans for pattern matching on EMF models" 
 * (DOI: 10.1007/s10270-013-0372-2)
 * 
 * @author Marton Bur
 *
 */
public class LocalSearchRuntimeBasedStrategy {

	private boolean allowInverseNavigation;
	private boolean useIndex;
	
    public LocalSearchRuntimeBasedStrategy() {
        this(true,true);
    }

    public LocalSearchRuntimeBasedStrategy(final boolean allowInverseNavigation, boolean useIndex) {
        this.allowInverseNavigation = allowInverseNavigation;
        this.useIndex = useIndex;
    }
	
    /**
     * The implementation of a local search-based algorithm to create a search plan for a flattened (and normalized)
     * PBody
     * @param pBody for which the plan is to be created
     * @param logger that logs the happenings
     * @param initialBoundVariables variables that are known to have already assigned values
     * @param metaContext the metamodel related information
     * @param runtimeContext the instance model related information
     * @param hints the optional hints for the plan creation
     * @return the complete search plan for the given {@link PBody}
     */
    public SubPlan plan(PBody pBody, Logger logger, Set<PVariable> initialBoundVariables,
            IQueryMetaContext metaContext, IQueryRuntimeContext runtimeContext, Map<String, Object> hints) {

        // 1. INITIALIZATION
        // Create a starting plan
        SubPlanFactory subPlanFactory = new SubPlanFactory(pBody);

        // We assume that the adornment (now the bound variables) is previously set
        SubPlan plan = subPlanFactory.createSubPlan(new PStart(initialBoundVariables));
        // Create mask infos
        Set<PConstraint> constraintSet = pBody.getConstraints();
        List<PConstraintInfo> constraintInfos = createPConstraintInfos(constraintSet, runtimeContext);

        // Calculate the characteristic function
        // The characteristic function tells whether a given adornment is backward reachable from the (B)* state, where
        // each variable is bound.
        // The characteristic function is represented as a set of set of variables
        // TODO this calculation is not not implemented yet, thus the contents of the returned set is not considered later
        List<Set<PVariable>> reachableBoundVariableSets = reachabilityAnalysis(pBody, constraintInfos);
        int k = 4;
        Integer rowCountHint= (Integer) hints.get(LocalSearchHintKeys.PLANNER_TABLE_ROW_COUNT);
        if(rowCountHint != null){
            k = rowCountHint;
        }
        PlanState searchPlan = calculateSearchPlan(pBody, initialBoundVariables, k, reachableBoundVariableSets, constraintInfos);

        List<PConstraintInfo> operations = searchPlan.getOperations();
        for (PConstraintInfo pConstraintPlanInfo : operations) {
            PConstraint pConstraint = pConstraintPlanInfo.getConstraint();
            plan = subPlanFactory.createSubPlan(new PApply(pConstraint), plan);
        }

        return subPlanFactory.createSubPlan(new PProject(pBody.getSymbolicParameterVariables()), plan);
    }

    private PlanState calculateSearchPlan(PBody pBody, Set<PVariable> initialBoundVariables, int k,
            List<Set<PVariable>> reachableBoundVariableSets, List<PConstraintInfo> allMaskInfos) {

        // rename for better understanding
        Set<PVariable> boundVariables = initialBoundVariables;
        Set<PVariable> freeVariables = Sets.difference(pBody.getUniqueVariables(), initialBoundVariables);

        int n = freeVariables.size();

        List<List<PlanState>> stateTable = initializeStateTable(k, n);

        // Set initial state: begin with an empty operation list
        PlanState initialState = new PlanState(pBody, Lists.<PConstraintInfo> newArrayList(), boundVariables);
        
        // It is needed to start from a list that is ordered by the cost of the constraint application
        OperationCostComparator infoComparator = new OperationCostComparator();
        Collections.sort(allMaskInfos, infoComparator);
        
        // Initial state creation, categorizes all operations; add present checks to operationsList
        initialState.updateOperations(allMaskInfos, allMaskInfos);
        stateTable.get(n).add(0, initialState);
        
        // stateTable.get(0) will contain the states with adornment B*
        for (int i = n; i > 0; i--) {
            for (int j = 0; j < k && j < stateTable.get(i).size(); j++) {
                PlanState currentState = stateTable.get(i).get(j);

                for (PConstraintInfo constraintInfo : currentState.getPresentExtends()) {
                    // for each present operation
                    PlanState newState = calculateNextState(currentState, constraintInfo);
                    int i2 = Sets.difference(pBody.getUniqueVariables(), newState.getBoundVariables()).size();
                    if(currentState.getBoundVariables().size() == newState.getBoundVariables().size()){
                        // This means no variable binding was done, go on with the next constraint info
                        continue;
                    }
                    List<Integer> newIndices = determineIndices(stateTable, i2, newState, k);
                    int a = newIndices.get(0);
                    int c = newIndices.get(1);

                    if (checkInsertCondition(stateTable.get(i2), newState, reachableBoundVariableSets, a, c, k)) {
                        updateOperations(newState, currentState, constraintInfo);
                        insert(stateTable,i2, newState, a, c, k);
                    }
                }
            }
        }
        
        return stateTable.get(0).get(0);
    }

    private List<List<PlanState>> initializeStateTable(int k, int n) {
        List<List<PlanState>> stateTable = Lists.newArrayList();
        // Initialize state table and fill it with null
        for (int i = 0; i <= n ; i++) {
            stateTable.add(Lists.<PlanState>newArrayList());
        }
        return stateTable;
    }

    private void insert(List<List<PlanState>> stateTable, int idx, PlanState newState, int a, int c, int k) {
        stateTable.get(idx).add(c, newState);
        while(stateTable.get(idx).size() > k){
            // Truncate back to size k when grows too big
            stateTable.set(idx, stateTable.get(idx).subList(0, k));
        }
    }

    private void updateOperations(PlanState newState, PlanState currentState, PConstraintInfo constraintInfo) {
        List<PConstraintInfo> presentExtends = currentState.getPresentExtends();
        List<PConstraintInfo> futureExtends = currentState.getFutureExtends();
        // Compile the list of extend operations
        List<PConstraintInfo> extendz = merge(presentExtends,futureExtends);
                
        // Check operations
        newState.updateOperations(extendz, currentState.getFutureChecks());
        
        return;
    }

    private List<PConstraintInfo> merge(List<PConstraintInfo> presentExtensionsForState, List<PConstraintInfo> futureExtensionsForState) {
        int presentExtensionsIndex = 0;
        int futureExtensionsIndex = 0;
        int extensionIndex = 0;

        List<PConstraintInfo> extensions = Lists.newArrayList();

        while (presentExtensionsIndex < presentExtensionsForState.size() || futureExtensionsIndex < futureExtensionsForState.size()) {
            if (futureExtensionsIndex >= futureExtensionsForState.size() || (presentExtensionsIndex < presentExtensionsForState.size() && presentExtensionsForState.get(presentExtensionsIndex).getCost() < futureExtensionsForState.get(futureExtensionsIndex).getCost())) {
                extensions.add(extensionIndex, presentExtensionsForState.get(presentExtensionsIndex));
                presentExtensionsIndex++;
            } else {
                extensions.add(extensionIndex, futureExtensionsForState.get(futureExtensionsIndex));
                futureExtensionsIndex++;
            }
            extensionIndex++;
        }
        return extensions;
    }

    private boolean checkInsertCondition(List<PlanState> list, PlanState newState,
            List<Set<PVariable>> reachableBoundVariableSets, int a, int c, int k) {
//        boolean isAmongBestK = (a == (k + 1)) && c < a && reachableBoundVariableSets.contains(newState.getBoundVariables());
        boolean isAmongBestK = a == k && c < a ;
        boolean isBetterThanCurrent = a < k && c <= a;

        return isAmongBestK || isBetterThanCurrent;
    }

    private List<Integer> determineIndices(List<List<PlanState>> stateTable, int i2, PlanState newState, int k) {
        int a = k;
        int c = 0;
        List<Integer> acIndices = Lists.newArrayList();
        for (int j = 0; j < k; j++) {
            if (j < stateTable.get(i2).size()) {
                PlanState stateInTable = stateTable.get(i2).get(j);
                if (newState.getBoundVariables().equals(stateInTable.getBoundVariables())) {
                    // The new state has the same adornment as the stored one - they are not adornment disjoint
                    a = j;
                }
                if (newState.getCost() >= stateInTable.getCost()) {
                    c = j + 1;
                }
            } else {
                break;
            }
        }

        acIndices.add(a);
        acIndices.add(c);
        return acIndices;
    }

    private PlanState calculateNextState(PlanState currentState, PConstraintInfo constraintInfo) {
        // Create operation list based on the current state
        ArrayList<PConstraintInfo> newOperationsList = Lists.newArrayList(currentState.getOperations());
        newOperationsList.add(constraintInfo);

        // Bound the free variables
        SetView<PVariable> allBoundVariables = Sets.union(currentState.getBoundVariables(), constraintInfo.getFreeVariables());
        PlanState newState = new PlanState(currentState.getAssociatedPBody(), newOperationsList, allBoundVariables.immutableCopy());

        return newState;
    }

    private List<Set<PVariable>> reachabilityAnalysis(PBody pBody, List<PConstraintInfo> constraintInfos) {
        // TODO implement reachability analisys, also save/persist the results somewhere
        List<Set<PVariable>> reachableBoundVariableSets = Lists.newArrayList();
        return reachableBoundVariableSets;
    }

    /**
     * Create all possible application condition for all constraint
     * 
     * @param constraintSet the set of constraints
     * @param runtimeContext the model dependent runtime contest
     * @return a collection of the wrapper PConstraintInfo objects with all the allowed application conditions
     */
    private List<PConstraintInfo> createPConstraintInfos(Set<PConstraint> constraintSet, IQueryRuntimeContext runtimeContext) {
        List<PConstraintInfo> constraintInfos = Lists.newArrayList();

        for (PConstraint pConstraint : constraintSet) {
            if(pConstraint instanceof TypeConstraint){
                Set<PVariable> affectedVariables = pConstraint.getAffectedVariables();
                Set<Set<PVariable>> bindings = Sets.powerSet(affectedVariables);
                doCreateConstraintInfosForTypeConstraint(runtimeContext, constraintInfos, (TypeConstraint)pConstraint, affectedVariables, bindings);
            } else {
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
                
                doCreateConstraintInfos(runtimeContext, constraintInfos, pConstraint, affectedVariables, bindings);
            }
        }
        return constraintInfos;
    }

    private void doCreateConstraintInfosForTypeConstraint(IQueryRuntimeContext runtimeContext,
            List<PConstraintInfo> constraintInfos, TypeConstraint typeConstraint, Set<PVariable> affectedVariables,
            Set<Set<PVariable>> bindings) {
        if(!allowInverseNavigation){
            // When inverse navigation is not allowed, filter out operation masks, where
            // the first variable would be free AND the feature is an EReference and has no EOpposite
            bindings = excludeInverseNavigationOperationMasks(typeConstraint, bindings);
        } else {
            // Also do the above case, if it is an EReference with no EOpposite, or is an EAttribute
            IInputKey inputKey = typeConstraint.getSupplierKey();
            if(inputKey instanceof EStructuralFeatureInstancesKey){
                final EStructuralFeature feature = ((EStructuralFeatureInstancesKey) inputKey).getEmfKey();
                if(feature instanceof EReference){
                    if(((EReference) feature).getEOpposite() == null){                        
                        bindings = excludeInverseNavigationOperationMasks(typeConstraint, bindings);
                    }
                } else {
                    bindings = excludeInverseNavigationOperationMasks(typeConstraint, bindings);
                }
            }
        }
        doCreateConstraintInfos(runtimeContext, constraintInfos, typeConstraint, affectedVariables, bindings);
    }
    private Set<Set<PVariable>> excludeInverseNavigationOperationMasks(TypeConstraint typeConstraint, Set<Set<PVariable>> bindings) {
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

    protected EClassifier extractClassifierLiteral(String packageUriAndClassifierName) {
        int lastSlashPosition = packageUriAndClassifierName.lastIndexOf('/');
        int scopingPosition = packageUriAndClassifierName.lastIndexOf("::");
        String packageUri = packageUriAndClassifierName.substring(scopingPosition + 2, lastSlashPosition);
        String classifierName = packageUriAndClassifierName.substring(lastSlashPosition + 1);

        EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageUri);
        Preconditions.checkState(ePackage != null, "EPackage %s not found in EPackage Registry.", packageUri);
        EClassifier literal = ePackage.getEClassifier(classifierName);
        Preconditions.checkState(literal != null, "Classifier %s not found in EPackage %s", classifierName, packageUri);
        return literal;
    }

}
