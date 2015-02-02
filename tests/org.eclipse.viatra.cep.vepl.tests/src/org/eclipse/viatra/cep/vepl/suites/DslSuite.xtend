package org.eclipse.viatra.cep.vepl.suites

import org.eclipse.viatra.cep.vepl.tests.atomic.AtomicTests
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses
import org.eclipse.viatra.cep.vepl.tests.complex.ComplexTests
import org.eclipse.viatra.cep.vepl.tests.validation.ValidationTests

@RunWith(typeof(Suite))
@SuiteClasses(AtomicTests, ComplexTests, ValidationTests)
class DslSuite {
}
