package org.eclipse.viatra.cep.vepl.tests.complex

import org.eclipse.viatra.cep.vepl.validation.VeplValidator
import org.eclipse.viatra.cep.vepl.vepl.VeplPackage
import org.junit.Test

class InfinityTests extends ComplexVeplTestCase {

	def static String[] validExpressions() {
		#['''a1{*}->a2''', '''a1{*} AND a2''', '''NOT a1''', '''(a1 -> a2{*}) -> a3''', '''(a1 -> (a2 -> a3{*})) -> a4''']
	}

	def static String[] invalidExpressions() {
		#['''a1{*}''', '''a1{*} OR a2''', '''NOT a1{*}''']
	}

	@Test
	def void parseExpressions() {
		for (expression : validExpressions) {
			'''ComplexEvent c1(){
				definition: («expression»)
			}'''.parse.assertNoErrors
		}
		for (expression : invalidExpressions) {
			'''ComplexEvent c1(){
				definition: («expression»)
			}'''.parse.assertError(VeplPackage::eINSTANCE.complexEventExpression,
				VeplValidator::UNSAFE_INFINITE_MULTIPLICITY)
		}
	}

}