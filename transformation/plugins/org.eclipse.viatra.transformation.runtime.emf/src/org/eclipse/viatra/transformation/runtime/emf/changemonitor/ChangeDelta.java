/**
 * Copyright (c) 2004-2015, Marton Bur, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Marton Bur, Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.transformation.runtime.emf.changemonitor;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;

/**
 * Class representing the changes in a given instance model since the last checkpoint. It is implemented as a set of
 * {@link QueryResultChangeDelta} instances that store deltas grouped by {@link IQuerySpecification} instances.
 * 
 * @author Lunk Péter
 */
public class ChangeDelta {
    private Map<IQuerySpecification<?>, QueryResultChangeDelta> map;

    /**
     * @since 2.0
     */
    public ChangeDelta(Map<IQuerySpecification<?>, QueryResultChangeDelta> delta) {
        super();
        this.map = delta;
    }

    @Override
    public int hashCode() {
        return Objects.hash(map);

    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChangeDelta other = (ChangeDelta) obj;

        return Objects.equals(map, other.map);
    }

    @Override
    public String toString() {
        return map.entrySet().stream().map(
                entry -> String.format("CHANGE: query: %s; changes %s", entry.getKey(), entry.getValue().toString()))
                .collect(Collectors.joining(", "));
                
    }

    /**
     * Return a list of query specifications that have corresponding changes.
     * @since 2.0
     */
    public Set<IQuerySpecification<?>> getChangedQuerySpecifications() {
        return map.keySet().stream().filter(spec -> map.containsKey(spec) && map.get(spec).hasChanges())
                .collect(Collectors.toSet());
    }
    
    /**
     * Returns a set of matches added in the selected change delta for the given query specification
     * 
     * @since 2.0
     */
    public Set<? extends IPatternMatch> getAppeared(IQuerySpecification<?> specification) {
        return this.map.get(specification).getAppeared();
    }
    
    /**
     * Returns a set of matches added to the selected change delta for all query specifications
     * @since 2.0
     */
    public Set<? extends IPatternMatch> getAllAppeared() {
        return this.map.values().stream().flatMap(delta -> delta.getAppeared().stream()).collect(Collectors.toSet());
    }

    /**
     * Returns a set of matches updated in the selected change delta for the given query specification
     * 
     * @since 2.0
     */
    public Set<? extends IPatternMatch> getUpdated(IQuerySpecification<?> specification) {
        return this.map.get(specification).getUpdated();
    }

    /**
     * Returns a set of matches updated in the selected change delta for all query specifications
     * @since 2.0
     */
    public Set<? extends IPatternMatch> getAllUpdated() {
        return this.map.values().stream().flatMap(delta -> delta.getUpdated().stream()).collect(Collectors.toSet());
    }
    
    /**
     * Returns a set of matches disappeared in the selected change delta for the given query specification
     * 
     * @since 2.0
     */
    public Set<? extends IPatternMatch> getDisappeared(IQuerySpecification<?> specification) {
        return this.map.get(specification).getDisappeared();
    }
    
    /**
     * Returns a set of matches disappeared in the selected change delta for all query specifications
     * @since 2.0
     */
    public Set<? extends IPatternMatch> getAllDisappeared() {
        return this.map.values().stream().flatMap(delta -> delta.getDisappeared().stream()).collect(Collectors.toSet());
    }
    
}
