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

import static org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchHintOptions.FLATTEN_CALL_PREDICATE;
import static org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchHintOptions.PLANNER_COST_FUNCTION;
import static org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchHintOptions.PLANNER_TABLE_ROW_COUNT;
import static org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchHintOptions.USE_BASE_INDEX;
import static org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchHintOptions.ADORNMENT_PROVIDER;
import static org.eclipse.viatra.query.runtime.matchers.backend.CommonQueryHintOptions.normalizationTraceCollector;

import java.util.Map;

import org.eclipse.viatra.query.runtime.localsearch.planner.cost.ICostFunction;
import org.eclipse.viatra.query.runtime.localsearch.planner.cost.impl.IndexerBasedConstraintCostFunction;
import org.eclipse.viatra.query.runtime.localsearch.planner.cost.impl.VariableBindingBasedCostFunction;
import org.eclipse.viatra.query.runtime.matchers.backend.IMatcherCapability;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendFactory;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryHintOption;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.DefaultFlattenCallPredicate;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.IFlattenCallPredicate;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.IRewriterTraceCollector;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.NeverFlattenCallPredicate;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.NopTraceCollector;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

/**
 * Type safe builder and extractor for Local search specific hints
 * 
 * @author Grill Balázs
 * @since 1.4
 *
 */
public final class LocalSearchHints implements IMatcherCapability {
    
    private Boolean useBase = null;
    
    private Integer rowCount = null;
    
    private ICostFunction costFunction = null;
    
    private IFlattenCallPredicate flattenCallPredicate = null;
    
    private IAdornmentProvider adornmentProvider = null;
    
    private IRewriterTraceCollector traceCollector = NopTraceCollector.INSTANCE;
    
