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

import org.eclipse.incquery.runtime.matchers.planning.SubPlan;
import org.eclipse.incquery.runtime.rete.recipes.ReteNodeRecipe;

/**
 * A trace marker associating a Rete recipe with a query SubPlan. 
 * 
 * <p> The recipe may be an auxiliary node; 
 *   see {@link QueryPlanRecipeTraceInfo} if it represents the entire SubPlan instead.
 */
public class AuxiliaryPlanningRecipeTraceInfo extends RecipeTraceInfo {

	protected SubPlan subPlan;

	public AuxiliaryPlanningRecipeTraceInfo(SubPlan subPlan, 
			ReteNodeRecipe recipe,
			Collection<? extends RecipeTraceInfo> parentRecipeTraces) {
		super(recipe, parentRecipeTraces);
		this.subPlan = subPlan;
	}

	public AuxiliaryPlanningRecipeTraceInfo(SubPlan subPlan, 
			ReteNodeRecipe recipe,
			RecipeTraceInfo... parentRecipeTraces) {
		this(subPlan, recipe, Arrays.asList(parentRecipeTraces));
	}

	public SubPlan getSubPlan() {
		return subPlan;
	}

	public String getPatternName() {
		return subPlan.getBody().getPattern().getFullyQualifiedName();
	}

}