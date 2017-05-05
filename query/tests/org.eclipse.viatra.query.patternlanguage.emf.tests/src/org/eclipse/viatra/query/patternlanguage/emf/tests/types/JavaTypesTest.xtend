/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.patternlanguage.emf.tests.types

import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.viatra.query.patternlanguage.emf.tests.EMFPatternLanguageInjectorProvider
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFIssueCodes
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageJavaValidator
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.eclipse.viatra.query.patternlanguage.emf.tests.util.AbstractValidatorTest

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
/**
 * Test cases for Java type references
 */
class JavaTypesTest extends AbstractValidatorTest {
    
    @Inject
    ParseHelper<PatternModel> parseHelper
    
    @Inject
    EMFPatternLanguageJavaValidator validator
    
    @Inject
    Injector injector
    
    
    ValidatorTester<EMFPatternLanguageJavaValidator> tester
    
    @Before
    def void initialize() {
        tester = new ValidatorTester(validator, injector)
    }
    
    @Test
    def inferredParameterType() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern just1295(x) = {
            x == eval(1337-42);
            }
        ''')
        tester.validate(model).assertAll(
            getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    
    @Test
    def definedParameterDataType() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern just1295(x : EInt) = {
            x == eval(1337-42);
            }
        ''')
        tester.validate(model).assertOK
    }
    
    @Test
    def definedParameterIncorrectDataType() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern just1295(x : EInt) = {
                x == eval(1337l);
            }
        ''')
        tester.validate(model).assertAll(
            getErrorCode(EMFIssueCodes::VARIABLE_TYPE_INVALID_ERROR)
        )
    }
    @Test
    def definedParameterJavaType() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern just1295(x : java Integer) = {
            x == eval(1337-42);
            }
        ''')
        tester.validate(model).assertOK
    }
    
    @Test
    def definedParameterInconsistentJavaType() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern just1295(x : java Integer) = {
            x == eval(1337l-42);
            }
        ''')
        tester.validate(model).assertAll(
            getErrorCode(EMFIssueCodes::VARIABLE_TYPE_INVALID_ERROR)
        )
    }
    @Test
    def undefinedParameterDataType() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern just1295(x) = {
                EInt(x);
                x == eval(1337-42);
            }
        ''')
        tester.validate(model).assertAll(
            getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    @Test
    def undefinedParameterInconsistentJavaType() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern just1295(x) = {
                java Integer(x);
                x == eval(1337l-42);
            }
        ''')
        tester.validate(model).assertAll(
            getErrorCode(EMFIssueCodes::VARIABLE_TYPE_INVALID_ERROR),
            getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
        )
    }

    
}