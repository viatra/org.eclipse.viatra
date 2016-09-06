package org.eclipse.viatra.query.patternlanguage.emf.tests.basic

import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.viatra.query.patternlanguage.emf.tests.EMFPatternLanguageInjectorProvider
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageJavaValidator
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.viatra.query.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFIssueCodes

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class ParameterDirectionTest extends AbstractValidatorTest {
    
    @Inject
    ParseHelper<PatternModel> parseHelper

    @Inject
    EMFPatternLanguageJavaValidator validator

    @Inject
    Injector injector

    ValidatorTester<EMFPatternLanguageJavaValidator> tester

    @Inject extension ValidationTestHelper

    @Before
    def void initialize() {
        tester = new ValidatorTester(validator, injector)
    }

    @Test
    def testWithType() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern name(in D: EClass, out n) = {
                EClass.name(D, n);
            }
        ')
        model.assertNoErrors
        tester.validate(model).assertAll(
            getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    
    @Test
    def void testWithoutType() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern name(in D, out n) = {
                EClass.name(D, n);
            }
        ')
        model.assertNoErrors
        tester.validate(model).assertAll(
            getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE),
            getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
        )
        // There should be one diagnostic about missing type
    }
    
}