/*******************************************************************************
 * Copyright (c) 2014-2016 IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Akos Horvath, Abel Hegedus, Akos Menyhert, Tamas Borbas, Marton Bur, Zoltan Ujhelyi, Daniel Segesdi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import java.util.Collection
import java.util.Random
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.SameVariablesQuerySpecification
import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters

import static org.junit.Assert.*
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.viatra.query.testing.core.ModelLoadHelper
import org.eclipse.viatra.query.runtime.emf.EMFScope

@RunWith(Parameterized)
class TestingFrameworkTest {
    extension ModelLoadHelper = new ModelLoadHelper
    
    @Parameters(name = "{0}")
    def static Collection<Object[]> testData() {
        newArrayList(
            #[ "org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem" ]
        )
    }
    
    @Parameter(0)
    public String modelPath
    
    @Test
    def unresolvableFileTest() {
        val ResourceSet set = new ResourceSetImpl
        
        
        var String exMessage = null
        val randomPath = modelPath.random
        try {
            set.loadExpectedResultsFromUri(randomPath)
        
            val scope = new EMFScope(set)
            ViatraQueryTest.test(SameVariablesQuerySpecification.instance)
                            .on(scope)
                            .with(BackendType.Rete.newBackendInstance)
                            .with(BackendType.LocalSearch.newBackendInstance)
                            .assertEquals
        } catch(NullPointerException ex) {
            throw ex
        } catch(Exception ex) {
            exMessage = ex.message
        }
        assertNotNull("No exception has been thrown or message is null", exMessage)
        assertTrue('''
		The message of the caught exception does not contains the path. The message:
		«exMessage»''',
                    exMessage.contains(randomPath))
    }
    
    def String getRandom(String path) {
        return '''«path»«(new Random).nextLong»'''
    }
    
}
