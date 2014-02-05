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

package org.eclipse.incquery.runtime.matchers.psystem;

import java.util.Collections;
import java.util.Set;

import org.eclipse.incquery.runtime.matchers.IPatternMatcherContext;
import org.eclipse.incquery.runtime.matchers.planning.QueryPlannerException;
import org.eclipse.incquery.runtime.matchers.planning.helpers.TypeHelper;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Inequality;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;

/**
 * Helper class for creating a normalized version of a PSystem, unifying variables and running basic sanity checks.
 *
 * @author Gabor Bergmann
 *
 */
public class PBodyNormalizer {

    /**
     * If set to true, shrinks the net by avoiding unnecessary typechecks
     */
    public static final boolean calcImpliedTypes = true;

    /**
     * Provides a normalized version of the pattern body. May return a different version than the original version if
     * needed.
     *
     * @param body
     * @param context
     * @return
     * @throws QueryPlannerException
     */
    public static PBody normalizeBody(PBody body, IPatternMatcherContext context) throws QueryPlannerException {
        // UNIFICATION AND WEAK INEQUALITY ELMINATION
        unifyVariablesAlongEqualities(body);
        eliminateWeakInequalities(body);


        // UNARY ELIMINATION WITH TYPE INFERENCE
        if (calcImpliedTypes) {
            eliminateInferrableUnaryTypes(body, context);
        }
        removeMootEqualities(body);
        // PREVENTIVE CHECKS
        checkSanity(body);
        return body;
    }

    private static void removeMootEqualities(PBody body) {
        Set<Equality> equals = body.getConstraintsOfType(Equality.class);
        for (Equality equality : equals) {
            if (equality.isMoot()) {
                equality.delete();
            }
        }
    }

    /**
     * Unifies allVariables along equalities so that they can be handled as one.
     *
     * @param body
     */
    public static void unifyVariablesAlongEqualities(PBody body) {
        Set<Equality> equals = body.getConstraintsOfType(Equality.class);
        for (Equality equality : equals) {
            if (!equality.isMoot()) {
                equality.getWho().unifyInto(equality.getWithWhom());
            }
        }
    }

    /**
     * Eliminates weak inequalities if they are not substantiated.
     *
     * @param body
     */
    public static void eliminateWeakInequalities(PBody body) {
        for (Inequality inequality : body.getConstraintsOfType(Inequality.class))
            inequality.eliminateWeak();
    }

    /**
     * Eliminates all unary type constraints that are inferrable from other constraints.
     */
    public static void eliminateInferrableUnaryTypes(final PBody body, IPatternMatcherContext context) {
        Set<TypeUnary> constraintsOfType = body.getConstraintsOfType(TypeUnary.class);
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
     * @param body
     * @throws RetePatternBuildException
     */
    public static void checkSanity(PBody body) throws QueryPlannerException {
        for (PConstraint pConstraint : body.getConstraints())
            pConstraint.checkSanity();
    }

}
