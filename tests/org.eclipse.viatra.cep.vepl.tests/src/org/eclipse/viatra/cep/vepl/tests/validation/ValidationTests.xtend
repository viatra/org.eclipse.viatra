/*******************************************************************************
 * Copyright (c) 2004-2015, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.cep.vepl.tests.validation

import org.eclipse.viatra.cep.vepl.tests.VeplTestCase
import org.eclipse.viatra.cep.vepl.validation.VeplValidator
import org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern
import org.eclipse.viatra.cep.vepl.vepl.VeplPackage
import org.junit.Test

class ValidationTests extends VeplTestCase {

	@Test
	def void uniqueName() {
		val model1 = '''
			AtomicEvent a
			AtomicEvent a
		'''.parse

		val erroneousElements = model1.modelElements.filter[e|e instanceof AtomicEventPattern]

		erroneousElements.forEach[e|
			e.assertError(VeplPackage::eINSTANCE.atomicEventPattern, VeplValidator::INVALID_NAME)]

		val model2 = '''
			AtomicEvent a
			AtomicEvent b
		'''.parse
		model2.assertNoErrors
	}

	@Test
	def void validPatternCallArguments() {
		val model1 = '''
			AtomicEvent a(p1:String, p2:int)
			AtomicEvent b(p1:String, p2:int)
			
			ComplexEvent c(p1:String, p2:int){
				definition: a->b(p1, _)
			}
		'''.parse
		model1.assertNoErrors

		val model2 = '''
			AtomicEvent a(p1:String, p2:int)
			AtomicEvent b(p1:String, p2:int)
			
			ComplexEvent c(p1:String, p2:int){
				definition: a(p1)->b
			}
		'''.parse
		model2.assertError(VeplPackage::eINSTANCE.parameterizedPatternCall, VeplValidator::INVALID_ARGUMENTS)

		val model3 = '''
			AtomicEvent a(p1:String, p2:int)
			AtomicEvent b(p1:String, p2:int)
			
			ComplexEvent c(p1:String, p2:int){
				definition: a(p1)->b()
			}
		'''.parse
		model3.assertError(VeplPackage::eINSTANCE.parameterizedPatternCall, VeplValidator::INVALID_ARGUMENTS)
	}

	@Test
	def void ruleActions() {
		val model1 = '''
			AtomicEvent a
			
			Rule r{
				events: a
			}
		'''.parse
		model1.assertError(VeplPackage::eINSTANCE.rule, VeplValidator::INVALID_ACTION_IN_RULE)

		val model2 = '''
			AtomicEvent a
			
			Rule r{
				events: a
				actionHandler: org.eclipse.some.Handler
				action{}
			}
		'''.parse

		model2.assertError(VeplPackage::eINSTANCE.rule, VeplValidator::INVALID_ACTION_IN_RULE)
	}

	@Test
	def void explicitlyImportedQueryPackage() {
		val model = '''
			QueryResultChangeEvent ce(){
				query: someUnimportedQuery
			}
		'''.parse
		model.assertError(VeplPackage::eINSTANCE.queryResultChangeEventPattern, VeplValidator::MISSING_QUERY_IMPORT)
	}

	@Test
	def void expressionAtomWithTimewindowMustFeatureMultiplicity() {
		val model1 = '''
			AtomicEvent a
			
			ComplexEvent c(){
				definition: a[1000]
			}
			
		'''.parse
		model1.assertError(VeplPackage::eINSTANCE.atom, VeplValidator::ATOM_TIMEWINDOW_NO_MULTIPLICITY)

		val model2 = '''
			AtomicEvent a
			
			
			ComplexEvent c(){
				definition: a{1}
			}
		'''.parse
		model2.assertError(VeplPackage::eINSTANCE.atom, VeplValidator::ATOM_TIMEWINDOW_NO_MULTIPLICITY)
	}

	@Test
	def void infiniteMultiplicityNotYetSupported() {
		val model = '''
			AtomicEvent a
			
			ComplexEvent c(){
				definition: a{*}
			}
			
		'''.parse
		model.assertError(VeplPackage::eINSTANCE.atom, VeplValidator::NO_INFINITE_SUPPORT)
	}

	@Test
	def void complexEventPatternWithPlainAtomExpression() {
		val model = '''
			AtomicEvent a
			
			ComplexEvent c1(){
				definition: a
			}
			
		'''.parse
		model.assertWarning(VeplPackage::eINSTANCE.complexEventPattern,
			VeplValidator::SINGE_PLAIN_ATOM_IN_COMPLEX_EVENT_EXPRESSION)
	}

}
