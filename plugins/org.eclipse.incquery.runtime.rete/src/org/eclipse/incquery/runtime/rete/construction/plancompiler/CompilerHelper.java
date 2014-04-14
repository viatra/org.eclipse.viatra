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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.incquery.runtime.matchers.planning.SubPlan;
import org.eclipse.incquery.runtime.matchers.psystem.EnumerablePConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.tuple.TupleMask;
import org.eclipse.incquery.runtime.rete.recipes.AggregatorRecipe;
import org.eclipse.incquery.runtime.rete.recipes.EqualityFilterRecipe;
import org.eclipse.incquery.runtime.rete.recipes.IndexerRecipe;
import org.eclipse.incquery.runtime.rete.recipes.JoinRecipe;
import org.eclipse.incquery.runtime.rete.recipes.ProjectionIndexerRecipe;
import org.eclipse.incquery.runtime.rete.recipes.RecipesFactory;
import org.eclipse.incquery.runtime.rete.recipes.ReteNodeRecipe;
import org.eclipse.incquery.runtime.rete.recipes.TrimmerRecipe;
import org.eclipse.incquery.runtime.rete.recipes.helper.RecipesHelper;
import org.eclipse.incquery.runtime.rete.traceability.AuxiliaryPlanningTrace;
import org.eclipse.incquery.runtime.rete.traceability.CompiledQueryPlan;
import org.eclipse.incquery.runtime.rete.traceability.RecipeTraceInfo;

/**
 * @author Bergmann Gabor
 *
 */
public class CompilerHelper {
	
	final static RecipesFactory FACTORY = RecipesFactory.eINSTANCE;
	
	
//    /**
//     * If two or more variables are the same in the variablesTuple of the subplan, then a checker node is built to enforce
//     * their equality.
//     * 
//     * @return the derived subplan that contains the additional checkers, or the original if no action was necessary.
//     */
//    public static SubPlan enforceVariableCoincidences(IOperationCompiler buildable, SubPlan plan) {
//        Map<Object, List<Integer>> indexWithMupliplicity = plan.getNaturalJoinVariablesTuple().invertIndexWithMupliplicity();
//        for (Map.Entry<Object, List<Integer>> pVariableIndices : indexWithMupliplicity.entrySet()) {
//            List<Integer> indices = pVariableIndices.getValue();
//            if (indices.size() > 1) {
//                int[] indexArray = new int[indices.size()];
//                int m = 0;
//                for (Integer index : indices)
//                    indexArray[m++] = index;
//                plan = buildable.buildEqualityChecker(plan, indexArray);
//                // TODO also trim here?
//            }
//        }
//        return plan;
//
//    }
//
//    /**
//     * Trims the results in the subplan by selecting exported variables in a particular order.
//     * 
//     * @return the derived subplan.
//     * @param enforceUniqueness if true, uniqueness after projection will be enforced
//     */
//	public static SubPlan project(
//			IOperationCompiler buildable,
//			SubPlan plan, PVariable[] selectedVariables,
//			boolean enforceUniqueness) {
//		int paramNum = selectedVariables.length;
//        int[] tI = new int[paramNum];
//        for (int i = 0; i < paramNum; i++) {
//            tI[i] = plan.getVariablesIndex().get(selectedVariables[i]);
//        }
//        int tiW = plan.getNaturalJoinVariablesTuple().getSize();
//        TupleMask trim = new TupleMask(tI, tiW);
//        SubPlan trimmer = buildable.buildTrimmer(plan, trim, enforceUniqueness);
//		return trimmer;
//	}

//	/**
//	 * Make sure last tuple element equals with the element at the given parameter, then trim it away.
//	 */
//	public static AuxiliaryPlanningRecipeTraceInfo trimLastIfEqual(
//			SubPlan plan,
//			AuxiliaryPlanningRecipeTraceInfo enforcerTrace,
//			final Integer lastEqualsWithIndex) 
//	{
//		final int coreVariablesSize = enforcerTrace.getVariablesTuple().size()-1;
//
//		EqualityFilterRecipe equalityFilterRecipe = FACTORY.createEqualityFilterRecipe();
//		equalityFilterRecipe.setParent(enforcerTrace.getRecipe());
//		equalityFilterRecipe.getIndices().add(lastEqualsWithIndex);
//		equalityFilterRecipe.getIndices().add(coreVariablesSize /*index of newly added copy*/);
//		final AuxiliaryPlanningRecipeTraceInfo equalityTrace = 
//				new AuxiliaryPlanningRecipeTraceInfo(plan, 
//						enforcerTrace.getVariablesTuple(), equalityFilterRecipe, enforcerTrace);
//		
//		TrimmerRecipe trimmerRecipe = FACTORY.createTrimmerRecipe();
//		trimmerRecipe.setParent(equalityFilterRecipe);
//		final TupleMask mask = TupleMask.omit(coreVariablesSize /*omit last*/, coreVariablesSize+1);
//		trimmerRecipe.setMask(RecipesHelper.mask(mask.sourceWidth, mask.indices));
//		return new AuxiliaryPlanningRecipeTraceInfo(plan, 
//				mask.transform(enforcerTrace.getVariablesTuple()), 
//				trimmerRecipe, equalityTrace);
//	}
	
