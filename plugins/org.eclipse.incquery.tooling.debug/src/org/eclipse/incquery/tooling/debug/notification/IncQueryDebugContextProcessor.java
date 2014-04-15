/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.debug.notification;

import org.eclipse.debug.ui.contexts.DebugContextEvent;
import org.eclipse.debug.ui.contexts.IDebugContextListener;

public class IncQueryDebugContextProcessor implements IDebugContextListener {

    private static IncQueryDebugContextProcessor instance;
    
    public static IncQueryDebugContextProcessor getInstance() {
        if (instance == null) {
           instance = new IncQueryDebugContextProcessor(); 
        }
        return instance;
    }
    
    protected IncQueryDebugContextProcessor() {
        
    }
    
    @Override
    public void debugContextChanged(DebugContextEvent event) {
        if ((event.getFlags() & DebugContextEvent.ACTIVATED) > 0) {
            //System.out.println("context");
        }
    }

}