/*******************************************************************************
 * Copyright (c) 2017-2014 Balazs Grill, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.impl.ResourceImpl
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.CyberPhysicalSystemFactory
import org.eclipse.viatra.query.runtime.base.api.IndexingLevel
import org.eclipse.viatra.query.runtime.base.api.ViatraBaseFactory
import org.junit.Assert
import org.junit.Test
import static org.eclipse.viatra.examples.cps.cyberPhysicalSystem.CyberPhysicalSystemPackage.Literals.*
import org.junit.Ignore

class BaseIndexWildcardModePropagationTest {
    
    val extension CyberPhysicalSystemFactory factory = CyberPhysicalSystemFactory.eINSTANCE 
    
    @Test
    @Ignore("Known to fail until https://bugs.eclipse.org/bugs/show_bug.cgi?id=517629 is not fixed")
    def testProxyResolution(){
        val rs = new ResourceSetImpl();
        val r1 = new ResourceImpl(URI.createURI("nothing://host"))
        
        val ht = createHostType => [
            identifier = "ht"
            instances += createHostInstance => [
                identifier = "hi1"
            ]
        ]
        r1.contents += ht
        rs.resources += r1
        
        val baseIndex = ViatraBaseFactory::instance.createNavigationHelper(rs, false, null)
        baseIndex.wildcardLevel = IndexingLevel.FULL
        
        baseIndex.getHoldersOfFeature(HOST_TYPE__INSTANCES).forEach[holder|
            val targets = baseIndex.getFeatureTargets(holder, HOST_TYPE__INSTANCES)
            Assert.assertEquals(1, targets.size)
        ]
        
        // Add another element
        ht.instances += createHostInstance => [
            identifier = "hi2"
        ]
        
        baseIndex.getHoldersOfFeature(HOST_TYPE__INSTANCES).forEach[holder|
            val targets = baseIndex.getFeatureTargets(holder, HOST_TYPE__INSTANCES)
            Assert.assertEquals(2, targets.size)
        ]
    }
    
}