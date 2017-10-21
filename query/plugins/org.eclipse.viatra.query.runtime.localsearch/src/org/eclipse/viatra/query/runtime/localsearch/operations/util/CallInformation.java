/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.operations.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.localsearch.matcher.MatcherReference;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.PatternCallBasedDeferred;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.BinaryTransitiveClosure;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * This class stores a precompiled version of call-related metadata and masks for local search operations
 * 
 * @author Zoltan Ujhelyi
 * @since 1.7
 */
public final class CallInformation {

    private final TupleMask fullFrameMask;   
    private final TupleMask thinFrameMask;   
    private final TupleMask parameterMask;
    private final int[] freeParameterIndices;
    
    private final Map<PParameter, Integer> mapping = Maps.newHashMap();
    private final Set<PParameter> adornment = Sets.newHashSet();
    private final PQuery referredQuery;
    private final MatcherReference matcherReference;
    
    public static CallInformation create(PatternCallBasedDeferred constraint, Map<PVariable, Integer> variableMapping, Set<Integer> bindings) {
        return new CallInformation(constraint.getActualParametersTuple(), constraint.getReferredQuery(), bindings, variableMapping);
    }
    
    public static CallInformation create(PositivePatternCall pCall, Map<PVariable, Integer> variableMapping, Set<Integer> bindings) {
        return new CallInformation(pCall.getVariablesTuple(), pCall.getReferredQuery(), bindings, variableMapping);
    }
    
    public static CallInformation create(BinaryTransitiveClosure constraint, Map<PVariable, Integer> variableMapping, Set<Integer> bindings) {
        return new CallInformation(constraint.getVariablesTuple(), constraint.getReferredQuery(), bindings, variableMapping);
    }
    
    private CallInformation(Tuple actualParameters, PQuery referredQuery, final Set<Integer> bindings,
            Map<PVariable, Integer> variableMapping) {
        this.referredQuery = referredQuery;
        int keySize = actualParameters.getSize();
        List<Integer> parameterMaskIndices = new ArrayList<>();
        int[] fullParameterMaskIndices = new int[keySize];
        for (int i = 0; i < keySize; i++) {
            PParameter symbolicParameter = referredQuery.getParameters().get(i);
            PVariable parameter = (PVariable) actualParameters.get(i);
            Integer originalFrameIndex = variableMapping.get(parameter);
            mapping.put(symbolicParameter, originalFrameIndex);
            fullParameterMaskIndices[i] = originalFrameIndex;
            if (bindings.contains(originalFrameIndex)) {
                parameterMaskIndices.add(originalFrameIndex);
                adornment.add(symbolicParameter);
            }
        }
        
        thinFrameMask = TupleMask.fromSelectedIndices(variableMapping.size(), parameterMaskIndices);
        fullFrameMask = TupleMask.fromSelectedIndices(variableMapping.size(), fullParameterMaskIndices);
        
        // This second iteration is necessary as we don't know beforehand the number of bound parameters
        int[] boundParameterIndices = new int[adornment.size()];
        int boundIndex = 0;
        freeParameterIndices = new int[keySize - adornment.size()];
        int freeIndex = 0;
        for (int i = 0; i < keySize; i++) {
            if (bindings.contains(variableMapping.get(actualParameters.get(i)))) {
                boundParameterIndices[boundIndex] = i;
                boundIndex++;
            } else {
                freeParameterIndices[freeIndex] = i;
                freeIndex++;
            }
        }
        parameterMask = TupleMask.fromSelectedIndices(keySize, boundParameterIndices);
        matcherReference = new MatcherReference(referredQuery, adornment);
    }

    /** 
     * Returns a mask describing how the bound variables of a Matching Frame are mapped to parameter indexes
     */
    public TupleMask getThinFrameMask() {
        return thinFrameMask;
    }
    
    /** 
     * Returns a mask describing how all variables of a Matching Frame are mapped to parameter indexes
     */
    public TupleMask getFullFrameMask() {
        return fullFrameMask;
    }
    
    /**
     * Returns a mask describing the adornment the called pattern uses
     */
    public TupleMask getParameterMask() {
        return parameterMask;
    }
    
    public MatcherReference getReference() {
        return matcherReference;
    }

    /**
     * Returns the parameter indices that are unbound before the call
     */
    public int[] getFreeParameterIndices() {
        return freeParameterIndices;
    }
    
    public List<Integer> getVariablePositions() {
        List<Integer> variables = new ArrayList<>(mapping.size());
        for(PParameter p : referredQuery.getParameters()){
            variables.add(mapping.get(p));
        }
        return variables;
    }
    
    @Override
    public String toString() {
        return referredQuery.getFullyQualifiedName()+"("+Joiner.on(",").join(
                Iterables.transform(referredQuery.getParameters(), new Function<PParameter, String>() {

                    @Override
                    public String apply(PParameter input) {
                        return (adornment.contains(input) ? "+" : "-") + mapping.get(input);
                    }
                }))+")";
    }
    
}
