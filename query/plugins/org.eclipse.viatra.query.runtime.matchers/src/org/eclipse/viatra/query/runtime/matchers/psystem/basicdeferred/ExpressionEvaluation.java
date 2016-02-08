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
package org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.viatra.query.runtime.matchers.psystem.IExpressionEvaluator;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.tuple.FlatTuple;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class ExpressionEvaluation extends BaseTypeSafeConstraint {

    private IExpressionEvaluator evaluator;

    public ExpressionEvaluation(PBody pBody, IExpressionEvaluator evaluator, PVariable outputVariable) {
        super(pBody, getPVariablesOfExpression(pBody, evaluator), outputVariable);
        this.evaluator = evaluator;
    }
    
    public IExpressionEvaluator getEvaluator() {
        return evaluator;
    }

    @Override
    protected String toStringRest() {
        return new FlatTuple(new ArrayList<PVariable>(inputVariables).toArray()).toString() + "|="
                + evaluator.getShortDescription();
    }

    @Override
    public Map<Set<PVariable>, Set<PVariable>> getFunctionalDependencies(IQueryMetaContext context) {
        if (outputVariable == null) 
            return Collections.emptyMap();
        else 
            return Collections.singletonMap(inputVariables, Collections.singleton(outputVariable));
    }
    
    private static Set<PVariable> getPVariablesOfExpression(PBody pBody,
            IExpressionEvaluator evaluator) {
        Set<PVariable> result = new HashSet<PVariable>();
        for (String name : evaluator.getInputParameterNames()) {
            PVariable variable = pBody.getOrCreateVariableByName(name);
            result.add(variable);
        }
        return result;
    }
}
