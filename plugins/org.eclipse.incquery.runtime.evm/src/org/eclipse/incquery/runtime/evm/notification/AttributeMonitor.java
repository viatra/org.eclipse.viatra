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
 * The class defines the operations that are required to observe the EMF attribute changes on pattern match objects.
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
    
    /**
     * Registers a listener for notifications when a feature value of an object in a match changes.
     * 
     * <p>
     * The listener can be unregistered via
     * {@link #removeAttributeMonitorListener(IAttributeMonitorListener)}.
     * 
     * @param listener
     */
    public void addAttributeMonitorListener(final IAttributeMonitorListener<MatchType> listener) {
        this.listeners.add(listener);
    }

    /**
     * Unregisters a listener registered by
     * {@link #addAttributeMonitorListener(IAttributeMonitorListener)}.
     * 
     * @param listener
     */
    public void removeAttributeMonitorListener(final IAttributeMonitorListener<MatchType> listener) {
        this.listeners.remove(listener);
    }

    /**
     * Register the attribute monitor to watch feature values  of object defined in the given match.
     *  
     * @param match
     */
    public abstract void registerFor(final MatchType match);

    /**
     * Remove the attribute monitor from watching registered matches.
     */
    public abstract void unregisterForAll();

    /**
     * Remove the attribute monitor from watching the given match.
     * 
     * @param match
     */
    public abstract void unregisterFor(final MatchType match);

    /**
     * Sends notification to listeners when the given match is updated.
     * 
     * @param match
     */
    protected void notifyListeners(final MatchType match) {
        for (IAttributeMonitorListener<MatchType> listener : listeners) {
            listener.notifyUpdate(match);
        }
    }

    /**
     * Disposes of the attribute monitor by unregistering from each match.
     * 
     * TODO should we clear the listener list?
     */
    public void dispose() {
        this.unregisterForAll();
    }
}
