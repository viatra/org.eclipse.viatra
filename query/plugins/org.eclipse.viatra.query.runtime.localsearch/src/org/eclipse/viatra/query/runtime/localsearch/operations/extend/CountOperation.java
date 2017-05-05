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
package org.eclipse.viatra.query.runtime.localsearch.operations.extend;

import java.util.List;
import java.util.Map;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.matcher.MatcherReference;
import org.eclipse.viatra.query.runtime.localsearch.operations.CallOperationHelper;
import org.eclipse.viatra.query.runtime.localsearch.operations.CallOperationHelper.PatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;

import com.google.common.collect.Iterators;

/**
 * Calculates the count of matches for a called matcher
 * 
 * @author Zoltan Ujhelyi
 */
public class CountOperation extends ExtendOperation<Integer>{

    private final CallOperationHelper helper;
    private PatternCall call;

    
    /**
     * @since 1.5
     */
    public CountOperation(MatcherReference calledQuery, Map<PParameter, Integer> parameterMapping, int position) {
        super(position);
        helper = new CallOperationHelper(calledQuery, parameterMapping);
    }

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        call = helper.createCall(frame, context);
        it = Iterators.singletonIterator(call.count(frame));
        
    }
    
    @Override
    public List<Integer> getVariablePositions() {
        return helper.getVariablePositions();
    }

    @Override
    public String toString() {
        return "extend    -"+position+" = count find "+helper.toString();
    }
    
}
