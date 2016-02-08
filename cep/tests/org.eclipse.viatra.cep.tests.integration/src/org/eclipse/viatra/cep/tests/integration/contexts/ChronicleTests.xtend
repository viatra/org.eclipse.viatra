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
package org.eclipse.viatra.cep.tests.integration.contexts

import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext
import org.eclipse.viatra.cep.tests.integration.BaseIntegrationTest
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.*
import static org.eclipse.viatra.cep.core.api.engine.CEPEngine.*

class ChronicleTests extends BaseIntegrationTest {

	@Before
	override void setUp() {
		super.setUp
		engine = newEngine.eventContext(EventContext.CHRONICLE).rule(rule_TestRule).prepare
		eventStream = engine.getStreamManager().newEventStream()
	}

	@Test
	def void test() {
		eventStream.push(createA1_Event);
		assertEquals(1, TestResultHelper.instance.getResults("or"))

		eventStream.push(createA2_Event);
		assertEquals(2, TestResultHelper.instance.getResults("or"))
		assertEquals(1, TestResultHelper.instance.getResults("follows"))
		assertEquals(1, TestResultHelper.instance.getResults("and"))
		assertEquals(1, TestResultHelper.instance.getResults("multiplicityatleast"))
		assertEquals(0, TestResultHelper.instance.getResults("multiplicity3"))
	}

}
