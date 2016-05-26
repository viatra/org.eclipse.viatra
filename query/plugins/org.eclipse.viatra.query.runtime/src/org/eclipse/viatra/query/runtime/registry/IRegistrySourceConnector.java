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
 * A registry source connector can provide query specifications to listeners (e.g. {@link IQuerySpecificationRegistry}).
 * The connector interface does not support direct access to query specifications, instead it sends existing specifications
 * to listeners on addition and sends notifications to listeners when a change occurs in the set of specifications.
 * 
 * @author Abel Hegedus
 * @since 1.3
 *
 */
public interface IRegistrySourceConnector {

    /**
     * The connector must return the same identifier every time it is invoked!
     * 
     * @return unique identifier of the connector
     */
    String getIdentifier();

    /**
     * 
     * @return true if the specifications of the connector should be included in default views
     */
    boolean includeSpecificationsInDefaultViews();
    
    /**
     * Add a listener to get updates on changes in the query specifications available from the connector. When the
     * listener is added, the connector is expected to call the listener with each existing query specification.
     * 
     * @param listener that should be added
     */
    void addListener(IConnectorListener listener);

    /**
     * Removes an already registered listener and stops sending updates. The connector is not required to send any
     * updates before returning from this method, but should not send any events after this method returns.
     * 
     * @param listener that should be removed
     */
    void removeListener(IConnectorListener listener);
}
