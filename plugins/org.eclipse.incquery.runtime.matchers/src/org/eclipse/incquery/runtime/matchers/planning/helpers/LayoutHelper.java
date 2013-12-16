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

import java.util.Collections;
import java.util.Set;

import org.eclipse.incquery.runtime.matchers.IPatternMatcherContext;
import org.eclipse.incquery.runtime.matchers.planning.QueryPlannerException;
import org.eclipse.incquery.runtime.matchers.planning.SubPlan;
import org.eclipse.incquery.runtime.matchers.psystem.ITypeInfoProviderConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PSystem;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Inequality;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;

/**
 * @author Gabor Bergmann
 * 
 */
public class LayoutHelper {

    /**
     * Unifies allVariables along equalities so that they can be handled as one.
     * 
     * @param pSystem
     */
	public static void unifyVariablesAlongEqualities(PSystem pSystem) {
        Set<Equality> equals = pSystem.getConstraintsOfType(Equality.class);
        for (Equality equality : equals) {
            if (!equality.isMoot()) {
                equality.getWho().unifyInto(equality.getWithWhom());
            }
            // equality.delete();
        }
    }

    /**
     * Eliminates weak inequalities if they are not substantiated.
     * 
     * @param pSystem
     */
	public static void eliminateWeakInequalities(PSystem pSystem) {
        for (Inequality inequality : pSystem.getConstraintsOfType(Inequality.class))
            inequality.eliminateWeak();
    }

    /**
     * Eliminates all unary type constraints that are inferrable from other constraints.
     */
	public static void eliminateInferrableUnaryTypes(final PSystem pSystem,
			IPatternMatcherContext context) {
        Set<TypeUnary> constraintsOfType = pSystem.getConstraintsOfType(TypeUnary.class);
        for (TypeUnary typeUnary : constraintsOfType) {
            PVariable var = (PVariable) typeUnary.getVariablesTuple().get(0);
            Object expressedType = typeUnary.getTypeInfo(var);
            Set<ITypeInfoProviderConstraint> typeRestrictors = var
                    .getReferringConstraintsOfType(ITypeInfoProviderConstraint.class);
            typeRestrictors.remove(typeUnary);
            for (ITypeInfoProviderConstraint iTypeRestriction : typeRestrictors) {
                Object typeInfo = iTypeRestriction.getTypeInfo(var);
                if (typeInfo != ITypeInfoProviderConstraint.TypeInfoSpecials.NO_TYPE_INFO_PROVIDED) {
                    Set<Object> typeClosure = TypeHelper.typeClosure(Collections.singleton(typeInfo), context);
                    if (typeClosure.contains(expressedType)) {
                        typeUnary.delete();
                        break;
                    }
                }
            }
        }
    }

    /**
     * Verifies the sanity of all constraints. Should be issued as a preventive check before layouting.
     * 
     * @param pSystem
     * @throws RetePatternBuildException
     */
	public static void checkSanity(PSystem pSystem)
			throws QueryPlannerException {
        for (PConstraint pConstraint : pSystem.getConstraints())
            pConstraint.checkSanity();
    }

    /**
     * Finds an arbitrary constraint that is not enforced at the given plan.
     * 
     * @param pSystem
     * @param plan
     * @return a PConstraint that is not enforced, if any, or null if all are enforced
     */
	public static PConstraint getAnyUnenforcedConstraint(PSystem pSystem,
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
	public static void finalCheck(final PSystem pSystem, SubPlan plan)
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
            if (!export.isReadyAt(plan)) {
                throw new QueryPlannerException(
                        "Exported pattern parameter {1} could not be deduced during pattern matcher construction."
                                + " A pattern constraint is required to positively deduce its value.",
                        new String[] { export.getParameterName().toString() }, "Could not calculate pattern parameter",
                        null);
            }
        }
    }

}
