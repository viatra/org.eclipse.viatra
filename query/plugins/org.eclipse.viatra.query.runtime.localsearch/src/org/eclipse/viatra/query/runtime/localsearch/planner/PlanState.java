/*******************************************************************************
 * Copyright (c) 2010-2015, Marton Bur, Zoltan Ujhelyi, Akos Horvath, Istvan Rath and Daniel Varro
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.viatra.query.runtime.localsearch.planner.util.OperationCostComparator;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;

/**
 * This class represents the state of the plan during planning.
 * 
 * <p> A PlanState represents a sequence of operations (operationsList) and caches the computed cost 
 * for this operation sequence. The list and the cost are initialized in the constructor. 
 * However, #categorizeChecks() also updates the operations list (by suffixing checks)
 * 
 * @author Marton Bur
 * @noreference This class is not intended to be referenced by clients.
 */
public class PlanState {

    private PBody pBody;
    private List<PConstraintInfo> operationsList;
    private Set<PVariable> boundVariables;
    
    private double cummulativeProduct = 1;
    private double cost = 0;

    /*
     * For a short explanation of past, present and future operations,
     * see class 
     */
    private List<PConstraintInfo> futureChecks;
    private List<PConstraintInfo> futureExtends;
    private List<PConstraintInfo> presentExtends;

    public PlanState(PBody pBody, List<PConstraintInfo> operationsList, Set<PVariable> boundVariables) {

        this.pBody = pBody;
        this.operationsList = operationsList;
        this.boundVariables = boundVariables;

        // Calculate and store cost for the associated search plan
        for (PConstraintInfo constraintInfo : operationsList) {
            accountCost(constraintInfo);
        }

    }

    private void accountCost(PConstraintInfo constraintInfo) {
        double constraintCost = constraintInfo.getCost();
        double branchFactor = constraintCost;
        if (constraintCost > 0){
            cost += cummulativeProduct * constraintCost;
            cummulativeProduct *= branchFactor;
        }
    }

    public void updateOperations(List<PConstraintInfo> allPotentialExtendInfos, List<PConstraintInfo> allPotentialChecks) {
        futureChecks = new ArrayList<>();
        futureExtends = new ArrayList<>();
        presentExtends = new ArrayList<>();

        Set<PConstraintInfo> allUsedAppliedConstraints = new HashSet<>();
        for (PConstraintInfo pConstraintPlanInfo : operationsList) {
            allUsedAppliedConstraints.addAll(pConstraintPlanInfo.getSameWithDifferentBindings());
        }

        final Set<PConstraintInfo> allUsedAppliedConstraintsArg = allUsedAppliedConstraints;

        // categorize extend constraint infos
        allPotentialExtendInfos.stream().filter(
            input -> !allUsedAppliedConstraintsArg.contains(input) && isPossibleExtend(input))
                .forEach(this::categorizeExtend);
        
        // categorize check constraint infos
        allPotentialChecks.stream().filter(
            input -> !allUsedAppliedConstraintsArg.contains(input) && isPossibleCheck(input))
                .forEach(this::categorizeCheck);
        
        // sort them by cost
        // TODO this sort is just for sure, most likely it's unnecessary
        OperationCostComparator infoComparator = new OperationCostComparator();
        
        Collections.sort(futureChecks, infoComparator);
        Collections.sort(futureExtends, infoComparator);
        Collections.sort(presentExtends, infoComparator);
    }

    private void categorizeCheck(PConstraintInfo checkInfo) {
        PConstraintCategory category = checkInfo.getCategory(pBody, boundVariables);
        if(category == PConstraintCategory.PRESENT){
            operationsList.add(checkInfo);
            accountCost(checkInfo);
        } else if (category == PConstraintCategory.FUTURE) {
            futureChecks.add(checkInfo);
        } else {
            // discard
        }
    }

    private void categorizeExtend(PConstraintInfo constraintInfo) {
        PConstraintCategory category = constraintInfo.getCategory(pBody, boundVariables);
        if (category == PConstraintCategory.FUTURE) {
            futureExtends.add(constraintInfo);
        } else if (category == PConstraintCategory.PRESENT) {
            presentExtends.add(constraintInfo);
        } else {
            // do not categorize past operations
        }
    }

    /**
     * @param constraintInfo
     * @return true, if constraintInfo is a present or future extend w.r.t. the current operation binding stored in
     *         the state
     */
    private boolean isPossibleExtend(PConstraintInfo constraintInfo) {
        return !constraintInfo.getFreeVariables().isEmpty();
    }

    /**
     * @param constraintInfo
     * @return true, if constraintInfo is a present or future check w.r.t. the current operation binding
     */
    private boolean isPossibleCheck(PConstraintInfo constraintInfo) {
        return constraintInfo.getFreeVariables().isEmpty();
    }

    public PBody getAssociatedPBody() {
        return pBody;
    }

    public List<PConstraintInfo> getOperations() {
        return operationsList;
    }

    public Set<PVariable> getBoundVariables() {
        return boundVariables;
    }

    /**
     * @return the derived cost of the plan contained in the state
     */
    public double getCost() {
        return cost;
    }

    
    /**
     * @return cumulative branching factor
     * @since 2.1
     */
    public double getCummulativeProduct() {
        return cummulativeProduct;
    }

    public List<PConstraintInfo> getFutureChecks() {
        return futureChecks;
    }

    public List<PConstraintInfo> getFutureExtends() {
        return futureExtends;
    }

    public List<PConstraintInfo> getPresentExtends() {
        return presentExtends;
    }

}
