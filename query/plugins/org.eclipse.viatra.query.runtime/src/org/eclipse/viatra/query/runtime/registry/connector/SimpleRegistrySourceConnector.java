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

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.viatra.query.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.viatra.query.runtime.extensibility.SingletonQuerySpecificationProvider;
import org.eclipse.viatra.query.runtime.registry.IConnectorListener;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * A simple connector implementation that allows users to simply add and remove specifications. These changes are
 * propagated to listeners (e.g. the registry). Note that duplicate FQNs are not allowed in a given connector.
 * 
 * @author Abel Hegedus
 * @since 1.3
 *
 */
public class SimpleRegistrySourceConnector extends AbstractRegistrySourceConnector {

    private static final String DUPLICATE_MESSAGE = "Duplicate FQN %s cannot be added to connector";
    private Map<String, IQuerySpecificationProvider> specificationProviderMap;

    /**
     * Creates an instance of the connector with the given identifier. The identifier should be unique if you want to
     * add it to a registry as a source.
     * 
     * @param identifier
     *            of the newly created connector
     */
    public SimpleRegistrySourceConnector(String identifier) {
        super(identifier);
        this.specificationProviderMap = Maps.newHashMap();
    }

    /**
     * Adds a query specification to the connector.
     * If you have an {@link IQuerySpecification} object, use {@link SingletonQuerySpecificationProvider}.
     * 
     * @param provider to add to the connector
     * @throws IllegalArgumentException if the connector already contains a specification with the same FQN
     */
    public void addQuerySpecificationProvider(IQuerySpecificationProvider provider) {
        checkNotNull(provider, "Provider must not be null!");
        String fullyQualifiedName = provider.getFullyQualifiedName();
        if (!specificationProviderMap.containsKey(fullyQualifiedName)) {
            specificationProviderMap.put(fullyQualifiedName, provider);
            for (IConnectorListener listener : listeners) {
                listener.querySpecificationAdded(this, provider);
            }
        } else {
            throw new IllegalArgumentException(String.format(DUPLICATE_MESSAGE, fullyQualifiedName));
        }
    }

    /**
     * Remove a specification that has been added with the given FQN.
     * 
     * @param fullyQualifiedName
     * @throws NoSuchElementException if the connector does not contain a specification with the given FQN
     */
    public void removeQuerySpecificationProvider(String fullyQualifiedName) {
        checkNotNull(fullyQualifiedName, "Fully qualified name must not be null!");
        IQuerySpecificationProvider provider = specificationProviderMap.remove(fullyQualifiedName);
        if (provider == null) {
            throw new NoSuchElementException(
                    String.format("Connector does not contain specification with FQN %s", fullyQualifiedName));
        }
        for (IConnectorListener listener : listeners) {
            listener.querySpecificationRemoved(this, provider);
        }
    }

    /**
     * @return the immutable copy of the set of FQNs for the added query specifications
     */
    public Set<String> getQuerySpecificationFQNs() {
        ImmutableSet<String> fqns = ImmutableSet.copyOf(specificationProviderMap.keySet());
        return fqns;
    }

    /**
     * 
     * @param fullyQualifiedName that is checked
     * @return true if a specification with the given FQN exists in the connector, false otherwise
     */
    public boolean hasQuerySpecificationFQN(String fullyQualifiedName) {
        checkNotNull(fullyQualifiedName, "FQN must not be null!");
        return specificationProviderMap.containsKey(fullyQualifiedName);
    }

    @Override
    protected void sendQuerySpecificationsToListener(IConnectorListener listener) {
        for (IQuerySpecificationProvider provider : specificationProviderMap.values()) {
            listener.querySpecificationAdded(this, provider);
        }
    }

}
