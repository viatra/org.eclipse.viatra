package org.eclipse.viatra.cep.core.tests.compiler.complex;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BasicStructuralTests.class, MultiplicityTests.class, AndTests.class, DuplicateTests.class,
        ParamTests.class, TimewindowTests.class, NotTests.class })
public class AllComplexTests {

}
