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

import static org.eclipse.viatra.query.runtime.matchers.util.Preconditions.checkArgument;

import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.viatra.query.runtime.registry.IConnectorListener;
import org.eclipse.viatra.query.runtime.registry.IDefaultRegistryView;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistry;
import org.eclipse.viatra.query.runtime.registry.IRegistryView;
import org.eclipse.viatra.query.runtime.registry.IRegistryViewFactory;
import org.eclipse.viatra.query.runtime.registry.IRegistryViewFilter;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryChangeListener;
import org.eclipse.viatra.query.runtime.registry.IRegistrySourceConnector;
import org.eclipse.viatra.query.runtime.registry.data.QuerySpecificationStore;
import org.eclipse.viatra.query.runtime.registry.data.RegistryEntryImpl;
import org.eclipse.viatra.query.runtime.registry.data.RegistrySourceImpl;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;

/**
 * This is the default implementation of the {@link IQuerySpecificationRegistry} interface.
 * It uses a {@link QuerySpecificationStore} to keep track of sources and entries.
 * It uses a {@link RegistryChangeMultiplexer} to update all views and a single {@link IConnectorListener}
 * to subscribe to sources added to the registry.
 * 
 * @author Abel Hegedus
 *
 */
public class QuerySpecificationRegistryImpl implements IQuerySpecificationRegistry {

    private static final String CONNECTOR_NULL_MSG = "Connector cannot be null";
    private final QuerySpecificationStore querySpecificationStore;
    private final IConnectorListener connectorListener;
    private final RegistryChangeMultiplexer multiplexer;
    private final Logger logger;
    private IDefaultRegistryView defaultView = null;
    
    /**
     * Creates a new instance of the registry
     */
    public QuerySpecificationRegistryImpl() {
        this.querySpecificationStore = new QuerySpecificationStore();
        this.connectorListener = new RegistryUpdaterConnectorListener();
        this.multiplexer = new RegistryChangeMultiplexer();
        this.logger = ViatraQueryLoggingUtil.getLogger(IQuerySpecificationRegistry.class);
    }
    
    @Override
    public boolean addSource(IRegistrySourceConnector connector) {
        checkArgument(connector != null, CONNECTOR_NULL_MSG);
        String identifier = connector.getIdentifier();
        Map<String, RegistrySourceImpl> sources = querySpecificationStore.getSources();
        if(sources.containsKey(identifier)){
            return false;
        }
        RegistrySourceImpl source = new RegistrySourceImpl(identifier, querySpecificationStore, connector.includeSpecificationsInDefaultViews());
        sources.put(identifier, source);
        connector.addListener(connectorListener);
        logger.debug("Source added: " + source.getIdentifier());
        return true;
    }
    
    @Override
    public boolean removeSource(IRegistrySourceConnector connector) {
        checkArgument(connector != null, CONNECTOR_NULL_MSG);
        String identifier = connector.getIdentifier();
        Map<String, RegistrySourceImpl> sources = querySpecificationStore.getSources();
        if(!sources.containsKey(identifier)){
            return false;
        }
        connector.removeListener(connectorListener);
        RegistrySourceImpl source = sources.remove(identifier);
        for (RegistryEntryImpl entry : source.getFqnToEntryMap().values()) {
            multiplexer.entryRemoved(entry);
        }
        logger.debug("Source removed: " + source.getIdentifier());
        return true;
    }
    
    /**
     * @return the internal store of the registry
     */
    protected QuerySpecificationStore getStore() {
        return querySpecificationStore;
    }

    @Override
    public IRegistryView createView() {
        return createGlobalView();
    }

    private GlobalRegistryView createGlobalView() {
        GlobalRegistryView registryView = new GlobalRegistryView(this);
        initializeChangeListener(registryView);
        return registryView;
    }

    protected void initializeChangeListener(IQuerySpecificationRegistryChangeListener listener) {
        // send existing entries to aspect
        for (RegistrySourceImpl source : querySpecificationStore.getSources().values()) {
            Map<String, RegistryEntryImpl> entryMap = source.getFqnToEntryMap();
            for (RegistryEntryImpl entry : entryMap.values()) {
                listener.entryAdded(entry);
            }
        }
        multiplexer.addListener(listener);
    }

    @Override
    public IRegistryView createView(IRegistryViewFilter filter) {
        checkArgument(filter != null, "Filter cannot be null");
        FilteringRegistryView registryView = new FilteringRegistryView(this, filter, false);
        initializeChangeListener(registryView);
        return registryView;
    }

    /**
     * Internal connector listener implementation for updating internal store and propagating changes to views.
     * 
     * @author Abel Hegedus
     *
     */
    private final class RegistryUpdaterConnectorListener implements IConnectorListener {
        @Override
        public void querySpecificationAdded(IRegistrySourceConnector connector, IQuerySpecificationProvider specification) {
            String identifier = connector.getIdentifier();
            RegistrySourceImpl source = querySpecificationStore.getSources().get(identifier);
            String fullyQualifiedName = specification.getFullyQualifiedName();
            RegistryEntryImpl registryEntry = new RegistryEntryImpl(source, specification);
            RegistryEntryImpl oldEntry = source.getFqnToEntryMap().put(fullyQualifiedName, registryEntry);
            if(oldEntry != null) {
                logger.warn(String.format("Specification added with existing FQN %s in source %s", fullyQualifiedName, identifier));
                multiplexer.entryRemoved(oldEntry);
            }
            multiplexer.entryAdded(registryEntry);
            
        }

        @Override
        public void querySpecificationRemoved(IRegistrySourceConnector connector, IQuerySpecificationProvider specification) {
            String identifier = connector.getIdentifier();
            RegistrySourceImpl source = querySpecificationStore.getSources().get(identifier);
            String fullyQualifiedName = specification.getFullyQualifiedName();
            RegistryEntryImpl registryEntry = source.getFqnToEntryMap().remove(fullyQualifiedName);
            if(registryEntry != null) {
                multiplexer.entryRemoved(registryEntry);
            }
        }
    }

    @Override
    public IDefaultRegistryView getDefaultView() {
        if(this.defaultView == null){
            this.defaultView = createGlobalView();
        }
        return this.defaultView;
    }

    @Override
    public IRegistryView createView(IRegistryViewFactory factory) {
        IRegistryView registryView = factory.createView(this);
        initializeChangeListener(registryView);
        return registryView;
    }
    
    
}
