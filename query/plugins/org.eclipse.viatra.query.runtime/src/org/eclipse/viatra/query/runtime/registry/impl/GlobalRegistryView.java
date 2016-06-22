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

import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.viatra.query.runtime.api.IQueryGroup;
import org.eclipse.viatra.query.runtime.api.LazyLoadingQueryGroup;
import org.eclipse.viatra.query.runtime.registry.IDefaultRegistryView;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistry;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryEntry;
import org.eclipse.viatra.query.runtime.registry.view.AbstractRegistryView;

import com.google.common.collect.ImmutableSet;

/**
 * Registry view implementation that considers specifications relevant if they are included in default views.
 * 
 * @author Abel Hegedus
 *
 */
public class GlobalRegistryView extends AbstractRegistryView implements IDefaultRegistryView {
    
    /**
     * Creates a new instance of the global view.
     * 
     * @param registry that defines the view
     */
    public GlobalRegistryView(IQuerySpecificationRegistry registry) {
        super(registry, false);
    }
    
    @Override
    protected boolean isEntryRelevant(IQuerySpecificationRegistryEntry entry) {
        return entry.includeInDefaultViews();
    }
    
    @Override
    public IQueryGroup getQueryGroup() {
        IQueryGroup queryGroup = LazyLoadingQueryGroup.of(ImmutableSet.copyOf(fqnToEntryMap.values())); 
        return queryGroup;
    }

    @Override
    public IQuerySpecificationRegistryEntry getEntry(String fullyQualifiedName) {
        Set<IQuerySpecificationRegistryEntry> entries = getEntries(fullyQualifiedName);
        if(entries.isEmpty()){
            throw new NoSuchElementException("Cannot find entry with FQN " + fullyQualifiedName);
        }
        if(entries.size() > 1) {
            throw new IllegalStateException("Global view must never contain duplicated FQNs!");
        }
        IQuerySpecificationRegistryEntry entry = entries.iterator().next();
        return entry;
    }

}
