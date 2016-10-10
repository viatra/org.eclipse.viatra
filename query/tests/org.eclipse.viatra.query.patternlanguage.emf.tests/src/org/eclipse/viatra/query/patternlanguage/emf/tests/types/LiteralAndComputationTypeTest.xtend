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
package org.eclipse.viatra.query.patternlanguage.emf.tests.types

import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.viatra.query.patternlanguage.emf.tests.EMFPatternLanguageInjectorProvider
import org.eclipse.viatra.query.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFIssueCodes
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageJavaValidator
import org.eclipse.viatra.query.patternlanguage.validation.IssueCodes
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class LiteralAndComputationTypeTest extends AbstractValidatorTest {

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
	def countFind() {
		val model = parseHelper.parse(
			'
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good(X : EClass) {
				EClass(X);
			}

			pattern CountFind(X : EClass) = {
				EClass(X);
				10 == count find Good(X);
			}
		')
		model.assertNoErrors
		tester.validate(model).assertOK
	}

	@Test
	def innerCountFind() {
		val model = parseHelper.parse(
			'
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good(X : EClass) {
				EClass(X);
			}

			pattern InnerCountFind(X : EClass) = {
				EClass(X);
				10 == count find Good(count find Good(X));
			}
		')
		tester.validate(model).assertError(EMFIssueCodes::LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_PATTERN_CALL)
	}

	@Test
	def doubleCountFind() {
		val model = parseHelper.parse(
			'
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good(X : EClass) {
				EClass(X);
			}

			pattern DoubleCountFind(X : EClass) = {
				EClass(X);
				count find Good(X) == count find Good(X);
			}
		')
		model.assertNoErrors
		tester.validate(model).assertOK
	}

	@Test
	def normalFind() {
		val model = parseHelper.parse(
			'
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good(X : EClass) {
				EClass(X);
			}

			pattern NormalFind(X : EClass) = {
				EClass(X);
				find Good(X);
			}
		')
		model.assertNoErrors
		tester.validate(model).assertOK
	}

	@Test
	def normalFindError() {
		val model = parseHelper.parse(
			'
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good(X : EClass) {
				EClass(X);
			}

			pattern NormalFindError(X : EClass) = {
				EClass(X);
				find Good(10);
			}
		')
		tester.validate(model).assertError(EMFIssueCodes::LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_PATTERN_CALL)
	}

	@Test
	def constantWarning1() {
		val model = parseHelper.parse(
			'
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good(X : EClass) {
				EClass(X);
			}

			pattern ConstantWarning1(X : EClass) = {
				EClass(X);
				10 == 20;
			}
		')
		model.assertNoErrors
		tester.validate(model).assertAll(getWarningCode(IssueCodes::CONSTANT_COMPARE_CONSTRAINT),
			getWarningCode(IssueCodes::CONSTANT_COMPARE_CONSTRAINT))
	}

	@Test
	def constantWarning2() {
		val model = parseHelper.parse(
			'
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good(X : EClass) {
				EClass(X);
			}

			pattern ConstantWarning2(X : EClass) = {
				EClass(X);
				"apple" == "orange";
			}
		')
		model.assertNoErrors
		tester.validate(model).assertAll(getWarningCode(IssueCodes::CONSTANT_COMPARE_CONSTRAINT),
			getWarningCode(IssueCodes::CONSTANT_COMPARE_CONSTRAINT))
	}

	@Test
	def constantMismatchError() {
		val model = parseHelper.parse(
			'
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good(X : EClass) {
				EClass(X);
			}

			pattern ConstantMismatchError(X : EClass) = {
				EClass(X);
				"apple" == 10;
			}
		')
		tester.validate(model).assertAll(
			getWarningCode(IssueCodes::CONSTANT_COMPARE_CONSTRAINT),
			getWarningCode(IssueCodes::CONSTANT_COMPARE_CONSTRAINT),
			getErrorCode(EMFIssueCodes::LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_COMPARE)
		)
	}

	@Test
	def constantComputationMismatchError() {
		val model = parseHelper.parse(
			'
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good(X : EClass) {
				EClass(X);
			}

			pattern ConstantComputationMismatchError(X : EClass) = {
				EClass(X);
				"test" == count find Good(X);
			}
		')
		tester.validate(model).assertError(EMFIssueCodes::LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_COMPARE)
	}

	@Test
	def constantInPathExpressionGood() {
		val model = parseHelper.parse(
			'
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern ConstantInPathExpressionGood(X : EClass) = {
				EClass.name(X, "Name");
			}
		')
		model.assertNoErrors
		tester.validate(model).assertOK
	}

	@Test
	def constantInPathExpressionMismatch() {
		val model = parseHelper.parse(
			'
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern ConstantInPathExpressionMismatch(X : EClass) = {
				EClass.name(X, 10);
			}
		')
		tester.validate(model).assertError(EMFIssueCodes::LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_PATH_EXPRESSION)
	}

	@Test
	def countFindInPathExpressionMismatch() {
		val model = parseHelper.parse(
			'
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Good(X : EClass) {
				EClass(X);
			}

			pattern CountFindInPathExpressionMismatch(X : EClass) = {
				EClass.name(X, count find Good(_));
			}
		')
		tester.validate(model).assertError(EMFIssueCodes::LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_PATH_EXPRESSION)
	}

    @Test
    def longVariable() {
        val model = parseHelper.parse('''
            package test
            
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"
            
            pattern circleDiameter1(c : Circle, d : java Long) {
                Circle.diameter(c, d);
            }
        '''
        )
        model.assertNoErrors
		tester.validate(model).assertOK
    }
    
    @Test
    def mistypedLongVariable() {
        val model = parseHelper.parse('''
            package test
            
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"
            
            pattern circleDiameter2(c : Circle, d : java Integer) {
                Circle.diameter(c, d);
            }
        '''
        )
		tester.validate(model).assertError(EMFIssueCodes::VARIABLE_TYPE_INVALID_ERROR)
    }
    
    @Test
    def longConstant() {
        val model = parseHelper.parse('''
            package test
            
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"
            
            pattern circleConstantDiameter1(c : Circle) {
                Circle.diameter(c, 3l);
            }
        '''
        )
        model.assertNoErrors
		tester.validate(model).assertOK
    }
    
    @Test
    def mistypedLongConstant() {
        val model = parseHelper.parse('''
            package test
            
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"
            
            pattern circleConstantDiameter2(c : Circle) {
                Circle.diameter(c, 3);
            }
        '''
        )
		tester.validate(model).assertError(EMFIssueCodes::LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_PATH_EXPRESSION)
    }
    
    @Test
    def floatAttribute() {
        val model = parseHelper.parse('''
            package test
            
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"
            
            pattern checkCircumference1(c : Circle, d : java Long, cf : java Float) {
                Circle.diameter(c, d);
                Circle.circumference(c, cf);
                cf == eval(d * 3.14f);
            }
        '''
        )
        model.assertNoErrors
		tester.validate(model).assertOK
    }
    
    @Test
    def mistypedFloatAttribute() {
        val model = parseHelper.parse('''
            package test
            
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"
            
            pattern checkCircumference2(c : Circle, d : java Long, cf : java Float) {
                Circle.diameter(c, d);
                Circle.circumference(c, cf);
                cf == eval(d * 3.14);
            }
        '''
        )
		tester.validate(model).assertError(EMFIssueCodes::VARIABLE_TYPE_INVALID_ERROR)
    }
    
    @Test
    def doubleAttribute() {
        val model = parseHelper.parse('''
            package test
            
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"
            
            pattern checkArea1(c : Circle, d : java Long, a : java Double) {
                Circle.diameter(c, d);
                Circle.area(c, a);
                a == eval(0.5 * d * 3.14);
            }
        '''
        )
        model.assertNoErrors
		tester.validate(model).assertOK
    }
    
    @Test
    def mistypedDoubleAttribute() {
        val model = parseHelper.parse('''
            package test
            
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"
            
            pattern checkArea2(c : Circle, d : java Long, a : java Double) {
                Circle.diameter(c, d);
                Circle.area(c, a);
                a == eval(0.5f * d * 3.14f);
            }
        '''
        )
		tester.validate(model).assertError(EMFIssueCodes::VARIABLE_TYPE_INVALID_ERROR)
    }
}
