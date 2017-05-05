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
package org.eclipse.viatra.query.runtime.emf;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.scope.IBaseIndex;
import org.eclipse.viatra.query.runtime.api.scope.IEngineContext;
import org.eclipse.viatra.query.runtime.api.scope.IIndexingErrorListener;
import org.eclipse.viatra.query.runtime.base.api.ViatraBaseFactory;
import org.eclipse.viatra.query.runtime.base.api.NavigationHelper;
import org.eclipse.viatra.query.runtime.base.exception.ViatraBaseException;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;

/**
 * Implements an engine context on EMF models.
 * @author Bergmann Gabor
 *
 */
class EMFEngineContext implements IEngineContext {

    private final EMFScope emfScope;
    ViatraQueryEngine engine;
    Logger logger;
    NavigationHelper navHelper;
    IBaseIndex baseIndex;
    IIndexingErrorListener taintListener;
    private EMFQueryRuntimeContext runtimeContext;
    
    public EMFEngineContext(EMFScope emfScope, ViatraQueryEngine engine, IIndexingErrorListener taintListener, Logger logger) {
        this.emfScope = emfScope;
        this.engine = engine;
        this.logger = logger;
        this.taintListener = taintListener;
    }
    
    public NavigationHelper getNavHelper() throws ViatraQueryException {
        return getNavHelper(true);
    }
    private NavigationHelper getNavHelper(boolean ensureInitialized) throws ViatraQueryException {
        if (navHelper == null) {
            try {
                // sync to avoid crazy compiler reordering which would matter if derived features use VIATRA and call this
                // reentrantly
                synchronized (this) {
                    navHelper = ViatraBaseFactory.getInstance().createNavigationHelper(null, this.emfScope.getOptions(),
                            logger);
                    getBaseIndex().addIndexingErrorListener(taintListener);
                }
            } catch (ViatraBaseException e) {
                throw new ViatraQueryException("Could not create VIATRA Base Index", "Could not create base index",
                        e);
            }

            if (ensureInitialized) {
                ensureIndexLoaded();
            }

        }
        return navHelper;
    }

    private void ensureIndexLoaded() throws ViatraQueryException {
        try {
            for (Notifier scopeRoot : this.emfScope.getScopeRoots()) {
                navHelper.addRoot(scopeRoot);
            }
        } catch (ViatraBaseException e) {
            throw new ViatraQueryException("Could not initialize VIATRA Base Index",
                    "Could not initialize base index", e);
        }
    }

    @Override
    public IQueryRuntimeContext getQueryRuntimeContext() throws ViatraQueryException {
        NavigationHelper nh = getNavHelper(false);
        if (runtimeContext == null) {
            runtimeContext = 
                    emfScope.getOptions().isDynamicEMFMode() ?
                     new DynamicEMFQueryRuntimeContext(nh, logger, emfScope) :
                     new EMFQueryRuntimeContext(nh, logger, emfScope);
                     
             ensureIndexLoaded();
        }
        
        return runtimeContext;
    }
    
    @Override
    public void dispose() {
        if (runtimeContext != null) runtimeContext.dispose();
        if (navHelper != null) navHelper.dispose();
        
        this.baseIndex = null;
        this.engine = null;
        this.logger = null;
        this.navHelper = null;
    }
    
    
    @Override
    public IBaseIndex getBaseIndex() throws ViatraQueryException {
        if (baseIndex == null) {
            final NavigationHelper navigationHelper = getNavHelper();
            baseIndex = new EMFBaseIndexWrapper(navigationHelper);
        }
        return baseIndex;
    }
}