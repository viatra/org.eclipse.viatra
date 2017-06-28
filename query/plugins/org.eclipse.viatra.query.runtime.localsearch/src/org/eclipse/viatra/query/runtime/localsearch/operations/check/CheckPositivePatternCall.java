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

import java.util.List;
import java.util.Map;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.matcher.MatcherReference;
import org.eclipse.viatra.query.runtime.localsearch.operations.CallOperationHelper;
import org.eclipse.viatra.query.runtime.localsearch.operations.CallOperationHelper.PatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;

/**
 * @author Grill Balázs
 * @since 1.4
 * @noextend This class is not intended to be subclassed by clients.
 */
public class CheckPositivePatternCall extends CheckOperation {

    private final CallOperationHelper helper;
    private PatternCall call;

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        super.onInitialize(frame, context);
        call = helper.createCall(context);
    }
    
    /**
     * @deprecated Use {@link #check(MatchingFrame, ISearchContext)} instead
     */
    @Deprecated
    public boolean check(MatchingFrame frame) throws LocalSearchException {
        return check(frame, null);
    }

    /**
     * @since 1.5
     */
    protected boolean check(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        return call.has(frame);
    }

    /**
     * @since 1.5
     */
    public CheckPositivePatternCall(MatcherReference calledQuery, Map<PParameter, Integer> frameMapping) {
        super();
        helper = new CallOperationHelper(calledQuery, frameMapping);
    }

    @Override
    public List<Integer> getVariablePositions() {
        return helper.getVariablePositions();
    }
    
    @Override
    public String toString() {
        return "check     find "+helper.toString();
    }

}
