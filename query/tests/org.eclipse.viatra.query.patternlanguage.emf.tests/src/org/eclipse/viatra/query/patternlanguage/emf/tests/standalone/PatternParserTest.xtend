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
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParserBuilder
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery.PQueryStatus
import org.eclipse.xtext.common.types.JvmGenericType
import org.eclipse.xtext.diagnostics.Severity
import org.junit.BeforeClass
import org.junit.Test

import static org.junit.Assert.*

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
        val results = parser.parse(pattern, uri)
        assertFalse(results.allDiagnostics.filter[diag | diag.severity === Severity.ERROR].isEmpty)
        val specificationList = results.querySpecifications.map[fullyQualifiedName].toList
        assertArrayEquals(#{"", "test"}, specificationList.toArray)
    }
    
    /**
     * When putting a pattern in a file with the same name, the generated pattern name would clash with the name
     * of the generated pattern group. This test uses the default module that results in the inferring of this
     * pattern group and a corresponding validation error.
     * 
     * For details, see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=553055
     */
    @Test()
    def void groupNameConflictTest() {
        val conflictingName = "conflict"
        val String pattern = '''
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern «conflictingName»(c : EClass) {
             EClass.name(c, "someName2");
            }
        '''
        val parser = new PatternParserBuilder()
            // This is the default injector used  
            .withInjector(new EMFPatternLanguageStandaloneSetup().createInjector)
            .build
        val uri = URI.createFileURI(conflictingName + ".vql")
        val results = parser.parse(pattern, uri)
        assertEquals(2, results.patterns.get(0).eResource.contents.filter(JvmGenericType).size)
        assertEquals(2, results.allDiagnostics.filter[diag | diag.severity === Severity.ERROR].size)
    }
    
    /**
     * When putting a pattern in a file with the same name, the generated pattern name would clash with the name
     * of the generated pattern group. This test uses the standalone parser module that omits the pattern group
     * inference and generation, resulting in fewer inferred types and no type error.
     * 
     * For details, see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=553055
     */
    @Test()
    def void noGroupNameConflictTest() {
        val conflictingName = "conflict"
        val String pattern = '''
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern «conflictingName»(c : EClass) {
             EClass.name(c, "someName2");
            }
        '''
        val parser = new PatternParserBuilder()
            .withInjector(new EMFPatternLanguageStandaloneSetup().createStandaloneInjector)
            .build
        val uri = URI.createFileURI(conflictingName + ".vql")
        val results = parser.parse(pattern, uri)
        assertEquals(1, results.patterns.size)
        assertEquals(1, results.patterns.get(0).eResource.contents.filter(JvmGenericType).size)
        assertTrue(results.allDiagnostics.filter[diag | diag.severity === Severity.ERROR].isEmpty)
    }
    
    @Test()
    def void customJavacodeCheck() {
        val String pattern = '''
            import "http://www.eclipse.org/emf/2002/Ecore";
            import java org.eclipse.viatra.query.patternlanguage.emf.tests.standalone.PatternParserTest;
            
            pattern javaCallTest(c : EClass) {
             EClass.name(c, name);
             check(PatternParserTest.checkName(name));
            }
        '''
        val parser = new PatternParserBuilder()
            .withInjector(new EMFPatternLanguageStandaloneSetup().createStandaloneInjector)
            .withClassLoader(PatternParserTest.classLoader)
            .build
        val results = parser.parse(pattern)
        results.allDiagnostics.filter[diag | diag.severity === Severity.ERROR].forEach[println]
        assertTrue(results.allDiagnostics.filter[diag | diag.severity === Severity.ERROR].isEmpty)
    }
    
    static def boolean checkName(String name) {
        return name.startsWith("abc")
    }
    
    /**
     * Test case for bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=561344
     */
    @Test()
    def void privateCallerTest() {
        val String pattern = '''
            
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            private pattern caller(class : EClass) {
                neg find callee(class);
            }
            private pattern callee(class : EClass) {
                EClass.name(class, "Block");
            }
        '''
        val parser = new PatternParserBuilder()
            .withInjector(new EMFPatternLanguageStandaloneSetup().createStandaloneInjector)
            .build
        val results = parser.parse(pattern)
        results.allDiagnostics.forEach[println]
        assertTrue(results.allDiagnostics.filter[diag | diag.severity === Severity.ERROR].isEmpty)
    }
    
    /**
     * Test case for bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=561344
     */
    @Test()
    def void publicCallerTest() {
        val String pattern = '''
            package test;
            
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern caller(class : EClass) {
                neg find callee(class);
            }
            private pattern callee(class : EClass) {
                EClass.name(class, "Block");
            }
        '''
        val parser = new PatternParserBuilder()
            .withInjector(new EMFPatternLanguageStandaloneSetup().createStandaloneInjector)
            .build
        val results = parser.parse(pattern)
        results.allDiagnostics.forEach[println]
        assertTrue(results.allDiagnostics.filter[diag | diag.severity === Severity.ERROR].isEmpty)
    }
    
    /**
     * Test case for bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=561344
     */
    @Test()
    def void publicCalleeTest() {
        val String pattern = '''
            package test;
            
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            private pattern caller(class : EClass) {
                neg find callee(class);
            }
            pattern callee(class : EClass) {
                EClass.name(class, "Block");
            }
        '''
        val parser = new PatternParserBuilder()
            .withInjector(new EMFPatternLanguageStandaloneSetup().createStandaloneInjector)
            .build
        val results = parser.parse(pattern)
        results.allDiagnostics.forEach[println]
        assertTrue(results.allDiagnostics.filter[diag | diag.severity === Severity.ERROR].isEmpty)
    }
    
    /**
     * Test case for bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=561344
     */
    @Test()
    def void privateCallerTestWithPackageDeclaration() {
        val String pattern = '''
            package test;
            
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            private pattern caller(class : EClass) {
                neg find callee(class);
            }
            private pattern callee(class : EClass) {
                EClass.name(class, "Block");
            }
        '''
        val parser = new PatternParserBuilder()
            .withInjector(new EMFPatternLanguageStandaloneSetup().createStandaloneInjector)
            .build
        val results = parser.parse(pattern)
        results.allDiagnostics.forEach[println]
        assertTrue(results.allDiagnostics.filter[diag | diag.severity === Severity.ERROR].isEmpty)
    }
    
    @Test()
    def void javaConstantInAnnotationParameter() {
        val String pattern = '''
            package test;
            
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            
            @Param1(p1 = java org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage::eNS_URI)
            pattern p(a : java Integer){
                a == 122;
            }
        '''
        val parser = new PatternParserBuilder()
            .withInjector(new EMFPatternLanguageStandaloneSetup().createStandaloneInjector)
            .build
        val results = parser.parse(pattern)
        results.allDiagnostics.forEach[println]
        assertTrue(results.allDiagnostics.filter[diag | diag.severity === Severity.ERROR].isEmpty)
        
        val querySpecification = results.getQuerySpecification("test.p").get
        val annotation = querySpecification.getFirstAnnotationByName("Param1").get
        val value = annotation.getFirstValue("p1", typeof(String)).get
        assertEquals(PatternLanguagePackage.eNS_URI, value)
    }
}