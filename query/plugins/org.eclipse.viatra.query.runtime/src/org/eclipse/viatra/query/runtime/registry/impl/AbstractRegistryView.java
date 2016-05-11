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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.viatra.query.runtime.api.GenericQueryGroup;
import org.eclipse.viatra.query.runtime.api.IQueryGroup;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistry;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryEntry;
import org.eclipse.viatra.query.runtime.registry.IRegistryChangeListener;
import org.eclipse.viatra.query.runtime.registry.IRegistryView;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * An abstract {@link IRegistryView} implementation that stores the registry, the set of listeners added to the view and
 * the FQN to entry map of the view itself. The only responsibility of subclasses is to decide whether an entry received
 * as an addition or removal notification is relevent to the view.
 * 
 * @author Abel Hegedus
 *
 */
public abstract class AbstractRegistryView implements IRegistryView, IRegistryChangeListener {

    protected IQuerySpecificationRegistry registry;
    protected Map<String, IQuerySpecificationRegistryEntry> fqnToEntryMap;
    protected Set<IRegistryChangeListener> listeners;

    /**
     * This method is called both when an addition or removal notification is received from the registry. Subclasses can
     * implement view filtering by returning false for those specifications that are not relevant for this view.
     * 
     * @param entry
     *            that is added or removed in the registry
     * @return true if the entry should be added to or removed from the view, false otherwise
     */
    protected abstract boolean isEntryRelevant(IQuerySpecificationRegistryEntry entry);

    /**
     * Creates a new view instance for the given registry. Note that views are created by the registry and the view
     * update mechanisms are also set up by the registry.
     * 
     * @param registry
     */
    public AbstractRegistryView(IQuerySpecificationRegistry registry) {
        this.registry = registry;
        this.fqnToEntryMap = Maps.newTreeMap();
        this.listeners = Sets.newHashSet();
    }

    @Override
    public IQuerySpecificationRegistry getRegistry() {
        return registry;
    }

    @Override
    public Iterable<IQuerySpecificationRegistryEntry> getEntries() {
        ImmutableSet<IQuerySpecificationRegistryEntry> entrySet = ImmutableSet.copyOf(fqnToEntryMap.values());
        return entrySet;
    }

    @Override
    public Set<String> getQuerySpecificationFQNs() {
        ImmutableSet<String> fqns = ImmutableSet.copyOf(fqnToEntryMap.keySet());
        return fqns;
    }

    @Override
    public boolean hasQuerySpecificationFQN(String fullyQualifiedName) {
        checkArgument(fullyQualifiedName != null, "FQN must not be null!");
        return fqnToEntryMap.containsKey(fullyQualifiedName);
    }

    @Override
    public IQuerySpecificationRegistryEntry getEntry(String fullyQualifiedName) {
        checkArgument(fullyQualifiedName != null, "FQN must not be null!");
        IQuerySpecificationRegistryEntry entry = fqnToEntryMap.get(fullyQualifiedName);
        if (entry == null) {
            throw new NoSuchElementException(String.format("Cannot find entry with FQN %s", fullyQualifiedName));
        }
        return entry;
    }

    /**
     * Note that calling this method will perform class loading on all included IQuerySpecifications.
     * 
     * TODO use different {@link IQueryGroup} implementation that can work with {@link IQuerySpecificationProvider}s.
     */
    @Override
    public IQueryGroup getQueryGroup() {
        Iterable<IQuerySpecification<?>> querySpecifications = Iterables.transform(fqnToEntryMap.values(),
                new Function<IQuerySpecificationRegistryEntry, IQuerySpecification<?>>() {
                    @Override
                    public IQuerySpecification<?> apply(IQuerySpecificationRegistryEntry entry) {
                        return entry.get();
                    }
                });
        IQueryGroup queryGroup = GenericQueryGroup.of(ImmutableSet.copyOf(querySpecifications));
        return queryGroup;
    }

    @Override
    public void addViewListener(IRegistryChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeViewListener(IRegistryChangeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void entryAdded(IQuerySpecificationRegistryEntry entry) {
        if (isEntryRelevant(entry)) {
            String fullyQualifiedName = entry.getFullyQualifiedName();
            IQuerySpecificationRegistryEntry oldEntry = fqnToEntryMap.put(fullyQualifiedName, entry);
            if (oldEntry != null) {
                for (IRegistryChangeListener iRegistryChangeListener : listeners) {
                    iRegistryChangeListener.entryRemoved(oldEntry);
                }
            }
            for (IRegistryChangeListener iRegistryChangeListener : listeners) {
                iRegistryChangeListener.entryAdded(entry);
            }
        }
    }

    @Override
    public void entryRemoved(IQuerySpecificationRegistryEntry entry) {
        if (isEntryRelevant(entry)) {
            String fullyQualifiedName = entry.getFullyQualifiedName();
            IQuerySpecificationRegistryEntry existingEntry = fqnToEntryMap.get(fullyQualifiedName);
            if (entry.equals(existingEntry)) {
                fqnToEntryMap.remove(fullyQualifiedName);
                for (IRegistryChangeListener iRegistryChangeListener : listeners) {
                    iRegistryChangeListener.entryRemoved(entry);
                }
            }
        }
    }

}