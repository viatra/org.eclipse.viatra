/*******************************************************************************
 * Copyright (c) 2010-2015, Marton Bur, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.matcher.integration;

import org.eclipse.viatra.query.runtime.localsearch.plan.IPlanProvider;
import org.eclipse.viatra.query.runtime.localsearch.planner.compiler.GenericOperationCompiler;
import org.eclipse.viatra.query.runtime.localsearch.planner.compiler.IOperationCompiler;
import org.eclipse.viatra.query.runtime.matchers.ViatraQueryRuntimeException;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryBackendContext;
import org.eclipse.viatra.query.runtime.matchers.context.IndexingService;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

/**
 * @author Zoltan Ujhelyi
 * @since 1.7
 *
 */
public class GenericLocalSearchResultProvider extends AbstractLocalSearchResultProvider {

    /**
     * @throws ViatraQueryRuntimeException
     */
    public GenericLocalSearchResultProvider(LocalSearchBackend backend, IQueryBackendContext context, PQuery query,
            IPlanProvider planProvider, QueryEvaluationHint userHints) {
        super(backend, context, query, planProvider, userHints);
    }

    @Override
    protected void indexInitializationBeforePlanning() {
        super.indexInitializationBeforePlanning();
        
        indexReferredTypesOfQuery(query, IndexingService.INSTANCES);
        indexReferredTypesOfQuery(query, IndexingService.STATISTICS);
    }

    @Override
    protected IOperationCompiler getOperationCompiler(IQueryBackendContext backendContext,
            LocalSearchHints configuration) {
        return new GenericOperationCompiler(runtimeContext);
    }

}
