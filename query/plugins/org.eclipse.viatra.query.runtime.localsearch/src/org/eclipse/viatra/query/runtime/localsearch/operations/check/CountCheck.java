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
import org.eclipse.viatra.query.runtime.localsearch.operations.PatternCallHelper;
import org.eclipse.viatra.query.runtime.localsearch.operations.PatternCallHelper.PatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

import com.google.common.collect.Lists;

/**
 * Calculates the count of matches for a called matcher
 * 
 * @author Zoltan Ujhelyi
 */
public class CountCheck extends CheckOperation{

    private final PatternCallHelper helper;
    private PatternCall call;
    private int position;
    
    public CountCheck(PQuery calledQuery, Map<Integer, PParameter> parameterMapping, int position) {
        super();
        helper = new PatternCallHelper(calledQuery, parameterMapping);
        this.position = position;
    }

    @Override
	public void onInitialize(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
		super.onInitialize(frame, context);
		call = helper.createCall(frame, context);
	}

    @Override
    protected boolean check(MatchingFrame frame) throws LocalSearchException {
        int count = call.count(frame);
        return ((Integer)frame.getValue(position)) == count;
    }

    @Override
	public List<Integer> getVariablePositions() {
		return Lists.asList(position, new Integer[0]);
	}
    
    @Override
    public String toString() {
    	StringBuilder builder = new StringBuilder();
    	builder.append("Check count ")
    		.append(helper.toString())
    		.append("for position ").append(position);
    	return builder.toString();
    }

    
}
