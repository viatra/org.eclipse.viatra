/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryresult.util

import java.util.Set
import org.eclipse.emf.common.notify.AdapterFactory
import org.eclipse.emf.edit.provider.ComposedAdapterFactory
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryEntry
import org.eclipse.viatra.query.tooling.ui.queryregistry.QueryRegistryTreeEntry
import org.eclipse.viatra.query.tooling.ui.queryregistry.QueryRegistryTreePackage
import org.eclipse.viatra.query.tooling.ui.queryregistry.QueryRegistryTreeSource
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory

/**
 * @author Abel Hegedus
 *
 */
class QueryResultViewUtil {
    
    /**
     * Constructor hidden for utility class
     */
    protected new() {
    }
    
    static def Set<QueryRegistryTreeEntry> getRegistryEntriesFromSelection(IStructuredSelection selection) {
        val selectedQueries = newHashSet()
        selection.iterator.forEach[
            switch it {
                QueryRegistryTreeEntry : selectedQueries.add(it)
                QueryRegistryTreePackage : selectedQueries.addAll(it.entries.values)
                QueryRegistryTreeSource : selectedQueries.addAll(it.packages.values.map[it.entries.values].flatten)
            }
        ]
        return selectedQueries
    }
    
    static def Iterable<IQuerySpecificationRegistryEntry> unwrapEntries(Set<QueryRegistryTreeEntry> entries) {
        return entries.map[entry]
    }
    
    /**
     * @since 1.4
     */
    static def AdapterFactory getGenericAdapterFactory() {
        val adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE)
        adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory)
        return adapterFactory
    }
}