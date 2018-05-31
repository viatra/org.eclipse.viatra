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
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.CyberPhysicalSystemFactory
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.HostInstance
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.HostType
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.HostInstancesPerHostTypesQuerySpecification
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.runtime.base.api.BaseIndexOptions
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized)
class CrossResourceContainmentTest {

    static val extension CyberPhysicalSystemFactory factory = CyberPhysicalSystemFactory.eINSTANCE
    ResourceSet resourceSet
    Resource resourceHt
    Resource resourceHi
    HostType myHostType
    HostInstance myHostInstance

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

    @Before
    def void setUp() {
        resourceSet = new ResourceSetImpl()
        resourceHt = resourceSet.createResource(URI.createURI("protocol://ht"))
        resourceHi = resourceSet.createResource(URI.createURI("protocol://hi"))
        myHostType = createHostType => [identifier = "MyHostType"]
        myHostInstance = createHostInstance => [identifier = "MyHostInstance"]

        myHostType.instances.add(myHostInstance)
        resourceHt.contents += myHostType
        resourceHi.contents += myHostInstance
    }

    // Target is in scope, Source is not
    @Test
    def void crossContTest_1() {        
        val AdvancedViatraQueryEngine engine = AdvancedViatraQueryEngine.createUnmanagedEngine(
            new EMFScope(resourceHi, option));
        val ViatraQueryMatcher<? extends IPatternMatch> matcher = engine.getMatcher(
            HostInstancesPerHostTypesQuerySpecification.instance);
        val Collection<? extends IPatternMatch> matches = matcher.getAllMatches();

        Assert.assertTrue(matches.empty)
    }

    // Source is in scope, Target is not (Resource scope)
    @Test
    def void crossContTest_2() {        
        val AdvancedViatraQueryEngine engine = AdvancedViatraQueryEngine.createUnmanagedEngine(
            new EMFScope(resourceHt, option));
        val ViatraQueryMatcher<? extends IPatternMatch> matcher = engine.getMatcher(
            HostInstancesPerHostTypesQuerySpecification.instance);
        val Collection<? extends IPatternMatch> matches = matcher.getAllMatches();

        // if danglingFreeAssum is true, the match set is undefined
        Assert.assertTrue(danglingFreeAssum || matches.empty)
    }

    // Target and Source are in the Scope (ResourceSet scope)
    @Test
    def void crossContTest_3() {                
        val AdvancedViatraQueryEngine engine = AdvancedViatraQueryEngine.createUnmanagedEngine(
            new EMFScope(resourceSet, option));
        val ViatraQueryMatcher<? extends IPatternMatch> matcher = engine.getMatcher(
            HostInstancesPerHostTypesQuerySpecification.instance);
        val Collection<? extends IPatternMatch> matches = matcher.getAllMatches();

        Assert.assertFalse(matches.empty)
        Assert.assertEquals(matches.get(0).get(0), myHostType)
        Assert.assertEquals(matches.get(0).get(1), myHostInstance)
    }
    
    // Target and Source are in the Scope (Resource scope)
    @Test
    def void crossContTest_4() {        
        val AdvancedViatraQueryEngine engine = AdvancedViatraQueryEngine.createUnmanagedEngine(
            new EMFScope(myHostType, option));
        val ViatraQueryMatcher<? extends IPatternMatch> matcher = engine.getMatcher(
            HostInstancesPerHostTypesQuerySpecification.instance);
        val Collection<? extends IPatternMatch> matches = matcher.getAllMatches();

        Assert.assertFalse(matches.empty)
        Assert.assertEquals(matches.get(0).get(0), myHostType)
        Assert.assertEquals(matches.get(0).get(1), myHostInstance)
    }
}
