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

package org.eclipse.viatra.query.runtime.matchers.planning.helpers;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.runtime.matchers.planning.SubPlan;
import org.eclipse.viatra.query.runtime.matchers.planning.SubPlanFactory;
import org.eclipse.viatra.query.runtime.matchers.planning.operations.PProject;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter;

/**
 * @author Gabor Bergmann
 * 
 */
public class BuildHelper {



//    public static SubPlan naturalJoin(IOperationCompiler buildable,
//            SubPlan primaryPlan, SubPlan secondaryPlan) {
//        JoinHelper joinHelper = new JoinHelper(primaryPlan, secondaryPlan);
//        return buildable.buildBetaNode(primaryPlan, secondaryPlan, joinHelper.getPrimaryMask(),
//                joinHelper.getSecondaryMask(), joinHelper.getComplementerMask(), false);
//    }
    
    
    /**
     * Reduces the number of tuples by trimming (existentially quantifying) the set of variables that <ul>
     * <li> are visible in the subplan, 
     * <li> are not exported parameters, 
     * <li> have all their constraints already enforced in the subplan,
     * </ul> and thus will not be needed anymore.
     * 
     * @param onlyIfNotDetermined if true, no trimming performed unless there is at least one such variable  
     * @return the plan after the trimming (possibly the original)
     */
    public static SubPlan trimUnneccessaryVariables(SubPlanFactory planFactory, /*IOperationCompiler buildable,*/
            SubPlan plan, boolean onlyIfNotDetermined, IQueryMetaContext context) {
    	Set<PVariable> canBeTrimmed = new HashSet<PVariable>();
    	Set<PVariable> variablesInPlan = plan.getVisibleVariables();
    	for (PVariable trimCandidate : variablesInPlan) {
    		if (trimCandidate.getReferringConstraintsOfType(ExportedParameter.class).isEmpty()) {
    			if (plan.getAllEnforcedConstraints().containsAll(trimCandidate.getReferringConstraints()))
    				canBeTrimmed.add(trimCandidate);
    		}
    	}
		final Set<PVariable> retainedVars = setMinus(variablesInPlan, canBeTrimmed);   	
    	if (!canBeTrimmed.isEmpty() && !(onlyIfNotDetermined && areVariablesDetermined(plan, retainedVars, canBeTrimmed, context))) {
    		// TODO add smart ordering? 
    		plan = planFactory.createSubPlan(new PProject(retainedVars), plan);
    	}
    	return plan;
    }
    
    
    /**
     * @return true iff a set of given variables functionally determine all visible variables in the subplan according to the subplan's constraints
     */
    public static boolean areAllVariablesDetermined(SubPlan plan, Collection<PVariable> determining, IQueryMetaContext context) {
		return areVariablesDetermined(plan, determining, plan.getVisibleVariables(), context);
	}
    
    /**
     * @return true iff one set of given variables functionally determine the other set according to the subplan's constraints
     */
    public static boolean areVariablesDetermined(SubPlan plan, Collection<PVariable> determining, Collection<PVariable> determined, IQueryMetaContext context) {
        Map<Set<PVariable>, Set<PVariable>> dependencies = new HashMap<Set<PVariable>, Set<PVariable>>();
        for (PConstraint pConstraint : plan.getAllEnforcedConstraints())
            dependencies.putAll(pConstraint.getFunctionalDependencies(context));
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
     * @throws QueryProcessingException
     */
    public static void finalCheck(final PBody pSystem, SubPlan plan, IQueryMetaContext context)
            throws QueryProcessingException {
        PConstraint unenforcedConstraint = getAnyUnenforcedConstraint(pSystem, plan);
        if (unenforcedConstraint != null) {
            throw new QueryProcessingException(
                    "Pattern matcher construction terminated without successfully enforcing constraint {1}."
                            + " Could be caused if the value of some variables can not be deduced, e.g. by circularity of pattern constraints.",
                    new String[] { unenforcedConstraint.toString() }, "Could not enforce a pattern constraint", null);
        }
        for (ExportedParameter export : pSystem
                .getConstraintsOfType(ExportedParameter.class)) {
            if (!export.isReadyAt(plan, context)) {
                throw new QueryProcessingException(
                        "Exported pattern parameter {1} could not be deduced during pattern matcher construction."
                                + " A pattern constraint is required to positively deduce its value.",
                        new String[] { export.getParameterName().toString() }, "Could not calculate pattern parameter",
                        null);
            }
        }
    }    

}
