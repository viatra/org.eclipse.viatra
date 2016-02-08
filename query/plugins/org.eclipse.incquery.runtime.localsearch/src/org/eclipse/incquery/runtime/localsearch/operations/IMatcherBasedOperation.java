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
package org.eclipse.incquery.runtime.localsearch.operations;

import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.incquery.runtime.localsearch.matcher.LocalSearchMatcher;

/**
 * A common interface for local search operations that execute by calling an additional matcher
 * 
 * @author Marton Bur
 *
 */
public interface IMatcherBasedOperation {

	/**
	 * Gets the called matcher based on the context and the current adornment and stores it in the corresponding field of the operation
	 * 
	 * @param frame the current matching frame
	 * @param context the search context
	 * @return the called local search matcher
	 */
	LocalSearchMatcher getAndPrepareCalledMatcher(MatchingFrame frame, ISearchContext context);
	
	/**
	 * Gets the called matcher associated with the operation
	 * 
	 * @return the called local search matcher or null if no matcher assigned
	 */
	LocalSearchMatcher getCalledMatcher();
	
}
