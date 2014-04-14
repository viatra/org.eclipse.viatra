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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.incquery.runtime.matchers.planning.QueryPlannerException;
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
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExpressionEvaluation;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Inequality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.NegativePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.PatternMatchCounter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.BinaryTransitiveClosure;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.ConstantValue;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;
import org.eclipse.incquery.runtime.rete.construction.plancompiler.CompilerHelper.JoinHelper;
import org.eclipse.incquery.runtime.rete.recipes.AntiJoinRecipe;
import org.eclipse.incquery.runtime.rete.recipes.BinaryInputRecipe;
import org.eclipse.incquery.runtime.rete.recipes.ConstantRecipe;
import org.eclipse.incquery.runtime.rete.recipes.CountAggregatorRecipe;
import org.eclipse.incquery.runtime.rete.recipes.EqualityFilterRecipe;
import org.eclipse.incquery.runtime.rete.recipes.ExpressionEnforcerRecipe;
import org.eclipse.incquery.runtime.rete.recipes.IndexerRecipe;
import org.eclipse.incquery.runtime.rete.recipes.InequalityFilterRecipe;
import org.eclipse.incquery.runtime.rete.recipes.JoinRecipe;
import org.eclipse.incquery.runtime.rete.recipes.ProductionRecipe;
import org.eclipse.incquery.runtime.rete.recipes.ProjectionIndexerRecipe;
import org.eclipse.incquery.runtime.rete.recipes.RecipesFactory;
import org.eclipse.incquery.runtime.rete.recipes.ReteNodeRecipe;
import org.eclipse.incquery.runtime.rete.recipes.TransitiveClosureRecipe;
import org.eclipse.incquery.runtime.rete.recipes.TrimmerRecipe;
import org.eclipse.incquery.runtime.rete.recipes.TypeInputRecipe;
import org.eclipse.incquery.runtime.rete.recipes.UnaryInputRecipe;
import org.eclipse.incquery.runtime.rete.recipes.UniquenessEnforcerRecipe;
import org.eclipse.incquery.runtime.rete.recipes.helper.RecipesHelper;
import org.eclipse.incquery.runtime.rete.traceability.AuxiliaryPlanningTrace;
import org.eclipse.incquery.runtime.rete.traceability.ParameterProjectionTrace;
import org.eclipse.incquery.runtime.rete.traceability.ProductionTraceInfo;
import org.eclipse.incquery.runtime.rete.traceability.CompiledQueryPlan;
import org.eclipse.incquery.runtime.rete.traceability.RecipeTraceInfo;

/**
 * Compiles query plans into Rete recipes traced by a {@link CompiledQueryPlan}.
 * 
 * @author Bergmann Gabor
 *
 */
public class RecipePlanCompiler {
	
	private Map<SubPlan, CompiledQueryPlan> compilerCache = new HashMap<SubPlan, CompiledQueryPlan>();
	private Map<ReteNodeRecipe, SubPlan> backTrace = new HashMap<ReteNodeRecipe, SubPlan>();
	
	final static RecipesFactory FACTORY = RecipesFactory.eINSTANCE;
	
