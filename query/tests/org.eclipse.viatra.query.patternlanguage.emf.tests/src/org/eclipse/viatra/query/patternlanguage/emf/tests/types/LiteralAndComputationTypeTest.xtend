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
class LiteralAndComputationTest extends AbstractValidatorTest {

	@Inject
	ParseHelper parseHelper

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
		') as PatternModel
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
		') as PatternModel
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
		') as PatternModel
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
		') as PatternModel
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
		') as PatternModel
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
		') as PatternModel
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
		') as PatternModel
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
		') as PatternModel
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
		') as PatternModel
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
		') as PatternModel
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
		') as PatternModel
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
		') as PatternModel
		tester.validate(model).assertError(EMFIssueCodes::LITERAL_OR_COMPUTATION_TYPE_MISMATCH_IN_PATH_EXPRESSION)
	}

}
