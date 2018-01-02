/** 
 * Copyright (c) 2010-2015, Marton Bur, Zoltan Ujhelyi, Akos Horvath, Istvan Rath and Danil Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Marton Bur - initial API and implementation
 */
package org.eclipse.viatra.query.runtime.localsearch.planner;

import java.util.Set;
import java.util.function.Function;

import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.localsearch.planner.cost.IConstraintEvaluationContext;
import org.eclipse.viatra.query.runtime.matchers.psystem.analysis.QueryAnalyzer;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryBackendContext;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryResultProviderAccess;

/** 
 * Wraps a PConstraint together with information required for the planner. Currently contains information about the expected binding state of
 * the affected variables also called application condition, and the cost of the enforcement, based on the meta and/or the runtime context.
 *  
 * @author Marton Bur
 * @noreference This class is not intended to be referenced by clients.
 */
public class PConstraintInfo implements IConstraintEvaluationContext {

    private PConstraint constraint;
    private Set<PVariable> boundMaskVariables;
    private Set<PVariable> freeMaskVariables;
    private Set<PConstraintInfo> sameWithDifferentBindings;
    private IQueryRuntimeContext runtimeContext;
    private QueryAnalyzer queryAnalyzer;
    private IQueryResultProviderAccess resultProviderAccess;

    private double cost;
    

    /** 
     * Instantiates the wrapper
     * @param constraintfor which the information is added and stored
     * @param boundMaskVariables the bound variables in the operation mask
     * @param freeMaskVariables the free variables in the operation mask
     * @param sameWithDifferentBindings during the planning process, multiple operation adornments are considered for a constraint, so that it
     * is represented by multiple plan infos. This parameter contains all plan infos that are for the same
     * constraint, but with different adornment
     * @param context the query backend context
     */
    public PConstraintInfo(PConstraint constraint, Set<PVariable> boundMaskVariables, Set<PVariable> freeMaskVariables,
        Set<PConstraintInfo> sameWithDifferentBindings, 
        IQueryBackendContext context,
        Function<IConstraintEvaluationContext, Double> costFunction) {
        this.constraint = constraint;
        this.boundMaskVariables = boundMaskVariables;
        this.freeMaskVariables = freeMaskVariables;
        this.sameWithDifferentBindings = sameWithDifferentBindings;
        this.runtimeContext = context.getRuntimeContext();
        this.queryAnalyzer = context.getQueryAnalyzer();
        this.resultProviderAccess = context.getResultProviderAccess();

        // Calculate cost of the constraint based on its type
        this.cost = costFunction.apply(this);
    }
    
    @Override
    public IQueryRuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

    @Override
    public QueryAnalyzer getQueryAnalyzer() {
        return queryAnalyzer;
    }

    @Override
    public PConstraint getConstraint() {
        return constraint;
    }

    @Override
    public Set<PVariable> getFreeVariables() {
        return freeMaskVariables;
    }

    @Override
    public Set<PVariable> getBoundVariables() {
        return boundMaskVariables;
    }

    public Set<PConstraintInfo> getSameWithDifferentBindings() {
        return sameWithDifferentBindings;
    }

    public double getCost() {
        return cost;
    }

    public PConstraintCategory getCategory(PBody pBody, Set<PVariable> boundVariables) {
        if (boundVariables.stream().anyMatch(this.freeMaskVariables::contains)) {
            return PConstraintCategory.PAST;
        } else if (pBody.getAllVariables().stream().filter(var -> !boundVariables.contains(var))
                .anyMatch(this.boundMaskVariables::contains)) {
            return PConstraintCategory.FUTURE;
        } else {
            return PConstraintCategory.PRESENT;
        }
    }

    @Override
    public String toString() {
        return String.format("%s, bound variables: %s, cost: \"%.2f\"", constraint.toString(), boundMaskVariables.toString(), cost);
    }

    @Override
    public IQueryResultProviderAccess resultProviderAccess() {
        return resultProviderAccess;
    }

}
