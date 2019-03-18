/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.patternlanguage.emf.tests.basic

import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel
import org.eclipse.viatra.query.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.viatra.query.patternlanguage.emf.validation.IssueCodes
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageValidator
import org.eclipse.viatra.query.patternlanguage.emf.tests.CustomizedEMFPatternLanguageInjectorProvider

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(CustomizedEMFPatternLanguageInjectorProvider))
class PatternValidationTest extends AbstractValidatorTest {
    @Inject
    ParseHelper<PatternModel> parseHelper
    @Inject
    EMFPatternLanguageValidator validator
    @Inject
    Injector injector
    
    ValidatorTester<EMFPatternLanguageValidator> tester
    
    @Before
    def void initialize() {
        tester = new ValidatorTester(validator, injector)
    }
    @Test
    def emptyBodyValidation() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            pattern resolutionTest(A) = {}')
        tester.validate(model).assertAll(getErrorCode(IssueCodes::PATTERN_BODY_EMPTY), getErrorCode(IssueCodes::SYMBOLIC_VARIABLE_NEVER_REFERENCED))
    }
    @Test
    def emptyParameterListValidation() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
        pattern resolutionTest() = {Pattern(A);}')
        tester.validate(model).assertAll(getWarningCode(IssueCodes::MISSING_PATTERN_PARAMETERS), getWarningCode(IssueCodes::LOCAL_VARIABLE_REFERENCED_ONCE))
    }
    
    @Test
    def unusedPrivatePatternValidation() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            private pattern unusedPrivatePattern(Pattern : Pattern) {
                Pattern(Pattern);
            }
        ')
        tester.validate(model).assertAll(getWarningCode(IssueCodes::UNUSED_PRIVATE_PATTERN))
    }
    
    @Test
    def singleUseParameterValidation() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            pattern unusedPrivatePattern(_Pattern : Pattern) {
                Pattern(_Pattern);
            }
        ')
        tester.validate(model).assertAll(getErrorCode(IssueCodes::SINGLEUSE_PARAMETER))
    }
    
    @Test
    def dubiusSingleUseVariable() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            pattern unusedPrivatePattern(p : Pattern) {
                Pattern(p);
                Pattern.name(p, _p);
            }
        ''')
        tester.validate(model).assertAll(getWarningCode(IssueCodes::DUBIUS_VARIABLE_NAME))
    }
    
    @Test
    def dubiusSingleUseVariable2() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            pattern unusedPrivatePattern(p : Pattern) {
                Pattern.name(_, _p);
            }
        ''')
        tester.validate(model).assertAll(getWarningCode(IssueCodes::DUBIUS_VARIABLE_NAME), getErrorCode(IssueCodes::SYMBOLIC_VARIABLE_NEVER_REFERENCED), getWarningCode(IssueCodes::CARTESIAN_STRICT_WARNING))
    }
    
    @Test
    def dubiusSingleUseVariableCapitalization() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            pattern unusedPrivatePattern(p : Pattern) {
                Pattern(p);
                Pattern.name(p, _P);
            }
        ''')
        tester.validate(model).assertOK
    }
    
    @Test
    def missingFeatureValidation() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            pattern unusedPrivatePattern(p : Pattern) {
                Pattern.(p, _);
            }
        ''')
        // While the EReference Pattern. does not exist, but no validation error should be thrown
        tester.validate(model).assertOK
    }
    
    @Test
    def misspelledFeatureValidation() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            pattern unusedPrivatePattern(p : Pattern) {
                Pattern.nam(p, _);
            }
        ''')
        // While the EReference Pattern.nam does not exist, but no validation error should be thrown
        tester.validate(model).assertOK
    }
}