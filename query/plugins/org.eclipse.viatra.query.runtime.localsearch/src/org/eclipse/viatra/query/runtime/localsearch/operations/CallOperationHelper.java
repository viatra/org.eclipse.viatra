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
import org.eclipse.viatra.query.runtime.matchers.backend.IUpdateable;
import org.eclipse.viatra.query.runtime.matchers.psystem.aggregations.IMultisetAggregationOperator;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
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
    private final Set<PParameter> adornment;
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
        
        private PatternCall(Set<PParameter> adornment, ISearchContext context) throws LocalSearchException{
            this.adornment = adornment;
            matcher = context.getMatcher(new MatcherReference(calledQuery, this.adornment));
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
        
        /**
         * @since 1.7
         */
        public void registerChangeListener(IUpdateable listener) {
            matcher.addUpdateListener(listener, listener, true);
        }
        
        /**
         * @since 1.7
         */
        public void removeChangeListener(IUpdateable listener) {
            matcher.removeUpdateListener(listener);
        }
    }
    
    /**
     * @since 1.5
     */
    public CallOperationHelper(MatcherReference calledQuery, Map<PParameter, Integer> parameterMapping) {
        this.calledQuery = calledQuery.getQuery();
        this.adornment = calledQuery.getAdornment();
        this.parameterMapping = parameterMapping;
        
        frameMapping = CallOperationHelper.calculateFrameMapping(calledQuery.getQuery(), parameterMapping);
    } 

    /**
     * Create a call object. The adornment is calculated from the given frame, considering
     * a variable bound if it's value is not null.
     * @since 1.5
     * @deprecated use {@link #createCall(ISearchContext)} instead
     */
    @Deprecated
    public PatternCall createCall(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        return new PatternCall(adornment, context);
    }
    
    /**
     * @since 1.5
     * @deprecated use {@link #createCall(ISearchContext)} instead
     */
    @Deprecated
    public PatternCall createCall(Set<Integer> adornment, ISearchContext context) throws LocalSearchException {
        return new PatternCall(this.adornment, context);
    }
    
    /**
     * Create a call object based on the adornment calculated from the matcher reference
     * 
     * @since 1.7
     */
    public PatternCall createCall(ISearchContext context) throws LocalSearchException {
        return new PatternCall(this.adornment, context);
    }
    
    /**
     * @since 1.5
     */
    public List<Integer> getVariablePositions() {
        List<Integer> variables = new ArrayList<Integer>(parameterMapping.size());
        for(PParameter p : calledQuery.getParameters()){
            variables.add(parameterMapping.get(p));
        }
        return variables;
    }
    
    /**
     * @since 1.7
     */
    public PQuery getCalledQuery() {
        return calledQuery;
    }

    @Override
    public String toString() {
        return calledQuery.getFullyQualifiedName()+"("+Joiner.on(",").join(
                Iterables.transform(calledQuery.getParameters(), new Function<PParameter, String>() {

                    @Override
                    public String apply(PParameter input) {
                        return (adornment.contains(input) ? "+" : "-") + parameterMapping.get(input);
                    }
                }))+")";
    }
    
}
