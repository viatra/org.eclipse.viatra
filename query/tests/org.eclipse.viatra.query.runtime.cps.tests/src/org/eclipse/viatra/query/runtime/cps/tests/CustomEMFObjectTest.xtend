/*******************************************************************************
 * Copyright (c) 2019 Geza Kulcsar, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import java.io.IOException
import java.util.HashMap
import java.util.Map
import java.util.function.Function
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.ApplicationType
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.CyberPhysicalSystemPackage
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.Identifiable
import org.eclipse.viatra.query.patternlanguage.emf.tests.pltest.Child1
import org.eclipse.viatra.query.patternlanguage.emf.tests.pltest.PltestFactory
import org.eclipse.viatra.query.patternlanguage.emf.tests.pltest.PltestPackage
import org.eclipse.viatra.query.runtime.base.api.BaseIndexOptions
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.ApplicationInstancesOfApplicationTypeQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.ApplicationTypesQuerySpecification
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.testing.core.SnapshotHelper
import org.eclipse.viatra.query.testing.core.XmiModelUtil
import org.eclipse.viatra.query.testing.core.XmiModelUtil.XmiModelUtilRunningOptionEnum
import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest
import org.eclipse.viatra.query.testing.snapshot.CustomEMFSubstitution
import org.eclipse.viatra.query.testing.snapshot.QuerySnapshot
import org.junit.Assert
import org.junit.Test

class CustomEMFObjectTest {
	
	
	val Map<EClass, Function<EObject,String>> customMap
	
	val ResourceSet rs
	
	val BackendType type = BackendType.Rete
	
	def snapshot(String snp, ResourceSet rs) {
        val modelUri = XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, snp)
        val snr = rs.getResource(modelUri, true)
        return snr.allContents.filter(QuerySnapshot).head
    }
    
    def snapshotHelper(Map<EClass, Function<EObject,String>> customMap) {
        return new SnapshotHelper(new HashMap, customMap)        
    }
    
    def EMFScope makeScope(String snp, ResourceSet rSet) {
        val options = new BaseIndexOptions().withDanglingFreeAssumption(false) 
        val uri = XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, snp)
        rSet.getResource(uri , true)
        return new EMFScope(rSet, options)
    }
	
	
	def QuerySnapshot loadSnapshotFromUri(ResourceSet set, URI uri) throws IOException {
        val res = set.getResource(uri, true);
        if (!res.loaded) {
            res.load(newHashMap)
        }
        val snapshot = res.getContents()?.findFirst[it instanceof QuerySnapshot]  
        if (snapshot instanceof QuerySnapshot) {
            return snapshot
        } else {
            throw new IOException(String.format("Resource at uri %S does not contain a query snapshot.", uri.toString()));
        }
    }
    
	new() {
	    
        customMap = new HashMap
        rs = new ResourceSetImpl
	    
	} 
    	
    @Test
    def void basicCustomTest() {
        
        customMap.put(CyberPhysicalSystemPackage.Literals.APPLICATION_TYPE, [at | (at as ApplicationType).identifier])
        
        ViatraQueryTest.test(ApplicationTypesQuerySpecification.instance, snapshotHelper(customMap))
                        .on(makeScope("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/customemftest_apptypes.snapshot", rs))
                        .with(snapshot("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/customemftest_apptypes.snapshot", rs))
                        .with(type.hints)
                        .assertEquals
    }
    
    @Test
    def void hybridPatternTest() {
        
        customMap.put(CyberPhysicalSystemPackage.Literals.APPLICATION_TYPE, [at | (at as ApplicationType).identifier])
        
        ViatraQueryTest.test(ApplicationInstancesOfApplicationTypeQuerySpecification.instance, snapshotHelper(customMap))
                        .on(makeScope("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/customemftest_AIwithAT.snapshot", rs))
                        .with(snapshot("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/customemftest_AIwithAT.snapshot", rs))
                        .with(type.hints)
                        .assertEquals
    }
    
    
    @Test
    def void classOnlySupertypeHasCustomTest() {
        
        customMap.put(CyberPhysicalSystemPackage.Literals.IDENTIFIABLE, [id | "ID" + (id as Identifiable).identifier])
        
        ViatraQueryTest.test(ApplicationTypesQuerySpecification.instance, snapshotHelper(customMap))
                        .on(makeScope("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/customemftest_apptypes_id.snapshot", rs))
                        .with(snapshot("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/customemftest_apptypes_id.snapshot", rs))
                        .with(type.hints)
                        .assertEquals
    }
    
    @Test
    def void classAndSupertypeHasCustomTest() {
        
        customMap.put(CyberPhysicalSystemPackage.Literals.IDENTIFIABLE, [id | "ID" + (id as Identifiable).identifier])
        customMap.put(CyberPhysicalSystemPackage.Literals.APPLICATION_TYPE, [at | (at as ApplicationType).identifier])
        
        ViatraQueryTest.test(ApplicationTypesQuerySpecification.instance, snapshotHelper(customMap))
                        .on(makeScope("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/customemftest_apptypes.snapshot", rs))
                        .with(snapshot("org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/customemftest_apptypes.snapshot", rs))
                        .with(type.hints)
                        .assertEquals
    }
    
    @Test
    def void grandparentHasCustomTest() {
        
        val child = PltestFactory.eINSTANCE.createChild1()
        child.name = "Child1"
        
        customMap.put(PltestPackage.Literals.BASE, [ch | "BASE:" + (ch as Child1).name])
        
        Assert.assertEquals((snapshotHelper(customMap).createSubstitution("Child1", child) as CustomEMFSubstitution).value, "BASE:Child1")
        
    }
    
    @Test
    def void grandparentAndParentHasCustomTest() {
        
        val child = PltestFactory.eINSTANCE.createChild1()
        child.name = "Child1"
        
        customMap.put(PltestPackage.Literals.BASE, [ch | "BASE:" + (ch as Child1).name])
        customMap.put(PltestPackage.Literals.COMMON, [ch | "COMMON:" + (ch as Child1).name])
        
        Assert.assertEquals((snapshotHelper(customMap).createSubstitution("Child1", child) as CustomEMFSubstitution).value, "COMMON:Child1")
        
    }
    
    @Test
    def void grandparentAndOtherParentHasCustomTest() {
        
        val child = PltestFactory.eINSTANCE.createChild1()
        child.name = "Child1"
        
        customMap.put(PltestPackage.Literals.BASE, [ch | "BASE:" + (ch as Child1).name])
        customMap.put(PltestPackage.Literals.INTERFACE, [ch | "INTERFACE:" + (ch as Child1).name])
        
        Assert.assertEquals((snapshotHelper(customMap).createSubstitution("Child1", child) as CustomEMFSubstitution).value, "INTERFACE:Child1")
        
    }
    
}