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
package org.eclipse.viatra.cep.vepl.tests.atomic

import org.eclipse.viatra.cep.vepl.tests.VeplTestCase
import org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern
import org.junit.Test

import static org.junit.Assert.*

class AtomicTests extends VeplTestCase {

	@Test
	def void parseAtomicPatterns() {
		val model = '''
			AtomicEvent a1		//no parameters no body
			AtomicEvent a2()	//parameters present but no body
			AtomicEvent a3{}	//no parameters but body present
			AtomicEvent a4(){}	//both parameters and body present
			
		'''.parse

		model.assertNoErrors

		assertEquals(4, model.modelElements.filter[m|m instanceof AtomicEventPattern].size)
	}
}
