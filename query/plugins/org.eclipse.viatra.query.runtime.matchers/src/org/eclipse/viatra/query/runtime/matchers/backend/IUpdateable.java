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

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

/**
 * Internal interface for the query backend to singal an update to a query result.
 * @author Bergmann Gabor
 * @since 0.9
 *
 */
public interface IUpdateable {
	
	/**
	 * This callback method must be free of exceptions, even {@link RuntimeException}s (though not {@link Error}s).
	 * @param updateElement the tuple that is changed
	 * @param isInsertion true if the tuple appeared in the result set, false if disappeared from the result set
	 */
	public void update(Tuple updateElement, boolean isInsertion);
}
