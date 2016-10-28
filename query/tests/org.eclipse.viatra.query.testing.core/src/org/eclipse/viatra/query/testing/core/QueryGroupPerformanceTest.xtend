package org.eclipse.viatra.query.testing.core

import org.eclipse.viatra.query.testing.core.QueryPerformanceTest

/**
 * A variant of {@link QueryPerformanceTest}, where an ensemble of queries is measured together, instead of as individual queries.
 */
abstract class QueryGroupPerformanceTest extends QueryPerformanceTest {
    override measureEntireGroup() {
        val group = queryGroup
        
        val usedHeapBefore = wipeAndMeasure        
        performMeasurements("*group*", -1, usedHeapBefore) [
            group.prepare(it)
            
            // match counting not meaningful for a group, no single matcher built
            return null
        ]        
    }
}