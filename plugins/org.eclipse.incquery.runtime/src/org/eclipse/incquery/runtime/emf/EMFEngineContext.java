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
package org.eclipse.incquery.runtime.emf;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.scope.IBaseIndex;
import org.eclipse.incquery.runtime.api.scope.IEngineContext;
import org.eclipse.incquery.runtime.base.api.IIndexingErrorListener;
import org.eclipse.incquery.runtime.base.api.IncQueryBaseFactory;
import org.eclipse.incquery.runtime.base.api.IncQueryBaseIndexChangeListener;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.base.exception.IncQueryBaseException;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.rete.matcher.IPatternMatcherRuntimeContext;

class EMFEngineContext implements IEngineContext {
	/**
	 * 
	 */
	private final EMFScope emfScope;
	IncQueryEngine engine;
	Logger logger;
	NavigationHelper navHelper;
	IBaseIndex baseIndex;
	
	
	public EMFEngineContext(EMFScope emfScope, IncQueryEngine engine, Logger logger) {
		this.emfScope = emfScope;
		this.engine = engine;
		this.logger = logger;
	}
	
	@Override
	public IPatternMatcherRuntimeContext getRuntimeContext() {
		return new EMFPatternMatcherRuntimeContext(engine, logger, getNavHelper());
	}
	
	public NavigationHelper getNavHelper() {
		return getNavHelper(true);
	}
	private NavigationHelper getNavHelper(boolean ensureInitialized) {
        if (navHelper == null) {
            try {
                // sync to avoid crazy compiler reordering which would matter if derived features use eIQ and call this
                // reentrantly
                synchronized (this) {
                	navHelper = IncQueryBaseFactory.getInstance().createNavigationHelper(null, this.emfScope.options,
                            logger);
                	navHelper.addIndexingErrorListener(taintListener);
                }
            } catch (IncQueryBaseException e) {
                throw new IncQueryException("Could not create EMF-IncQuery base index", "Could not create base index",
                        e);
            }

            if (ensureInitialized) {
                ensureInitialized();
            }

        }
        return navHelper;
	}

	private void ensureInitialized() throws IncQueryException {
        try {
            navHelper.addRoot(this.emfScope.scopeRoot);
        } catch (IncQueryBaseException e) {
            throw new IncQueryException("Could not initialize EMF-IncQuery base index",
                    "Could not initialize base index", e);
        }
	}

	@Override
	public void withoutBaseIndexInitializationDo(Runnable runnable) throws IncQueryException {
        // if uninitialized, don't initialize yet
		getNavHelper(false);

		runnable.run();
		
        // lazy initialization now,
        ensureInitialized();
	}
	
	@Override
	public void dispose() {
		if (navHelper != null) navHelper.dispose();
		
		this.baseIndex = null;
		this.engine = null;
		this.logger = null;
		this.navHelper = null;
	}
	
	
	@Override
	public IBaseIndex getBaseIndex() {
		if (baseIndex == null) {
			baseIndex = new IBaseIndex() {
				@Override
				public void resampleDerivedFeatures() {
					getNavHelper().resampleDerivedFeatures();
				}
				@Override
				public boolean removeIndexingErrorListener(IIndexingErrorListener listener) {
					return getNavHelper().removeIndexingErrorListener(listener);
				}
				@Override
				public void removeBaseIndexChangeListener(
						IncQueryBaseIndexChangeListener listener) {
					getNavHelper().removeBaseIndexChangeListener(listener);
				}
				@Override
				public <V> V coalesceTraversals(Callable<V> callable) throws InvocationTargetException {
					return getNavHelper().coalesceTraversals(callable);
				}
				@Override
				public boolean addIndexingErrorListener(IIndexingErrorListener listener) {
					return getNavHelper().addIndexingErrorListener(listener);
				}
				@Override
				public void addBaseIndexChangeListener(
						IncQueryBaseIndexChangeListener listener) {
					getNavHelper().addBaseIndexChangeListener(listener);
				}
			};
		}
		return null;
	}
}