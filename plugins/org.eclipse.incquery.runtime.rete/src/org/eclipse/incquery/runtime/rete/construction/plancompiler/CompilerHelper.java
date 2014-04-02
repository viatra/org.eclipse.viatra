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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.incquery.runtime.matchers.planning.IOperationCompiler;
import org.eclipse.incquery.runtime.matchers.planning.SubPlan;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.tuple.TupleMask;

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
     * Calculated index mappings for a join, based on the common variables of the two parent subplans.
     * 
     * @author Gabor Bergmann
     * 
     */
    public static class JoinHelper {
        private TupleMask primaryMask;
        private TupleMask secondaryMask;
        private TupleMask complementerMask;

        /**
         * @pre enforceVariableCoincidences() has been called on both sides.
         */
        public JoinHelper(SubPlan primaryPlan, SubPlan secondaryPlan) {
            super();

            Set<PVariable> primaryVariables = primaryPlan.getVariablesTuple().getDistinctElements();
            Set<PVariable> secondaryVariables = secondaryPlan.getVariablesTuple().getDistinctElements();
            int oldNodes = 0;
            Set<Integer> introducingSecondaryIndices = new TreeSet<Integer>();
            for (PVariable var : secondaryVariables) {
                if (primaryVariables.contains(var))
                    oldNodes++;
                else
                    introducingSecondaryIndices.add(secondaryPlan.getVariablesIndex().get(var));
            }
            int[] primaryIndices = new int[oldNodes];
            final int[] secondaryIndices = new int[oldNodes];
            int k = 0;
            for (PVariable var : secondaryVariables) {
                if (primaryVariables.contains(var)) {
                    primaryIndices[k] = primaryPlan.getVariablesIndex().get(var);
                    secondaryIndices[k] = secondaryPlan.getVariablesIndex().get(var);
                    k++;
                }
            }
            int[] complementerIndices = new int[introducingSecondaryIndices.size()];
            int l = 0;
            for (Integer integer : introducingSecondaryIndices) {
                complementerIndices[l++] = integer;
            }
            primaryMask = new TupleMask(primaryIndices, primaryPlan.getVariablesTuple().getSize());
            secondaryMask = new TupleMask(secondaryIndices, secondaryPlan.getVariablesTuple().getSize());
            complementerMask = new TupleMask(complementerIndices, secondaryPlan.getVariablesTuple().getSize());

        }

        /**
         * @return the primaryMask
         */
        public TupleMask getPrimaryMask() {
            return primaryMask;
        }

        /**
         * @return the secondaryMask
         */
        public TupleMask getSecondaryMask() {
            return secondaryMask;
        }

        /**
         * @return the complementerMask
         */
        public TupleMask getComplementerMask() {
            return complementerMask;
        }

    }
    
    
}
