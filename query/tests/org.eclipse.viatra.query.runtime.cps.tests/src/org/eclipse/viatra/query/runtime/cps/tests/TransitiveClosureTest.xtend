/*******************************************************************************
 * Copyright (c) 2014-2016 Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import java.util.Collection
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.StatesTCQuerySpecification
import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest
import org.junit.Test
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized)
class TransitiveClosureTest {
    
    @Parameters(name = "{0}")
    def static Collection<Object[]> testData() {
        #[
            #[ "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_tc_bug_520194.snapshot" ],
            #[ "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_tc_filtered_w_match_bug_520194.snapshot" ],
            #[ "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_tc_filtered_wo_match_bug_520194.snapshot" ]
        ]
    }
    
    @Parameter(0)
    public String snapshot
    
    extension org.eclipse.viatra.query.runtime.cps.tests.AllBackendTypes = new org.eclipse.viatra.query.runtime.cps.tests.AllBackendTypes
    
    @Test
    def void simpleTransitiveClosure() {
        ViatraQueryTest.test(StatesTCQuerySpecification.instance)
                        .with(snapshot)
                        .withAll
                        .assertEquals
    }

}
