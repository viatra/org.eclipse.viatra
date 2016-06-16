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
 * Incrementally computes the minimum of java.lang.Comparable values, using the default comparison
 * @author Gabor Bergmann
 * @since 1.4
 */
public class MinimumOperator
    extends AbstractMultisetAggregationOperator<Comparable, SortedMultiset<Comparable>, Comparable> {
    
    public static MinimumOperator INSTANCE = new MinimumOperator();

    @Override
    public String getShortDescription() {
        return "min incrementally computes the minimum of java.lang.Comparable values, using the default comparison";
    }
    @Override
    public String getName() {
        return "min";
    }

    @Override
    public SortedMultiset<Comparable> createNeutral() {
        return TreeMultiset.create();
    }

    @Override
    public boolean isNeutral(SortedMultiset<Comparable> result) {
        return result.isEmpty();
    }

    @Override
    public SortedMultiset<Comparable> update(SortedMultiset<Comparable> oldResult, Comparable updateValue,
            boolean isInsertion) 
    {
        if (isInsertion)
            oldResult.add(updateValue);
        else
            oldResult.remove(updateValue);
        return oldResult;
    }

    @Override
    public Comparable getAggregate(SortedMultiset<Comparable> result) {
        return result.isEmpty() ?
                null :
                result.firstEntry().getElement();
    }
    

    @Override
    public Comparable aggregateStatelessly(Collection<Comparable> aggregableValues) {
        if (aggregableValues.isEmpty())
            return null;
        else 
            return Collections.min(aggregableValues);
    }
}
