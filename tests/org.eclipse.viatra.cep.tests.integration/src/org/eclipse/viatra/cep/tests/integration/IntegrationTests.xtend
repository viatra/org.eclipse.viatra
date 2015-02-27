package org.eclipse.viatra.cep.tests.integration

import org.eclipse.viatra.cep.core.api.engine.CEPEngine
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext
import org.eclipse.viatra.cep.core.streams.EventStream
import org.eclipse.viatra.cep.tests.integration.internal.DefaultRealm
import org.eclipse.viatra.cep.tests.integration.model.CepFactory
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class IntegrationTests {

	extension CepFactory cf = CepFactory.getInstance

	private DefaultRealm defaultRealm
	private CEPEngine engine
	private EventStream eventStream

	@Before
	def void setUp() throws Exception {
		defaultRealm = new DefaultRealm()
		engine = CEPEngine.newEngine(EventContext::CHRONICLE)
		eventStream = engine.getStreamManager().newEventStream()
	}

	@After
	def void tearDown() throws Exception {
		engine = null
		eventStream = null
		defaultRealm.dispose
	}

	@Test
	def void test() {
		engine.addRule(createFollowsRule());
		engine.addRule(createOrRule());

		eventStream.push(createA1_Event);
		Assert.assertEquals(1, TestResultHelper.instance.results.get("or"))
		eventStream.push(createA2_Event);
		Assert.assertEquals(2, TestResultHelper.instance.results.get("or"))
		Assert.assertEquals(1, TestResultHelper.instance.results.get("follows"))
	}
}
