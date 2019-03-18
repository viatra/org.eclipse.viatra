/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.registry.impl;

import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistry;
import org.eclipse.viatra.query.runtime.registry.IRegistryViewFilter;
import org.eclipse.viatra.query.runtime.registry.view.AbstractRegistryView;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryEntry;

/**
 * Registry view implementation that uses a filter to delegate the decision on whether 
 * a given specification is relevant to the view.
 * 
 * @author Abel Hegedus
 *
 */
public class FilteringRegistryView extends AbstractRegistryView {

    private IRegistryViewFilter filter;

    /**
     * Creates a new filtering view instance.
     * 
     * @param registry that defines the view
     * @param filter that is used for deciding relevancy
     */
    public FilteringRegistryView(IQuerySpecificationRegistry registry, IRegistryViewFilter filter, boolean allowDuplicateFQNs) {
        super(registry, allowDuplicateFQNs);
        this.filter = filter;
    }

    @Override
    protected boolean isEntryRelevant(IQuerySpecificationRegistryEntry entry) {
        return filter.isEntryRelevant(entry);
    }

}
