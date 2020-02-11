/** 
 * Copyright (c) 2010-2019, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.query.runtime.cps.tests.api

import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParserBuilder
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery.PQueryStatus
import org.junit.Test

import static org.junit.Assert.assertTrue
import org.eclipse.viatra.query.patternlanguage.emf.tests.CustomizedEMFPatternLanguageInjectorProvider
import org.junit.Rule
import org.eclipse.viatra.query.patternlanguage.emf.tests.util.XtextInfrastructureRule

class GenericMatcherTest {
    
    @Rule
    public val rule = new XtextInfrastructureRule(this, CustomizedEMFPatternLanguageInjectorProvider)
    
    def getScope() {
        val uri = URI.createPlatformPluginURI("org.eclipse.viatra.query.runtime.cps.tests/models/instances/dangling.cyberphysicalsystem", false)
        val ResourceSet rSet = new ResourceSetImpl()
        rSet.getResource(uri , true)
        return new EMFScope(rSet)
    } 
    
    @Test
    def void correctParsedPatternExecutionTest() {
        val String pattern = '''
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern b(c : EObject) {
               EObject(c);
            }
        '''
        val results = PatternParserBuilder.instance.parse(pattern)
        val querySpecification = results.getQuerySpecification("b").get
        assertTrue(ViatraQueryEngine.on(getScope()).getMatcher(querySpecification).hasMatch)
    }
    
    /**
     * Test case for bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=546885:
     * an erroneous specification must not be tried to be initialized by the engine,
     * but an early exception should be thrown
     */
    @Test(expected = IllegalArgumentException)
    def void erroneousParsedPatternExecutionTest() {
        val String pattern = '''
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern b(c : EClass) {
               find a(_);
            }
        '''
        val results = PatternParserBuilder.instance.parse(pattern) 
        val querySpecification = results.getQuerySpecification("b").get
        assertTrue(querySpecification.internalQueryRepresentation.status == PQueryStatus.ERROR)
        ViatraQueryEngine.on(getScope()).getMatcher(querySpecification)
    }
}
