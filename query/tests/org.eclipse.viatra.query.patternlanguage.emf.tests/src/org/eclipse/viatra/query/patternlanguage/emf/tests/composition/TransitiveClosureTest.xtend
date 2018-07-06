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
package org.eclipse.viatra.query.patternlanguage.emf.tests.composition

import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.emf.ecore.EObject
import org.eclipse.viatra.query.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.viatra.query.patternlanguage.emf.validation.IssueCodes
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageValidator
import org.eclipse.viatra.query.patternlanguage.emf.tests.CustomizedEMFPatternLanguageInjectorProvider

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(CustomizedEMFPatternLanguageInjectorProvider))
class TransitiveClosureTest extends AbstractValidatorTest {

    @Inject
    ParseHelper<EObject> parseHelper

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
    def void validClosure() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern patternDependency(p1 : Pattern, p2 : Pattern) = {
                Pattern.bodies.constraints(p1,c);
                PatternCompositionConstraint.call.patternRef(c,p2);
            }

            pattern transitive(p : Pattern) = {
                find patternDependency+(p,p2);	// p2 should be a single variable, e.g. _p2
                Pattern(p2);					// Then this line can be deleted.
            }'
        )
        tester.validate(model).assertOK;
    }

    @Test
    def void validReflexiveClosure() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern patternDependency(p1 : Pattern, p2 : Pattern) = {
                Pattern.bodies.constraints(p1,c);
                PatternCompositionConstraint.call.patternRef(c,p2);
            }

            pattern transitive(p : Pattern) = {
                find patternDependency*(p,p2);  // p2 should be a single variable, e.g. _p2
                Pattern(p2);                    // Then this line can be deleted.
            }'
        )
        tester.validate(model).assertOK;
    }

    @Test
    def void wrongArityClosure() {
        val model = parseHelper.parse(
            '''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            
            pattern patternDependency(p1 : Pattern, p2 : Pattern, c) = {
                Pattern.bodies.constraints(p1,c);
                PatternCompositionConstraint.call.patternRef(c,p2);
            }
            
            pattern transitive(p : Pattern) = {
                find patternDependency+(p,p2,c);	// p2 and c should be single variables, e.g. _p2, _s
                Pattern(p2);						    // Then these lines...
                Constraint(c);						// ...could be deleted.
            }'''
        )
        tester.validate(model).assertAll(getErrorCode(IssueCodes::TRANSITIVE_PATTERNCALL_ARITY),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE));
    }
    
    @Test
    def void wrongArityReflexiveClosure() {
        val model = parseHelper.parse(
            '''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            
            pattern patternDependency(p1 : Pattern, p2 : Pattern, c) = {
                Pattern.bodies.constraints(p1,c);
                PatternCompositionConstraint.call.patternRef(c,p2);
            }
            
            pattern transitive(p : Pattern) = {
                find patternDependency*(p,p2,c);	// p2 and c should be single variables, e.g. _p2, _s
                Pattern(p2);						    // Then these lines...
                Constraint(c);						// ...could be deleted.
            }'''
        )
        tester.validate(model).assertAll(getErrorCode(IssueCodes::TRANSITIVE_PATTERNCALL_ARITY),
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE));
    }

    @Test
    def void misTypedClosure() {
        val model = parseHelper.parse(
            '''
                package org.eclipse.viatra.query.patternlanguage.emf.tests
                import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
                
                pattern bodyOfPattern(p : Pattern, b : PatternBody) {
                    Pattern.bodies(p, b);
                }
                
                pattern transitive(p : Pattern, b : PatternBody) {
                    find bodyOfPattern+(p, b);
                }
            '''
        )
        tester.validate(model).assertError(IssueCodes::TRANSITIVE_PATTERNCALL_TYPE)
    }

    @Test
    def void misTypedReflexiveClosure() {
        val model = parseHelper.parse(
            '''
                package org.eclipse.viatra.query.patternlanguage.emf.tests
                import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
                
                pattern bodyOfPattern(p : Pattern, b : PatternBody) {
                    Pattern.bodies(p, b);
                }
                
                pattern transitive(p : Pattern, b : PatternBody) {
                    find bodyOfPattern*(p, b);
                }
            '''
        )
        tester.validate(model).assertError(IssueCodes::TRANSITIVE_PATTERNCALL_TYPE)
    }

    @Test
    def void misTypedReflexiveClosure2() {
        val model = parseHelper.parse(
            '''
                package org.eclipse.viatra.query.patternlanguage.emf.tests
                import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
                
                pattern patternDependency(p1 : Pattern, p2 : Pattern, c : PatternCompositionConstraint) {
                    Pattern.bodies.constraints(p1,c);
                    PatternCompositionConstraint.call.patternRef(c,p2);
                }
                
                pattern nameDependency(n1 : java String, n2 : java String) {
                    find patternDependency(p1, p2, _);
                    Pattern.name(p1, n1);
                    Pattern.name(p2, n2);
                }
                
                pattern transitive(n1 : java String, n2 : java String) {
                    find nameDependency*(n1, n2);
                }
            '''
        )
        tester.validate(model).assertError(IssueCodes::TRANSITIVE_PATTERNCALL_TYPE)
    }

    @Test
    def void negatedClosure() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern patternDependency(p1 : Pattern, p2 : Pattern) = {
                Pattern.bodies.constraints(p1,c);
                PatternCompositionConstraint.call.patternRef(c,p2);
            }

            pattern transitive(p : Pattern) = {
                Pattern(p);
                neg find patternDependency+(p,_p2);
            }'
        )
        tester.validate(model).assertAll(getErrorCode(IssueCodes::TRANSITIVE_PATTERNCALL_NOT_APPLICABLE));
    }
    
    @Test
    def void negatedReflexiveClosure() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern patternDependency(p1 : Pattern, p2 : Pattern) = {
                Pattern.bodies.constraints(p1,c);
                PatternCompositionConstraint.call.patternRef(c,p2);
            }

            pattern transitive(p : Pattern) = {
                Pattern(p);
                neg find patternDependency*(p,_p2);
            }'
        )
        tester.validate(model).assertAll(getErrorCode(IssueCodes::TRANSITIVE_PATTERNCALL_NOT_APPLICABLE));
    }

    @Test
    def void aggregatedClosure() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern patternDependency(p1 : Pattern, p2 : Pattern) = {
                Pattern.bodies.constraints(p1,c);
                PatternCompositionConstraint.call.patternRef(c,p2);
            }

            pattern transitive(p : Pattern) = {
                Pattern(p);
                3 == count find patternDependency+(p,p2);	// p2 should be a single variable, e.g. _p2
                Pattern(p2);								// Then this line can be deleted.
            }'
        )
        tester.validate(model).assertAll(getErrorCode(IssueCodes::TRANSITIVE_PATTERNCALL_NOT_APPLICABLE),
            getWarningCode(IssueCodes::CARTESIAN_SOFT_WARNING));
    }
    
    @Test
    def void aggregatedReflexiveClosure() {
        val model = parseHelper.parse(
            'package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern patternDependency(p1 : Pattern, p2 : Pattern) = {
                Pattern.bodies.constraints(p1,c);
                PatternCompositionConstraint.call.patternRef(c,p2);
            }

            pattern transitive(p : Pattern) = {
                Pattern(p);
                3 == count find patternDependency*(p,p2);	// p2 should be a single variable, e.g. _p2
                Pattern(p2);								// Then this line can be deleted.
            }'
        )
        tester.validate(model).assertAll(getErrorCode(IssueCodes::TRANSITIVE_PATTERNCALL_NOT_APPLICABLE),
            getWarningCode(IssueCodes::CARTESIAN_SOFT_WARNING));
    }
}
