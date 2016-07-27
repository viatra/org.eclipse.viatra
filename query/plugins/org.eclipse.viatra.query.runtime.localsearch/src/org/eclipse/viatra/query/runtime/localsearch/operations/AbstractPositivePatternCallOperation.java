/*******************************************************************************
 * Copyright (c) 2010-2016, Grill Balázs, IncQueryLabs
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.operations;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.matcher.LocalSearchMatcher;
import org.eclipse.viatra.query.runtime.localsearch.matcher.MatcherReference;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

/**
 * @author Grill Balázs
 * @since 1.4
 *
 */
public abstract class AbstractPositivePatternCallOperation implements ISearchOperation, IMatcherBasedOperation {

    private final PQuery calledQuery;
    private final Map<Integer, PParameter> parameterMapping;
    private final Map<Integer, Integer> frameMapping;
    LocalSearchMatcher matcher;
    Set<PParameter> adornment;
    Set<Integer> filledVariables;
    
    protected AbstractPositivePatternCallOperation(PQuery calledQuery, Map<Integer, PParameter> parameterMapping) {
        this.calledQuery = calledQuery;
        this.parameterMapping = parameterMapping;
        
        frameMapping = CallOperationHelper.calculateFrameMapping(calledQuery, parameterMapping);
    }
    
    protected MatchingFrame mapFrame(MatchingFrame frameInCaller){
        final MatchingFrame mappedFrame = matcher.editableMatchingFrame();
        Object[] parameterValues = new Object[matcher.getParameterCount()];
        for (Entry<Integer, Integer> entry : frameMapping.entrySet()) {
            parameterValues[entry.getValue()] = frameInCaller.getValue(entry.getKey());
        }
        mappedFrame.setParameterValues(parameterValues);
        return mappedFrame;
    }
    
    protected boolean fillInResult(MatchingFrame frame, Tuple tuple){
        filledVariables = Sets.newHashSet();
        Multimap<Integer, Integer> backMap = Multimaps.invertFrom(Multimaps.forMap(frameMapping), ArrayListMultimap.<Integer, Integer>create());
        for(int i=0;i<tuple.getSize();i++){
            if (!adornment.contains(calledQuery.getParameters().get(i))){
                Object value = null;
                for(Integer j : backMap.get(i)){
                    Object filledValue = tuple.get(i);
                    if (value != null && !value.equals(filledValue)){
                        // If the inverse map contains more than one values for the same key, it means that these arguments are unified by the caller. 
                        // In this case if the callee assigns different values the frame shall be dropped
                        return false;
                    }
                    value = filledValue;
                    frame.setValue(j, filledValue);
                    filledVariables.add(j);
                }
            }
        }
        return true;
    }
    
    @Override
    public void onBacktrack(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        if (filledVariables != null){
            for(Integer i : filledVariables){
                frame.setValue(i, null);
            }
        }
    }
    
    @Override
    public LocalSearchMatcher getAndPrepareCalledMatcher(MatchingFrame frame, ISearchContext context) {
        adornment = Sets.newHashSet();
        for (Entry<Integer, PParameter> mapping : parameterMapping.entrySet()) {
            Preconditions.checkNotNull(mapping.getKey(), "Mapping frame must not contain null keys");
            Preconditions.checkNotNull(mapping.getValue(), "Mapping frame must not contain null values");
            Integer source = mapping.getKey();
            if (frame.get(source) != null) {
                adornment.add(mapping.getValue());
            }
        }
        matcher = context.getMatcher(new MatcherReference(calledQuery, adornment));
        return matcher;
    }

    @Override
    public LocalSearchMatcher getCalledMatcher(){
        return matcher;
    }
    
    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        getAndPrepareCalledMatcher(frame, context);
    }
    
    @Override
    public List<Integer> getVariablePositions() {
        return Lists.newArrayList(frameMapping.keySet());
    }

}
