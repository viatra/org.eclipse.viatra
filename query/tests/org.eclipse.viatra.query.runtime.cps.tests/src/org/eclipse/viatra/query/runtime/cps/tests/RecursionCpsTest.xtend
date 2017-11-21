/*******************************************************************************
 * Copyright (c) 2014-2016 IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Akos Horvath, Abel Hegedus, Akos Menyhert, Tamas Borbas, Marton Bur, Zoltan Ujhelyi, Daniel Segesdi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import java.io.File
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.HostInstance
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.IncreasingAlphabeticalCommunicationChainRecQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.IncreasingAlphabeticalCommunicationChainTCQuerySpecification
import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest
import org.eclipse.viatra.query.testing.core.coverage.CoverageAnalyzer
import org.eclipse.viatra.query.testing.core.coverage.CoverageReporter
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.viatra.query.testing.core.ModelLoadHelper
import org.junit.Before
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.query.runtime.emf.EMFScope

class RecursionCpsTest {
    String snpRecOrig = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_recursion_chainRec.snapshot"
    String snpRecModified = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_recursion_communicationRemoved_chainRec.snapshot"
    String snpTCOrig = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_recursion_chainTC.snapshot"
    String snpTCModified = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test_recursion_communicationRemoved_chainTC.snapshot"
    
    static var CoverageAnalyzer coverage;
    
    @BeforeClass
    static def void before(){
        coverage = new CoverageAnalyzer();
    }
    
    @AfterClass
    static def void after(){
        CoverageReporter.reportHtml(coverage, new File("RecursionCpsTest_coverage.html"))
    }
    
    var ResourceSet set
    var EMFScope scope

    extension ModelLoadHelper = new ModelLoadHelper
    
    @Before
    def void initialize() {
        set = new ResourceSetImpl
        set.loadAdditionalResourceFromUri("org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem")
        scope = new EMFScope(set)
    }
    
    @Test
    def void staticRecursionTest() {
        ViatraQueryTest.test(IncreasingAlphabeticalCommunicationChainRecQuerySpecification.instance)
                        .analyzeWith(coverage)
                        .on(scope)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(set.loadExpectedResultsFromUri(snpRecOrig))
                        .assertEquals
    }
    
    @Test
    def void staticTransitiveClosureTest() {
        ViatraQueryTest.test(IncreasingAlphabeticalCommunicationChainTCQuerySpecification.instance)
                        .analyzeWith(coverage)
                        .on(scope)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(set.loadExpectedResultsFromUri(snpTCOrig))
                        .assertEquals
    }
    
    @Test
    def void removeCommunicationRecursionTest() {
        ViatraQueryTest.test(IncreasingAlphabeticalCommunicationChainRecQuerySpecification.instance)
                        .analyzeWith(coverage)
                        .on(scope)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(set.loadExpectedResultsFromUri(snpRecOrig))
                        .assertEqualsThen
                        .modify( HostInstance,
                            [it.identifier == "simple.cps.host.SecondHostClass0.inst1"],
                            [ hostInst | hostInst.communicateWith.clear ] )
                        .with(set.loadExpectedResultsFromUri(snpRecModified))
                        .assertEquals
    }
    
    @Test
    def void removeCommunicationTransitiveClosureTest() {
        ViatraQueryTest.test(IncreasingAlphabeticalCommunicationChainTCQuerySpecification.instance)
                        .analyzeWith(coverage)
                        .on(scope)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(set.loadExpectedResultsFromUri(snpTCOrig))
                        .assertEqualsThen
                        .modify( HostInstance,
                            [it.identifier == "simple.cps.host.SecondHostClass0.inst1"],
                            [ hostInst | hostInst.communicateWith.clear ] )
                        .with(set.loadExpectedResultsFromUri(snpTCModified))
                        .assertEquals
    }
}
