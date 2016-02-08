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
package org.eclipse.incquery.patternlanguage.emf.tests.composition

import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.emf.ecore.EObject
import org.eclipse.incquery.patternlanguage.emf.tests.EMFPatternLanguageInjectorProvider
import org.eclipse.incquery.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.incquery.patternlanguage.emf.validation.EMFPatternLanguageJavaValidator
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternLanguagePackage
import org.eclipse.incquery.patternlanguage.validation.IssueCodes
import org.eclipse.xtext.diagnostics.Diagnostic
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
class CompositionTest extends AbstractValidatorTest { //} extends AbstractEMFPatternLanguageTest{

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
	
	@Inject extension ValidationTestHelper

	@Test
	def void testSimpleComposition() {
		val model = parseHelper.parse(
			'package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			pattern calledPattern(p : Pattern) = {
				Pattern(p);
			}

			pattern callPattern(p : Pattern) = {
				find calledPattern(p);
			}'
		)
		tester.validate(model).assertOK
	}

	@Test
	def void testRecursiveComposition() {
		val model = parseHelper.parse(
			'package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			pattern calledPattern(p : Pattern) = {
				Pattern(p);
			} or {
				find calledPattern(p);
			}'
		)
		tester.validate(model).assertWarning(IssueCodes::RECURSIVE_PATTERN_CALL)
	}

	@Test
	def void testNegativeComposition() {
		val model = parseHelper.parse(
			'package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			pattern calledPattern(p : Pattern) = {
				Pattern(p);
				neg find calledPattern(p);
			}'
		)

		tester.validate(model).assertError(IssueCodes::RECURSIVE_PATTERN_CALL)
	}

	@Test
	def void testMissingComposition() {
		val model = parseHelper.parse(
			'
			package org.eclipse.incquery.patternlanguage.emf.tests
			import "http://www.eclipse.org/incquery/patternlanguage/PatternLanguage"

			pattern callPattern(p : Pattern) = {
				find calledPatternMissing(p);
			}'
		)
		
		model.assertError(PatternLanguagePackage::eINSTANCE.patternCall, 
			Diagnostic::LINKING_DIAGNOSTIC, 
			"calledPatternMissing"
		)
	}

}
