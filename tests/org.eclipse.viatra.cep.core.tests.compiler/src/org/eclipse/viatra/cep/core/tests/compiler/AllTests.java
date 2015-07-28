package org.eclipse.viatra.cep.core.tests.compiler;

import org.eclipse.viatra.cep.core.tests.compiler.atomic.AtomicCompilerTests;
import org.eclipse.viatra.cep.core.tests.compiler.complex.AllComplexTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AtomicCompilerTests.class, AllComplexTests.class })
public class AllTests {

}
