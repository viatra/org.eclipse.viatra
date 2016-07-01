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
package org.eclipse.viatra.query.runtime.localsearch.plan;

import java.util.Map;

import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchHintKeys;
import org.eclipse.viatra.query.runtime.localsearch.planner.cost.ICostFunction;
import org.eclipse.viatra.query.runtime.localsearch.planner.cost.impl.IndexerBasedConstraintCostFunction;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.DefaultFlattenCallPredicate;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.IFlattenCallPredicate;

import com.google.common.base.Preconditions;

/**
 * @author Grill Balázs
 * @since 1.4
 * @noreference This class is not intended to be referenced by clients.
 */
public class PlannerConfiguration {
    
    private static ICostFunction createCostFunction(Map<String, Object> hints){
        Object object = hints.get(LocalSearchHintKeys.PLANNER_COST_FUNCTION);
        if (object != null){
            Preconditions.checkArgument(object instanceof ICostFunction);
            ICostFunction costFunction = (ICostFunction)object;
            return costFunction;
        }
        return new IndexerBasedConstraintCostFunction();
    }
    
    private static IFlattenCallPredicate createFlattenCallPredicate(Map<String, Object> hints){
        Object object = hints.get(LocalSearchHintKeys.FLATTEN_CALL_PREDICATE);
        if (object != null){
            Preconditions.checkArgument(object instanceof IFlattenCallPredicate);
            IFlattenCallPredicate predicate = (IFlattenCallPredicate)object;
            return predicate;
        }
        return new DefaultFlattenCallPredicate();
    }

    private final boolean allowInverse;
    private final boolean useBase;
    private final ICostFunction costFunction;
    private final IFlattenCallPredicate flattenCallPredicate;
    private final int rowCount;
    
    public PlannerConfiguration(Map<String, Object> hints) {
        Boolean allowInverse = (Boolean) hints.get(LocalSearchHintKeys.ALLOW_INVERSE_NAVIGATION);
        this.allowInverse = allowInverse == null ? true : allowInverse; 
        Boolean useBase = (Boolean) hints.get(LocalSearchHintKeys.USE_BASE_INDEX);
        
        int rowCount = 4;
        Integer rowCountHint= (Integer) hints.get(LocalSearchHintKeys.PLANNER_TABLE_ROW_COUNT);
        if(rowCountHint != null){
            rowCount = rowCountHint;
        }
        this.rowCount = rowCount;
        
        this.useBase = useBase == null ? true : useBase; 
        this.costFunction = createCostFunction(hints);
        this.flattenCallPredicate = createFlattenCallPredicate(hints);
        
    }

    public boolean isAllowInverse() {
        return allowInverse;
    }
    
    public boolean isUseBase() {
        return useBase;
    }
    
    public int getRowCount() {
        return rowCount;
    }
    
    public ICostFunction getCostFunction() {
        return costFunction;
    }
    
    public IFlattenCallPredicate getFlattenCallPredicate() {
        return flattenCallPredicate;
    }
    
}
