/*******************************************************************************
 * Copyright (c) 2010-2016, Balazs Grill and IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Balazs Grill - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.tests.basic

import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.viatra.query.patternlanguage.emf.tests.EMFPatternLanguageInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel
import org.eclipse.viatra.query.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.viatra.query.patternlanguage.emf.validation.IssueCodes
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageValidator

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class ParameterDirectionTest extends AbstractValidatorTest {
    
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
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE)
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
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
        // There should be one diagnostic about missing type
    }
    
}