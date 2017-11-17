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

import org.eclipse.viatra.query.runtime.cps.tests.queries.util.HostInstanceWithMinCPU1QuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.HostInstanceWithMinCPU2QuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.MinCPUQuerySpecification
import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest
import org.junit.Test
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.SumCPUQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.AvgCPUQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.HasOddApplicationsQuerySpecification
import org.junit.runners.Parameterized.Parameters
import java.util.Collection
import org.junit.runners.Parameterized
import org.junit.runner.RunWith
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.common.util.URI

@RunWith(Parameterized)
class AggregatorTest {
        
    extension org.eclipse.viatra.query.runtime.cps.tests.AllBackendTypes = new org.eclipse.viatra.query.runtime.cps.tests.AllBackendTypes
    
    
    @Parameters(name = "Model: {0}")
    def static Collection<Object[]> testData() {
        newArrayList(
            #[ 
                "org.eclipse.viatra.query.runtime.cps.tests/models/instances/aggregators.cyberphysicalsystem"
            ]
        )
    }
    
    val String modelPath
    val EMFScope scope
    
    new(String modelPath) {
        this.modelPath = modelPath
        val rs = new ResourceSetImpl
        
        rs.getResource(URI.createPlatformPluginURI(modelPath, true), true)
        
        scope = new EMFScope(rs)
    }
    
    @Test
    def void testMinCPU1() {
        ViatraQueryTest.test(MinCPUQuerySpecification::instance)
                        .on(scope)
                        .withAll
                        .assertEquals 
    }
    
    @Test
    def void testMinCPU2() {
        ViatraQueryTest.test(HostInstanceWithMinCPU1QuerySpecification::instance)
                        .on(scope)
                        .withAll
                        .assertEquals 
    }
    
    @Test
    def void testMinCPU3() {
        ViatraQueryTest.test(HostInstanceWithMinCPU2QuerySpecification::instance)
                        .on(scope)
                        .withAll
                        .assertEquals 
    }
    
    @Test
    def void testSumCPU() {
        ViatraQueryTest.test(SumCPUQuerySpecification::instance)
                        .on(scope)
                        .withAll
                        .assertEquals 
    }
    
    @Test
    def void testHasOddApplications() {
        ViatraQueryTest.test(HasOddApplicationsQuerySpecification::instance)
                        .on(scope)
                        .withAll
                        .assertEquals 
    }
    
    @Test
    def void testAvgCPU() {
        ViatraQueryTest.test(AvgCPUQuerySpecification::instance)
                        .on(scope)
                        .withAll
                        .assertEquals 
    }

}
