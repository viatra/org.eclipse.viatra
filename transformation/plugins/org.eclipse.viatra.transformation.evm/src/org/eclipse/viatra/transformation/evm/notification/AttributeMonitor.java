/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.transformation.evm.notification;

import java.util.ArrayList;
import java.util.List;

/**
 * The class defines the operations that are required to observe the EMF attribute changes on atom objects.
 * 
 * @author Tamas Szabo
 * 
 */
public abstract class AttributeMonitor<Atom> {

    private List<IAttributeMonitorListener<Atom>> listeners;

    public AttributeMonitor() {
        this.listeners = new ArrayList<IAttributeMonitorListener<Atom>>();
    }
    
    /**
     * Registers a listener for notifications when a feature value of an object in a atom changes.
     * 
     * <p>
     * The listener can be unregistered via
     * {@link #removeAttributeMonitorListener(IAttributeMonitorListener)}.
     * 
     * @param listener
     */
    public void addAttributeMonitorListener(final IAttributeMonitorListener<Atom> listener) {
        this.listeners.add(listener);
    }

    /**
     * Unregisters a listener registered by
     * {@link #addAttributeMonitorListener(IAttributeMonitorListener)}.
     * 
     * @param listener
     */
    public void removeAttributeMonitorListener(final IAttributeMonitorListener<Atom> listener) {
        this.listeners.remove(listener);
    }

    /**
     * Register the attribute monitor to watch feature values  of object defined in the given atom.
     *  
     * @param atom
     */
    public abstract void registerFor(final Atom atom);

    /**
     * Remove the attribute monitor from watching registered atoms.
     */
    public abstract void unregisterForAll();

    /**
     * Remove the attribute monitor from watching the given atom.
     * 
     * @param atom
     */
    public abstract void unregisterFor(final Atom atom);

    /**
     * Sends notification to listeners when the given atom is updated.
     * 
     * @param match
     */
    protected void notifyListeners(final Atom atom) {
        for (IAttributeMonitorListener<Atom> listener : listeners) {
            listener.notifyUpdate(atom);
        }
    }

    /**
     * Disposes of the attribute monitor by unregistering from each atom.
     * 
     * TODO should we clear the listener list?
     */
    public void dispose() {
        this.unregisterForAll();
    }
}
