/*******************************************************************************
 * Copyright (c) 2010-2018, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.tests.standalone

import java.util.HashMap
import org.eclipse.emf.common.util.URI
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParserBuilder
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery.PQueryStatus
import org.junit.BeforeClass
import org.junit.Test

import static org.junit.Assert.*

class AdvancedPatternParserTest {

    @BeforeClass
    static def void initializeInjector() {
        EMFPatternLanguageStandaloneSetup.doSetup
    }

    @Test
    def void addSinglePatternTest() {
        val input = new HashMap<URI, String>

        val String pattern = '''
            package pat1
            
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern b(c : EClass) {
             EClass.name(c, _);
            }
        '''
        val uri = URI.createURI('''__synthetic_advancedPatternTest''').resolve(
            URI.createFileURI(System.getProperty("user.dir")))
        input.put(uri, pattern)

        val results = PatternParserBuilder.instance.buildAdvanced.addSpecifications(input);
        assertTrue(
            results.getAddedSpecifications.filter[it.internalQueryRepresentation.status === PQueryStatus.OK].size === 1)

    }

    @Test
    def void addMultiplePatternsSamePackageTest() {
        val input = new HashMap<URI, String>

        val String pattern1 = '''
            package pat1
            
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern b(c : EClass) {
             EClass.name(c, _);
            }
        '''

        val uri1 = URI.createURI('''__synthetic_pattern1''').resolve(URI.createFileURI(System.getProperty("user.dir")))
        val uri2 = URI.createURI('''__synthetic_pattern2''').resolve(URI.createFileURI(System.getProperty("user.dir")))
        input.put(uri1, pattern1)
        input.put(uri2, pattern1)

        val results = PatternParserBuilder.instance.buildAdvanced.addSpecifications(input);
        assertTrue(
            results.getAddedSpecifications.filter[it.internalQueryRepresentation.status === PQueryStatus.ERROR].size === 2)

    }

    @Test
    def void addMultiplePatternsTest() {
        val input = new HashMap<URI, String>

        val String pattern1 = '''
            package pat1
            
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern b(c : EClass) {
             EClass.name(c, _);
            }
        '''

        val String pattern2 = '''
            package pat2
            
                import "http://www.eclipse.org/emf/2002/Ecore";
                
                pattern c(c : EClass) {
                 EClass.name(c, _);
                }
        '''
        val uri1 = URI.createURI('''__synthetic_pattern1''').resolve(URI.createFileURI(System.getProperty("user.dir")))
        val uri2 = URI.createURI('''__synthetic_pattern2''').resolve(URI.createFileURI(System.getProperty("user.dir")))
        input.put(uri1, pattern1)
        input.put(uri2, pattern2)

        val results = PatternParserBuilder.instance.buildAdvanced.addSpecifications(input);
        assertTrue(
            results.getAddedSpecifications.filter[it.internalQueryRepresentation.status === PQueryStatus.OK].size === 2)

    }
    
    @Test(expected = IllegalStateException)
    def void updateNonExistingPatternErrorTest() {
        val input = new HashMap<URI, String>

        val String pattern1 = '''
            package pat1
            
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern b(c : EClass) {
             EClass.name(c, _);
            }
        '''

        
        val uri1 = URI.createURI('''__synthetic_pattern1''').resolve(URI.createFileURI(System.getProperty("user.dir")))
        input.put(uri1, pattern1)
        
        PatternParserBuilder.instance.buildAdvanced.updateSpecifications(input);
    }
    
    @Test(expected = IllegalStateException)
    def void removeNonExistingPatternErrorTest() {
        val input = new HashMap<URI, String>

        val String pattern1 = '''
            package pat1
            
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern b(c : EClass) {
             EClass.name(c, _);
            }
        '''

        
        val uri1 = URI.createURI('''__synthetic_pattern1''').resolve(URI.createFileURI(System.getProperty("user.dir")))
        input.put(uri1, pattern1)
        
        PatternParserBuilder.instance.buildAdvanced.removeSpecifications(input);
    }
    
    
    @Test(expected = IllegalStateException)
    def void addExistingPatternErrorTest() {
        val input = new HashMap<URI, String>

        val String pattern1 = '''
            package pat1
            
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern b(c : EClass) {
             EClass.name(c, _);
            }
        '''

        
        val uri1 = URI.createURI('''__synthetic_pattern1''').resolve(URI.createFileURI(System.getProperty("user.dir")))
        input.put(uri1, pattern1)
        
        val parser = PatternParserBuilder.instance.buildAdvanced
        parser.addSpecifications(input);
        parser.addSpecifications(input);
    }
    
    @Test
    def void resetParserTest() {
        val input = new HashMap<URI, String>

        val String pattern1 = '''
            package pat1
            
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern b(c : EClass) {
             EClass.name(c, _);
            }
        '''

        
        val uri1 = URI.createURI('''__synthetic_pattern1''').resolve(URI.createFileURI(System.getProperty("user.dir")))
        input.put(uri1, pattern1)
        
        val parser = PatternParserBuilder.instance.buildAdvanced
        var results = parser.addSpecifications(input);
        
        assertTrue(
            results.getAddedSpecifications.filter[it.internalQueryRepresentation.status === PQueryStatus.OK].size === 1)
        assertTrue('''Erroneous Specifications: expected: 0 result: «results.erroneousSpecifications.size»''',
            results.erroneousSpecifications.size === 0)
        assertTrue('''Registered Uris: expected: 1 result: «parser.registeredURIs.size»''',
            parser.registeredURIs.size === 1) 
        
        parser.reset
        assertTrue('''Registered Uris: expected: 0 result: «parser.registeredURIs.size»''',
            parser.registeredURIs.size === 0) 
        
        results = parser.addSpecifications(input);
        
        assertTrue(
            results.getAddedSpecifications.filter[it.internalQueryRepresentation.status === PQueryStatus.OK].size === 1)
        assertTrue('''Erroneous Specifications: expected: 0 result: «results.erroneousSpecifications.size»''',
            results.erroneousSpecifications.size === 0)
        assertTrue('''Registered Uris: expected: 1 result: «parser.registeredURIs.size»''',
            parser.registeredURIs.size === 1)
    }

    @Test
    def void addMultiplePatternsWithDependencyTest() {
        val input = new HashMap<URI, String>

        val String pattern1 = '''
            package pat1
            
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern b(c : EClass) {
             EClass.name(c, _);
            }
        '''

        val String pattern2 = '''
            package pat2
            
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern c(c : EClass) {
             find pat1.b(c);
            }
        '''
        val uri1 = URI.createURI('''__synthetic_pattern1''').resolve(URI.createFileURI(System.getProperty("user.dir")))
        val uri2 = URI.createURI('''__synthetic_pattern2''').resolve(URI.createFileURI(System.getProperty("user.dir")))
        input.put(uri1, pattern1)
        input.put(uri2, pattern2)

        val results = PatternParserBuilder.instance.buildAdvanced.addSpecifications(input);
        assertTrue(
            results.getAddedSpecifications.filter[it.internalQueryRepresentation.status === PQueryStatus.OK].size === 2)
    }

   @Test
   def void dependencyDuplicateNameTest() {
        val input = new HashMap<URI, String>

        val String pattern1 = '''
            package pat1
            
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern b(c : EClass) {
             EClass.name(c, _);
            }
        '''

        val String pattern2 = '''
            package pat2
            
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern c(c : EClass) {
             find pat1.b(c);
            }
        '''
        val uri1 = URI.createURI('''__synthetic_pattern1''').resolve(URI.createFileURI(System.getProperty("user.dir")))
        val uri2 = URI.createURI('''__synthetic_pattern2''').resolve(URI.createFileURI(System.getProperty("user.dir")))
        input.put(uri1, pattern1)
        input.put(uri2, pattern2)

        val parser = PatternParserBuilder.instance.buildAdvanced

        val results = parser.addSpecifications(input);
        assertTrue(
            results.getAddedSpecifications.filter[it.internalQueryRepresentation.status === PQueryStatus.OK].size === 2)
            
        val uri3 = URI.createURI('''__synthetic_pattern3''').resolve(URI.createFileURI(System.getProperty("user.dir")))
        
        input.clear
        input.put(uri3, pattern1)
        
        val results2 = parser.addSpecifications(input)
        assertTrue(
            results2.getAddedSpecifications.filter[it.internalQueryRepresentation.status === PQueryStatus.ERROR].size === 1)
    }

    
    @Test
    def void removeReferencedPatternTest() {
        val input = new HashMap<URI, String>

        val String pattern1 = '''
            package pat1
            
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern b(c : EClass) {
             EClass.name(c, _);
            }
        '''

        val String pattern2 = '''
            package pat2
            
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern c(c : EClass) {
             find pat1.b(c);
            }
        '''
        val uri1 = URI.createURI('''__synthetic_pattern1''').resolve(URI.createFileURI(System.getProperty("user.dir")))
        val uri2 = URI.createURI('''__synthetic_pattern2''').resolve(URI.createFileURI(System.getProperty("user.dir")))
        input.put(uri1, pattern1)
        input.put(uri2, pattern2)

        val parser = PatternParserBuilder.instance.buildAdvanced

        val results = parser.addSpecifications(input);
        assertTrue(
            results.getAddedSpecifications.filter[it.internalQueryRepresentation.status === PQueryStatus.OK].size === 2)
            
        input.remove(uri2)
        
        val removedResults = parser.removeSpecifications(input)
        assertTrue(
            removedResults.getRemovedSpecifications.filter[it.internalQueryRepresentation.status === PQueryStatus.OK].size === 1)
        assertTrue(
            removedResults.getImpactedSpecifications.filter[it.internalQueryRepresentation.status === PQueryStatus.ERROR].size === 1)
    }
    
    @Test
    def void missingTransitiveDependencyTest() {
        val input = new HashMap<URI, String>

        val String pattern1 = '''
            package pat1
            
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern b(c : EClass) {
             EClass.name(c, _);
            }
        '''

        val String pattern2 = '''
            package pat2
            
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern c(c : EClass) {
             find pat1.b(c);
            }
        '''
        
        val String pattern3 = '''
            package pat3
            
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern a(c : EClass) {
             find pat2.c(c);
            }
        '''
        val uri1 = URI.createURI('''__synthetic_pattern1''').resolve(URI.createFileURI(System.getProperty("user.dir")))
        val uri2 = URI.createURI('''__synthetic_pattern2''').resolve(URI.createFileURI(System.getProperty("user.dir")))
        val uri3 = URI.createURI('''__synthetic_pattern3''').resolve(URI.createFileURI(System.getProperty("user.dir")))
        input.put(uri1, pattern1)
        input.put(uri2, pattern2)
        input.put(uri3, pattern3)

        val parser = PatternParserBuilder.instance.buildAdvanced

        val results = parser.addSpecifications(input);
        assertTrue(
            results.getAddedSpecifications.filter[it.internalQueryRepresentation.status === PQueryStatus.OK].size === 3)
        assertTrue('''Erroneous Specifications: expected: 0 result: «results.erroneousSpecifications.size»''',
            results.erroneousSpecifications.size === 0)
        assertTrue('''Registered Uris: expected: 3 result: «parser.registeredURIs.size»''',
            parser.registeredURIs.size === 3) 
         
         
        input.clear    
        input.put(uri1, pattern1)
        
        val removedResults = parser.removeSpecifications(input)
        assertTrue(
            removedResults.getRemovedSpecifications.filter[it.internalQueryRepresentation.status === PQueryStatus.OK].size === 1)
        assertTrue(
            removedResults.getImpactedSpecifications.filter[it.internalQueryRepresentation.status === PQueryStatus.ERROR].size === 2)
        assertTrue('''Erroneous Specifications: expected: 2 result: «removedResults.erroneousSpecifications.size»''',
            removedResults.erroneousSpecifications.size === 2)
        assertTrue('''Registered Uris: expected: 2 result: «parser.registeredURIs.size»''',
            parser.registeredURIs.size === 2)
            
        input.clear    
        input.put(uri1, pattern1)  
        
        val reAddResults = parser.addSpecifications(input);
        
        assertTrue(
            reAddResults.getAddedSpecifications.filter[it.internalQueryRepresentation.status === PQueryStatus.OK].size === 1)
        assertTrue(
            reAddResults.getImpactedSpecifications.filter[it.internalQueryRepresentation.status === PQueryStatus.OK].size === 2)
        assertTrue('''Erroneous Specifications: expected: 0 result: «reAddResults.erroneousSpecifications.size»''',
            reAddResults.erroneousSpecifications.size === 0)
        assertTrue('''Registered Uris: expected: 3 result: «parser.registeredURIs.size»''',
            parser.registeredURIs.size === 3)
    }
    
    @Test
    def void patternUpdateTest() {
        val input = new HashMap<URI, String>

        val String pattern1 = '''
            package pat1
            
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern b(c : EClass) {
             EClass.name(c, _);
            }
        '''

        val String pattern2 = '''
            package pat2
            
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern c(c : EClass) {
             find pat1.b(c);
            }
        '''
        
        val String pattern3 = '''
            package pat3
            
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern a(c : EClass) {
             find pat2.c(c);
            }
        '''
        val uri1 = URI.createURI('''__synthetic_pattern1''').resolve(URI.createFileURI(System.getProperty("user.dir")))
        val uri2 = URI.createURI('''__synthetic_pattern2''').resolve(URI.createFileURI(System.getProperty("user.dir")))
        val uri3 = URI.createURI('''__synthetic_pattern3''').resolve(URI.createFileURI(System.getProperty("user.dir")))
        input.put(uri1, pattern1)
        input.put(uri2, pattern2)
        input.put(uri3, pattern3)

        val parser = PatternParserBuilder.instance.buildAdvanced

        val results = parser.addSpecifications(input);
        assertTrue(
            results.getAddedSpecifications.filter[it.internalQueryRepresentation.status === PQueryStatus.OK].size === 3)
         
        input.clear    
        val String updatedPattern1 = '''
            package pat1
            
            import "http://www.eclipse.org/emf/2002/Ecore";
            
            pattern b1(c : EClass) {
             EClass.name(c, _);
            }
        '''
        
        input.put(uri1, updatedPattern1)
        
        val updatedResults = parser.updateSpecifications(input)
        assertTrue(
            updatedResults.getUpdatedSpecifications.filter[it.internalQueryRepresentation.status === PQueryStatus.OK].size === 1)
        assertTrue(
            updatedResults.getImpactedSpecifications.filter[it.internalQueryRepresentation.status === PQueryStatus.ERROR].size === 2)
            
        input.clear    
        input.put(uri1, pattern1)  
        
        val reAddResults = parser.updateSpecifications(input);
        
        assertTrue(
            reAddResults.getUpdatedSpecifications.filter[it.internalQueryRepresentation.status === PQueryStatus.OK].size === 1)
        assertTrue(
            reAddResults.getImpactedSpecifications.filter[it.internalQueryRepresentation.status === PQueryStatus.OK].size === 2)
            
    }
}
