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
import org.eclipse.viatra.query.runtime.localsearch.operations.MatchingFrameValueProvider;
import org.eclipse.viatra.query.runtime.matchers.psystem.IExpressionEvaluator;

import com.google.common.collect.Lists;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class ExpressionCheck extends CheckOperation {

    IExpressionEvaluator evaluator;
    Map<String, Integer> nameMap;

    public ExpressionCheck(IExpressionEvaluator evaluator, Map<String, Integer> nameMap) {
        super();
        this.evaluator = evaluator;
        this.nameMap = nameMap;
    }

    @Override
    protected boolean check(MatchingFrame frame) throws LocalSearchException {
        try {
            boolean result = (Boolean) evaluator.evaluateExpression(new MatchingFrameValueProvider(frame, nameMap));
            return result;
        } catch (Exception e) {
            throw new LocalSearchException("Error while evaluating expression", e);
        }
    }

    @Override
	public List<Integer> getVariablePositions() {
    	// XXX not sure if this is the correct implementation to get the affected variable indicies
    	List<Integer> variables = Lists.newArrayList();
    	variables.addAll(nameMap.values());
		return variables;
	}
    
    @Override
    public String toString() {
    	return "Expression check";
    }
    
}
