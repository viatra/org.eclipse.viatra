/*******************************************************************************
 * Copyright (c) 2010-2015, Marton Bur, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.matcher.integration;

import org.eclipse.viatra.query.runtime.localsearch.plan.IPlanProvider;
import org.eclipse.viatra.query.runtime.localsearch.planner.compiler.GenericOperationCompiler;
import org.eclipse.viatra.query.runtime.localsearch.planner.compiler.IOperationCompiler;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryBackendContext;
import org.eclipse.viatra.query.runtime.matchers.context.IndexingService;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

/**
 * @author Zoltan Ujhelyi
 * @since 1.7
 *
 */
public class GenericLocalSearchResultProvider extends AbstractLocalSearchResultProvider {

    public GenericLocalSearchResultProvider(LocalSearchBackend backend, IQueryBackendContext context, PQuery query,
            IPlanProvider planProvider, QueryEvaluationHint userHints) throws QueryProcessingException {
        super(backend, context, query, planProvider, userHints);
    }

    @Override
    protected void indexInitializationBeforePlanning() throws QueryProcessingException {
        super.indexInitializationBeforePlanning();
        
        indexReferredTypesOfQuery(query, IndexingService.INSTANCES);
    }

    @Override
    protected IOperationCompiler getOperationCompiler(IQueryBackendContext backendContext,
            LocalSearchHints configuration) {
        return new GenericOperationCompiler(runtimeContext);
    }

}
