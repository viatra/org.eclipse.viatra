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

import org.eclipse.viatra.query.runtime.matchers.psystem.aggregations.AbstractMultisetAggregationOperator;

import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

/**
 * Incrementally computes the minimum or maximum of java.lang.Comparable values, using the default comparison
 * 
 * @author Gabor Bergmann
 * @since 1.4
 */
public class ExtremumOperator<T extends Comparable<T>>
        extends AbstractMultisetAggregationOperator<T, SortedMultiset<T>, T> {
    
    public enum Extreme {
        MIN, MAX;
        
        public <T> T pickFrom(SortedMultiset<T> nonEmptyMultiSet) {
            switch(this) {
            case MIN: 
                return nonEmptyMultiSet.firstEntry().getElement(); 
            case MAX:
                return nonEmptyMultiSet.lastEntry().getElement();
            default:
                return null;
            }
        }
    }

    private static final ExtremumOperator MIN_OP = new ExtremumOperator<>(Extreme.MIN);
    private static final ExtremumOperator MAX_OP = new ExtremumOperator<>(Extreme.MAX);

    public static <T extends Comparable<T>> ExtremumOperator<T> getMin() {
        return MIN_OP;
    }
    public static <T extends Comparable<T>> ExtremumOperator<T> getMax() {
        return MAX_OP;
    }
    
    Extreme extreme;
    private ExtremumOperator(Extreme extreme) {
        super();
        this.extreme = extreme;
    }

    @Override
    public String getShortDescription() {
        String opName = getName();
        return String.format(
                "%s incrementally computes the %simum of java.lang.Comparable values, using the default comparison",
                opName, opName);
    }

    @Override
    public String getName() {
        return extreme.name().toLowerCase();
    }

    @Override
    public SortedMultiset<T> createNeutral() {
        return TreeMultiset.create();
    }

    @Override
    public boolean isNeutral(SortedMultiset<T> result) {
        return result.isEmpty();
    }

    @Override
    public SortedMultiset<T> update(SortedMultiset<T> oldResult, T updateValue, boolean isInsertion) {
        if (isInsertion) {
            oldResult.add(updateValue);
        } else {
            oldResult.remove(updateValue);
        }
        return oldResult;
    }

    @Override
    public T getAggregate(SortedMultiset<T> result) {
        return result.isEmpty() ? null : 
            extreme.pickFrom(result);
    }
}
