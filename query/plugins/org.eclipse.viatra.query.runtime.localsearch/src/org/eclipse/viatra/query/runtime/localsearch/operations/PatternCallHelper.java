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
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

/**
 * This utility can be used by search operations to access sub-patterns, by taking care
 * of frame mapping, adornment calculation and other tasks required to call an {@link IQueryResultProvider}
 * from the context of a Local search execution.
 * 
 * @author Grill Balázs
 * @since 1.5
 *
 */
public class PatternCallHelper {

    private final PQuery calledQuery;
    private final Map<Integer, PParameter> parameterMapping;
    private final Map<Integer, Integer> frameMapping;
    
    public class PatternCall{
        IQueryResultProvider matcher;
        Set<PParameter> adornment;
        Set<Integer> filledVariables;
        
        public boolean fillInResult(MatchingFrame frame, Tuple result){
            filledVariables = Sets.newHashSet();
            Multimap<Integer, Integer> backMap = Multimaps.invertFrom(Multimaps.forMap(frameMapping), ArrayListMultimap.<Integer, Integer>create());
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
            for(Integer boundParameter : adornment){
                Preconditions.checkArgument(parameterMapping.containsKey(boundParameter), "Mapping does not contain "+boundParameter);
                this.adornment.add(parameterMapping.get(boundParameter));
            }
            matcher = context.getMatcher(new MatcherReference(calledQuery, this.adornment));
        }
        
        private PatternCall(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
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
        }
        
        private Object[] mapFrame(MatchingFrame frameInCaller){
            Object[] parameterValues = new Object[calledQuery.getParameters().size()];
            for (Entry<Integer, Integer> entry : frameMapping.entrySet()) {
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
    
    public PatternCallHelper(PQuery calledQuery, Map<Integer, PParameter> parameterMapping) {
        this.calledQuery = calledQuery;
        this.parameterMapping = parameterMapping;
        
        frameMapping = CallOperationHelper.calculateFrameMapping(calledQuery, parameterMapping);
    } 

    /**
     * Create a call object. The adornment is calculated from the given frame, considering
     * a variable bound if it's value is not null.
     */
    public PatternCall createCall(MatchingFrame frame, ISearchContext context) throws LocalSearchException{
        return new PatternCall(frame, context);
    }
    
    public PatternCall createCall(Set<Integer> adornment, ISearchContext context) throws LocalSearchException{
        return new PatternCall(adornment, context);
    }
    
    public List<Integer> getVariablePositions() {
        return Lists.newArrayList(frameMapping.keySet());
    }
    
    @Override
    public String toString() {
        return calledQuery.getFullyQualifiedName()+"("+Joiner.on(",").join(getVariablePositions())+")";
    }
    
}
