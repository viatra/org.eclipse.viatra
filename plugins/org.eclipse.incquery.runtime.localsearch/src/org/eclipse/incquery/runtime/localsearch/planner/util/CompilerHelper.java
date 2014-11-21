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
package org.eclipse.incquery.runtime.localsearch.planner.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.runtime.matchers.planning.SubPlan;
import org.eclipse.incquery.runtime.matchers.planning.operations.PApply;
import org.eclipse.incquery.runtime.matchers.planning.operations.POperation;
import org.eclipse.incquery.runtime.matchers.psystem.PConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 
 * Helper methods for compiling SubPlans
 * 
 * @author Marton Bur
 *
 */
public class CompilerHelper {

    public static Map<PVariable, Integer> createVariableMapping(SubPlan plan) {
        Map<PVariable, Integer> variableMapping = Maps.newHashMap();

        int variableNumber = 0;

        List<PVariable> symbolicParameterVariables = plan.getBody().getSymbolicParameterVariables();
        for (PVariable pVariable : symbolicParameterVariables) {
            variableMapping.put(pVariable, variableNumber++);
        }

        // Reason for complexity here: not all variables were given back for call plan.getAllDeducedVariables();
        Set<PVariable> allVariables = Sets.newHashSet();
        Set<PConstraint> allEnforcedConstraints = plan.getAllEnforcedConstraints();
        for (PConstraint pConstraint : allEnforcedConstraints) {
            allVariables.addAll(pConstraint.getAffectedVariables());
        }
        for (PVariable pVariable : allVariables) {
            if (!variableMapping.containsKey(pVariable)) {
                variableMapping.put(pVariable, variableNumber++);
            }
        }

        return variableMapping;
    }

    public static Map<PConstraint, Set<Integer>> cacheVariableBindings(SubPlan plan, Map<PVariable, Integer> variableMappings, Set<Integer> adornment) {
        Map<PConstraint, Set<Integer>> variableBindings = Maps.newHashMap();
        Map<PConstraint, Set<PVariable>> variableBindingsDebug = Maps.newHashMap();

        POperation operation;
        while (plan.getParentPlans().size() > 0) {
            // Get the operation
            operation = plan.getOperation();
            // Get bound variables from previous plan
            plan = plan.getParentPlans().get(0);

            if (operation instanceof PApply) {
                Set<PConstraint> enforcedConstraint = plan.getAllEnforcedConstraints();
                Set<PVariable> allDeducedVariables = Sets.newHashSet();
                for (PConstraint pConstraint : enforcedConstraint) {
                    allDeducedVariables.addAll(pConstraint.getAffectedVariables());
                }
                
                variableBindingsDebug.put(((PApply) operation).getPConstraint(), allDeducedVariables);
                Set<Integer> boundVariables = Sets.newHashSet();
                boundVariables.addAll(adornment);
                for (PVariable pVariable : allDeducedVariables) {
                    boundVariables.add(variableMappings.get(pVariable));
                }
                variableBindings.put(((PApply) operation).getPConstraint(), boundVariables);
            }
        }
        operation = plan.getOperation();
        if (operation instanceof PApply) {
            Set<PVariable> allDeducedVariables = Sets.newHashSet();
            allDeducedVariables.addAll(((PApply) operation).getPConstraint().getAffectedVariables());
            variableBindingsDebug.put(((PApply) operation).getPConstraint(), allDeducedVariables);
            Set<Integer> boundVariables = Sets.newHashSet();
            boundVariables.addAll(adornment);
            for (PVariable pVariable : allDeducedVariables) {
                boundVariables.add(variableMappings.get(pVariable));
            }
            variableBindings.put(((PApply) operation).getPConstraint(), boundVariables);
        }

        return variableBindings;
    }

    /**
     * Extracts the operations from a SubPlan into a list of POperations in the order of execution
     * 
     * @param plan
     * @return
     */
    public static List<POperation> createOperationsList(SubPlan plan) {
        List<POperation> operationsList = Lists.newArrayList();
        while (plan.getParentPlans().size() > 0) {
            operationsList.add(plan.getOperation());
            plan = plan.getParentPlans().get(0);
        }
        operationsList.add(plan.getOperation());

        return Lists.reverse(operationsList);
    }

}
