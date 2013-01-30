/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Abel Hegedus - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.evm.notification;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.incquery.runtime.api.IPatternMatch;

/**
 * The class defines the operations that are required to observ the EMF attribute changes on pattern match objects.
 * 
 * @author Tamas Szabo
 * 
 * @param <MatchType>
 */
public abstract class AttributeMonitor<MatchType extends IPatternMatch> {

    private List<IAttributeMonitorListener<MatchType>> listeners;

    public AttributeMonitor() {
        this.listeners = new ArrayList<IAttributeMonitorListener<MatchType>>();
    }

    public void addCallbackOnMatchUpdate(final IAttributeMonitorListener<MatchType> listener) {
        this.listeners.add(listener);
    }

    public void removeCallbackOnMatchUpdate(final IAttributeMonitorListener<MatchType> listener) {
        this.listeners.remove(listener);
    }

    public abstract void registerFor(final MatchType match);

    public abstract void unregisterForAll();

    public abstract void unregisterFor(final MatchType match);

    protected void notifyListeners(final MatchType match) {
        for (IAttributeMonitorListener<MatchType> listener : listeners) {
            listener.notifyUpdate(match);
        }
    }

    public void dispose() {
        this.unregisterForAll();
    }
}
