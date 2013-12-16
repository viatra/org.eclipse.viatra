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
package org.eclipse.incquery.runtime.matchers.psystem.basicdeferred;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.runtime.matchers.psystem.IExpressionEvaluator;
import org.eclipse.incquery.runtime.matchers.psystem.PSystem;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class ExpressionEvaluation extends BaseTypeSafeConstraint {

    private IExpressionEvaluator evaluator;

    public ExpressionEvaluation(PSystem pSystem, IExpressionEvaluator evaluator, PVariable outputVariable) {
        super(pSystem, getPVariablesOfExpression(pSystem, evaluator), outputVariable);
        this.evaluator = evaluator;
    }
    
    /**
     * @return the evaluator
     */
    public IExpressionEvaluator getEvaluator() {
        return evaluator;
    }

    @Override
    protected String toStringRest() {
        return new FlatTuple(new ArrayList<PVariable>(inputVariables).toArray()).toString() + "|="
                + evaluator.getShortDescription();
    }

    @Override
    public Map<Set<PVariable>, Set<PVariable>> getFunctionalDependencies() {
        if (outputVariable == null) 
            return Collections.emptyMap();
        else 
            return Collections.singletonMap(inputVariables, Collections.singleton(outputVariable));
    }
    
    private static Set<PVariable> getPVariablesOfExpression(PSystem pSystem,
            IExpressionEvaluator evaluator) {
        Set<PVariable> result = new HashSet<PVariable>();
        for (String name : evaluator.getInputParameterNames()) {
            PVariable variable = pSystem.getOrCreateVariableByName(name);
            result.add(variable);
        }
        return result;
    }
}
