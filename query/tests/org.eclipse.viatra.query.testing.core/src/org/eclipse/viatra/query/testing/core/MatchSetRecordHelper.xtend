/**
 * Copyright (c) 2010-2015, Tamas Borbas, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Tamas Borbas - initial API and implementation
 */
package org.eclipse.viatra.query.testing.core

import org.eclipse.viatra.query.testing.core.internal.MatchSetRecordDiff
import org.eclipse.viatra.query.testing.snapshot.MatchSetRecord

class MatchSetRecordHelper {
    extension SnapshotHelper = new SnapshotHelper

    public static val String UNEXPECTED_MATCH = "Unexpected match"
    public static val String EXPECTED_NOT_FOUND = "Expected match not found"
    
    def computeDiff(MatchSetRecord expected, MatchSetRecord actual) {
        val matchdiff = MatchSetRecordDiff.compute(expected, actual)
        val diff = newHashSet
        diff.addAll(matchdiff.additions.map[UNEXPECTED_MATCH + " (" + it.prettyPrint + ")"])
        diff.addAll(matchdiff.removals.map[EXPECTED_NOT_FOUND + " (" + it.prettyPrint + ")"])

        return diff
    }
}