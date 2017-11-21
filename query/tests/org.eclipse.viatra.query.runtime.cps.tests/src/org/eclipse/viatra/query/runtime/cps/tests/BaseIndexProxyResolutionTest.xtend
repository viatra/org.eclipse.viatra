/*******************************************************************************
 * Copyright (c) 2016 Denes Harmath, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Denes Harmath - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import org.eclipse.emf.ecore.resource.impl.ResourceImpl
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.query.runtime.base.api.IndexingLevel
import org.eclipse.viatra.query.runtime.base.api.ViatraBaseFactory
import org.junit.Test
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.CyberPhysicalSystemFactory
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.InternalEObject
import static extension org.eclipse.viatra.examples.cps.cyberPhysicalSystem.CyberPhysicalSystemPackage.Literals.*
import org.junit.Assert
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.emf.ecore.EObject

class BaseIndexProxyResolutionTest {
    
    val extension CyberPhysicalSystemFactory factory = CyberPhysicalSystemFactory.eINSTANCE 
    
    /**
     * https://bugs.eclipse.org/bugs/show_bug.cgi?id=517607
     */
    @Test
    def testProxyResolution(){
        val rs = new ResourceSetImpl();
        val r1 = new ResourceImpl(URI.createURI("nothing://host"))
        val r2 = new ResourceImpl(URI.createURI("nothing://app"))
        
        r1.contents += createHostType => [
            identifier = "ht"
            instances += createHostInstance => [
                identifier = "hi"
                applications += createApplicationInstance => [
                    (it as InternalEObject).eSetProxyURI(URI.createURI("nothing://app#ai"))
                ]
            ]
        ]
        r2.contents += createApplicationType => [
            identifier = "at"
            instances += createApplicationInstance => [
                identifier = "ai"
            ]
        ]
        
        // Add only the first model with the proxy
        rs.resources += r1
        
        val baseIndex = ViatraBaseFactory::instance.createNavigationHelper(rs, false, null)
        baseIndex.registerEStructuralFeatures(#{HOST_INSTANCE__APPLICATIONS}, IndexingLevel.FULL)
        
        // Base index should contain proxy at this point
        baseIndex.processAllFeatureInstances(HOST_INSTANCE__APPLICATIONS)[k,v|
            Assert.assertEquals(1, baseIndex.countFeatureTargets(k, HOST_INSTANCE__APPLICATIONS))
            Assert.assertTrue((v as EObject).eIsProxy)
        ]
        
        // Remove failed loaded resource
        rs.resources -= rs.resources.filter[URI == r2.URI].toList
        // Add second resource
        rs.resources += r2
        // Resolve all proxies
        EcoreUtil.resolveAll(rs)
        
        // Base index should NOT contain proxy at this point
        baseIndex.processAllFeatureInstances(HOST_INSTANCE__APPLICATIONS)[k,v|
            Assert.assertEquals(1, baseIndex.countFeatureTargets(k, HOST_INSTANCE__APPLICATIONS))
            Assert.assertFalse((v as EObject).eIsProxy)
        ]
    }
    
}
