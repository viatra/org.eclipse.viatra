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
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
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
    IQueryResultProvider matcher;
    Set<PParameter> adornment;
    Set<Integer> filledVariables;
    
    protected AbstractPositivePatternCallOperation(PQuery calledQuery, Map<Integer, PParameter> parameterMapping) {
        this.calledQuery = calledQuery;
        this.parameterMapping = parameterMapping;
        
        frameMapping = CallOperationHelper.calculateFrameMapping(calledQuery, parameterMapping);
    }
    
    /**
     * @since 1.5
     */
    protected Object[] mapFrame(MatchingFrame frameInCaller){
        Object[] parameterValues = new Object[calledQuery.getParameters().size()];
        for (Entry<Integer, Integer> entry : frameMapping.entrySet()) {
            parameterValues[entry.getValue()] = frameInCaller.getValue(entry.getKey());
        }
        return parameterValues;
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
    
    /**
     * @throws LocalSearchException 
     * @since 1.5
     */
    @Override
    public IQueryResultProvider getAndPrepareCalledMatcher(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
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

    /**
     * @since 1.5
     */
    @Override
    public IQueryResultProvider getCalledMatcher(){
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
