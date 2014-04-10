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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.incquery.runtime.matchers.planning.SubPlan;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.rete.recipes.ReteNodeRecipe;

/**
 * A trace marker associating a Rete recipe with a query SubPlan. 
 * 
 * <p> The Rete node represented by the recipe is equivalent to the SubPlan.
 */
public class QueryPlanRecipeTraceInfo extends AuxiliaryPlanningRecipeTraceInfo {

	public QueryPlanRecipeTraceInfo(SubPlan subPlan, List<PVariable> variablesTuple,
			ReteNodeRecipe recipe,
			Collection<? extends RecipeTraceInfo> parentRecipeTraces) {
		super(subPlan, variablesTuple, recipe, parentRecipeTraces);
	}
	public QueryPlanRecipeTraceInfo(SubPlan subPlan, List<PVariable> variablesTuple,
			ReteNodeRecipe recipe,
			RecipeTraceInfo... parentRecipeTraces) {
		this(subPlan, variablesTuple, recipe, Arrays.asList(parentRecipeTraces));
	}

}
