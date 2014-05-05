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

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.junit.Assert;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * Helper class for checking the soundness of various pattern matching strategies
 * 
 * @author Marton Bur
 *
 */
public class CompareQueryTester {

    /**
     * Comnpares the results of two queries. It is assumed that the queri specifications are equivalent and use the same
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

        HashSet<IPatternMatch> allMatchesSet = Sets.newHashSet();
        allMatchesSet.addAll(allMatches);
        HashSet<IPatternMatch> allFlattenedMatchesSet = Sets.newHashSet();
        allFlattenedMatchesSet.addAll(allFlattenedMatches);

        SetView<? extends IPatternMatch> intersection = Sets.intersection(allMatchesSet, allFlattenedMatchesSet);
        int intersectionSize = intersection.size();
        int matchesCount = allMatches.size();

        Assert.assertEquals(intersectionSize, matchesCount);
    }
}
