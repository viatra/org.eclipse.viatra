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
package org.eclipse.viatra.query.runtime.registry.data;

import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryEntry;

/**
 * Internal data storage object that represents a query specification entry. The entry contains an
 * {@link IQuerySpecificationProvider} and has a reference to the source that contains it.
 * 
 * @author Abel Hegedus
 *
 */
public class RegistryEntryImpl implements IQuerySpecificationRegistryEntry {

    private IQuerySpecificationProvider provider;
    private RegistrySourceImpl source;

    /**
     * Creates a new instance with the given source and the given provider.
     * 
     * @param source
     *            that contains the new entry
     * @param provider
     *            that wraps the specification represented by the entry
     */
    public RegistryEntryImpl(RegistrySourceImpl source, IQuerySpecificationProvider provider) {
        this.source = source;
        this.provider = provider;
    }

    /**
     * @return the source that contains this entry
     */
    public RegistrySourceImpl getSource() {
        return source;
    }

    @Override
    public String getSourceIdentifier() {
        return source.getIdentifier();
    }

    @Override
    public boolean includeInDefaultViews() {
        return getSource().includeEntriesInDefaultViews();
    }

    @Override
    public String getFullyQualifiedName() {
        return provider.getFullyQualifiedName();
    }

    @Override
    public IQuerySpecification<?> get() {
        return provider.get();
    }

    @Override
    public IQuerySpecificationProvider getProvider() {
        return provider;
    }

    @Override
    public boolean isFromProject() {
        return provider.getSourceProjectName() != null;
    }

    @Override
    public String getSourceProjectName() {
        return provider.getSourceProjectName();
    }
}
