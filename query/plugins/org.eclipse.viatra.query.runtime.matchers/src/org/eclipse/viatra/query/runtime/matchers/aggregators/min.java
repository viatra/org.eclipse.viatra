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
import org.eclipse.viatra.query.runtime.matchers.psystem.aggregations.MinimumOperator;

/**
 * This aggregator calculates the minimum value of a selected aggregate parameter of a called pattern. The aggregate
 * parameter is selected with the '#' symbol; the aggregate parameter must not be used outside the aggregator call. The
 * other parameters of the call might be bound or unbound; bound parameters limit the matches to consider for the
 * minimum calculation.
 * 
 * @since 1.4
 *
 */
@AggregatorType(
        // TODO T extends Comparable?
        parameterTypes = {Integer.class, Double.class},
        returnTypes = {Integer.class, Double.class})
public final class min implements IAggregatorFactory {
    
    @Override
    public BoundAggregator getAggregatorLogic(Class<?> domainClass) {
        if (Comparable.class.isAssignableFrom(domainClass))
            return new BoundAggregator(MinimumOperator.getInstance(), domainClass, domainClass);
        else throw new IllegalArgumentException();
    }

}
