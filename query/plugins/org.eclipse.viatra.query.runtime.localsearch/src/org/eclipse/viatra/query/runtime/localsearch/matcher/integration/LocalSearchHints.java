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
package org.eclipse.viatra.query.runtime.localsearch.matcher.integration;

import java.util.Map;

import org.eclipse.viatra.query.runtime.localsearch.planner.cost.ICostFunction;
import org.eclipse.viatra.query.runtime.localsearch.planner.cost.impl.IndexerBasedConstraintCostFunction;
import org.eclipse.viatra.query.runtime.localsearch.planner.cost.impl.VariableBindingBasedCostFunction;
import org.eclipse.viatra.query.runtime.matchers.backend.IMatcherCapability;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.DefaultFlattenCallPredicate;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.IFlattenCallPredicate;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.NeverFlattenCallPredicate;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * Type safe builder and extractor for Local search specific hints
 * 
 * @author Grill Balázs
 * @since 1.4
 *
 */
public final class LocalSearchHints implements IMatcherCapability{

    private Boolean useBase = null;
    
    private Boolean allowInverse = null;
    
    private Integer rowCount = null;
    
    private ICostFunction costFunction = null;
    
    private IFlattenCallPredicate flattenCallPredicate = null;
    
    private LocalSearchHints() {}

    /**
     * Return the default settings overridden by the given hints
     */
    public static LocalSearchHints getDefaultOverriddenBy(QueryEvaluationHint overridingHint){
        return parse(getDefault().build().overrideBy(overridingHint));
    }
    
    /**
     * Default settings which are considered the most safe, providing a reasonable performance for most of the cases. Assumes the availability of the base indexer.
     */
    public static LocalSearchHints getDefault(){
        LocalSearchHints result = new LocalSearchHints();
        result.useBase = true;
        result.allowInverse = true;
        result.rowCount = 4;
        result.costFunction = new IndexerBasedConstraintCostFunction();
        result.flattenCallPredicate = new NeverFlattenCallPredicate();
        return result;
    }
    
    /**
     * With this setting, the patterns are flattened before planning. This may cause performance gain in most of the cases compared to the {@link #DEFAULT} settings,
     * However this should be used with care for patterns containing calls with several bodies.
     */
    public static LocalSearchHints getDefaultFlatten(){
        LocalSearchHints result = new LocalSearchHints();
        result.useBase = true;
        result.allowInverse = true;
        result.rowCount = 4;
        result.costFunction = new IndexerBasedConstraintCostFunction();
        result.flattenCallPredicate = new DefaultFlattenCallPredicate();
        return result;
    }
    
    /**
     * Settings to be used when the base index is not available.
     */
    public static LocalSearchHints getDefaultNoBase(){
        LocalSearchHints result = new LocalSearchHints();
        result.useBase = false;
        result.allowInverse = false;
        result.rowCount = 4;
        result.costFunction = new VariableBindingBasedCostFunction();
        result.flattenCallPredicate = new NeverFlattenCallPredicate();
        return result;
    }
    
    public static LocalSearchHints parse(QueryEvaluationHint hint){
        LocalSearchHints result = new LocalSearchHints();
        Map<String, Object> hints = hint.getBackendHints();
        
        result.useBase = (Boolean) hints.get(LocalSearchHintKeys.USE_BASE_INDEX);
        result.allowInverse = (Boolean) hints.get(LocalSearchHintKeys.ALLOW_INVERSE_NAVIGATION);
        result.rowCount = (Integer) hints.get(LocalSearchHintKeys.PLANNER_TABLE_ROW_COUNT);
        Object object = hints.get(LocalSearchHintKeys.FLATTEN_CALL_PREDICATE);
        if (object != null){
            Preconditions.checkArgument(object instanceof IFlattenCallPredicate);
            result.flattenCallPredicate = (IFlattenCallPredicate)object;
        }
        
        object = hints.get(LocalSearchHintKeys.PLANNER_COST_FUNCTION);
        if (object != null){
            Preconditions.checkArgument(object instanceof ICostFunction);
            result.costFunction = (ICostFunction)object;
        }
        
        return result;
    }
    
    public QueryEvaluationHint build(){
        Map<String, Object> map = Maps.newHashMap();
        if (useBase != null){
            map.put(LocalSearchHintKeys.USE_BASE_INDEX, useBase);
        }
        if (allowInverse != null){
            map.put(LocalSearchHintKeys.ALLOW_INVERSE_NAVIGATION, allowInverse);
        }
        if (rowCount != null){
            map.put(LocalSearchHintKeys.PLANNER_TABLE_ROW_COUNT, rowCount);
        }
        if (costFunction != null){
            map.put(LocalSearchHintKeys.PLANNER_COST_FUNCTION, costFunction);
        }
        if (flattenCallPredicate != null){
            map.put(LocalSearchHintKeys.FLATTEN_CALL_PREDICATE, flattenCallPredicate);
        }
        return new QueryEvaluationHint(LocalSearchBackendFactory.INSTANCE, map);
    }
    
    public boolean isUseBase() {
        return useBase;
    }
    
    public Boolean isAllowInverse() {
        return allowInverse;
    }
    
    public ICostFunction getCostFunction() {
        return costFunction;
    }
    
    public IFlattenCallPredicate getFlattenCallPredicate() {
        return flattenCallPredicate;
    }

    public Integer getRowCount() {
        return rowCount;
    }
    
    public LocalSearchHints setAllowInverse(boolean allowInverse) {
        this.allowInverse = allowInverse;
        return this;
    }
    
    public LocalSearchHints setUseBase(boolean useBase) {
        this.useBase = useBase;
        return this;
    }
    
    public LocalSearchHints setRowCount(int rowCount) {
        this.rowCount = rowCount;
        return this;
    }
    
    public LocalSearchHints setCostFunction(ICostFunction costFunction) {
        this.costFunction = costFunction;
        return this;
    }
    
    public LocalSearchHints setFlattenCallPredicate(IFlattenCallPredicate flattenCallPredicate) {
        this.flattenCallPredicate = flattenCallPredicate;
        return this;
    }
    
    public static LocalSearchHints customizeUseBase(boolean useBase){
        return new LocalSearchHints().setUseBase(useBase);
    }
    
    public static LocalSearchHints customizeAllowInverse(boolean allowInverse){
        return new LocalSearchHints().setAllowInverse(allowInverse);
    }
    
    public static LocalSearchHints customizeRowCount(int rowCount){
        return new LocalSearchHints().setRowCount(rowCount);
    }
    
    public static LocalSearchHints customizeCostFunction(ICostFunction costFunction){
        return new LocalSearchHints().setCostFunction(costFunction);
    }
    
    public static LocalSearchHints customizeFlattenCallPredicate(IFlattenCallPredicate predicate){
        return new LocalSearchHints().setFlattenCallPredicate(predicate);
    }

    @Override
    public boolean canBeSubstitute(IMatcherCapability capability) {
        if (capability instanceof LocalSearchHints){
            LocalSearchHints other = (LocalSearchHints)capability;
            /*
             * We allow substitution of matchers if their settings are equal.
             */
            return Objects.equal(other.useBase, useBase) && 
                    Objects.equal(other.allowInverse, allowInverse) &&
                    Objects.equal(other.costFunction, costFunction) &&
                    Objects.equal(other.flattenCallPredicate, flattenCallPredicate) &&
                    Objects.equal(other.rowCount, rowCount);
        }
        /*
         * For any other cases (e.g. for Rete), we cannot assume
         * that matchers created by LS are functionally equivalent.
         */
        return false;
    }
}
