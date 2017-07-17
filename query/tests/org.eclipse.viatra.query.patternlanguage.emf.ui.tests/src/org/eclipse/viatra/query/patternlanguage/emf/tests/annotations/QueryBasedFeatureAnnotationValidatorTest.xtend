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
import org.eclipse.viatra.query.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.viatra.addon.querybasedfeatures.runtime.validation.QueryBasedFeaturePatternValidator
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageValidator
import org.eclipse.viatra.query.patternlanguage.emf.ui.tests.EMFPatternLanguageUiInjectorProvider

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageUiInjectorProvider))
class QueryBasedFeatureAnnotationValidatorTest extends AbstractValidatorTest{
        
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
    def void tooFewParameters() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            @QueryBasedFeature
            pattern pattern2(p : Pattern) = {
                Pattern(p);
            }'
        ) 
        val validationResult = tester.validate(model)
        validationResult.assertError(QueryBasedFeaturePatternValidator::PATTERN_ISSUE_CODE);
    }
    
    @Test
    def void emptyFeatureName() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            @QueryBasedFeature(feature = "")
            pattern pattern2(p : Pattern, pb : PatternBody) = {
                Pattern.bodies(p, pb);
            }'
        ) 
        val validationResult = tester.validate(model)
        validationResult.assertError(QueryBasedFeaturePatternValidator::ANNOTATION_ISSUE_CODE);
    }
    
    @Test
    def void notFoundFeature() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            @QueryBasedFeature
            pattern pattern2(p : Pattern, pb : PatternBody) = {
                Pattern.bodies(p, pb);
            }'
        ) 
        val validationResult = tester.validate(model)
        validationResult.assertError(QueryBasedFeaturePatternValidator::ANNOTATION_ISSUE_CODE);
    }
    
    @Test
    def void multipleAnnotations() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            @QueryBasedFeature
            @QueryBasedFeature
            pattern pattern2(p : Pattern, pb : PatternBody) = {
                Pattern.bodies(p, pb);
            }'
        ) 
        val validationResult = tester.validate(model)
        validationResult.assertAll(
            getErrorCode(QueryBasedFeaturePatternValidator::ANNOTATION_ISSUE_CODE),
            getErrorCode(QueryBasedFeaturePatternValidator::ANNOTATION_ISSUE_CODE)
        );
    }
    
    @Test
    def void ambiguousAnnotations() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            @QueryBasedFeature(feature = "x")
            @QueryBasedFeature(feature = "x")
            pattern pattern2(p : Pattern, pb : PatternBody) = {
                Pattern.bodies(p, pb);
            }'
        ) 
        val validationResult = tester.validate(model)
        validationResult.assertAll(
            getErrorCode(QueryBasedFeaturePatternValidator::ANNOTATION_ISSUE_CODE),
            getErrorCode(QueryBasedFeaturePatternValidator::ANNOTATION_ISSUE_CODE)
        );
    }
    
    @Test
    def void incorrectFeature() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            @QueryBasedFeature
            pattern bodies(p : Pattern, pb : PatternBody) = {
                Pattern.bodies(p, pb);
            }'
        ) 
        val validationResult = tester.validate(model)
        validationResult.assertAll(getErrorCode(QueryBasedFeaturePatternValidator::METAMODEL_ISSUE_CODE),
          getErrorCode(QueryBasedFeaturePatternValidator::METAMODEL_ISSUE_CODE),
          getErrorCode(QueryBasedFeaturePatternValidator::METAMODEL_ISSUE_CODE)
        );
    }
    
    @Test
    def void notVolatileFeature() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            @QueryBasedFeature
            pattern variables(pb : PatternBody, v : Variable) = {
                PatternBody.variables(pb, v);
            }'
        ) 
        val validationResult = tester.validate(model)
        validationResult.assertAll(
            getErrorCode(QueryBasedFeaturePatternValidator::METAMODEL_ISSUE_CODE),
            getErrorCode(QueryBasedFeaturePatternValidator::METAMODEL_ISSUE_CODE)
        );
    }
    
}