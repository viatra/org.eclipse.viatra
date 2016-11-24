/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.matcher.MatcherReference;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.viatra.query.runtime.matchers.psystem.aggregations.IMultisetAggregationOperator;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

/**
 * This utility can be used by search operations to access sub-patterns, by taking care
 * of frame mapping, adornment calculation and other tasks required to call an {@link IQueryResultProvider}
 * from the context of a Local search execution.
 * @since 1.4
 */
public class CallOperationHelper {

    private static Multimap<Integer, Integer> calculateFrameMapping(PQuery calledQuery, Map<PParameter, Integer> parameterMapping) {
        Multimap<Integer, Integer> frameMapping = Multimaps.newListMultimap(Maps.<Integer, Collection<Integer>>newHashMap(), new Supplier<List<Integer>>() {

            @Override
            public List<Integer> get() {
                return new ArrayList<>(1);
            }
        });
        for (Entry<PParameter, Integer> entry : parameterMapping.entrySet()) {
            frameMapping.put(entry.getValue(), calledQuery.getPositionOfParameter(entry.getKey().getName()));
        }
        return frameMapping;
    }
    
    private final PQuery calledQuery;
    private final Map<PParameter, Integer> parameterMapping;
    private final Multimap<Integer, Integer> frameMapping;
    
    /**
     * @since 1.5
     */
    public class PatternCall{
        IQueryResultProvider matcher;
        Set<PParameter> adornment;
        Set<Integer> filledVariables;
        
        public boolean fillInResult(MatchingFrame frame, Tuple result){
            filledVariables = Sets.newHashSet();
            Multimap<Integer, Integer> backMap = Multimaps.invertFrom(frameMapping, ArrayListMultimap.<Integer, Integer>create());
            for(int i=0;i<result.getSize();i++){
                if (!adornment.contains(calledQuery.getParameters().get(i))){
                    Object value = null;
                    for(Integer j : backMap.get(i)){
                        Object filledValue = result.get(i);
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
        
        public void backtrack(MatchingFrame frame) throws LocalSearchException {
            if (filledVariables != null){
                for(Integer i : filledVariables){
                    frame.setValue(i, null);
                }
            }
        }
        
        private PatternCall(Set<Integer> adornment, ISearchContext context) throws LocalSearchException{
            this.adornment = Sets.newHashSet();
            for(Entry<PParameter, Integer> entry: parameterMapping.entrySet()){
                if (adornment.contains(entry.getValue())){
                    this.adornment.add(entry.getKey());
                }
            }
            matcher = context.getMatcher(new MatcherReference(calledQuery, this.adornment));
        }
        
        private PatternCall(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
            adornment = Sets.newHashSet();
            for (Entry<PParameter, Integer> mapping : parameterMapping.entrySet()) {
                Preconditions.checkNotNull(mapping.getKey(), "Mapping frame must not contain null keys");
                Preconditions.checkNotNull(mapping.getValue(), "Mapping frame must not contain null values");
                Integer source = mapping.getValue();
                if (frame.get(source) != null) {
                    adornment.add(mapping.getKey());
                }
            }
            matcher = context.getMatcher(new MatcherReference(calledQuery, adornment));
        }
        
        private Object[] mapFrame(MatchingFrame frameInCaller){
            Object[] parameterValues = new Object[calledQuery.getParameters().size()];
            for (Entry<Integer, Integer> entry : frameMapping.entries()) {
                parameterValues[entry.getValue()] = frameInCaller.getValue(entry.getKey());
            }
            return parameterValues;
        }
        
        public boolean has(MatchingFrame frame) {
            return matcher.getOneArbitraryMatch(mapFrame(frame)) != null;
        }
        
        public int count(MatchingFrame frame){
            return matcher.countMatches(mapFrame(frame));
        }
        
        public <Domain, Accumulator, AggregateResult> AggregateResult aggregate(IMultisetAggregationOperator<Domain, Accumulator, AggregateResult> operator, int aggregatedColumn, MatchingFrame initialFrame) throws LocalSearchException{
            Object[] frame = mapFrame(initialFrame);
            Accumulator accumulator = operator.createNeutral();
            for(Tuple match : matcher.getAllMatches(frame)){
                @SuppressWarnings("unchecked")
                Domain column = (Domain) match.get(aggregatedColumn);
                accumulator = operator.update(accumulator, column, true);
            }
            return operator.getAggregate(accumulator);
        }

        public Collection<? extends Tuple> getAllMatches(Object[] mappedFrame) {
            return matcher.getAllMatches(mappedFrame);
        }
        
        public Collection<? extends Tuple> getAllMatches(MatchingFrame frameInCaller) {
            return matcher.getAllMatches(mapFrame(frameInCaller));
        }
    }
    
    /**
     * @since 1.5
     */
    public CallOperationHelper(PQuery calledQuery, Map<PParameter, Integer> parameterMapping) {
        this.calledQuery = calledQuery;
        this.parameterMapping = parameterMapping;
        
        frameMapping = CallOperationHelper.calculateFrameMapping(calledQuery, parameterMapping);
    } 

    /**
     * Create a call object. The adornment is calculated from the given frame, considering
     * a variable bound if it's value is not null.
     * @since 1.5
     */
    public PatternCall createCall(MatchingFrame frame, ISearchContext context) throws LocalSearchException{
        return new PatternCall(frame, context);
    }
    
    /**
     * @since 1.5
     */
    public PatternCall createCall(Set<Integer> adornment, ISearchContext context) throws LocalSearchException{
        return new PatternCall(adornment, context);
    }
    
    /**
     * @since 1.5
     */
    public List<Integer> getVariablePositions() {
        return Lists.newArrayList(frameMapping.keySet());
    }
    
    @Override
    public String toString() {
        return calledQuery.getFullyQualifiedName()+"("+Joiner.on(",").join(getVariablePositions())+")";
    }
}
