/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import static org.junit.Assert.assertEquals
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.ApplicationInstance
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.CyberPhysicalSystemFactory
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.CyberPhysicalSystemPackage
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.Requirement
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.query.runtime.base.api.IndexingLevel
import org.eclipse.viatra.query.runtime.base.api.NavigationHelper
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.junit.Before
import org.junit.Test

class NavigationHelperTest {
    ViatraQueryEngine queryEngine
    Requirement requirement
    ApplicationInstance applicationInstance
    NavigationHelper navigationHelper

    @Before
    def void setup() {
        val ResourceSetImpl resourceSet = new ResourceSetImpl()
        val CyberPhysicalSystemFactory factory = CyberPhysicalSystemFactory.eINSTANCE
        
        applicationInstance = factory.createApplicationInstance()
        requirement = factory.createRequirement() => [
            applications += applicationInstance
        ]
        resourceSet.createResource(URI.createURI("resource")) => [
            contents+= applicationInstance
            contents += requirement
        ]
        
        queryEngine = ViatraQueryEngine.on(new EMFScope(resourceSet))
        navigationHelper = EMFScope.extractUnderlyingEMFIndex(queryEngine)
        navigationHelper.registerEStructuralFeatures(
            #{CyberPhysicalSystemPackage.Literals.REQUIREMENT__APPLICATIONS}, IndexingLevel.FULL)
    }

    @Test
    def void inverseReferencesFound() {
        val inverseReferences = navigationHelper.getInverseReferences(applicationInstance)
        assertEquals(1, inverseReferences.size())
        val setting = inverseReferences.findFirst[true]
        assertEquals(CyberPhysicalSystemPackage.Literals.REQUIREMENT__APPLICATIONS, setting.getEStructuralFeature())
        assertEquals(requirement, setting.getEObject())
    }
    
    @Test
    def void inverseReferencesMissing() {
        val inverseReferences = navigationHelper.getInverseReferences(requirement)
        assertEquals(0, inverseReferences.size())
    }
}
