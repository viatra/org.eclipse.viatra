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
package org.eclipse.viatra.query.runtime.registry;

import org.eclipse.viatra.query.runtime.extensibility.IQuerySpecificationProvider;

/**
 * Connector listeners are used to receive notifications on addition and removal of query specifications.
 * The connector itself is also passed in the methods to allow the usage of the same listener on multiple connectors.
 * 
 * @author Abel Hegedus
 * @since 1.3
 *
 */
public interface IConnectorListener {

    /**
     * Called when a new query specification is added to the given connector.
     * The provider interface is used to avoid class loading as long as possible.
     * 
     * @param connector that has a new specification
     * @param specificationProvider that wraps the new specification
     */
    void querySpecificationAdded(IRegistrySourceConnector connector, IQuerySpecificationProvider specificationProvider);
    
    /**
     * Called when a query specification is removed from the given connector.
     * The provider interface is used to avoid class loading as long as possible.
     * 
     * @param connector that has a removed specification
     * @param specificationProvider that wraps the removed specification
     */
    void querySpecificationRemoved(IRegistrySourceConnector connector, IQuerySpecificationProvider specificationProvider);
}
