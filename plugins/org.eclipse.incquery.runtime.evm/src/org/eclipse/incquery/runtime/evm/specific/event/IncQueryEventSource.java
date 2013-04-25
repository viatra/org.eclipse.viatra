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
package org.eclipse.incquery.runtime.evm.specific.event;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.log4j.Logger;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.evm.api.event.EventSource;

/**
 * @author Abel Hegedus
 *
 */
public class IncQueryEventSource implements EventSource {

    private IncQueryEngine engine;
    
    /**
     * 
     */
    private IncQueryEventSource(IncQueryEngine engine) {
        this.engine = engine;
    }
    
    /**
     * @return the engine
     */
    public IncQueryEngine getEngine() {
        return engine;
    }
    
    public static EventSource create(IncQueryEngine engine) {
        checkArgument(engine != null, "Cannot create event source for null engine!");
        return new IncQueryEventSource(engine);
    }

    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.evm.api.event.EventSource#getLogger()
     */
    @Override
    public Logger getLogger() {
        return engine.getLogger();
    }
    
}
