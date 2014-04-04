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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.incquery.runtime.matchers.planning.SubPlan;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.tuple.TupleMask;
import org.eclipse.incquery.runtime.rete.recipes.IndexerRecipe;
import org.eclipse.incquery.runtime.rete.recipes.JoinRecipe;
import org.eclipse.incquery.runtime.rete.recipes.ProjectionIndexerRecipe;
import org.eclipse.incquery.runtime.rete.recipes.RecipesFactory;
import org.eclipse.incquery.runtime.rete.recipes.helper.RecipesHelper;
import org.eclipse.incquery.runtime.rete.traceability.AuxiliaryPlanningRecipeTraceInfo;
import org.eclipse.incquery.runtime.rete.traceability.QueryPlanRecipeTraceInfo;
import org.eclipse.incquery.runtime.rete.traceability.RecipeTraceInfo;

/**
 * @author Bergmann Gabor
 *
 */
public class CompilerHelper {
    /**
     * If two or more variables are the same in the variablesTuple of the subplan, then a checker node is built to enforce
     * their equality.
     * 
     * @return the derived subplan that contains the additional checkers, or the original if no action was necessary.
     */
    public static SubPlan enforceVariableCoincidences(IOperationCompiler buildable, SubPlan plan) {
        Map<Object, List<Integer>> indexWithMupliplicity = plan.getVariablesTuple().invertIndexWithMupliplicity();
        for (Map.Entry<Object, List<Integer>> pVariableIndices : indexWithMupliplicity.entrySet()) {
            List<Integer> indices = pVariableIndices.getValue();
            if (indices.size() > 1) {
                int[] indexArray = new int[indices.size()];
                int m = 0;
                for (Integer index : indices)
                    indexArray[m++] = index;
                plan = buildable.buildEqualityChecker(plan, indexArray);
                // TODO also trim here?
            }
        }
        return plan;

    }

    /**
     * Trims the results in the subplan by selecting exported variables in a particular order.
     * 
     * @return the derived subplan.
     * @param enforceUniqueness if true, uniqueness after projection will be enforced
     */
	public static SubPlan project(
			IOperationCompiler buildable,
			SubPlan plan, PVariable[] selectedVariables,
			boolean enforceUniqueness) {
		int paramNum = selectedVariables.length;
        int[] tI = new int[paramNum];
        for (int i = 0; i < paramNum; i++) {
            tI[i] = plan.getVariablesIndex().get(selectedVariables[i]);
        }
        int tiW = plan.getVariablesTuple().getSize();
        TupleMask trim = new TupleMask(tI, tiW);
        SubPlan trimmer = buildable.buildTrimmer(plan, trim, enforceUniqueness);
		return trimmer;
	}
	
	/**
	 * Returns a compiled indexer trace according to a mask 
	 */
    public static RecipeTraceInfo getIndexerRecipe(SubPlan planToCompile, QueryPlanRecipeTraceInfo parentTrace, TupleMask mask) {
		final ProjectionIndexerRecipe recipe = RecipesHelper.projectionIndexerRecipe(
    			parentTrace.getRecipe(), 
    			RecipesHelper.mask(mask.sourceWidth, mask.indices)
    	);
		return new AuxiliaryPlanningRecipeTraceInfo(planToCompile, recipe, parentTrace);
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
		private JoinRecipe joinRecipe;
		private List<PVariable> variablesTuple;

        /**
         * @pre enforceVariableCoincidences() has been called on both sides.
         */
        public JoinHelper(SubPlan planToCompile, QueryPlanRecipeTraceInfo primaryCompiled, QueryPlanRecipeTraceInfo secondaryCompiled) {
            super();

            Set<PVariable> primaryVariables = new HashSet<PVariable>(primaryCompiled.getVariablesTuple());
            Set<PVariable> secondaryVariables = new HashSet<PVariable>(secondaryCompiled.getVariablesTuple());
            int oldNodes = 0;
            Set<Integer> introducingSecondaryIndices = new TreeSet<Integer>();
            for (PVariable var : secondaryVariables) {
                if (primaryVariables.contains(var))
                    oldNodes++;
                else
                    introducingSecondaryIndices.add(secondaryCompiled.getPosMapping().get(var));
            }
            int[] primaryIndices = new int[oldNodes];
            final int[] secondaryIndices = new int[oldNodes];
            int k = 0;
            for (PVariable var : secondaryVariables) {
                if (primaryVariables.contains(var)) {
                    primaryIndices[k] = primaryCompiled.getPosMapping().get(var);
                    secondaryIndices[k] = secondaryCompiled.getPosMapping().get(var);
                    k++;
                }
            }
            int[] complementerIndices = new int[introducingSecondaryIndices.size()];
            int l = 0;
            for (Integer integer : introducingSecondaryIndices) {
                complementerIndices[l++] = integer;
            }
            primaryMask = new TupleMask(primaryIndices, primaryCompiled.getVariablesTuple().size());
            secondaryMask = new TupleMask(secondaryIndices, secondaryCompiled.getVariablesTuple().size());
            complementerMask = new TupleMask(complementerIndices, secondaryCompiled.getVariablesTuple().size());
            
        	primaryIndexer = getIndexerRecipe(planToCompile, primaryCompiled, primaryMask);
        	secondaryIndexer = getIndexerRecipe(planToCompile, secondaryCompiled, secondaryMask);
        	
        	joinRecipe = RecipesFactory.eINSTANCE.createJoinRecipe();
        	joinRecipe.setLeftParent((ProjectionIndexerRecipe) primaryIndexer.getRecipe());
        	joinRecipe.setRightParent((IndexerRecipe) secondaryIndexer.getRecipe());
    		joinRecipe.setRightParentComplementaryMask(RecipesHelper.mask(complementerMask.sourceWidth, complementerMask.indices));
        	
            variablesTuple = new ArrayList<PVariable>(primaryCompiled.getVariablesTuple());
            for (int complementerIndex : complementerMask.indices)
            	variablesTuple.add(secondaryCompiled.getVariablesTuple().get(complementerIndex));
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


		public JoinRecipe getJoinRecipe() {
			return joinRecipe;
		}


		public List<PVariable> getVariablesTuple() {
			return variablesTuple;
		}
        
    }
    
    
}
