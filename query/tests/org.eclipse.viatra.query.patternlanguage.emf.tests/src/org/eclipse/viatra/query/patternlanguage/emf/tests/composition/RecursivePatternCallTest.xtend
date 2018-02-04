/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.tests.composition

import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.emf.ecore.EObject
import org.eclipse.viatra.query.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageValidator
import org.eclipse.viatra.query.patternlanguage.emf.validation.IssueCodes
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.junit4.validation.AssertableDiagnostics
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.viatra.query.patternlanguage.emf.tests.CustomizedEMFPatternLanguageInjectorProvider

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(CustomizedEMFPatternLanguageInjectorProvider))
class RecursivePatternCallTest extends AbstractValidatorTest {

    @Inject
    private ParseHelper<EObject> parseHelper

    @Inject
    private EMFPatternLanguageValidator validator

    @Inject
    private Injector injector

    private ValidatorTester<EMFPatternLanguageValidator> tester

    @Before
    def void initialize() {
        tester = new ValidatorTester(validator, injector)
    }

    @Test
    def void testNoRecursion() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern p1(p : Pattern) = {
                Pattern(p);
            }

            pattern p2(p : Pattern) = {
                find p1(p);
            }'
        )
        tester.validate(model).assertOK
    }

    @Test
    def void testSelfRecursion() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern p1(p : Pattern) = {
                Pattern(p);
            } or {
                find p1(p);
            }'
        )
        tester.validate(model).assertWarning(IssueCodes::RECURSIVE_PATTERN_CALL)
    }

    @Test
    def void testSelfRecursionNeg() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern p1(p : Pattern) = {
                Pattern(p);
            } or {
                neg find p1(p);
            }'
        )
        tester.validate(model).assertError(IssueCodes::RECURSIVE_PATTERN_CALL)
    }
    
    @Test
    def void testSelfRecursionTC() {
        val model = parseHelper.parse(
            '''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"
            
            pattern reachableClass(src : EClass, trg : EClass) {
                EClass.eStructuralFeatures.eType(src, trg);
            } or {
                EClass.eSuperTypes(src, sup);
                find reachableClass+(sup, trg);
            }
            '''
        )
        tester.validate(model).assertError(IssueCodes::RECURSIVE_PATTERN_CALL)
    }

    @Test
    def void testChainNoRecursion() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern p1(p : Pattern) = {
                find p2(p);
            }

            pattern p2(p : Pattern) = {
                find p3(p);
            }

            pattern p3(p : Pattern) = {
                Pattern(p);
            }
            '
        )

        tester.validate(model).assertOK
    }

    @Test
    def void testChainNoRecursionNeg() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern p1(p : Pattern) = {
                neg find p2(p);
            }

            pattern p2(p : Pattern) = {
                neg find p3(p);
            }

            pattern p3(p : Pattern) = {
                Pattern(p);
            }
            '
        )

        tester.validate(model).assertOK
    }

    @Test
    def void testCycleRecursion() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern p1(p : Pattern) = {
                find p2(p);
            }

            pattern p2(p : Pattern) = {
                find p3(p);
            }

            pattern p3(p : Pattern) = {
                find p1(p);
            }
            '
        )

        val result = tester.validate(model)

        result.assertAll(
            AssertableDiagnostics.warning(IssueCodes::RECURSIVE_PATTERN_CALL,
                "Recursive pattern call: p3 -> p1 -> p2 -> p3"),
            AssertableDiagnostics.warning(IssueCodes::RECURSIVE_PATTERN_CALL,
                "Recursive pattern call: p2 -> p3 -> p1 -> p2"),
            AssertableDiagnostics.warning(IssueCodes::RECURSIVE_PATTERN_CALL,
                "Recursive pattern call: p1 -> p2 -> p3 -> p1"))
    }

    @Test
    def void testCycleRecursionNeg() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern p1(p : Pattern) = {
                find p2(p);
            }

            pattern p2(p : Pattern) = {
                find p3(p);
            }

            pattern p3(p : Pattern) = {
                neg find p1(p);
            }
            '
        )

        val result = tester.validate(model)
        result.assertAll(
            AssertableDiagnostics.warning(IssueCodes::RECURSIVE_PATTERN_CALL,
                "p3 -> neg p1 -> p2 -> p3"),
            AssertableDiagnostics.warning(IssueCodes::RECURSIVE_PATTERN_CALL,
                "p2 -> p3 -> neg p1 -> p2"),
            AssertableDiagnostics.error(IssueCodes::RECURSIVE_PATTERN_CALL,
                "neg p1 -> p2 -> p3 -> neg p1"))
    }

    @Test
    def void testCycleRecursionMultiPath() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            
            pattern p1(p : Pattern) = {
                find p2(p);
            }
            
            pattern p2(p : Pattern) = {
                find p3(p);
            } or {
                find p4(p);
            }
            
            pattern p3(p : Pattern) = {
                find p1(p);
            }
            
            pattern p4(p : Pattern) = {
                find p1(p);
            }
            '
        )

        val result = tester.validate(model)
        result.assertAll(
            AssertableDiagnostics.warning(IssueCodes::RECURSIVE_PATTERN_CALL,
                "Recursive pattern call: p1 -> p2 -> p3 -> p1"),
            AssertableDiagnostics.warning(IssueCodes::RECURSIVE_PATTERN_CALL,
                "Recursive pattern call: p1 -> p2 -> p4 -> p1"), 
            AssertableDiagnostics.warning(IssueCodes::RECURSIVE_PATTERN_CALL,
                "Recursive pattern call: p2 -> p3 -> p1 -> p2"), 
            AssertableDiagnostics.warning(IssueCodes::RECURSIVE_PATTERN_CALL,
                "Recursive pattern call: p3 -> p1 -> p2 -> p3"),
            AssertableDiagnostics.warning(IssueCodes::RECURSIVE_PATTERN_CALL,
                "Recursive pattern call: p4 -> p1 -> p2 -> p4"))
    }
}
