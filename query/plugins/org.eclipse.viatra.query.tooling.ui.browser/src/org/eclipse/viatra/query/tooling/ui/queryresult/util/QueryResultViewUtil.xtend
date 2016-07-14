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
package org.eclipse.viatra.query.tooling.ui.queryresult.util

import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.viatra.query.tooling.ui.queryregistry.QueryRegistryTreeEntry
import org.eclipse.viatra.query.tooling.ui.queryregistry.QueryRegistryTreePackage
import org.eclipse.viatra.query.tooling.ui.queryregistry.QueryRegistryTreeSource
import java.util.Set
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryEntry

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
        (selection as IStructuredSelection).iterator.forEach[
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
}