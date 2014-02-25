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

import org.eclipse.incquery.runtime.matchers.planning.SubPlan;
import org.eclipse.incquery.runtime.rete.recipes.ReteNodeRecipe;

/**
 * A trace marker associating a Rete recipe with a query SubPlan.
 *
 * TODO: if SubPlan will be orderless (PVariable set instead of tuple), then the variable tuple must be constructed here.
 */
public class QueryPlanRecipeTraceInfo extends RecipeTraceInfo {

	SubPlan subPlan;
	
	public QueryPlanRecipeTraceInfo(SubPlan subPlan, ReteNodeRecipe recipe,
			Collection<? extends RecipeTraceInfo> parentRecipeTraces) {
		super(recipe, parentRecipeTraces);
		this.subPlan = subPlan;
	}
	public QueryPlanRecipeTraceInfo(SubPlan subPlan, ReteNodeRecipe recipe,
			RecipeTraceInfo... parentRecipeTraces) {
		super(recipe, parentRecipeTraces);
		this.subPlan = subPlan;
	}

	public SubPlan getSubPlan() {
		return subPlan;
	}
	

}
