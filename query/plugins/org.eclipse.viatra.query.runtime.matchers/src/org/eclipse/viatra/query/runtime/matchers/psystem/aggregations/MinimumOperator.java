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
package org.eclipse.viatra.query.runtime.matchers.psystem.aggregations;

import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

/**
 * Incrementally computes the minimum of java.lang.T values, using the default comparison
 * 
 * @author Gabor Bergmann
 * @since 1.4
 */
public class MinimumOperator<T extends Comparable<T>>
        extends AbstractMultisetAggregationOperator<T, SortedMultiset<T>, T> {

    private static MinimumOperator INSTANCE = new MinimumOperator();

    public static <T extends Comparable<T>> MinimumOperator<T> getInstance() {
        return INSTANCE;
    }

    @Override
    public String getShortDescription() {
        return "min incrementally computes the minimum of java.lang.T values, using the default comparison";
    }

    @Override
    public String getName() {
        return "min";
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
        return result.isEmpty() ? null : result.firstEntry().getElement();
    }
}
