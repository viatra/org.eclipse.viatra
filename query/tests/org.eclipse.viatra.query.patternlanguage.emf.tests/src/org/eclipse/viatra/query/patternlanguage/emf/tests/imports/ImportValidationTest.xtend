/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.patternlanguage.emf.tests.imports

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
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageValidator
import org.eclipse.viatra.query.patternlanguage.emf.tests.CustomizedEMFPatternLanguageInjectorProvider

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(CustomizedEMFPatternLanguageInjectorProvider))
class ImportValidationTest extends AbstractValidatorTest {

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
    def duplicateImport() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern resolutionTest(Name : Pattern) = {
                Pattern(Name);
            }
        ')
//		model.assertError(PatternLanguagePackage::Literals.PATTERN_MODEL, IssueCodes::PACKAGE_NAME_MISMATCH)
        model.assertNoErrors
        tester.validate(model).assertAll(
            getWarningCode(IssueCodes::DUPLICATE_IMPORT),
            getWarningCode(IssueCodes::DUPLICATE_IMPORT)
        );
    }

    @Test
    def implicitJavaImport() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern name(D : java Double) = {
                EDouble(D);
                check(Math::abs(D) > 10.5);
            }
        ')
//		model.assertError(PatternLanguagePackage::Literals.PATTERN_MODEL, IssueCodes::PACKAGE_NAME_MISMATCH)
        model.assertNoErrors
        tester.validate(model).assertOK
    }

    @Test
    def javaClassImport() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"
            import java ^java.util.Calendar

            pattern name(L) = {
                ELong(L);
                check(Calendar::getInstance().getTime().getTime() > L);
            }
        ')
//		model.assertError(PatternLanguagePackage::Literals.PATTERN_MODEL, IssueCodes::PACKAGE_NAME_MISMATCH)
        model.assertNoErrors
        tester.validate(model).assertAll(
            getWarningCode(IssueCodes::CHECK_WITH_IMPURE_JAVA_CALLS),
            getWarningCode(IssueCodes::CHECK_WITH_IMPURE_JAVA_CALLS),
            getWarningCode(IssueCodes::CHECK_WITH_IMPURE_JAVA_CALLS),
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }

    @Test
    def javaPackageImport() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"
            import java ^java.util.*

            pattern name(L : java Long) = {
                ELong(L);
                check(Calendar::getInstance().getTime().getTime() > L);
            }
        ')
        tester.validate(model).assertAll(
            getWarningCode(IssueCodes::CHECK_WITH_IMPURE_JAVA_CALLS),
            getWarningCode(IssueCodes::CHECK_WITH_IMPURE_JAVA_CALLS),
            getWarningCode(IssueCodes::CHECK_WITH_IMPURE_JAVA_CALLS),
            getWarningCode(org.eclipse.xtext.xbase.validation.IssueCodes::IMPORT_WILDCARD_DEPRECATED)
        )
    }
}