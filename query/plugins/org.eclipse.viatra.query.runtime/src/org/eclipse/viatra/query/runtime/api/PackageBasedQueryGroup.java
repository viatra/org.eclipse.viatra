/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus, Mark Czotter - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.api;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.viatra.query.runtime.api.impl.BaseQueryGroup;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistry;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryEntry;
import org.eclipse.viatra.query.runtime.registry.IRegistryChangeListener;
import org.eclipse.viatra.query.runtime.registry.IRegistryView;
import org.eclipse.viatra.query.runtime.registry.IRegistryViewFilter;
import org.eclipse.viatra.query.runtime.registry.QuerySpecificationRegistry;

import com.google.common.collect.ImmutableSet;

/**
 * Package based {@link BaseQueryGroup} implementation. It handles patterns as a group within the same package.
 * 
 * @author Abel Hegedus, Mark Czotter
 * 
 */
public class PackageBasedQueryGroup extends BaseQueryGroup {

    private final Set<IQuerySpecification<?>> querySpecifications = new HashSet<IQuerySpecification<?>>();
    private final String packageName;
    private final boolean includeSubPackages;
    private IRegistryView view;
    private SpecificationSetUpdater listener;

    public PackageBasedQueryGroup(String packageName) {
        this(packageName, false);
    }

    public PackageBasedQueryGroup(String packageName, boolean includeSubPackages) {
        super();
        this.packageName = packageName;
        this.includeSubPackages = includeSubPackages;
        IQuerySpecificationRegistry registry = QuerySpecificationRegistry.getInstance();
        this.view = registry.createView(new PackageNameBasedViewFilter());
        this.querySpecifications.addAll(this.view.getQueryGroup().getSpecifications());
        this.listener = new SpecificationSetUpdater();
        this.view.addViewListener(listener);
    }

    @Override
    public Set<IQuerySpecification<?>> getSpecifications() {
        return ImmutableSet.copyOf(querySpecifications);
    }

    /**
     * @return the packageName
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * @return the querySpecifications
     * @deprecated Use {@link #getSpecifications()} instead
     */
    public Set<IQuerySpecification<?>> getQuerySpecifications() {
        return getSpecifications();
    }

    /**
     * @return the includeSubPackages
     */
    public boolean isIncludeSubPackages() {
        return includeSubPackages;
    }

    /**
     * Refreshes the pattern group from the query specification registry based on the parameters used during the
     * initialization.
     */
    public void refresh() {
        // do nothing, view is automatically refreshed
    }

    /**
     * Listener to update the specification set
     * 
     * @author Abel Hegedus
     *
     */
    private final class SpecificationSetUpdater implements IRegistryChangeListener {
        @Override
        public void entryAdded(IQuerySpecificationRegistryEntry entry) {
            querySpecifications.add(entry.get());
        }
    
        @Override
        public void entryRemoved(IQuerySpecificationRegistryEntry entry) {
            querySpecifications.remove(entry.get());
        }
    }

    /**
     * Registry view filter that checks FQNs against the given package name.
     * 
     * @author Abel Hegedus
     *
     */
    private final class PackageNameBasedViewFilter implements IRegistryViewFilter {
        @Override
        public boolean isEntryRelevant(IQuerySpecificationRegistryEntry entry) {
            String fqn = entry.getFullyQualifiedName();
            if (packageName.length() + 1 < fqn.length()) {
                if (includeSubPackages) {
                    if (fqn.startsWith(packageName + '.')) {
                        return true;
                    }
                } else {
                    String name = fqn.substring(fqn.lastIndexOf('.') + 1, fqn.length());
                    if (fqn.equals(packageName + '.' + name)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

}
