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

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.MatchingFrameValueProvider;
import org.eclipse.viatra.query.runtime.matchers.psystem.IExpressionEvaluator;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;

import com.google.common.collect.Lists;

/**
 * @author Grill Balázs
 * @since 1.3
 * @noextend This class is not intended to be subclassed by clients.
 */
public class ExpressionEvalCheck extends CheckOperation {

    private final int outputPosition;
    private final IExpressionEvaluator evaluator;
    private final Map<String, Integer> nameMap;
    
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
    protected boolean check(MatchingFrame frame, ISearchContext context) {
        try {
            Object result = evaluator.evaluateExpression(new MatchingFrameValueProvider(frame, nameMap));
            if (result != null) {
                Object currentValue = frame.get(outputPosition);
                return result.equals(currentValue);
            }
        } catch (Exception e) {
            Logger logger = ViatraQueryLoggingUtil.getLogger(getClass());
            logger.warn("Error while evaluating expression", e);
        }
        return false;
    }

    @Override
    public String toString() {
        return "check     "+outputPosition+" = expression "+evaluator.getShortDescription();
    }
}
