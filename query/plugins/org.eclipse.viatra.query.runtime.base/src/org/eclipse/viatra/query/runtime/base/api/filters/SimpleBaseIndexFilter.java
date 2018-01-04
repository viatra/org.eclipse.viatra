/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.base.api.filters;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.notify.Notifier;

/**
 * An index filter that is based on a collection of {@link Notifier} instances.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class SimpleBaseIndexFilter implements IBaseIndexObjectFilter {

    Set<Notifier> filters;

    /**
     * Creates a filter using a collection of (Resource and) Notifier instances. Every containment subtree, selected by
     * the given Notifiers are filtered out.
     * 
     * @param filterConfiguration
     */
    public SimpleBaseIndexFilter(Collection<Notifier> filterConfiguration) {
        filters = new HashSet<>(filterConfiguration);
    }

    public SimpleBaseIndexFilter(SimpleBaseIndexFilter other) {
        this(other.filters);
    }

    @Override
    public boolean isFiltered(Notifier notifier) {
        return filters.contains(notifier);
    }

}
