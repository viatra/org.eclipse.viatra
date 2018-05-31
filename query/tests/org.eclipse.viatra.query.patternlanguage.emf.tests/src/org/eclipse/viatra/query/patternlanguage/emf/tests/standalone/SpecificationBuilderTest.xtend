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
import org.junit.BeforeClass
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParser
import org.eclipse.viatra.query.patternlanguage.emf.specification.SpecificationBuilder

class SpecificationBuilderTest {
    
    @BeforeClass
    static def void initializeInjector() {
        EMFPatternLanguageStandaloneSetup.doSetup
    }
    
    @Test
    def void forgetPatternTransitive() {
        val String pattern = '''
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern clazz(c : EClass) {
              EClass(c);
            }
            
            pattern clazzName(c : EClass, name : java String) {
                find clazz(c);
                EClass.name(c, name);
            }
            
            pattern unnamedClazz(c : EClass) {
                find clazz(c);
                neg find clazzName(c, _);
            }
        '''
        val results = PatternParser.parser.parse(pattern)
        assertFalse(results.hasError)
        val builder = new SpecificationBuilder()
        results.patterns.forEach[builder.getOrCreateSpecification(it)]
        builder.forgetSpecification(builder.getSpecification("clazz").get)
        assertTrue(builder.getSpecification("unnamedClazz").isPresent())
    }
    
    @Test
    def void forgetPatternTransitiveTest() {
        val String pattern = '''
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern clazz(c : EClass) {
              EClass(c);
            }
            
            pattern clazzName(c : EClass, name : java String) {
                find clazz(c);
                EClass.name(c, name);
            }
            
            pattern unnamedClazz(c : EClass) {
                find clazz(c);
                neg find clazzName(c, _);
            }
        '''
        val results = PatternParser.parser.parse(pattern)
        assertFalse(results.hasError)
        val builder = new SpecificationBuilder()
        results.patterns.forEach[builder.getOrCreateSpecification(it)]
        builder.forgetSpecificationTransitively(builder.getSpecification("clazz").get)
        assertFalse(builder.getSpecification("unnamedClazz").isPresent())
    }
    
}