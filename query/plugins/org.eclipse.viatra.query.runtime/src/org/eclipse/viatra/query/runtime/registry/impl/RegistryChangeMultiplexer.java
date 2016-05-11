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

import java.lang.ref.WeakReference;
import java.util.Set;

import org.eclipse.viatra.query.runtime.registry.IRegistryChangeListener;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryEntry;

import com.google.common.collect.Sets;

/**
 * Listener implementation that propagates all changes to a set of listeners.
 * The listeners are stored with weak references to avoid a need for disposal.
 * 
 * @author Abel Hegedus
 *
 */
public class RegistryChangeMultiplexer implements IRegistryChangeListener {

    private Set<WeakReference<IRegistryChangeListener>> listeners;
    
    /**
     * Creates a new instance of the multiplexer.
     */
    public RegistryChangeMultiplexer() {
        this.listeners = Sets.newHashSet();
    }
    
    /**
     * Adds a weak reference on the listener to the multiplexer. The listener will receive all further notifications and
     * does not have to be removed, since the multiplexer will not keep it in memory when it can be collected.
     * 
     * @param listener
     * @return
     */
    public boolean addListener(IRegistryChangeListener listener) {
        return listeners.add(new WeakReference<IRegistryChangeListener>(listener));
    }
    
    @Override
    public void entryAdded(IQuerySpecificationRegistryEntry entry) {
        for (WeakReference<IRegistryChangeListener> listenerRef : listeners) {
            IRegistryChangeListener iRegistryChangeListener = listenerRef.get();
            if(iRegistryChangeListener != null){
                iRegistryChangeListener.entryAdded(entry);
            } else {
                listeners.remove(listenerRef);
            }
        }
    }

    @Override
    public void entryRemoved(IQuerySpecificationRegistryEntry entry) {
        for (WeakReference<IRegistryChangeListener> listenerRef : listeners) {
            IRegistryChangeListener iRegistryChangeListener = listenerRef.get();
            if(iRegistryChangeListener != null){
                iRegistryChangeListener.entryRemoved(entry);
            } else {
                listeners.remove(listenerRef);
            }
        }
    }

}
