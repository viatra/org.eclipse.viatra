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
package org.eclipse.viatra.query.runtime.rete.construction.plancompiler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.rete.recipes.ProductionRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.ReteNodeRecipe;
import org.eclipse.viatra.query.runtime.rete.traceability.CompiledQuery;
import org.eclipse.viatra.query.runtime.rete.traceability.RecipeTraceInfo;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

/**
 * In a recursive query structure, query composition references can be cut off so that the remaining structure is DAG.
 * {@link RecursionCutoffPoint} represents one such cut off query composition. 
 * When the compilation of the recursive query finishes and the compiled form becomes available, 
 *   the {@link RecursionCutoffPoint} has to be signaled to update parent traces and recipes of the recursive call.
 *  
 * @author Bergmann Gabor
 *
 */
class RecursionCutoffPoint {
    final RecipeTraceInfo.ParentTraceList futureTraceList;
    final Map<PBody, RecipeTraceInfo> futureTraceMap;
    final CompiledQuery compiledQuery;
    final ProductionRecipe recipe;
    final QueryEvaluationHint hint;

    public RecursionCutoffPoint(PQuery query, QueryEvaluationHint hint, IQueryMetaContext context) {
        super();
        this.hint = hint;
        this.futureTraceList = new RecipeTraceInfo.ParentTraceList();
        this.futureTraceMap = new HashMap<>();
        this.compiledQuery = CompilerHelper.makeQueryTrace(query, futureTraceMap, Collections.<ReteNodeRecipe>emptySet(), hint, context);
        this.recipe = (ProductionRecipe)compiledQuery.getRecipe();
        Preconditions.checkArgument(
                compiledQuery.getParentRecipeTraces().isEmpty(), 
                String.format("Recursion cut-off point of query %s has trace parents: %s", 
                        compiledQuery.getQuery(),
                        Joiner.on(", ").join(compiledQuery.getParentRecipeTraces())));
        Preconditions.checkArgument(
                recipe.getParents().isEmpty(), 
                String.format("Recursion cut-off point of query %s has recipe parents: %s", 
                        compiledQuery.getQuery(),
                        Joiner.on(", ").join(compiledQuery.getParentRecipeTraces())));
    }
    
    /**
     * Signals that compilation of the recursive query has terminated, culminating into the given compiled form.
     * The query composition that has been cut off will be connected now.
     */
    public void mend(CompiledQuery finalCompiledForm) {
        futureTraceList.addAll(finalCompiledForm.getParentRecipeTraces());
        futureTraceMap.putAll(finalCompiledForm.getParentRecipeTracesPerBody());
        recipe.getParents().addAll(((ProductionRecipe)finalCompiledForm.getRecipe()).getParents());
    }

    public CompiledQuery getCompiledQuery() {
        return compiledQuery;
    }

    public ProductionRecipe getRecipe() {
        return recipe;
    }
    
    

}
