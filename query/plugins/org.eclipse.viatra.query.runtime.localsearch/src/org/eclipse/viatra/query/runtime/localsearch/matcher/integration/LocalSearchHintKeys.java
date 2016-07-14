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
package org.eclipse.viatra.query.runtime.localsearch.matcher.integration;

import org.eclipse.viatra.query.runtime.localsearch.planner.cost.ICostFunction;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.IFlattenCallPredicate;

/**
 * @author Marton Bur
 *
 */
public class LocalSearchHintKeys {
    public static final String ALLOW_INVERSE_NAVIGATION = "org.eclipse.viatra.query.runtime.localsearch - allow inverse navigation";
    public static final String USE_BASE_INDEX = "org.eclipse.viatra.query.runtime.localsearch - use base index";

    // This key can be used to influence the core planner algorithm
    public static final String PLANNER_TABLE_ROW_COUNT = "org.eclipse.viatra.query.runtime.localsearch - row count";
    
    /**
     * Cost function to be used by the planner. Must implement {@link ICostFunction}
     * @since 1.4
     */
    public static final String PLANNER_COST_FUNCTION = "org.eclipse.viatra.query.runtime.localsearch - cost function";
    
    /**
     * Predicate to decide whether to flatten specific positive pattern calls {@link IFlattenCallPredicate}
     * @since 1.4
     */
    public static final String FLATTEN_CALL_PREDICATE = "org.eclipse.viatra.query.runtime.localsearch - flatten call predicate";
}
