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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.viatra.query.runtime.localsearch.planner.util.OperationCostComparator;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * This class represents the state of the plan during planning
 * 
 * @author Marton Bur
 *
 */
public class PlanState {

    private PBody pBody;
    private List<PConstraintInfo> operationsList;
    private Set<PVariable> boundVariables;
    private float cost;
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
        cost = 0;
        float cummulativeProduct = 1;
        for (PConstraintInfo constraintInfo : operationsList) {
            cummulativeProduct *= constraintInfo.getCost();
            cost += cummulativeProduct;
        }

    }

    public void updateOperations(List<PConstraintInfo> allPotentialExtendInfos, List<PConstraintInfo> allPotentialChecks) {
        futureChecks = Lists.newArrayList();
        futureExtends = Lists.newArrayList();
        presentExtends = Lists.newArrayList();

        Set<PConstraintInfo> allUsedAppliedConstraints = Sets.newHashSet();
        for (PConstraintInfo pConstraintPlanInfo : operationsList) {
            allUsedAppliedConstraints.addAll(pConstraintPlanInfo.getSameWithDifferentBindings());
        }

        final Set<PConstraintInfo> allUsedAppliedConstraintsArg = allUsedAppliedConstraints;

        Collection<PConstraintInfo> allRelevantExtendInfos = Collections2.filter(allPotentialExtendInfos,
            new Predicate<PConstraintInfo>() {

                @Override
                public boolean apply(PConstraintInfo input) {
                    return !allUsedAppliedConstraintsArg.contains(input) && isPossibleExtend(input);
                }
            });
        // categorize extend constraint infos
        categorizeExtends(allRelevantExtendInfos);
        
        Collection<PConstraintInfo> allRelevantCheckInfos = Collections2.filter(allPotentialChecks,
            new Predicate<PConstraintInfo>() {

                @Override
                public boolean apply(PConstraintInfo input) {
                    return !allUsedAppliedConstraintsArg.contains(input) && isPossibleCheck(input);
                }
            });
        // categorize check constraint infos
        categorizeChecks(allRelevantCheckInfos);
        
        // sort them by cost
        // TODO this sort is just for sure, most likely it's unnecessary
        OperationCostComparator infoComparator = new OperationCostComparator();
        
        Collections.sort(futureChecks, infoComparator);
        Collections.sort(futureExtends, infoComparator);
        Collections.sort(presentExtends, infoComparator);
    }

    private void categorizeChecks(Collection<PConstraintInfo> allRelevantCheckInfos) {
        for (PConstraintInfo checkInfo : allRelevantCheckInfos) {
            PConstraintCategory category = checkInfo.getCategory(pBody, boundVariables);
            if(category == PConstraintCategory.PRESENT){
                operationsList.add(checkInfo);
            } else if (category == PConstraintCategory.FUTURE) {
                futureChecks.add(checkInfo);
            } else {
                // discard
            }
        }
    }

    private void categorizeExtends(Collection<PConstraintInfo> allRelevantExtendInfos) {
        for (PConstraintInfo constraintInfo : allRelevantExtendInfos) {
            PConstraintCategory category = constraintInfo.getCategory(pBody, boundVariables);
            if (category == PConstraintCategory.FUTURE) {
                futureExtends.add(constraintInfo);
            } else if (category == PConstraintCategory.PRESENT) {
                presentExtends.add(constraintInfo);
            } else {
                // do not categorize past operations
            }
        }
    }

    /**
     * @param constraintInfo
     * @return true, if constraintInfo is a present or future extend w.r.t. the current operation binding stored in
     *         the state
     */
    private boolean isPossibleExtend(PConstraintInfo constraintInfo) {

        PConstraintCategory category = constraintInfo.getCategory(getAssociatedPBody(), boundVariables);
        if (category == PConstraintCategory.PAST) {
            return false;
        } else {
            return constraintInfo.getFreeVariables().size() > 0;
        }
    }

    /**
     * @param constraintInfo
     * @return true, if constraintInfo is a present or future check w.r.t. the current operation binding
     */
    private boolean isPossibleCheck(PConstraintInfo constraintInfo) {
        PConstraintCategory category = constraintInfo.getCategory(getAssociatedPBody(), boundVariables);
        if (category == PConstraintCategory.PAST) {
            return false;
        } else {
            return constraintInfo.getFreeVariables().size() == 0;
        }
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
    public float getCost() {
        return cost;
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
