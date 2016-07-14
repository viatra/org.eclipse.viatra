/*******************************************************************************
 * Copyright (c) 2010-2016, Grill Balázs, IncQueryLabs
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.operations.extend;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.AbstractPositivePatternCallOperation;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

/**
 * @author Grill Balázs
 * @since 1.4
 *
 */
public class ExtendPositivePatternCall extends AbstractPositivePatternCallOperation {

    private Iterator<Tuple> matches = null;
    
    /**
     * @param position
     */
    public ExtendPositivePatternCall(PQuery calledQuery, Map<Integer, Integer> frameMapping) {
       super(calledQuery, frameMapping);
    }

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        super.onInitialize(frame, context);
        matches = getCalledMatcher().getAllMatches(mapFrame(frame)).iterator();
    }

    @Override
    public boolean execute(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        if (matches.hasNext()){
            Tuple tuple = matches.next();
            while(!fillInResult(frame, tuple) && matches.hasNext()){
                tuple = matches.next();
            }
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void onBacktrack(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        super.onBacktrack(frame, context);
        matches = null;
    }
}
