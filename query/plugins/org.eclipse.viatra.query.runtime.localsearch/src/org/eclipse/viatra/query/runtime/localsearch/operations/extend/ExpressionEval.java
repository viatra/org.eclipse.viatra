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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.MatchingFrameValueProvider;
import org.eclipse.viatra.query.runtime.matchers.psystem.IExpressionEvaluator;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;

/**
 * Calculates the result of an expression and stores it inside a variable for future reference.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class ExpressionEval extends SingleValueExtendOperation<Object> {

    IExpressionEvaluator evaluator;
    Map<String, Integer> nameMap;

    public ExpressionEval(IExpressionEvaluator evaluator, Map<String, Integer> nameMap, int position) {
        super(position);
        this.evaluator = evaluator;
        this.nameMap = nameMap;
    }

    @Override
    public Iterator<?> getIterator(MatchingFrame frame, ISearchContext context) {
        try {
            Object result = evaluator.evaluateExpression(new MatchingFrameValueProvider(frame, nameMap));
            if (result != null){
                return Collections.singletonList(result).iterator();
            } else {
                return Collections.emptyIterator();
            }
        } catch (Exception e) {
            Logger logger = ViatraQueryLoggingUtil.getLogger(getClass());
            logger.warn("Error while evaluating expression", e);
            return Collections.emptyIterator();
        }
    }
    
    @Override
    public String toString() {
        return "extend    -"+position+" = expression "+evaluator.getShortDescription();
    }
    
    
    @Override
    public List<Integer> getVariablePositions() {
        // XXX not sure if this is the correct implementation to get the affected variable indicies
        List<Integer> variables = new ArrayList<>();
        variables.addAll(nameMap.values());
        return variables;
    }
    
}
