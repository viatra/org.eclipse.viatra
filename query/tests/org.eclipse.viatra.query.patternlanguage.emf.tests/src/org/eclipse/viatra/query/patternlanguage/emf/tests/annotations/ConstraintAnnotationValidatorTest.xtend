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

package org.eclipse.viatra.query.patternlanguage.emf.tests.annotations

import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.viatra.query.patternlanguage.emf.tests.EMFPatternLanguageInjectorProvider
import org.eclipse.viatra.query.patternlanguage.emf.annotations.AnnotationExpressionValidator
import org.eclipse.viatra.query.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageJavaValidator
import org.eclipse.viatra.query.patternlanguage.validation.IssueCodes
import org.eclipse.viatra.addon.validation.runtime.annotation.ConstraintAnnotationValidator
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class ConstraintAnnotationValidatorTest extends AbstractValidatorTest{
		
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
	def void expressionSimpleVariable() {
		val model = parseHelper.parse(
			'''package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

			@Constraint(severity = "error",  location = p, message = "$p$")
			pattern pattern2(p : Pattern) = {
				Pattern(p);
			}'''
		) 
		tester.validate(model).assertOK
	}
	
	@Test
	def void expressionVariableWithDescription() {
		val model = parseHelper.parse(
			'''package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

			@Constraint(severity = "error",  location = p, message = "Pattern $p$")
			pattern pattern2(p : Pattern) = {
				Pattern(p);
			}'''
		) 
		tester.validate(model).assertOK
	}
	
	@Test
	def void expressionEmpty() {
		val model = parseHelper.parse(
			'''package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

			@Constraint(severity = "error",  location = p, message = "")
			pattern pattern2(p : Pattern) = {
				Pattern(p);
			}'''
		) 
		tester.validate(model).assertError(AnnotationExpressionValidator::GENERAL_ISSUE_CODE)
	}
	
	@Test
	def void expressionEmpty2() {
		val model = parseHelper.parse(
			'''package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

			@Constraint(severity = "error",  location = p, message = "$$")
			pattern pattern2(p : Pattern) = {
				Pattern(p);
			}'''
		) 
		tester.validate(model).assertError(AnnotationExpressionValidator::GENERAL_ISSUE_CODE)
	}
	
	@Test
	def void expressionEmptyReference() {
		val model = parseHelper.parse(
			'''package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

			@Constraint(severity = "error",  location = p, message = "Pattern $$")
			pattern pattern2(p : Pattern) = {
				Pattern(p);
			}'''
		) 
		tester.validate(model).assertError(AnnotationExpressionValidator::GENERAL_ISSUE_CODE)
	}
	
	@Test
	def void expressionInDollars() {
		val model = parseHelper.parse(
			'''package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

			@Constraint(severity = "error",  location = p, message =  "$p.name$")
			pattern pattern2(p : Pattern) = {
				Pattern(p);
			}'''
		) 
		tester.validate(model).assertOK
	}
	
	@Test
	def void expressionInvalidParameterNotReferenced() {
		val model = parseHelper.parse(
			'''package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

			@Constraint(severity = "error",  location = p, message =  "p1 $p$")
			pattern pattern2(p : Pattern) = {
				Pattern(p);
			}'''
		) 
		tester.validate(model).assertOK
	}
	@Test
	def void expressionInvalidParameterAttributeNotReferenced() {
		val model = parseHelper.parse(
			'''package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

			@Constraint(severity = "error",  location = p, message =  "p1.name $p$")
			pattern pattern2(p : Pattern) = {
				Pattern(p);
			}'''
		) 
		tester.validate(model).assertOK
	}
	@Test
	def void expressionInvalidParameterReferenced() {
		val model = parseHelper.parse(
			'''package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

			@Constraint(severity = "error",  location = p, message =  "p1 $p1$")
			pattern pattern2(p : Pattern) = {
				Pattern(p);
			}'''
		) 
		tester.validate(model).assertError(AnnotationExpressionValidator::UNKNOWN_VARIABLE_CODE)
	}
	@Test
	def void expressionInvalidParameterAttributeReferenced() {
		val model = parseHelper.parse(
			'''package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

			@Constraint(severity = "error",  location = p, message =  "p1 $p.notExists$")
			pattern pattern2(p : Pattern) = {
				Pattern(p);
			}'''
		) 
		tester.validate(model).assertError(AnnotationExpressionValidator::UNKNOWN_ATTRIBUTE_CODE)
	}
	@Test
	def void expressionInvalidFeature() {
		val model = parseHelper.parse(
			'''package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

			@Constraint(severity = "error",  location = p, message = "$p.notExists$")
			pattern pattern2(p : Pattern) = {
				Pattern(p);
			}'''
		) 
		tester.validate(model).assertError(AnnotationExpressionValidator::UNKNOWN_ATTRIBUTE_CODE)
	}
	
	@Test
	def void invalidLocation() {
		val model = parseHelper.parse(
			'''package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

			@Constraint(severity = "error",  location = notexists, message = "$p$")
			pattern pattern2(p : Pattern) = {
				Pattern(p);
			}'''
		) 
		tester.validate(model).assertError(IssueCodes::MISTYPED_ANNOTATION_PARAMETER)
	}
	
	@Test
	def void warningSeverity() {
		val model = parseHelper.parse(
			'''package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

			@Constraint(severity = "warning",  location = p, message = "$p$")
			pattern pattern2(p : Pattern) = {
				Pattern(p);
			}'''
		) 
		tester.validate(model).assertOK
	}
	
	@Test
	def void invalidSeverity() {
		val model = parseHelper.parse(
			'''package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

			@Constraint(severity = "invalid",  location = p, message = "$p$")
			pattern pattern2(p : Pattern) = {
				Pattern(p);
			}'''
		) 
		tester.validate(model).assertError(ConstraintAnnotationValidator::SEVERITY_ISSUE_CODE)
	}
}