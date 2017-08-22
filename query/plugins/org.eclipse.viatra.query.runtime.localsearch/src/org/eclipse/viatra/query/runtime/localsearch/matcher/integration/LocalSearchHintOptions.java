/*******************************************************************************
 * Copyright (c) 2010-2016, Gabor Bergmann, IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.matcher.integration;

import org.eclipse.viatra.query.runtime.localsearch.planner.cost.ICostFunction;
import org.eclipse.viatra.query.runtime.localsearch.planner.cost.impl.IndexerBasedConstraintCostFunction;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryHintOption;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.IFlattenCallPredicate;

/**
 * 
 * @author Gabor Bergmann
 * @since 1.5
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public final class LocalSearchHintOptions {
    
    private LocalSearchHintOptions() {
        // Private constructor for utility class
    }
    
    public static final QueryHintOption<Boolean> USE_BASE_INDEX = 
            hintOption("USE_BASE_INDEX", true);
    
    // This key can be used to influence the core planner algorithm
    public static final QueryHintOption<Integer> PLANNER_TABLE_ROW_COUNT = 
            hintOption("PLANNER_TABLE_ROW_COUNT", 4);
    /**
     * Cost function to be used by the planner. Must implement {@link ICostFunction}
     * @since 1.4
     */
    public static final QueryHintOption<ICostFunction> PLANNER_COST_FUNCTION = 
            hintOption("PLANNER_COST_FUNCTION", (ICostFunction) new IndexerBasedConstraintCostFunction());
    /**
     * Predicate to decide whether to flatten specific positive pattern calls {@link IFlattenCallPredicate}
     * @since 1.4
     */
    public static final QueryHintOption<IFlattenCallPredicate> FLATTEN_CALL_PREDICATE = 
            hintOption("FLATTEN_CALL_PREDICATE", (IFlattenCallPredicate) new DontFlattenIncrementalPredicate());
    
    /**
     * A provider of expected adornments {@link IAdornmentProvider}
     * @since 1.5
     */
    public static final QueryHintOption<IAdornmentProvider> ADORNMENT_PROVIDER = 
            hintOption("ADORNMENT_PROVIDER", (IAdornmentProvider)new AllValidAdornments());
    
    // internal helper for conciseness
    private static <T> QueryHintOption<T> hintOption(String hintKeyLocalName, T defaultValue) {
        return new QueryHintOption<>(LocalSearchHintOptions.class, hintKeyLocalName, defaultValue);
    }
}
