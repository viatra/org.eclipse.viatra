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
import org.eclipse.viatra.query.runtime.cps.tests.queries.SimpleCpsQueries
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.ApplicationInstancesOfApplicationTypeIdentifiersQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.ApplicationInstancesOfApplicationTypeQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.ApplicationInstancesQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.ApplicationTypeWithHostedInstanceIdentifiersQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.ApplicationTypeWithHostedInstancesQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.ApplicationTypeWithoutHostedInstanceIdentifiersQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.ApplicationTypeWithoutHostedInstanceQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.ApplicationTypesQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.CommunicateWithQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.FinalPatternQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.HasMoreCommunicationPartnerQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.HasMoreHostedApplicationInstancesQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.HasMoreHostedApplicationsQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.HasTheMostCommunicationPartnerQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.HasTheMostHostedApplicationInstancesQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.HasTheMostHostedApplicationsQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.HostInstanceWithAtLeastAsMuchTotalRamAsTotalHddQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.HostInstanceWithPrimeTotalRamQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.HostInstancesWithZeroTotalRamQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.HostedApplicationsQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.InTheCommunicationChainsQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.InstancesQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.TransitionsOfApplicationTypeIdentifiersQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.TransitionsOfApplicationTypeQuerySpecification
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest
import org.eclipse.viatra.query.testing.core.coverage.CoverageAnalyzer
import org.eclipse.viatra.query.testing.core.coverage.CoverageReporter
import org.junit.AfterClass
import org.junit.Assume
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.HasMoreHostedApplications2QuerySpecification
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParserBuilder

@RunWith(Parameterized)
class BasicCpsTest extends AbstractQueryComparisonTest {
    
    static var CoverageAnalyzer coverage;
    
    override getSnapshotUri() {
         "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test.snapshot"
    }
    
    @BeforeClass
    static def void beforeClass(){
        coverage = new CoverageAnalyzer();
    }
    
    
    @AfterClass
    static def void afterClass(){
        CoverageReporter.reportHtml(coverage, new File("BasicCpsTest_coverage.html"))
    }
    
    @Test
    def void testAllQueries() {
        Assume.assumeTrue(type != BackendType.LocalSearch_NoBase) // This test takes 20+ seconds to run without base 
        SimpleCpsQueries.instance.specifications.forEach[
            ViatraQueryTest.test(it as IQuerySpecification<ViatraQueryMatcher<IPatternMatch>>).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals
        ]
    }
    
