/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.registry.impl;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryChangeListener;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryEntry;

/**
 * Listener implementation that propagates all changes to a set of listeners.
 * The listeners are stored with weak references to avoid a need for disposal.
 * 
 * @author Abel Hegedus
 *
 */
public class RegistryChangeMultiplexer implements IQuerySpecificationRegistryChangeListener {

    private Set<IQuerySpecificationRegistryChangeListener> listeners;
    
    /**
     * Creates a new instance of the multiplexer.
     */
    public RegistryChangeMultiplexer() {
        this.listeners = Collections.newSetFromMap(new WeakHashMap<IQuerySpecificationRegistryChangeListener, Boolean>());
    }
    
    /**
     * Adds a weak reference on the listener to the multiplexer. The listener will receive all further notifications and
     * does not have to be removed, since the multiplexer will not keep it in memory when it can be collected.
     */
    public boolean addListener(IQuerySpecificationRegistryChangeListener listener) {
        return listeners.add(listener);
    }
    
    @Override
    public void entryAdded(IQuerySpecificationRegistryEntry entry) {
        for (IQuerySpecificationRegistryChangeListener listener : listeners) {
            listener.entryAdded(entry);
        }
    }

    @Override
    public void entryRemoved(IQuerySpecificationRegistryEntry entry) {
        for (IQuerySpecificationRegistryChangeListener listener : listeners) {
            listener.entryRemoved(entry);
        }
    }

}
