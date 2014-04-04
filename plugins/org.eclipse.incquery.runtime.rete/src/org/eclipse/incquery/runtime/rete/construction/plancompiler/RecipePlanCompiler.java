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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.incquery.runtime.matchers.planning.SubPlan;
import org.eclipse.incquery.runtime.matchers.planning.helpers.BuildHelper;
import org.eclipse.incquery.runtime.matchers.planning.operations.PApply;
import org.eclipse.incquery.runtime.matchers.planning.operations.PEnumerate;
import org.eclipse.incquery.runtime.matchers.planning.operations.PJoin;
import org.eclipse.incquery.runtime.matchers.planning.operations.POperation;
import org.eclipse.incquery.runtime.matchers.planning.operations.PProject;
import org.eclipse.incquery.runtime.matchers.planning.operations.PStart;
import org.eclipse.incquery.runtime.matchers.psystem.DeferredPConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.EnumerablePConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PQuery;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.matchers.tuple.TupleMask;
import org.eclipse.incquery.runtime.rete.construction.plancompiler.CompilerHelper.JoinHelper;
import org.eclipse.incquery.runtime.rete.recipes.ConstantRecipe;
import org.eclipse.incquery.runtime.rete.recipes.IndexerRecipe;
import org.eclipse.incquery.runtime.rete.recipes.JoinRecipe;
import org.eclipse.incquery.runtime.rete.recipes.ProductionRecipe;
import org.eclipse.incquery.runtime.rete.recipes.ProjectionIndexerRecipe;
import org.eclipse.incquery.runtime.rete.recipes.RecipesFactory;
import org.eclipse.incquery.runtime.rete.recipes.ReteNodeRecipe;
import org.eclipse.incquery.runtime.rete.recipes.TrimmerRecipe;
import org.eclipse.incquery.runtime.rete.recipes.UniquenessEnforcerRecipe;
import org.eclipse.incquery.runtime.rete.recipes.helper.RecipesHelper;
import org.eclipse.incquery.runtime.rete.traceability.AuxiliaryPlanningRecipeTraceInfo;
import org.eclipse.incquery.runtime.rete.traceability.ParameterProjectionTraceInfo;
import org.eclipse.incquery.runtime.rete.traceability.ProductionTraceInfo;
import org.eclipse.incquery.runtime.rete.traceability.QueryPlanRecipeTraceInfo;
import org.eclipse.incquery.runtime.rete.traceability.RecipeTraceInfo;
import org.eclipse.incquery.runtime.rete.util.Options;

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
			// skip over any projections at the end
			while (bodyFinalPlan.getOperation() instanceof PProject)
				bodyFinalPlan = bodyFinalPlan.getParentPlans().get(0);
		
			// compile body
			final QueryPlanRecipeTraceInfo compiledBody = getCompiledForm(bodyFinalPlan);
			
			// project to parameter list 
			final PBody body = bodyFinalPlan.getBody();
			final List<PVariable> parameterList = body.getSymbolicParameterVariables();
			if (parameterList.equals(compiledBody.getVariablesTuple())) { // no projection needed
				bodyFinalTraces.add(compiledBody);
				bodyFinalRecipes.add(compiledBody.getRecipe());
			} else {
				TrimmerRecipe trimmerRecipe = makeTrimmerRecipe(compiledBody, parameterList);
				RecipeTraceInfo trimmerTrace = new ParameterProjectionTraceInfo(body, trimmerRecipe, compiledBody);
				bodyFinalTraces.add(trimmerTrace);
				bodyFinalRecipes.add(trimmerRecipe);
			}
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
			return doCompileJoin((PJoin) operation, plan);
		} else if (operation instanceof PProject) {
			return doCompileProject((PProject) operation, plan);
		} else if (operation instanceof PStart) {
			return doCompileStart((PStart) operation, plan);
		} else {
			throw new IllegalArgumentException(
					"Unsupported POperation in query plan: " + plan.toShortString());
		}
		// TODO dispatch
	}


	private QueryPlanRecipeTraceInfo doCompileJoin(PJoin operation, SubPlan plan) {
		final List<QueryPlanRecipeTraceInfo> compiledParents = getCompiledFormOfParents(plan);
		final QueryPlanRecipeTraceInfo leftCompiled = compiledParents.get(0);
		final QueryPlanRecipeTraceInfo rightCompiled = compiledParents.get(1);
		
		JoinHelper joinHelper = new JoinHelper(plan, leftCompiled, rightCompiled);
        		
        return new QueryPlanRecipeTraceInfo(plan, 
        		joinHelper.getVariablesTuple(), joinHelper.getJoinRecipe(), compiledParents);
	}
	/**
	 * @param operation
	 * @param plan
	 * @return
	 */
	private QueryPlanRecipeTraceInfo doCompileProject(PProject operation, SubPlan plan) {
		final List<QueryPlanRecipeTraceInfo> compiledParents = getCompiledFormOfParents(plan);
		final QueryPlanRecipeTraceInfo compiledParent = compiledParents.get(0);
		
		// TODO add smarter ordering here?
		List<PVariable> projectedVariables = new ArrayList<PVariable>(operation.getToVariables());
		
		final TrimmerRecipe trimmerRecipe = makeTrimmerRecipe(compiledParent, projectedVariables);
		
		if (BuildHelper.areAllVariablesDetermined(plan.getParentPlans().get(0), projectedVariables)) {
			// skip uniqueness enforcement if unneeded?
			return new QueryPlanRecipeTraceInfo(plan, projectedVariables, trimmerRecipe, compiledParent);
		} else {
			UniquenessEnforcerRecipe uniquenessEnforcerRecipe = FACTORY.createUniquenessEnforcerRecipe();
			uniquenessEnforcerRecipe.getParents().add(trimmerRecipe);			
			RecipeTraceInfo trimTrace = new AuxiliaryPlanningRecipeTraceInfo(plan, trimmerRecipe, compiledParent);
			return new QueryPlanRecipeTraceInfo(plan, projectedVariables, uniquenessEnforcerRecipe, trimTrace);
		}							
	}

	private QueryPlanRecipeTraceInfo doCompileStart(PStart operation,
			SubPlan plan) {
		if (!operation.getAPrioriVariables().isEmpty()) {
			throw new IllegalArgumentException(
					"Input variables unsupported by Rete: " + plan.toShortString());
		}
		final ConstantRecipe recipe = FACTORY.createConstantRecipe();
		recipe.getConstantValues().clear();
		
		return new QueryPlanRecipeTraceInfo(plan, new ArrayList<PVariable>(), recipe);
	}

	private QueryPlanRecipeTraceInfo doCompileEnumerate(
			EnumerablePConstraint constraint,
			SubPlan plan) {		
		final Tuple originalVariablesTuple = constraint.getVariablesTuple();
		final Map<Object, Integer> invertedIndex = originalVariablesTuple.invertIndex();
		List<PVariable> variables = new ArrayList<PVariable>();
		for (int i = 0; i < originalVariablesTuple.getSize(); ++i) {
			final Object variable = originalVariablesTuple.get(i);
			if (i == invertedIndex.get(variable)){ // only on last occurrence
				variables.add((PVariable) variable);
			}
		}
		FACTORY.crea
		return new QueryPlanRecipeTraceInfo(plan, variables, recipe);
		
		TODO check variable coincidences
		TODO determine ordering
	}
	
	
	protected List<QueryPlanRecipeTraceInfo> getCompiledFormOfParents(SubPlan plan) {
		List<QueryPlanRecipeTraceInfo> results = new ArrayList<QueryPlanRecipeTraceInfo>();
		for (SubPlan parentPlan : plan.getParentPlans()) {
			results.add(getCompiledForm(parentPlan));
		}
		return results;
	}

	protected TrimmerRecipe makeTrimmerRecipe(
			final QueryPlanRecipeTraceInfo compiledParent,
			List<PVariable> projectedVariables) {
		final ReteNodeRecipe parentRecipe = compiledParent.getRecipe();
		List<Integer> projectionSourceIndices = new ArrayList<Integer>();
		for (PVariable pVariable : projectedVariables) {
			projectionSourceIndices.add(compiledParent.getPosMapping().get(pVariable));
		}
		final TrimmerRecipe trimmerRecipe = FACTORY.createTrimmerRecipe();
		trimmerRecipe.setParent(parentRecipe);
		trimmerRecipe.setMask(RecipesHelper.mask(parentRecipe.getArity(), projectionSourceIndices));
		return trimmerRecipe;
	}
	
	
}