	/**
	 * Makes sure that all variables in the tuple are different so that it can be used as {@link CompiledQueryPlan}.
	 * If a variable occurs multiple times, equality checks are applied and then the results are trimmed so that duplicates are hidden.
	 * If no manipulation is necessary, the original trace is returned.  
	 * 
	 * <p> to be used whenever a constraint introduces new variables.
	 */
	public static AuxiliaryPlanningTrace checkAndTrimEqualVariables(
			SubPlan plan, final AuxiliaryPlanningTrace coreTrace) 
	{
		// are variables in the constraint all different?
		final List<PVariable> coreVariablesTuple = coreTrace.getVariablesTuple();
		final int constraintArity = coreVariablesTuple.size();
		final int distinctVariables = coreTrace.getPosMapping().size();
		if (constraintArity == distinctVariables) {
			// all variables occur exactly once in tuple
			return coreTrace;
		} else { // apply equality checks and trim
			
			// find the positions in the tuple for each variable
			Map<PVariable, SortedSet<Integer>> posMultimap = new HashMap<PVariable, SortedSet<Integer>>();
			List<PVariable> trimmedVariablesTuple = new ArrayList<PVariable>(distinctVariables);
			int[] trimIndices = new int[distinctVariables]; 
			for (int i = 0; i < constraintArity; ++i) {
				final PVariable variable = coreVariablesTuple.get(i);
				SortedSet<Integer> indexSet = posMultimap.get(variable);
				if (indexSet == null) { // first occurrence of variable
					indexSet = new TreeSet<Integer>();
					posMultimap.put(variable, indexSet);
					
					// this is the first occurrence, set up trimming
					trimIndices[trimmedVariablesTuple.size()] = i;
					trimmedVariablesTuple.add(variable);
				}
				indexSet.add(i);
			}

			// construct equality checks for each variable occurring multiple times
			AuxiliaryPlanningTrace lastTrace = coreTrace;
			for (Entry<PVariable, SortedSet<Integer>> entry : posMultimap.entrySet()) {
				if (entry.getValue().size() > 1) {
					EqualityFilterRecipe equalityFilterRecipe = FACTORY.createEqualityFilterRecipe();
					equalityFilterRecipe.setParent(lastTrace.getRecipe());
					equalityFilterRecipe.getIndices().addAll(entry.getValue());
					lastTrace = new AuxiliaryPlanningTrace(plan, 
							coreVariablesTuple, equalityFilterRecipe, lastTrace);
				}
			}
				
			// trim so that each variable occurs only once
			TrimmerRecipe trimmerRecipe = FACTORY.createTrimmerRecipe();
			trimmerRecipe.setParent(lastTrace.getRecipe());
			trimmerRecipe.setMask(RecipesHelper.mask(constraintArity, trimIndices));
			return new AuxiliaryPlanningTrace(plan, 
					trimmedVariablesTuple, trimmerRecipe, lastTrace);
		}
	}
	
	
	/**
	 * Extracts the variable list representation of the variables tuple. 
	 */
	public static List<PVariable> convertVariablesTuple(EnumerablePConstraint constraint) {
		List<PVariable> result = new ArrayList<PVariable>();
		for (Object o : constraint.getVariablesTuple().getElements())
			result.add((PVariable) o);
		return result;
	}
	
	
	/**
	 * Returns a compiled indexer trace according to a mask 
	 */
    public static RecipeTraceInfo getIndexerRecipe(SubPlan planToCompile, AuxiliaryPlanningTrace parentTrace, TupleMask mask) {
		final ReteNodeRecipe parentRecipe = parentTrace.getRecipe();
		if (parentRecipe instanceof AggregatorRecipe) 
			throw new IllegalArgumentException("Cannot take projection indexer of aggregator node at plan " + planToCompile);
		IndexerRecipe recipe = RecipesHelper.projectionIndexerRecipe(parentRecipe, 
				RecipesHelper.mask(mask.sourceWidth, mask.indices));
		// final List<PVariable> maskedVariables = mask.transform(parentTrace.getVariablesTuple());
		return new AuxiliaryPlanningTrace(planToCompile, 
				/*maskedVariables*/ parentTrace.getVariablesTuple(), recipe, parentTrace);
		// TODO add specialized indexer trace info?
    }
    