    private IQueryBackendFactory backendFactory = null;
    
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
        result.useBase = USE_BASE_INDEX.getDefaultValue();
        result.rowCount = PLANNER_TABLE_ROW_COUNT.getDefaultValue();
        result.costFunction = PLANNER_COST_FUNCTION.getDefaultValue();
        result.flattenCallPredicate = FLATTEN_CALL_PREDICATE.getDefaultValue();
        result.adornmentProvider = ADORNMENT_PROVIDER.getDefaultValue();
        result.backendFactory = LocalSearchBackendFactory.INSTANCE;
        return result;
    }
    
    /**
     * With this setting, the patterns are flattened before planning. This may cause performance gain in most of the cases compared to the {@link #getDefault()} settings,
     * However this should be used with care for patterns containing calls with several bodies.
     */
    public static LocalSearchHints getDefaultFlatten(){
        LocalSearchHints result = new LocalSearchHints();
        result.useBase = true;
        result.rowCount = 4;
        result.costFunction = new IndexerBasedConstraintCostFunction();
        result.flattenCallPredicate = new DefaultFlattenCallPredicate();
        result.adornmentProvider = ADORNMENT_PROVIDER.getDefaultValue();
        result.backendFactory = LocalSearchBackendFactory.INSTANCE;
        return result;
    }
    
    /**
     * Settings to be used when the base index is not available.
     */
    public static LocalSearchHints getDefaultNoBase(){
        LocalSearchHints result = new LocalSearchHints();
        result.useBase = false;
        result.rowCount = 4;
        result.costFunction = new VariableBindingBasedCostFunction();
        result.flattenCallPredicate = new NeverFlattenCallPredicate();
        result.adornmentProvider = ADORNMENT_PROVIDER.getDefaultValue();
        result.backendFactory = LocalSearchBackendFactory.INSTANCE;
        return result;
    }
    
    /**
     * Initializes the generic (not EMF specific) search backend with the default settings
     * @since 1.7
     */
    public static LocalSearchHints getDefaultGeneric(){
        LocalSearchHints result = new LocalSearchHints();
        result.useBase = true; // Should be unused; but a false value might cause surprises as an engine-default hint
        result.rowCount = 4;
        result.costFunction = new VariableBindingBasedCostFunction();
        result.flattenCallPredicate = new NeverFlattenCallPredicate();
        result.adornmentProvider = ADORNMENT_PROVIDER.getDefaultValue();
        result.backendFactory = LocalSearchGenericBackendFactory.INSTANCE;
        return result;
    }
    
    public static LocalSearchHints parse(QueryEvaluationHint hint){
        LocalSearchHints result = new LocalSearchHints();
        
        result.useBase = USE_BASE_INDEX.getValueOrNull(hint);
        result.rowCount = PLANNER_TABLE_ROW_COUNT.getValueOrNull(hint);
        result.flattenCallPredicate = FLATTEN_CALL_PREDICATE.getValueOrNull(hint);
        result.costFunction = PLANNER_COST_FUNCTION.getValueOrNull(hint);
        result.adornmentProvider = ADORNMENT_PROVIDER.getValueOrNull(hint);
        result.traceCollector = normalizationTraceCollector.getValueOrDefault(hint);
        
        return result;
    }
    
    public QueryEvaluationHint build(){
        @SuppressWarnings("rawtypes")
        Map<QueryHintOption, Object> map = Maps.newHashMap();
        if (useBase != null){
            USE_BASE_INDEX.insertOverridingValue(map, useBase); 
        }
        if (rowCount != null){
            PLANNER_TABLE_ROW_COUNT.insertOverridingValue(map, rowCount);
        }
        if (costFunction != null){
            PLANNER_COST_FUNCTION.insertOverridingValue(map, costFunction);
        }
        if (flattenCallPredicate != null){
            FLATTEN_CALL_PREDICATE.insertOverridingValue(map, flattenCallPredicate);
        }
        if (adornmentProvider != null){
            ADORNMENT_PROVIDER.insertOverridingValue(map, adornmentProvider);
        }
        if (traceCollector != null){
            normalizationTraceCollector.insertOverridingValue(map, traceCollector);
        }
        return new QueryEvaluationHint(map, backendFactory);
    }
    
    public boolean isUseBase() {
        return useBase;
    }
    
    /**
     * @deprecated allow inverse was deprecated in 1.4; its uses are ignored 
     */
    @Deprecated
    public Boolean isAllowInverse() {
        return true;
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
    
    /**
     * @since 1.5
     */
    public IAdornmentProvider getAdornmentProvider() {
        return adornmentProvider;
    }

    /**
     * @since 1.6
     */
    public IRewriterTraceCollector getTraceCollector() {
        return traceCollector == null ? normalizationTraceCollector.getDefaultValue() : traceCollector;
    }
    
    /**
     * @deprecated allow inverse was deprecated in 1.4; its uses are ignored 
     */
    @Deprecated
    public LocalSearchHints setAllowInverse(boolean allowInverse) {
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
    
    /**
     * @since 1.6
     */
    public LocalSearchHints setTraceCollector(IRewriterTraceCollector traceCollector) {
        this.traceCollector = traceCollector;
        return this;
    }
    
    /**
     * @since 1.5
     */
    public LocalSearchHints setAdornmentProvider(IAdornmentProvider adornmentProvider) {
        this.adornmentProvider = adornmentProvider;
        return this;
    }
    
    public static LocalSearchHints customizeUseBase(boolean useBase){
        return new LocalSearchHints().setUseBase(useBase);
    }
    
    /**
     * @deprecated This allowInverse hint is ignored by the runtime, do not use it
     */
    @Deprecated
    public static LocalSearchHints customizeAllowInverse(boolean allowInverse){
        return new LocalSearchHints();
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
    
    /**
     * @since 1.5
     */
    public static LocalSearchHints customizeAdornmentProvider(IAdornmentProvider adornmentProvider){
        return new LocalSearchHints().setAdornmentProvider(adornmentProvider);
    }
    
    /**
     * @since 1.6
     */
    public static LocalSearchHints customizeTraceCollector(IRewriterTraceCollector traceCollector){
        return new LocalSearchHints().setTraceCollector(traceCollector);
    }

    @Override
    public boolean canBeSubstitute(IMatcherCapability capability) {
        if (capability instanceof LocalSearchHints){
            LocalSearchHints other = (LocalSearchHints)capability;
            /*
             * We allow substitution of matchers if their functionally relevant settings are equal.
             */
            return Objects.equal(other.useBase, useBase);
        }
        /*
         * For any other cases (e.g. for Rete), we cannot assume
         * that matchers created by LS are functionally equivalent.
         */
        return false;
    }
}
