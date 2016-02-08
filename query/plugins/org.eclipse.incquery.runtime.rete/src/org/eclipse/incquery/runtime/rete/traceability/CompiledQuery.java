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
package org.eclipse.incquery.runtime.rete.traceability;

import java.util.Collection;

import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.incquery.runtime.rete.recipes.ReteNodeRecipe;

/**
 * Indicates that recipe expresses the finished match set of a query.
 * @author Bergmann Gabor
 *
 */
public class CompiledQuery extends RecipeTraceInfo implements
		PatternTraceInfo {
	
	private PQuery query;
	
	public CompiledQuery(ReteNodeRecipe recipe,
			Collection<? extends RecipeTraceInfo> parentRecipeTraces,
			PQuery query) {
		super(recipe, parentRecipeTraces);
		this.query = query;
	}
	public PQuery getQuery() {
		return query;
	}

	@Override
	public String getPatternName() {
		return query.getFullyQualifiedName();
	}

}
