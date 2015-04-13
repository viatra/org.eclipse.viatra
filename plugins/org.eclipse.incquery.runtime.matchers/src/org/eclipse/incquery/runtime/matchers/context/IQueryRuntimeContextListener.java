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
package org.eclipse.incquery.runtime.matchers.context;

import org.eclipse.incquery.runtime.matchers.tuple.Tuple;

/**
 * Listens for changes in the runtime context.
 * @author Bergmann Gabor
 *
 */
public interface IQueryRuntimeContextListener {
	
	/**
	 * The given tuple was inserted into or removed from the input relation indicated by the given key.
	 * @param key the key identifying the input relation that was updated
	 * @param updateTuple the tuple that was inserted or removed
	 * @param isInsertion true if it was an insertion, false otherwise.
	 */
	public void update(IInputKey key, Tuple updateTuple, boolean isInsertion);
}
