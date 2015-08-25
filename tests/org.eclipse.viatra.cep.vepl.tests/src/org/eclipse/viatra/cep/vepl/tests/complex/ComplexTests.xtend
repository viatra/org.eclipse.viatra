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
package org.eclipse.viatra.cep.vepl.tests.complex

import org.eclipse.viatra.cep.vepl.validation.VeplValidator
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern
import org.eclipse.viatra.cep.vepl.vepl.VeplPackage
import org.junit.Test
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theory

import static org.junit.Assert.*

class ComplexTests extends ComplexVeplTestCase {

	def getFullExpression(String expression, String multiplicity, String timewindow) {
		'''
			ComplexEvent c1(){
				definition: («expression»)«multiplicity»«timewindow»
			}
		'''
	}

	@DataPoints
	def static String[] expressions() {
		#['''a1->a2''', '''a1 OR a2''', '''a1 AND a2''', '''NOT a1''']
	}

	@DataPoints
	def static String[] multiplicities() {
		#['''''', '''{10}''', '''{+}''', '''{*}''']
	}

	@DataPoints
	def static String[] timewindows() {
		#['''''', '''[1000]''']
	}

	/*
	 * This test method should be captured by a {@link Theory},
	 * but we rely on the XtextRunner and no hybrid Xtext-Theory runner is available currently.
	 */
	@Test
	def void parseOperators() {
		for (expression : expressions) {
			for (timewindow : timewindows) {
				for (multiplicity : multiplicities.filter[m|!m.equalsIgnoreCase("{*}")]) {
					testExpression(expression, multiplicity, timewindow, false)
				}
				testExpression(expression, "{*}", timewindow, true)
			}
		}
	}

	def testExpression(String expression, String multiplicity, String timewindow, boolean assertErrors) {
		val fullExpression = getFullExpression(expression, multiplicity, timewindow)

		// println(fullExpression)
		val model = fullExpression.parse

		if (assertErrors) {
			model.assertError(VeplPackage::eINSTANCE.complexEventExpression, VeplValidator::UNSAFE_INFINITE_MULTIPLICITY)
		} else {
			model.assertNoErrors
		}
		assertEquals(1, model.modelElements.filter[m|m instanceof ComplexEventPattern].size)
	}
}