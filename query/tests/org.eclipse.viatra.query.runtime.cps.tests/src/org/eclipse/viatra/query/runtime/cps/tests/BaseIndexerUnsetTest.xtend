/*******************************************************************************
 * Copyright (c) 2016 Abel Hegedus, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.CyberPhysicalSystemFactory
import org.eclipse.viatra.query.runtime.base.api.ViatraBaseFactory
import org.eclipse.viatra.query.testing.core.TestingSeverityAggregatorLogAppender
import org.junit.Assert
import org.junit.Test
import org.junit.Before
import org.junit.After
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.ApplicationType
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.Request
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.Requirement
import org.eclipse.viatra.query.runtime.base.api.NavigationHelper

class BaseIndexerUnsetTest {
    
    ApplicationType appType
    Request request
    Requirement requirement
    NavigationHelper baseIndex
    TestingSeverityAggregatorLogAppender appender
    Logger logger
    
    @Before
    def void setup() {
        // prepare model
        appType = CyberPhysicalSystemFactory.eINSTANCE.createApplicationType
        request = CyberPhysicalSystemFactory.eINSTANCE.createRequest
        requirement = CyberPhysicalSystemFactory.eINSTANCE.createRequirement
        request.requirements += requirement
        requirement.type = appType
        
        // prepare resource set and base index
        val rs = new ResourceSetImpl
        val resource = rs.createResource(URI.createURI("dummyModel"))
        resource.contents.add(appType)
        resource.contents.add(request)
        logger = Logger.getLogger(this.class)
        appender = new TestingSeverityAggregatorLogAppender
        logger.addAppender(appender)
        baseIndex = ViatraBaseFactory::instance.createNavigationHelper(rs, true, logger)
    }
    
    @After
    def void cleanup() {
        logger.removeAppender(appender)        
        baseIndex.dispose
        baseIndex = null
        appType = null
        request = null
        requirement = null
        appender = null
    }
    
    @Test
    def void test_unsetManyValueFeature() {
        // unset many-value feature
        request.unsetRequirements
        Assert.assertTrue("Many-value unset caused exception", appender.severity == Level.INFO)
    }
    
    @Test
    def void test_setUnsetManyValueFeature() {
        request.unsetRequirements
        // set many-value feature
        request.requirements += requirement
        Assert.assertTrue("Many-value add on unset feature caused exception", appender.severity == Level.INFO)
    }
    
    @Test
    def void test_unsetSingleFeature() {
        // unset single feature
        requirement.unsetType
        Assert.assertTrue("Single value unset caused exception", appender.severity == Level.INFO)
    }
    
    @Test
    def void test_setUnsetSingleFeature() {
        requirement.unsetType
        // set single value feature
        requirement.type = appType
        Assert.assertTrue("Single value set on unset feature caused exception", appender.severity == Level.INFO)
    }
}
