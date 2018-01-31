/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.tests.standalone

import static org.junit.Assert.*

import org.junit.Test
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParsingUtil
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery.PQueryStatus
import org.junit.BeforeClass
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup

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
        val results = PatternParsingUtil.parsePatternDefinitions(pattern)
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
        val results = PatternParsingUtil.parsePatternDefinitions(pattern)
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
        val results = PatternParsingUtil.parsePatternDefinitions(pattern)
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
        val results = PatternParsingUtil.parsePatternDefinitions(pattern)
        assertFalse(results.querySpecifications.forall[it.internalQueryRepresentation.status === PQueryStatus.OK])
        assertTrue(results.querySpecifications.forall[it.internalQueryRepresentation.status === PQueryStatus.ERROR])
        assertTrue(results.hasError)
    }
}