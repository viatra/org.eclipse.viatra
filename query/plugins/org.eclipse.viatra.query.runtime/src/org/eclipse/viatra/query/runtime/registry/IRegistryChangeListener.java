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
 * Listener interface for providing update notifications of views to users. It is used for propagating changes from the
 * query specification registry to the views and from the views to users.
 * 
 * @author Abel Hegedus
 * @since 1.3
 *
 */
public interface IRegistryChangeListener {

    /**
     * Called when a new entry is added to the registry.
     * 
     * @param entry that is added
     */
    void entryAdded(IQuerySpecificationRegistryEntry entry);

    /**
     * Called when an existing entry is removed from the registry.
     *  
     * @param entry that is removed
     */
    void entryRemoved(IQuerySpecificationRegistryEntry entry);

}
