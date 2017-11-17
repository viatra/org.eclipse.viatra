/*******************************************************************************
 * Copyright (c) 2014-2016 IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest
import org.junit.Test
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.EvalDivisionByZeroQuerySpecification
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.CheckDivisionByZeroQuerySpecification
import org.eclipse.emf.common.util.URI
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.apache.log4j.Level

class ErrorHandlingTest {
    
    def getScope() {
        val uri = URI.createPlatformPluginURI("org.eclipse.viatra.query.runtime.cps.tests/models/instances/dangling.cyberphysicalsystem", false)
        val ResourceSet rSet = new ResourceSetImpl()
        rSet.getResource(uri , true)
        return new EMFScope(rSet)
    } 
    
    @Test
    def void testDivisionByZeroInEval() {
        ViatraQueryTest.test(EvalDivisionByZeroQuerySpecification.instance)
                        .on(getScope)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(BackendType.LocalSearch.newBackendInstance)
                        .assertEquals(Level::WARN)
    }
    
    @Test
    def void testDivisionByZeroInCheck() {
        
        ViatraQueryTest.test(CheckDivisionByZeroQuerySpecification.instance)
                        .on(getScope)
                        .with(BackendType.Rete.newBackendInstance)
                        .with(BackendType.LocalSearch.newBackendInstance)
                        .assertEquals(Level::WARN) 
    }

}
