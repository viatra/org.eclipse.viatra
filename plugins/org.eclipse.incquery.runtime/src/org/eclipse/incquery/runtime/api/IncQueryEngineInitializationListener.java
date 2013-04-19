/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.api;

/**
 * Listener interface to get notifications when a new managed engine is initialized.
 * 
 * @author Abel Hegedus
 *
 */
public interface IncQueryEngineInitializationListener {

    /**
     * Called when a managed engine is initialized in the EngineManager.
     * 
     * @param engine the initialized engine
     */
    void engineInitialized(IncQueryEngine engine);
    
}
