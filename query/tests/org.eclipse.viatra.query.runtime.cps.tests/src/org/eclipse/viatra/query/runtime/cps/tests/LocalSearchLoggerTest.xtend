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

import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.ApplicationTypesQuerySpecification
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchBackend
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.util.Collection
import org.junit.runners.Parameterized.Parameter
import org.junit.After
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.ApplicationType
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.FinalPatternQuerySpecification
import org.eclipse.viatra.query.runtime.localsearch.ExecutionLoggerAdapter

@RunWith(Parameterized)
class LocalSearchLoggerTest {

    val modelPath = "org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem"

    @Parameters(name="{index} : {0}")
    static def Collection<Object[]> data() {
        return #[
            #[BackendType.LocalSearch]//,
//            #[BackendType.LocalSearch_Flat],
//            #[BackendType.LocalSearch_NoBase],
//            #[BackendType.LocalSearch_Generic]
        ]
    }

    @Parameter(0)
    public var BackendType type

    ResourceSet rs
    ExecutionLoggerAdapter logger 
    AdvancedViatraQueryEngine engine

    @Before
    def void prepareTest() {
        rs = new ResourceSetImpl
        rs.getResource(URI.createPlatformPluginURI(modelPath, true), true)
        
        val hints = type.hints
        val options = ViatraQueryEngineOptions.defineOptions.withDefaultHint(hints).build

        val scope = new EMFScope(rs)
        engine = AdvancedViatraQueryEngine.createUnmanagedEngine(scope, options)

        logger = new ExecutionLoggerAdapter[/* NOP logging - this test only temporary*/]
        val backend = engine.getQueryBackend(hints.queryBackendFactory) as LocalSearchBackend
        backend.addAdapter(logger)
    }

    @After
    def void tearDown() {
            engine?.dispose
    }
    
    @Test
    def void simpleUnboundQueryProfileTest() {
        val query = ApplicationTypesQuerySpecification.instance
        engine.getMatcher(query).allMatches
        
//        val profileString = profiler.toString
//        Assert.assertFalse(profileString.isEmpty)
//        Assert.assertTrue(profileString.contains(query.fullyQualifiedName))
    }
    
    @Test
    def void simpleBoundQueryProfileTest() {
        val at = rs.allContents.findFirst[o | o instanceof ApplicationType] as ApplicationType
        
        val query = ApplicationTypesQuerySpecification.instance
        engine.getMatcher(query).getAllMatches(at)
        
//        val profileString = profiler.toString
//        Assert.assertFalse(profileString.isEmpty)
//        Assert.assertTrue(profileString.contains(query.fullyQualifiedName))
    }
    
    @Test
    def void complexQueryProfileTest() {
        val query = FinalPatternQuerySpecification.instance
        engine.getMatcher(query).getAllMatches
        
//        val profileString = profiler.toString
//        Assert.assertFalse(profileString.isEmpty)
//        Assert.assertTrue(profileString.contains(query.fullyQualifiedName))
//        Assert.assertTrue(profileString.contains(HostedApplicationsQuerySpecification.instance.fullyQualifiedName))
    }

}
