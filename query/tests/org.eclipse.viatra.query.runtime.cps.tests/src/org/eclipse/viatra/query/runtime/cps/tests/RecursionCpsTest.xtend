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
    
    @Test
    def void staticRecursionTest() {
        ViatraQueryTest.test(IncreasingAlphabeticalCommunicationChainRecQuerySpecification.instance)
                        .analyzeWith(coverage)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(snpRecOrig)
                        .assertEquals
    }
    
    @Test
    def void staticTransitiveClosureTest() {
        ViatraQueryTest.test(IncreasingAlphabeticalCommunicationChainTCQuerySpecification.instance)
                        .analyzeWith(coverage)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(snpTCOrig)
                        .assertEquals
    }
    
    @Test
    def void removeCommunicationRecursionTest() {
        ViatraQueryTest.test(IncreasingAlphabeticalCommunicationChainRecQuerySpecification.instance)
                        .analyzeWith(coverage)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(snpRecOrig)
                        .assertEqualsThen
                        .modify( HostInstance,
                            [it.identifier == "simple.cps.host.SecondHostClass0.inst1"],
                            [ hostInst | hostInst.communicateWith.clear ] )
                        .with(snpRecModified)
                        .assertEquals
    }
    
    @Test
    def void removeCommunicationTransitiveClosureTest() {
        ViatraQueryTest.test(IncreasingAlphabeticalCommunicationChainTCQuerySpecification.instance)
                        .analyzeWith(coverage)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(snpTCOrig)
                        .assertEqualsThen
                        .modify( HostInstance,
                            [it.identifier == "simple.cps.host.SecondHostClass0.inst1"],
                            [ hostInst | hostInst.communicateWith.clear ] )
                        .with(snpTCModified)
                        .assertEquals
    }
}
