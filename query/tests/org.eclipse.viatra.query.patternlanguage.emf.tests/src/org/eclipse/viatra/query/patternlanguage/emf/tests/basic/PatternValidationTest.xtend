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
import org.eclipse.viatra.query.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFIssueCodes
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageJavaValidator
import org.eclipse.viatra.query.patternlanguage.validation.IssueCodes
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class PatternValidationTest extends AbstractValidatorTest {
	@Inject
	ParseHelper parseHelper
	@Inject
	EMFPatternLanguageJavaValidator validator
	@Inject
	Injector injector
	
	ValidatorTester<EMFPatternLanguageJavaValidator> tester
	
	@Before
	def void initialize() {
		tester = new ValidatorTester(validator, injector)
	}
	@Test
	def emptyBodyValidation() {
		val model = parseHelper.parse('
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"
        	pattern resolutionTest(A) = {}') as PatternModel
		tester.validate(model).assertAll(getErrorCode(IssueCodes::PATTERN_BODY_EMPTY), getErrorCode(IssueCodes::SYMBOLIC_VARIABLE_NEVER_REFERENCED))
	}
	@Test
	def emptyParameterListValidation() {
		val model = parseHelper.parse('
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"
		pattern resolutionTest() = {Pattern(A);}') as PatternModel
		tester.validate(model).assertAll(getWarningCode(IssueCodes::MISSING_PATTERN_PARAMETERS), getWarningCode(IssueCodes::LOCAL_VARIABLE_REFERENCED_ONCE))
	}
	
	@Test
	def unusedPrivatePatternValidation() {
		val model = parseHelper.parse('
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"
			private pattern unusedPrivatePattern(Pattern) {
				Pattern(Pattern);
			}
		')
		tester.validate(model).assertAll(getWarningCode(IssueCodes::UNUSED_PRIVATE_PATTERN), getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE))
	}
	
	@Test
	def singleUseParameterValidation() {
		val model = parseHelper.parse('
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"
			pattern unusedPrivatePattern(_Pattern) {
				Pattern(_Pattern);
			}
		')
		tester.validate(model).assertAll(getErrorCode(EMFIssueCodes::SINGLEUSE_PARAMETER), getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE))
	}
	
	@Test
	def dubiusSingleUseVariable() {
		val model = parseHelper.parse('''
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"
			pattern unusedPrivatePattern(p) {
				Pattern(p);
				Pattern.name(p, _p);
			}
		''')
		tester.validate(model).assertAll(getWarningCode(IssueCodes::DUBIUS_VARIABLE_NAME), getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE))
	}
	
	@Test
	def dubiusSingleUseVariableCapitalization() {
		val model = parseHelper.parse('''
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"
			pattern unusedPrivatePattern(p) {
				Pattern(p);
				Pattern.name(p, _P);
			}
		''')
		tester.validate(model).assertAll(getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE))
	}
}