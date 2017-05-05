/*******************************************************************************
 * Copyright (c) 2010-2015, Marton Bur, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.planner.cost;

import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.planning.SubPlan;
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExpressionEvaluation;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.Inequality;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * A predicate that filters out constraints that cannot be evaluated at a selected search plan state.
 * 
 * @author Marton Bur, Zoltan Ujhelyi
 *
 */
public final class EvaluablePConstraint implements Predicate<PConstraint> {
    private final SubPlan plan;
    private boolean allowInverseNavigation;

    public EvaluablePConstraint(SubPlan plan, boolean allowInverseNavigation) {
        this.plan = plan;
        this.allowInverseNavigation = allowInverseNavigation;
    }

    @Override
    public boolean apply(PConstraint input) {
        if (input instanceof Inequality) {
            return Sets.difference(input.getAffectedVariables(), plan.getAllDeducedVariables()).isEmpty();
        } else if (input instanceof ExpressionEvaluation) {
            PVariable output = ((ExpressionEvaluation) input).getOutputVariable();
            
            final ImmutableSet<PVariable> outputs = (output == null) 
                    ? ImmutableSet.<PVariable>of() : ImmutableSet.of(output);
            
            Set<PVariable> inputVariables = Sets.difference(input.getAffectedVariables(), outputs);
            return Sets.difference(inputVariables, plan.getAllDeducedVariables()).isEmpty();
            
        } else if (input instanceof ExportedParameter) {
            return plan.getAllDeducedVariables().contains(((ExportedParameter) input).getParameterVariable());
        } else if (input instanceof TypeConstraint){
            if(!allowInverseNavigation && ((TypeConstraint) input).getSupplierKey().getArity() == 2){
                Tuple variables = ((TypeConstraint) input).getVariablesTuple();
                if(!plan.getAllDeducedVariables().contains(variables.get(0))){
                    return false;
                }
            }
        }
        return true;
    }
}