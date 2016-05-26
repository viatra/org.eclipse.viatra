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
package org.eclipse.viatra.query.runtime.registry.connector;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.eclipse.viatra.query.runtime.registry.IConnectorListener;
import org.eclipse.viatra.query.runtime.registry.IRegistrySourceConnector;

import com.google.common.collect.Sets;

/**
 * Abstract registry source connector implementation that stores the identifier and listener set.
 * 
 * 
 * @author Abel Hegedus
 * @since 1.3
 *
 */
public abstract class AbstractRegistrySourceConnector implements IRegistrySourceConnector {

    protected Set<IConnectorListener> listeners;
    private String identifier;
    private boolean includeInDefaultViews;

    /**
     * Creates an instance of the connector with the given identifier. The identifier should be unique if you want to
     * add it to a registry as a source.
     * 
     * @param identifier
     *            of the newly created connector
     * @param includeInDefaultViews
     *            true if the specifications in the connector should be included in default views
     */
    public AbstractRegistrySourceConnector(String identifier, boolean includeInDefaultViews) {
        super();
        checkNotNull(identifier, "Identifier must not be null!");
        this.identifier = identifier;
        this.includeInDefaultViews = includeInDefaultViews;
        this.listeners = Sets.newHashSet();
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }
    
    @Override
    public boolean includeSpecificationsInDefaultViews() {
        return includeInDefaultViews;
    }

    @Override
    public void addListener(IConnectorListener listener) {
        checkNotNull(listener, "Listener must not be null!");
        boolean added = listeners.add(listener);
        if (added) {
            sendQuerySpecificationsToListener(listener);
        }
    }

    @Override
    public void removeListener(IConnectorListener listener) {
        checkNotNull(listener, "Listener must not be null!");
        listeners.remove(listener);
    }

    /**
     * Subclasses should send add notifications for each specification in the connector to the given listener.
     * 
     * @param listener that should receive the notifications
     */
    protected abstract void sendQuerySpecificationsToListener(IConnectorListener listener);

}