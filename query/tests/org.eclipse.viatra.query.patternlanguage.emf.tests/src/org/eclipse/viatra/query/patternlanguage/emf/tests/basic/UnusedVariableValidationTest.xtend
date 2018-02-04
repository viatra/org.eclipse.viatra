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
import org.eclipse.viatra.query.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.viatra.query.patternlanguage.emf.validation.IssueCodes
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageValidator
import org.eclipse.viatra.query.patternlanguage.emf.tests.CustomizedEMFPatternLanguageInjectorProvider

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(CustomizedEMFPatternLanguageInjectorProvider))
class UnusedVariableValidationTest extends AbstractValidatorTest {

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
    def testSymbolicVariableNoReference() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern testPattern(p) = {
                Pattern(h);
                Pattern.name(h, "");
            }'
        )
        tester.validate(model).assertAll(getErrorCode(IssueCodes::SYMBOLIC_VARIABLE_NEVER_REFERENCED), getWarningCode(IssueCodes::CARTESIAN_STRICT_WARNING))
    }

    @Test
    def testSymbolicVariableOnePositiveReference() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern testPattern(p) = {
                Pattern(p);
            }'
        )
        tester.validate(model).assertAll(getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE))
    }

    @Test
    def testParametersEqualityError() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern testPattern(p, p2) = {
                p == p2;
            }'
        )
        tester.validate(model).assertAll(
            getErrorCode(IssueCodes::SYMBOLIC_VARIABLE_NO_POSITIVE_REFERENCE),
            getErrorCode(IssueCodes::SYMBOLIC_VARIABLE_NO_POSITIVE_REFERENCE)
        )
    }

    @Test
    def testSymbolicVariableOneNegativeReference() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern helper(p) = {
                Pattern(p);
            }
            pattern testPattern(p) = {
                neg find helper(p);
            }'
        )
        tester.validate(model).assertAll(
            getErrorCode(IssueCodes::SYMBOLIC_VARIABLE_NO_POSITIVE_REFERENCE),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }

    @Test
    def testSymbolicVariableOneReadOnlyReference() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern helper(p) = {
                Pattern(p);
            }

            pattern testPattern(p) = {
                // Pattern(h);
                EInt(h);
                h == count find helper(p);
            }'
        )
        tester.validate(model).assertAll(
            getErrorCode(IssueCodes::SYMBOLIC_VARIABLE_NO_POSITIVE_REFERENCE),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }

    @Test
    def testSymbolicVariableNoPositiveReference() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern helper(p) = {
                Pattern(p);
            }

            pattern testPattern(p) = {
                neg find helper(p);
                check(p == 0);
                Pattern(h);
                h != p;
            }'
        )
        tester.validate(model).assertAll(
            getErrorCode(IssueCodes::SYMBOLIC_VARIABLE_NO_POSITIVE_REFERENCE),
            getWarningCode(IssueCodes::CARTESIAN_SOFT_WARNING),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }

    @Test
    def testSymbolicVariablePositiveReferenceAsParameter() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern helper(p) = {
                Pattern(p);
            }

            pattern testPattern(p : Pattern) = {
                neg find helper(p);
                Pattern(h);
                h != p;
            }'
        )
        tester.validate(model).assertAll(
            getWarningCode(IssueCodes::CARTESIAN_SOFT_WARNING),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }

    @Test
    def testSymbolicVariableAllReferences() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern helper(p) = {
                Pattern(p);
            }

            pattern testPattern(p) = {
                Pattern(p);
                neg find helper(p);
                check(p == 0);
                Pattern(h);
                h == p;
            }'
        )
        tester.validate(model).assertAll(
            getErrorCode(IssueCodes::CHECK_CONSTRAINT_SCALAR_VARIABLE_ERROR),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }

    @Test
    def testLocalVariableOnePositiveReference() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern testPattern(c) = {
                Pattern(c);
                Pattern(p);
            }'
        )
        tester.validate(model).assertAll(
            getWarningCode(IssueCodes::LOCAL_VARIABLE_REFERENCED_ONCE),
            getWarningCode(IssueCodes::CARTESIAN_STRICT_WARNING),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }

    @Test
    def testLocalVariableOneNegativeReference() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern helper(P) = {
                Pattern(P);
            }
            pattern testPattern(c) = {
                Pattern(c);
                neg find helper(p);
            }'
        )
        tester.validate(model).assertAll(
            getWarningCode(IssueCodes::LOCAL_VARIABLE_QUANTIFIED_REFERENCE),
            getWarningCode(IssueCodes::CARTESIAN_STRICT_WARNING),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }

    @Test
    def testLocalVariableOneReadOnlyReference() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern helper(p) = {
                Pattern(p);
            }

            pattern testPattern(c) = {
                Pattern(c);
                EInt(h);
                h == count find helper(p);
            }'
        )
        tester.validate(model).assertAll(
            getWarningCode(IssueCodes::LOCAL_VARIABLE_QUANTIFIED_REFERENCE),
            getWarningCode(IssueCodes::CARTESIAN_STRICT_WARNING),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }

    @Test
    def testLocalVariableMultiplePositiveReferences() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern testPattern(c) = {
                Pattern(c);
                Pattern(p);
                Pattern.name(p, "");
            }'
        )
        tester.validate(model).assertAll(
            getWarningCode(IssueCodes::CARTESIAN_STRICT_WARNING),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }

    @Test
    def testLocalVariableOnePositiveOneNegativeReference() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern helper(p) = {
                Pattern(p);
            }
            pattern testPattern(c) = {
                Pattern(c);
                Pattern(p);
                neg find helper(p);
            }'
        )
        tester.validate(model).assertAll(
            getWarningCode(IssueCodes::CARTESIAN_STRICT_WARNING),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }

    @Test
    def testLocalVariableOnePositiveOneReadOnlyReference() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern helper(p) = {
                Pattern(p);
            }

            pattern testPattern(c) = {
                Pattern(c);
                Pattern(p);
                EInt(h);
                h == count find helper(p);
            }'
        )
        tester.validate(model).assertAll(
            getWarningCode(IssueCodes::CARTESIAN_STRICT_WARNING),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    @Test
    def testMultipleUseOfSingleUseVariables() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern testPattern(c) = {
                Pattern(c);
                Pattern(_p);
                Pattern(_p);
            }'
        )
        tester.validate(model).assertAll(
            getErrorCode(IssueCodes::ANONYM_VARIABLE_MULTIPLE_REFERENCE),
            getErrorCode(IssueCodes::ANONYM_VARIABLE_MULTIPLE_REFERENCE),
            getWarningCode(IssueCodes::CARTESIAN_STRICT_WARNING),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    @Test
    def testReadOnlyReference() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern testPattern(c) = {
                Pattern(c);
                Pattern(P);
                P != Q;
            }'
        )
        tester.validate(model).assertAll(
            getErrorCode(IssueCodes::LOCAL_VARIABLE_READONLY),
            getWarningCode(IssueCodes::CARTESIAN_STRICT_WARNING),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }

    @Test
    def testLocalVariableMultipleNegativeReferences() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern helper(p) = {
                Pattern(p);
            }

            pattern helper2(p) = {
                Pattern(p);
            }

            pattern testPattern(c) = {
                Pattern(c);
                neg find helper2(p);
                neg find helper(p);
            }'
        )
        tester.validate(model).assertAll(
            getErrorCode(IssueCodes::LOCAL_VARIABLE_NO_POSITIVE_REFERENCE),
            getWarningCode(IssueCodes::CARTESIAN_STRICT_WARNING),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }

    @Test
    def testLocalVariableOneNegativeOneReadOnlyReference() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern helper(p) = {
                Pattern(p);
            }

            pattern testPattern(c) = {
                Pattern(c);
                neg find helper(p);
                EInt(h);
                h == count find helper(p);
            }'
        )
        tester.validate(model).assertAll(
            getErrorCode(IssueCodes::LOCAL_VARIABLE_NO_POSITIVE_REFERENCE),
            getWarningCode(IssueCodes::CARTESIAN_STRICT_WARNING),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }

    @Test
    def testLocalVariableMultipleReadOnlyReferences() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern helper(p) = {
                Pattern(p);
            }

            pattern testPattern(c) = {
                Pattern(c);
                EInt(h);
                EInt(i);
                h == count find helper(p);
                i == count find helper(p);
            }
        ''')
        tester.validate(model).assertAll(
            getErrorCode(IssueCodes::LOCAL_VARIABLE_NO_POSITIVE_REFERENCE),
            getWarningCode(IssueCodes::CARTESIAN_STRICT_WARNING),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    
    
    @Test
    def testEvalParameterReferences1() {
        // Test case provided in bug 508181
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"
            
            pattern bug(name : java String) {
                 neg find eClassName(_, temp);
                 name == eval(temp);
            }
            
            private pattern eClassName(eClass : EClass, name : java String) {
                 EClass.name(eClass, name);
            }
        ''')
        tester.validate(model).assertAll(
            getErrorCode(IssueCodes::LOCAL_VARIABLE_NO_POSITIVE_REFERENCE)
        )
    }
    
    @Test
    def testEvalParameterReferences2() {
        // Test case provided in bug 508181
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"
            
            pattern bug(eClass : EClass, name : java String) {
                 neg find eClassName(eClass, temp);
                 name == eval(temp);
            }
            
            private pattern eClassName(eClass : EClass, name : java String) {
                 EClass.name(eClass, name);
            }
        ''')
        tester.validate(model).assertAll(
            getErrorCode(IssueCodes::LOCAL_VARIABLE_NO_POSITIVE_REFERENCE),
            getWarningCode(IssueCodes::CARTESIAN_SOFT_WARNING)
        )
    }
}