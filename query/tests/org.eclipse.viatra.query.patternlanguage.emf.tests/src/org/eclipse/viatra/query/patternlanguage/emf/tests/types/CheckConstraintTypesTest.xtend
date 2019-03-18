/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
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
import org.eclipse.viatra.query.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.viatra.query.patternlanguage.emf.validation.IssueCodes
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Ignore
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageValidator
import org.eclipse.viatra.query.patternlanguage.emf.tests.CustomizedEMFPatternLanguageInjectorProvider

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(CustomizedEMFPatternLanguageInjectorProvider))
class CheckConstraintTypesTest extends AbstractValidatorTest{

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
    def booleanCheck() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern name(C) = {
                EBoolean(C);
                check(C);
            }
        ''')
//      model.assertError(PatternLanguagePackage::Literals.PATTERN_MODEL, IssueCodes::PACKAGE_NAME_MISMATCH)
        model.assertNoErrors
        tester.validate(model).assertAll(
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }

    @Test
    def accessEClassInCheck() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern name(C) = {
                EClass(C);
                check(C.name.empty);
            }
        ''')
        tester.validate(model).assertAll(
            getErrorCode(IssueCodes::CHECK_CONSTRAINT_SCALAR_VARIABLE_ERROR),
            getWarningCode(IssueCodes::CHECK_WITH_IMPURE_JAVA_CALLS),
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }

    @Test
    def booleanBlockExpressionCheck() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern name(C) = {
                EClass(C);
                EClass.name(C,S);
                check({
                    val name = S;
                    name.empty;
                });
            }
        ''')
//      model.assertError(PatternLanguagePackage::Literals.PATTERN_MODEL, IssueCodes::PACKAGE_NAME_MISMATCH)
        model.assertNoErrors
        tester.validate(model).assertAll(getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE))
    }

    @Test
    def booleanBlockExpressionWithReturnCheck() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern name(C) = {
                EClass(C);
                EClass.name(C,S);
                check({
                    val name = S;
                    name.empty
                });
            }
        ''')
//      model.assertError(PatternLanguagePackage::Literals.PATTERN_MODEL, IssueCodes::PACKAGE_NAME_MISMATCH)
        model.assertNoErrors
        tester.validate(model).assertAll(getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE))
    }

    @Test
    def nonBooleanCheck() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern name(S) = {
                EString(S);
                check(S.length);
            }
        ''')
        tester.validate(model).assertAll(
            getErrorCode(IssueCodes::CHECK_MUST_BE_BOOLEAN),
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }

    @Test
    def multibodyCheck() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern object(obj) {
                EObject(obj);
            }

            pattern andPrecond(n) {
                n == c1;
                c1 == count find object(_);
                check(c1 >= 2);
            } or {
                n == c2;
                c2 == count find object(_);
                check(c2 >= 2);
            }
        ''')
        tester.validate(model).assertAll(
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE),
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    
    @Test
    def constantEvalCheck() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern name(n) = {
                n == eval(2);
            }
        ''')
        tester.validate(model).assertAll(
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    
    @Test
    def parameterizedEvalCheck() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern name(n) = {
                EString(s);
                n == eval(s.length);
            }
        ''')
        tester.validate(model).assertAll(
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    
    @Test
    def parameterizedEvalCheck2() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern name(s) = {
                EString(s);
                4 == eval(s.length);
            }
        ''')
        tester.validate(model).assertAll(
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    
    @Test
    def parameterizedEvalCheck3() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern name(s, o : EStructuralFeature) = {
                EString(s);
                n == eval(s.length);
                EStructuralFeature.upperBound(o, n);
            }
        ''')
        tester.validate(model).assertAll(
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    
    @Test
    def parameterizedEvalCheck4() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern name(s, o : EStructuralFeature) = {
                EString(s);
                n == eval(s.length);
                EStructuralFeature.upperBound(o, n);
                check(s.length > 5);
            }
        ''')
        tester.validate(model).assertAll(
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    
    @Test
    def parameterizedEvalCheck5() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern name(s, o : EStructuralFeature) = {
                check(s.length > 5);
                EStructuralFeature.upperBound(o, n);
                n == eval(s.length);
                EString(s);
            }
        ''')
        tester.validate(model).assertAll(
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    
    @Test
    def incorrectEvalCheck() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern name(s, o : EStructuralFeature) = {
                EString(s);
                n == eval(s.length);
                EStructuralFeature.transient(o, n);
            }
        ''')
        tester.validate(model).assertAll(
            getErrorCode(IssueCodes::VARIABLE_TYPE_INVALID_ERROR),
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    
    @Test
    @Ignore("Known issues with type information process between Xbase expressions")
    def multipleEvalCheck1() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests

            pattern t2(n) {
                find t(n);
                check(n > 2);
            }
            pattern t(n){
                n == eval(Integer.parseInt("2"));
            }
        ''')
        tester.validate(model).assertAll(
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE),
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    
    @Test
    @Ignore("Known issues with type information process between Xbase expressions")
    def multipleEvalCheck2() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests

            pattern t(n){
                n == eval(Integer.parseInt("2"));
            }
            
            pattern t2(n) {
                find t(n);
                check(n > 2);
            }
        ''')
        tester.validate(model).assertAll(
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
}