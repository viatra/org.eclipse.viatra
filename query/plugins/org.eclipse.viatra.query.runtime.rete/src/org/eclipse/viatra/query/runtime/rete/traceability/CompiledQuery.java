/*******************************************************************************
 * Copyright (c) 2010-2014, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.traceability;

import java.util.Map;

import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.rete.recipes.ReteNodeRecipe;

/**
 * Indicates that recipe expresses the finished match set of a query.
 * @author Bergmann Gabor
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class CompiledQuery extends RecipeTraceInfo implements
        PatternTraceInfo {
    
    private PQuery query;
    private final Map<PBody, ? extends RecipeTraceInfo> parentRecipeTracesPerBody;
    
    /**
     * @since 1.6
     */
    public CompiledQuery(ReteNodeRecipe recipe,
            Map<PBody, ? extends RecipeTraceInfo> parentRecipeTraces,
            PQuery query) {
        super(recipe, parentRecipeTraces.values());
        parentRecipeTracesPerBody = parentRecipeTraces;
        this.query = query;
    }
    public PQuery getQuery() {
        return query;
    }

    @Override
    public String getPatternName() {
        return query.getFullyQualifiedName();
    }

    /**
     * @since 1.6
     */
    public Map<PBody, ? extends RecipeTraceInfo> getParentRecipeTracesPerBody() {
        return parentRecipeTracesPerBody;
    }

}
