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

import java.util.Comparator;

import org.eclipse.incquery.runtime.localsearch.planner.cost.ICostEstimator;
import org.eclipse.incquery.runtime.localsearch.planner.cost.impl.VariableBindingBasedCostEstimator;
import org.eclipse.incquery.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.incquery.runtime.matchers.planning.SubPlan;
import org.eclipse.incquery.runtime.matchers.psystem.PConstraint;

/**
 * Class to contain the ordering logic for local search planning. Not to be confused with
 * {@link org.eclipse.incquery.runtime.rete.construction.basiclinear.OrderingHeuristics}
 * 
 * @author Marton Bur
 *
 */
public class OrderingHeuristics implements Comparator<PConstraint> {

    private SubPlan plan;

    public OrderingHeuristics(SubPlan plan, IQueryMetaContext context) {
        this.plan = plan;
    }

    @Override
    public int compare(PConstraint constraint1, PConstraint constraint2) {
        // Create a cost estimator (optionally pass runtime context)
        ICostEstimator costEstimator = new VariableBindingBasedCostEstimator();

        double cost1 = costEstimator.getCost(plan, constraint1);
        double cost2 = costEstimator.getCost(plan, constraint2);

        return Double.compare(cost1, cost2);
    }

}
