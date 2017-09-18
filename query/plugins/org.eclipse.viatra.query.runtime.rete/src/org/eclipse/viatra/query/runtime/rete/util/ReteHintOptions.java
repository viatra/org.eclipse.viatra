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

    public static final QueryHintOption<Boolean> useDiscriminatorDispatchersForConstantFiltering = 
            hintOption("useDiscriminatorDispatchersForConstantFiltering", true);
    
    public static final QueryHintOption<Boolean> prioritizeConstantFiltering = 
            hintOption("prioritizeConstantFiltering", true);

    public static final QueryHintOption<Boolean> cacheOutputOfEvaluatorsByDefault = 
            hintOption("cacheOutputOfEvaluatorsByDefault", true);
    
    /**
     * @since 1.6
     */
    public static final QueryHintOption<Boolean> deleteRederiveEvaluation = 
            hintOption("deleteRederiveEvaluation", false);
    
    /**
     * This hint disallows the Rete network to remove type checks that can be inferred from other constraints but might leave the model scope. In general,
     * this might result in larger Rete networks, but can avoid hard to debug issues. </p>
     * 
     * Was related to the dangling edges issue http://bugs.eclipse.org/512752, but is now entirely ignored.
     * 
     * @deprecated Use the base index option <code>DANGLING_FREE_ASSUMPTION_DEFAULT</code> instead.
     * @since 1.6
     */
    @Deprecated
    public static final QueryHintOption<Boolean> checkScopeBoundaries =
            hintOption("checkScopeBoundaries", true);
    
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
