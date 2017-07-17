/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.tests.basic

import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.viatra.query.patternlanguage.emf.tests.EMFPatternLanguageInjectorProvider
import org.eclipse.viatra.query.patternlanguage.validation.IssueCodes
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.viatra.query.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFIssueCodes
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageValidator

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class CheckConstraintTest extends AbstractValidatorTest {

    @Inject
    ParseHelper<PatternModel> parseHelper

    @Inject
    EMFPatternLanguageValidator validator

    @Inject
    Injector injector

    ValidatorTester<EMFPatternLanguageValidator> tester

    @Inject extension ValidationTestHelper

    @Before
    def void initialize() {
        tester = new ValidatorTester(validator, injector)
    }

    @Test
    def whitelistedMethodCheck1() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern name(D) = {
                EDouble(D);
                check(^java::lang::Math::abs(D) > 10.5);
            }
        ')
        model.assertNoErrors
        tester.validate(model).assertAll(
            getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    
    @Test
    def whitelistedMethodCheck2() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern name(D) = {
                EDouble(D);
                check(Math::max(0,D) < 3);
            }
        ')
        model.assertNoErrors
        tester.validate(model).assertAll(
            getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    @Test
    def whitelistedMethodCheck3() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern name(S) = {
                EString(S);
                check(Integer.parseInt(S) < 3);
            }
        ')
        model.assertNoErrors
        tester.validate(model).assertAll(
            getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    @Test
    def whitelistedMethodCheck4() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern name(S) = {
                EString(S);
                check(S.contains("abc"));
            }
        ')
        model.assertNoErrors
        tester.validate(model).assertAll(
            getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    @Test
    def whitelistedMethodCheck5() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern name(S) = {
                EClass.name(_, name);
                S == eval(String.format("Name: %s", name));
            }
        ')
        model.assertNoErrors
        tester.validate(model).assertAll(
            getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    @Test
    def whitelistedClassCheck() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern name(D) = {
                EDouble(D);
                check (org::eclipse::viatra::query::patternlanguage::emf::tests::DummyClass::alwaysTrue());
            }
        ''')
        model.assertNoErrors
        tester.validate(model).assertAll(
            getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    @Test
    def whitelistedImportedClassCheck() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"
            import java org.eclipse.viatra.query.patternlanguage.emf.tests.DummyClass

            pattern name(D) = {
                EDouble(D);
                check (DummyClass::alwaysFalse());
            }
        ''')
        model.assertNoErrors
        tester.validate(model).assertAll(
            getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    @Test
    def nonwhitelistedImportedToplevelCheck() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"
            import java org.eclipse.viatra.query.patternlanguage.emf.tests.DummyClass2

            pattern name(D) = {
                EDouble(D);
                check (DummyClass2::alwaysFalse());
            }
        ''')
        model.assertNoErrors
        tester.validate(model).assertAll(
            getWarningCode(IssueCodes::CHECK_WITH_IMPURE_JAVA_CALLS),
            getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
        )
    }

    @Test
    def nonWhitelistedCheck() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern name(L) = {
                ELong(L);
                check(^java::util::Calendar::getInstance().getTime().getTime() > L);
            }
        ')
        model.assertNoErrors
        tester.validate(model).assertAll(
            getWarningCode(IssueCodes::CHECK_WITH_IMPURE_JAVA_CALLS),
            getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    
    @Test
    def evalReturnAsParameterCheck() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern evalTest(cl : EClass, name : java String) {
                EClass.name(cl, name);
                name == eval(name);
            }
        ''')
        tester.validate(model).assertAll(
            getErrorCode(IssueCodes::EVAL_INCORRECT_RETURNVALUE)
        )
    }
    
    @Test
    def evalReturnAsParameterCheck2() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern evalTest(cl : EClass, name : java String) {
                EClass.name(cl, name);
                name != eval(name);
            }
        ''')
        model.assertNoErrors
        tester.validate(model).assertAll(
            getWarningCode(IssueCodes::EVAL_INCORRECT_RETURNVALUE)
        )
    }
    
    @Test
    def evalReturnAsParameterCheck3() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern evalTest(cl : EClass, n : java String) {
                EClass.name(cl, name);
                n == eval(name);
            }
        ''')
        model.assertNoErrors
        tester.validate(model).assertOK
    }

}