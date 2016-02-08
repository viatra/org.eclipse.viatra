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
package org.eclipse.viatra.query.runtime.internal.apiimpl;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.runtime.api.IncQueryEngine;
import org.eclipse.viatra.query.runtime.api.scope.IEngineContext;
import org.eclipse.viatra.query.runtime.api.scope.IIndexingErrorListener;

/**
 * Internal interface for a Scope to reveal model contents to the engine.
 * 
 * @author Bergmann Gabor
 *
 */
public abstract class EngineContextFactory {
	protected abstract IEngineContext createEngineContext(IncQueryEngine engine, IIndexingErrorListener errorListener, Logger logger);
}
