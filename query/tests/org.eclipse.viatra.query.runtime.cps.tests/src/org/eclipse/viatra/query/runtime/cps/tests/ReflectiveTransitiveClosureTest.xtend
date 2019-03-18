/*******************************************************************************
 * Copyright (c) 2014-2018 Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import java.util.Collection
import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest
import org.junit.Test
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.viatra.query.testing.core.ModelLoadHelper
import org.junit.Before
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.StatesRTC1QuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.StatesRTC2QuerySpecification
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.runtime.api.IPatternMatch

@RunWith(Parameterized)
class ReflectiveTransitiveClosureTest {
    
    @Parameters(name = "{0}")
    def static Collection<Object[]> testData() {
        #[
            #[ StatesRTC1QuerySpecification.instance ],
            #[ StatesRTC2QuerySpecification.instance ]
        ]
    }
    
    @Parameter(0)
    public IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> query
    
    var ResourceSet set
    var EMFScope scope

    extension org.eclipse.viatra.query.runtime.cps.tests.AllBackendTypes = new org.eclipse.viatra.query.runtime.cps.tests.AllBackendTypes
    extension ModelLoadHelper = new ModelLoadHelper
    
    @Before
    def void initialize() {
        set = new ResourceSetImpl
        scope = new EMFScope(set)
    }
    
    @Test
    def void simpleReflectiveTransitiveClosure() {
        ViatraQueryTest.test(query)
                        .on(scope)
                        .with(set.loadExpectedResultsFromUri("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_rtc.snapshot"))
                        .withAll
                        .assertEquals
    }

}
