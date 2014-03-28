/*******************************************************************************
 * Copyright (c) 2010-2012, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.extensibility;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.base.api.IIndexingErrorListener;

/**
 * Listens for the event of the engine becoming tainted.
 * 
 * Attach this listener to the logger of the engine as an appender. Do not forget to remove when losing interest or when
 * engine is disposed.
 * 
 * @author Bergmann Gabor
 * @see IncQueryEngine#isTainted()
 */
public abstract class EngineTaintListener implements IIndexingErrorListener {

    /**
     * This callback will be alerted at most once, when the engine becomes tainted.
     */
    public abstract void engineBecameTainted();

    private boolean noTaintDetectedYet = true;

    protected void notifyTainted() {
        if (noTaintDetectedYet) {
            noTaintDetectedYet = false;
            engineBecameTainted();
        }
    }

    @Override
    public void error(String description) {
        //Errors does not mean tainting
    }

    @Override
    public void error(String description, Throwable t) {
        //Errors does not mean tainting        
    }

    @Override
    public void fatal(String description) {
        notifyTainted();
    }

    @Override
    public void fatal(String description, Throwable t) {
        notifyTainted();
    }
    
}