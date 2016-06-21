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
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.viatra.query.patternlanguage.emf.tests.EMFPatternLanguageInjectorProvider
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
import org.junit.Ignore

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class CheckConstraintTest {

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
	def whitelistedMethodCheck1() {
		val model = parseHelper.parse('
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern name(D) = {
				EDouble(D);
				check(^java::lang::Math::abs(D) > 10.5);
			}
		')
		model.assertNoErrors
		tester.validate(model).assertOK
	}
	
	@Test
	def whitelistedMethodCheck2() {
		val model = parseHelper.parse('
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern name(D) = {
				EDouble(D);
				check(Math::max(0,D) < 3);
			}
		')
		model.assertNoErrors
		tester.validate(model).assertOK
	}
	@Test
	def whitelistedClassCheck() {
		val model = parseHelper.parse('''
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern name(D) = {
				EDouble(D);
				check (org::eclipse::viatra::query::patternlanguage::emf::tests::DummyClass::alwaysTrue());
			}
		''')
		model.assertNoErrors
		tester.validate(model).assertOK
	}
	@Test
	def whitelistedImportedClassCheck() {
		val model = parseHelper.parse('''
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"
			import java org.eclipse.viatra.query.patternlanguage.emf.tests.DummyClass

			pattern name(D) = {
				EDouble(D);
				check (DummyClass::alwaysFalse());
			}
		''')
		model.assertNoErrors
		tester.validate(model).assertOK
	}

	@Test
	def nonWhitelistedCheck() {
		val model = parseHelper.parse('
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern name(L) = {
				ELong(L);
				check(^java::util::Calendar::getInstance().getTime().getTime() > L);
			}
		')
		model.assertNoErrors
		tester.validate(model).assertWarning(IssueCodes::CHECK_WITH_IMPURE_JAVA_CALLS)
	}

}