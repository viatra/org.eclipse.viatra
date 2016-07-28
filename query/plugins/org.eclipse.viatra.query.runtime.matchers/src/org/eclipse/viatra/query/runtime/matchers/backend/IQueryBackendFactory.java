/*******************************************************************************
 * Copyright (c) 2010-2014, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.backend;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryCacheContext;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

/**
 * A Query Backend Factory identifies a query evaluator implementation, and can create an evaluator instance (an {@link IQueryBackend}) tied to a specific VIATRA Query engine upon request. 
 * 
 * <p> The factory is used as a lookup key for the backend instance, 
 *   therefore implementors should either be singletons, or implement equals() / hashCode() accordingly. 
 * 
 * @author Bergmann Gabor
 *
 */
public interface IQueryBackendFactory {
	/**
	 * Creates a new {@link IQueryBackend} instance tied to the given context elements. 
	 * 
	 * @return an instance of the class returned by {@link #getBackendClass()} that operates in the given context.
	 */
	public IQueryBackend 
		create(Logger logger,
				IQueryRuntimeContext runtimeContext,
				IQueryCacheContext queryCacheContext,
				IQueryBackendHintProvider hintProvider);
	
	
	/**
	 * The backend instances created by this factory are guaranteed to conform to the returned class.
	 */
	public Class<? extends IQueryBackend> getBackendClass();

	/**
	 * Calculate the required capabilities, which are needed to execute the given pattern
	 * 
	 * @since 1.4
	 */
	public IMatcherCapability calculateRequiredCapability(PQuery query, QueryEvaluationHint hint);
	
}
