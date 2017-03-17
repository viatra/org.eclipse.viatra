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
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageJavaValidator
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class BackendSelectionTest {
    
    @Inject
    ParseHelper<PatternModel> parseHelper

    @Inject
    EMFPatternLanguageJavaValidator validator

    @Inject
    Injector injector

    ValidatorTester<EMFPatternLanguageJavaValidator> tester

    @Inject extension ValidationTestHelper

    @Before
    def void initialize() {
        tester = new ValidatorTester(validator, injector)
    }

    @Test
    def incrementalPatternTest() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            incremental pattern name(D: EClass) = {
                EClass(D);
            }
        ')
        model.assertNoErrors
        tester.validate(model).assertOK
    }
    
    @Test
    def searchPatternTest() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            search pattern name(D: EClass) = {
                EClass(D);
            }
        ')
        model.assertNoErrors
        tester.validate(model).assertOK
    }
    
    @Test
    def privateSearchPatternTest() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            private search pattern name(D: EClass) = {
                EClass(D);
            }

            search pattern useName(D: EClass) = {
                find name(D);
            }
        ')
        model.assertNoErrors
        tester.validate(model).assertOK
    }
}