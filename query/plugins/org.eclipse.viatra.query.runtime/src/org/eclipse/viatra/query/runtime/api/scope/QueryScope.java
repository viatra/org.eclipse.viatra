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
package org.eclipse.viatra.query.runtime.api.scope;

import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.internal.apiimpl.EngineContextFactory;

/**
 * Defines a scope for a VIATRA Query engine, which determines the set of model elements that query evaluation operates on.
 * 
 * @author Bergmann Gabor
 *
 */
public abstract class QueryScope extends EngineContextFactory {
	
	/**
	 * Determines whether a query engine initialized on this scope can evaluate queries formulated against the given scope type.
	 * <p> Every query scope class is compatible with a query engine initialized on a scope of the same class or a subclass.
	 * @param queryScopeClass the scope class returned by invoking {@link IQuerySpecification#getPreferredScopeClass()} on a query specification
	 * @return true if an {@link ViatraQueryEngine} initialized on this scope can consume an {@link IQuerySpecification}
	 */
	public boolean isCompatibleWithQueryScope(Class<? extends QueryScope> queryScopeClass) {
		return queryScopeClass.isAssignableFrom(this.getClass());
	}

}
