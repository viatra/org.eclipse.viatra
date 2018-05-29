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
package org.eclipse.viatra.query.runtime.rete.util;

import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryHintOption;

/**
 * Provides key objects (of type {@link QueryHintOption}) for {@link QueryEvaluationHint}s.
 * @author Gabor Bergmann
 * @since 1.5
 */
public final class ReteHintOptions {

    private ReteHintOptions() {/*Utility class constructor*/}
    
    public static final QueryHintOption<Boolean> useDiscriminatorDispatchersForConstantFiltering = 
            hintOption("useDiscriminatorDispatchersForConstantFiltering", true);
    
    public static final QueryHintOption<Boolean> prioritizeConstantFiltering = 
            hintOption("prioritizeConstantFiltering", true);

    public static final QueryHintOption<Boolean> cacheOutputOfEvaluatorsByDefault = 
            hintOption("cacheOutputOfEvaluatorsByDefault", true);
    
    /**
     * The incremental query evaluator backend can evaluate recursive patterns. 
     * However, by default, instance models that contain cycles are not supported with recursive queries 
     * and can lead to incorrect query results. 
     * Enabling Delete And Rederive (DRED) mode guarantees that recursive query evaluation leads to correct results in these cases as well.
     *  
     * <p> As DRED may diminish the performance of incremental maintenance, it is not enabled by default.
     * @since 1.6
     */
    public static final QueryHintOption<Boolean> deleteRederiveEvaluation = 
            hintOption("deleteRederiveEvaluation", false);
    
    /**
     * This hint allows the query planner to take advantage of "weakened alternative" suggestions of the meta context.
     * For instance, enumerable unary type constraints may be substituted with a simple type filtering where sufficient.
     * 
     * @since 1.6
     */
    public static final QueryHintOption<Boolean> expandWeakenedAlternativeConstraints =
            hintOption("expandWeakenedAlternativeConstraints", true);
   
    // internal helper for conciseness
    private static <T> QueryHintOption<T> hintOption(String hintKeyLocalName, T defaultValue) {
        return new QueryHintOption<>(ReteHintOptions.class, hintKeyLocalName, defaultValue);
    }
}
