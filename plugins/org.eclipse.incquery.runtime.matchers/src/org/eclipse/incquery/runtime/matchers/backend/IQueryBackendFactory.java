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
package org.eclipse.incquery.runtime.matchers.backend;

import org.eclipse.incquery.runtime.matchers.context.IPatternMatcherRuntimeContext;
import org.eclipse.incquery.runtime.matchers.context.IQueryCacheContext;
import org.eclipse.incquery.runtime.matchers.context.IQueryRuntimeContext;

/**
 * Factory for instantiating {@link IQueryBackend}.
 * @author Bergmann Gabor
 *
 */
public interface IQueryBackendFactory {
	public IQueryBackend 
		create(IPatternMatcherRuntimeContext matcherContext,
				IQueryRuntimeContext runtimeContext,
				IQueryCacheContext queryCacheContext,
				IQueryBackendHintProvider hintProvider);

}
