/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.localsearch.operations.extend;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.incquery.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.incquery.runtime.localsearch.matcher.LocalSearchMatcher;
import org.eclipse.incquery.runtime.localsearch.matcher.MatcherReference;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

/**
 * Calculates the count of matches for a called matcher
 * 
 * @author Zoltan Ujhelyi
 *
 */
public class CountOperation extends ExtendOperation<Integer> {

    PQuery calledQuery;
    Map<Integer, Integer> frameMapping;

    public CountOperation(PQuery calledQuery, Map<Integer, Integer> frameMapping, int position) {
        super(position);
        this.calledQuery = calledQuery;
        this.frameMapping = frameMapping;
    }

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        Set<Integer> adornment = Sets.newHashSet();
        for (Entry<Integer, Integer> mapping : frameMapping.entrySet()) {
            Integer source = mapping.getKey();
            if (frame.get(source) != null) {
                adornment.add(mapping.getValue());
            }
        }
        
        LocalSearchMatcher calledMatcher = context.getMatcher(new MatcherReference(calledQuery, adornment));
        final MatchingFrame mappedFrame = calledMatcher.editableMatchingFrame();
        for (Entry<Integer, Integer> entry : frameMapping.entrySet()) {
            mappedFrame.setValue(entry.getValue(), frame.getValue(entry.getKey()));
        }
        it = Iterators.singletonIterator(calledMatcher.countMatches(mappedFrame));
        
    }

}