	public RecipeTraceInfo compileProduction(PQuery query, Collection<SubPlan> bodies) throws QueryPlannerException {
		//TODO skip production node if there is just one body and no projection needed?
		Collection<RecipeTraceInfo> bodyFinalTraces = new HashSet<RecipeTraceInfo>();
		Collection<ReteNodeRecipe> bodyFinalRecipes = new HashSet<ReteNodeRecipe>();
		
		for (SubPlan bodyFinalPlan : bodies) {
			// skip over any projections at the end
			while (bodyFinalPlan.getOperation() instanceof PProject)
				bodyFinalPlan = bodyFinalPlan.getParentPlans().get(0);
			
			// TODO checkAndTrimEqualVariables may introduce superfluous trim, 
			// but whatever (no uniqueness enforcer needed)
			
			// compile body
			final CompiledQueryPlan compiledBody = getCompiledForm(bodyFinalPlan);
			
			// project to parameter list 
			final PBody body = bodyFinalPlan.getBody();
			final List<PVariable> parameterList = body.getSymbolicParameterVariables();
			if (parameterList.equals(compiledBody.getVariablesTuple())) { // no projection needed
				bodyFinalTraces.add(compiledBody);
				bodyFinalRecipes.add(compiledBody.getRecipe());
			} else {
				TrimmerRecipe trimmerRecipe = CompilerHelper.makeTrimmerRecipe(compiledBody, parameterList);
				RecipeTraceInfo trimmerTrace = new ParameterProjectionTrace(body, trimmerRecipe, compiledBody);
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
	
	public CompiledQueryPlan getCompiledForm(SubPlan plan) throws QueryPlannerException {
		CompiledQueryPlan compiled = compilerCache.get(plan);
		if (compiled == null) {
			compiled = doCompileDispatch(plan);
			compilerCache.put(plan, compiled);
			backTrace.put(compiled.getRecipe(), plan);
		}
		return compiled;
	}
	
	
	private CompiledQueryPlan doCompileDispatch(SubPlan plan) throws QueryPlannerException {
		final POperation operation = plan.getOperation();
		if (operation instanceof PEnumerate) {
			return doCompileEnumerate(((PEnumerate) operation).getEnumerablePConstraint(), plan);
		} else if (operation instanceof PApply) {
			final PConstraint pConstraint = ((PApply) operation).getPConstraint();
			if (pConstraint instanceof EnumerablePConstraint) {
				CompiledQueryPlan primaryParent = getCompiledForm(plan.getParentPlans().get(0));
				AuxiliaryPlanningTrace secondaryParent = 
						doEnumerateDispatch(plan, (EnumerablePConstraint) pConstraint);		
				return compileToNaturalJoin(plan, primaryParent, secondaryParent);
			} else if (pConstraint instanceof DeferredPConstraint) {
				 return doDeferredDispatch((DeferredPConstraint)pConstraint, plan);
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


	private CompiledQueryPlan doDeferredDispatch(DeferredPConstraint constraint, SubPlan plan) throws QueryPlannerException {
		final SubPlan parentPlan = plan.getParentPlans().get(0);
    	final CompiledQueryPlan parentCompiled = getCompiledForm(parentPlan);
        if (constraint instanceof Equality) {
            return compileDeferred((Equality)constraint, plan, parentPlan, parentCompiled);
        } else if (constraint instanceof ExportedParameter) {
            return compileDeferred((ExportedParameter)constraint, plan, parentPlan, parentCompiled);
        } else if (constraint instanceof Inequality) {
            return compileDeferred((Inequality)constraint, plan, parentPlan, parentCompiled);
        } else if (constraint instanceof NegativePatternCall) {
            return compileDeferred((NegativePatternCall)constraint, plan, parentPlan, parentCompiled);
        } else if (constraint instanceof PatternMatchCounter) {
            return compileDeferred((PatternMatchCounter)constraint, plan, parentPlan, parentCompiled);
        } else if (constraint instanceof ExpressionEvaluation) {
            return compileDeferred((ExpressionEvaluation)constraint, plan, parentPlan, parentCompiled);
        }
        throw new UnsupportedOperationException("Unknown deferred constraint " + constraint);
	}
	
    private CompiledQueryPlan compileDeferred(Equality constraint, 
    		SubPlan plan, SubPlan parentPlan, CompiledQueryPlan parentCompiled) {
        if (constraint.isMoot())
        	return parentCompiled.cloneFor(plan);

        Integer index1 = parentCompiled.getPosMapping().get(constraint.getWho());
        Integer index2 = parentCompiled.getPosMapping().get(constraint.getWithWhom());
        
        if (index1 != null && index2 != null && index1 != index2 ) {
        	Integer indexLower = Math.min(index1, index2);
        	Integer indexHigher = Math.max(index1, index2);
        	
        	EqualityFilterRecipe equalityFilterRecipe = FACTORY.createEqualityFilterRecipe();
        	equalityFilterRecipe.setParent(parentCompiled.getRecipe());
        	equalityFilterRecipe.getIndices().add(indexLower);
        	equalityFilterRecipe.getIndices().add(indexHigher);	
        	
            return new CompiledQueryPlan(plan, 
            		parentCompiled.getVariablesTuple(), 
            		equalityFilterRecipe, 
            		parentCompiled);
        } else {
            throw new IllegalArgumentException(
            		String.format(
            				"Unable to interpret %s after compiled parent %s", 
            				plan.toShortString(), parentCompiled.toString()
            		)
            );
        }
    }
    
    private CompiledQueryPlan compileDeferred(ExportedParameter constraint, 
    		SubPlan plan, SubPlan parentPlan, CompiledQueryPlan parentCompiled) {
    	return parentCompiled.cloneFor(plan);
    }
    
    private CompiledQueryPlan compileDeferred(Inequality constraint, 
    		SubPlan plan, SubPlan parentPlan, CompiledQueryPlan parentCompiled) {
        if (constraint.isEliminable())
        	return parentCompiled.cloneFor(plan);

        Integer index1 = parentCompiled.getPosMapping().get(constraint.getWho());
        Integer index2 = parentCompiled.getPosMapping().get(constraint.getWithWhom());
        
        if (index1 != null && index2 != null && index1 != index2 ) {
        	Integer indexLower = Math.min(index1, index2);
        	Integer indexHigher = Math.max(index1, index2);
        	
        	InequalityFilterRecipe inequalityFilterRecipe = FACTORY.createInequalityFilterRecipe();
        	inequalityFilterRecipe.setParent(parentCompiled.getRecipe());
        	inequalityFilterRecipe.setSubject(indexLower);
        	inequalityFilterRecipe.getInequals().add(indexHigher);
        	        	
            return new CompiledQueryPlan(plan, 
            		parentCompiled.getVariablesTuple(), 
            		inequalityFilterRecipe, 
            		parentCompiled);
        } else {
            throw new IllegalArgumentException(
            		String.format(
            				"Unable to interpret %s after compiled parent %s", 
            				plan.toShortString(), parentCompiled.toString()
            		)
            );
        }
    }
    private CompiledQueryPlan compileDeferred(NegativePatternCall constraint, 
    		SubPlan plan, SubPlan parentPlan, CompiledQueryPlan parentCompiled) throws QueryPlannerException  
    {
		final AuxiliaryPlanningTrace callTrace = referQuery(constraint.getReferredQuery());
		
		JoinHelper joinHelper = new JoinHelper(plan, parentCompiled, callTrace);
		final RecipeTraceInfo primaryIndexer = joinHelper.getPrimaryIndexer();
		final RecipeTraceInfo secondaryIndexer = joinHelper.getSecondaryIndexer();
		
		AntiJoinRecipe antiJoinRecipe = FACTORY.createAntiJoinRecipe();
		antiJoinRecipe.setLeftParent((ProjectionIndexerRecipe) primaryIndexer.getRecipe());
		antiJoinRecipe.setRightParent((IndexerRecipe) secondaryIndexer.getRecipe());
		
		return new CompiledQueryPlan(plan, parentCompiled.getVariablesTuple(), antiJoinRecipe, primaryIndexer, secondaryIndexer);
    }
    private CompiledQueryPlan compileDeferred(PatternMatchCounter constraint, 
    		SubPlan plan, SubPlan parentPlan, CompiledQueryPlan parentCompiled) throws QueryPlannerException  
    {
		final AuxiliaryPlanningTrace callTrace = referQuery(constraint.getReferredQuery());
		
		// hack: use some mask computations (+ the indexers) from a fake natural join against the called query
		JoinHelper fakeJoinHelper = new JoinHelper(plan, parentCompiled, callTrace);
		final RecipeTraceInfo primaryIndexer = fakeJoinHelper.getPrimaryIndexer();
		final RecipeTraceInfo callProjectionIndexer = fakeJoinHelper.getSecondaryIndexer();

		final List<PVariable> sideVariablesTuple = fakeJoinHelper.getSecondaryMask().transform(callTrace.getVariablesTuple());
		/*if (!booleanCheck)*/ sideVariablesTuple.add(constraint.getResultVariable());

		CountAggregatorRecipe aggregatorRecipe = FACTORY.createCountAggregatorRecipe();
		aggregatorRecipe.setParent((ProjectionIndexerRecipe) callProjectionIndexer.getRecipe());
		AuxiliaryPlanningTrace aggregatorTrace = 
				new AuxiliaryPlanningTrace(plan, sideVariablesTuple, aggregatorRecipe, callProjectionIndexer);
		
		IndexerRecipe aggregatorIndexerRecipe = FACTORY.createAggregatorIndexerRecipe();
		aggregatorIndexerRecipe.setParent(aggregatorRecipe);
		aggregatorIndexerRecipe.setMask(RecipesHelper.mask(
				sideVariablesTuple.size(), 
				//use same indices as in the projection indexer 
				// EVEN if result variable already visible in left parent
				fakeJoinHelper.getSecondaryMask().indices 
		));
		AuxiliaryPlanningTrace aggregatorIndexerTrace = 
				new AuxiliaryPlanningTrace(plan, sideVariablesTuple, aggregatorIndexerRecipe, aggregatorTrace);
		
    	JoinRecipe naturalJoinRecipe = FACTORY.createJoinRecipe();
    	naturalJoinRecipe.setLeftParent((ProjectionIndexerRecipe) primaryIndexer.getRecipe());
    	naturalJoinRecipe.setRightParent(aggregatorIndexerRecipe);
		naturalJoinRecipe.setRightParentComplementaryMask(RecipesHelper.mask(
				sideVariablesTuple.size(), 
				// extend with last element only - the computation value
				sideVariablesTuple.size() - 1
		));
		
        // what if the new variable already has a value?
		boolean alreadyKnown = parentPlan.getVisibleVariables().contains(constraint.getResultVariable());
		final List<PVariable> aggregatedVariablesTuple = new ArrayList<PVariable>(parentCompiled.getVariablesTuple());		
		if (!alreadyKnown) aggregatedVariablesTuple.add(constraint.getResultVariable());

		AuxiliaryPlanningTrace joinTrace = new AuxiliaryPlanningTrace(plan,
			aggregatedVariablesTuple, 
			naturalJoinRecipe, 
			primaryIndexer, aggregatorIndexerTrace);

        return CompilerHelper.checkAndTrimEqualVariables(plan, joinTrace).cloneFor(plan);
//		if (!alreadyKnown) {
//			return joinTrace.cloneFor(plan);
//		} else {
//        	//final Integer equalsWithIndex = parentCompiled.getPosMapping().get(parentCompiled.getVariablesTuple());
//		}
    }
    
    private CompiledQueryPlan compileDeferred(ExpressionEvaluation constraint, 
    		SubPlan plan, SubPlan parentPlan, CompiledQueryPlan parentCompiled) {
        Map<String, Integer> tupleNameMap = new HashMap<String, Integer>();
        for (String name : constraint.getEvaluator().getInputParameterNames()) {
            Map<? extends Object, Integer> index = parentCompiled.getPosMapping();
            PVariable variable = constraint.getPSystem().getVariableByNameChecked(name);
            Integer position = index.get(variable);
            tupleNameMap.put(name, position);
        }
        
        final PVariable outputVariable = constraint.getOutputVariable();
		final boolean booleanCheck = outputVariable == null;
        
		ExpressionEnforcerRecipe enforcerRecipe = 
				booleanCheck ? FACTORY.createCheckRecipe() : FACTORY.createEvalRecipe();
		enforcerRecipe.setParent(parentCompiled.getRecipe());
		enforcerRecipe.setExpression(RecipesHelper.expressionDefinition(constraint));
		enforcerRecipe.getMappedIndices().addAll(tupleNameMap.entrySet());
        				
        final List<PVariable> enforcerVariablesTuple = new ArrayList<PVariable>(parentCompiled.getVariablesTuple());
        if (!booleanCheck) enforcerVariablesTuple.add(outputVariable);
        AuxiliaryPlanningTrace enforcerTrace = 
        		new AuxiliaryPlanningTrace(plan, enforcerVariablesTuple, enforcerRecipe, parentCompiled);

        return CompilerHelper.checkAndTrimEqualVariables(plan, enforcerTrace).cloneFor(plan);
    }


	private CompiledQueryPlan doCompileJoin(PJoin operation, SubPlan plan) throws QueryPlannerException {
		final List<CompiledQueryPlan> compiledParents = getCompiledFormOfParents(plan);
		final CompiledQueryPlan leftCompiled = compiledParents.get(0);
		final CompiledQueryPlan rightCompiled = compiledParents.get(1);
		
		return compileToNaturalJoin(plan, leftCompiled, rightCompiled);
	}

	private CompiledQueryPlan compileToNaturalJoin(SubPlan plan,
			final AuxiliaryPlanningTrace leftCompiled,
			final AuxiliaryPlanningTrace rightCompiled) {
		JoinHelper joinHelper = new JoinHelper(plan, leftCompiled, rightCompiled);
        return new CompiledQueryPlan(plan, 
        		joinHelper.getNaturalJoinVariablesTuple(), 
        		joinHelper.getNaturalJoinRecipe(), 
        		joinHelper.getPrimaryIndexer(), joinHelper.getSecondaryIndexer());
	}

	private CompiledQueryPlan doCompileProject(PProject operation, SubPlan plan) throws QueryPlannerException {
		final List<CompiledQueryPlan> compiledParents = getCompiledFormOfParents(plan);
		final CompiledQueryPlan compiledParent = compiledParents.get(0);
		
		// TODO add smarter ordering here?
		List<PVariable> projectedVariables = new ArrayList<PVariable>(operation.getToVariables());
		
		final TrimmerRecipe trimmerRecipe = CompilerHelper.makeTrimmerRecipe(compiledParent, projectedVariables);
		
		if (BuildHelper.areAllVariablesDetermined(plan.getParentPlans().get(0), projectedVariables)) {
			// skip uniqueness enforcement if unneeded?
			return new CompiledQueryPlan(plan, projectedVariables, trimmerRecipe, compiledParent);
		} else {
			RecipeTraceInfo trimTrace = new AuxiliaryPlanningTrace(plan, projectedVariables, trimmerRecipe, compiledParent);
			UniquenessEnforcerRecipe uniquenessEnforcerRecipe = FACTORY.createUniquenessEnforcerRecipe();
			uniquenessEnforcerRecipe.getParents().add(trimmerRecipe);			
			return new CompiledQueryPlan(plan, projectedVariables, uniquenessEnforcerRecipe, trimTrace);
		}							
	}

	private CompiledQueryPlan doCompileStart(PStart operation,
			SubPlan plan) {
		if (!operation.getAPrioriVariables().isEmpty()) {
			throw new IllegalArgumentException(
					"Input variables unsupported by Rete: " + plan.toShortString());
		}
		final ConstantRecipe recipe = FACTORY.createConstantRecipe();
		recipe.getConstantValues().clear();
		
		return new CompiledQueryPlan(plan, new ArrayList<PVariable>(), recipe);
	}

	private CompiledQueryPlan doCompileEnumerate(
			EnumerablePConstraint constraint,
			SubPlan plan) throws QueryPlannerException 
	{		
		final AuxiliaryPlanningTrace trimmedTrace = 
				doEnumerateAndDeduplicate(constraint, plan);
			
		return trimmedTrace.cloneFor(plan);
	}

	private AuxiliaryPlanningTrace doEnumerateAndDeduplicate(
			EnumerablePConstraint constraint, SubPlan plan) throws QueryPlannerException 
	{
		final AuxiliaryPlanningTrace coreTrace = 
				doEnumerateDispatch(plan, constraint);		
		final AuxiliaryPlanningTrace trimmedTrace = 
				CompilerHelper.checkAndTrimEqualVariables(plan, coreTrace);
		return trimmedTrace;
	}

	
	
	
	private AuxiliaryPlanningTrace doEnumerateDispatch(SubPlan plan, EnumerablePConstraint constraint) throws QueryPlannerException {
        if (constraint instanceof BinaryTransitiveClosure) {
            return compileEnumerable(plan, (BinaryTransitiveClosure) constraint);
        } else if (constraint instanceof ConstantValue) {
            return compileEnumerable(plan, (ConstantValue) constraint);
//        } else if (constraint instanceof Containment) {
//            return compileEnumerable(plan, (Containment) constraint);
//        } else if (constraint instanceof Generalization) {
//            return compileEnumerable(plan, (Generalization) constraint);
//        } else if (constraint instanceof Instantiation) {
//            return compileEnumerable(plan, (Instantiation) constraint);
        } else if (constraint instanceof PositivePatternCall) {
            return compileEnumerable(plan, (PositivePatternCall) constraint);
        } else if (constraint instanceof TypeBinary) {
            return compileEnumerable(plan, (TypeBinary) constraint);
//        } else if (constraint instanceof TypeTernary) {
//            return compileEnumerable((TypeTernary) constraint);
        } else if (constraint instanceof TypeUnary) {
            return compileEnumerable(plan, (TypeUnary) constraint);
        }
        throw new UnsupportedOperationException("Unknown enumerable constraint " + constraint);
	}

	private AuxiliaryPlanningTrace compileEnumerable(SubPlan plan,
			BinaryTransitiveClosure constraint) throws QueryPlannerException {
		final PQuery referredQuery = constraint.getSupplierKey();
		final AuxiliaryPlanningTrace callTrace = referQuery(referredQuery);
		
		final TransitiveClosureRecipe recipe = FACTORY.createTransitiveClosureRecipe();
		recipe.setParent(callTrace.getRecipe());

		return new AuxiliaryPlanningTrace(plan, CompilerHelper.convertVariablesTuple(constraint), recipe, callTrace);
	}

	private AuxiliaryPlanningTrace compileEnumerable(SubPlan plan, PositivePatternCall constraint) throws QueryPlannerException {
		final PQuery referredQuery = constraint.getReferredQuery();
		return referQuery(referredQuery);
	}

	private AuxiliaryPlanningTrace compileEnumerable(SubPlan plan, TypeBinary constraint) {
		final BinaryInputRecipe recipe = FACTORY.createBinaryInputRecipe();
		initTypeInputRecipe(constraint, recipe);
		return new AuxiliaryPlanningTrace(plan, CompilerHelper.convertVariablesTuple(constraint), recipe);
	}
	private AuxiliaryPlanningTrace compileEnumerable(SubPlan plan, TypeUnary constraint) {
		final UnaryInputRecipe recipe = FACTORY.createUnaryInputRecipe();
		initTypeInputRecipe(constraint, recipe);
		return new AuxiliaryPlanningTrace(plan, CompilerHelper.convertVariablesTuple(constraint), recipe);
	}
	private void initTypeInputRecipe(TypeConstraint constraint, final TypeInputRecipe recipe) {
		recipe.setTypeKey(constraint.getSupplierKey());
		recipe.setTypeName(constraint.getTypeString());
	}
	
	private AuxiliaryPlanningTrace compileEnumerable(SubPlan plan, ConstantValue constraint) {
		final ConstantRecipe recipe = FACTORY.createConstantRecipe();
		recipe.getConstantValues().add(constraint.getSupplierKey());
		return new AuxiliaryPlanningTrace(plan, CompilerHelper.convertVariablesTuple(constraint), recipe);
	}
	
	
	private AuxiliaryPlanningTrace referQuery(PQuery query) throws QueryPlannerException {
	}

	

	protected List<CompiledQueryPlan> getCompiledFormOfParents(SubPlan plan) throws QueryPlannerException {
		List<CompiledQueryPlan> results = new ArrayList<CompiledQueryPlan>();
		for (SubPlan parentPlan : plan.getParentPlans()) {
			results.add(getCompiledForm(parentPlan));
		}
		return results;
	}
	
	
}
