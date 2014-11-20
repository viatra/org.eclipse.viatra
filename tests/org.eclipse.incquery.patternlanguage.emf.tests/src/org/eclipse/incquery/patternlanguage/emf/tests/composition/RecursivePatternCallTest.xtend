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
package org.eclipse.incquery.patternlanguage.emf.tests.composition

import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.emf.ecore.EObject
import org.eclipse.incquery.patternlanguage.emf.tests.EMFPatternLanguageInjectorProvider
import org.eclipse.incquery.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.incquery.patternlanguage.emf.validation.EMFPatternLanguageJavaValidator
import org.eclipse.incquery.patternlanguage.validation.IssueCodes
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.AssertableDiagnostics
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class RecursivePatternCallTest extends AbstractValidatorTest {

	@Inject
	private ParseHelper<EObject> parseHelper

	@Inject
	private EMFPatternLanguageJavaValidator validator

	@Inject
	private Injector injector

	private ValidatorTester<EMFPatternLanguageJavaValidator> tester

	@Before
	def void initialize() {
		tester = new ValidatorTester(validator, injector)
	}

	@Test
	def void testNoRecursion() {
		val model = parseHelper.parse(
			'package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

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
			'package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

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
			'package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			pattern p1(p : Pattern) = {
				Pattern(p);
			} or {
				neg find p1(p);
			}'
		)
		tester.validate(model).assertError(IssueCodes::RECURSIVE_PATTERN_CALL)
	}

	@Test
	def void testChainNoRecursion() {
		val model = parseHelper.parse(
			'package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

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
			'package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

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
			'package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

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
			'package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

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
				"Recursive pattern call: p3 -> neg p1 -> p2 -> p3"),
			AssertableDiagnostics.warning(IssueCodes::RECURSIVE_PATTERN_CALL,
				"Recursive pattern call: p2 -> p3 -> neg p1 -> p2"),
			AssertableDiagnostics.error(IssueCodes::RECURSIVE_PATTERN_CALL,
				"Recursive pattern call: neg p1 -> p2 -> p3 -> neg p1"))
	}

}
