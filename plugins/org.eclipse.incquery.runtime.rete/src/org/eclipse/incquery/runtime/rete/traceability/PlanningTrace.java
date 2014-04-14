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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.incquery.runtime.matchers.planning.SubPlan;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.rete.recipes.ReteNodeRecipe;

/**
 * A trace marker associating a Rete recipe with a query SubPlan. 
 * 
 * <p> The recipe may be an auxiliary node; 
 *   see {@link CompiledSubPlan} if it represents the entire SubPlan instead.
 */
public class PlanningTrace extends RecipeTraceInfo {

	protected SubPlan subPlan;
	protected List<PVariable> variablesTuple;
	protected Map<PVariable, Integer> posMapping;

	public PlanningTrace(SubPlan subPlan, List<PVariable> variablesTuple, 
			ReteNodeRecipe recipe,
			Collection<? extends RecipeTraceInfo> parentRecipeTraces) {
		super(recipe, parentRecipeTraces);
		this.subPlan = subPlan;
		this.variablesTuple = variablesTuple;
		
		this.posMapping = new HashMap<PVariable, Integer>();
		for (int i = 0; i < variablesTuple.size(); ++i)
			posMapping.put(variablesTuple.get(i), i);
	}

	public PlanningTrace(SubPlan subPlan, List<PVariable> variablesTuple, 
			ReteNodeRecipe recipe,
			RecipeTraceInfo... parentRecipeTraces) {
		this(subPlan, variablesTuple, recipe, Arrays.asList(parentRecipeTraces));
	}

	public SubPlan getSubPlan() {
		return subPlan;
	}

	public String getPatternName() {
		return subPlan.getBody().getPattern().getFullyQualifiedName();
	}

	public List<PVariable> getVariablesTuple() {
		return variablesTuple;
	}

	public Map<PVariable, Integer> getPosMapping() {
		return posMapping;
	}

	/**
	 * Returns a new clone that reinterprets the same compiled form as belonging to a different subPlan.
	 * Useful e.g. if child plan turns out to be a no-op. 
	 */
	public CompiledSubPlan cloneFor(SubPlan newSubPlan) {
	    return new CompiledSubPlan(newSubPlan, 
	    		getVariablesTuple(), 
	    		getRecipe(), 
	    		getParentRecipeTraces());
	}

}