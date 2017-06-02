/*******************************************************************************
 * Copyright (c) 2010-2016, Grill Balázs, IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.context;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendHintProvider;
import org.eclipse.viatra.query.runtime.matchers.psystem.analysis.QueryAnalyzer;

/**
 * This interface is a collector which holds every API that is provided by the engine to control
 * the operation of the backends.
 *
 * @since 1.5
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IQueryBackendContext {

    Logger getLogger();
    
    IQueryRuntimeContext getRuntimeContext();
    
    IQueryCacheContext getQueryCacheContext();
    
    IQueryBackendHintProvider getHintProvider();
    
    IQueryResultProviderAccess getResultProviderAccess();
    
    QueryAnalyzer getQueryAnalyzer();
    
    /**
     * @since 1.6
     */
    boolean areUpdatesDelayed();
    
}
