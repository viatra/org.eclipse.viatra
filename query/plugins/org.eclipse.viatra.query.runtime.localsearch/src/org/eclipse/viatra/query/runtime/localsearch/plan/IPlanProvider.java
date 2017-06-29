/*******************************************************************************
 * Copyright (c) 2010-2016, Grill Balázs, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.plan;

import org.eclipse.viatra.query.runtime.localsearch.matcher.MatcherReference;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchHints;
import org.eclipse.viatra.query.runtime.localsearch.planner.compiler.IOperationCompiler;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryBackendContext;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;

/**
 * @author Grill Balázs
 * @since 1.4
 * @noreference This interface is not intended to be referenced by clients.
 */
public interface IPlanProvider {

    /**
     * @since 1.7
     */
    public IPlanDescriptor getPlan(IQueryBackendContext backend, IOperationCompiler compiler, LocalSearchHints configuration, MatcherReference key) throws QueryProcessingException;
    
}
