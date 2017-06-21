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
package org.eclipse.viatra.query.runtime.localsearch.operations.check;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.matcher.MatcherReference;
import org.eclipse.viatra.query.runtime.localsearch.operations.CallOperationHelper;
import org.eclipse.viatra.query.runtime.localsearch.operations.CallOperationHelper.PatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.AggregatorConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;

import com.google.common.collect.Lists;

/**
 * Calculates the aggregated value of a column based on the given {@link AggregatorConstraint}
 * 
 * @author Balázs Grill
 * @since 1.4
 */
public class AggregatorCheck extends CheckOperation{

    private final CallOperationHelper helper;
    private PatternCall call;
    private int position;
    private final AggregatorConstraint aggregator;
    
    
    /**
     * @since 1.5
     */
    public AggregatorCheck(MatcherReference calledQuery, AggregatorConstraint aggregator, Map<PParameter, Integer> parameterMapping, int position) {
        super();
        helper = new CallOperationHelper(calledQuery, parameterMapping);
        this.position = position;
        this.aggregator = aggregator;
    }

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        super.onInitialize(frame, context);
        call = helper.createCall(context);
    }

    @Override
    protected boolean check(MatchingFrame frame) throws LocalSearchException {
        Object result = call.aggregate(aggregator.getAggregator().getOperator(), aggregator.getAggregatedColumn(), frame);
        return result == null ? false : Objects.equals(frame.getValue(position), result);
    }

    @Override
    public List<Integer> getVariablePositions() {
        return Lists.asList(position, new Integer[0]);
    }
    
    @Override
    public String toString() {
        return "check     "+position+" = " + aggregator.getAggregator().getOperator().getName()+" find "+helper.toString();
    }

    
}
