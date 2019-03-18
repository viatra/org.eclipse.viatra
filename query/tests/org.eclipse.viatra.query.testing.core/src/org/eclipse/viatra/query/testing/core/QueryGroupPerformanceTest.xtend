/**
 * Copyright (c) 2010-2016, Gabor Bergmann, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.query.testing.core

import org.eclipse.viatra.query.testing.core.QueryPerformanceTest

/**
 * A variant of {@link QueryPerformanceTest}, where an ensemble of queries is measured together, instead of as individual queries.
 * 
 * @author Gabor Bergmann
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