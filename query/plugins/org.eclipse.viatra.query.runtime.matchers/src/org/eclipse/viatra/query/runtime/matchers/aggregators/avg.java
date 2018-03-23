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
package org.eclipse.viatra.query.runtime.matchers.aggregators;

import org.eclipse.viatra.query.runtime.matchers.psystem.aggregations.AggregatorType;
import org.eclipse.viatra.query.runtime.matchers.psystem.aggregations.BoundAggregator;
import org.eclipse.viatra.query.runtime.matchers.psystem.aggregations.IAggregatorFactory;

/**
 * This aggregator calculates the average of the values of a selected aggregate parameter of a called pattern. The aggregate
 * parameter is selected with the '#' symbol; the aggregate parameter must not be used outside the aggregator call. The
 * other parameters of the call might be bound or unbound; bound parameters limit the matches to consider for the
 * summation.
 * 
 * @since 2.0
 *
 */
@AggregatorType(
        parameterTypes = {Integer.class, Double.class, Long.class},
        returnTypes = {Double.class, Double.class, Double.class})
public final class avg implements IAggregatorFactory {

    @Override
    public BoundAggregator getAggregatorLogic(Class<?> domainClass) {
        if (Integer.class.equals(domainClass))
            return new BoundAggregator(IntegerAverageOperator.INSTANCE, Integer.class, Double.class);
        if (Double.class.equals(domainClass))
            return new BoundAggregator(DoubleAverageOperator.INSTANCE, Double.class, Double.class);
        if (Long.class.equals(domainClass))
            return new BoundAggregator(LongAverageOperator.INSTANCE, Long.class, Double.class);
        else throw new IllegalArgumentException();
    }
}