package org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic;

import org.eclipse.viatra.cep.core.metamodels.events.impl.AtomicEventPatternImpl;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.events.C_1_Event;

@SuppressWarnings("all")
public class C_1_Pattern extends AtomicEventPatternImpl {
  public C_1_Pattern() {
    super();
    setType(C_1_Event.class.getCanonicalName());
    setId("org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic.c_1_pattern");
  }
  
  public boolean evaluateCheckExpression() {
    return true;
  }
}
