/*******************************************************************************
 * Copyright (c) 2010-2015, Marton Bur, Zoltan Ujhelyi, Akos Horvath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.operations;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.matcher.LocalSearchMatcher;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;

/**
 * A common interface for local search operations that execute by calling an additional matcher
 * 
 * @author Marton Bur
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IMatcherBasedOperation {

	/**
	 * Gets the called matcher based on the context and the current adornment and stores it in the corresponding field of the operation
	 * 
	 * @param frame the current matching frame
	 * @param context the search context
	 * @return the called local search matcher
	 * @throws LocalSearchException 
	 * @since 1.5
	 */
	IQueryResultProvider getAndPrepareCalledMatcher(MatchingFrame frame, ISearchContext context) throws LocalSearchException;
	
	/**
	 * Gets the called matcher associated with the operation
	 * 
	 * @return the called local search matcher or null if no matcher assigned
	 * @since 1.5
	 */
	IQueryResultProvider getCalledMatcher();
	
}
