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

/**
 * @author Zoltan Ujhelyi
 *
 */
public class NACOperation extends CheckOperation {

    PatternCallHelper helper;
    PatternCall call;

    public NACOperation(PQuery calledQuery, Map<Integer, PParameter> parameterMapping) {
        super();
        helper = new PatternCallHelper(calledQuery, parameterMapping);
    }

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        super.onInitialize(frame, context);
        call = helper.createCall(frame, context);
    }

    @Override
    protected boolean check(MatchingFrame frame) throws LocalSearchException {
        return !call.has(frame);
    }
    
    
    @Override
    public String toString() {
        return "NAC check "+helper.toString();
    }
    
    @Override
	public List<Integer> getVariablePositions() {
    	return helper.getVariablePositions();
	}


}
