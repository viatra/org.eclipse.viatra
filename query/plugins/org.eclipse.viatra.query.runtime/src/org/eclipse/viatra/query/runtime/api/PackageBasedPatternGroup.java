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

import org.eclipse.viatra.query.runtime.api.impl.BasePatternGroup;
import org.eclipse.viatra.query.runtime.extensibility.QuerySpecificationRegistry;

/**
 * Package based {@link BasePatternGroup} implementation. It handles patterns as a group within the same package.
 * 
 * @author Abel Hegedus, Mark Czotter
 * 
 */
public class PackageBasedPatternGroup extends BasePatternGroup {

    private final Set<IQuerySpecification<?>> querySpecifications = new HashSet<IQuerySpecification<?>>();
    private final String packageName;
    private final boolean includeSubPackages;

    public PackageBasedPatternGroup(String packageName) {
        this(packageName, false);
    }

    public PackageBasedPatternGroup(String packageName, boolean includeSubPackages) {
        super();
        this.packageName = packageName;
        this.includeSubPackages = includeSubPackages;
        refresh();
    }

    @Override
    public Set<IQuerySpecification<?>> getSpecifications() {
        return getQuerySpecifications();
    }

    /**
     * @return the packageName
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * @return the querySpecifications
     */
    public Set<IQuerySpecification<?>> getQuerySpecifications() {
        return querySpecifications;
    }

    /**
     * @return the includeSubPackages
     */
    public boolean isIncludeSubPackages() {
        return includeSubPackages;
    }

    /**
     * Refreshes the pattern group from the matcher registry based on the parameters used during the initialization
     */
    public void refresh() {
        refreshInternal();
    }

    private void refreshInternal() {
        if (isIncludeSubPackages()) {
            this.querySpecifications.addAll(QuerySpecificationRegistry.getPatternSubTree(this.packageName));
        } else {
            this.querySpecifications.addAll(QuerySpecificationRegistry.getPatternGroup(this.packageName));
        }
    }

}
