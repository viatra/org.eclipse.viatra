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
package org.eclipse.incquery.patternlanguage.emf.tests.types

import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.incquery.patternlanguage.emf.tests.EMFPatternLanguageInjectorProvider
import org.eclipse.incquery.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.incquery.patternlanguage.emf.validation.EMFIssueCodes
import org.eclipse.incquery.patternlanguage.emf.validation.EMFPatternLanguageJavaValidator
import org.eclipse.incquery.patternlanguage.validation.IssueCodes
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
class CheckConstraintTypesTest extends AbstractValidatorTest{

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
	def booleanCheck() {
		val model = parseHelper.parse('
			package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern name(C) = {
				EBoolean(C);
				check(C);
			}
		')
//		model.assertError(PatternLanguagePackage::Literals.PATTERN_MODEL, IssueCodes::PACKAGE_NAME_MISMATCH)
		model.assertNoErrors
		tester.validate(model).assertOK
	}

	@Test
	def accessEClassInCheck() {
		val model = parseHelper.parse('
			package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern name(C) = {
				EClass(C);
				check(C.name.empty);
			}
		')
		tester.validate(model).assertAll(
			getErrorCode(EMFIssueCodes::CHECK_CONSTRAINT_SCALAR_VARIABLE_ERROR),
			getWarningCode(IssueCodes::CHECK_WITH_IMPURE_JAVA_CALLS)
		)
	}

	@Test
	def booleanBlockExpressionCheck() {
		val model = parseHelper.parse('
			package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern name(C) = {
				EClass(C);
				EClass.name(C,S);
				check({
					val name = S;
					name.empty;
				});
			}
		')
//		model.assertError(PatternLanguagePackage::Literals.PATTERN_MODEL, IssueCodes::PACKAGE_NAME_MISMATCH)
		model.assertNoErrors
		tester.validate(model).assertWarning(IssueCodes::CHECK_WITH_IMPURE_JAVA_CALLS)
	}

	@Test
	def booleanBlockExpressionWithReturnCheck() {
		val model = parseHelper.parse('
			package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern name(C) = {
				EClass(C);
				EClass.name(C,S);
				check({
					val name = S;
					name.empty
				});
			}
		')
//		model.assertError(PatternLanguagePackage::Literals.PATTERN_MODEL, IssueCodes::PACKAGE_NAME_MISMATCH)
		model.assertNoErrors
		tester.validate(model).assertWarning(IssueCodes::CHECK_WITH_IMPURE_JAVA_CALLS)
	}

	@Test
	def nonBooleanCheck() {
		val model = parseHelper.parse('
			package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern name(S) = {
				EString(S);
				check(S.length);
			}
		')
		tester.validate(model).assertError(IssueCodes::CHECK_MUST_BE_BOOLEAN)
	}

	@Test
	def multibodyCheck() {
		val model = parseHelper.parse('''
			package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern object(obj) {
				EObject(obj);
			}

			pattern andPrecond(n) {
				n == c1;
				c1 == count find object(_);
				check(c1 >= 2);
			}/* or {
				n == c2;
				c2 == count find object(_);
				check(c2 >= 2);
			}*/
		''')
		tester.validate(model).assertOK
	}
}