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
package org.eclipse.viatra.transformation.evm.update;

import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryModelUpdateListener;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;

/**
 * This provider implementation uses the ViatraQueryEngine model update listener as
 * an event source for update complete events.
 * 
 * @author Abel Hegedus
 *
 */
public class QueryEngineUpdateCompleteProvider extends UpdateCompleteProvider {

    private final ViatraQueryModelUpdateListener modelUpdateListener;
    private final AdvancedViatraQueryEngine engine;
    
    /**
     * Creates a new provider for the given {@link ViatraQueryEngine}
     */
    public QueryEngineUpdateCompleteProvider(ViatraQueryEngine engine) {
        super();
        Preconditions.checkArgument(engine != null, "Cannot create provider with null engine!");
        this.engine = AdvancedViatraQueryEngine.from(engine);
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
        // Engine dispose already removes all model update listeners, no reason to do it again
        if (!engine.isDisposed()) {
            engine.removeModelUpdateListener(modelUpdateListener);
        }
    }
    
    /**
     * Callback class invoked by the {@link ViatraQueryEngine}
     * 
     * @author Abel Hegedus
     *
     */
    private final class ModelUpdateListener implements ViatraQueryModelUpdateListener {
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
