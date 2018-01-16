package org.eclipse.viatra.query.patternlanguage.emf.tests.standalone

import static org.junit.Assert.*

import org.junit.Test
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParsingUtil
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery.PQueryStatus

class PatternParserTest {
    
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
        assertTrue(results.querySpecifications.filter[it.internalQueryRepresentation.status === PQueryStatus.OK].isEmpty)
        assertTrue(results.hasError)
    } 
}