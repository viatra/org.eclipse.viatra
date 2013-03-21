/*******************************************************************************
 * Copyright (c) 2010-2013, Adam Dudas, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Adam Dudas - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.base.api;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Helper utility class for functional dependency analysis.
 * 
 * Throughout this class attribute sets are represented as generic sets and functional dependencies as maps from
 * attribute set (generic sets) to attribute set (generic sets)
 * 
 * @author Adam Dudas
 * 
 */
public class FunctionalDependencyHelper {
    /**
     * Get the closure of the specified attribute set relative to the specified functional dependencies.
     * 
     * @param attributes
     *            The attributes to get the closure of.
     * @param dependencies
     *            The functional dependencies of which the closure operation is relative to.
     * @return The closure of the specified attribute set relative to the specified functional dependencies.
     */
    public static <A> Set<A> closureOf(Set<A> attributes, Map<Set<A>, Set<A>> dependencies) {
        Set<A> closureSet = new HashSet<A>();

        for (Set<A> closureSet1 = new HashSet<A>(attributes); closureSet.addAll(closureSet1);) {
            closureSet1 = new HashSet<A>(closureSet);
            for (Entry<Set<A>, Set<A>> dependency : dependencies.entrySet()) {
                if (closureSet.containsAll(dependency.getKey()))
                    closureSet1.addAll(dependency.getValue());
            }
        }

        return closureSet;
    }
}
