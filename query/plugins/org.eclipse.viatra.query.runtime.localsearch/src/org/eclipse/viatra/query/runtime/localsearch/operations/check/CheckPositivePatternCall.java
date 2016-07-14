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
package org.eclipse.viatra.query.runtime.localsearch.operations.check;

import java.util.Map;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.AbstractPositivePatternCallOperation;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

/**
 * @author Grill Balázs
 * @since 1.4
 *
 */
public class CheckPositivePatternCall extends AbstractPositivePatternCallOperation {

    /**
     * The executed field ensures that the second call of the check always returns false, resulting in a quick
     * backtracking.
     */
    private boolean executed;

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        executed = false;
    }

    @Override
    public boolean execute(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        executed = executed ? false : check(frame);
        return executed;
    }
    
    /**
     * @param frame
     * @return
     * @throws LocalSearchException 
     */
    private boolean check(MatchingFrame frame) throws LocalSearchException {
        return getCalledMatcher().hasMatch(mapFrame(frame));
    }

    /**
     * @param calledQuery
     * @param frameMapping
     */
    public CheckPositivePatternCall(PQuery calledQuery, Map<Integer, Integer> frameMapping) {
        super(calledQuery, frameMapping);
    }



}
