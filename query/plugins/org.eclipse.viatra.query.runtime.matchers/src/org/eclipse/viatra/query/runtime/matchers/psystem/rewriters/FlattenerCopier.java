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
package org.eclipse.viatra.query.runtime.matchers.psystem.rewriters;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * This rewriter class can add new equality constraints to the copied body
 * 
 * @author Marton Bur
 *
 */
class FlattenerCopier extends PBodyCopier {

    private Map<PositivePatternCall, PBody> callsToFlatten;

    private Table<PositivePatternCall, PVariable, PVariable> variableCopyTable = HashBasedTable.create();

    protected void copyVariable(PositivePatternCall contextPatternCall, PVariable variable, String newName) {
        PVariable newPVariable = body.getOrCreateVariableByName(newName);
        variableCopyTable.put(contextPatternCall, variable, newPVariable);
        variableMapping.put(variable, newPVariable);
    }

    /**
     * Merge all variables and constraints from the body called through the given pattern call to a target body. If
     * multiple bodies are merged into a single one, use the renamer and filter options to avoid collisions.
     * 
     * @param sourceBody
     * @param namingTool
     * @param filter
     */
    public void mergeBody(PositivePatternCall contextPatternCall, IVariableRenamer namingTool,
            IConstraintFilter filter) {

        PBody sourceBody = callsToFlatten.get(contextPatternCall);

        // Copy variables
        Set<PVariable> allVariables = sourceBody.getAllVariables();
        for (PVariable pVariable : allVariables) {
            if (pVariable.isUnique()) {
                copyVariable(contextPatternCall, pVariable,
                        namingTool.createVariableName(pVariable, sourceBody.getPattern()));
            }
        }

        // Copy constraints which are not filtered
        Set<PConstraint> constraints = sourceBody.getConstraints();
        for (PConstraint pConstraint : constraints) {
            if (!(pConstraint instanceof ExportedParameter) && !filter.filter(pConstraint)) {
                copyConstraint(pConstraint);
            }
        }
    }

    public FlattenerCopier(PQuery query, Map<PositivePatternCall, PBody> callsToFlatten) {
        super(query);
        this.callsToFlatten = callsToFlatten;
    }

    @Override
    protected void copyPositivePatternCallConstraint(PositivePatternCall positivePatternCall) {

        if (!callsToFlatten.containsKey(positivePatternCall)) {
            // If the call was not flattened, copy the constraint
            super.copyPositivePatternCallConstraint(positivePatternCall);
        } else {
            PBody calledBody = callsToFlatten.get(positivePatternCall);
            Preconditions.checkNotNull(calledBody);
            Preconditions.checkArgument(positivePatternCall.getReferredQuery().equals(calledBody.getPattern()));

            List<PVariable> symbolicParameters = calledBody.getSymbolicParameterVariables();
            Object[] elements = positivePatternCall.getVariablesTuple().getElements();
            for (int i = 0; i < elements.length; i++) {
                // Create equality constraints between the caller PositivePatternCall and the corresponding body
                // parameter variables
                createEqualityConstraint((PVariable) elements[i], symbolicParameters.get(i), positivePatternCall);
            }

        }
    }

    private void createEqualityConstraint(PVariable pVariable1, PVariable pVariable2,
            PositivePatternCall contextPatternCall) {
        PVariable who = variableMapping.get(pVariable1);
        PVariable withWhom = variableCopyTable.get(contextPatternCall, pVariable2);
        new Equality(body, who, withWhom);
    }

}
