/*******************************************************************************
 * Copyright (c) 2010-2014, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.testing.core.base;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.junit.Assert;

import com.google.common.collect.Sets;

/**
 * Helper class for checking the soundness of various pattern matching strategies
 * 
 * @author Marton Bur
 *
 */
public class CompareQueryTester {

    /**
     * Compares the results of two queries. It is assumed that the query specifications are equivalent and use the same
     * match classes
     */
    public static void assertQueriesEquivalent(IncQueryEngine engine, IQuerySpecification<?> querySpecification1,
            IQuerySpecification<?> querySpecification2) throws IncQueryException {
        // Create a matcher for the original query specification
        IncQueryMatcher<? extends IPatternMatch> matcher = querySpecification1.getMatcher(engine);
        // Create a matcher for the flattened query specification
        IncQueryMatcher<? extends IPatternMatch> flattenedMatcher = querySpecification2.getMatcher(engine);

        Collection<? extends IPatternMatch> allMatches = matcher.getAllMatches();
        Collection<? extends IPatternMatch> allFlattenedMatches = flattenedMatcher.getAllMatches();

        // Cannot compare directly collection of matches, so that first we need to convert matches to arrays
        Set<List<Object>> allMatchArrays = Sets.newHashSet();
        for (IPatternMatch iPatternMatch : allMatches) {
            allMatchArrays.add(Arrays.asList(iPatternMatch.toArray()));
        }
        Set<List<Object>> allFlattenedMatchArrays = Sets.newHashSet();
        for (IPatternMatch iPatternMatch : allFlattenedMatches) {
            allFlattenedMatchArrays.add(Arrays.asList(iPatternMatch.toArray()));
        }
        
        Assert.assertTrue(allMatchArrays.equals(allFlattenedMatchArrays));
    }
}
