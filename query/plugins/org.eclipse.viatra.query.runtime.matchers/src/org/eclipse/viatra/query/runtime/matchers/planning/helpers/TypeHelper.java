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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.viatra.query.runtime.matchers.psystem.ITypeInfoProviderConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.TypeJudgement;

/**
 * @author Gabor Bergmann
 * 
 */
public class TypeHelper {

    /**
     * Infers unary type information for variables, based on the given constraints. 
     * 
     * Subsumptions are not taken into account.
     * 
     * @param constraints
     *            the set of constraints to extract type info from
     */
    public static Map<PVariable, Set<TypeJudgement>> inferUnaryTypes(Set<PConstraint> constraints, IQueryMetaContext context) {
    	Set<TypeJudgement> equivalentJudgements = getDirectJudgements(constraints, context);
    	Set<TypeJudgement> impliedJudgements = typeClosure(equivalentJudgements, context);
    			
    	Map<PVariable, Set<TypeJudgement>> results = new HashMap<PVariable, Set<TypeJudgement>>();
        for (TypeJudgement typeJudgement : impliedJudgements) {
			final IInputKey inputKey = typeJudgement.getInputKey();
			if (inputKey.getArity() == 1) {
				PVariable variable = (PVariable) typeJudgement.getVariablesTuple().get(0);
		        Set<TypeJudgement> inferredTypes = results.get(variable);
		        if (inferredTypes == null) {
		        	inferredTypes = new HashSet<TypeJudgement>();
		        	results.put(variable, inferredTypes);
		        }
				inferredTypes.add(typeJudgement);
			}
		}
        return results;
    }

    /**
     * Gets direct judgements reported by constraints. No closure is applied yet.
     */
	public static Set<TypeJudgement> getDirectJudgements(
			Set<PConstraint> constraints, IQueryMetaContext context) {
		Set<TypeJudgement> equivalentJudgements =  new HashSet<TypeJudgement>();
    	for (PConstraint pConstraint : constraints) {
    		if (pConstraint instanceof ITypeInfoProviderConstraint) {
    			equivalentJudgements.addAll(((ITypeInfoProviderConstraint) pConstraint).getImpliedJudgements(context));
    		}
    	}
		return equivalentJudgements;
	}

    /**
     * Calculates the closure of a set of type judgements, with respect to supertyping.
     * 
     * @return the set of all type judgements in typesToClose and all their direct and indirect supertypes
     */
    public static Set<TypeJudgement> typeClosure(Set<TypeJudgement> typesToClose, IQueryMetaContext context) {
        Set<TypeJudgement> closure = new HashSet<TypeJudgement>(typesToClose);
        Set<TypeJudgement> delta = closure;
        while (!delta.isEmpty()) {
            Set<TypeJudgement> newTypes = new HashSet<TypeJudgement>();
            for (TypeJudgement deltaType : delta) {
            	newTypes.addAll(deltaType.getDirectlyImpliedJudgements(context));
            }
            newTypes.removeAll(closure);
            delta = newTypes;
            closure.addAll(delta);
        }
        return closure;
    }

    /**
     * Calculates a remainder set of types from a larger set, that are not subsumed by a given set of subsuming types.
     * 
     * @param subsumableTypes
     *            a set of types from which some may be implied by the subsuming types
     * @param subsumingTypes
     *            a set of types that may imply some of the subsuming types
     * @return the collection of types in subsumableTypes that are NOT identical to or supertypes of any type in
     *         subsumingTypes.
     */
    public static Set<TypeJudgement> subsumeTypes(Set<TypeJudgement> subsumableTypes, Set<TypeJudgement> subsumingTypes, IQueryMetaContext context) {
        Set<TypeJudgement> closure = typeClosure(subsumingTypes, context);
        Set<TypeJudgement> subsumed = new HashSet<TypeJudgement>(subsumableTypes);
        subsumed.removeAll(closure);
        return subsumed;
    }

}
