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

/**
 * The query specification registry is used to manage query specifications provided by multiple connectors which can
 * dynamically add and remove specifications. Users can read the contents of the registry through views that are also
 * dynamically updated when the registry is changed by the connectors.  
 * 
 * @author Abel Hegedus
 * @since 1.3
 *
 */
public interface IQuerySpecificationRegistry {

    /**
     * Cannot register connectors with the same identifier twice. No change occurs if the identifier is already used.
     * 
     * @param connector,
     *            cannot be null
     * @return false if a connector with the given identifier has already been added, true otherwise
     */
    boolean addSource(IRegistrySourceConnector connector);

    /**
     * Removes the connector if it was registered. No change occurs if the identifier of the connector was not used
     * before.
     * 
     * @param connector,
     *            cannot be null
     * @return false if a registered connector with the given identifier was not found, true if it was successfully
     *         removed
     */
    boolean removeSource(IRegistrySourceConnector connector);

    /**
     * Returns a default view instance that contains all query specification entries in the registry. If there are entries with 
     * the same FQN, only the last added will be included in the view to avoid duplicate FQNs.
     * 
     * 
     * @return the default view instance
     */
    IRegistryView getDefaultView();
    
    /**
     * Creates a view which contains all registered query specifications. This view will also be incrementally updated
     * on registry changes and accepts listeners to notify on changes.
     * 
     * @return a new view instance
     */
    IRegistryView createView();

    /**
     * Creates a view which contains registered query specifications that are considered relevant by the passed filter.
     * This view will also be incrementally updated on registry changes and accepts listeners to notify on changes.
     * 
     * @return a new filtered view instance
     */
    IRegistryView createView(IRegistryViewFilter filter);
}
