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
package org.eclipse.viatra.query.runtime.localsearch.operations.check;

import java.util.List;
import java.util.Map;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.matcher.MatcherReference;
import org.eclipse.viatra.query.runtime.localsearch.operations.CallOperationHelper;
import org.eclipse.viatra.query.runtime.localsearch.operations.IPatternMatcherOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.CallOperationHelper.PatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;

/**
 * @author Zoltan Ujhelyi
 * @noextend This class is not intended to be subclassed by clients.
 */
public class NACOperation extends CheckOperation implements IPatternMatcherOperation {

    CallOperationHelper helper;
    PatternCall call;

    /**
     * @since 1.5
     */
    public NACOperation(MatcherReference calledQuery, Map<PParameter, Integer> parameterMapping) {
        super();
        helper = new CallOperationHelper(calledQuery, parameterMapping);
    }

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        super.onInitialize(frame, context);
        call = helper.createCall(context);
    }

    @Override
    protected boolean check(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        return !call.has(frame);
    }
    
    
    @Override
    public String toString() {
        return "check     neg find "+helper.toString();
    }
    
    @Override
    public List<Integer> getVariablePositions() {
        return helper.getVariablePositions();
    }


}
