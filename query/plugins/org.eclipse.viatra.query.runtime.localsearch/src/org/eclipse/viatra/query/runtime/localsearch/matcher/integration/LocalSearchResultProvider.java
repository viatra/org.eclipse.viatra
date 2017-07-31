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
import org.eclipse.viatra.query.runtime.localsearch.planner.compiler.EMFOperationCompiler;
import org.eclipse.viatra.query.runtime.localsearch.planner.compiler.IOperationCompiler;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryBackendContext;
import org.eclipse.viatra.query.runtime.matchers.context.IndexingService;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

/**
 * @author Marton Bur, Zoltan Ujhelyi
 *
 */
public class LocalSearchResultProvider extends AbstractLocalSearchResultProvider {

    /**
     * @throws QueryProcessingException
     * @since 1.5
     */
    public LocalSearchResultProvider(LocalSearchBackend backend, IQueryBackendContext context, PQuery query,
            IPlanProvider planProvider) throws QueryProcessingException {
        this(backend, context, query, planProvider, null);
    }

    /**
     * @throws QueryProcessingException
     * @since 1.5
     */
    public LocalSearchResultProvider(LocalSearchBackend backend, IQueryBackendContext context, PQuery query,
            IPlanProvider planProvider, QueryEvaluationHint userHints) throws QueryProcessingException {
        super(backend, context, query, planProvider, userHints);
    }

    @Override
    protected void indexInitializationBeforePlanning() throws QueryProcessingException {
        super.indexInitializationBeforePlanning();
        
        indexReferredTypesOfQuery(query, IndexingService.STATISTICS);
    }

    @Override
    protected IOperationCompiler getOperationCompiler(IQueryBackendContext backendContext,
            LocalSearchHints configuration) {
        return new EMFOperationCompiler(runtimeContext, configuration.isUseBase());
    }
}
