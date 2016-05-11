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
 * The registry view filter can control which entries are added and removed from an {@link IRegistryView}.
 * 
 * @author Abel Hegedus
 * @since 1.3
 *
 */
public interface IRegistryViewFilter {

    /**
     * This method controls whether a registry entry is added to the view or not. The filtering is called before
     * checking the uniqueness of fully qualified names, and relevant entries can overwrite existing entries with the
     * same FQN.
     * 
     * Note that filters should usually return the same value for the same entry on multiple invocations.
     * 
     * @param entry
     *            that is checked
     * @return true, if the entry is relevant for the view, false otherwise
     */
    boolean isEntryRelevant(IQuerySpecificationRegistryEntry entry);

}
