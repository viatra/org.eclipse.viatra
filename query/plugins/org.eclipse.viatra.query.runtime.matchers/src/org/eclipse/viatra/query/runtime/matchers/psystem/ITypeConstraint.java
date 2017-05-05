/*******************************************************************************
 * Copyright (c) 2010-2015, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.psystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

import java.util.Set;

/**
 * Common superinterface of enumerable and deferred type constraints.
 * @author Bergmann Gabor
 *
 */
public interface ITypeConstraint extends ITypeInfoProviderConstraint {

    public abstract TypeJudgement getEquivalentJudgement();
    
    /**
     * Static internal utility class for implementations of {@link ITypeConstraint}s.
     * @author Bergmann Gabor
     */
    public static class TypeConstraintUtil {
        public static Map<Set<PVariable>, Set<PVariable>> getFunctionalDependencies(IQueryMetaContext context, IInputKey inputKey, Tuple variablesTuple) {
            final Map<Set<PVariable>, Set<PVariable>> result = new HashMap<Set<PVariable>, Set<PVariable>>();
            
            Set<Entry<Set<Integer>, Set<Integer>>> dependencies = context.getFunctionalDependencies(inputKey).entrySet();
            for (Entry<Set<Integer>, Set<Integer>> dependency : dependencies) {
                result.put(
                        transcribeVariables(dependency.getKey(), variablesTuple), 
                        transcribeVariables(dependency.getValue(), variablesTuple)
                );
            }

            return result;
        }

        private static Set<PVariable> transcribeVariables(Set<Integer> indices, Tuple variablesTuple) {
            Set<PVariable> result = new HashSet<PVariable>();
            for (Integer index : indices) {
                result.add((PVariable) variablesTuple.get(index));
            }
            return result;
        }

    }

}
