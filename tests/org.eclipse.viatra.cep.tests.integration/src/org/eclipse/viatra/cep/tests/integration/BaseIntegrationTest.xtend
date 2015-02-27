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

import org.eclipse.viatra.cep.core.api.engine.CEPEngine
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext
import org.eclipse.viatra.cep.core.streams.EventStream
import org.eclipse.viatra.cep.tests.integration.internal.DefaultRealm
import org.eclipse.viatra.cep.tests.integration.model.CepFactory
import org.junit.After
import org.junit.Before

abstract class BaseIntegrationTest {

	protected extension CepFactory cf = CepFactory.getInstance

	private EventContext eventContext
	private DefaultRealm defaultRealm
	private CEPEngine engine
	private EventStream eventStream

	new(EventContext eventContext) {
		this.eventContext = eventContext
	}

	@Before
	def void setUp() throws Exception {
		defaultRealm = new DefaultRealm()
		engine = CEPEngine.newEngine(eventContext)
		eventStream = engine.getStreamManager().newEventStream()
		TestResultHelper.instance.results.clear
	}

	@After
	def void tearDown() throws Exception {
		engine = null
		eventStream = null
		defaultRealm.dispose
		TestResultHelper.instance.results.clear
	}

	def getEngine() {
		engine
	}

	def getEventStream() {
		eventStream
	}
}
