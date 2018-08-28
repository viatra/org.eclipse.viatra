/*******************************************************************************
 * Copyright (c) 2010-2018, Gabor Bergmann, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.tabular;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.scope.*;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author Gabor Bergmann
 * 
 * @since 2.1
 */
class TabularEngineContext implements IEngineContext, IBaseIndex {

	private TabularIndexHost indexHost;
	private ViatraQueryEngine engine;
	private Logger logger;
	
	private List<IIndexingErrorListener> errorListeners = CollectionsFactory.createObserverList();

	public TabularEngineContext(TabularIndexHost server, ViatraQueryEngine engine,
			IIndexingErrorListener errorListener, Logger logger) {
				this.indexHost = server;
				this.engine = engine;
				this.logger = logger;
				
				this.addIndexingErrorListener(errorListener);
	}

	@Override
	public IBaseIndex getBaseIndex() {
		return this;
	}

	@Override
	public void dispose() {
		// NOP, server lifecycle not controlled by engine
	}

	@Override
	public IQueryRuntimeContext getQueryRuntimeContext() {
		return indexHost.getRuntimeContext();
	}

	@Override
	public <V> V coalesceTraversals(Callable<V> callable) throws InvocationTargetException {
		try {
			return callable.call();
		} catch (Exception e) {
			throw new InvocationTargetException(e);
		}
	}

	@Override
	public void addBaseIndexChangeListener(ViatraBaseIndexChangeListener listener) {
		// TODO no notifications yet
	}

	@Override
	public void removeBaseIndexChangeListener(ViatraBaseIndexChangeListener listener) {
		// TODO no notifications yet
	}

	@Override
	public boolean addInstanceObserver(IInstanceObserver observer, Object observedObject) {
		// TODO no notifications yet
		return true;
	}

	@Override
	public boolean removeInstanceObserver(IInstanceObserver observer, Object observedObject) {
		// TODO no notifications yet
		return true;
	}

	@Override
	public void resampleDerivedFeatures() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addIndexingErrorListener(IIndexingErrorListener listener) {
		return errorListeners.add(listener);
	}

	@Override
	public boolean removeIndexingErrorListener(IIndexingErrorListener listener) {
		return errorListeners.remove(listener);
	}
	
	

}
