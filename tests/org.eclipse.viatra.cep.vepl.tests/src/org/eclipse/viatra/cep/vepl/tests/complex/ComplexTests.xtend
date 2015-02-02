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

import org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern
import org.junit.Test

import static org.junit.Assert.*

class ComplexTests extends ComplexVeplTestCase {

	@Test
	def void parseFollows() {
		val model = '''
			ComplexEvent c(){
				definition: a1->a2
			}
			
		'''.parse

		model.assertNoErrors

		assertEquals(1, model.modelElements.filter[m|m instanceof ComplexEventPattern].size)
	}
}
