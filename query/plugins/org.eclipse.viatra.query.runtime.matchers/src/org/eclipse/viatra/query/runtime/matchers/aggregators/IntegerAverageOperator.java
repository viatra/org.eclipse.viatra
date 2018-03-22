/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.aggregators;

import java.util.OptionalDouble;
import java.util.stream.Stream;

import org.eclipse.viatra.query.runtime.matchers.psystem.aggregations.IMultisetAggregationOperator;

/**
 * @author Zoltan Ujhelyi
 * @since 2.0
 */
public class IntegerAverageOperator implements IMultisetAggregationOperator<Integer, AverageAccumulator<Integer>, Double> {

    public static final IntegerAverageOperator INSTANCE = new IntegerAverageOperator();
    
    private IntegerAverageOperator() {
        // Singleton, do not call.
    }
    
    @Override
    public String getShortDescription() {
        return "avg<Integer> incrementally computes the average of java.lang.Integer values";
    }

    @Override
    public String getName() {
        return "avg<Integer>";
    }

    @Override
    public AverageAccumulator<Integer> createNeutral() {
        return new AverageAccumulator<Integer>(0, 0l);
    }

    @Override
    public boolean isNeutral(AverageAccumulator<Integer> result) {
        return result.count == 0l;
    }

    @Override
    public AverageAccumulator<Integer> update(AverageAccumulator<Integer> oldResult, Integer updateValue,
            boolean isInsertion) {
        if (isInsertion) {
            oldResult.value += updateValue;
            oldResult.count++;
        } else {
            oldResult.value -= updateValue;
            oldResult.count--;
        }
        return oldResult;
    }

    @Override
    public Double getAggregate(AverageAccumulator<Integer> result) {
        return (result.count == 0)
                ? null
                : ((double)result.value)/result.count;
    }

    @Override
    public Double aggregateStream(Stream<Integer> stream) {
        final OptionalDouble averageOpt = stream.mapToInt(Integer::intValue).average();
        return averageOpt.isPresent() ? averageOpt.getAsDouble() : null;
    }
    
}