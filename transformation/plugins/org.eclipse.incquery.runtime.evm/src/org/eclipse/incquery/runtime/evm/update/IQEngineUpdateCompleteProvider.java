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
package org.eclipse.incquery.runtime.evm.update;

import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryModelUpdateListener;

import com.google.common.base.Preconditions;

/**
 * This provider implementation uses the IncQueryEngine model update listener as
 * an event source for update complete events.
 * 
 * @author Abel Hegedus
 *
 */
public class IQEngineUpdateCompleteProvider extends UpdateCompleteProvider {

    private final IncQueryModelUpdateListener modelUpdateListener;
    private final AdvancedIncQueryEngine engine;
    
    /**
     * Creates a new provider for the given {@link IncQueryEngine}
     */
    public IQEngineUpdateCompleteProvider(IncQueryEngine engine) {
        super();
        Preconditions.checkArgument(engine != null, "Cannot create provider with null engine!");
        this.engine = AdvancedIncQueryEngine.from(engine);
        this.modelUpdateListener = new ModelUpdateListener();
    }
    
    @Override
    protected void firstListenerAdded() {
        super.firstListenerAdded();
        this.engine.addModelUpdateListener(modelUpdateListener);
    }
    
    @Override
    protected void lastListenerRemoved() {
        super.lastListenerRemoved();
        engine.removeModelUpdateListener(modelUpdateListener);
    }
    
    /**
     * Callback class invoked by the {@link IncQueryEngine}
     * 
     * @author Abel Hegedus
     *
     */
    private final class ModelUpdateListener implements IncQueryModelUpdateListener {
        @Override
        public void notifyChanged(ChangeLevel changeLevel) {
            updateCompleted();
        }
    
        @Override
        public ChangeLevel getLevel() {
            return ChangeLevel.MODEL;
        }
    }

}
