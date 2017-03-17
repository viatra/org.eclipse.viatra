/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/
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
			'''complexEvent c1(){
				as («expression»)
			}'''.parse.assertNoErrors
		}
		for (expression : invalidExpressions) {
			'''complexEvent c1(){
				as («expression»)
			}'''.parse.assertError(VeplPackage::eINSTANCE.complexEventExpression,
				VeplValidator::UNSAFE_INFINITE_MULTIPLICITY)
		}
	}

}