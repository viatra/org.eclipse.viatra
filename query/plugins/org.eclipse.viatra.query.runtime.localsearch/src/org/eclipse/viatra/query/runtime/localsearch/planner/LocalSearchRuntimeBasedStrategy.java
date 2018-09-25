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
package org.eclipse.viatra.query.runtime.localsearch.planner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchHints;
import org.eclipse.viatra.query.runtime.localsearch.planner.cost.ICostFunction;
import org.eclipse.viatra.query.runtime.localsearch.planner.util.OperationCostComparator;
import org.eclipse.viatra.query.runtime.matchers.backend.ResultProviderRequestor;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryBackendContext;
import org.eclipse.viatra.query.runtime.matchers.planning.SubPlan;
import org.eclipse.viatra.query.runtime.matchers.planning.SubPlanFactory;
import org.eclipse.viatra.query.runtime.matchers.planning.operations.PApply;
import org.eclipse.viatra.query.runtime.matchers.planning.operations.PProject;
import org.eclipse.viatra.query.runtime.matchers.planning.operations.PStart;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;

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
 * @noreference This class is not intended to be referenced by clients.
 */
public class LocalSearchRuntimeBasedStrategy {

    /**
     * Converts a plan to the standard format
     */
    protected SubPlan convertPlan(Set<PVariable> initialBoundVariables, PlanState searchPlan) {
        PBody pBody;
        pBody = searchPlan.getAssociatedPBody();
        
        // 1. INITIALIZATION
        // Create a starting plan
        SubPlanFactory subPlanFactory = new SubPlanFactory(pBody);

        // We assume that the adornment (now the bound variables) is previously set
        SubPlan plan = subPlanFactory.createSubPlan(new PStart(initialBoundVariables));

        List<PConstraintInfo> operations = searchPlan.getOperations();
        for (PConstraintInfo pConstraintPlanInfo : operations) {
            PConstraint pConstraint = pConstraintPlanInfo.getConstraint();
            plan = subPlanFactory.createSubPlan(new PApply(pConstraint), plan);
        }

        return subPlanFactory.createSubPlan(new PProject(pBody.getSymbolicParameterVariables()), plan);
    }

    /**
     * The implementation of a local search-based algorithm to create a search plan for a flattened (and normalized)
     * PBody
     * @param pBody for which the plan is to be created
     * @param initialBoundVariables variables that are known to have already assigned values
     * @param context the backend context
     * @param resultProviderRequestor requestor for accessing result providers of called patterns
     * @param configuration the planner configuration
     * @return the complete search plan for the given {@link PBody}
     * @since 2.1
     */
    protected PlanState plan(PBody pBody, Set<PVariable> initialBoundVariables,
            IQueryBackendContext context, final ResultProviderRequestor resultProviderRequestor,
            LocalSearchHints configuration) {
        final ICostFunction costFunction = configuration.getCostFunction();
        PConstraintInfoInferrer pConstraintInfoInferrer = new PConstraintInfoInferrer(
                configuration.isUseBase(), context, resultProviderRequestor, costFunction::apply);
        
        // Create mask infos
        Set<PConstraint> constraintSet = pBody.getConstraints();
        List<PConstraintInfo> constraintInfos = 
                pConstraintInfoInferrer.createPConstraintInfos(constraintSet);
        
        // Calculate the characteristic function
        // The characteristic function tells whether a given adornment is backward reachable from the (B)* state, where
        // each variable is bound.
        // The characteristic function is represented as a set of set of variables
        // TODO this calculation is not not implemented yet, thus the contents of the returned set is not considered later
        List<Set<PVariable>> reachableBoundVariableSets = reachabilityAnalysis(pBody, constraintInfos);
        int k = configuration.getRowCount();
        PlanState searchPlan = calculateSearchPlan(pBody, initialBoundVariables, k, reachableBoundVariableSets, constraintInfos);
        return searchPlan;
    }

    private PlanState calculateSearchPlan(PBody pBody, Set<PVariable> initialBoundVariables, int k,
            List<Set<PVariable>> reachableBoundVariableSets, List<PConstraintInfo> allMaskInfos) {

        // rename for better understanding
        Set<PVariable> boundVariables = initialBoundVariables;
        
        Set<PVariable> freeVariables = Sets.difference(pBody.getUniqueVariables(), initialBoundVariables);

        int n = freeVariables.size();

        List<List<PlanState>> stateTable = initializeStateTable(k, n);

        // Set initial state: begin with an empty operation list
        PlanState initialState = new PlanState(pBody, new ArrayList<>(), boundVariables);
        
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
                    // for each present EXTEND operation
                    PlanState newState = calculateNextState(currentState, constraintInfo);
                    
                    if(currentState.getBoundVariables().size() == newState.getBoundVariables().size()){
                        // This means no variable binding was done, go on with the next constraint info
                        continue;
                    }
                    int i2 = Sets.difference(pBody.getUniqueVariables(), newState.getBoundVariables()).size();
                    updateOperations(newState, currentState, constraintInfo); // also perform any CHECK operations 
                    
                    List<Integer> newIndices = determineIndices(stateTable, i2, newState, k);
                    int a = newIndices.get(0);
                    int c = newIndices.get(1);

                    if (checkInsertCondition(stateTable.get(i2), newState, reachableBoundVariableSets, a, c, k)) {
                        insert(stateTable,i2, newState, a, c, k);
                    }
                }
            }
        }

        return stateTable.get(0).get(0);
    }

    private List<List<PlanState>> initializeStateTable(int k, int n) {
        List<List<PlanState>> stateTable = new ArrayList<>();
        // Initialize state table and fill it with null
        for (int i = 0; i <= n ; i++) {
            stateTable.add(new ArrayList<>());
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

        List<PConstraintInfo> extensions = new ArrayList<>();

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
        List<Integer> acIndices = new ArrayList<>();
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
        ArrayList<PConstraintInfo> newOperationsList = new ArrayList<>(currentState.getOperations());
        newOperationsList.add(constraintInfo);

        // Bound the free variables
        SetView<PVariable> allBoundVariables = Sets.union(currentState.getBoundVariables(), constraintInfo.getFreeVariables());
        PlanState newState = new PlanState(currentState.getAssociatedPBody(), newOperationsList, allBoundVariables.immutableCopy());

        return newState;
    }

    private List<Set<PVariable>> reachabilityAnalysis(PBody pBody, List<PConstraintInfo> constraintInfos) {
        // TODO implement reachability analisys, also save/persist the results somewhere
        List<Set<PVariable>> reachableBoundVariableSets = new ArrayList<>();
        return reachableBoundVariableSets;
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
