package org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic;

import org.eclipse.viatra.cep.core.metamodels.events.impl.AtomicEventPatternImpl;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.events.A_1_Event;

@SuppressWarnings("all")
public class A_1_Pattern extends AtomicEventPatternImpl {
  public A_1_Pattern() {
    super();
    setType(A_1_Event.class.getCanonicalName());
    setId("org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic.a_1_pattern");
  }
  
  public boolean evaluateCheckExpression() {
    return true;
  }
}
