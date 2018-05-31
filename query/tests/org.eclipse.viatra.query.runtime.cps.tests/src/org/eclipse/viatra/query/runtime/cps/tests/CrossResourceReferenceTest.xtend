/*******************************************************************************
 * Copyright (c) 2010-2017, Krisztian Gabor Mayer, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Krisztian Gabor Mayer - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import java.util.Collection
import org.eclipse.emf.common.notify.Notifier
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.ApplicationInstance
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.ApplicationType
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.CyberPhysicalSystemFactory
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.HostInstance
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.HostType
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.runtime.api.scope.QueryScope
import org.eclipse.viatra.query.runtime.base.api.BaseIndexOptions
import org.eclipse.viatra.query.runtime.base.api.filters.IBaseIndexObjectFilter
import org.eclipse.viatra.query.runtime.base.api.filters.IBaseIndexResourceFilter
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.AppInstanceAllocatedToHostInstanceQuerySpecification

@RunWith(Parameterized)
class CrossResourceReferenceTest {

    static final extension CyberPhysicalSystemFactory factory = CyberPhysicalSystemFactory.eINSTANCE
    static final URI resourceAllInOneURI = URI.createURI("protocol://allinone")
    static final URI resourceEmptyURI = URI.createURI("protocol://empty")
    static final URI resourceHostsURI = URI.createURI("protocol://host")
    static final URI resourceAppsURI = URI.createURI("protocol://apps")

    ResourceSet resourceSet
    Resource resourceHosts
    Resource resourceApps
    Resource resourceEmpty
    Resource resourceAIO
    HostType myHostType
    HostInstance myHostInstance
    ApplicationType myAppType
    ApplicationInstance myAppInstance

    @Parameters(name="With DanglingFreeAssumption: {0}")
    def static Collection<Object[]> testData() {
        newArrayList(
            #["true",  true,  new BaseIndexOptions().withDanglingFreeAssumption(true)],
            #["false", false, new BaseIndexOptions().withDanglingFreeAssumption(false)],
            #["unset", true,  new BaseIndexOptions()]
        )
    }
    @Parameter(0)
    public var String desc
    @Parameter(1)
    public var boolean danglingFreeAssum
    @Parameter(2)
    public var BaseIndexOptions option

    // Resource filters:
    static class AppsResourceFilter implements IBaseIndexResourceFilter {
        override isResourceFiltered(Resource resource) {
            return resourceAppsURI.equals(resource.getURI());
        }

    }

    static class HostsResourceFilter implements IBaseIndexResourceFilter {
        override isResourceFiltered(Resource resource) {
            return resourceHostsURI.equals(resource.getURI());
        }

    }

    static class EmptyResourceFilter implements IBaseIndexResourceFilter {
        override isResourceFiltered(Resource resource) {
            return (resourceEmptyURI.equals(resource.getURI()));
        }

    }

    static class AllInOneResourceFilter implements IBaseIndexResourceFilter {
        override isResourceFiltered(Resource resource) {
            return (resourceAllInOneURI.equals(resource.getURI()));
        }

    }

    // Object Filters:
    @FinalFieldsConstructor
    static class ObjectFilter implements IBaseIndexObjectFilter {
        val EObject object;

        override isFiltered(Notifier notifier) {
            return notifier.equals(object)
        }
    }

    @FinalFieldsConstructor
    static class TwoObjectsFilter implements IBaseIndexObjectFilter {
        val EObject object1;
        val EObject object2;

        override isFiltered(Notifier notifier) {
            return (notifier.equals(object1) || notifier.equals(object2))
        }
    }

    def <Matcher extends ViatraQueryMatcher<? extends IPatternMatch>> Matcher getMatchesOnScopeWithQuerySpec(
        QueryScope scope, IQuerySpecification<Matcher> querySpecification) {
        val AdvancedViatraQueryEngine engine = AdvancedViatraQueryEngine.createUnmanagedEngine(scope);
        val Matcher = engine.getMatcher(querySpecification);
        return Matcher
    }

    @Before
    def void setUp() {
        resourceSet = new ResourceSetImpl()
        resourceHosts = resourceSet.createResource(resourceHostsURI)
        resourceApps = resourceSet.createResource(resourceAppsURI)
        resourceEmpty = resourceSet.createResource(resourceEmptyURI)
        resourceAIO = resourceSet.createResource(resourceAllInOneURI)
        myHostType = createHostType => [identifier = "MyHostType"]
        myHostInstance = createHostInstance => [identifier = "MyHostInstance"]
        myAppType = createApplicationType => [identifier = "MyAppType"]
        myAppInstance = createApplicationInstance => [identifier = "MyAppInstance"]
        myHostType.instances.add(myHostInstance)
        myAppType.instances.add(myAppInstance)
        myAppInstance.allocatedTo = myHostInstance
    }

    // RESOURCE, Target is in scope, Source is not
    @Test
    def void crossRefTest_1() {
        resourceHosts.contents += myHostType
        resourceApps.contents += myAppType

        val Collection<? extends IPatternMatch> matches = getMatchesOnScopeWithQuerySpec(
            new EMFScope(resourceHosts, option), // scope is on resourceHosts
            AppInstanceAllocatedToHostInstanceQuerySpecification.instance).allMatches;

        Assert.assertTrue(matches.empty)
    }

    // RESOURCE, Source is in scope, Target is not
    @Test
    def void crossRefTest_2() {
        resourceHosts.contents += myHostType
        resourceApps.contents += myAppType

        val Collection<? extends IPatternMatch> matches = getMatchesOnScopeWithQuerySpec(
            new EMFScope(resourceApps, option), // scope is on resourceApps
            AppInstanceAllocatedToHostInstanceQuerySpecification.instance).allMatches;

        Assert.assertFalse(matches.empty)
        Assert.assertEquals(matches.get(0).get(0), myAppInstance)
        Assert.assertEquals(matches.get(0).get(1), myHostInstance)
    }

    // RESOURCE, Target and Source are in the Scope
    @Test
    def void crossRefTest_3() {
        resourceAIO.contents += myHostType
        resourceAIO.contents += myAppType

        val Collection<? extends IPatternMatch> matches = getMatchesOnScopeWithQuerySpec(
            new EMFScope(resourceAIO, option), // scope is on resourceAIO
            AppInstanceAllocatedToHostInstanceQuerySpecification.instance).allMatches;

        Assert.assertFalse(matches.empty)
        Assert.assertEquals(matches.get(0).get(0), myAppInstance)
        Assert.assertEquals(matches.get(0).get(1), myHostInstance)
    }

    // RESOURCE, Target and Source are out of Scope
    @Test
    def void crossRefTest_4() {
        resourceAIO.contents += myHostType
        resourceAIO.contents += myAppType

        val Collection<? extends IPatternMatch> matches = getMatchesOnScopeWithQuerySpec(
            new EMFScope(resourceEmpty, option), // Scope is on resourceEmpty
            AppInstanceAllocatedToHostInstanceQuerySpecification.instance).allMatches;

        Assert.assertTrue(matches.empty)
    }

    // RESOURCESET, Target and Source are in the Scope
    @Test
    def void crossRefTest_5() {
        resourceHosts.contents += myHostType
        resourceApps.contents += myAppType

        val Collection<? extends IPatternMatch> matches = getMatchesOnScopeWithQuerySpec(
            new EMFScope(resourceSet, option), AppInstanceAllocatedToHostInstanceQuerySpecification.instance).
            allMatches;

        Assert.assertFalse(matches.empty)
        Assert.assertEquals(matches.get(0).get(0), myAppInstance)
        Assert.assertEquals(matches.get(0).get(1), myHostInstance)
    }

    // NODE FILTER + RESOURCESET, Target is in scope, Source is not (MyAppInstance is filtered)
    @Test
    def void crossRefTest_6() {
        resourceHosts.contents += myHostType
        resourceApps.contents += myAppType

        val baseOptions = option.withObjectFilterConfiguration(new ObjectFilter(myAppInstance)) // MyAppInstance is filtered
        val Collection<? extends IPatternMatch> matches = getMatchesOnScopeWithQuerySpec(
            new EMFScope(resourceSet, baseOptions), AppInstanceAllocatedToHostInstanceQuerySpecification.instance).
            allMatches;

        Assert.assertTrue(matches.empty)
    }

    // NODE FILTER + RESOURCESET, Source is in scope, Target is not (MyHostInstance is filtered)
    @Test
    def void crossRefTest_7() {
        resourceHosts.contents += myHostType
        resourceApps.contents += myAppType

        val baseOptions = option.withObjectFilterConfiguration(new ObjectFilter(myHostInstance)) // MyHostInstance is filtered
        val Collection<? extends IPatternMatch> matches = getMatchesOnScopeWithQuerySpec(
            new EMFScope(resourceSet, baseOptions), AppInstanceAllocatedToHostInstanceQuerySpecification.instance).
            allMatches;

        Assert.assertFalse(matches.empty)
        Assert.assertEquals(matches.get(0).get(0), myAppInstance)
        Assert.assertEquals(matches.get(0).get(1), myHostInstance)
    }

    // NODE FILTER + RESOURCESET, Target and Source are in the Scope (A Dummy EObject is filtered)
    @Test
    def void crossRefTest_8() {
        resourceHosts.contents += myHostType
        resourceApps.contents += myAppType

        val Resource resourceDummy = resourceSet.createResource(URI.createURI("protocol://dummy")) // we are going to filter the dummyhost
        val HostType MyDummyHostType = createHostType => [identifier = "MyDummyHostType"]
        resourceDummy.contents += MyDummyHostType

        val baseOptions = option.withDanglingFreeAssumption(danglingFreeAssum).withObjectFilterConfiguration(
            new ObjectFilter(MyDummyHostType)) // MyDummyHostType is filtered
        val Collection<? extends IPatternMatch> matches = getMatchesOnScopeWithQuerySpec(
            new EMFScope(resourceSet, baseOptions), AppInstanceAllocatedToHostInstanceQuerySpecification.instance).
            allMatches;

        Assert.assertFalse(matches.empty)
        Assert.assertEquals(matches.get(0).get(0), myAppInstance)
        Assert.assertEquals(matches.get(0).get(1), myHostInstance)
    }

    // NODE FILTER + RESOURCESET, Target and Source are out of Scope (MyHostInstance and  MyAppInstance is filtered)
    @Test
    def void crossRefTest_9() {
        resourceHosts.contents += myHostType
        resourceApps.contents += myAppType

        val baseOptions = option.withObjectFilterConfiguration(new TwoObjectsFilter(myHostInstance, myAppInstance))
        val Collection<? extends IPatternMatch> matches = getMatchesOnScopeWithQuerySpec(
            new EMFScope(resourceSet, baseOptions), AppInstanceAllocatedToHostInstanceQuerySpecification.instance).
            allMatches;

        Assert.assertTrue(matches.empty)
    }

    // RESOURCE FILTER + RESOURCESET, Target is in scope, Source is not
    @Test
    def void crossRefTest_10() {
        resourceHosts.contents += myHostType
        resourceApps.contents += myAppType

        val baseOptions = option.withResourceFilterConfiguration(new AppsResourceFilter())
        val Collection<? extends IPatternMatch> matches = getMatchesOnScopeWithQuerySpec(
            new EMFScope(resourceSet, baseOptions), AppInstanceAllocatedToHostInstanceQuerySpecification.instance).
            allMatches;

        Assert.assertTrue(matches.empty)
    }

    // RESOURCE FILTER + RESOURCESET, Source is in scope, Target is not
    @Test
    def void crossRefTest_11() {
        resourceHosts.contents += myHostType
        resourceApps.contents += myAppType

        val baseOptions = option.withResourceFilterConfiguration(new HostsResourceFilter())
        val Collection<? extends IPatternMatch> matches = getMatchesOnScopeWithQuerySpec(
            new EMFScope(resourceSet, baseOptions), AppInstanceAllocatedToHostInstanceQuerySpecification.instance).
            allMatches;

        Assert.assertFalse(matches.empty)
        Assert.assertEquals(matches.get(0).get(0), myAppInstance)
        Assert.assertEquals(matches.get(0).get(1), myHostInstance)
    }

    // RESOURCE FILTER + RESOURCESET, Target and Source are in the Scope
    @Test
    def void crossRefTest_12() {
        resourceAIO.contents += myHostType
        resourceAIO.contents += myAppType

        val baseOptions = option.withResourceFilterConfiguration(new EmptyResourceFilter())
        val Collection<? extends IPatternMatch> matches = getMatchesOnScopeWithQuerySpec(
            new EMFScope(resourceSet, baseOptions), AppInstanceAllocatedToHostInstanceQuerySpecification.instance).
            allMatches;

        Assert.assertFalse(matches.empty)
        Assert.assertEquals(matches.get(0).get(0), myAppInstance)
        Assert.assertEquals(matches.get(0).get(1), myHostInstance)
    }

    // RESOURCE FILTER + RESOURCESET, Target and Source are out of Scope
    @Test
    def void crossRefTest_13() {
        resourceAIO.contents += myHostType
        resourceAIO.contents += myAppType

        val baseOptions = option.withResourceFilterConfiguration(new AllInOneResourceFilter())         
        val Collection<? extends IPatternMatch> matches = getMatchesOnScopeWithQuerySpec(
            new EMFScope(resourceSet, baseOptions), AppInstanceAllocatedToHostInstanceQuerySpecification.instance).
            allMatches;

        Assert.assertTrue(matches.empty)
    }

}
