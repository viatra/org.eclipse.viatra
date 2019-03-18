/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.patternlanguage.emf.tests.annotations

import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.Before
import org.junit.Test
import org.eclipse.viatra.query.patternlanguage.emf.validation.IssueCodes
import org.eclipse.xtext.testing.XtextRunner
import org.junit.runner.RunWith
import org.eclipse.xtext.testing.InjectWith
import com.google.inject.Inject
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageValidator
import org.eclipse.xtext.junit4.validation.ValidatorTester
import com.google.inject.Injector
import org.eclipse.viatra.query.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel
import org.eclipse.viatra.query.patternlanguage.emf.ui.tests.EMFPatternLanguageUiInjectorProvider

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageUiInjectorProvider))
class AnnotationValidatorTest extends AbstractValidatorTest {
        
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
    def void unknownAnnotation() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            @NonExistent
            pattern pattern2(p : Pattern) = {
                Pattern(p);
            }'
        ) 
        tester.validate(model).assertWarning(IssueCodes::UNKNOWN_ANNOTATION);
    }
    @Test
    def void unknownAnnotationAttribute() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            @Optional(unknown=1)
            pattern pattern2(p : Pattern) = {
                Pattern(p);
            }'
        ) 
        tester.validate(model).assertError(IssueCodes::UNKNOWN_ANNOTATION_PARAMETER);
    }
    @Test
    def void unknownAnnotationAttributeTogetherWithValid() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            @Param1(p1="1", unknown=1)
            pattern pattern2(p : Pattern) = {
                Pattern(p);
            }'
        ) 
        tester.validate(model).assertError(IssueCodes::UNKNOWN_ANNOTATION_PARAMETER);
    }
    @Test
    def void missingRequiredAttribute() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            @Param2(p2=1)
            pattern pattern2(p : Pattern) = {
                Pattern(p);
            }'
        ) 
        tester.validate(model).assertError(IssueCodes::MISSING_REQUIRED_ANNOTATION_PARAMETER);
    }
    @Test
    def void onlyRequiredAttributeSet() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            @Param2(p1=1)
            pattern pattern2(p : Pattern) = {
                Pattern(p);
            }'
        ) 
        tester.validate(model).assertOK;
    }
    @Test
    def void bothRequiredAndOptionalAttributeSet() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            @Param2(p1=1,p2=1)
            pattern pattern2(p : Pattern) = {
                Pattern(p);
            }'
        ) 
        tester.validate(model).assertOK;
    }
    @Test
    def void parameterTypeStringExpectedIntFound() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            @Param1(p1=1)
            pattern pattern2(p : Pattern) = {
                Pattern(p);
            }'
        ) 
        tester.validate(model).assertError(IssueCodes::MISTYPED_ANNOTATION_PARAMETER);
    }
    @Test
    def void parameterTypeStringExpectedBoolFound() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            @Param1(p1=true)
            pattern pattern2(p : Pattern) = {
                Pattern(p);
            }'
        ) 
        tester.validate(model).assertError(IssueCodes::MISTYPED_ANNOTATION_PARAMETER);
    }
    @Test
    def void parameterTypeStringExpectedVariableFound() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            @Param1(p1=p)
            pattern pattern2(p : Pattern) = {
                Pattern(p);
            }'
        ) 
        tester.validate(model).assertError(IssueCodes::MISTYPED_ANNOTATION_PARAMETER);
    }
    @Test
    def void parameterTypeStringExpectedDoubleFound() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            @Param1(p1=1.1)
            pattern pattern2(p : Pattern) = {
                Pattern(p);
            }'
        ) 
        tester.validate(model).assertError(IssueCodes::MISTYPED_ANNOTATION_PARAMETER);
    }
    @Test
    def void parameterTypeStringExpectedListFound() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            @Param1(p1={1,2,3})
            pattern pattern2(p : Pattern) = {
                Pattern(p);
            }'
        ) 
        tester.validate(model).assertError(IssueCodes::MISTYPED_ANNOTATION_PARAMETER);
    }
    @Test
    def void parameterTypeUncheckedIntFound() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            @Param2(p1=1)
            pattern pattern2(p : Pattern) = {
                Pattern(p);
            }'
        ) 
        tester.validate(model).assertOK
    }
    @Test
    def void parameterTypeUncheckedBoolFound() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            @Param2(p1=true)
            pattern pattern2(p : Pattern) = {
                Pattern(p);
            }'
        ) 
        tester.validate(model).assertOK
    }
    @Test
    def void parameterTypeUncheckedVariableFound() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            @Param2(p1=p)
            pattern pattern2(p : Pattern) = {
                Pattern(p);
            }'
        ) 
        tester.validate(model).assertOK
    }
    @Test
    def void parameterTypeUncheckedDoubleFound() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            @Param2(p1=1.1)
            pattern pattern2(p : Pattern) = {
                Pattern(p);
            }'
        ) 
        tester.validate(model).assertOK
    }
    @Test
    def void parameterTypeUncheckedListFound() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            @Param2(p1={1,2,3})
            pattern pattern2(p : Pattern) = {
                Pattern(p);
            }'
        ) 
        tester.validate(model).assertOK
    }
    @Test
    def void parameterTypeUncheckedStringFound() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            @Param2(p1="{1,2,3}")
            pattern pattern2(p : Pattern) = {
                Pattern(p);
            }'
        ) 
        tester.validate(model).assertOK
    }
}