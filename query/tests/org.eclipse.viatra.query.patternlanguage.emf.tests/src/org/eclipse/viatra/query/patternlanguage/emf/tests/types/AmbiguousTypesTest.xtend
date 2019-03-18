/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.patternlanguage.emf.tests.types

import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel
import org.eclipse.viatra.query.patternlanguage.emf.validation.IssueCodes
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.eclipse.viatra.query.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageValidator
import org.eclipse.viatra.query.patternlanguage.emf.tests.CustomizedEMFPatternLanguageInjectorProvider

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(CustomizedEMFPatternLanguageInjectorProvider))
/**
 * Test cases for bug 419114
 */
class AmbiguousTypesTest extends AbstractValidatorTest {
    
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
    def ambiguousParameterType1() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern circles(c) {
                Circle(c);
            }
            
            pattern redObjects(r) {
                Red(r);
            }
            
            pattern error1(other) = {
                find circles(internal);
                find redObjects(internal);
                WhatEver.someRef(other,internal);
                check(internal.redness == 5);
            }
        ''')
        tester.validate(model).assertAll(
            getErrorCode(IssueCodes::VARIABLE_TYPE_INVALID_ERROR),
            getErrorCode(IssueCodes::CHECK_CONSTRAINT_SCALAR_VARIABLE_ERROR),
            getErrorCode(IssueCodes::CHECK_MUST_BE_BOOLEAN),
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE),
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE),
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    @Test
    def ambiguousParameterType1a() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern circles(c) {
                Circle(c);
            }
            
            pattern redObjects(r) {
                Red(r);
            }
            
            pattern error1(other) = {
                find circles(internal);
                find redObjects(internal);
                WhatEver.someRef(other,internal);
                Red.redness(internal, r);
                check(r == 5);
            }
        ''')
        tester.validate(model).assertAll(
            getErrorCode(IssueCodes::VARIABLE_TYPE_INVALID_ERROR),
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE),
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE),
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    @Test
    def ambiguousParameterType2() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern circles(c) {
                Circle(c);
            }
            
            pattern redObjects(r) {
                Red(r);
            }
            
            pattern error2(other) = {
                find circles(internal);
                find redObjects(internal);
                WhatEver.someRef(other,internal);
            }
        ''')
        tester.validate(model).assertAll(
            getErrorCode(IssueCodes::VARIABLE_TYPE_INVALID_ERROR),
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE),
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE),
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    @Test
    def ambiguousParameterType3() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern circles(c) {
                Circle(c);
            }
            
            pattern redObjects(r) {
                Red(r);
            }
            
            pattern error3(other) = {
                find circles(internal);
                find redObjects(internal);
                Red(internal);
                WhatEver.someRef(other,internal);
            }
        ''')
        tester.validate(model).assertAll(
            getErrorCode(IssueCodes::VARIABLE_TYPE_INVALID_ERROR),
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE),
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE),
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    @Test
    def ambiguousParameterType4() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern circles(c) {
                Circle(c);
            }
            
            pattern redObjects(r) {
                Red(r);
            }
            
            pattern error4(other) = {
                find circles(internal);
                find redObjects(internal);
                Red(internal);
                Circle(internal);
                WhatEver.someRef(other,internal);
            }
        ''')
        tester.validate(model).assertAll(
            getErrorCode(IssueCodes::VARIABLE_TYPE_INVALID_ERROR),
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE),
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE),
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    
}