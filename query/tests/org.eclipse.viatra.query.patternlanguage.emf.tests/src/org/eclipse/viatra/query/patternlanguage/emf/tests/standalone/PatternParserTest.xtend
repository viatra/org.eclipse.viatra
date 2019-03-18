/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.tests.standalone

import org.eclipse.emf.common.util.URI
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParser
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery.PQueryStatus
import org.junit.BeforeClass
import org.junit.Test

import static org.junit.Assert.*
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParserBuilder

class PatternParserTest {
    
    @BeforeClass
    static def void initializeInjector() {
        EMFPatternLanguageStandaloneSetup.doSetup
    }
    
    @Test
    def void correctPatternTest() {
        val String pattern = '''
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern b(c : EClass) {
             EClass.name(c, _);
            }
        '''
        val results = PatternParser.parser.parse(pattern) 
        assertTrue(results.querySpecifications.filter[it.internalQueryRepresentation.status === PQueryStatus.OK].size === 1)
        assertFalse(results.hasError)
    }
    
    @Test
    def void mistypedPatternTest() {
        val String pattern = '''
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern b(c : EClass) {
             EClass.name(_, c);
            }
        '''
        val results = PatternParser.parser.parse(pattern)
        assertTrue(results.querySpecifications.filter[it.internalQueryRepresentation.status === PQueryStatus.OK].isEmpty)
        assertTrue(results.hasError)
    }
    
    @Test
    def void missingComposedPatternTest() {
        val String pattern = '''
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern b(c : EClass) {
             EClass.name(c, "someName");
             find a(c);
            }
        '''
        val results = PatternParser.parser.parse(pattern)
        assertTrue(results.querySpecifications.forall[it.internalQueryRepresentation.status === PQueryStatus.ERROR])
        results.querySpecifications.map[it.internalQueryRepresentation.PProblems].flatten.exists[location.contains("Line 5")]
        assertTrue(results.hasError)
    }
    
    
    @Test
    def void duplicatePatternsTest() {
        val String pattern = '''
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern b(c : EClass) {
             EClass.name(c, "someName");
            }
            
            pattern b(c : EClass) {
             EClass.name(c, "someName2");
            }
        '''
        val results = PatternParser.parser.parse(pattern)
        assertFalse(results.querySpecifications.forall[it.internalQueryRepresentation.status === PQueryStatus.OK])
        assertTrue(results.querySpecifications.forall[it.internalQueryRepresentation.status === PQueryStatus.ERROR])
        assertTrue(results.hasError)
    }
    
    @Test
    def void crossFileDuplicatePatternsTest(){
        val String pattern = '''
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern b(c : EClass) {
             EClass.name(c, "someName2");
            }
        '''
        val parser = PatternParser.parser.build
        parser.enableReuseSpecificationBuilder(true)
        val results1 = parser.parse(pattern)
        val results2 = parser.parse(pattern)
        assertTrue(results1.querySpecifications.forall[it.internalQueryRepresentation.status === PQueryStatus.OK])
        assertTrue(results2.querySpecifications.forall[it.internalQueryRepresentation.status === PQueryStatus.ERROR])
        assertTrue(results2.hasError)
    }
    
    @Test()
    def void defaultUnusedURIProviderTest(){
        val String pattern = '''
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern b(c : EClass) {
             EClass.name(c, "someName2");
            }
        '''
        val parser = PatternParser.parser.build
        parser.enableReuseSpecificationBuilder(true)
        val results = parser.parse(pattern)
        assertTrue(results.patterns.forall[!(eResource.URI.isRelative)])
    }
    
    @Test()
    def void absoluteUnusedURIProviderTest(){
        val String pattern = '''
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern b(c : EClass) {
             EClass.name(c, "someName2");
            }
        '''
        val parser = PatternParser.parser.withUnusedURIComputer(PatternParser.UNUSED_ABSOLUTE_FILE_URI_PROVIDER).build
        parser.enableReuseSpecificationBuilder(true)
        val results = parser.parse(pattern)
        assertTrue(results.patterns.forall[!(eResource.URI.isRelative)])
    }
    
    @Test()
    def void relativeUnusedURIProviderTest(){
        val String pattern = '''
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern b(c : EClass) {
             EClass.name(c, "someName2");
            }
        '''
        val parser = PatternParser.parser.withUnusedURIComputer(PatternParser.UNUSED_RELATIVE_URI_PROVIDER).build
        parser.enableReuseSpecificationBuilder(true)
        val results = parser.parse(pattern)
        assertTrue(results.patterns.forall[eResource.URI.isRelative])
    }
    
    @Test()
    def void customURITest(){
        val String pattern = '''
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern b(c : EClass) {
             EClass.name(c, "someName2");
            }
        '''
        val parser = PatternParser.parser.build
        parser.enableReuseSpecificationBuilder(true)
        val uri = URI.createURI("__synthetic_custom")
        val results = parser.parse(pattern, uri)
        assertTrue(results.patterns.forall[eResource.URI == uri])
    }
    
    @Test(expected = IllegalStateException)
    def void duplicateCustomURITest(){
        val String pattern = '''
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern b(c : EClass) {
             EClass.name(c, "someName2");
            }
        '''
        val parser = PatternParser.parser.build
        parser.enableReuseSpecificationBuilder(true)
        val uri = URI.createURI("__synthetic_custom")
        parser.parse(pattern, uri)
        parser.parse(pattern, uri)
    }
    
    @Test()
    def void completelyBogusSyntaxTest(){
        val String pattern = '''
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            @Constraint{
                
            }
            pattern test(class : EClass){
                EClass(class);
            }
        '''
        val parser = new PatternParserBuilder().build
        val uri = URI.createURI("__synthetic_custom")
        val specificationList = parser.parse(pattern, uri).querySpecifications.map[fullyQualifiedName].toList
        assertArrayEquals(#{null, "test"}, specificationList.toArray)
    }
}