    /**
     * Calculated index mappings for a join, based on the common variables of the two parent subplans.
     * 
     * @author Gabor Bergmann
     * 
     */
    public static class JoinHelper {
        private TupleMask primaryMask;
        private TupleMask secondaryMask;
        private TupleMask complementerMask;
		private RecipeTraceInfo primaryIndexer;
		private RecipeTraceInfo secondaryIndexer;
		private JoinRecipe naturalJoinRecipe;
		private List<PVariable> naturalJoinVariablesTuple;

        /**
         * @pre enforceVariableCoincidences() has been called on both sides.
         */
        public JoinHelper(SubPlan planToCompile, 
        		AuxiliaryPlanningTrace primaryCompiled, 
        		AuxiliaryPlanningTrace callTrace) 
        {
            super();

            Set<PVariable> primaryVariables = new HashSet<PVariable>(primaryCompiled.getVariablesTuple());
            Set<PVariable> secondaryVariables = new HashSet<PVariable>(callTrace.getVariablesTuple());
            int oldNodes = 0;
            Set<Integer> introducingSecondaryIndices = new TreeSet<Integer>();
            for (PVariable var : secondaryVariables) {
                if (primaryVariables.contains(var))
                    oldNodes++;
                else
                    introducingSecondaryIndices.add(callTrace.getPosMapping().get(var));
            }
            int[] primaryIndices = new int[oldNodes];
            final int[] secondaryIndices = new int[oldNodes];
            int k = 0;
            for (PVariable var : secondaryVariables) {
                if (primaryVariables.contains(var)) {
                    primaryIndices[k] = primaryCompiled.getPosMapping().get(var);
                    secondaryIndices[k] = callTrace.getPosMapping().get(var);
                    k++;
                }
            }
            int[] complementerIndices = new int[introducingSecondaryIndices.size()];
            int l = 0;
            for (Integer integer : introducingSecondaryIndices) {
                complementerIndices[l++] = integer;
            }
            primaryMask = new TupleMask(primaryIndices, primaryCompiled.getVariablesTuple().size());
            secondaryMask = new TupleMask(secondaryIndices, callTrace.getVariablesTuple().size());
            complementerMask = new TupleMask(complementerIndices, callTrace.getVariablesTuple().size());
            
        	primaryIndexer = getIndexerRecipe(planToCompile, primaryCompiled, primaryMask);
        	secondaryIndexer = getIndexerRecipe(planToCompile, callTrace, secondaryMask);
        	
        	naturalJoinRecipe = FACTORY.createJoinRecipe();
        	naturalJoinRecipe.setLeftParent((ProjectionIndexerRecipe) primaryIndexer.getRecipe());
        	naturalJoinRecipe.setRightParent((IndexerRecipe) secondaryIndexer.getRecipe());
    		naturalJoinRecipe.setRightParentComplementaryMask(RecipesHelper.mask(complementerMask.sourceWidth, complementerMask.indices));
        	
            naturalJoinVariablesTuple = new ArrayList<PVariable>(primaryCompiled.getVariablesTuple());
            for (int complementerIndex : complementerMask.indices)
            	naturalJoinVariablesTuple.add(callTrace.getVariablesTuple().get(complementerIndex));
        }
        

        public TupleMask getPrimaryMask() {
            return primaryMask;
        }

        public TupleMask getSecondaryMask() {
            return secondaryMask;
        }

        public TupleMask getComplementerMask() {
            return complementerMask;
        }


		public RecipeTraceInfo getPrimaryIndexer() {
			return primaryIndexer;
		}


		public RecipeTraceInfo getSecondaryIndexer() {
			return secondaryIndexer;
		}


		public JoinRecipe getNaturalJoinRecipe() {
			return naturalJoinRecipe;
		}


		public List<PVariable> getNaturalJoinVariablesTuple() {
			return naturalJoinVariablesTuple;
		}
        
    }

	protected static TrimmerRecipe makeTrimmerRecipe(
			final AuxiliaryPlanningTrace compiledParent,
			List<PVariable> projectedVariables) {
		final ReteNodeRecipe parentRecipe = compiledParent.getRecipe();
		List<Integer> projectionSourceIndices = new ArrayList<Integer>();
		for (PVariable pVariable : projectedVariables) {
			projectionSourceIndices.add(compiledParent.getPosMapping().get(pVariable));
		}
		final TrimmerRecipe trimmerRecipe = RecipePlanCompiler.FACTORY.createTrimmerRecipe();
		trimmerRecipe.setParent(parentRecipe);
		trimmerRecipe.setMask(RecipesHelper.mask(parentRecipe.getArity(), projectionSourceIndices));
		return trimmerRecipe;
	}    
    
}
