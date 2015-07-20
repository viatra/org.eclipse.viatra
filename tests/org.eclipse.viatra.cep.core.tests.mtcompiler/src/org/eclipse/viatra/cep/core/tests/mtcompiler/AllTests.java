package org.eclipse.viatra.cep.core.tests.mtcompiler;

import org.eclipse.viatra.cep.core.tests.mtcompiler.atomic.AtomicMTCompilerTests;
import org.eclipse.viatra.cep.core.tests.mtcompiler.complex.AllComplexTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AtomicMTCompilerTests.class, AllComplexTests.class })
public class AllTests {

}
