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
import org.eclipse.viatra.query.runtime.localsearch.operations.MatchingFrameValueProvider;
import org.eclipse.viatra.query.runtime.matchers.psystem.IExpressionEvaluator;

import com.google.common.collect.Lists;

/**
 * @author Grill Balázs
 * @since 1.3
 *
 */
public class ExpressionEvalCheck extends CheckOperation {

    private final int outputPosition;
    private final IExpressionEvaluator evaluator;
    private final Map<String, Integer> nameMap;
    
    /**
     * 
     */
    public ExpressionEvalCheck(IExpressionEvaluator evaluator, Map<String, Integer> nameMap, int position) {
        this.evaluator = evaluator;
        this.nameMap = nameMap;
        this.outputPosition = position;
    }

    @Override
    public List<Integer> getVariablePositions() {
        // XXX not sure if this is the correct implementation to get the affected variable indicies
        List<Integer> variables = Lists.newArrayList();
        variables.addAll(nameMap.values());
        return variables;
    }

    @Override
    protected boolean check(MatchingFrame frame) throws LocalSearchException {
        try {
            Object result = evaluator.evaluateExpression(new MatchingFrameValueProvider(frame, nameMap));
            if (result != null){
                Object currentValue = frame.get(outputPosition);
                return result.equals(currentValue);
            }else{
                return false;
            }
        } catch (Exception e) {
            throw new LocalSearchException("Error while evaluating expression", e);
        }
    }

    @Override
    public String toString() {
        return String.format("Expression check %s for position %d", evaluator.getShortDescription(), outputPosition);
    }
}
