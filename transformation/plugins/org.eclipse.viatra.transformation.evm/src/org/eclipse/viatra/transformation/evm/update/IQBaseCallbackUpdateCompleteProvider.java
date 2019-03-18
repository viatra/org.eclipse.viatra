/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.update;

import java.util.Objects;

import org.eclipse.viatra.query.runtime.api.scope.IBaseIndex;
import org.eclipse.viatra.query.runtime.api.scope.ViatraBaseIndexChangeListener;
import org.eclipse.viatra.query.runtime.base.api.NavigationHelper;

/**
 * This provider implementation uses the IQBase after update callback as
 * an event source for update complete events.
 * 
 * @author Abel Hegedus
 * 
 */
public class IQBaseCallbackUpdateCompleteProvider extends UpdateCompleteProvider {

    private final ViatraBaseIndexChangeListener modelUpdateListener;
    private final IBaseIndex index;

    /**
     * Creates a new provider for the given {@link IBaseIndex}.
     */
    public IQBaseCallbackUpdateCompleteProvider(final IBaseIndex index) {
        super();
        Objects.requireNonNull(index, "Cannot create provider with null helper!");
        this.modelUpdateListener = new BaseIndexListener();
        this.index = index;
    }

    @Override
    protected void firstListenerAdded() {
        super.firstListenerAdded();
        index.addBaseIndexChangeListener(modelUpdateListener);
    }
    
    @Override
    protected void lastListenerRemoved() {
        super.lastListenerRemoved();
        index.removeBaseIndexChangeListener(modelUpdateListener);
    }
    
    /**
     * Callback class invoked by the {@link NavigationHelper}.
     * 
     * @author Abel Hegedus
     *
     */
    private class BaseIndexListener implements ViatraBaseIndexChangeListener {

        @Override
        public boolean onlyOnIndexChange() {
            return false;
        }

        @Override
        public void notifyChanged(boolean indexChanged) {
            updateCompleted();
        }
    }

}
