/** 
 * Copyright (c) 2010-2017, Grill Balázs, IncQueryLabs
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.query.testing.core.coverage

import java.util.HashMap
import java.util.Map
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody
import org.eclipse.viatra.query.runtime.matchers.psystem.PTraceable
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery

/** 
 * Associates {@link CoverageState}s to objects.
 * @param <T> type of objects whose coverages are stored
 * @since 1.6
 */
class CoverageInfo<T> extends HashMap<CoverageContext<T>, CoverageState> {
    protected static final long serialVersionUID = -8699692647123679741L

    /** 
     * Merge coverage. The keys of this and other should be disjunct.
     */
    def CoverageInfo<T> mergeWith(CoverageInfo<T> other) {
        val CoverageInfo<T> result = new CoverageInfo()
        result.putAll(this)
        result.putAll(other)
        return result
    }
    
    /**
     * Returns the coverage for elements aggregated per scope.
     */
    def Map<T, CoverageState> getElementCoverage() {
        val Map<T, CoverageState> result = newHashMap
        for (it : entrySet) {
            val element = key.element
            val CoverageState state = value
            val CoverageState otherState = result.get(element)
            if ((state !== null) && (otherState !== null)) {
                result.put(element, state.best(otherState))
            } else {
                result.put(element, state ?: otherState)
            }
        }
        result
    }

    /**
     * Returns the ratio of the covered and the represented {@link PBody}s contained in this info, in percents.
     */
    def double getAggregatedCoveragePercent() {
        elementCoverage.keySet.filter(PBody).coveragePercent
    }
    
    /**
     * Returns the ratio of the given covered and the represented {@link PTraceable}s, in percents.
     */
    def double getCoveragePercent(Iterable<? extends PTraceable> traceables) {
        val elementCoverage = getElementCoverage
        val coveredCount = traceables.filter[elementCoverage.get(it) == CoverageState.COVERED].size as double
        val representedCount = traceables.filter[#{CoverageState.COVERED, CoverageState.NOT_COVERED}.contains(elementCoverage.get(it))].size
        if (representedCount == 0) {
            0
        } else {
            coveredCount / representedCount * 100
        }        
    }

    /**
     * Returns the ratio of the covered and the represented {@link PBody}s contained in the given {@link PQuery}, in percents.
     */
    def double getCoveragePercent(PQuery query) {
        query.disjunctBodies.bodies.coveragePercent
    }
    
}
