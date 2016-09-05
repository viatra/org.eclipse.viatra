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
 * Incrementally computes the sum of java.lang.Double values
 * @author Gabor Bergmann
 * @since 1.4
 */
public class DoubleSumOperator extends AbstractMemorylessAggregationOperator<Double, Double> {
    public static DoubleSumOperator INSTANCE = new DoubleSumOperator();
    
    private DoubleSumOperator() {
        // Singleton, do not call.
    }

    @Override
    public String getShortDescription() {
        return "sum<Double> incrementally computes the sum of java.lang.Double values";
    }
    @Override
    public String getName() {
        return "sum<Double>";
    }
    
    @Override
    public Double createNeutral() {
        return 0d;
    }

    @Override
    public boolean isNeutral(Double result) {
        return createNeutral().equals(result);
    }

    @Override
    public Double update(Double oldResult, Double updateValue, boolean isInsertion) {
        return isInsertion ? 
                oldResult + updateValue : 
                oldResult - updateValue;
    }
}
