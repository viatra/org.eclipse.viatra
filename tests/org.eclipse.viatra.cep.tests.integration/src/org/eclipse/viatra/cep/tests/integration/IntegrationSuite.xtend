package org.eclipse.viatra.cep.tests.integration

import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(typeof(Suite))
@SuiteClasses(ChronicleTests, ImmediateTests, StrictTests)
class IntegrationSuite {
}
