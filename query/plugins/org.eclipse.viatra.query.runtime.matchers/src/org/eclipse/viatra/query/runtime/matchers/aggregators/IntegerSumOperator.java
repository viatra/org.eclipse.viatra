/*******************************************************************************
 * Copyright (c) 2010-2016, Gabor Bergmann, IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.aggregators;

import org.eclipse.viatra.query.runtime.matchers.psystem.aggregations.AbstractMemorylessAggregationOperator;

/**
 * Incrementally computes the sum of java.lang.Integer values
 * @author Gabor Bergmann
 * @since 1.4
 */
public class IntegerSumOperator extends AbstractMemorylessAggregationOperator<Integer, Integer> {
    public static final IntegerSumOperator INSTANCE = new IntegerSumOperator();
    
    private IntegerSumOperator() {
        // Singleton, do not call.
    }

    @Override
    public String getShortDescription() {
        return "sum<Integer> incrementally computes the sum of java.lang.Integer values";
    }
    @Override
    public String getName() {
        return "sum<Integer>";
    }
    
    @Override
    public Integer createNeutral() {
        return 0;
    }

    @Override
    public boolean isNeutral(Integer result) {
        return createNeutral().equals(result);
    }

    @Override
    public Integer update(Integer oldResult, Integer updateValue, boolean isInsertion) {
        return isInsertion ? 
                oldResult + updateValue : 
                oldResult - updateValue;
    }

}
