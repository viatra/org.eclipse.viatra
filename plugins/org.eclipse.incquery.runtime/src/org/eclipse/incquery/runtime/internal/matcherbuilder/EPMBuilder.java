/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.internal.matcherbuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.incquery.runtime.matchers.IPatternMatcherContext;
import org.eclipse.incquery.runtime.matchers.planning.IOperationCompiler;
import org.eclipse.incquery.runtime.matchers.planning.QueryPlannerException;
import org.eclipse.incquery.runtime.matchers.psystem.PQuery;
import org.eclipse.incquery.runtime.rete.construction.IRetePatternBuilder;
import org.eclipse.incquery.runtime.rete.construction.RetePatternBuildException;
import org.eclipse.incquery.runtime.rete.traceability.RecipeTraceInfo;

/**
 * @author Bergmann Gábor
 *
 */
public class EPMBuilder implements IRetePatternBuilder {
    protected IOperationCompiler operationCompiler;
    protected IPatternMatcherContext context;

    public EPMBuilder(IOperationCompiler operationCompiler, IPatternMatcherContext context) {
        super();
        this.operationCompiler = operationCompiler;
        this.context = context;
    }

    @Override
    public IPatternMatcherContext getContext() {
        return context;
    }

    @Override
    public void refresh() {
        operationCompiler.reinitialize();
    }

    @Override
    public RecipeTraceInfo construct(PQuery pattern) throws QueryPlannerException {
        try {
            EPMBuildScaffold epmBuildScaffold = new EPMBuildScaffold(operationCompiler, context);
            return epmBuildScaffold.construct(pattern);
        } catch (RuntimeException ex) {
            throw new RetePatternBuildException(
                    "Error during constructing Rete pattern matcher; please review Error Log and consult developers",
                    new String[0], "Error during pattern matcher construction", pattern, ex);
        }
    }

    @Override
    public Map<String, Integer> getPosMapping(PQuery query) {
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        List<String> parameters = query.getParameterNames();
        for (int i = 0; i < parameters.size(); ++i)
            result.put(parameters.get(i), i);
        return result;
    }
}
