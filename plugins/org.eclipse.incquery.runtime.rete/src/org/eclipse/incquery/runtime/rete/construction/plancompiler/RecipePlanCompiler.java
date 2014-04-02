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
package org.eclipse.incquery.runtime.rete.construction.plancompiler;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.incquery.runtime.matchers.planning.SubPlan;
import org.eclipse.incquery.runtime.matchers.planning.operations.PApply;
import org.eclipse.incquery.runtime.matchers.planning.operations.PEnumerate;
import org.eclipse.incquery.runtime.matchers.planning.operations.PJoin;
import org.eclipse.incquery.runtime.matchers.planning.operations.POperation;
import org.eclipse.incquery.runtime.matchers.planning.operations.PProject;
import org.eclipse.incquery.runtime.matchers.planning.operations.PStart;
import org.eclipse.incquery.runtime.matchers.psystem.DeferredPConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.EnumerablePConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PQuery;
import org.eclipse.incquery.runtime.rete.recipes.ConstantRecipe;
import org.eclipse.incquery.runtime.rete.recipes.ProductionRecipe;
import org.eclipse.incquery.runtime.rete.recipes.RecipesFactory;
import org.eclipse.incquery.runtime.rete.recipes.ReteNodeRecipe;
import org.eclipse.incquery.runtime.rete.traceability.ProductionTraceInfo;
import org.eclipse.incquery.runtime.rete.traceability.QueryPlanRecipeTraceInfo;
import org.eclipse.incquery.runtime.rete.traceability.RecipeTraceInfo;

/**
 * Compiles query plans into Rete recipes 
 * 
 * @author Bergmann Gabor
 *
 */
public class RecipePlanCompiler {
	
	private Map<SubPlan, QueryPlanRecipeTraceInfo> compilerCache = new HashMap<SubPlan, QueryPlanRecipeTraceInfo>();
	private Map<ReteNodeRecipe, SubPlan> backTrace = new HashMap<ReteNodeRecipe, SubPlan>();
	
	final static RecipesFactory FACTORY = RecipesFactory.eINSTANCE;
	
	public RecipeTraceInfo compileProduction(PQuery query, Collection<SubPlan> bodies) {
		//TODO skip production node if there is just one body and no projection needed?
		Collection<RecipeTraceInfo> bodyFinalTraces = new HashSet<RecipeTraceInfo>();
		Collection<ReteNodeRecipe> bodyFinalRecipes = new HashSet<ReteNodeRecipe>();
		
		for (SubPlan bodyFinalPlan : bodies) {
			final QueryPlanRecipeTraceInfo compiledForm = getCompiledForm(bodyFinalPlan);
			TODO skip over & reuse uniqueness enforcer;
			bodyFinalTraces.add(compiledForm);
			bodyFinalRecipes.add(compiledForm.getRecipe());
		}
		
		final ProductionRecipe recipe = FACTORY.createProductionRecipe();
		recipe.setPattern(query);
		recipe.getParents().addAll(bodyFinalRecipes);
		for (int i = 0; i < query.getParameterNames().size(); ++i)
			recipe.getMappedIndices().put(query.getParameterNames().get(i), i);
		ProductionTraceInfo compiled = new ProductionTraceInfo(recipe, bodyFinalTraces, query);
		
		return compiled;
	}
	
	public QueryPlanRecipeTraceInfo getCompiledForm(SubPlan plan) {
		QueryPlanRecipeTraceInfo compiled = compilerCache.get(plan);
		if (compiled == null) {
			compiled = doCompileDispatch(plan);
			compilerCache.put(plan, compiled);
			backTrace.put(compiled.getRecipe(), plan);
		}
		return compiled;
	}
	
	
	private QueryPlanRecipeTraceInfo doCompileDispatch(SubPlan plan) {
		final POperation operation = plan.getOperation();
		if (operation instanceof PEnumerate) {
			return doCompileEnumerate(((PEnumerate) operation).getEnumerablePConstraint(), plan);
		} else if (operation instanceof PApply) {
			final PConstraint pConstraint = ((PApply) operation).getPConstraint();
			if (pConstraint instanceof EnumerablePConstraint) {
				QueryPlanRecipeTraceInfo primaryParent = getCompiledForm(plan.getParentPlans().get(0));
				QueryPlanRecipeTraceInfo secondaryParent = 
						doCompileEnumerate((EnumerablePConstraint) pConstraint, plan); TODO trace nem jó		
				return doCompileJoin( plan);
			} else if (pConstraint instanceof DeferredPConstraint) {
				 return doCheckDispatch((DeferredPConstraint)pConstraint, plan);
			} else  {
				throw new IllegalArgumentException(
						"Unsupported PConstraint in query plan: " + plan.toShortString()); 
			}
		} else if (operation instanceof PJoin) {
			return doCompileJoin(operation, plan);
		} else if (operation instanceof PProject) {
			return doCompileProject(operation, plan);
		} else if (operation instanceof PStart) {
			return doCompileStart((PStart) operation, plan);
		} else {
			throw new IllegalArgumentException(
					"Unsupported POperation in query plan: " + plan.toShortString());
		}
		// TODO dispatch
	}


	private QueryPlanRecipeTraceInfo doCompileStart(PStart operation,
			SubPlan plan) {
		if (!operation.getAPrioriVariables().isEmpty()) {
			throw new IllegalArgumentException(
					"Input variables unsupported by Rete: " + plan.toShortString());
		}
		final ConstantRecipe recipe = FACTORY.createConstantRecipe();
		recipe.getConstantValues().clear();
		
		return new QueryPlanRecipeTraceInfo(plan, recipe, new HashSet<RecipeTraceInfo>());
	}

	private QueryPlanRecipeTraceInfo doCompileEnumerate(
			EnumerablePConstraint constraint,
			SubPlan plan) {		
		TODO check variable coincidences
		TODO determine ordering
		return null;
	}
	
	
	
	
}
