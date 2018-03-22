/*******************************************************************************
 * Copyright (c) 2016 IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Balazs Grill, Tamas Szabo, Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import java.util.Collection
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.AvgCPU2QuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.AvgCPUQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.AvgRamRequirementQuerySpecification
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.testing.core.ModelLoadHelper
import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized)
class AvgAggregatorTest {
        
    extension AllBackendTypes = new AllBackendTypes
    extension ModelLoadHelper = new ModelLoadHelper
    
    
    @Parameters(name = "Model: {0} Snapshot : {1}")
    def static Collection<Object[]> testData() {
        newArrayList(
            #[ 
                // In this model, there are some CPU usage values set; according average calculation is necessary
                "org.eclipse.viatra.query.runtime.cps.tests/models/instances/avgdemo.cyberphysicalsystem",
                "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_avg_demo.snapshot"
            ],
            #[ 
                // In this model, all CPU values are set to 0; in this case 0 result is expected
                "org.eclipse.viatra.query.runtime.cps.tests/models/instances/aggregators.cyberphysicalsystem",
                "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_avg_aggregators.snapshot"
            ]
        )
    }
    
    val String modelPath
    val String snapshotPath
    val EMFScope scope
    
    new(String modelPath, String snapshotPath) {
        this.modelPath = modelPath
        this.snapshotPath = snapshotPath
        val rs = new ResourceSetImpl
        
        rs.getResource(URI.createPlatformPluginURI(modelPath, true), true)
        
        scope = new EMFScope(rs)
    }
    
    @Test
    def void testAvgCPU() {
        ViatraQueryTest.test(AvgCPUQuerySpecification::instance)
                        .on(scope)
                        .withSnapshotMatches(snapshotPath.loadExpectedResultsFromUri)
                        .withAll
                        .assertEquals 
    }
    
    @Test
    def void testAvg2CPU() {
        ViatraQueryTest.test(AvgCPU2QuerySpecification::instance)
                        .on(scope)
                        .withSnapshotMatches(snapshotPath.loadExpectedResultsFromUri)
                        .withAll
                        .assertEquals 
    }
    
    @Test
    def void testAvgRamRequirementNoMatch() {
        ViatraQueryTest.test(AvgRamRequirementQuerySpecification::instance)
                        .on(scope)
                        .withSnapshotMatches(snapshotPath.loadExpectedResultsFromUri)
                        .withAll
                        .assertEquals 
    }

}
