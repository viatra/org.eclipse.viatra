/*******************************************************************************
 * Copyright (c) 2010-2014, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *   Denes Harmath - support for multiple scope roots
 *******************************************************************************/
package org.eclipse.incquery.runtime.emf;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.scope.IBaseIndex;
import org.eclipse.incquery.runtime.api.scope.IEngineContext;
import org.eclipse.incquery.runtime.api.scope.IIndexingErrorListener;
import org.eclipse.incquery.runtime.base.api.IncQueryBaseFactory;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.base.exception.IncQueryBaseException;
import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * Implements an engine context on EMF models.
 * @author Bergmann Gabor
 *
 */
class EMFEngineContext implements IEngineContext {

    private final EMFScope emfScope;
    IncQueryEngine engine;
    Logger logger;
    NavigationHelper navHelper;
    IBaseIndex baseIndex;
    IIndexingErrorListener taintListener;
    private EMFPatternMatcherRuntimeContext matcherContext;
    
    public EMFEngineContext(EMFScope emfScope, IncQueryEngine engine, IIndexingErrorListener taintListener, Logger logger) {
        this.emfScope = emfScope;
        this.engine = engine;
        this.logger = logger;
        this.taintListener = taintListener;
    }
    
    public NavigationHelper getNavHelper() throws IncQueryException {
        return getNavHelper(true);
    }
    private NavigationHelper getNavHelper(boolean ensureInitialized) throws IncQueryException {
        if (navHelper == null) {
            try {
                // sync to avoid crazy compiler reordering which would matter if derived features use eIQ and call this
                // reentrantly
                synchronized (this) {
                    navHelper = IncQueryBaseFactory.getInstance().createNavigationHelper(null, this.emfScope.getOptions(),
                            logger);
                    getBaseIndex().addIndexingErrorListener(taintListener);
                }
            } catch (IncQueryBaseException e) {
                throw new IncQueryException("Could not create EMF-IncQuery base index", "Could not create base index",
                        e);
            }

            if (ensureInitialized) {
                ensureIndexLoaded();
            }

        }
        return navHelper;
    }

    private void ensureIndexLoaded() throws IncQueryException {
        try {
            for (Notifier scopeRoot : this.emfScope.getScopeRoots()) {
                navHelper.addRoot(scopeRoot);
            }
        } catch (IncQueryBaseException e) {
            throw new IncQueryException("Could not initialize EMF-IncQuery base index",
                    "Could not initialize base index", e);
        }
    }

    @Override
    public void initializeBackends(IQueryBackendInitializer initializer) throws IncQueryException {
       try {
           if (matcherContext == null) 
               matcherContext = new EMFPatternMatcherRuntimeContext(engine, logger, getNavHelper(false));
           
           initializer.initializeWith(matcherContext);
       } finally {
           // lazy navHelper initialization now,
           ensureIndexLoaded();
       }        
    }   
    
    @Override
    public void dispose() {
        if (matcherContext != null) matcherContext.dispose();
        if (navHelper != null) navHelper.dispose();
        
        this.baseIndex = null;
        this.engine = null;
        this.logger = null;
        this.navHelper = null;
    }
    
    
    @Override
    public IBaseIndex getBaseIndex() throws IncQueryException {
        if (baseIndex == null) {
            final NavigationHelper navigationHelper = getNavHelper();
            baseIndex = new EMFBaseIndexWrapper(navigationHelper);
        }
        return baseIndex;
    }
}