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
package org.eclipse.viatra.query.runtime.localsearch.operations;

import java.util.List;
import java.util.Map;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.matcher.MatcherReference;
import org.eclipse.viatra.query.runtime.localsearch.operations.CallOperationHelper.PatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;

/**
 * @author Grill Balázs
 * @since 1.4
 *
 */
public abstract class AbstractPositivePatternCallOperation implements ISearchOperation {

    private final CallOperationHelper helper;
    /**
     * @since 1.5
     */
    protected PatternCall call;
    
    /**
     * @since 1.5
     */
    protected AbstractPositivePatternCallOperation(MatcherReference calledQuery, Map<PParameter, Integer> parameterMapping) {
        helper = new CallOperationHelper(calledQuery, parameterMapping);
    }
    
    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        call = helper.createCall(frame, context);
    }
    
    @Override
    public List<Integer> getVariablePositions() {
        return helper.getVariablePositions();
    }

    @Override
    public String toString() {
        return helper.toString();
    }
    
}
