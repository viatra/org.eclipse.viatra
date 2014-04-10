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
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.Containment;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.Generalization;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.Instantiation;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeTernary;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.matchers.tuple.TupleMask;
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
				TrimmerRecipe trimmerRecipe = CompilerHelper.makeTrimmerRecipe(compiledBody, parameterList);
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
				AuxiliaryPlanningRecipeTraceInfo secondaryParent = 
						doEnumerateDispatch(plan, (EnumerablePConstraint) pConstraint);		
				return compileToNaturalJoin(plan, primaryParent, secondaryParent);
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


	private QueryPlanRecipeTraceInfo doCheckDispatch(DeferredPConstraint constraint, SubPlan plan) {
		final SubPlan parentPlan = plan.getParentPlans().get(0);
    	final QueryPlanRecipeTraceInfo parentCompiled = getCompiledForm(parentPlan);
        if (constraint instanceof Equality) {
            return processConstraint((Equality)constraint, plan, parentPlan, parentCompiled);
        } else if (constraint instanceof ExportedParameter) {
            return processConstraint((ExportedParameter)constraint, plan, parentPlan, parentCompiled);
        } else if (constraint instanceof Inequality) {
            return processConstraint((Inequality)constraint, plan, parentPlan, parentCompiled);
        } else if (constraint instanceof NegativePatternCall) {
            return processConstraint((NegativePatternCall)constraint, plan, parentPlan, parentCompiled);
        } else if (constraint instanceof PatternMatchCounter) {
            return processConstraint((PatternMatchCounter)constraint, plan, parentPlan, parentCompiled);
        } else if (constraint instanceof ExpressionEvaluation) {
            return processConstraint((ExpressionEvaluation)constraint, plan, parentPlan, parentCompiled);
        }
        throw new UnsupportedOperationException("Unknown deferred constraint " + constraint);
	}
	
    private QueryPlanRecipeTraceInfo processConstraint(Equality constraint, 
    		SubPlan plan, SubPlan parentPlan, QueryPlanRecipeTraceInfo parentCompiled) {
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
        	
            return new QueryPlanRecipeTraceInfo(plan, 
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
    
    private QueryPlanRecipeTraceInfo processConstraint(ExportedParameter constraint, 
    		SubPlan plan, SubPlan parentPlan, QueryPlanRecipeTraceInfo parentCompiled) {
    	return parentCompiled.cloneFor(plan);
    }
    
    private QueryPlanRecipeTraceInfo processConstraint(Inequality constraint, 
    		SubPlan plan, SubPlan parentPlan, QueryPlanRecipeTraceInfo parentCompiled) {
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
        	        	
            return new QueryPlanRecipeTraceInfo(plan, 
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
    private QueryPlanRecipeTraceInfo processConstraint(NegativePatternCall constraint, 
    		SubPlan plan, SubPlan parentPlan, QueryPlanRecipeTraceInfo parentCompiled)  
    {
		final AuxiliaryPlanningRecipeTraceInfo callTrace = referQuery(constraint.getReferredQuery());
		
		JoinHelper joinHelper = new JoinHelper(plan, parentCompiled, callTrace);
		final RecipeTraceInfo primaryIndexer = joinHelper.getPrimaryIndexer();
		final RecipeTraceInfo secondaryIndexer = joinHelper.getSecondaryIndexer();
		
		AntiJoinRecipe antiJoinRecipe = FACTORY.createAntiJoinRecipe();
		antiJoinRecipe.setLeftParent((ProjectionIndexerRecipe) primaryIndexer.getRecipe());
		antiJoinRecipe.setRightParent((IndexerRecipe) secondaryIndexer.getRecipe());
		
		return new QueryPlanRecipeTraceInfo(plan, parentCompiled.getVariablesTuple(), antiJoinRecipe, primaryIndexer, secondaryIndexer);
    }
    private QueryPlanRecipeTraceInfo processConstraint(PatternMatchCounter constraint, 
    		SubPlan plan, SubPlan parentPlan, QueryPlanRecipeTraceInfo parentCompiled)  
    {
		final AuxiliaryPlanningRecipeTraceInfo callTrace = referQuery(constraint.getReferredQuery());
		
		JoinHelper fakeJoinHelper = new JoinHelper(plan, parentCompiled, callTrace);
		final RecipeTraceInfo fakePrimaryIndexer = fakeJoinHelper.getPrimaryIndexer();
		final RecipeTraceInfo fakeSecondaryIndexer = fakeJoinHelper.getSecondaryIndexer();

		final List<PVariable> sideVariablesTuple = fakeJoinHelper.getSecondaryMask().transform(callTrace.getVariablesTuple());
		/*if (!booleanCheck)*/ sideVariablesTuple.add(constraint.getResultVariable());

		CountAggregatorRecipe aggregatorRecipe = FACTORY.createCountAggregatorRecipe();
		aggregatorRecipe.setParent((ProjectionIndexerRecipe) fakeSecondaryIndexer.getRecipe());
		AuxiliaryPlanningRecipeTraceInfo aggregatorTrace = new AuxiliaryPlanningRecipeTraceInfo(plan, sideVariablesTuple, aggregatorRecipe, fakeSecondaryIndexer);
		
		JoinHelper joinHelper = new JoinHelper(plan, parentCompiled, aggregatorTrace);		
		return new QueryPlanRecipeTraceInfo(plan, 
				joinHelper.getNaturalJoinVariablesTuple(), 
				joinHelper.getNaturalJoinRecipe(), 
				joinHelper.getPrimaryIndexer(), joinHelper.getSecondaryIndexer());
        TODO what if new variable already known
//        SubPlan sidePlan = constraint.getSidePlan(compiler);
//        BuildHelper.JoinHelper joinHelper = new BuildHelper.JoinHelper(parentPlan, sidePlan);
//        Integer resultPositionLeft = parentPlan.getVariablesIndex().get(constraint.getResultVariable());
//        TupleMask primaryMask = joinHelper.getPrimaryMask();
//        TupleMask secondaryMask = joinHelper.getSecondaryMask();
//        final SubPlan counterBetaPlan = compiler.buildCounterBetaNode(parentPlan, sidePlan, primaryMask, secondaryMask,
//                joinHelper.getComplementerMask(), constraint.getResultVariable());
//        if (resultPositionLeft == null) {
//            return counterBetaPlan;
//        } else {
//            int resultPositionFinal = counterBetaPlan.getNaturalJoinVariablesTuple().getSize() - 1; // appended to the last position
//            final SubPlan equalityCheckerPlan = 
//                    compiler.buildEqualityChecker(counterBetaPlan, new int[]{resultPositionFinal, resultPositionLeft});
//            return compiler.buildTrimmer(equalityCheckerPlan, TupleMask.omit(resultPositionFinal, 1+resultPositionFinal), false);
//        }
    }
    
    private QueryPlanRecipeTraceInfo processConstraint(ExpressionEvaluation constraint, 
    		SubPlan plan, SubPlan parentPlan, QueryPlanRecipeTraceInfo parentCompiled) {
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
        AuxiliaryPlanningRecipeTraceInfo enforcerTrace = 
        		new AuxiliaryPlanningRecipeTraceInfo(plan, enforcerVariablesTuple, enforcerRecipe, parentCompiled);
        AuxiliaryPlanningRecipeTraceInfo resultTrace = enforcerTrace;
        if (!booleanCheck && parentPlan.getVisibleVariables().contains(outputVariable)) {     
        	// what if the new variable already has a value?
        	final Integer equalsWithIndex = parentCompiled.getPosMapping().get(outputVariable);
        	resultTrace = CompilerHelper.trimLastIfEqual(plan, enforcerTrace, equalsWithIndex);
        } 
        return resultTrace.cloneFor(plan);
    }


	private QueryPlanRecipeTraceInfo doCompileJoin(PJoin operation, SubPlan plan) {
		final List<QueryPlanRecipeTraceInfo> compiledParents = getCompiledFormOfParents(plan);
		final QueryPlanRecipeTraceInfo leftCompiled = compiledParents.get(0);
		final QueryPlanRecipeTraceInfo rightCompiled = compiledParents.get(1);
		
		return compileToNaturalJoin(plan, leftCompiled, rightCompiled);
	}

	private QueryPlanRecipeTraceInfo compileToNaturalJoin(SubPlan plan,
			final AuxiliaryPlanningRecipeTraceInfo leftCompiled,
			final AuxiliaryPlanningRecipeTraceInfo rightCompiled) {
		JoinHelper joinHelper = new JoinHelper(plan, leftCompiled, rightCompiled);
        return new QueryPlanRecipeTraceInfo(plan, 
        		joinHelper.getNaturalJoinVariablesTuple(), 
        		joinHelper.getNaturalJoinRecipe(), 
        		joinHelper.getPrimaryIndexer(), joinHelper.getSecondaryIndexer());
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
		
		final TrimmerRecipe trimmerRecipe = CompilerHelper.makeTrimmerRecipe(compiledParent, projectedVariables);
		
		if (BuildHelper.areAllVariablesDetermined(plan.getParentPlans().get(0), projectedVariables)) {
			// skip uniqueness enforcement if unneeded?
			return new QueryPlanRecipeTraceInfo(plan, projectedVariables, trimmerRecipe, compiledParent);
		} else {
			RecipeTraceInfo trimTrace = new AuxiliaryPlanningRecipeTraceInfo(plan, projectedVariables, trimmerRecipe, compiledParent);
			UniquenessEnforcerRecipe uniquenessEnforcerRecipe = FACTORY.createUniquenessEnforcerRecipe();
			uniquenessEnforcerRecipe.getParents().add(trimmerRecipe);			
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
		final AuxiliaryPlanningRecipeTraceInfo coreTrace = doEnumerateDispatch(plan, constraint);
		
		return new QueryPlanRecipeTraceInfo(plan, variables, coreTrace.getRecipe(), coreTrace.getParentRecipeTraces());
		
		TODO check variable coincidences
	}
	
	
	private AuxiliaryPlanningRecipeTraceInfo doEnumerateDispatch(SubPlan plan, EnumerablePConstraint constraint) {
        if (constraint instanceof BinaryTransitiveClosure) {
            return processConstraint(plan, (BinaryTransitiveClosure) constraint);
        } else if (constraint instanceof ConstantValue) {
            return processConstraint(plan, (ConstantValue) constraint);
//        } else if (constraint instanceof Containment) {
//            return processConstraint(plan, (Containment) constraint);
//        } else if (constraint instanceof Generalization) {
//            return processConstraint(plan, (Generalization) constraint);
//        } else if (constraint instanceof Instantiation) {
//            return processConstraint(plan, (Instantiation) constraint);
        } else if (constraint instanceof PositivePatternCall) {
            return processConstraint(plan, (PositivePatternCall) constraint);
        } else if (constraint instanceof TypeBinary) {
            return processConstraint(plan, (TypeBinary) constraint);
//        } else if (constraint instanceof TypeTernary) {
//            return processConstraint((TypeTernary) constraint);
        } else if (constraint instanceof TypeUnary) {
            return processConstraint(plan, (TypeUnary) constraint);
        }
        throw new UnsupportedOperationException("Unknown enumerable constraint " + constraint);
	}

	private AuxiliaryPlanningRecipeTraceInfo processConstraint(SubPlan plan,
			BinaryTransitiveClosure constraint) {
		final PQuery referredQuery = constraint.getSupplierKey();
		final AuxiliaryPlanningRecipeTraceInfo callTrace = referQuery(referredQuery);
		
		final TransitiveClosureRecipe recipe = FACTORY.createTransitiveClosureRecipe();
		recipe.setParent(callTrace.getRecipe());

		return new AuxiliaryPlanningRecipeTraceInfo(plan, CompilerHelper.convertVariablesTuple(constraint), recipe, callTrace);
	}

	private AuxiliaryPlanningRecipeTraceInfo processConstraint(SubPlan plan, PositivePatternCall constraint) {
		final PQuery referredQuery = constraint.getReferredQuery();
		return referQuery(referredQuery);
	}

	private AuxiliaryPlanningRecipeTraceInfo processConstraint(SubPlan plan, TypeBinary constraint) {
		final BinaryInputRecipe recipe = FACTORY.createBinaryInputRecipe();
		initTypeInputRecipe(constraint, recipe);
		return new AuxiliaryPlanningRecipeTraceInfo(plan, CompilerHelper.convertVariablesTuple(constraint), recipe);
	}
	private AuxiliaryPlanningRecipeTraceInfo processConstraint(SubPlan plan, TypeUnary constraint) {
		final UnaryInputRecipe recipe = FACTORY.createUnaryInputRecipe();
		initTypeInputRecipe(constraint, recipe);
		return new AuxiliaryPlanningRecipeTraceInfo(plan, CompilerHelper.convertVariablesTuple(constraint), recipe);
	}
	private void initTypeInputRecipe(TypeConstraint constraint, final TypeInputRecipe recipe) {
		recipe.setTypeKey(constraint.getSupplierKey());
		recipe.setTypeName(constraint.getTypeString());
	}
	
	private AuxiliaryPlanningRecipeTraceInfo processConstraint(SubPlan plan, ConstantValue constraint) {
		final ConstantRecipe recipe = FACTORY.createConstantRecipe();
		recipe.getConstantValues().add(constraint.getSupplierKey());
		return new AuxiliaryPlanningRecipeTraceInfo(plan, CompilerHelper.convertVariablesTuple(constraint), recipe);
	}
	
	
	private AuxiliaryPlanningRecipeTraceInfo referQuery(PQuery query) {
	}

	

	protected List<QueryPlanRecipeTraceInfo> getCompiledFormOfParents(SubPlan plan) {
		List<QueryPlanRecipeTraceInfo> results = new ArrayList<QueryPlanRecipeTraceInfo>();
		for (SubPlan parentPlan : plan.getParentPlans()) {
			results.add(getCompiledForm(parentPlan));
		}
		return results;
	}
	
	
}
