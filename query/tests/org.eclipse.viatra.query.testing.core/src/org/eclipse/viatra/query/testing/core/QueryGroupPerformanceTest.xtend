/**
 * Copyright (c) 2010-2016, Gabor Bergmann, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
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