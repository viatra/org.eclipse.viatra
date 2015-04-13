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
package org.eclipse.incquery.runtime.localsearch.matcher.integration;

import org.eclipse.incquery.runtime.matchers.backend.IQueryBackend;
import org.eclipse.incquery.runtime.matchers.backend.IQueryBackendHintProvider;
import org.eclipse.incquery.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.incquery.runtime.matchers.context.IPatternMatcherRuntimeContext;
import org.eclipse.incquery.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;

/**
 * @author Marton Bur, Zoltan Ujhelyi
 *
 */
public class LocalSearchBackend implements IQueryBackend {

    IPatternMatcherRuntimeContext matcherContext;
    IQueryBackendHintProvider hintProvider;
    
    public LocalSearchBackend(IPatternMatcherRuntimeContext context, IQueryBackendHintProvider hintProvider) {
        super();
        this.matcherContext = context;
        this.hintProvider = hintProvider;
    }

    @Override
    public IQueryResultProvider getResultProvider(PQuery query) throws QueryProcessingException {
        //TODO caching
        return new LocalSearchResultProvider(this, matcherContext, hintProvider, query);
    }

    @Override
    public void dispose() {        
    }

	@Override
	public boolean isCaching() {
		return false;
	}

}
