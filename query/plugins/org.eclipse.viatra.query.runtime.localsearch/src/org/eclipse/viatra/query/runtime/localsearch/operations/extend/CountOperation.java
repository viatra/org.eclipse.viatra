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
import org.eclipse.viatra.query.runtime.localsearch.operations.PatternCallHelper;
import org.eclipse.viatra.query.runtime.localsearch.operations.PatternCallHelper.PatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

import com.google.common.collect.Iterators;

/**
 * Calculates the count of matches for a called matcher
 * 
 * @author Zoltan Ujhelyi
 */
public class CountOperation extends ExtendOperation<Integer>{

    private final PatternCallHelper helper;
    private PatternCall call;

	
    public CountOperation(PQuery calledQuery, Map<Integer, PParameter> parameterMapping, int position) {
        super(position);
        helper = new PatternCallHelper(calledQuery, parameterMapping);
    }

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        call = helper.createCall(frame, context);
        it = Iterators.singletonIterator(call.count(frame));
        
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Extend count ")
            .append(helper.toString())
            .append("for position ").append(position);
        return builder.toString();
    }
    
    @Override
	public List<Integer> getVariablePositions() {
    	return helper.getVariablePositions();
	}


    
}
