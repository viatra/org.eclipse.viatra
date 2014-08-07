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
package org.eclipse.incquery.runtime.api.scope;

import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.rete.matcher.IPatternMatcherRuntimeContext;

/**
 * The context of the engine is instantiated by the scope, 
 * and provides information and services regarding the model the towards the engine.  
 * 
 * @author Bergmann Gabor
 *
 */
public interface IEngineContext {
	/**
	 * Returns the runtime context for the query evaluator backends.
	 */
	IPatternMatcherRuntimeContext getRuntimeContext();
	
	/**
	 * Returns the base index. 
	 */	
	IBaseIndex getBaseIndex();
	
	/**
	 * Invokes the given code. 
	 * 
	 * If the base index has not yet been initialized, 
	 * it will be only loaded with content after the callback, 
	 * but it will be guaranteed to exist during it.
	 * @throws IncQueryException 
	 */
	void withoutBaseIndexInitializationDo(Runnable runnable) throws IncQueryException;

	/**
	 * Disposes this context object. Resources in the index may now be freed up.
	 * No more methods should be called after this one.
	 * 
     * @throws IllegalStateException if there are any active listeners to the underlying index
	 */
	void dispose();
}