/*******************************************************************************
 * Copyright (c) 2010-2016, Grill Balázs, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.operations.extend;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.matcher.LocalSearchMatcher;
import org.eclipse.viatra.query.runtime.localsearch.matcher.MatcherReference;
import org.eclipse.viatra.query.runtime.localsearch.operations.CallOperationHelper;
import org.eclipse.viatra.query.runtime.localsearch.operations.IMatcherBasedOperation;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.viatra.query.runtime.matchers.psystem.aggregations.IMultisetAggregationOperator;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.AggregatorConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Calculates the aggregated value of a column based on the given {@link AggregatorConstraint}
 * 
 * @author Balázs Grill
 * @since 1.4
 */
public class AggregatorExtend extends ExtendOperation<Object> implements IMatcherBasedOperation{

    private PQuery calledQuery;
    private IQueryResultProvider matcher;
    Map<Integer, PParameter> parameterMapping;
    Map<Integer, Integer> frameMapping;
    private int position;
    private final AggregatorConstraint aggregator;
    
	/**
     * @since 1.5
     */
	@Override
	public IQueryResultProvider getAndPrepareCalledMatcher(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
		Set<PParameter> adornment = Sets.newHashSet();
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
    
	public AggregatorExtend(PQuery calledQuery, AggregatorConstraint aggregator, Map<Integer, PParameter> parameterMapping, int position) {
        super(position);
        this.calledQuery = calledQuery;
        this.parameterMapping = parameterMapping;
        this.frameMapping = CallOperationHelper.calculateFrameMapping(calledQuery, parameterMapping);
        this.aggregator = aggregator;
    }

    public PQuery getCalledQuery() {
        return calledQuery;
    }

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        getAndPrepareCalledMatcher(frame, context);
        Object[] parameterValues = new Object[calledQuery.getParameters().size()];
        for (Entry<Integer, Integer> entry : frameMapping.entrySet()) {
            parameterValues[entry.getValue()] = frame.getValue(entry.getKey());
        }
        Object aggregate = doAggregate(aggregator.getAggregator().getOperator(), parameterValues);
        it = aggregate == null ? Iterators.emptyIterator() : Iterators.<Object>singletonIterator(aggregate);
        
    }
    
    private <Domain, Accumulator, AggregateResult> AggregateResult doAggregate(IMultisetAggregationOperator<Domain, Accumulator, AggregateResult> operator, Object[] initialFrame) throws LocalSearchException{
        Accumulator accumulator = operator.createNeutral();
        for(Tuple match : matcher.getAllMatches(initialFrame)){
            @SuppressWarnings("unchecked")
            Domain column = (Domain) match.get(aggregator.getAggregatedColumn());
            accumulator = operator.update(accumulator, column, true);
        }
        return operator.getAggregate(accumulator);
    }

    @Override
	public List<Integer> getVariablePositions() {
		return Lists.asList(position, new Integer[0]);
	}
    
    @Override
    public String toString() {
        String patternName = calledQuery.getFullyQualifiedName().substring(calledQuery.getFullyQualifiedName().lastIndexOf('.') + 1);
        return String.format("Extend aggregation of %s %s to position %d", aggregator.getAggregator().getOperator().getName(), patternName, position);
    }
    
}
