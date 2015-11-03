/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andras Okros - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.patternlanguage.emf.tests.basic

import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.incquery.patternlanguage.emf.tests.EMFPatternLanguageInjectorProvider
import org.eclipse.incquery.patternlanguage.emf.validation.EMFIssueCodes
import org.eclipse.incquery.patternlanguage.emf.validation.EMFPatternLanguageJavaValidator
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.xtext.junit4.validation.AssertableDiagnostics
import org.junit.Assert

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class CartesianProductTest {

	@Inject
	ParseHelper<PatternModel> parseHelper

	@Inject
	EMFPatternLanguageJavaValidator validator

	@Inject
	Injector injector

	@Inject extension ValidationTestHelper

	extension ValidatorTester<EMFPatternLanguageJavaValidator> tester

	def assertOk(AssertableDiagnostics diagnostics) {
		Assert.assertEquals(#[], diagnostics.allDiagnostics.toList)
	}

	@Before
	def void initialize() {
		tester = new ValidatorTester(validator, injector)
	}

	@Test
	def testGoodEquality() {
		val model = parseHelper.parse('
			package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Equality(X : EClass, Y : EClass) {
				EClass(X);
				EClass(Y);
				X == Y;
			}
		')
		model.assertNoErrors
		tester.validate(model).assertOk
	}

	@Test
	def testGoodFind() {
		val model = parseHelper.parse('
			package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Equality(X : EClass, Y : EClass) {
				EClass(X);
				EClass(Y);
				X == Y;
			}

			pattern Find(X : EClass, Y : EClass) {
				EClass(X);
				EClass(Y);
				find Equality(X,Y);
			}
		')
		model.assertNoErrors
		tester.validate(model).assertOk
	}

	@Test
	def testGoodCountFind() {
		val model = parseHelper.parse('
			package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Equality(X : EClass, Y : EClass) {
				EClass(X);
				EClass(Y);
				X == Y;
			}

			pattern CountFind(X : EClass, Y : EClass) {
				EClass(X);
				EClass(Y);
				X == Y;
				Z == count find Equality(X,Y);
				check(Z > 10);
			}
		')
		model.assertNoErrors
		tester.validate(model).assertOk
	}

	@Test
	def testGoodFindPathExpressionWithCountFind() {
		val model = parseHelper.parse('
			package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Equality(X : EClass, Y : EClass) {
				EClass(X);
				EClass(Y);
				X == Y;
			}

			pattern PathExpression(X, Y : EClass) {
				EInt(X);
				EClass(Y);
				EClass.eStructuralFeatures.upperBound(Y,X);
			}

			pattern FindPathExpressionWithCountFind(X : EClass, Y : EClass, Z : EClass) {
				EClass(X);
				EClass(Y);
				X == Y;
				EClass(Z);
				find PathExpression(count find Equality(X,Y), Z);
			}
		')
		model.assertNoErrors
		tester.validate(model).assertOk
	}

	@Test
	def testGoodPathExpressionWithCountFind() {
		val model = parseHelper.parse('
			package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Equality(X : EClass, Y : EClass) {
				EClass(X);
				EClass(Y);
				X == Y;
			}

			pattern PathExpressionWithCountFind(X : EClass, Y : EClass, Z : EClass) {
				EClass(X);
				EClass(Y);
				X == Y;
				EClass(Z);
				EClass.eStructuralFeatures.upperBound(Z,count find Equality(X,Y));
			}
		')
		model.assertNoErrors
		tester.validate(model).assertOk
	}

	@Test
	def testGoodNegFindWithRunningVariable() {
		val model = parseHelper.parse('
			package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Equality(X : EClass, Y : EClass) {
				EClass(X);
				EClass(Y);
				X == Y;
			}

			pattern NegFindWithRunningVariable(X : EClass) {
				EClass(X);
				neg find Equality(X,_A);
			}
		')
		model.assertNoErrors
		tester.validate(model).assertOk
	}
	
	@Test
	def testGoodNegFindUnrelatedRunningVariables() {
		val model = parseHelper.parse('
			package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Equality(X : EClass, Y : EClass) {
				EClass(X);
				EClass(Y);
				X == Y;
			}

			pattern NegFindUnrelatedRunningVariables(X : EClass) {
				EClass(X);
				neg find Equality(_A,_B);
			}
		')
		model.assertNoErrors
		tester.validate(model).assertOk
	}

	@Test
	def testGoodPathExpressionInequality() {
		val model = parseHelper.parse('
			package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern PathExpressionInequality(X : EClass, Y) {
				EClass.eStructuralFeatures.upperBound(X, Y);
				Y != 1;
			}
		')
		model.assertNoErrors
		tester.validate(model).assertOk
	}

	@Test
	def testGoodCountFindWithRunningVariable() {
		val model = parseHelper.parse('
			package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Equality(X : EClass, Y : EClass) {
				EClass(X);
				EClass(Y);
				X == Y;
			}

			pattern CountFindWithRunningVariable(X : EClass) {
				EClass(X);
				M == count find Equality(X,_A);
				check(M>10);
			}
		')
		model.assertNoErrors
		tester.validate(model).assertOk
	}

	@Test
	def testGoodUnconnectedButSingleton() {
		val model = parseHelper.parse('
			package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern UnconnectedButSingleton(X : EClass, Y) {
				EClass(X);
				EInt(Y);
				Y == 10;
			}
		')
		model.assertNoErrors
		tester.validate(model).assertOk
	}

	@Test
	def testSoftCheck() {
		val model = parseHelper.parse('
			package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Check(X, Y) {
				EInt(X);
				EInt(Y);
				check(X == Y);
			}
		')
		model.assertNoErrors
		tester.validate(model).assertWarning(EMFIssueCodes::CARTESIAN_SOFT_WARNING)
	}

	@Test
	def testSoftCountFindRunningVariableResult() {
		val model = parseHelper.parse('
			package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Equality(X : EClass, Y : EClass) {
				EClass(X);
				EClass(Y);
				X == Y;
			}

			pattern CountFindRunningVariableResult(X : EClass, Y : EClass) {
				EClass(X);
				EClass(Y);
				_A == count find Equality(X,Y);
			}
		')
		model.assertNoErrors
		tester.validate(model).assertWarning(EMFIssueCodes::CARTESIAN_SOFT_WARNING)
	}

	@Test
	def testSoftNegFind() {
		val model = parseHelper.parse('
			package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Equality(X : EClass, Y : EClass) {
				EClass(X);
				EClass(Y);
				X == Y;
			}

			pattern NegFind(X : EClass, Y : EClass) {
				EClass(X);
				EClass(Y);
				neg find Equality(X,Y);
			}
		')
		model.assertNoErrors
		tester.validate(model).assertWarning(EMFIssueCodes::CARTESIAN_SOFT_WARNING)
	}

	@Test
	def testSoftInequality() {
		val model = parseHelper.parse('
			package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern Inequality(X : EClass, Y : EClass) {
				EClass(X);
				EClass(Y);
				X != Y;
			}
		')
		model.assertNoErrors
		tester.validate(model).assertWarning(EMFIssueCodes::CARTESIAN_SOFT_WARNING)
	}

	@Test
	def testStrictUnconnected() {
		val model = parseHelper.parse('
			package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern NoConnectingConstraint(X : EClass, Y : EClass) {
				EClass(X);
				EClass(Y);
			}
		')
		model.assertNoErrors
		tester.validate(model).assertWarning(EMFIssueCodes::CARTESIAN_STRICT_WARNING)
	}

	@Test
	def testStrictUnconnectedRunningVariable() {
		val model = parseHelper.parse('
			package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern UnconnectedRunningVariable(X : EClass) {
				EClass(X);
				EClass(_Y);
			}
		')
		model.assertNoErrors
		tester.validate(model).assertWarning(EMFIssueCodes::CARTESIAN_STRICT_WARNING)
	}

}