    @Test
    def void testApplicationTypes() {
        ViatraQueryTest.test("org.eclipse.viatra.query.runtime.cps.tests.queries.applicationTypes")
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void testApplicationInstances() {
        ViatraQueryTest.test("org.eclipse.viatra.query.runtime.cps.tests.queries.applicationInstances")
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void testApplicationInstancesOfApplicationType() {
        ViatraQueryTest.test("org.eclipse.viatra.query.runtime.cps.tests.queries.applicationInstancesOfApplicationType")
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void testApplicationInstancesOfApplicationTypeIdentifiers() {
        ViatraQueryTest.test("org.eclipse.viatra.query.runtime.cps.tests.queries.applicationInstancesOfApplicationTypeIdentifiers")
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void testApplicationTypeWithHostedInstances() {
        ViatraQueryTest.test("org.eclipse.viatra.query.runtime.cps.tests.queries.applicationTypeWithHostedInstances")
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void testApplicationTypeWithHostedInstanceIdentifiers() {
        ViatraQueryTest.test("org.eclipse.viatra.query.runtime.cps.tests.queries.applicationTypeWithHostedInstanceIdentifiers")
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void testApplicationTypeWithoutHostedInstance() {
        ViatraQueryTest.test("org.eclipse.viatra.query.runtime.cps.tests.queries.applicationTypeWithoutHostedInstance")
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void testApplicationTypeWithoutHostedInstanceIdentifiers() {
        ViatraQueryTest.test("org.eclipse.viatra.query.runtime.cps.tests.queries.applicationTypeWithoutHostedInstanceIdentifiers")
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void testTransitionsOfApplicationType() {
        ViatraQueryTest.test("org.eclipse.viatra.query.runtime.cps.tests.queries.transitionsOfApplicationType")
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void testTransitionsOfApplicationTypeIdentifiers() {
        ViatraQueryTest.test("org.eclipse.viatra.query.runtime.cps.tests.queries.transitionsOfApplicationTypeIdentifiers")
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void testHostInstancesWithZeroTotalRam() {
        ViatraQueryTest.test("org.eclipse.viatra.query.runtime.cps.tests.queries.hostInstancesWithZeroTotalRam")
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void testHostInstanceWithAtLeastAsMuchTotalRamAsTotalHdd() {
        ViatraQueryTest.test("org.eclipse.viatra.query.runtime.cps.tests.queries.hostInstanceWithAtLeastAsMuchTotalRamAsTotalHdd")
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void testHostInstanceWithPrimeTotalRam() {
        ViatraQueryTest.test("org.eclipse.viatra.query.runtime.cps.tests.queries.hostInstanceWithPrimeTotalRam")
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    // This is necessary because of 490761 bug
    @Test
    def void testHasMoreHostedApplicationInstances() {
        ViatraQueryTest.test("org.eclipse.viatra.query.runtime.cps.tests.queries.hasMoreHostedApplicationInstances")
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    // This is necessary because of 490761 bug
    @Test
    def void testHasTheMostHostedApplicationInstances() {
        ViatraQueryTest.test("org.eclipse.viatra.query.runtime.cps.tests.queries.hasTheMostHostedApplicationInstances")
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void testCommunicateWith() {
        ViatraQueryTest.test("org.eclipse.viatra.query.runtime.cps.tests.queries.communicateWith")
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void testInTheCommunicationChains() {
        ViatraQueryTest.test("org.eclipse.viatra.query.runtime.cps.tests.queries.inTheCommunicationChains")
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void testHasMoreCommunicationPartner() {
        ViatraQueryTest.test("org.eclipse.viatra.query.runtime.cps.tests.queries.hasMoreCommunicationPartner")
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void testHasTheMostCommunicationPartner() {
        ViatraQueryTest.test("org.eclipse.viatra.query.runtime.cps.tests.queries.hasTheMostCommunicationPartner")
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void testHostedApplications() {
        ViatraQueryTest.test("org.eclipse.viatra.query.runtime.cps.tests.queries.hostedApplications")
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void testHasMoreHostedApplications() {
        ViatraQueryTest.test("org.eclipse.viatra.query.runtime.cps.tests.queries.hasMoreHostedApplications")
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals
    }
    @Test
    def void testHasMoreHostedApplications2() {
        ViatraQueryTest.test("org.eclipse.viatra.query.runtime.cps.tests.queries.hasMoreHostedApplications2")
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals
    }
    @Test
    def void testHasMoreHostedApplications2_generic() {
        new EMFPatternLanguageStandaloneSetup().createStandaloneInjector
        val IQuerySpecification spec = PatternParserBuilder.instance().parse('''
            package org.eclipse.viatra.query.runtime.cps.tests.queries
            
            import "http://org.eclipse.viatra/model/cps"
            
            pattern hasMoreHostedApplications2(HI1 : HostInstance, HI2 : HostInstance) {
                N == count HostInstance.applications(HI1, _AI1);
                M == count HostInstance.applications(HI2, _AI2);
                check(N > M);
            }
        ''').getQuerySpecification("org.eclipse.viatra.query.runtime.cps.tests.queries.hasMoreHostedApplications2").get
        ViatraQueryTest.test(spec)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void testHasTheMostHostedApplications() {
        ViatraQueryTest.test("org.eclipse.viatra.query.runtime.cps.tests.queries.hasTheMostHostedApplications")
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void testFinalPattern() {
        Assume.assumeTrue(type != BackendType.LocalSearch_NoBase) // This test takes 20+ seconds to run without base
        ViatraQueryTest.test("org.eclipse.viatra.query.runtime.cps.tests.queries.finalPattern")
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void testInstances() {
        ViatraQueryTest.test("org.eclipse.viatra.query.runtime.cps.tests.queries.instances")
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    
    @Test
    def void mfTestApplicationTypes() {
        ViatraQueryTest.test(ApplicationTypesQuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void mfTestApplicationInstances() {
        ViatraQueryTest.test(ApplicationInstancesQuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void mfTestApplicationInstancesOfApplicationType() {
        ViatraQueryTest.test(ApplicationInstancesOfApplicationTypeQuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void mfTestApplicationInstancesOfApplicationTypeIdentifiers() {
        ViatraQueryTest.test(ApplicationInstancesOfApplicationTypeIdentifiersQuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void mfTestApplicationTypeWithHostedInstances() {
        ViatraQueryTest.test(ApplicationTypeWithHostedInstancesQuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void mfTestApplicationTypeWithHostedInstanceIdentifiers() {
        ViatraQueryTest.test(ApplicationTypeWithHostedInstanceIdentifiersQuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void mfTestApplicationTypeWithoutHostedInstance() {
        ViatraQueryTest.test(ApplicationTypeWithoutHostedInstanceQuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void mfTestApplicationTypeWithoutHostedInstanceIdentifiers() {
        ViatraQueryTest.test(ApplicationTypeWithoutHostedInstanceIdentifiersQuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void mfTestTransitionsOfApplicationType() {
        ViatraQueryTest.test(TransitionsOfApplicationTypeQuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void mfTestTransitionsOfApplicationTypeIdentifiers() {
        ViatraQueryTest.test(TransitionsOfApplicationTypeIdentifiersQuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void mfTestHostInstancesWithZeroTotalRam() {
        ViatraQueryTest.test(HostInstancesWithZeroTotalRamQuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void mfTestHostInstanceWithAtLeastAsMuchTotalRamAsTotalHdd() {
        ViatraQueryTest.test(HostInstanceWithAtLeastAsMuchTotalRamAsTotalHddQuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void mfTestHostInstanceWithPrimeTotalRam() {
        ViatraQueryTest.test(HostInstanceWithPrimeTotalRamQuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void mfTestHasMoreHostedApplicationInstances() {
        ViatraQueryTest.test(HasMoreHostedApplicationInstancesQuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void mfTestHasTheMostHostedApplicationInstances() {
        ViatraQueryTest.test(HasTheMostHostedApplicationInstancesQuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void mfTestCommunicateWith() {
        ViatraQueryTest.test(CommunicateWithQuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void mfTestInTheCommunicationChains() {
        ViatraQueryTest.test(InTheCommunicationChainsQuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void mfTestHasMoreCommunicationPartner() {
        ViatraQueryTest.test(HasMoreCommunicationPartnerQuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void mfTestHasTheMostCommunicationPartner() {
        ViatraQueryTest.test(HasTheMostCommunicationPartnerQuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void mfTestHostedApplications() {
        ViatraQueryTest.test(HostedApplicationsQuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void mfTestHasMoreHostedApplications() {
        ViatraQueryTest.test(HasMoreHostedApplicationsQuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void mfTestHasMoreHostedApplications2() {
        ViatraQueryTest.test(HasMoreHostedApplications2QuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void mfTestHasTheMostHostedApplications() {
        Assume.assumeTrue(type != BackendType.LocalSearch_NoBase) // This test takes 20+ seconds to run without base
        ViatraQueryTest.test(HasTheMostHostedApplicationsQuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void mfTestFinalPattern() {
        Assume.assumeTrue(type != BackendType.LocalSearch_NoBase) // This test takes 20+ seconds to run without base
        ViatraQueryTest.test(FinalPatternQuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    @Test
    def void mfTestInstances() {
        ViatraQueryTest.test(InstancesQuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals 
    }
    
    @Test
    def void wildCardTestFinalPattern() {
        Assume.assumeTrue(type != BackendType.LocalSearch_NoBase) // This test takes 20+ seconds to run without base
        ViatraQueryTest.test(FinalPatternQuerySpecification.instance).analyzeWith(coverage)
                        .on(scope)
                        .with(snapshot)
                        .with(type.hints)
                        .assertEquals
    }
}
