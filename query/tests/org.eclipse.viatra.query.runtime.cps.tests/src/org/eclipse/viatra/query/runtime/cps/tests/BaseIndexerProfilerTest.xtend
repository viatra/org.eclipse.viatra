/*******************************************************************************
 * Copyright (c) 2016 Balazs Grill, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.CyberPhysicalSystemFactory
import org.eclipse.viatra.query.runtime.base.api.BaseIndexOptions
import org.eclipse.viatra.query.runtime.base.api.EMFBaseIndexChangeListener
import org.eclipse.viatra.query.runtime.base.api.IndexingLevel
import org.eclipse.viatra.query.runtime.base.api.NavigationHelper
import org.eclipse.viatra.query.runtime.base.api.ViatraBaseFactory
import org.eclipse.viatra.query.runtime.base.api.profiler.BaseIndexProfiler
import org.eclipse.viatra.query.runtime.base.api.profiler.ProfilerMode
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class BaseIndexerProfilerTest {

    ResourceSet rs
    Resource resource

    static class WaitOnChangeListener implements EMFBaseIndexChangeListener {

        override notifyChanged(boolean indexChanged) {
            Thread.sleep(10)
        }

        override onlyOnIndexChange() {
            true
        }

    }

    @Before
    def void prepareTest() {
        rs = new ResourceSetImpl
        resource = rs.createResource(URI.createURI("_synthetic_model"))
        resource.contents += CyberPhysicalSystemFactory.eINSTANCE.createApplicationInstance
    }

    @Test(expected=IllegalArgumentException)
    def void testWithProfilerUnmentioned() {
        val options = new BaseIndexOptions().withWildcardLevel(IndexingLevel.FULL)
        val baseIndex = ViatraBaseFactory::instance.createNavigationHelper(rs, options, null)
        doExecuteTests(baseIndex)

        new BaseIndexProfiler(baseIndex)
    }

    @Test(expected=IllegalArgumentException)
    def void testWithProfilerOff() {
        val options = new BaseIndexOptions().withIndexProfilerMode(ProfilerMode.OFF).withWildcardLevel(
            IndexingLevel.FULL)
        val baseIndex = ViatraBaseFactory::instance.createNavigationHelper(rs, options, null)
        doExecuteTests(baseIndex)

        new BaseIndexProfiler(baseIndex)
    }

    @Test
    def void testWithProfilerDisabled() {
        val options = new BaseIndexOptions().withIndexProfilerMode(ProfilerMode.START_DISABLED).withWildcardLevel(
            IndexingLevel.FULL)
        val baseIndex = ViatraBaseFactory::instance.createNavigationHelper(rs, options, null)
        doExecuteTests(baseIndex)

        val profiler = new BaseIndexProfiler(baseIndex)
        Assert.assertFalse(profiler.isEnabled)
        Assert.assertEquals(0l, profiler.notificationCount)
        Assert.assertEquals(0l, profiler.totalMeasuredTimeInMS)
    }

    @Test
    def void testWithProfilerEnabled() {
        val options = new BaseIndexOptions().withIndexProfilerMode(ProfilerMode.START_ENABLED).withWildcardLevel(
            IndexingLevel.FULL)
        val baseIndex = ViatraBaseFactory::instance.createNavigationHelper(rs, options, null)
        baseIndex.addBaseIndexChangeListener(new WaitOnChangeListener)
        
        doExecuteTests(baseIndex)

        val profiler = new BaseIndexProfiler(baseIndex)
        Assert.assertTrue(profiler.isEnabled)
        Assert.assertTrue(profiler.notificationCount > 0l)
        Assert.assertTrue(profiler.totalMeasuredTimeInMS > 0l)
        Assert.assertTrue(profiler.totalMeasuredTimeInMS < 100l)
    }

    @Test
    def void testWithProfilerStateChanges(){
        val options = new BaseIndexOptions()
            .withIndexProfilerMode(ProfilerMode.START_DISABLED)
            .withWildcardLevel(IndexingLevel.FULL)
        val baseIndex = ViatraBaseFactory::instance.createNavigationHelper(rs, options, null)
        baseIndex.addBaseIndexChangeListener(new WaitOnChangeListener)
    
        val profiler = new BaseIndexProfiler(baseIndex)
    
        profiler.resetMeasurement
        doExecuteTests(baseIndex)
    
        Assert.assertFalse(profiler.isEnabled)
        Assert.assertEquals(0l, profiler.notificationCount) 
        Assert.assertEquals(0l, profiler.totalMeasuredTimeInMS) 
    
        profiler.enabled = true 
        profiler.resetMeasurement
        doExecuteTests(baseIndex)
    
        Assert.assertTrue(profiler.isEnabled)
        Assert.assertTrue(profiler.notificationCount > 0l) 
        Assert.assertTrue(profiler.totalMeasuredTimeInMS > 0l)
        Assert.assertTrue(profiler.totalMeasuredTimeInMS < 100l)
    
        profiler.enabled = false
        profiler.resetMeasurement
    
        doExecuteTests(baseIndex)
        Assert.assertFalse(profiler.isEnabled)
        Assert.assertEquals(0l, profiler.notificationCount) 
        Assert.assertEquals(0l, profiler.totalMeasuredTimeInMS) 
   }

    private def void doExecuteTests(NavigationHelper baseIndex) {
        resource.contents += CyberPhysicalSystemFactory.eINSTANCE.createApplicationInstance
    }

}
