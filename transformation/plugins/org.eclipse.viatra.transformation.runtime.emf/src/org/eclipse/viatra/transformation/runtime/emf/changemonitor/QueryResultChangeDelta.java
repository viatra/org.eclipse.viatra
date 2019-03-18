/**
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.runtime.emf.changemonitor;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;

/**
 * This delta class stores differences for a single query specification instance.
 * @since 2.0
 */
public class QueryResultChangeDelta {

    private final IQuerySpecification<?> specification;
    
    private final Set<IPatternMatch> appeared;
    private final Set<IPatternMatch> updated;
    private final Set<IPatternMatch> disappeared;
    
    public QueryResultChangeDelta(IQuerySpecification<?> specification) {
        this.specification = specification;
        this.appeared = new HashSet<>();
        this.updated = new HashSet<>();
        this.disappeared = new HashSet<>();
    }

    public IQuerySpecification<?> getSpecification() {
        return specification;
    }


    /**
     * Returns the matches that are added to this delta
     */
    public Set<IPatternMatch> getAppeared() {
        return appeared;
    }


    /**
     * Returns the matches that were updated in this delta
     */
    public Set<IPatternMatch> getUpdated() {
        return updated;
    }

    /**
     * Returns the matches that have disappeared in this delta
     */
    public Set<IPatternMatch> getDisappeared() {
        return disappeared;
    }

    /**
     * Returns whether this delta contains changes of any kind
     */
    public boolean hasChanges() {
        return !appeared.isEmpty() || !updated.isEmpty() || !disappeared.isEmpty(); 
    }

    @Override
    public String toString() {
        return "QueryResultChangeDelta [specification=" + specification + ", appeared=" + appeared + ", updated="
                + updated + ", disappeared=" + disappeared + "]";
    }

}
