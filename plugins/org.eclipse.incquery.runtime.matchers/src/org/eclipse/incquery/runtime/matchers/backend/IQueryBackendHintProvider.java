/*******************************************************************************
 * Copyright (c) 2010-2015, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.matchers.backend;

import java.util.Collections;
import java.util.Map;

import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;

/**
 * Provides query evaluation hints to evaluation backends. 
 * May override hints embedded in query itself.
 * 
 * @author Bergmann Gabor
 *
 */
public interface IQueryBackendHintProvider {
	
	/**
	 * Suggests query evaluation hints regarding a query. 
	 * @return non-null map of option keys and values
	 */
	Map<String, Object> getHints(PQuery query);

	
	/**
	 * A default implementation that just returns the hints embedded in the query, without overriding.
	 */
	public static final IQueryBackendHintProvider DEFAULT = new IQueryBackendHintProvider() {
		
		@Override
		public Map<String, Object> getHints(PQuery query) {
			final QueryEvaluationHint embeddedHints = query.getEvaluationHints();
			if (embeddedHints != null && embeddedHints.getBackendHints() != null)
				return embeddedHints.getBackendHints();
			else return Collections.emptyMap();
		}
	};
}
