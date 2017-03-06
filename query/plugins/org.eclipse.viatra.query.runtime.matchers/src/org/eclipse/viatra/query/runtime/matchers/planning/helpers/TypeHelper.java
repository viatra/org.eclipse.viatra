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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.viatra.query.runtime.matchers.psystem.ITypeInfoProviderConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.TypeJudgement;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

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
        return typeClosure(Collections.<TypeJudgement>emptySet(), typesToClose, context);
    }

    /**
     * Calculates the closure of a set of type judgements (with respect to supertyping), 
     *      where the closure has been calculated before for a given base set, but not for a separate delta set.
     * <p> Precondition: the set (typesToClose MINUS delta) is already closed w.r.t. supertyping. 
     * 
     * @return the set of all type judgements in typesToClose and all their direct and indirect supertypes
     * @since 1.6
     */
    public static Set<TypeJudgement> typeClosure(Set<TypeJudgement> preclosedBaseSet, Set<TypeJudgement> delta, IQueryMetaContext context) {
        delta = Sets.difference(delta, preclosedBaseSet);
        if (delta.isEmpty()) return preclosedBaseSet;
        
        Set<TypeJudgement> closure = new HashSet<TypeJudgement>(preclosedBaseSet);
        Queue<TypeJudgement> queue = new LinkedList<TypeJudgement>(delta);
        
        SetMultimap<TypeJudgement, TypeJudgement> conditionalImplications = HashMultimap.create();
        for (TypeJudgement typeJudgement : closure) {
            conditionalImplications.putAll(typeJudgement.getConditionalImpliedJudgements(context));
        }
        
        do {
            TypeJudgement deltaType = queue.poll();
            if (closure.add(deltaType)) {
                // direct implications
                queue.addAll(deltaType.getDirectlyImpliedJudgements(context));
                
                // conditional implications, source key processed before, this is the condition key
                queue.addAll(conditionalImplications.get(deltaType));
                
                // conditional implications, this is the source key 
                SetMultimap<TypeJudgement, TypeJudgement> deltaConditionalImplications = deltaType.getConditionalImpliedJudgements(context);
                for (TypeJudgement condition : deltaConditionalImplications.keySet()) {
                    if (closure.contains(condition)) {
                        // condition processed before
                        queue.addAll(deltaConditionalImplications.get(condition));                        
                    } else {
                        // condition not processed yet
                        conditionalImplications.putAll(condition, deltaConditionalImplications.get(condition));
                    }
                }
            }
        } while (!queue.isEmpty());
        
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
