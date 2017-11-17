/*******************************************************************************
 * Copyright (c) 2016 IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Balazs Grill - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import com.google.common.collect.Collections2
import com.google.common.collect.Maps
import com.google.common.collect.Sets
import java.util.Collection
import java.util.List
import java.util.Set
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.ENamedElement
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.EcorePackage
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.CyberPhysicalSystemPackage
import org.eclipse.viatra.query.runtime.base.api.IndexingLevel
import org.eclipse.viatra.query.runtime.base.api.ViatraBaseFactory
import org.eclipse.viatra.query.testing.core.XmiModelUtil
import org.eclipse.viatra.query.testing.core.XmiModelUtil.XmiModelUtilRunningOptionEnum
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters
import org.eclipse.viatra.query.runtime.base.api.BaseIndexOptions

@RunWith(Parameterized)
class BaseIndexerIndexingLevelsTest {
    
    @Parameters(name = "Type: {0}, Model: {1}")
    def static Collection<Object[]> testData() {
        val elements = #{
            CyberPhysicalSystemPackage.eINSTANCE.applicationType,
            CyberPhysicalSystemPackage.eINSTANCE.applicationInstance,
            CyberPhysicalSystemPackage.eINSTANCE.hostType,
            CyberPhysicalSystemPackage.eINSTANCE.hostInstance,
            CyberPhysicalSystemPackage.eINSTANCE.cyberPhysicalSystem_AppTypes,
            CyberPhysicalSystemPackage.eINSTANCE.applicationInstance_AllocatedTo,
            CyberPhysicalSystemPackage.eINSTANCE.hostInstance_Applications,
            CyberPhysicalSystemPackage.eINSTANCE.hostType_Instances,
            CyberPhysicalSystemPackage.eINSTANCE.applicationType_Behavior,
            CyberPhysicalSystemPackage.eINSTANCE.hostInstance_CommunicateWith,
            EcorePackage.eINSTANCE.EString,
            EcorePackage.eINSTANCE.EInt
            
        }
        val models = #{"org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem"}
        val datas = Sets.cartesianProduct(elements, models);
        return Collections2.transform(datas, [it.toArray]);
    }
    
    @Parameter(0)
    public ENamedElement element
    @Parameter(1)
    public String modelPath
    ResourceSet rs
    
    
    @Before
    def void prepareTest() {
        val modelUri = XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, modelPath)
        rs = new ResourceSetImpl
        rs.getResource(modelUri, true)
    }
    
    def collectSettings(List<Object> list, EObject eobject, EStructuralFeature feature) {
        val o = eobject.eGet(feature)
        if (o instanceof List<?>) {
            for (u : o) {
                list.add(#[eobject, feature, u])
            }
        } else {
            if (o !== null){
                list.add(#[eobject, feature, o])
            }
        }
    }
    
    def enumerateInstances(ENamedElement element){
        val result = newArrayList()
        val iterator = rs.allContents
        while(iterator.hasNext){
            val next = iterator.next
            if (next instanceof EObject){
                if (element instanceof EClass){
                    if (element.isInstance(next)){
                        result.add(next)
                    }
                }
                if (element instanceof EStructuralFeature){
                    if (next.eClass.EAllStructuralFeatures.contains(element)){
                        result.collectSettings(next, element)
                    }
                }
                if (element instanceof EDataType){
                    for(a : next.eClass.EAllAttributes){
                        if (a.EType.equals(element)){
                            result.collectSettings(next, a)               
                        }
                    }                
                }
            }
        }
        result
    }
    
    @Test
    def void testWildCardStatistics(){
        val baseIndex = ViatraBaseFactory::instance.createNavigationHelper(rs, false, null)
        baseIndex.wildcardLevel = IndexingLevel.STATISTICS
        val expected = enumerateInstances(element).size
        var counted = -1;
        if (element instanceof EClass){
            counted = baseIndex.countAllInstances(element)
        }
        if (element instanceof EStructuralFeature){
            counted = baseIndex.countFeatures(element)
        }
        if (element instanceof EDataType){
            counted = baseIndex.countDataTypeInstances(element)   
        }
        
        Assert.assertEquals(expected, counted)
    }
    
    @Test
    def void testStatistics(){
        val baseIndex = ViatraBaseFactory::instance.createNavigationHelper(rs, false, null)
        val expected = enumerateInstances(element).size
        var counted = -1;
        if (element instanceof EClass){
            baseIndex.registerEClasses(#{element}, IndexingLevel.STATISTICS)
            counted = baseIndex.countAllInstances(element)
        }
        if (element instanceof EStructuralFeature){
            baseIndex.registerEStructuralFeatures(#{element}, IndexingLevel.STATISTICS)
            counted = baseIndex.countFeatures(element)
        }
        if (element instanceof EDataType){
            baseIndex.registerEDataTypes(#{element}, IndexingLevel.STATISTICS)
            counted = baseIndex.countDataTypeInstances(element)   
        }
        
        Assert.assertEquals(expected, counted)
    }
    
    @Test
    def void testStatisticsInFull(){
        val baseIndex = ViatraBaseFactory::instance.createNavigationHelper(rs, false, null)
        val expected = enumerateInstances(element).size
        var counted = -1;
        if (element instanceof EClass){
            baseIndex.registerEClasses(#{element}, IndexingLevel.FULL)
            counted = baseIndex.getAllInstances(element).size
        }
        if (element instanceof EStructuralFeature){
            baseIndex.registerEStructuralFeatures(#{element}, IndexingLevel.FULL)
            counted = baseIndex.getFeatureInstances(element).mapValues[it.size].values.fold(0, [a, b | a+b])
        }
        if (element instanceof EDataType){
            baseIndex.registerEDataTypes(#{element}, IndexingLevel.FULL)
            counted = baseIndex.countDataTypeInstances(element) 
        }
        
        Assert.assertEquals(expected, counted)
    }
    
    @Test
    def void combineStatisticsWithFull(){
        val options = new BaseIndexOptions().withWildcardLevel(IndexingLevel.STATISTICS)
        val baseIndex = ViatraBaseFactory::instance.createNavigationHelper(rs, options, null)
        val expected = enumerateInstances(element).size
        var counted = -1;
        if (element instanceof EClass){
            baseIndex.registerEClasses(#{element}, IndexingLevel.FULL)
            counted = baseIndex.getAllInstances(element).size
        }
        if (element instanceof EStructuralFeature){
            baseIndex.registerEStructuralFeatures(#{element}, IndexingLevel.FULL)
            counted = baseIndex.getFeatureInstances(element).mapValues[it.size].values.fold(0, [a, b | a+b])
        }
        if (element instanceof EDataType){
            baseIndex.registerEDataTypes(#{element}, IndexingLevel.FULL)
            counted = baseIndex.countDataTypeInstances(element) 
        }
        
        Assert.assertEquals(expected, counted)
    }
    
    @Test
    def void followStatisticsByFull(){
        val options = new BaseIndexOptions().withWildcardLevel(IndexingLevel.STATISTICS)
        val baseIndex = ViatraBaseFactory::instance.createNavigationHelper(rs, options, null)
        var counted = -1;
        var expected = -1 
        if (element instanceof EClass){
            baseIndex.registerEClasses(#{element}, IndexingLevel.STATISTICS)
            expected = enumerateInstances(element).size
            baseIndex.registerEClasses(#{element}, IndexingLevel.FULL)
            counted = baseIndex.getAllInstances(element).size
        }
        if (element instanceof EStructuralFeature){
            baseIndex.registerEStructuralFeatures(#{element}, IndexingLevel.STATISTICS)
            expected = enumerateInstances(element).size
            baseIndex.registerEStructuralFeatures(#{element}, IndexingLevel.FULL)
            counted = baseIndex.getFeatureInstances(element).mapValues[it.size].values.fold(0, [a, b | a+b])
        }
        if (element instanceof EDataType){
            baseIndex.registerEDataTypes(#{element}, IndexingLevel.STATISTICS)
            expected = enumerateInstances(element).size
            baseIndex.registerEDataTypes(#{element}, IndexingLevel.FULL)
            counted = baseIndex.countDataTypeInstances(element) 
        }
        
        Assert.assertEquals(expected, counted)
    }
    
    @Test
    def void combineBothWithFull(){
        val options = new BaseIndexOptions().withWildcardLevel(IndexingLevel.BOTH)
        val baseIndex = ViatraBaseFactory::instance.createNavigationHelper(rs, options, null)
        val expected = enumerateInstances(element).size
        var counted = -1;
        if (element instanceof EClass){
            baseIndex.registerEClasses(#{element}, IndexingLevel.FULL)
            counted = baseIndex.getAllInstances(element).size
        }
        if (element instanceof EStructuralFeature){
            baseIndex.registerEStructuralFeatures(#{element}, IndexingLevel.FULL)
            counted = baseIndex.getFeatureInstances(element).mapValues[it.size].values.fold(0, [a, b | a+b])
        }
        if (element instanceof EDataType){
            baseIndex.registerEDataTypes(#{element}, IndexingLevel.FULL)
            counted = baseIndex.countDataTypeInstances(element) 
        }
        
        Assert.assertEquals(expected, counted)
    }
    
    @Test
    def void testWildcardFullWithOptions(){
        val baseIndex = ViatraBaseFactory::instance.createNavigationHelper(rs, true, null)
        val expected = enumerateInstances(element)
        if (element instanceof EClass){
            val instances = baseIndex.getAllInstances(element)
            val symmetricDifference = Sets.symmetricDifference(Sets.newHashSet(expected), instances)
            Assert.assertTrue(symmetricDifference.empty)
        }
        if (element instanceof EStructuralFeature){
            val featureMap = baseIndex.getFeatureInstances(element)
            val expectedFeatureMap = <EObject, Set<Object>>newHashMap()    
            for(o : expected){
                val entry = o as List<?>
                var values = expectedFeatureMap.get(entry.get(0))
                if (values === null){
                    values = newHashSet()
                    expectedFeatureMap.put(entry.get(0) as EObject, values)
                }
                values.add(entry.get(2))
            }       
            val diff = Maps.difference(featureMap, expectedFeatureMap)
            Assert.assertTrue(diff.areEqual)
        }
        if (element instanceof EDataType){
            val values = baseIndex.getDataTypeInstances(element)
            val expectedValues = newHashSet()
            for(o : expected){
                val entry = o as List<?>
                expectedValues.add(entry.get(2))
            }
            val symmetricDifference = Sets.symmetricDifference(values, expectedValues)
            Assert.assertTrue(symmetricDifference.empty)
        }
        
    }
    
    @Test
    def void testWildcardFull(){
        val baseIndex = ViatraBaseFactory::instance.createNavigationHelper(rs, false, null)
        baseIndex.wildcardLevel = IndexingLevel.FULL
        val expected = enumerateInstances(element)
        if (element instanceof EClass){
            val instances = baseIndex.getAllInstances(element)
            val symmetricDifference = Sets.symmetricDifference(Sets.newHashSet(expected), instances)
            Assert.assertTrue(symmetricDifference.empty)
        }
        if (element instanceof EStructuralFeature){
            val featureMap = baseIndex.getFeatureInstances(element)
            val expectedFeatureMap = <EObject, Set<Object>>newHashMap()    
            for(o : expected){
                val entry = o as List<?>
                var values = expectedFeatureMap.get(entry.get(0))
                if (values === null){
                    values = newHashSet()
                    expectedFeatureMap.put(entry.get(0) as EObject, values)
                }
                values.add(entry.get(2))
            }       
            val diff = Maps.difference(featureMap, expectedFeatureMap)
            Assert.assertTrue(diff.areEqual)
        }
        if (element instanceof EDataType){
            val values = baseIndex.getDataTypeInstances(element)
            val expectedValues = newHashSet()
            for(o : expected){
                val entry = o as List<?>
                expectedValues.add(entry.get(2))
            }
            val symmetricDifference = Sets.symmetricDifference(values, expectedValues)
            Assert.assertTrue(symmetricDifference.empty)
        }
        
    }
    
    @Test
    def void testFull(){
        val baseIndex = ViatraBaseFactory::instance.createNavigationHelper(rs, false, null)
        val expected = enumerateInstances(element)
        if (element instanceof EClass){
            baseIndex.registerEClasses(#{element}, IndexingLevel.FULL)
            val instances = baseIndex.getAllInstances(element)
            val symmetricDifference = Sets.symmetricDifference(Sets.newHashSet(expected), instances)
            Assert.assertTrue(symmetricDifference.empty)
        }
        if (element instanceof EStructuralFeature){
            baseIndex.registerEStructuralFeatures(#{element}, IndexingLevel.FULL)
            val featureMap = baseIndex.getFeatureInstances(element)
            val expectedFeatureMap = <EObject, Set<Object>>newHashMap()    
            for(o : expected){
                val entry = o as List<?>
                var values = expectedFeatureMap.get(entry.get(0))
                if (values === null){
                    values = newHashSet()
                    expectedFeatureMap.put(entry.get(0) as EObject, values)
                }
                values.add(entry.get(2))
            }       
            val diff = Maps.difference(featureMap, expectedFeatureMap)
            Assert.assertTrue(diff.areEqual)
        }
        if (element instanceof EDataType){
            baseIndex.registerEDataTypes(#{element}, IndexingLevel.FULL)
            val values = baseIndex.getDataTypeInstances(element)
            val expectedValues = newHashSet()
            for(o : expected){
                val entry = o as List<?>
                expectedValues.add(entry.get(2))
            }
            val symmetricDifference = Sets.symmetricDifference(values, expectedValues)
            Assert.assertTrue(symmetricDifference.empty)
        }
        
    }
    
    @Test
    def void testStatisticsInCallback(){
        val baseIndex = ViatraBaseFactory::instance.createNavigationHelper(rs, false, null)
        val int[] counted = #[-1];
        
        baseIndex.coalesceTraversals([
            baseIndex.executeAfterTraversal([
                if (element instanceof EClass){
                    counted.set(0,baseIndex.countAllInstances(element))
                }
                if (element instanceof EStructuralFeature){
                    counted.set(0,baseIndex.countFeatures(element))
                }
                if (element instanceof EDataType){
                    counted.set(0,baseIndex.countDataTypeInstances(element))   
                }  
            ])
            if (element instanceof EClass){
                baseIndex.registerEClasses(#{element}, IndexingLevel.STATISTICS)
            }
            if (element instanceof EStructuralFeature){
                baseIndex.registerEStructuralFeatures(#{element}, IndexingLevel.STATISTICS)
            }
            if (element instanceof EDataType){
                baseIndex.registerEDataTypes(#{element}, IndexingLevel.STATISTICS)
            }  
            return null;
        ]);
        val expected = enumerateInstances(element).size

        Assert.assertEquals(expected, counted.get(0))
    }
    
}
