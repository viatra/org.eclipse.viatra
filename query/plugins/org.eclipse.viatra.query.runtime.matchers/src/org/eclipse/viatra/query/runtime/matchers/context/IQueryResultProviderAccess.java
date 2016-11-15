/*******************************************************************************
 * Copyright (c) 2010-2016, Grill Balázs, IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.context;

import org.eclipse.viatra.query.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

/**
 * This interface exposes API to request {@link IQueryResultProvider} for {@link PQuery} instances.
 * 
 * @author Grill Balázs
 * @since 1.5
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IQueryResultProviderAccess {

    /**
     * Get a result provider for the given {@link PQuery}, which conforms the capabilities requested by the
     * given {@link QueryEvaluationHint} object.
     * @throws QueryProcessingException 
     * @throws ViatraQueryException 
     */
    public IQueryResultProvider getResultProvider(PQuery query, QueryEvaluationHint overrideHints) throws QueryProcessingException;
    
}
