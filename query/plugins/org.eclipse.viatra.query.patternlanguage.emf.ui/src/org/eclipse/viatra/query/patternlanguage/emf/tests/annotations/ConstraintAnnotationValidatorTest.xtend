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

package org.eclipse.viatra.query.patternlanguage.emf.tests.annotations

import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.viatra.query.patternlanguage.emf.tests.EMFPatternLanguageInjectorProvider
import org.eclipse.viatra.query.patternlanguage.emf.annotations.AnnotationExpressionValidator
import org.eclipse.viatra.query.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.viatra.query.patternlanguage.validation.IssueCodes
import org.eclipse.viatra.addon.validation.runtime.annotation.ConstraintAnnotationValidator
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageValidator

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class ConstraintAnnotationValidatorTest extends AbstractValidatorTest {

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

    val patternFileHeader = '''
        package org.eclipse.viatra.query.patternlanguage.emf.tests
        import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"
        '''

    val singleParameterPatternDefinition = '''
        pattern pattern2(p : Pattern) = {
            Pattern(p);
        }
        '''
        
    val twoParameterPatternDefinition = '''
        pattern pattern2(p : Pattern, p2 : Pattern) = {
            Pattern(p);
            Pattern(p2);
            p == p2;
        }
        '''

    def parsePatternWithAnnotation(String patternDefinition, String constraintAnnotation) {
        val patternDef = '''
            «patternFileHeader»
            «constraintAnnotation»
            «patternDefinition»
            '''
        return parseHelper.parse(patternDef)
    }

    @Test
    def void expressionSimpleVariable() {
        val model = singleParameterPatternDefinition.parsePatternWithAnnotation('''
            @Constraint(severity = "error",  key = {p}, message = "$p$")
            ''')
        tester.validate(model).assertOK
    }

    @Test
    def void expressionVariableWithDescription() {
        val model = singleParameterPatternDefinition.parsePatternWithAnnotation('''
            @Constraint(severity = "error",  key = {p}, message = "Pattern $p$")
            ''')
        tester.validate(model).assertOK
    }

    @Test
    def void expressionEmpty() {
        val model = singleParameterPatternDefinition.parsePatternWithAnnotation('''
            @Constraint(severity = "error",  key = {p}, message = "")
            ''')
        tester.validate(model).assertError(AnnotationExpressionValidator::GENERAL_ISSUE_CODE)
    }

    @Test
    def void expressionEmpty2() {
        val model = singleParameterPatternDefinition.parsePatternWithAnnotation('''
            @Constraint(severity = "error",  key = {p}, message = "$$")
            ''')
        tester.validate(model).assertError(AnnotationExpressionValidator::GENERAL_ISSUE_CODE)
    }

    @Test
    def void expressionEmptyReference() {
        val model = singleParameterPatternDefinition.parsePatternWithAnnotation('''
            @Constraint(severity = "error",  key = {p}, message = "Pattern $$")
            ''')
        tester.validate(model).assertError(AnnotationExpressionValidator::GENERAL_ISSUE_CODE)
    }

    @Test
    def void expressionInDollars() {
        val model = singleParameterPatternDefinition.parsePatternWithAnnotation('''
            @Constraint(severity = "error",  key = {p}, message =  "$p.name$")
            ''')
        tester.validate(model).assertOK
    }

    @Test
    def void expressionInvalidParameterNotReferenced() {
        val model = singleParameterPatternDefinition.parsePatternWithAnnotation('''
            @Constraint(severity = "error",  key = {p}, message =  "p1 $p$")
            ''')
        tester.validate(model).assertOK
    }

    @Test
    def void expressionInvalidParameterAttributeNotReferenced() {
        val model = singleParameterPatternDefinition.parsePatternWithAnnotation('''
            @Constraint(severity = "error",  key = {p}, message =  "p1.name $p$")
            ''')
        tester.validate(model).assertOK
    }

    @Test
    def void expressionInvalidParameterReferenced() {
        val model = singleParameterPatternDefinition.parsePatternWithAnnotation('''
            @Constraint(severity = "error",  key = {p}, message =  "p1 $p1$")
            ''')
        tester.validate(model).assertError(AnnotationExpressionValidator::UNKNOWN_VARIABLE_CODE)
    }

    @Test
    def void expressionInvalidParameterAttributeReferenced() {
        val model = singleParameterPatternDefinition.parsePatternWithAnnotation('''
            @Constraint(severity = "error",  key = {p}, message =  "p1 $p.notExists$")
            ''')
        tester.validate(model).assertError(AnnotationExpressionValidator::UNKNOWN_ATTRIBUTE_CODE)
    }

    @Test
    def void expressionInvalidFeature() {
        val model = singleParameterPatternDefinition.parsePatternWithAnnotation('''
            @Constraint(severity = "error",  key = {p}, message = "$p.notExists$")
            ''')
        tester.validate(model).assertError(AnnotationExpressionValidator::UNKNOWN_ATTRIBUTE_CODE)
    }

    @Test
    def void stringKey() {
        val model = singleParameterPatternDefinition.parsePatternWithAnnotation('''
            @Constraint(severity = "error",  key = {"p"}, message = "$p$")
            ''')
        tester.validate(model).assertWarning(ConstraintAnnotationValidator::INVALID_KEY_PARAMETERS)
    }

    @Test
    def void multipleKeys() {
        val model = twoParameterPatternDefinition.parsePatternWithAnnotation('''
            @Constraint(severity = "error",  key = {p, p2}, message = "$p$")
            ''')
        tester.validate(model).assertOK
    }

    @Test
    def void mixedKeys() {
        val model = twoParameterPatternDefinition.parsePatternWithAnnotation('''
            @Constraint(severity = "error",  key = {"p", p2}, message = "$p$")
            ''')
        tester.validate(model).assertAll(
            getWarningCode(ConstraintAnnotationValidator::INVALID_KEY_PARAMETERS),
            getErrorCode(ConstraintAnnotationValidator::INVALID_KEY_PARAMETERS)
        )
    }

    @Test
    def void invalidKey() {
        val model = singleParameterPatternDefinition.parsePatternWithAnnotation('''
            @Constraint(severity = "error",  key = {notexists}, message = "$p$")
            ''')
        tester.validate(model).assertAll(
            getErrorCode(IssueCodes::MISTYPED_ANNOTATION_PARAMETER),
            getErrorCode(ConstraintAnnotationValidator::INVALID_KEY_PARAMETERS)
        )
    }

    @Test
    def void invalidKeyString() {
        val model = singleParameterPatternDefinition.parsePatternWithAnnotation('''
            @Constraint(severity = "error",  key = {"notexists"}, message = "$p$")
            ''')
        tester.validate(model).assertAll(
            getErrorCode(ConstraintAnnotationValidator::INVALID_KEY_PARAMETERS),
            getErrorCode(ConstraintAnnotationValidator::INVALID_KEY_PARAMETERS)
        )
    }

    @Test
    def void singleSymmetric() {
        val model = twoParameterPatternDefinition.parsePatternWithAnnotation('''
            @Constraint(severity = "error",  key = {p, p2}, symmetric = {p}, message = "$p$")
            ''')
        tester.validate(model).assertError(ConstraintAnnotationValidator::INVALID_SYMMETRIC_PARAMETERS)
    }

    @Test
    def void multipleSymmetric() {
        val model = twoParameterPatternDefinition.parsePatternWithAnnotation('''
            @Constraint(severity = "error",  key = {p, p2}, symmetric = {p, p2}, message = "$p$")
            ''')
        tester.validate(model).assertOK
    }

    @Test
    def void mixedSymmetric() {
        val model = twoParameterPatternDefinition.parsePatternWithAnnotation('''
            @Constraint(severity = "error",  key = {p, p2}, symmetric = {"p", p2}, message = "$p$")
            ''')
        tester.validate(model).assertAll(
            getWarningCode(ConstraintAnnotationValidator::INVALID_SYMMETRIC_PARAMETERS),
            getErrorCode(ConstraintAnnotationValidator::INVALID_SYMMETRIC_PARAMETERS)
        )
    }

    @Test
    def void invalidSymmetric() {
        val model = singleParameterPatternDefinition.parsePatternWithAnnotation('''
            @Constraint(severity = "error",  key = {p}, symmetric = {notexists, p}, message = "$p$")
            ''')
        tester.validate(model).assertAll(
            getErrorCode(IssueCodes::MISTYPED_ANNOTATION_PARAMETER),
            getErrorCode(ConstraintAnnotationValidator::INVALID_SYMMETRIC_PARAMETERS)
        )
    }

    @Test
    def void invalidSymmetricString() {
        val model = twoParameterPatternDefinition.parsePatternWithAnnotation('''
            @Constraint(severity = "error",  key = {p, p2}, symmetric = {"notexists", "p", "p2"}, message = "$p$")
            ''')
        tester.validate(model).assertAll(
            getErrorCode(ConstraintAnnotationValidator::INVALID_SYMMETRIC_PARAMETERS),
            getWarningCode(ConstraintAnnotationValidator::INVALID_SYMMETRIC_PARAMETERS),
            getWarningCode(ConstraintAnnotationValidator::INVALID_SYMMETRIC_PARAMETERS)
        )
    }

    @Test
    def void warningSeverity() {
        val model = singleParameterPatternDefinition.parsePatternWithAnnotation('''
            @Constraint(severity = "warning",  key = {p}, message = "$p$")
            ''')
        tester.validate(model).assertOK
    }

    @Test
    def void invalidSeverity() {
        val model = singleParameterPatternDefinition.parsePatternWithAnnotation('''
            @Constraint(severity = "invalid",  key = {p}, message = "$p$")
            ''')
        tester.validate(model).assertError(ConstraintAnnotationValidator::SEVERITY_ISSUE_CODE)
    }

    @Test
    def void listVariableReferenceVariableSet() {
        val model = singleParameterPatternDefinition.parsePatternWithAnnotation('''
            @Constraint(severity = "error",  key = {p}, message = "$p$")
            ''')
        tester.validate(model).assertOK

    }
}
