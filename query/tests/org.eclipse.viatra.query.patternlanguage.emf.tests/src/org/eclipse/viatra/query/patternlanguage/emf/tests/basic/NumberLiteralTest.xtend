/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.patternlanguage.emf.tests.basic

import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.viatra.query.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageValidator
import org.eclipse.viatra.query.patternlanguage.emf.tests.CustomizedEMFPatternLanguageInjectorProvider

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(CustomizedEMFPatternLanguageInjectorProvider))
class NumberLiteralTest extends AbstractValidatorTest {

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
    def testPositiveInt() {
        val model = parseHelper.parse(
            '''
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern negativeNumber(n : Numbers) {
                Numbers.int(n, 1);
            }'''
        )
        tester.validate(model).assertOK
    }
    @Test
    def testNegativeInt() {
        val model = parseHelper.parse(
            '''
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern negativeNumber(n : Numbers) {
                Numbers.int(n, -1);
            }'''
        )
        tester.validate(model).assertOK
    }
    @Test
    def testPositiveLong() {
        val model = parseHelper.parse(
            '''
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern negativeNumber(n : Numbers) {
                Numbers.long(n, 1l);
            }'''
        )
        tester.validate(model).assertOK
    }
    @Test
    def testNegativeLong() {
        val model = parseHelper.parse(
            '''
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern negativeNumber(n : Numbers) {
                Numbers.long(n, -1l);
            }'''
        )
        tester.validate(model).assertOK
    }
    @Test
    def testPositiveFloat() {
        val model = parseHelper.parse(
            '''
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern negativeNumber(n : Numbers) {
                Numbers.float(n, 1.0f);
            }'''
        )
        tester.validate(model).assertOK
    }
    @Test
    def testNegativeFloat() {
        val model = parseHelper.parse(
            '''
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern negativeNumber(n : Numbers) {
                Numbers.float(n, -1.0f);
            }'''
        )
        tester.validate(model).assertOK
    }
    @Test
    def testPositiveDouble1() {
        val model = parseHelper.parse(
            '''
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern negativeNumber(n : Numbers) {
                Numbers.double(n, 1.0d);
            }'''
        )
        tester.validate(model).assertOK
    }
    @Test
    def testPositiveDouble2() {
        val model = parseHelper.parse(
            '''
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern negativeNumber(n : Numbers) {
                Numbers.double(n, 1.0);
            }'''
        )
        tester.validate(model).assertOK
    }
    @Test
    def testNegativeDouble1() {
        val model = parseHelper.parse(
            '''
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern negativeNumber(n : Numbers) {
                Numbers.double(n, -1.0d);
            }'''
        )
        tester.validate(model).assertOK
    }
    @Test
    def testNegativeDouble2() {
        val model = parseHelper.parse(
            '''
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern negativeNumber(n : Numbers) {
                Numbers.double(n, -1.0);
            }'''
        )
        tester.validate(model).assertOK
    }
    @Test
    def testPositiveBigInt() {
        val model = parseHelper.parse(
            '''
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern negativeNumber(n : Numbers) {
                Numbers.bigInt(n, 1bi);
            }'''
        )
        tester.validate(model).assertOK
    }
    
    @Test
    def testNegativeBigInt() {
        val model = parseHelper.parse(
            '''
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern negativeNumber(n : Numbers) {
                Numbers.bigInt(n, -1bi);
            }'''
        )
        tester.validate(model).assertOK
    }
    @Test
    def testPositiveBigDecimal() {
        val model = parseHelper.parse(
            '''
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern negativeNumber(n : Numbers) {
                Numbers.bigDecimal(n, 1.0bd);
            }'''
        )
        tester.validate(model).assertOK
    }
    
    @Test
    def testNegativeBigDecimal() {
        val model = parseHelper.parse(
            '''
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern negativeNumber(n : Numbers) {
                Numbers.bigDecimal(n, -1.0bd);
            }'''
        )
        tester.validate(model).assertOK
    }

}