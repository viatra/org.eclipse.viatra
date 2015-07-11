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
package org.eclipse.viatra.cep.tests.integration

import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext
import org.junit.Test

import static org.junit.Assert.*
import org.apache.log4j.Level

class StrictTests extends BaseIntegrationTest {

	new() {
		super(EventContext::STRICT_IMMEDIATE)
	}

	@Test
	def void test() {
		engine.cepEngineDebugLevel = Level::DEBUG
		engine.addRule(createTestRule());

		eventStream.push(createA1_Event);
		assertEquals(1, TestResultHelper.instance.getResults("or"))

		eventStream.push(createA1_Event);
		assertEquals(2, TestResultHelper.instance.getResults("or"))

		eventStream.push(createA2_Event);
		assertEquals(3, TestResultHelper.instance.getResults("or"))
		assertEquals(1, TestResultHelper.instance.getResults("follows"))
		assertEquals(1, TestResultHelper.instance.getResults("and"))
		assertEquals(1, TestResultHelper.instance.getResults("multiplicityatleast"))
		assertEquals(0, TestResultHelper.instance.getResults("multiplicity3"))

		eventStream.push(createA2_Event);
		assertEquals(4, TestResultHelper.instance.getResults("or"))
		assertEquals(1, TestResultHelper.instance.getResults("follows"))
		assertEquals(1, TestResultHelper.instance.getResults("and"))
		assertEquals(1, TestResultHelper.instance.getResults("multiplicityatleast"))
		assertEquals(0, TestResultHelper.instance.getResults("multiplicity3"))
	}

}
