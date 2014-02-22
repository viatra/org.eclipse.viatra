/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.matchers.planning.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.incquery.runtime.matchers.IPatternMatcherContext;
import org.eclipse.incquery.runtime.matchers.planning.IOperationCompiler;
import org.eclipse.incquery.runtime.matchers.planning.QueryPlannerException;
import org.eclipse.incquery.runtime.matchers.planning.SubPlan;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.tuple.TupleMask;

/**
 * @author Gabor Bergmann
 * 
 */
public class BuildHelper {

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
     * Trims the results in the subplan into a collector, by selecting exported variables in a particular order.
     */
    public static void projectIntoCollector(IOperationCompiler buildable,
            SubPlan plan, Collector collector, PVariable[] selectedVariables) {
        SubPlan trimmer = project(buildable, plan, selectedVariables, false);
        buildable.buildConnection(trimmer, collector);
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

    public static SubPlan naturalJoin(IOperationCompiler buildable,
            SubPlan primaryPlan, SubPlan secondaryPlan) {
        JoinHelper joinHelper = new JoinHelper(primaryPlan, secondaryPlan);
        return buildable.buildBetaNode(primaryPlan, secondaryPlan, joinHelper.getPrimaryMask(),
                joinHelper.getSecondaryMask(), joinHelper.getComplementerMask(), false);
    }
    
    
    /**
     * Reduces the number of tuples by trimming (existentially quantifying) the set of variables that <ul>
     * <li> are in the tuple, 
     * <li> are not exported parameters, 
     * <li> have all their constraints already enforced,
     * </ul> and thus will not be needed anymore.
     * 
     * @param onlyIfNotDetermined if true, no trimming performed unless there is at least one such variable  
     * @return the plan after the trimming (possibly the original)
     */
    public static SubPlan trimUnneccessaryVariables(IOperationCompiler buildable,
            SubPlan plan, boolean onlyIfNotDetermined) {
    	Set<PVariable> canBeTrimmed = new HashSet<PVariable>();
    	Set<PVariable> variablesInTuple = plan.getVariablesTuple().getDistinctElements();
    	for (PVariable trimCandidate : variablesInTuple) {
    		if (trimCandidate.getReferringConstraintsOfType(ExportedParameter.class).isEmpty()) {
    			if (plan.getAllEnforcedConstraints().containsAll(trimCandidate.getReferringConstraints()))
    				canBeTrimmed.add(trimCandidate);
    		}
    	}
		final Set<PVariable> retainedVars = setMinus(variablesInTuple, canBeTrimmed);   	
    	if (!canBeTrimmed.isEmpty() && !(onlyIfNotDetermined && areVariablesDetermined(plan, retainedVars, canBeTrimmed))) {
    		// TODO add ordering? 
    		final PVariable[] selectedVariablesArray = new ArrayList<PVariable>(retainedVars).toArray(new PVariable[retainedVars.size()]);
    		plan = project(buildable, plan, selectedVariablesArray, true);
    	}
    	return plan;
    }
    
    
    /**
     * @return true iff one set of given variables functionally determine the other set according to the subplan's constraints
     */
    public static boolean areVariablesDetermined(SubPlan plan, Set<PVariable> determining, Set<PVariable> determined) {
        Map<Set<PVariable>, Set<PVariable>> dependencies = new HashMap<Set<PVariable>, Set<PVariable>>();
        for (PConstraint pConstraint : plan.getAllEnforcedConstraints())
            dependencies.putAll(pConstraint.getFunctionalDependencies());
		final Set<PVariable> closure = FunctionalDependencyHelper.closureOf(determining, dependencies);
		final boolean isDetermined = closure.containsAll(determined);
		return isDetermined;
	}

	private static <T> Set<T> setMinus(Set<T> a, Set<T> b) {
		Set<T> difference = new HashSet<T>(a);
		difference.removeAll(b);
		return difference;
	}
    
    /**
     * Finds an arbitrary constraint that is not enforced at the given plan.
     * 
     * @param pSystem
     * @param plan
     * @return a PConstraint that is not enforced, if any, or null if all are enforced
     */
    public static PConstraint getAnyUnenforcedConstraint(PBody pSystem,
            SubPlan plan) {
        Set<PConstraint> allEnforcedConstraints = plan.getAllEnforcedConstraints();
        Set<PConstraint> constraints = pSystem.getConstraints();
        for (PConstraint pConstraint : constraints) {
            if (!allEnforcedConstraints.contains(pConstraint))
                return pConstraint;
        }
        return null;
    }

    /**
     * Verifies whether all constraints are enforced and exported parameters are present.
     * 
     * @param pSystem
     * @param plan
     * @throws RetePatternBuildException
     */
    public static void finalCheck(final PBody pSystem, SubPlan plan, IPatternMatcherContext context)
            throws QueryPlannerException {
        PConstraint unenforcedConstraint = getAnyUnenforcedConstraint(pSystem, plan);
        if (unenforcedConstraint != null) {
            throw new QueryPlannerException(
                    "Pattern matcher construction terminated without successfully enforcing constraint {1}."
                            + " Could be caused if the value of some variables can not be deduced, e.g. by circularity of pattern constraints.",
                    new String[] { unenforcedConstraint.toString() }, "Could not enforce a pattern constraint", null);
        }
        for (ExportedParameter export : pSystem
                .getConstraintsOfType(ExportedParameter.class)) {
            if (!export.isReadyAt(plan, context)) {
                throw new QueryPlannerException(
                        "Exported pattern parameter {1} could not be deduced during pattern matcher construction."
                                + " A pattern constraint is required to positively deduce its value.",
                        new String[] { export.getParameterName().toString() }, "Could not calculate pattern parameter",
                        null);
            }
        }
    }    

}
