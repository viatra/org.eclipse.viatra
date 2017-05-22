/** 
 * Copyright (c) 2010-2017, Grill Balázs, IncQueryLabs
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Grill Balázs - initial API and implementation
 */
package org.eclipse.viatra.query.testing.core.coverage

import com.google.common.base.Objects
import com.google.common.collect.Sets
import com.google.common.math.DoubleMath
import java.util.HashMap
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQueries
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery

/** 
 * Associates {@link CoverageState}s to objects.
 * @param <T> type of objects whose coverages are stored
 * @since 1.6
 */
class CoverageInfo<T> extends HashMap<T, CoverageState> {
    protected static final long serialVersionUID = -8699692647123679741L

    /** 
     * Merge coverage. A key is considered covered, if it's covered in at least one of the infos.
     */
    def CoverageInfo<T> mergeWith(CoverageInfo<T> other) {
        var CoverageInfo<T> result = new CoverageInfo()
        for (T key : Sets.union(this.keySet(), other.keySet())) {
            var CoverageState state = get(key)
            var CoverageState otherState = other.get(key)
            if ((state !== null) && (otherState !== null)) {
                result.put(key, state.best(otherState))
            } else {
                result.put(key, Objects.firstNonNull(state, otherState))
            }
        }
        return result
    }

    def double getAggregatedCoveragePercent() {
        val queryCoverages = keySet.filter(PQuery).map[coveragePercent]
        if (queryCoverages.empty) 0 else DoubleMath.mean(queryCoverages)
    }

    def double getCoveragePercent(PQuery query) {
        val traceables = PQueries.getTraceables(query)
        val coveredCount = traceables.filter[get(it) == CoverageState.COVERED].size as double
        val representedCount = traceables.filter[#{CoverageState.COVERED, CoverageState.NOT_COVERED}.contains(get(it))].size;
        coveredCount / representedCount * 100
    }
    
}
